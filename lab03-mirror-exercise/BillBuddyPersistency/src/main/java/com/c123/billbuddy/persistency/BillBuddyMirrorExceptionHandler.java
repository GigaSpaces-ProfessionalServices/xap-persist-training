package com.c123.billbuddy.persistency;

import com.gigaspaces.sync.DataSyncOperation;
import com.gigaspaces.sync.OperationsBatchData;
import org.openspaces.persistency.patterns.PersistencyExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mirror exception handler with a bounded-retry / dead-letter pattern: a failing entry is
 * retried up to {@link #MAX_ATTEMPTS} times, then diverted to {@link #handleDeadLetter} instead
 * of being retried forever.
 *
 * <p>This PU's {@code <os-core:mirror>} is configured with
 * {@code operation-grouping="group-by-replication-bulk"} (the mirror default). Under that mode,
 * {@code onException}'s {@code data} argument is an {@link OperationsBatchData} whose batch can
 * mix operations from multiple unrelated transactions/sources, and there is no guarantee that a
 * retry of "the same batch" is delivered with the same item composition next time. That rules out
 * tracking retry attempts by batch identity — this handler tracks them per entry instead, keyed by
 * each {@link DataSyncOperation}'s type + UID, which stays stable across retries even though the
 * enclosing batch object does not.
 *
 * <p>See:
 * <a href="https://docs.gigaspaces.com/latest/dev-java/asynchronous-persistency-with-the-mirror.html">
 * Asynchronous Persistency with the Mirror</a> and
 * <a href="https://docs.gigaspaces.com/latest/dev-java/async-persistency-mirror-advanced.html">
 * Mirror Advanced</a> for the operation-grouping modes and the batch/transaction callback shapes
 * this design relies on. If {@code operation-grouping} is ever switched to
 * {@code group-by-space-transaction}, {@code data} becomes a {@code TransactionData} instead of an
 * {@code OperationsBatchData} and this handler's batch-unwrapping logic would need to change
 * accordingly.
 *
 * <p>Rethrowing from {@code onException} tells XAP to keep the entry in the primary's redo log and
 * retry the replication; not rethrowing tells XAP the batch was handled and it can move on. Since
 * that signal applies to the whole batch (there is no API to retry some entries and skip others),
 * this handler rethrows whenever at least one entry in the batch is still under its retry limit —
 * so already-dead-lettered entries can still be redelivered bundled with the rest, restarting their
 * own count from zero. Only when every entry in a batch has just been dead-lettered does it decline
 * to rethrow.
 */
public class BillBuddyMirrorExceptionHandler implements PersistencyExceptionHandler {

    /** Number of consecutive failures for the same entry before it's diverted to the dead letter path. */
    private static final int MAX_ATTEMPTS = 3;

    private final Logger log = LoggerFactory.getLogger(BillBuddyMirrorExceptionHandler.class);

    private final ConcurrentHashMap<String, AtomicInteger> attemptCounts = new ConcurrentHashMap<>();

    @Override
    public void onException(Exception e, Object data) {
        if (!(data instanceof OperationsBatchData)) {
            // Not the shape we expect under group-by-replication-bulk (see class javadoc) -
            // fall back to always-retry rather than guess at per-entry keys we don't have.
            log.warn("Error caught in mirror exception handler (unrecognized data shape {})",
                    data == null ? "null" : data.getClass().getName(), e);
            throw new RuntimeException(e);
        }

        DataSyncOperation[] items = ((OperationsBatchData) data).getBatchDataItems();
        boolean anyStillRetryable = false;

        for (DataSyncOperation item : items) {
            String key = entryKey(item);
            int attempts = attemptCounts.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet();

            if (attempts >= MAX_ATTEMPTS) {
                log.error("Entry {} failed {} times, diverting to dead letter path instead of retrying again",
                        key, attempts, e);
                handleDeadLetter(item, e);
                attemptCounts.remove(key);
            } else {
                log.warn("Entry {} failed (attempt {}/{}), will retry", key, attempts, MAX_ATTEMPTS, e);
                anyStillRetryable = true;
            }
        }

        if (anyStillRetryable) {
            throw new RuntimeException(e);
        }
        // Every entry in this batch was just dead-lettered - deliberately not rethrowing, so XAP
        // considers the batch handled and the channel isn't blocked behind entries we've already
        // diverted elsewhere.
    }

    private String entryKey(DataSyncOperation item) {
        String typeName = item.supportsGetTypeDescriptor()
                ? item.getTypeDescriptor().getTypeName()
                : item.getDataSyncOperationType().name();
        return typeName + "#" + item.getUid();
    }

    /**
     * Business-specific dead-letter action for an entry that has permanently failed to persist -
     * e.g. write it to a dead-letter table/topic, raise an alert, etc. Left as a stub: what the
     * right action is depends entirely on the use case, not on anything this handler can decide
     * generically.
     */
    protected void handleDeadLetter(DataSyncOperation item, Exception e) {
        // TODO: replace with a real dead-letter action (dead-letter table, alerting, message
        // queue, etc). For now this only logs, which means the data is silently lost from the
        // database's perspective once this method returns without rethrowing - see class javadoc.
        log.error("Dropping entry {} after exhausting {} retries - no dead-letter action is implemented " +
                        "yet, so this data is now lost from the database's perspective",
                entryKey(item), MAX_ATTEMPTS, e);
    }
}

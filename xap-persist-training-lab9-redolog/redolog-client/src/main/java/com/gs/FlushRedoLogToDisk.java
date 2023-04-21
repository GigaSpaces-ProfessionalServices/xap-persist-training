package com.gs;

import com.gigaspaces.async.AsyncFuture;
import com.gigaspaces.internal.cluster.node.impl.packets.IReplicationOrderedPacket;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.SpaceProxyConfigurer;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FlushRedoLogToDisk {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        String spaceName = "redolog";
        if (args.length >= 1) {
            spaceName = args[0];
        }
        GigaSpace gs = new GigaSpaceConfigurer(new SpaceProxyConfigurer(spaceName)).gigaSpace();
        AsyncFuture<Integer> execute = gs.execute(new FlushRedoLogTask());
        Integer totalFlushedPackets = execute.get(60, TimeUnit.SECONDS);
        System.out.println("totalFlushedPackets = " + totalFlushedPackets);
    }
}

package com.gs;

import com.gigaspaces.internal.cluster.node.impl.packets.IReplicationOrderedPacket;
import com.gigaspaces.internal.cluster.node.impl.packets.data.IReplicationPacketData;
import com.gigaspaces.internal.cluster.node.impl.packets.data.IReplicationPacketEntryData;
import com.gigaspaces.internal.cluster.node.impl.packets.data.IReplicationTransactionalPacketEntryData;
import com.gigaspaces.internal.cluster.node.impl.packets.data.operations.*;
import com.gigaspaces.internal.io.IOUtils;
import com.gigaspaces.internal.server.space.redolog.DBSwapRedoLogFileConfig;
import com.gigaspaces.internal.server.space.redolog.storage.SqliteRedoLogFileStorage;
import com.gigaspaces.internal.server.space.redolog.storage.StorageReadOnlyIterator;
import com.gigaspaces.internal.server.storage.IEntryData;
import com.gigaspaces.start.SystemLocations;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import org.openspaces.core.transaction.manager.DistributedJiniTxManagerConfigurer;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.*;

public class DeserializeRedoLog {

    static String spaceName = "redolog";
    static String containerName = "redolog_container1";
    Path directory = SystemLocations.singleton().work("redo-log").resolve(spaceName);
    Path codeMapFile = directory.resolve(containerName + "_code_map");

    public DeserializeRedoLog() throws Exception {
        PlatformTransactionManager ptm = new DistributedJiniTxManagerConfigurer().transactionManager();
    }

    public static void main(String[] args) throws Exception{
        if (args != null && args.length ==2){
            spaceName = args[0];
            containerName= args[1];
        }

        DeserializeRedoLog proccesRedolog= new DeserializeRedoLog();
        System.out.println("verify home is correct should be set as vm arg:" + SystemLocations.singleton().home());

        proccesRedolog.readCodeMap();
        proccesRedolog.proccess();
    }

    /*
        This code is needed in order to deserialize packets internally
     */
    public void readCodeMap(){
        System.out.println("READ CLASS CODES FROM: " + codeMapFile.getFileName());

        if (codeMapFile.toFile().exists()) {
            try (FileInputStream fis = new FileInputStream(codeMapFile.toFile())) {
                try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                    IOUtils.readCodeMaps(ois);
                    //IOUtils.
                    System.out.println("===");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void proccess() throws FileNotFoundException, UnusableEntryException, TransactionException, RemoteException, InterruptedException {
        System.out.println("READ REDO-LOG FILE");
        Path path = SystemLocations.singleton().work("redo-log/" + spaceName);
        String dbName = "sqlite_storage_redo_log_" + containerName;
        System.out.println("Path:" +path + " dbName:"+ dbName);

        DBSwapRedoLogFileConfig<IReplicationOrderedPacket> config =
                new DBSwapRedoLogFileConfig<>(spaceName, containerName, 0);
        config.setKeepDatabaseFile(true);

        SqliteRedoLogFileStorage<IReplicationOrderedPacket> redoLogFile = new SqliteRedoLogFileStorage<>(config);
        StorageReadOnlyIterator<IReplicationOrderedPacket> readOnlyIterator = redoLogFile.readOnlyIterator(0);
        while (readOnlyIterator.hasNext()) {
            IReplicationOrderedPacket packet = readOnlyIterator.next();
            processPacket(packet.getData());
        }

    }


    protected void processSingleEntryData(IReplicationPacketEntryData data) throws UnusableEntryException, TransactionException, RemoteException, InterruptedException {
        boolean writePacket = data instanceof WriteReplicationPacketData?true:false;
        boolean updatePacket = data instanceof UpdateReplicationPacketData?true:false;
        if (writePacket || updatePacket){
            IEntryData entryData = writePacket?getWriteData(data):getUpdateData(data);
            Object[] values1 = entryData.getFixedPropertiesValues();
            Map<String, Object> values2 = entryData.getDynamicProperties();
            System.out.println("Write/Update operation of type: " + data.getTypeName() +" fixed properties:" );
            Arrays.stream(values1).iterator().forEachRemaining(value -> System.out.printf(value + " "));
            System.out.println();
            if (values2 != null){
                System.out.println(" dynamic properties:" );
                values2.forEach((key,value) -> System.out.printf("key:" + key + " value:" + value));
            }

            System.out.println();
        }
        else System.out.println(data);
    }

    protected IEntryData getUpdateData(IReplicationPacketEntryData data){
        UpdateReplicationPacketData writeReplicationPacketData = (UpdateReplicationPacketData)data;
        return writeReplicationPacketData.getMainEntryData();
    }

    protected IEntryData getWriteData(IReplicationPacketEntryData data){
        WriteReplicationPacketData writeReplicationPacketData = (WriteReplicationPacketData)data;
        return writeReplicationPacketData.getMainEntryData();
    }

    protected void processTransactionData(IReplicationPacketData data) throws UnusableEntryException, TransactionException, RemoteException, InterruptedException {
        if (data instanceof TransactionOnePhaseReplicationPacketData){
            System.out.println("START TRANSACTION");
            TransactionOnePhaseReplicationPacketData transactionalPacket = (TransactionOnePhaseReplicationPacketData)data;
            Iterator<IReplicationTransactionalPacketEntryData> iterator = transactionalPacket.iterator();
            while (iterator.hasNext()){
                IReplicationTransactionalPacketEntryData entryData = iterator.next();
                processSingleEntryData(entryData);
            }
            System.out.println("END TRANSACTION");
        }
    }

    public void processPacket(IReplicationPacketData data) throws UnusableEntryException, TransactionException, RemoteException, InterruptedException {
        if (data.isSingleEntryData())
            processSingleEntryData((AbstractReplicationPacketSingleEntryData)data.getSingleEntryData());
        else {
            processTransactionData(data);
        }
    }
}
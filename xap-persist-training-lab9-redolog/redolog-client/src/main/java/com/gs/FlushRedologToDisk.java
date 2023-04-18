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

public class FlushRedologToDisk {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        String group ="xap-16.2.1";
        GigaSpace gs = new GigaSpaceConfigurer(new SpaceProxyConfigurer("redolog").lookupGroups(group).lookupLocators("localhost")).gigaSpace();
        AsyncFuture<Integer> execute = gs.execute(new FlushRedoLogTask());
        Integer totalFlushedPackets = execute.get(60, TimeUnit.SECONDS);
        System.out.println("totalFlushedPackets =" + totalFlushedPackets);
    }
}

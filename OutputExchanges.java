package chat.exchange;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import chat.session.Session;
import chat.session.SessionManager;
import chat.util.Const;

public class OutputExchanges {
    private Queue<OutputExchange> pool = new ArrayDeque<>(Const.Exchange.MAX_OUTPUT_EXCHANGE_SIZE);
    private Queue<OutputExchange> queued = new ArrayDeque<>(Const.Exchange.MAX_INPUT_EXCHANGE_SIZE);
    private List<OutputExchange> pending = new ArrayList<>(Const.Exchange.MAX_OUTPUT_EXCHANGE_SIZE);
    private static int FREEING_PENDING_SIZE = 25;

    public OutputExchanges(){
        long start = System.currentTimeMillis();

        for (int i = 0; i < Const.Exchange.MAX_OUTPUT_EXCHANGE_SIZE; ++i) {
            pool.add(new OutputExchange(i));
        }

        System.out.println("OutputExchanges time: " + (System.currentTimeMillis() - start));

        System.out.println("queued:" + queued.size());
        System.out.println("pool:" + pool.size());
    }

    public boolean hasQueued() {
        return queued.size() > 0 ? true : false;
    }

    public Queue<OutputExchange> getQueued() {
        return queued;
    }

    private void handlePoolShortage() {
        System.out.println("pool create");
        this.echo();
        for (int i = 0; i < FREEING_PENDING_SIZE; i++) {
            OutputExchange outExchange = pending.removeFirst();
            handlePendingClients(outExchange.getReceiversKey());
            pool.add(outExchange.recycle());
        }
        System.out.println();
        System.out.println("end create");
        this.echo();
        System.out.println();
    }

    private void handlePendingClients(Set<SelectionKey> receiversKey) {
        System.out.println("handlePendingClients");
        receiversKey.forEach(key -> {
            SessionManager.disconnect(key);
        });
    }

    public void setDataMoveToQueued(byte[] pdu, Set<SelectionKey> receiversKey) {
        System.out.println("setDataMoveToQueued");
        if (pool.isEmpty()) {
            handlePoolShortage();
            //TODO 現地点ではここでremoveしても無駄な事がほとんどだが
            if (SessionManager.removeDisconnectedClientsFrom(receiversKey).isEmpty()) {
                System.out.println("setDataMoveToQueued receiversKey isEmpty");
                return;
            }
        }
        
        queued.add(pool.remove().set(pdu, receiversKey));

        System.out.println("add to queued");
        this.echo();
        System.out.println();
    }

    public void removeQueuedAddToPool() {
        if (queued.isEmpty()) {
            throw new Error("queued empty");
        }

        pool.add(queued.remove().recycle());
    }

    public void removeQueuedAddToPending() {
        if (queued.isEmpty()) {
            throw new Error("queued empty");
        }

        pending.add(queued.remove());
    }

    public void removePendingAddToPool() {
        if (pending.isEmpty()) {
            throw new Error("pending empty");
        }

        pool.add(pending.removeFirst().recycle());
    }

    public void removePendingAddToPool(int idx) {
        if (pending.isEmpty()) {
            throw new Error("pending empty");
        }

        pool.add(pending.remove(idx).recycle());
    }

    public void echo() {
        System.out.println("pool:" + pool.size());
        System.out.println("queued:" + queued.size());
        System.out.println("pending:" + pending.size());
    }
}

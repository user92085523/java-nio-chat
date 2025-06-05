package chat.exchange;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.ArrayList;
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
        for (int i = 0; i < FREEING_PENDING_SIZE; i++) {
            OutputExchange outExchange = pending.getFirst();
            handlePendingClients(outExchange.getReceiversKey());
            removePendingAddToPool();
            this.echo();
            System.out.println("");
        }
    }

    private void handlePendingClients(Set<SelectionKey> remainingReceiversKey) {
        // System.out.println("clients:" + remainingReceiversKey.size());
        remainingReceiversKey.forEach(key -> {
            SessionManager.disconnect(key);
        });
        // System.out.println("clients:" + remainingReceiversKey.size());
    }

    public void setDataMoveToQueued(byte[] pdu, Set<SelectionKey> receiversKey) {
        if (pool.isEmpty()) {
            handlePoolShortage();
        }
        // System.out.println("adding to queued");
        // this.echo();
        System.out.println("adding receiversKey isEmpty:" + receiversKey.isEmpty());
        
        queued.add(pool.remove().set(pdu, receiversKey));
        System.out.println("add to queued");
        this.echo();
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

    // public void run() {
    //     Random random = new Random();
    //     int x = random.nextInt(1, 20);
    //     int y = random.nextInt(x);
    //     System.out.println("x=" + x + " y=" + y);

    //     for (int i = 0; i < x; i++) {
    //         if (pool.isEmpty()) {
    //             for (OutputExchange output : queued) {
    //                 System.out.println("id=" + output.getId());
    //             }
    //             throw new Error("pool empty");
    //         }
    //         queued.add(pool.remove());
    //     }

    //     for (int i = 0; i < y; i++) {
    //         if (queued.isEmpty()) {
    //             throw new Error("queued empty");
    //         }
    //         pool.add(queued.remove());
    //     }

    //     System.out.println("queued:" + queued.size());
    //     System.out.println("pool:" + pool.size());
    //     System.out.println("queued first id:" + queued.peek().getId());
    // }

    public void echo() {
        System.out.println("pool:" + pool.size());
        System.out.println("queued:" + queued.size());
        System.out.println("pending:" + pending.size());
    }
}

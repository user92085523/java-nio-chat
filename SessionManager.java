package chat.session;

import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import chat.session.ClientInfo;
import chat.session.Session;
import chat.util.Const;

public class SessionManager {
    private static long sessionCnt = 0L;
    private static Set<Long> connectedClientsId = new HashSet<>();
    private static HashMap<Long, SelectionKey> connectedClientsEntity = new HashMap<>();
    private static Set<SelectionKey> disconnectedClients = new HashSet<>();

    public SessionManager() {
        System.out.println("SessionManager: " + hashCode());
    }

    public static void accept(SelectionKey key) {
        if (connectedClientsId.size() >= Const.Server.MAX_CLIENT) {
            System.out.println("max client size");
            //TODO
            return;
        }

        try {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            Selector selector = key.selector();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            SelectionKey tempKey = sc.register(selector, SelectionKey.OP_READ);
            Session session = createSession(tempKey);
            tempKey.attach(session);

            addClient(session.getClientInfo().getId(), tempKey);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void disconnect(SelectionKey key) {
        if (disconnectedClients.contains(key)) {
            System.out.println("already DCed");
            return;
        }

        try {
            SocketChannel sc = (SocketChannel) key.channel();
            Session session = (Session) key.attachment();
            sc.close();
            key.cancel();

            disconnectedClients.add(key);

            System.out.println("disconnect");
            removeClient(session.getClientInfo().getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleClosedChannelException(SelectionKey key) {
        System.out.println("handleClosedChannelException");
        key.cancel();
        disconnectedClients.add(key);
        Session session = (Session) key.attachment();
        removeClient(session.getClientInfo().getId());
    }

    private static Session createSession(SelectionKey key) {
        return new Session(--sessionCnt, key);
    }

    private static void addClient(long id, SelectionKey key) {
        connectedClientsId.add(id);
        connectedClientsEntity.put(id, key);
    }

    private static void removeClient(long id) {
        if (connectedClientsId.contains(id)) {
            connectedClientsId.remove(id);
        } else {
        }

        if (connectedClientsEntity.containsKey(id)) {
            disconnectedClients.add(connectedClientsEntity.get(id));
            System.out.println("dcclients:" + disconnectedClients);
            connectedClientsEntity.remove(id);
        } else {
        }
    }

    public static int getClientsCnt() {
        return connectedClientsId.size();
    }

    public static void clearDisconnectedClients() {
        disconnectedClients.clear();
    }

    public static Set<SelectionKey> getClientsKeyClone() {
        return new HashSet<SelectionKey>(connectedClientsEntity.values());
    }

    public static Set<SelectionKey> removeDisconnectedClientsFrom(Set<SelectionKey> receiversKey) {
        if (!disconnectedClients.isEmpty()) {
            receiversKey.removeAll(disconnectedClients);
        }

        return receiversKey;
    }
}

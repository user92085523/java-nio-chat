package chat.session;

import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import chat.session.ClientID;
import chat.session.Session;
import chat.util.Const;

public class SessionManager {
    private static long sessionCnt = 0L;
    private static Set<Long> clientsId = new HashSet<>();
    private static HashMap<Long, SelectionKey> clientsEntity = new HashMap<>();

    public SessionManager() {
        System.out.println("SessionManager: " + hashCode());
    }

    public static void accept(SelectionKey key) {
        if (clientsId.size() == Const.Server.MAX_CLIENT) {
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

            addClient(session.getClientID().getId(), tempKey);

            // clientsId.stream()
            //     .forEach(id -> System.out.println("id: " + id));
            // clientsEntity.forEach((k, v) -> System.out.println("key: " + k + "       value: " + v));
            
        } catch (Exception e) {
            System.err.println(e);
            System.exit(-1);
        }
    }

    public static void disconnect(SelectionKey key) {
        try {
            SocketChannel sc = (SocketChannel) key.channel();
            Session session = (Session) key.attachment();
            sc.close();
            key.cancel();

            removeClient(session.getClientID().getId());

            // clientsId.stream()
            //     .forEach(id -> System.out.println("id: " + id));

            // clientsEntity.forEach((k, v) -> System.out.println("key: " + k + "       value: " + v));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static Session createSession(SelectionKey key) {
        return new Session(--sessionCnt, key);
    }

    private static void addClient(long id, SelectionKey key) {
        clientsId.add(id);
        clientsEntity.put(id, key);
    }

    private static  void removeClient(long id) {
        clientsId.remove(id);
        clientsEntity.remove(id);
    }

    public static Set<SelectionKey> getClientsKey() {
        return new HashSet<SelectionKey>(clientsEntity.values());
    }
}

package chat.session;

import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

import chat.session.ClientID;
import chat.session.Session;
import chat.util.Const;

public class SessionManager {
    private long sessionCnt = 0;
    private Set<ClientID> clients = new HashSet<>(Const.Server.MAX_CLIENT);

    public SessionManager() {
        System.out.println("SessionManager: " + hashCode());
    }

    public void accept(SelectionKey key) {
        if (clients.size() == Const.Server.MAX_CLIENT) {
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
            addToClients(session.getClientID());
            System.out.println(session.getClientID());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(-1);
        }
    }

    public void disconnect(SelectionKey key) {
        try {
            SocketChannel sc = (SocketChannel) key.channel();
            Session session = (Session) key.attachment();
            sc.close();
            key.cancel();
            System.out.println("clients cnt: " + clients.size());
            clients.remove(session.getClientID());
            System.out.println("clients cnt: " + clients.size());
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public Session createSession(SelectionKey key) {
        return new Session(--sessionCnt, key);
    }

    public void addToClients(ClientID clientID) {
        clients.add(clientID);
    }



    public Set<ClientID> getClients() {
        return clients;
    }
}

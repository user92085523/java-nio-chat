package chat.session;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import chat.exchange.InputTempStorage;
import chat.exchange.PendingExchanges;
import chat.util.Const;

public class Session {
    private final InputTempStorage storage = new InputTempStorage();
    private final ClientInfo clientInfo;
    private final PendingExchanges pendingExchanges = new PendingExchanges();

    public Session(long id, SelectionKey key) {
        System.out.println("Session: " + hashCode());
        clientInfo = new ClientInfo(id, key);
    }

    public boolean isPending() {
        return pendingExchanges.hasPendingExchange();
    }

    public Session() {
        clientInfo = null;
    }

    public InputTempStorage getStorage() {
        return storage;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public PendingExchanges getPendingExchanges() {
        return pendingExchanges;
    }
}

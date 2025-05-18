package chat.session;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import chat.exchange.InputTempStorage;
import chat.util.Const;

public class Session {
    private final InputTempStorage storage = new InputTempStorage();
    private final ClientID clientID;

    public Session(long id, SelectionKey key) {
        System.out.println("Session: " + hashCode());
        clientID = new ClientID(id, key);
    }

    public Session() {
        clientID = null;
    }

    public InputTempStorage getStorage() {
        return storage;
    }

    public ClientID getClientID() {
        return clientID;
    }
}

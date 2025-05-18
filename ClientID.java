package chat.session;

import java.nio.channels.SelectionKey;

public class ClientID {
    private long id;
    private String name = null;
    private final SelectionKey key;

    public ClientID(long id, SelectionKey key) {
        System.out.println("ClientID: " + hashCode());
        this.id = id;
        this.key = key;
    }

    public long getId() {
        return id;
    }

    public void updateId(long id) {
        this.id = id;
    }

    public SelectionKey getKey() {
        return this.key;
    }

    public String toString() {
        return new String("id: " + id + "\nname: " + name + "\nkey: " + key);
    }
}

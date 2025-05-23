package chat.session;

import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;

public class ClientID {
    private long id;
    private byte[] name;
    private final SelectionKey key;

    public ClientID(long id, SelectionKey key) {
        System.out.println("ClientID: " + hashCode());
        this.id = id;
        this.key = key;

        String temp = "太郎" + id;
        name = temp.getBytes();
    }

    public long getId() {
        return id;
    }

    public void updateId(long id) {
        this.id = id;
    }

    public byte[] getName() {
        return name;
    }

    public SelectionKey getKey() {
        return this.key;
    }

    public String toString() {
        return new String("id: " + id + "\nname: " + name + "\nkey: " + key);
    }
}

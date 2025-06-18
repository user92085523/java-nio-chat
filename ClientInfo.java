package chat.session;

import java.nio.channels.SelectionKey;

public class ClientInfo {
    private long id;
    private byte[] name;
    private int nameSize;
    private final SelectionKey key;

    public ClientInfo(long id, SelectionKey key) {
        System.out.println("ClientInfo: " + hashCode());
        this.id = id;
        this.key = key;

        String temp = "太郎" + id;
        name = temp.getBytes();

        nameSize = name.length;
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

    public int getNameSize() {
        return nameSize;
    }

    public SelectionKey getKey() {
        return this.key;
    }

    public String toString() {
        return new String("id: " + id + "\nname: " + name + "\nkey: " + key);
    }
}

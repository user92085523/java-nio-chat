package chat.exchange;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;
import java.util.Set;

//TODO outとinでわけるかも,recycleしてDirectBufferに変更
public class Exchange {
    public long Id;
    public ByteBuffer Pdu;
    public SelectionKey SenderKey;
    public Set<SelectionKey> ReceiversKey;

    public static class Builder {
        private final long Id;
        private final ByteBuffer Pdu;
        private final SelectionKey SenderKey;
        private Set<SelectionKey> ReceiversKey;

        public Builder(long id, ByteBuffer pdu, SelectionKey key) {
            Id = id;

            Pdu = ByteBuffer.allocate(pdu.limit());
            Pdu.put(pdu);
            Pdu.flip();
            pdu.clear();

            SenderKey = key;
        }

        public Builder receiversKey(Set<SelectionKey> receiversKey) {
            ReceiversKey = receiversKey;
            return this;
        }

        public Exchange build() {
            return new Exchange(this);
        }

    }

    private Exchange(Builder builder) {
        Id = builder.Id;
        Pdu = builder.Pdu;
        SenderKey = builder.SenderKey != null ? builder.SenderKey : null;
        ReceiversKey = builder.ReceiversKey != null ? builder.ReceiversKey : null;
    }

    public String toString() {
        return new String("Id: " + Id + "\nPdu: " + Pdu + "\nSenderKey: " + SenderKey + "\nReceiversKey: " + ReceiversKey + "\nReceiversKey Size: " + ReceiversKey.size());
    }

    public String pduToString() {
        return new String(Pdu.array(), StandardCharsets.UTF_8);
    }
}

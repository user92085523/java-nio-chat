package chat.exchange;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Exchange {
    public final long Id;
    public final ByteBuffer Pdu;
    public final SelectionKey SenderKey;
    public final Set<SelectionKey> ReceiverKeys;

    public static class Builder {
        private final long Id;
        private final ByteBuffer Pdu;
        private final SelectionKey SenderKey;
        private Set<SelectionKey> ReceiverKeys;

        public Builder(long id, ByteBuffer pdu, SelectionKey key) {
            Id = id;

            Pdu = ByteBuffer.allocate(pdu.limit());
            Pdu.put(pdu);
            Pdu.flip();
            pdu.clear();

            SenderKey = key;
        }

        public Builder receiverKeys(Set<SelectionKey> receiverKeys) {
            ReceiverKeys = receiverKeys;
            return this;
        }

        public Exchange build() {
            return new Exchange(this);
        }

    }

    private Exchange(Builder builder) {
        Id = builder.Id;
        Pdu = builder.Pdu;
        SenderKey = builder.SenderKey;
        ReceiverKeys = builder.ReceiverKeys != null ? builder.ReceiverKeys : null;
    }

    public String toString() {
        return new String("Id: " + Id + "\nPdu: " + Pdu + "\nSenderKey: " + SenderKey + "\nReceiverKeys: " + ReceiverKeys);
    }

    public String pduToString() {
        return new String(Pdu.array(), StandardCharsets.UTF_8);
    }
}

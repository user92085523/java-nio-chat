package chat.exchange;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Set;

import chat.util.Const;

public class OutputExchange {
    private int id;
    private ByteBuffer pdu = ByteBuffer.allocateDirect(Const.Pdu.MAX_SIZE);
    private int pduSize;
    private Set<SelectionKey> receiversKey;

    public OutputExchange set(byte[] pdu, Set<SelectionKey> receiversKey) {
        this.pdu.put(pdu);
        this.pdu.flip();

        this.pduSize = pdu.length;
        this.receiversKey = receiversKey;

        return this;
    }

    public OutputExchange(int cnt){
        id = cnt;
    }

    public int getId() {
        return id;
    }

    public ByteBuffer getPdu() {
        return pdu;
    }

    public void rewind() {
        this.pdu.rewind();
    }

    public int getPduSize() {
        return pduSize;
    }

    public Set<SelectionKey> getReceiversKey() {
        return receiversKey;
    }

    public OutputExchange recycle() {
        pdu.clear();
        pduSize = 0;

        return this;
    }
}

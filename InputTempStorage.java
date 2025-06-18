package chat.exchange;

import java.nio.ByteBuffer;

import chat.util.Const;
import chat.util.Header;

public class InputTempStorage {
    private final ByteBuffer InputBuf;
    private final byte[] h1 = new byte[Const.Pdu.H1_SIZE];
    private int payloadSize;
    private int pduSize;

    public InputTempStorage() {
        InputBuf = ByteBuffer.allocateDirect(1024 * 1024);
    }

    public InputTempStorage(int size) {
        InputBuf = ByteBuffer.allocateDirect(size);
    }

    public ByteBuffer getBuf() {
        return InputBuf;
    }

    public int setPayloadSize() {
        InputBuf.get(h1);
        InputBuf.rewind();

        payloadSize = Header.getPayloadSize(h1);

        return payloadSize;
    }

    public int setPduSize() {
        pduSize = payloadSize + Const.Pdu.HEADER_TOTAL_SIZE;

        return pduSize;
    }
    
    public void resetPduSize() {
        pduSize = 0;
    }

    public int getPduSize() {
        return pduSize;
    }

    public int getPayloadSize() {
        return payloadSize;
    }

    public int getBufPosition() {
        return InputBuf.position();
    }

    public int getBufLimit() {
        return InputBuf.limit();
    }

    public void flipBuf() {
        InputBuf.flip();
    }

    public void compactBuf() {
        InputBuf.compact();
    }
}

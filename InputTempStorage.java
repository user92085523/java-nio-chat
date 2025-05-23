package chat.exchange;

import java.nio.ByteBuffer;

import chat.util.Header;

public class InputTempStorage {
    private final ByteBuffer InputBuf;
    private final ByteBuffer Pdu = ByteBuffer.allocateDirect(1024);
    private final ByteBuffer PayloadSizeBuf = ByteBuffer.allocate(3);
    private int pduSize;

    public InputTempStorage() {
        InputBuf = ByteBuffer.allocateDirect(1024 * 1024);
    }

    public InputTempStorage(int size) {
        InputBuf = ByteBuffer.allocateDirect(size);
    }

    public ByteBuffer getInputBuf() {
        return InputBuf;
    }

    public ByteBuffer getPdu() {
        return Pdu;
    }

    public int getPayloadSize() {
        int limit = InputBuf.limit();

        InputBuf.limit(PayloadSizeBuf.capacity());
        PayloadSizeBuf.put(InputBuf);
        PayloadSizeBuf.clear();
        InputBuf.limit(limit);
        InputBuf.rewind();

        return Header.getPayloadSize(PayloadSizeBuf.array());
    }

    public void readPdu() {
        int limit = InputBuf.limit();

        InputBuf.limit(pduSize);
        Pdu.put(InputBuf);
        Pdu.flip();
        InputBuf.limit(limit);
        InputBuf.compact();
    }

    public void setPduSize(int size) {
        pduSize = size;
    }
    
    public void resetPduSize() {
        pduSize = 0;
    }

    public int getPduSize() {
        return pduSize;
    }

    public int getInputBufPosition() {
        return InputBuf.position();
    }

    public int getInputBufLimit() {
        return InputBuf.limit();
    }

    public void flipInputBuf() {
        InputBuf.flip();
    }

    public void compactInputBuf() {
        InputBuf.compact();
    }
}

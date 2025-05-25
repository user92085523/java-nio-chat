package chat.exchange;

import java.nio.ByteBuffer;

import chat.util.Const;

public class InputExchange {
    public InputExchange(){}

    private byte[] h1 = new byte[Const.Pdu.H1_SIZE];
    private byte[] h2 = new byte[Const.Pdu.H2_SIZE];
    private byte[] payload = new byte[Const.Pdu.PAYLOAD_MAX_SIZE];
    private int payloadSize;

    public void readInput(ByteBuffer tempBuffer, int payloadSize) throws Exception{
        this.payloadSize = payloadSize;
        tempBuffer.get(h1);
        tempBuffer.get(h2);
        tempBuffer.get(Const.Pdu.HEADER_TOTAL_SIZE, payload, 0, payloadSize + 1);
        tempBuffer.position(payloadSize + Const.Pdu.HEADER_TOTAL_SIZE);
        tempBuffer.compact();
    }

    public void reset() {
        payloadSize = 0;
    }
}

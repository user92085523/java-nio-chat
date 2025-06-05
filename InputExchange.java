package chat.exchange;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Arrays;

import chat.exchange.enumerate.Command;
import chat.util.Const;

public class InputExchange {
    public InputExchange(){}

    private byte[] h1 = new byte[Const.Pdu.H1_SIZE];
    private Command cmd;
    private byte[] payload = new byte[Const.Pdu.PAYLOAD_MAX_SIZE];
    private int payloadSize;
    private SelectionKey senderKey;

    public void set(ByteBuffer tempBuffer, int payloadSize, SelectionKey senderKey) throws Exception{
        this.payloadSize = payloadSize;

        tempBuffer.get(h1);
        cmd = Command.resolve(tempBuffer.get());
        tempBuffer.get(Const.Pdu.HEADER_TOTAL_SIZE, payload, 0, payloadSize);
        tempBuffer.position(payloadSize + Const.Pdu.HEADER_TOTAL_SIZE);
        tempBuffer.compact();

        this.senderKey = senderKey;
    }

    public byte[] getH1() {
        return h1;
    }

    public Command getCommand() {
        return cmd;
    }

    public int getPayloadSize() {
        return payloadSize;
    }

    public byte[] getPayload() {
        return Arrays.copyOfRange(payload, 0, payloadSize);
    }

    public SelectionKey getSenderKey() {
        return senderKey;
    }

    public void reset() {
        payloadSize = 0;
    }
}

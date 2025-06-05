package chat.exchange;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import chat.util.Const;

public class PendingExchanges {
    private final List<ByteBuffer> pdus = new ArrayList<>(Const.Exchange.MAX_OUTPUT_EXCHANGE_SIZE);
    private final List<Integer> pdusSize = new ArrayList<>(Const.Exchange.MAX_OUTPUT_EXCHANGE_SIZE);
    private final List<Integer> pdusBytesWritten = new ArrayList<>(Const.Exchange.MAX_OUTPUT_EXCHANGE_SIZE);

    public PendingExchanges(){}

    public boolean hasPendingExchange() {
        return !pdus.isEmpty();
    }

    public void add(ByteBuffer pdu, int pduSize, int bytesWritten) {
        pdus.add(pdu);
        pdusSize.add(pduSize);
        pdusBytesWritten.add(bytesWritten);
    }

    public int getPendingCnt() {
        return pdus.size();
    }
}

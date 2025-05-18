package chat.exchange;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Set;

public class ExchangeManager {
    private long inputCnt = 0;
    private long outputCnt = 0;

    public ExchangeManager() {
        System.out.println("ExchangeManager: " + hashCode());
    }

    public Exchange createInputExchange(ByteBuffer pdu, SelectionKey sender) {
        return new Exchange.Builder(++inputCnt, pdu, sender).build();
    }

    public Exchange createOutputExchange(ByteBuffer pdu, SelectionKey sender, Set<SelectionKey> receivers) {
        return new Exchange.Builder(++outputCnt, pdu, sender).receiverKeys(receivers).build();
    }
}

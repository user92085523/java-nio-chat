package chat.exchange;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import chat.exchange.Exchange;

public class ExchangeManager {
    private final ExchangeProcessor exchangeProcessor;
    // private static List<Exchange> outExchanges;
    private static long inputCnt = 0L;
    private static long outputCnt = 0L;

    public ExchangeManager(ExchangeProcessor exchangeProcessor) {
        System.out.println("ExchangeManager: " + hashCode());

        this.exchangeProcessor = exchangeProcessor;
    }

    public List<Exchange> process(List<Exchange> inExchanges) {
        List<Exchange> outExchanges = new ArrayList<>();
        for (Exchange inExchange : inExchanges) {
            Exchange outExchange = exchangeProcessor.process(inExchange);
            if (outExchange != null) {
                outExchanges.add(outExchange);
            }
        }
        return outExchanges.size() != 0 ? outExchanges : null;
    }

    public static Exchange createInputExchange(ByteBuffer pdu, SelectionKey sender) {
        return new Exchange.Builder(++inputCnt, pdu, sender).build();
    }

    public static Exchange createOutputExchange(ByteBuffer pdu, SelectionKey sender, Set<SelectionKey> receivers) {
        return new Exchange.Builder(++outputCnt, pdu, sender).receiversKey(receivers).build();
    }
}

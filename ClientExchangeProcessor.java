package chat.exchange;

import java.nio.charset.StandardCharsets;
import java.util.List;

import chat.exchange.Exchange;
import chat.exchange.enumerate.Command;

public class ClientExchangeProcessor extends ExchangeProcessor{
    public ClientExchangeProcessor() {
        System.out.println("ClientExchangeProcessor: " + hashCode());
    }

    public Exchange process(Exchange exchange) {
        ExchangeMetadata metadata = new ExchangeMetadata(exchange.Pdu);
        return resolve(exchange, metadata.getCommand());
    }

    private Exchange resolve(Exchange exchange, Command cmd) {
        Exchange outExchange = null;
        switch (cmd) {
            case DISPLAY_MSG_ALL, DISPLAY_MSG_GROUP, DISPLAY_MSG_TO -> {
                handleMSG(exchange, cmd);
                return null;
            }
            default -> {
                break;
            }
        }

        return outExchange;
    }

    private Exchange handleMSG(Exchange exchange, Command cmd) {
        String output = new String(exchange.Pdu.array(), StandardCharsets.UTF_8);
        System.out.println(output);
        return null;
    }
}

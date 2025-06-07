package chat.client;

import javax.management.InvalidAttributeValueException;

import chat.event.Processor;
import chat.exchange.InputExchange;
import chat.exchange.OutputExchanges;
import chat.exchange.enumerate.Command;

public class ClientProcessor extends Processor {

    protected void resolve(InputExchange inExchange, OutputExchanges outExchanges) throws Exception {
        Command cmd = inExchange.getCommand();
        switch(cmd) {
            case DISPLAY_MSG_ALL, DISPLAY_MSG_GROUP, DISPLAY_MSG_TO -> {
            }
            default -> {
                throw new InvalidAttributeValueException("this should not happen");
            }
        }
    }

    private void display(Command cmd, InputExchange inExchange) throws Exception{
        switch(cmd) {
            case DISPLAY_MSG_ALL -> {
                System.out.println(inExchange.getPayload());
            }
            default -> {
                throw new InvalidAttributeValueException("this should not happen");
            }
        }
    }
}

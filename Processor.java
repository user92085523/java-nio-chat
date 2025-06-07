package chat.event;

import chat.exchange.InputExchange;
import chat.exchange.InputExchanges;
import chat.exchange.OutputExchanges;

public abstract class Processor {
    public Processor(){}

    public void handle(InputExchanges inExchanges, OutputExchanges outExchanges) {
        try {
            for (int i = 0, len = inExchanges.size(); i < len; i++) {
                resolve(inExchanges.getInput(i), outExchanges);
            }   
        } catch (Exception e) {
            e.printStackTrace();
            //TODO DC? clear outExchange.queued?
            System.out.println("DC");
        }
    }

    protected abstract void resolve(InputExchange inExchange, OutputExchanges outExchanges) throws Exception;
}

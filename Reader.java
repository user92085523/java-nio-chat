package chat.event;

import java.nio.channels.SelectionKey;

import chat.exchange.InputExchanges;

public abstract class Reader {
    public Reader(){}
    public abstract int handle(SelectionKey key, InputExchanges inExchanges);
}
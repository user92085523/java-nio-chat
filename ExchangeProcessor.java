package chat.exchange;

import java.util.List;

import chat.exchange.Exchange;

public abstract class ExchangeProcessor {
    public abstract Exchange process(Exchange inExchange);
}
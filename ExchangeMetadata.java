package chat.exchange;

import java.nio.ByteBuffer;

import chat.exchange.enumerate.Command;

public class ExchangeMetadata {
    private Command command;

    public ExchangeMetadata(ByteBuffer Pdu) {
        Pdu.position(3);
        this.command = Command.resolve(Pdu.get());
    }

    public Command getCommand() {
        return command;
    }
}

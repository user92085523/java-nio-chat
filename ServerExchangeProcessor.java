package chat.exchange;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.List;

import chat.exchange.Exchange;
import chat.exchange.ExchangeManager;
import chat.exchange.ExchangeMetadata;
import chat.exchange.enumerate.Command;
import chat.session.Session;
import chat.session.SessionManager;
import chat.util.Const;
import chat.util.Header;

public class ServerExchangeProcessor extends ExchangeProcessor{
    public ServerExchangeProcessor() {
        System.out.println("ServerExchangeProcessor: " + hashCode());
    }

    public Exchange process(Exchange inExchange) {
        ExchangeMetadata metadata = new ExchangeMetadata(inExchange.Pdu);
        return resolve(inExchange, metadata.getCommand());
    }

    private Exchange resolve(Exchange exchange, Command cmd) {
        switch (cmd) {
            case IM_ALIVE -> {
                return null;
            } 
            case MSG_ALL, MSG_GROUP, MSG_TO -> {
                return handleMSG(exchange, cmd);
            }
            default -> {
                return null;
            }
        }
    }

    public Exchange handleMSG(Exchange exchange, Command cmd) {
        Session session = (Session) exchange.SenderKey.attachment();
        byte[] payload = new byte[exchange.Pdu.remaining()];
        exchange.Pdu.get(payload);

        Exchange outExchange = null;
        switch (cmd) {
            case MSG_ALL -> {
                byte[] senderName = session.getClientID().getName();
                ByteBuffer newPdu = buildOutputPdu(senderName, payload);
                outExchange = ExchangeManager.createOutputExchange(newPdu, null, SessionManager.getClientsKey());
            }
            case MSG_GROUP -> {
            }
            case MSG_TO -> {
            }
            default -> {
                throw new InvalidParameterException("client should not send this cmd maybe dc");
            }
        }

        return outExchange;
    }

    private ByteBuffer buildOutputPdu(byte[]... byteArray) {
        byte command = (byte) 10;
        int size = 0;

        for (int i = 0; i < byteArray.length; i++) {
            size += byteArray[i].length;
            if (i == byteArray.length - 1) break;
            size++;
        }

        byte[] h1 = Header.getPayloadSize(size);

        ByteBuffer newPdu = ByteBuffer.allocate(size + Const.Pdu.MAX_SIZE);


        newPdu.put(h1);
        newPdu.put(command);

        for (int i = 0; i < byteArray.length; i++) {
            newPdu.put(byteArray[i]);
            if (i == byteArray.length - 1) break;
            newPdu.put((byte) 32);
        }

        // System.out.println("newPdu:" + newPdu);

        // for (byte b : newPdu.array()) {
        //     System.out.println(b);
        // }

        newPdu.rewind();
        return newPdu;
    }
}

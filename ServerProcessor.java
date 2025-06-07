package chat.server;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Set;

import javax.naming.directory.InvalidAttributeValueException;

import chat.event.Processor;
import chat.exchange.InputExchange;
import chat.exchange.InputExchanges;
import chat.exchange.OutputExchanges;
import chat.exchange.enumerate.Command;
import chat.session.ClientInfo;
import chat.session.Session;
import chat.session.SessionManager;
import chat.util.Header;

public class ServerProcessor extends Processor {
    private byte[] separator = new byte[] {0x20};

    protected void resolve(InputExchange inExchange, OutputExchanges outExchanges) throws Exception{
        Command cmd = inExchange.getCommand();
        switch (cmd) {
            case MSG_ALL, MSG_GROUP, MSG_TO -> {
                byte[] pdu = createPdu(cmd, inExchange);
                Set<SelectionKey> receiversKey = getReceiversKey(cmd, inExchange);
                if (!receiversKey.isEmpty()) {
                    outExchanges.setDataMoveToQueued(pdu, receiversKey);
                }
            }
            default -> {
                throw new InvalidAttributeValueException("Client sending Invalid cmd");
            }
        }
    }

    private Set<SelectionKey> getReceiversKey(Command cmd, InputExchange inExchange) {
        SelectionKey senderKey = inExchange.getSenderKey();
        Set<SelectionKey> clientsKeyClone = SessionManager.getClientsKeyClone();

        Set<SelectionKey> receiversKey = switch(cmd) {
            case MSG_ALL -> {
                // clientsKeyClone.forEach(key -> {
                //     if (!key.isValid()) {
                //         clientsKeyClone.remove(key);
                //     }
                // });
                // clientsKeyClone.remove(senderKey);
                yield clientsKeyClone;
            }
            default -> {
                yield null;
            }
        };

        return receiversKey;
    }

    private byte[] createPdu(Command cmd, InputExchange inExchange) {
        byte[] h1 = createHeader1(cmd, inExchange);
        byte[] h2 = createHeader2(cmd);
        byte[] payload = createPayload(cmd, inExchange);

        return concatByteArrays(h1, h2, payload);
    }

    private byte[] createHeader1(Command cmd, InputExchange inExchange) {
        Session session = (Session) inExchange.getSenderKey().attachment();
        ClientInfo clientInfo = session.getClientInfo();

        switch (cmd) {
            case MSG_ALL -> {
                int payloadPrefixSize = clientInfo.getNameSize();
                int separatorCnt = 1;
                return Header.getPayloadSize(payloadPrefixSize + inExchange.getPayloadSize() + separatorCnt);
            }
            case MSG_GROUP -> {

            }
            case MSG_TO -> {

            }
            default -> {
                return null;
            }
        }

        return null;
    }

    private byte[] createHeader2(Command cmd) {
        byte[] h2 = switch(cmd) {
            case MSG_ALL, MSG_GROUP, MSG_TO -> {
                yield new byte[] {(byte) ~Command.desolve(cmd)};
            }
            default -> {
                yield null;
            }
        };

        return h2;
    }

    private byte[] createPayload(Command cmd, InputExchange inExchange) {
        Session session = (Session) inExchange.getSenderKey().attachment();
        ClientInfo clientInfo = session.getClientInfo();
        switch (cmd) {
            case MSG_ALL -> {
                return concatByteArrays(clientInfo.getName(), separator, inExchange.getPayload());
            }
            case MSG_GROUP -> {
                return null;
            }
            case MSG_TO -> {
                return null;
            }
            default -> {
                return null;
            }
        }
    }

    private byte[] concatByteArrays(byte[]... srcs) {
        int size = 0;

        for (byte[] bs : srcs) {
            size += bs.length;
        }

        byte[] concated = new byte[size];

        for (int i = 0, pos = 0, srcsCnt = srcs.length; i < srcsCnt; i++) {
            System.arraycopy(srcs[i], 0, concated, pos, srcs[i].length);
            pos += srcs[i].length;
        }

        return concated;
    }
}

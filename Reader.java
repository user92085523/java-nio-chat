package chat.event;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import javax.management.InvalidAttributeValueException;

import chat.exchange.InputTempStorage;
import chat.exchange.Exchange;
import chat.exchange.ExchangeManager;
import chat.exchange.InputExchanges;
import chat.session.Session;
import chat.session.SessionManager;
import chat.util.Header;
import chat.util.Const;

public final class Reader {
    public final InputExchanges inputExchanges = new InputExchanges();

    public Reader() {
        System.out.println("Reader: " + hashCode());
    }

    public InputExchanges handle(SelectionKey key) {
        try {
            SocketChannel sc = (SocketChannel) key.channel();
            Session session = (Session) key.attachment();
            InputTempStorage storage = session.getStorage();

            System.out.println("before read Buf: " + storage.getBuf());

            if (sc.read(storage.getBuf()) == -1) {
                System.out.println("DC");
                SessionManager.disconnect(key);
                return null;
            }

            int limit = storage.getBufPosition();

            System.out.println("Buf:" + storage.getBuf());

            if (limit < Const.Pdu.HEADER_TOTAL_SIZE + 1 || storage.getPduSize() > limit) {
                System.out.println("data too small to process");
                return null;
            }

            while (true) {

                storage.flipBuf();

                if (storage.getPduSize() == 0) {
                    int payloadSize = storage.setPayloadSize();

                    if (payloadSize < Const.Pdu.PAYLOAD_MIN_SIZE || payloadSize > Const.Pdu.PAYLOAD_MAX_SIZE) {
                        SessionManager.disconnect(key);
                        System.out.println("DC");
                        throw new InvalidAttributeValueException("Logic Error OR Client sending illegal Header\npduSize :" + payloadSize);
                    }

                    if (storage.setPduSize() > storage.getBufLimit()) {
                        storage.compactBuf();
                        return inputExchanges.getInputCnt() > 0 ? inputExchanges : null;
                    }
                }

                inputExchanges.readInto(storage.getBuf(), storage.getPayloadSize());

                storage.resetPduSize();

                if (storage.getBufPosition() < Const.Pdu.HEADER_TOTAL_SIZE + 1) {
                    break;
                }
            }

            System.out.println("Buf:" + storage.getBuf());
        } catch (Exception e) {
            if (e instanceof InvalidAttributeValueException) {
                e.printStackTrace();
            } else {
                e.printStackTrace();
            }
            System.out.println("DC");
            SessionManager.disconnect(key);
            inputExchanges.reset();
            return null;
        }

        return inputExchanges;
    }

}
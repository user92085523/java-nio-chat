package chat.event;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import chat.exchange.InputTempStorage;
import chat.exchange.Exchange;
import chat.exchange.ExchangeManager;
import chat.session.Session;
import chat.session.SessionManager;
import chat.util.Header;
import chat.util.Const;

public class Reader {
    public Reader() {
        System.out.println("Reader: " + hashCode());
    }

    public List<Exchange> handle(
        SessionManager sessionManager,
        ExchangeManager exchangeManager,
        SelectionKey key
    ) {
        List<Exchange> exchanges = new ArrayList<>();

        try {
            SocketChannel sc = (SocketChannel) key.channel();
            Session session = (Session) key.attachment();
            InputTempStorage storage = session.getStorage();
            System.out.println("before read Buf: " + storage.getInputBuf());
            int bytes_read = sc.read(storage.getInputBuf());
            int limit = storage.getInputBufPosition();
            System.out.println("Buf:" + storage.getInputBuf());

            if (bytes_read == -1) {
                System.out.println("DC");
                sessionManager.disconnect(key);
                return null;
            }

            if (limit < 9 || storage.getPduSize() > limit) {
                System.out.println("pduSize: " + storage.getPduSize());
                System.out.println("not enough data");
                return null;
            }

            System.out.println("before loop Buf: " + storage.getInputBuf());
            // long startTime = System.currentTimeMillis();

            while (true) {

                storage.flipInputBuf();

                if (storage.getPduSize() == 0) {
                    storage.setPduSize(storage.getPayloadSize() + Const.Header.TOTAL_SIZE);
                }

                if (storage.getPduSize() != 13 && storage.getPduSize() != 17) {
                    System.out.println("pduSize:" + storage.getPduSize());
                    System.out.println("Buf: " + storage.getInputBuf());
                    throw new Error("Logic Error: chat.util.Header?");
                }

                if (storage.getInputBufLimit() < storage.getPduSize()) {
                    storage.compactInputBuf();
                    break;
                }

                storage.readPdu();
                exchanges.add(exchangeManager.createInputExchange(storage.getPdu(), key));

                storage.resetPduSize();

                if (storage.getInputBufPosition() < 9) {
                    break;
                }
            }

            // long diff = System.currentTimeMillis() - startTime;

            // System.out.println("time takes: " + diff + "ms");

            System.out.println("Buf:" + storage.getInputBuf());
        } catch (Exception e) {
            System.err.println(e);
        }

        return exchanges.size() != 0 ? exchanges : null;
    }
}
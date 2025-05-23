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

public final class Reader {
    public static final List<Exchange> inExchanges = new ArrayList<>(50);

    // public static final List<InputExchange> inputExchanges = new ArrayList<>(10);
    // static{
    //     for (int i = 0; i < 10; i++) {
    //         inputExchanges.add(new InputExchange());
    //     }
    // }

    public Reader() {
        System.out.println("Reader: " + hashCode());
    }

    public List<Exchange> handle(SelectionKey key) {
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
                SessionManager.disconnect(key);
                return null;
            }

            if (limit < Const.Header.TOTAL_SIZE + 1 || storage.getPduSize() > limit) {
                System.out.println("pduSize: " + storage.getPduSize());
                System.out.println("not enough data");
                return null;
            }

            System.out.println("before loop Buf: " + storage.getInputBuf());

            while (true) {

                storage.flipInputBuf();

                if (storage.getPduSize() == 0) {
                    storage.setPduSize(storage.getPayloadSize() + Const.Header.TOTAL_SIZE);
                }

                if (storage.getPduSize() < Const.Header.TOTAL_SIZE + 1 || storage.getPduSize() > 1024) {
                    System.out.println("pduSize:" + storage.getPduSize());
                    System.out.println("Buf: " + storage.getInputBuf());
                    throw new Error("Logic Error: chat.util.Header?");
                }

                if (storage.getInputBufLimit() < storage.getPduSize()) {
                    storage.compactInputBuf();
                    break;
                }

                storage.readPdu();
                inExchanges.add(ExchangeManager.createInputExchange(storage.getPdu(), key));

                storage.resetPduSize();

                if (storage.getInputBufPosition() < Const.Header.TOTAL_SIZE + 1) {
                    break;
                }
            }

            System.out.println("Buf:" + storage.getInputBuf());
        } catch (Exception e) {
            System.err.println(e);
        }

        return inExchanges;
    }
}
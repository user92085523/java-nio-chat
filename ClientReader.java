package chat.client;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import javax.management.InvalidAttributeValueException;

import chat.event.Reader;
import chat.exchange.InputExchanges;
import chat.exchange.InputTempStorage;
import chat.util.Const;

public final class ClientReader extends Reader {
    private final InputTempStorage storage = new InputTempStorage();

    public int handle(SelectionKey key, InputExchanges inExchanges) {
        try {
            SocketChannel sc = (SocketChannel) key.channel();

            System.out.println("before read Buf: " + storage.getBuf());

            if (sc.read(storage.getBuf()) == -1) {
                //TODO Exception投げて再接続を試行かプログラムの終了
                System.out.println("DCed from Server");
                System.exit(0);
                // SessionManager.disconnect(key);
                return 0;
            }

            int limit = storage.getBufPosition();

            System.out.println("Buf:" + storage.getBuf());

            if (limit < Const.Pdu.MIN_SIZE || storage.getPduSize() > limit) {
                System.out.println("data too small to process");
                return 0;
            }

            while (true) {

                storage.flipBuf();

                if (storage.getPduSize() == 0) {
                    int payloadSize = storage.setPayloadSize();

                    if (payloadSize < Const.Pdu.PAYLOAD_MIN_SIZE || payloadSize > Const.Pdu.PAYLOAD_MAX_SIZE) {
                        //TODO 強制終了か再接続試行

                        // SessionManager.disconnect(key);
                        // System.out.println("DC");
                        throw new InvalidAttributeValueException("Logic Error OR Server sending illegal Header\npduSize :" + payloadSize);
                    }

                    if (storage.setPduSize() > storage.getBufLimit()) {
                        storage.compactBuf();
                        break;
                    }
                }

                inExchanges.set(storage.getBuf(), storage.getPayloadSize(), key);

                storage.resetPduSize();

                if (storage.getBufPosition() < Const.Pdu.MIN_SIZE) {
                    break;
                }
            }

            System.out.println("Buf:" + storage.getBuf());
        } catch (Exception e) {
            //例外発生時、受け取ったすべてのデータは破棄される
            e.printStackTrace();
            //TODO 強制終了か再接続試行

            // System.out.println("DC");
            // SessionManager.disconnect(key);
            inExchanges.reset();
            return 0;
        }

        return inExchanges.size();
    }
}

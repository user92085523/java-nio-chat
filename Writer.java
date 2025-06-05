package chat.event;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import chat.exchange.OutputExchange;
import chat.exchange.OutputExchanges;
import chat.exchange.PendingExchanges;
import chat.session.Session;
import chat.session.SessionManager;

public class Writer {
    public Writer() {
        System.out.println("Writer: " + hashCode());
    }


    //TODO pool作成後の最初だけ receiversKeyがある状態
    public void tryWrite(OutputExchanges outExchanges) {
        try {
            outExchanges.echo();
            for (OutputExchange outExchange : outExchanges.getQueued()) {
                ByteBuffer pdu = outExchange.getPdu();
                int pduSize = outExchange.getPduSize();
                // System.out.println("pduSize:" + pduSize);
                // System.out.println("pdu:" + pdu);
                // System.out.println("queued cnt:" + outExchanges.getQueued().size());

                Set<SelectionKey> receiversKey = outExchange.getReceiversKey();
                Iterator<SelectionKey> iter = receiversKey.iterator();

                System.out.println("receiversKey isEmpty:" + receiversKey.isEmpty());
                while (iter.hasNext()) {
                    SelectionKey receiver = iter.next();
                    Session session = (Session) receiver.attachment();

                    // if (!receiver.isValid()) {
                    //     System.out.println("invalid key");
                    //     continue;
                    // }

                    if (session.isPending()) {
                        //TODO 保持上限の例外
                        System.out.println("isPending");
                        session.getPendingExchanges().add(pdu, pduSize, 0);
                        continue;
                    }


                    SocketChannel sc = (SocketChannel) receiver.channel();
                    int bytesWritten = write(sc, pdu);
                    outExchange.rewind();

                    if (bytesWritten != pduSize) {
                        System.out.println("bytesWritten != pduSize");
                        iter.remove();
                    } else {
                        System.out.println("written but not all");
                        session.getPendingExchanges().add(pdu, pduSize, bytesWritten);
                    }
                }

                // System.out.println("keys:" + outExchange.getReceiversKey());
                // System.out.println("before outExchange move");
                // outExchanges.echo();
                System.out.println("clientsCnt:" + SessionManager.getClientsCnt());
                System.out.println("receiversKey:" + receiversKey);
                if (receiversKey.isEmpty()) {
                    System.out.println("receiversKey is empty");
                    outExchanges.removeQueuedAddToPool();
                } else {
                    System.out.println("receiversKey not empty");
                    outExchanges.removeQueuedAddToPending();
                }
                // System.out.println("after outExchange move");
                // outExchanges.echo();

                // System.out.println("outExchange.receiversKey:" + outExchange.getReceiversKey().size());
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public int write(SocketChannel sc, ByteBuffer pdu) throws Exception{
        return sc.write(pdu);
    }
}

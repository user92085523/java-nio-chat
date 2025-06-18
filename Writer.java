package chat.event;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
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

    public void tryWrite(OutputExchanges outExchanges) {
        try {
            System.out.println("\ntryWrite");
            outExchanges.echo();
            System.out.println("loop\n");
            for (OutputExchange outExchange : outExchanges.getQueued()) {
                ByteBuffer pdu = outExchange.getPdu();
                int pduSize = outExchange.getPduSize();

                Set<SelectionKey> receiversKey = SessionManager.removeDisconnectedClientsFrom(outExchange.getReceiversKey());

                if (!receiversKey.isEmpty()) {
                    Iterator<SelectionKey> iter = receiversKey.iterator();

                    while (iter.hasNext()) {
                        SelectionKey receiver = iter.next();

                        if (!receiver.isValid()) {
                            continue;   
                        }
                        
                        Session session = (Session) receiver.attachment();

                        if (session.isPending()) {
                            System.out.println("isPending");
                            session.getPendingExchanges().add(pdu, pduSize, 0);
                            continue;
                        }

                        SocketChannel sc = (SocketChannel) receiver.channel();
                        int bytesWritten = 0;

                        try {
                            bytesWritten = writeToChannel(sc, pdu);
                        } catch (Exception e) {
                            System.out.println("happy");
                            e.printStackTrace();
                            if (e instanceof ClosedChannelException) {
                                SessionManager.handleClosedChannelException(receiver);
                            } else if (e instanceof IOException) {
                                //TODO 例外の種類について調査
                            }
                        } finally {
                            outExchange.rewind();
                        }

                        //TODO　テスト中　== に戻すこと
                        if (bytesWritten == pduSize) {
                            System.out.println("bytesWritten != pduSize");
                            iter.remove();
                        } else {
                            System.out.println("written but not all");
                            session.getPendingExchanges().add(pdu, pduSize, bytesWritten);
                        }
                    }
                }

                if (receiversKey.isEmpty()) {
                    System.out.println("receiversKey is empty");
                    outExchanges.removeQueuedAddToPool();
                } else {
                    System.out.println("receiversKey not empty");
                    outExchanges.removeQueuedAddToPending();
                }

                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int writeToChannel(SocketChannel sc, ByteBuffer pdu) throws Exception{
        return sc.write(pdu);
    }
}

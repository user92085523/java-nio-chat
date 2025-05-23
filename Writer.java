package chat.event;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

import chat.exchange.Exchange;
import chat.session.Session;

public class Writer {
    public Writer() {
        System.out.println("Writer: " + hashCode());
    }

    public void writeNow(List<Exchange> outExchanges) {
        for (Exchange exchange : outExchanges) {
            // System.out.println("writeNow");
            // exchange.ReceiverKeys.forEach(key -> System.out.println(key));
            for (SelectionKey receiverKey : exchange.ReceiversKey) {

                try {
                    SocketChannel sc = (SocketChannel) receiverKey.channel();
                    // System.out.println("Pdu: " + exchange.Pdu);
                    int pduSize = exchange.Pdu.limit();
                    int bytesWrite = sc.write(exchange.Pdu);

                    // System.out.println("Pdu: " + exchange.Pdu);
                    Session session = (Session) receiverKey.attachment();
                    // System.out.println("receiver.Id: " + session.getClientID().getId());
                    if (pduSize != bytesWrite) {
                        // System.out.println("FAIL");
                        // System.out.println("pduSize: " + pduSize);
                        // System.out.println("bytesWrite: " + bytesWrite);
                        // System.out.println("pduSize != bytesWrite");
                    } else {
                        // System.out.println("OK");
                    }
                    // System.out.println("-----------------");
                    exchange.Pdu.rewind();
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }
        System.out.println("writed");
        System.out.println("exchanges: " + outExchanges.size());
    }
}

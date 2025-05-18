package chat.server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import chat.session.ClientID;
import chat.session.Session;
import chat.session.SessionManager;
import chat.util.Const;
import chat.event.Reader;
import chat.event.Writer;
import chat.exchange.Exchange;
import chat.exchange.ExchangeManager;

public class ServerManager {
    private final SessionManager sessionManager = new SessionManager();
    private final ExchangeManager exchangeManager = new ExchangeManager();
    private final Reader reader = new Reader();
    private final Writer writer = new Writer();

    private Selector selector;

    public ServerManager() {
        System.out.println("ServerManager: " + hashCode());
        try {
            selector = Selector.open();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void run() {
        try {

            while (true) {

                selector.keys().forEach(key -> System.out.println(key.toString()));

                selector.select();

                long start = System.currentTimeMillis();

                System.out.println("clients: " + sessionManager.getClients().size());

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                System.out.println("selectedKeysCnt: " + selectedKeys.size());
    
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
    
                    if (key.isAcceptable()) {
                        sessionManager.accept(key);
                    }

                    if (key.isReadable()) {
                        List<Exchange> inExchanges = new ArrayList<>();

                        inExchanges = reader.handle(sessionManager, exchangeManager, key);

                        if (inExchanges == null) {
                            System.out.println("NO EXCHANGES");
                            continue;
                        }

                        if (inExchanges != null) {
                            System.out.println("inputExchanges size: " + inExchanges.size());
                            System.out.println("exchangeID: " + inExchanges.getLast().Id);


                            Set<SelectionKey> clientKeys = new HashSet<>();
                            for (ClientID clientID : sessionManager.getClients()) {
                                clientKeys.add(clientID.getKey());
                            }

                            List<Exchange> outExchange = new ArrayList<>();
                            for (Exchange inExchange : inExchanges) {
                                outExchange.add(exchangeManager.createOutputExchange(inExchange.Pdu, inExchange.SenderKey, clientKeys));
                            }

                            writer.writeNow(outExchange);
                        }

                    }
                }

                System.out.println("time: " + (System.currentTimeMillis() - start) + " ms");
                Thread.sleep(1000);
                System.out.println("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void init() {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            ServerSocket ss = ssc.socket();
            ss.bind(new InetSocketAddress(Const.Server.PORT));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void test(SelectionKey key) {
        // System.out.println("test");
        try {
            SocketChannel sc = (SocketChannel) key.channel();
            byte[] buf = new byte[] {0, 0, 9, 1, 1, -128, -64, 15, 'r', 'c', 'v', 'd', '2', 'm', 's', 'g', 0x04};
            int bytes_written = sc.write(ByteBuffer.wrap(buf));
            // System.out.println("bytes_written: " + bytes_written);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}

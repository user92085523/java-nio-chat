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
import chat.exchange.InputExchanges;
import chat.exchange.ServerExchangeProcessor;

public class ServerManager {
    private final ExchangeManager exchangeManager = new ExchangeManager(new ServerExchangeProcessor());
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


                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                System.out.println("selectedKeysCnt: " + selectedKeys.size());
    
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
    
                    if (key.isAcceptable()) {
                        SessionManager.accept(key);
                    }

                    if (key.isReadable()) {
                        
                        InputExchanges inputExchanges = reader.handle(key);

                        if (inputExchanges != null) {
                            inputExchanges.echo();


                            inputExchanges.reset();
                            System.out.println("reset");
                            inputExchanges.echo();
                        } else {
                            System.out.println("inputExchanges == NULL");
                        }
                        // if (inExchanges == null) {
                        //     System.out.println("NO EXCHANGES");
                        //     continue;
                        // }

                        // if (inExchanges.size() > 0) {
                        //     System.out.println("inputExchanges size: " + inExchanges.size());

                        //     List<Exchange> outExchanges = exchangeManager.process(inExchanges);

                        //     if (outExchanges != null) {
                        //         writer.writeNow(outExchanges);
                        //     }
                        // }

                        // inExchanges.clear();

                    }
                }

                System.out.println("time: " + (System.currentTimeMillis() - start) + " ms");
                Thread.sleep(1000);
                System.out.println("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.err.println(e);
            throw new Error("ServerManager");
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
            byte[] buf = new byte[] {0, 0, 9, 1, 'r', 'c', 'v', 'd', '2', 'm', 's', 'g', 0x04};
            int bytes_written = sc.write(ByteBuffer.wrap(buf));
            // System.out.println("bytes_written: " + bytes_written);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}

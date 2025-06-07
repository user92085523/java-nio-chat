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

import chat.session.ClientInfo;
import chat.session.Session;
import chat.session.SessionManager;
import chat.util.Const;
import chat.event.Processor;
import chat.event.Reader;
import chat.event.Writer;
import chat.exchange.InputExchanges;
import chat.exchange.OutputExchanges;
import chat.server.ServerProcessor;
import chat.server.ServerReader;

public class ServerManager {
    private final Processor processor = new ServerProcessor();
    private final Reader reader = new ServerReader();
    private final Writer writer = new Writer();
    public final InputExchanges inExchanges = new InputExchanges();
    public final OutputExchanges outExchanges = new OutputExchanges();

    private Selector selector;

    public ServerManager() {
        System.out.println("ServerManager: " + hashCode());
        try {
            selector = Selector.open();
        } catch (Exception e) {
            e.printStackTrace();
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

                    if (!key.isValid()) {
                        continue;
                    }
    
                    if (key.isAcceptable()) {
                        SessionManager.accept(key);
                    }

                    if (key.isReadable()) {
                        
                        int inputCnt = reader.handle(key, inExchanges);

                        if (inputCnt > 0) {

                            processor.handle(inExchanges, outExchanges);
                            // inExchanges.echo();
                            inExchanges.reset();
                            // System.out.println("reset");

                            // outExchanges.echo();
                            // System.out.println("queued:" + outExchanges.getQueued().size());

                            if (outExchanges.hasQueued()) {
                                writer.tryWrite(outExchanges);
                                outExchanges.echo();
                            }
                            // outExchanges.run();
                        }

                    }

                    // Session session = (Session) key.attachment();

                    // if (session == null) continue;

                    // System.out.println("session has pending:" + session.getPendingExchanges().getPendingCnt());
                }

                SessionManager.clearDisconnectedClients();

                System.out.println("time: " + (System.currentTimeMillis() - start) + " ms");
                Thread.sleep(1000);
                System.out.println("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}

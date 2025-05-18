package chat.client;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import chat.event.Reader;
import chat.exchange.Exchange;
import chat.exchange.ExchangeManager;
import chat.session.Session;
import chat.session.SessionManager;
import chat.util.Const;

public class ClientManager {
    private final ExchangeManager exchangeManager = new ExchangeManager();
    private final SessionManager sessionManager = new SessionManager();
    private final Reader reader = new Reader();
    private SelectionKey key;
    private boolean input_ready;
    private ByteBuffer inputBuf = ByteBuffer.allocateDirect(1024 * 1024 * 10);

    public ClientManager() {
        System.out.println("ClientManager: " + hashCode());
    }

    public void run() {
        GetInput gi = new GetInput();
        gi.start();

        Selector selector = key.selector();
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            while (true) {
                int actions = selector.select(10);

                if (input_ready) {
                    int limit = inputBuf.limit();
                    inputBuf.flip();
                    System.out.println("inb flip:" + inputBuf);
                    sc.write(inputBuf);
                    System.out.println("inb write:" + inputBuf);
                    inputBuf.compact();
                    input_ready = false;
                }

                if (actions != 0) {
                    if (key.isReadable()) {
                        List<Exchange> inExchanges = reader.handle(sessionManager, exchangeManager, key);

                        if (inExchanges != null) {
                            for (Exchange exchange : inExchanges) {
                                System.out.println(exchange.pduToString());
                            }
                            System.out.println("inExchanges size: " + inExchanges.size());
                            System.out.println("exchangeID:" + inExchanges.getLast().Id);
                        }
                    }

                }

                selector.selectedKeys().clear();
                Thread.sleep(10);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void init() {
        System.out.println("ClientManager.init()");
        try {
            SocketChannel sc = SocketChannel.open();
            Selector selector = Selector.open();
            sc.configureBlocking(false);
            key = sc.register(selector, SelectionKey.OP_CONNECT, new Session());
        } catch (Exception e) {
            System.err.println(e);
        }
        System.out.println("#ClientManager.init");
    }

    public void connect() {
        System.out.println("ClientManager.connect()");
        try {
            SocketChannel sc = (SocketChannel) key.channel();
            sc.connect(new InetSocketAddress(Const.Server.PORT));

            while (!sc.finishConnect()) {
                System.out.println("waiting...");
            }

            key.interestOps(SelectionKey.OP_READ);
        } catch (Exception e) {
            System.err.println(e);
        }
        System.out.println("#ClientManager.connect");
    }

    private class GetInput extends Thread {
        public void run() {
            Scanner stdin = new Scanner(System.in);
            String input;
            int cnt = 0;

            byte[] str = {0, 0, 5, -126, -127, -1, 1, 1, 97, 98, 99, 100, 101};

            try {
                while (true) {
                    if(input_ready == true) {
                        Thread.sleep(10);
                    } else {
                        Thread.sleep(10);
                        // System.out.println("inb:" + inputBuf);
                        Random rand = new Random();
                        int rnd = rand.nextInt(str.length * 30);

                        for (int i = 0; i < rnd; i++) {
                            inputBuf.put(str[cnt]);
                            cnt++;
                            if (cnt == str.length) cnt = 0;
                        }

                        System.out.println("inputBuf:" + inputBuf);

                        if (rnd == str.length) {
                            input_ready = true;
                            Thread.sleep(10);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println(e);
                System.exit(99);
            }
        }
    }
}

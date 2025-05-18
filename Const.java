package chat.util;

public class Const {
    public static class Server {
        public static final int PORT = 8080;
        public static final int MAX_CLIENT = 1000;
    }

    public class Header {
        public static final int TOTAL_SIZE = 8;
        public static final int H1_SIZE = 3;
        public static final int H2_SIZE = 1;
        public static final int H3_SIZE = 4;
    }

    public class Body {
        public static final int SIZE = 1016;
    }

    public class Buffer {
        public static final int SESSION_BUFFER_SIZE = 1024 * 2;
        public static final int CLIENT_BUFFER_SIZE = 1024 * 10;
    }
}

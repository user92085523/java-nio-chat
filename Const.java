package chat.util;

public class Const {
    public static class Server {
        public static final int PORT = 8080;
        public static final int MAX_CLIENT = 1000;
    }

    public class Pdu {
        public static final int MAX_SIZE = 1024;
        public static final int HEADER_TOTAL_SIZE = 4;
        public static final int H1_SIZE = 3;
        public static final int H2_SIZE = 1;
        public static final int PAYLOAD_MAX_SIZE = MAX_SIZE - HEADER_TOTAL_SIZE;
        public static final int PAYLOAD_MIN_SIZE = 1;
        public static final int MIN_SIZE = HEADER_TOTAL_SIZE + PAYLOAD_MIN_SIZE;
    }

    public class Exchange {
        public static final int MAX_INPUT_EXCHANGE_SIZE = 30;
        public static final int MAX_OUTPUT_EXCHANGE_SIZE = 50;
    }

    public class Buffer {
        public static final int SESSION_BUFFER_SIZE = 1024 * 2;
        public static final int CLIENT_BUFFER_SIZE = 1024 * 10;
    }
}

package chat.exchange.enumerate;

public enum CommandToClient {

    IS_ALIVE((byte) 1),

    DISPLAY_MSG_ALL((byte) 10),
    DISPLAY_MSG_GROUP((byte) 11),
    DISPLAY_MSG_TO((byte) 12);


    private final byte CommandToClient;

    CommandToClient(byte b) {
        this.CommandToClient = b;
    }

    public static CommandToClient resolve(byte b) {
        switch (b) {
            case 1:
                return IS_ALIVE;
            case 10:
                return DISPLAY_MSG_ALL;
            case 11:
                return DISPLAY_MSG_GROUP;
            case 12:
                return DISPLAY_MSG_TO;
            default:
                throw new Error("Invalid CommandToClient maybe dc this client");
        }
    }
}

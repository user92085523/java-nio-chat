package chat.exchange.enumerate;

public enum Command {

    //ToServer
    IM_ALIVE((byte) 0),

    MSG_ALL((byte) 10),
    MSG_GROUP((byte) 11),
    MSG_TO((byte) 12),

    //ToClient
    IS_ALIVE((byte) -1),

    DISPLAY_MSG_ALL((byte) -11),
    DISPLAY_MSG_GROUP((byte) -12),
    DISPLAY_MSG_TO((byte) -13);

    private final byte Command;

    Command(byte b) {
        this.Command = b;
    }

    public static byte desolve(Command cmd) {
        switch (cmd) {
            case IM_ALIVE:
                return 0;
            case MSG_ALL:
                return 10;
            case MSG_GROUP:
                return 11;
            case MSG_TO:
                return 12;
            
            case IS_ALIVE:
                return -1;
            case DISPLAY_MSG_ALL:
                return -11;
            case DISPLAY_MSG_GROUP:
                return -12;
            case DISPLAY_MSG_TO:
                return -13;
            default:
                throw new Error("Invalid Command maybe dc this client");
        }
    }

    public static Command resolve(byte b) {
        switch (b) {
            case 0:
                return IM_ALIVE;
            case 10:
                return MSG_ALL;
            case 11:
                return MSG_GROUP;
            case 12:
                return MSG_TO;

            case -1:
                return IS_ALIVE;
            case -11:
                return DISPLAY_MSG_ALL;
            case -12:
                return DISPLAY_MSG_GROUP;
            case -13:
                return DISPLAY_MSG_TO;
            default:
                throw new Error("Invalid Command maybe dc this client");
        }
    }
}

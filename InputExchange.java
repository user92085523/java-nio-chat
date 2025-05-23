import java.nio.ByteBuffer;

public class InputExchange {
    public InputExchange(){}

    private ByteBuffer h1 = ByteBuffer.allocateDirect(3);
    private ByteBuffer h2 = ByteBuffer.allocateDirect(1);
    private ByteBuffer payload = ByteBuffer.allocateDirect(1020);
}

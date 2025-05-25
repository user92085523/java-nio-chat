package chat.exchange;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import chat.exchange.InputExchange;
import chat.session.SessionManager;
import chat.util.Const;

public class InputExchanges {
    private InputExchange[] inputExchanges = new InputExchange[Const.Exchange.MAX_INPUT_EXCHANGE_SIZE];
    private int inputCnt = 0;

    public InputExchanges(){
        System.out.println("InputExchanges: " + hashCode());

        for (int i = 0, len = inputExchanges.length; i < len; i++) {
            inputExchanges[i] = new InputExchange();
        }
    }

    public int readInto(ByteBuffer tempBuffer, int payloadSize) throws Exception{
        if (inputCnt >= Const.Exchange.MAX_INPUT_EXCHANGE_SIZE) {
            throw new Exception("Client sending too many exchange in short period");
        }

        inputExchanges[inputCnt++].readInput(tempBuffer, payloadSize);

        return inputCnt;
    }

    public int getInputCnt() {
        return inputCnt;
    }

    public void reset() {
        for (int i = 0, len = inputCnt; i < len; i++) {
            inputExchanges[i].reset();
        }
        inputCnt = 0;
    }

    public void echo() {
        System.out.println("inputCnt: " + inputCnt);
    }
}

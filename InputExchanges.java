package chat.exchange;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;

import chat.exchange.InputExchange;
import chat.session.SessionManager;
import chat.util.Const;

public class InputExchanges {
    private final InputExchange[] inputExchanges = new InputExchange[Const.Exchange.MAX_INPUT_EXCHANGE_SIZE];
    private int inputCnt = 0;

    public InputExchanges(){
        System.out.println("InputExchanges: " + hashCode());

        long start = System.currentTimeMillis();

        for (int i = 0, len = inputExchanges.length; i < len; i++) {
            inputExchanges[i] = new InputExchange();
        }

        System.out.println("InputExchanges time: " + (System.currentTimeMillis() - start));
    }

    public boolean hasInputs() {
        return inputCnt > 0 ? true : false;
    }

    public int set(ByteBuffer tempBuffer, int payloadSize, SelectionKey senderKey) throws Exception{
        if (inputCnt >= Const.Exchange.MAX_INPUT_EXCHANGE_SIZE) {
            throw new Exception("Client sending too many exchange in short period");
        }

        inputExchanges[inputCnt++].set(tempBuffer, payloadSize, senderKey);

        return inputCnt;
    }

    public InputExchange getInput(int idx) {
        return inputExchanges[idx];
    }

    public int getInputCnt() {
        return inputCnt;
    }

    public void reset() {
        System.out.println("InputExchange Cnt:" + inputCnt);
        for (int i = 0, len = inputCnt; i < len; i++) {
            inputExchanges[i].reset();
        }
        inputCnt = 0;
    }

    public void echo() {
        System.out.println("inputCnt: " + inputCnt);
    }
}

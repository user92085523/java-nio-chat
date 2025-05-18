package chat.util;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Random;

public class Header {
    private static final int HEADER_TOTAL_SIZE = 8;
    private static final int HEADER_DSIZE_SIZE = 3;
    private static final int MAX_DSIZE = 1024;

    private static final int pow2[] = new int[HEADER_DSIZE_SIZE * 8];
    static{
        int max = (int) Math.pow(2, pow2.length - 1);
        for (int i = 0; i < pow2.length; i++) {
            pow2[i] = max;
            max >>>= 1;
        }
    }

    private static boolean validateSize(int bytes) {
        return (bytes + HEADER_TOTAL_SIZE <= MAX_DSIZE) ? true : false;
    }

    public static byte[] getPayloadSize(int size) {
        byte[] header_dsize = new byte[3];

        for (int i = 0; i < 3; i++) {
            int mask = 0xff << 8 * (2 - i);
            header_dsize[i] = (byte) ((size & mask) >>> 8 * (2 - i));
        }

        return header_dsize;
    }

    public static int getPayloadSize(byte[] bin) {
        int acm = 0;

        for (int i = 0; i < bin.length; i++) {
            int num = (int) bin[i];
            for (int j = 0; j < 8; j++) {
                acm += (num & 1) == 1 ? pow2[(i * 8) + (7 - j)] : 0;
                num >>>= 1;
            }
        }

        return acm;
    }

    public static byte[] createHeader(String input) {
        byte[] b_input = input.getBytes();
        int b_input_cnt = b_input.length;

        if (! validateSize(b_input_cnt)) return null;

        return getPayloadSize(b_input_cnt);
    }

    public static void main(String[] args) {
        System.out.println("hi");
        Random rnd = new Random();
        for (int i = 0; i < 100000000; i++) {
            int rndint = rnd.nextInt(2000);
            int result = getPayloadSize(getPayloadSize(rndint));
            if (rndint != result) {
                System.out.println("ERR");
            }
        }
    }
}
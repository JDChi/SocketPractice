package util;

/**
 * @Created by jdchi
 * @Date 2019/5/22
 * @Description
 **/
public class Tools {

    public static int byteArrayToInt(byte[] b){
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    /**
     * 一个 int 是 32 位
     * @param a
     * @return
     */
    public static byte[] intToByteArray(int a){
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }
}

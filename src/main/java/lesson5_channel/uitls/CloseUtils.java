package lesson5_channel.uitls;

import java.io.Closeable;
import java.io.IOException;

/**
 * @Created by jdchi
 * @Date 2019/5/27
 * @Description
 **/
public class CloseUtils {

    public static void close(Closeable...closeables){
        if (closeables == null) {
            return;
        }

        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

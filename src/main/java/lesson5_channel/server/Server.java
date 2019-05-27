package lesson5_channel.server;

import lesson5_channel.constants.TCPConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Created by jdchi
 * @Date 2019/5/22
 * @Description
 **/
public class Server {

    public static void main(String[] args) {
        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER);
        boolean isSucceed = tcpServer.start();
        if (!isSucceed) {
            System.out.println("Start TCP server failed");
            return;
        }

        UDPProvider.start(TCPConstants.PORT_SERVER);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String str = null;

        do{
            try {
                str = bufferedReader.readLine();
                tcpServer.broadcast(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }while (!"00bye00".equalsIgnoreCase(str));

        UDPProvider.stop();
        tcpServer.stop();

    }
}

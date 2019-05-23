package lesson5.client;

import lesson5.client.bean.ServerInfo;

import java.io.IOException;

/**
 * @Created by jdchi
 * @Date 2019/5/23
 * @Description
 **/
public class Client {

    public static void main(String[] args) {
        try {
            ServerInfo serverInfo = UDPSearcher.searchServer(1000);
            System.out.println("Server: " + serverInfo);

            if (serverInfo != null) {
                TCPClient.linkWith(serverInfo);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

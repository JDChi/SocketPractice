package lesson5_channel.server;

import lesson5_channel.server.handle.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @Created by jdchi
 * @Date 2019/5/22
 * @Description
 **/
public class TCPServer {

    private final int port;
    private ClientListener clientListener;
    private List<ClientHandler> clientHandlerList = new ArrayList<>();

    public TCPServer(int port) {
        this.port = port;
    }

    public boolean start() {

        try {
            ClientListener listener = new ClientListener(port);
            this.clientListener = listener;
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public void stop() {
        if (this.clientListener != null) {
            this.clientListener.exit();
        }

        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.exit();
        }
        clientHandlerList.clear();
    }

    public void broadcast(String str) {

        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(str);
        }

    }

    private class ClientListener extends Thread {

        private ServerSocket serverSocket;
        private boolean done = false;

        private ClientListener(int port) throws IOException {
            serverSocket = new ServerSocket(port);
            System.out.println("服务器信息：" + serverSocket.getInetAddress() + "port: " + serverSocket.getLocalPort());
        }

        @Override
        public void run() {
            super.run();

            System.out.println("服务器准备就绪~");
            do {
                Socket client;
                try {
                    client = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                try {
                    ClientHandler clientHandler = new ClientHandler(client, new ClientHandler.CloseNotify() {
                        @Override
                        public void onSelfClosed(ClientHandler clientHandler) {
                            clientHandlerList.remove(clientHandler);
                        }
                    });
                    //读取数据并打印
                    clientHandler.readToPrint();
                    clientHandlerList.add(clientHandler);

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("客户端连接异常：" + e.getMessage());
                }


            } while (!done);
        }

        void exit() {
            done = true;

            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

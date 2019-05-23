package lesson5.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Handler;

/**
 * @Created by jdchi
 * @Date 2019/5/22
 * @Description
 **/
public class TCPServer {

    private final int port;
    private ClientListener clientListener;

    public TCPServer(int port){
        this.port = port;
    }

    public boolean start(){

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


    public void stop(){
        if (this.clientListener != null) {
            this.clientListener.exit();
        }
    }

    private static class ClientListener extends Thread{

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
            do{
                Socket client;
                try {
                    client = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                ClientHander clientHander = new ClientHander(client);
                clientHander.start();
            }while (!done);
        }

        void exit(){
            done = true;

            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientHander extends Thread{

        private Socket socket;
        private boolean flag = true;

        ClientHander(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接：" + socket.getInetAddress() + "port: " + socket.getPort());

            try {
                PrintStream printStream = new PrintStream(socket.getOutputStream());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                do{
                    String str = bufferedReader.readLine();
                    if("bye".equalsIgnoreCase(str)){
                        flag = false;
                        printStream.println("bye");
                    }else {
                        System.out.println(str);
                        printStream.println("回送：" +  str.length());
                    }
                }while (flag);

                printStream.close();
                bufferedReader.close();



            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("连接异常断开");
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("客户端已退出：" + socket.getInetAddress() + "port : " + socket.getPort());
        }


    }
}

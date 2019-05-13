package lesson1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Created by jdchi
 * @Date 2019/5/10
 * @Description
 **/
public class Server {

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(2000);

            System.out.println("服务器准备就绪");
            System.out.println("服务器信息 :" + serverSocket.getInetAddress() + " port : " + serverSocket.getLocalPort());



            for(;;){
                //等待客户端连接
                Socket socket = serverSocket.accept();
                //构建异步线程
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandler.start();


            }





        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread{
        private Socket socket;
        private boolean flag = true;

        public ClientHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接 " + socket.getInetAddress() + " port : " + socket.getPort());

            try {
                //得到打印流，用于数据输出，服务器回送数据使用
                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                //得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                do{
                    //客户端拿到一条数据
                    String str = socketInput.readLine();

                    if("bye".equalsIgnoreCase(str)){
                        flag = false;
                        socketOutput.println("bye");
                    }else {
                        System.out.println(str);
                        socketOutput.println("回送：" + str.length());
                    }
                }while (flag);

                socketInput.close();
                socketOutput.close();

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

            System.out.println("客户端已关闭" + socket.getInetAddress() + " port : " + socket.getPort());
        }
    }
}

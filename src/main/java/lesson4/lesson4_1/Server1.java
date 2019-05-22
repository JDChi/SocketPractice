package lesson4.lesson4_1;

import util.Tools;

import javax.tools.Tool;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * @Created by jdchi
 * @Date 2019/5/17
 * @Description
 * 1. 初始化服务器 TCP 链接监听
 * 2. 初始化客户端发起链接操作
 * 3. 服务器 Socket 链接处理
 **/
public class Server1 {

    private static final int PORT = 20000;

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = createServerSocket();

        initServerSocket(serverSocket);

        //绑定到本地端口上
        serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost() , PORT ) , 50);

        System.out.println("服务器准备就绪~");
        System.out.println("服务器信息： " + serverSocket.getInetAddress() + "port: " + serverSocket.getLocalPort());

        for(;;){
            Socket client = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(client);
            clientHandler.start();


        }

    }

    private static void initServerSocket(ServerSocket serverSocket) throws SocketException {

        //是否复用未完全关闭的地址端口
        serverSocket.setReuseAddress(true);

        serverSocket.setReceiveBufferSize(64 * 1024 * 1024);
//        serverSocket.setSoTimeout(2000);

        serverSocket.setPerformancePreferences(1 , 1, 1);


    }

    private static ServerSocket createServerSocket() throws IOException {

        ServerSocket serverSocket = new ServerSocket();



//        serverSocket = new ServerSocket(PORT , 50);
//        serverSocket = new ServerSocket(PORT , 50 , Inet4Address.getLocalHost());
        return serverSocket;
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

//            try {
//                //得到打印流，用于数据输出，服务器回送数据使用
//                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
//                //得到输入流，用于接收数据
//                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//                do{
//                    //客户端拿到一条数据
//                    String str = socketInput.readLine();
//
//                    if("bye".equalsIgnoreCase(str)){
//                        flag = false;
//                        socketOutput.println("bye");
//                    }else {
//                        System.out.println(str);
//                        socketOutput.println("回送：" + str.length());
//                    }
//                }while (flag);
//
//                socketInput.close();
//                socketOutput.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("连接异常断开");
//            }finally {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

            try {
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();

                byte[] buffer = new byte[256];

                int readCount = inputStream.read(buffer);

                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer , 0 , readCount);

                byte be = byteBuffer.get();

                char c = byteBuffer.getChar();

                int i = byteBuffer.getInt();

                boolean b = byteBuffer.get() == 1;

                long l = byteBuffer.getLong();

                float f = byteBuffer.getFloat();

                double d = byteBuffer.getDouble();

                int pos = byteBuffer.position();

                String str = new String(buffer , pos , readCount - pos - 1);

                System.out.println("收到数量：" + readCount + "数据："
                + be + "\n" + c + "\n" + i + "\n" + b + "\n" + l + "\n" + f + "\n" + d + "\n" + str + "\n");

                if (readCount > 0) {

//                    int value = Tools.byteArrayToInt(buffer);
//                    System.out.println("收到数量：" + readCount + " 数据：" + value);

                    outputStream.write(buffer , 0 , readCount);
                }else {
                    System.out.println("没有收到 " + readCount);
                    outputStream.write(new byte[]{0});
                }

                outputStream.close();
                inputStream.close();


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

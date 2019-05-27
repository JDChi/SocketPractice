package lesson5_channel.client;

import lesson5_channel.client.bean.ServerInfo;
import lesson5_channel.server.handle.ClientHandler;
import lesson5_channel.uitls.CloseUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * @Created by jdchi
 * @Date 2019/5/22
 * @Description
 **/
public class TCPClient {

    public static void linkWith(ServerInfo serverInfo) throws IOException {

        Socket socket = new Socket();

        socket.setSoTimeout(3000);
        socket.connect(new InetSocketAddress(InetAddress.getByName(serverInfo.getAddress()), serverInfo.getPort()), 3000);

        System.out.println("已发起服务器连接，并进入后续流程~");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P: " + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + "P: " + socket.getPort());


        ReadHandler readHandler = new ReadHandler(socket.getInputStream());
        readHandler.start();
        write(socket);

        readHandler.exit();

        socket.close();
        System.out.println("客户端已退出");

    }

    private static void write(Socket client) throws IOException {

        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        OutputStream outputStream = client.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);


        do {
            String str = input.readLine();
            printStream.println(str);

            if ("00bye00".equalsIgnoreCase(str)) {
                break;
            }
        } while (true);

        printStream.close();


    }

    static class ReadHandler extends Thread {


        private boolean done = false;
        private final InputStream inputStream;

        ReadHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }


        @Override
        public void run() {
            super.run();

            try {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                do {
                    String str;

                    try {
                        str = bufferedReader.readLine();
                    } catch (SocketTimeoutException e) {
                        continue;
                    }

                    if (str == null) {
                        System.out.println("客户端无法读取数据");
                        break;
                    }


                    System.out.println(str);

                } while (!done);

            } catch (IOException e) {
                if (!done)
                    System.out.println("连接异常断开");
            } finally {
                CloseUtils.close(inputStream);
            }
        }

        void exit() {
            done = true;
            CloseUtils.close(inputStream);

        }

    }

}

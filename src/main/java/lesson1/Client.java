package lesson1;

import java.io.*;
import java.net.*;

/**
 * @Created by jdchi
 * @Date 2019/5/10
 * @Description
 **/
public class Client {

    public static void main(String[] args) {

        Socket socket = new Socket();

        try {
            socket.setSoTimeout(3000);
            socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 2000), 3000);

            System.out.println("以发起服务器连接");
            System.out.println("客户端信息 :" + socket.getLocalAddress() + " port : " + socket.getLocalPort());
            System.out.println("服务端信息 :" + socket.getInetAddress() + " port : " + socket.getPort());


            todo(socket);


            socket.close();
            System.out.println("客户端已退出");


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void todo(Socket client) throws IOException {
        //构建键盘输入流
        InputStream keyboardInput = System.in;
        BufferedReader keyboardInputReader = new BufferedReader(new InputStreamReader(keyboardInput));

        //得到 Socket 输出流，并转换为打印流
        OutputStream outputStream = client.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        //得到 socket输入流，并转换为 bufferedreader
        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferReader = new BufferedReader(new InputStreamReader(inputStream));
        boolean flag = true;
        do {


            //键盘读取一行
            String str = keyboardInputReader.readLine();
            //发送到服务器
            printStream.println(str);

            //从服务器读取一行
            String echo = socketBufferReader.readLine();
            if ("bye".equalsIgnoreCase(echo)) {
                flag = false;
            } else {
                System.out.println(echo);
            }
        } while (flag);

        printStream.close();
        socketBufferReader.close();

    }
}

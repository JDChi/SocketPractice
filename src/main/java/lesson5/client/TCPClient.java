package lesson5.client;

import lesson5.client.bean.ServerInfo;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @Created by jdchi
 * @Date 2019/5/22
 * @Description
 **/
public class TCPClient {

    public static void linkWith(ServerInfo serverInfo) throws IOException {

        Socket socket = new Socket();

        socket.setSoTimeout(3000);
        socket.connect(new InetSocketAddress(InetAddress.getByName(serverInfo.getAddress()) , serverInfo.getPort()) , 3000);

        System.out.println("已发起服务器连接，并进入后续流程~");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P: " + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + "P: " + socket.getPort());


        todo(socket);

        socket.close();
        System.out.println("客户端已退出");

    }

    private static void todo(Socket client) throws IOException {

        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        OutputStream outputStream = client.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferReader = new BufferedReader(new InputStreamReader(inputStream));

        boolean flag = true;

        do{
            String str = input.readLine();
            if("bye".equalsIgnoreCase(str)){
                flag = false;
            }else {
                System.out.println(str);
            }
        }while (flag);

        printStream.close();
        socketBufferReader.close();


    }
}

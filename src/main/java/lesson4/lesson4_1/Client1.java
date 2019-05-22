package lesson4.lesson4_1;

import java.io.*;
import java.net.*;

/**
 * @Created by jdchi
 * @Date 2019/5/17
 * @Description
 * 1. 客户端发送简单字节
 * 2. 服务器接收客户端发送数据
 * 3. 服务器会送消息，客户端识别回送消息
 **/
public class Client1 {

    private static final int PORT = 20000;
    private static final int LOCAL_PORT = 20001;

    public static void main(String[] args) throws IOException {

        Socket socket = createSocket();

        initSocket(socket);

        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost() , PORT) , 3000);

        System.out.println("已发起服务器连接，并进入后续流程~");
        System.out.println("客户端信息: " + socket.getLocalAddress() + "port : " + socket.getLocalPort());
        System.out.println("服务端信息: " + socket.getInetAddress() + "port : " + socket.getPort());

        try {
            todo(socket);
        }catch (Exception e){
            System.out.println("异常关闭");
        }

        socket.close();
        System.out.println("客户端已退出");


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

    private static void initSocket(Socket socket) throws SocketException {

        //设置读取超时时间
        socket.setSoTimeout(3000);
        //是否复用未完全关闭的 Socket 地址，对于指定 bind 操作后的套接字有效
        socket.setReuseAddress(true);
        //是否开启 Nagle 算法 用于减少必须发送包的个数来增加网络软件系统的效率
        socket.setTcpNoDelay(true);
        //是否需要在长时无数据响应时发送确认数据（类似心跳包），时间大约为 2 小时
        socket.setKeepAlive(true);
        //对于 close 关闭操作行为进行怎样的处理，默认为 false ， 0
        //false , 0 : 默认情况，关闭时立即返回，底层系统接管输出流，将缓冲区内的数据发送完成
        //true , 0 : 关闭时立即返回，缓冲区数据抛弃，直接发送 RST 结束命令到对方，并无需经过 2ms 等待
        //true , 20 : 关闭时最长阻塞 20ms，随后按第二种情况处理
        socket.setSoLinger(true , 20);

        //是否让紧急数据内敛，默认为 false；紧急数据通过 socket.sendUrgentData(1) 发送
        socket.setOOBInline(true);

        // 设置发送缓冲区大小
        socket.setReceiveBufferSize(64 * 1024 * 1024);

        socket.setSendBufferSize(64 * 1024 * 1024);

        //设置性能参数，数值是权重
        socket.setPerformancePreferences(1 , 1 , 1);



    }

    private static Socket createSocket() throws IOException {

//        //无代理模式，等于空构造函数
//        Socket socket = new Socket(Proxy.NO_PROXY);
//
//        //新建一份具有 HTTP 代理的套接字，传输数据将通过 www.baidu.com:8800 端口转发
//        Proxy proxy = new Proxy(Proxy.Type.HTTP , new InetSocketAddress(Inet4Address.getByName("www.baidu.com") , 8800));
//
//        socket = new Socket(proxy);
//
//        // 新建一个套接字，并且直连到本地服务器上
//        socket = new Socket("localhost" , PORT);
//
//        //同上
//        socket = new Socket(Inet4Address.getLocalHost() , PORT);
//
//        //新建一个套接字，并且直接连接到本地 20000 的服务器上，并且绑定到本地 20001 端口上
//        socket = new Socket("localhost" , PORT , Inet4Address.getLocalHost() , LOCAL_PORT);
//
//        socket = new Socket(Inet4Address.getLocalHost() , PORT , Inet4Address.getLocalHost() , LOCAL_PORT);

        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(Inet4Address.getLocalHost() , LOCAL_PORT));
        return socket;
    }


}

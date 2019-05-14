package lesson2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @Created by jdchi
 * @Date 2019/5/13
 * @Description 不需要连接操作，只要监听和发送即可
 **/
public class UDPSearcher {

    public static void main(String[] args) {

        System.out.println("UDPProvider Started");

        try {

            //作为接收者，指定一个端口用于数据接收
            DatagramSocket datagramSocket = new DatagramSocket();

            //构建一份请求数据
            String requestData = "Hello World";
            byte[] requestDataBytes = requestData.getBytes();

            DatagramPacket requestPacket = new DatagramPacket(requestDataBytes , requestDataBytes.length);
            requestPacket.setAddress(InetAddress.getLocalHost());
            requestPacket.setPort(20000);
            datagramSocket.send(requestPacket);


            //构建接收实体
            final byte[] buf = new byte[512];
            DatagramPacket receivePacket = new DatagramPacket(buf , buf.length);

            //接收
            datagramSocket.receive(receivePacket);

            // 打印接收到的信息与发送者的信息
            // 发送者的 ip 地址
            String ip = receivePacket.getAddress().getHostAddress();
            int port = receivePacket.getPort();
            int dataLen = receivePacket.getLength();

            String data = new String(receivePacket.getData() , 0 , dataLen);

            System.out.println("UDPSearcher receive from ip : " + ip + "\n port : " + port + "\n data : " + data);


            //完成
            System.out.println("UDPSearcher Finished .");
            datagramSocket.close();






        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

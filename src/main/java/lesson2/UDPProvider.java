package lesson2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @Created by jdchi
 * @Date 2019/5/13
 * @Description
 **/
public class UDPProvider {

    public static void main(String[] args) {
        System.out.println("UDPProvider Started");

        try {

            //作为接收者，指定一个端口用于数据接收
            DatagramSocket datagramSocket = new DatagramSocket(20000);

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

            System.out.println("UDPProvider receive from ip : " + ip + "\n port : " + port + "\n data : " + data);

            //构建一份回送数据
            String responseData = "Receive data with len : " + dataLen;

            byte[] responseDataBytes = responseData.getBytes();

            DatagramPacket responsePacket = new DatagramPacket(responseDataBytes , responseDataBytes.length ,
                    receivePacket.getAddress() , receivePacket.getPort());

            datagramSocket.send(responsePacket);

            //完成
            System.out.println("UDPProvider Finished .");
            datagramSocket.close();






        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

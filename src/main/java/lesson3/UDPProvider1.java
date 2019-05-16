package lesson3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

/**
 * @Created by jdchi
 * @Date 2019/5/14
 * @Description
 **/
public class UDPProvider1 {

    public static void main(String[] args) {

        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn);
        provider.start();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        provider.exit();

    }


    private static class Provider extends Thread {

        private final String sn;
        private boolean done = false;
        private DatagramSocket ds = null;

        public Provider(String sn) {
            this.sn = sn;
        }

        @Override
        public void run() {
            super.run();

            System.out.println("UDPProvider Started");
            try {


                //监听 20000 端口
                ds = new DatagramSocket(20000);

                while (!done) {
                    //构建接收实体
                    final byte[] buf = new byte[512];
                    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

                    //接收
                    ds.receive(receivePacket);

                    // 打印接收到的信息与发送者的信息
                    // 发送者的 ip 地址
                    String ip = receivePacket.getAddress().getHostAddress();
                    int port = receivePacket.getPort();
                    int dataLen = receivePacket.getLength();

                    String data = new String(receivePacket.getData(), 0, dataLen);

                    System.out.println("UDPProvider receive from ip : " + ip + "\n port : " + port + "\n data : " + data);

                    //解析端口号
                    int responsePort = MessageCreator.parsePort(data);
                    if (responsePort != -1) {
                        //构建一份回送数据
                        String responseData = MessageCreator.buildWithSn(sn);
                        byte[] responseDataBytes = responseData.getBytes();
                        //直接根据发送者构建一份回送信息
                        DatagramPacket responsePacket = new DatagramPacket(responseDataBytes,
                                responseDataBytes.length,
                                receivePacket.getAddress(),
                                responsePort);

                        ds.send(responsePacket);

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();

            }

            System.out.println("UDPProvider finish");


        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        void exit() {
            done = true;
            close();
        }
    }
}

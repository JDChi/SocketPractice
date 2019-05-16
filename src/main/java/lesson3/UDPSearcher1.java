package lesson3;

import lesson2.UDPSearcher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Created by jdchi
 * @Date 2019/5/14
 * @Description
 **/
public class UDPSearcher1 {

    private static final int LISTEN_PORT = 30000;


    public static void main(String[] args) {

        System.out.println("UDPSearcher started .");

        Listener listener = listen();
        sendBroadcast();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Device> devices = listener.getDevicesAndClose();
        for (Device device : devices) {
            System.out.println("Devices : " + device.toString());
        }

        System.out.println("UDPSearcher finished ");

    }

    private static Listener listen() {


        System.out.println("UDPSearcher start listen.");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT , countDownLatch);
        listener.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return listener;


    }

    private static void sendBroadcast() {

        System.out.println("UDPSearcher sendBroadcast Started");

        try {

            //作为接收者，指定一个端口用于数据接收
            DatagramSocket datagramSocket = new DatagramSocket();

            //构建一份请求数据
            String requestData = MessageCreator.buildWithPort(LISTEN_PORT);
            byte[] requestDataBytes = requestData.getBytes();

            DatagramPacket requestPacket = new DatagramPacket(requestDataBytes, requestDataBytes.length);
            requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
            requestPacket.setPort(20000);
            datagramSocket.send(requestPacket);
            datagramSocket.close();


            //完成
            System.out.println("UDPSearcher sendBroadcast Finished .");


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class Device {
        int port;
        String sn;
        String ip;

        public Device(int port, String sn, String ip) {
            this.port = port;
            this.sn = sn;
            this.ip = ip;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "port= " + port +
                    " ip= " + ip +
                    " sn= " + sn +
                    "}";
        }
    }


    private static class Listener extends Thread {
        private final int listenPort;

        private final CountDownLatch countDownLatch;
        private final List<Device> devices = new ArrayList<>();

        private boolean done = false;
        private DatagramSocket ds = null;

        public Listener(int listenPort, CountDownLatch countDownLatch) {
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            super.run();

            //通知已启动
            countDownLatch.countDown();


            try {
                //监听回送端口
                ds = new DatagramSocket(listenPort);


                while (!done) {
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

                    System.out.println("UDPSearcher receive from ip : " + ip + "\n port : " + port + "\n data : " + data);

                    String sn = MessageCreator.parseSn(data);
                    if (sn != null) {
                        Device device = new Device(port, ip, sn);
                        devices.add(device);
                    }
                }
            } catch (Exception e) {

            } finally {
                close();
            }

            System.out.println("UDPSearcher listener Finished .");
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        List<Device> getDevicesAndClose() {

            done = true;
            close();
            return devices;
        }
    }
}

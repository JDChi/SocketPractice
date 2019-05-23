package lesson5.server;


import lesson5.clink.net.uitls.ByteUtils;
import lesson5.constants.UDPConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @Created by jdchi
 * @Date 2019/5/22
 * @Description
 **/
public class UDPProvider {

    private static Provider PROVIDER_INSTANCE;

    static void start(int port){
        stop();
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn , port);
        provider.start();
        PROVIDER_INSTANCE = provider;

    }

    static void stop(){
        if (PROVIDER_INSTANCE != null) {
            PROVIDER_INSTANCE.exit();
            PROVIDER_INSTANCE = null;
        }
    }

    private static class Provider extends Thread{
        private final byte[] sn;
        private final int port;
        private boolean done = false;
        private DatagramSocket datagramSocket = null;
        final byte[] buffer = new byte[128];

        Provider(String sn , int port){
            this.sn = sn.getBytes();
            this.port = port;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("UDPProvider started");

            try {
                datagramSocket = new DatagramSocket(UDPConstants.PORT_SERVER);
                DatagramPacket receivePack = new DatagramPacket(buffer , buffer.length);

                while (!done){
                    datagramSocket.receive(receivePack);

                    String clientIp = receivePack.getAddress().getHostAddress();
                    int clientPort = receivePack.getPort();
                    int clientDataLen = receivePack.getLength();
                    byte[] clientData = receivePack.getData();
                    boolean isValid = clientDataLen >= (UDPConstants.HEADER.length + 2 + 4)
                            && ByteUtils.startsWith(clientData , UDPConstants.HEADER);

                    System.out.println("UDPProvider receive from ip: " + clientIp +
                    "\n port: " + clientPort + "\n dataValid: " + isValid);

                    if (!isValid) {
                        continue;
                    }

                    int index = UDPConstants.HEADER.length;
                    short cmd = (short)((clientData[index++] << 8) | (clientData[index++] & 0xff));
                    int responsePort = clientData[index++] << 24 | (clientData[index++] & 0xff) << 16|
                            clientData[index++] & 0xff << 8 | clientData[index++] & 0xff;


                    if (cmd == 1 && responsePort > 0) {

                        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                        byteBuffer.put(UDPConstants.HEADER);
                        byteBuffer.putShort((short) 2);
                        byteBuffer.putInt(port);
                        byteBuffer.put(sn);
                        int len = byteBuffer.position();

                        DatagramPacket responsePacket = new DatagramPacket(buffer , len , receivePack.getAddress() , responsePort);
                        datagramSocket.send(responsePacket);
                        System.out.println("UDPProvider response to: " + clientIp + "\n port : " + responsePort + "\ndataLen: " + len);

                    }else {
                        System.out.println("UDPProvider receive cmd nonsupport; cmd:" + cmd + "\nport: " + port);
                    }
                }


            }  catch (IOException e) {
                e.printStackTrace();
            }finally {
                close();
            }
        }

        private void close() {
            if (datagramSocket != null) {
                datagramSocket.close();
                datagramSocket = null;
            }
        }

        void exit(){

            done = true;
            close();
        }
    }
}

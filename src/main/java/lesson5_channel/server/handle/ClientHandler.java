package lesson5_channel.server.handle;

import lesson5_channel.uitls.CloseUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Created by jdchi
 * @Date 2019/5/27
 * @Description
 **/
public class ClientHandler {

    private final Socket socket;
    private final ClientReadHandler readHandler;
    private final ClientWriterHandler writerHandler;
    private final CloseNotify closeNotify;



    public ClientHandler(Socket socket, CloseNotify closeNotify) throws IOException {
        this.socket = socket;
        this.readHandler = new ClientReadHandler(socket.getInputStream());
        this.writerHandler = new ClientWriterHandler(socket.getOutputStream());
        this.closeNotify = closeNotify;

        System.out.println("新客户端连接：" + socket.getInetAddress() + "port: " + socket.getPort());
    }


    public void send(String str) {

        writerHandler.send(str);



    }

    public void exit() {

        readHandler.exit();
        writerHandler.exit();
        CloseUtils.close(socket);
        System.out.println("客户端已退出： " + socket.getInetAddress() + "P: " + socket.getPort());

    }

    private void exitBySelf() {
        exit();
        closeNotify.onSelfClosed(this);
    }

    public interface CloseNotify{
        void onSelfClosed(ClientHandler clientHandler);
    }

    public void readToPrint() {

        readHandler.start();

    }

    class ClientReadHandler extends Thread {


        private boolean done = false;
        private final InputStream inputStream;

        ClientReadHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }


        @Override
        public void run() {
            super.run();

            try {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                do {
                    String str = bufferedReader.readLine();


                    if (str == null) {
                        System.out.println("客户端无法读取数据");

                        ClientHandler.this.exitBySelf();
                        break;
                    }


                    System.out.println(str);

                } while (!done);

            } catch (IOException e) {
                e.printStackTrace();
                if (!done)
                    System.out.println("连接异常断开");
                ClientHandler.this.exitBySelf();
            } finally {
                CloseUtils.close(inputStream);
            }
        }

        void exit(){
            done = true;
            CloseUtils.close(inputStream);

        }

    }

    class ClientWriterHandler{

        private boolean done = false;
        private final PrintStream printStream;
        private final ExecutorService executorService;

        ClientWriterHandler(OutputStream outputStream){
            this.printStream = new PrintStream(outputStream);
            this.executorService = Executors.newSingleThreadExecutor();
        }

        public void send(String str) {

            executorService.execute(new WriteRunnable(str));


        }


        class WriteRunnable implements Runnable{

            private final String msg;

            WriteRunnable(String msg){
                this.msg = msg;
            }



            @Override
            public void run() {

                if (ClientWriterHandler.this.done) {
                    return;
                }


                try {
                    ClientWriterHandler.this.printStream.println(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }

        public void exit(){
            done = true;
            CloseUtils.close(printStream);
            executorService.shutdownNow();
        }

    }


}

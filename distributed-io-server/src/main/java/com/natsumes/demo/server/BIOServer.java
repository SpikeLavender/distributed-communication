package com.natsumes.demo.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BIOServer {
    public static void main(String[] args) throws IOException {
        //创建
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("127.0.0.1", 37258));

        while (true) {
            Socket socket = serverSocket.accept(); //同步阻塞

            new Thread(()-> {
                try{
                    byte[] bytes = new byte[1024];
                    int len = socket.getInputStream().read(bytes);  //同步阻塞
                    System.out.println(new String(bytes, 0, len));
                    socket.getOutputStream().write(bytes, 0, len);
                    socket.getOutputStream().flush();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        }
    }
}

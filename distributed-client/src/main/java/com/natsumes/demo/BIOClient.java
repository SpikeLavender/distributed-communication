package com.natsumes.demo;


import java.io.IOException;
import java.net.Socket;

public class BIOClient {
    public static void main(String[] args) throws IOException {
        //创建
        Socket socket = new Socket("127.0.0.1", 37258);
        socket.getOutputStream().write("bio test".getBytes());
        socket.getOutputStream().flush();
        System.out.println("server send back data");
        byte[] bytes = new byte[1024];
        int len = socket.getInputStream().read(bytes);
        System.out.println(new String(bytes, 0, len));
        socket.close();
    }
}

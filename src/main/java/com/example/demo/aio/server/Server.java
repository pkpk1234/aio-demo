package com.example.demo.aio.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.charset.Charset;

/**
 * @author 李佳明 https://github.com/pkpk1234
 * @date 2018-04-09
 */
public class Server {
    public static final Charset CHARSET = Charset.forName("UTF-8");
    public static void main(String[] args) {
        AsynchronousServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = AsynchronousServerSocketChannel.open();
            serverSocketChannel.bind(
                    new InetSocketAddress(InetAddress.getByName("localhost"), 9090), 2024);
            serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR,true);

            AttachMent attachMent = new AttachMent();
            attachMent.serverSocketChannel = serverSocketChannel;
            serverSocketChannel.accept(attachMent,new AcceptHandler());

            System.out.println("Echo Server listen to localhost:9090 ...");
            Thread.currentThread().join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (serverSocketChannel != null) {
                try {
                    serverSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

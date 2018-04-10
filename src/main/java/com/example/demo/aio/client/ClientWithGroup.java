package com.example.demo.aio.client;

import com.example.demo.aio.server.Server;
import com.example.demo.aio.server.ServerWithGroup;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author 李佳明 https://github.com/pkpk1234
 * @date 2018-04-09
 */
public class ClientWithGroup {
    public static void main(String[] args) {
        Thread clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                AsynchronousSocketChannel asynchronousSocketChannel = null;
                AsynchronousChannelGroup asynchronousChannelGroup = null;
                try {
                    asynchronousChannelGroup = AsynchronousChannelGroup.
                            withFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4,
                                    new MyThreadFactory());

                    asynchronousSocketChannel = AsynchronousSocketChannel.open(asynchronousChannelGroup);
                    asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                    asynchronousSocketChannel.connect(new InetSocketAddress(InetAddress.getByName("localhost"), 9090)).get();
                    System.out.println("Client connected");

                    Attachment attachment = new Attachment();
                    attachment.clientSocketChannel = asynchronousSocketChannel;
                    // 初始化为非read模式
                    attachment.isReadMode = false;
                    attachment.byteBuffer = ByteBuffer.allocate(2048);
                    attachment.mainThread = Thread.currentThread();

                    byte[] data = "Hello".getBytes(Server.CHARSET);
                    attachment.byteBuffer.put(data);
                    attachment.byteBuffer.flip();

                    //客户端连接成功后，启动异步写
                    asynchronousSocketChannel.write(attachment.byteBuffer, attachment, new ReadWriteHandler());

                    //因为AsynchronousSocketChannel的读写方法都是非阻塞的，所以需要阻塞当前线程，避免在客户端退出
                    attachment.mainThread.join();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    if (asynchronousSocketChannel != null) {
                        try {
                            asynchronousSocketChannel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (asynchronousChannelGroup != null) {
                        asynchronousChannelGroup.shutdown();
                    }
                }
            }
        });
        clientThread.start();

        while (clientThread.isAlive()) {
            System.out.println(">>>>>>>> Server thread state " + clientThread.getState());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class MyThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("Client thread " + thread.getId());
            return thread;
        }
    }
}

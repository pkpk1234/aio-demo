package com.example.demo.aio.server;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author 李佳明 https://github.com/pkpk1234
 * @date 2018-04-09
 */
public class AttachMent {
    public AsynchronousServerSocketChannel serverSocketChannel;
    public AsynchronousSocketChannel clientSocketChannel;
    public SocketAddress clientAddress;
    public ByteBuffer byteBuffer;
    public boolean isReadMode;
}

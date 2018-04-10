package com.example.demo.aio.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author 李佳明 https://github.com/pkpk1234
 * @date 2018-04-09
 */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AttachMent> {
    @Override
    public void completed(AsynchronousSocketChannel client, AttachMent attachment) {
        System.out.println(">>>>>>>> AcceptHandler,Thread name is " + Thread.currentThread().getName());
        try {
            SocketAddress clientAdress = client.getRemoteAddress();
            System.out.println("Server accept client :" + clientAdress);
            //为什么此处需要再次accept？？？
            attachment.serverSocketChannel.accept(attachment, this);

            AttachMent newAttachMent = new AttachMent();
            newAttachMent.clientAddress = clientAdress;
            newAttachMent.clientSocketChannel = client;
            newAttachMent.serverSocketChannel = attachment.serverSocketChannel;
            newAttachMent.isReadMode = true;
            newAttachMent.byteBuffer = ByteBuffer.allocate(2048);

            ReadWriteHandler readWriteHandler = new ReadWriteHandler();
            client.read(newAttachMent.byteBuffer, newAttachMent, readWriteHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, AttachMent attachment) {

    }
}

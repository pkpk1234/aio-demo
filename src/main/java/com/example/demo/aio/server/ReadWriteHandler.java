package com.example.demo.aio.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * @author 李佳明 https://github.com/pkpk1234
 * @date 2018-04-09
 */
public class ReadWriteHandler implements CompletionHandler<Integer, AttachMent> {

    @Override
    public void completed(Integer readConut, AttachMent attachment) {
        System.out.println(">>>>>>>> ReadWriteHandler,Thread name is " + Thread.currentThread().getName());
        if (readConut == -1) {
            System.out.println("Server close client " + attachment.clientAddress);
            try {
                attachment.clientSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (attachment.isReadMode) {
            System.out.println("Server read count is " + readConut);
            ByteBuffer byteBuffer = attachment.byteBuffer;
            byteBuffer.flip();
            int limit = byteBuffer.limit();
            byte[] bytes = new byte[limit];
            byteBuffer.get(bytes, 0, limit);
            byteBuffer.rewind();
            System.out.println("Client " + attachment.clientAddress + " send " + new String(bytes, Server.CHARSET));
            attachment.isReadMode = false;
            attachment.clientSocketChannel.write(attachment.byteBuffer, attachment, this);

        } else {
            attachment.isReadMode = true;
            attachment.byteBuffer.clear();
            attachment.clientSocketChannel.read(attachment.byteBuffer, attachment, this);
        }
    }

    @Override
    public void failed(Throwable exc, AttachMent attachment) {
        System.out.println("Connetion exception " + exc);
        try {
            attachment.clientSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

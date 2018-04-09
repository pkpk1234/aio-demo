package com.example.demo.aio.client;

import com.example.demo.aio.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * @author 李佳明 https://github.com/pkpk1234
 * @date 2018-04-09
 */
public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {

    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void completed(Integer readCount, Attachment attachment) {
        if (attachment.isReadMode) {
            ByteBuffer byteBuffer = attachment.byteBuffer;
            byteBuffer.flip();
            int limit = byteBuffer.limit();
            byte[] bytes = new byte[limit];
            byteBuffer.get(bytes, 0, limit);
            String msg = new String(bytes, Server.CHARSET);
            System.out.println("Server response: " + msg);

            try {
                msg = "";
                while (msg.length() == 0) {
                    System.out.println("Enter msg");
                    msg = reader.readLine();
                }
                if (msg.equalsIgnoreCase("end")) {
                    attachment.mainThread.interrupt();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            attachment.isReadMode = false;
            byteBuffer.clear();
            byteBuffer.put(msg.getBytes(Server.CHARSET));
            byteBuffer.flip();
            attachment.clientSocketChannel.write(byteBuffer, attachment, this);
        } else {
            attachment.isReadMode = true;
            attachment.byteBuffer.clear();
            attachment.clientSocketChannel.read(attachment.byteBuffer, attachment, this);
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        System.out.println("exc" + exc.getCause());
        exc.printStackTrace();
        System.exit(1);
    }
}

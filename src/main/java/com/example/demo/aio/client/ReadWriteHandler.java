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
            //read读取完毕后的回调
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
                    //中断client主线程，程序退出
                    attachment.mainThread.interrupt();
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //读取命令行输入的信息之后，再次向服务器写
            attachment.isReadMode = false;
            byteBuffer.clear();
            byteBuffer.put(msg.getBytes(Server.CHARSET));
            byteBuffer.flip();
            attachment.clientSocketChannel.write(byteBuffer, attachment, this);
        } else {
            //write完成全部写入时的回调
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

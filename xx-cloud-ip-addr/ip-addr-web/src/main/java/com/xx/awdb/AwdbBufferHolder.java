package com.xx.awdb;

import com.xx.awdb.enumerate.FileOpenMode;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 构造一个缓冲器实例
 */
class AwdbBufferHolder {
    private final ByteBuffer buffer;

    public AwdbBufferHolder(File file, FileOpenMode mode) throws IOException {
        try (final RandomAccessFile awFile = new RandomAccessFile(file, "r");
             final FileChannel channel = awFile.getChannel()) {
            if (mode == FileOpenMode.MEMORY) {
                this.buffer = ByteBuffer.wrap(new byte[(int) channel.size()]);
                if (channel.read(this.buffer) != this.buffer.capacity()) {
                    throw new IOException("Unable to read " + file.getName() + " into memory. Unexpected end of stream.");
                }
                this.buffer.position(0);
            } else {
                buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            }
        }
    }

    /**
     * buffer 的输入流构造器
     *
     * @param stream 输入流
     * @param mode   模式
     */
    public AwdbBufferHolder(InputStream stream, FileOpenMode mode) throws IOException {
        if (null == stream) {
            throw new NullPointerException("Unable to use a NULL InputStream");
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] bytes = new byte[16 * 1024];
        int br;
        while (-1 != (br = stream.read(bytes))) {
            baos.write(bytes, 0, br);
        }

        byte[] byteArray = baos.toByteArray();
        if (mode == FileOpenMode.MEMORY) {
            // 使用HeapByteBuffer，使用的是jvm的堆内存
            this.buffer = ByteBuffer.wrap(byteArray);
        } else {
            // 使用DirectByteBuffer，直接使用内存空间
            this.buffer = ByteBuffer.allocateDirect(byteArray.length).put(byteArray);
            this.buffer.position(0);
        }

    }

    /**
     * 返回 ByteBuffer 的副本。
     */
    public synchronized ByteBuffer getCurrBuffer() {
        return buffer.duplicate();
    }
}

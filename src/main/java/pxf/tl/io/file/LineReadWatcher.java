package pxf.tl.io.file;


import pxf.tl.api.Charsets;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.io.FileUtil;
import pxf.tl.io.LineHandler;
import pxf.tl.io.watch.SimpleWatcher;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * 行处理的Watcher实现
 *
 * @author potatoxf
 */
public class LineReadWatcher extends SimpleWatcher implements Runnable {

    private final RandomAccessFile randomAccessFile;
    private final Charsets charsets;
    private final LineHandler lineHandler;

    /**
     * 构造
     *
     * @param randomAccessFile {@link RandomAccessFile}
     * @param charsets         编码
     * @param lineHandler      行处理器{@link LineHandler}实现
     */
    public LineReadWatcher(
            RandomAccessFile randomAccessFile, Charsets charsets, LineHandler lineHandler) {
        this.randomAccessFile = randomAccessFile;
        this.charsets = charsets;
        this.lineHandler = lineHandler;
    }

    @Override
    public void run() {
        onModify(null, null);
    }

    @Override
    public void onModify(WatchEvent<?> event, Path currentPath) {
        final RandomAccessFile randomAccessFile = this.randomAccessFile;
        final Charsets charsets = this.charsets;
        final LineHandler lineHandler = this.lineHandler;

        try {
            final long currentLength = randomAccessFile.length();
            final long position = randomAccessFile.getFilePointer();
            if (position == currentLength) {
                // 内容长度不变时忽略此次事件
                return;
            } else if (currentLength < position) {
                // 如果内容变短或变0，说明文件做了删改或清空，回到内容末尾或0
                randomAccessFile.seek(currentLength);
                return;
            }

            // 读取行
            FileUtil.readLines(randomAccessFile, charsets, lineHandler);

            // 记录当前读到的位置
            randomAccessFile.seek(currentLength);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}

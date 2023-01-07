package pxf.tlx.monitor;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.concurrent.ExecutorService;

/**
 * 轮询型文件监视器
 *
 * @author potatoxf
 */
public final class LoopFileMonitor extends FileMonitor {

    private final FileAlterationMonitor monitor;

    LoopFileMonitor(ExecutorService workerExecutorService, long interval) {
        super(workerExecutorService);
        monitor = new FileAlterationMonitor(Math.max(interval, interval));
    }

    /**
     * 添加文件监听器
     *
     * @param fileWatch 文件监视参数
     * @param listener  文件监听器
     * @throws Throwable 抛出任何异常
     */
    @Override
    protected synchronized void doAddListener(FileWatch fileWatch, FileEventListener listener)
            throws Throwable {
        FileAlterationObserver observer = new FileAlterationObserver(fileWatch.getRoot().toFile());
        observer.addListener(new FileAlterationListenerImpl(listener));
        monitor.addObserver(observer);
    }

    /**
     * 删除文件监听器
     *
     * @param fileWatch 文件监视参数
     * @throws Throwable 抛出任何异常
     */
    @Override
    public synchronized void doDelListener(FileWatch fileWatch) throws Throwable {
        FileAlterationObserver data = (FileAlterationObserver) getData(fileWatch);
        if (data != null) {
            monitor.removeObserver(data);
        }
    }

    /**
     * 停止文件监视器
     *
     * @throws Throwable 抛出任何异常
     */
    @Override
    public synchronized void stop() throws Throwable {
        super.stop();
        monitor.stop();
    }

    /**
     * 启动文件监视器
     *
     * @throws Throwable 抛出任何异常
     */
    @Override
    public synchronized void start() throws Throwable {
        super.start();
        monitor.setThreadFactory(getEventThreadFactory());
        monitor.start();
    }

    private static class FileAlterationListenerImpl extends FileAlterationListenerAdaptor {

        private final FileEventListener fileListener;

        FileAlterationListenerImpl(FileEventListener fileListener) {
            this.fileListener = fileListener;
        }

        @Override
        public void onDirectoryCreate(File file) {
            fileListener.emit(file.toPath(), FileWatch.buildEventMark(file, true, false, false));
        }

        @Override
        public void onDirectoryChange(File file) {
            fileListener.emit(file.toPath(), FileWatch.buildEventMark(file, false, false, true));
        }

        @Override
        public void onDirectoryDelete(File file) {
            fileListener.emit(file.toPath(), FileWatch.buildEventMark(file, false, true, false));
        }

        @Override
        public void onFileCreate(File file) {
            fileListener.emit(file.toPath(), FileWatch.buildEventMark(file, true, false, false));
        }

        @Override
        public void onFileChange(File file) {
            fileListener.emit(file.toPath(), FileWatch.buildEventMark(file, false, false, true));
        }

        @Override
        public void onFileDelete(File file) {
            fileListener.emit(file.toPath(), FileWatch.buildEventMark(file, false, true, false));
        }
    }
}

package pxf.tlx.monitor;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyListener;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 事件型文件监视器
 *
 * @author potatoxf
 */
final class EventFileMonitor extends FileMonitor {
    private final AtomicBoolean locked = new AtomicBoolean();
    private final Runnable HOLD =
            () -> {
                synchronized (locked) {
                    try {
                        while (locked.get()) {
                            locked.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
    private Thread thread;

    EventFileMonitor(ExecutorService workerExecutorService) {
        super(workerExecutorService);
    }

    /**
     * 添加文件监听器
     *
     * @param fileWatch 文件监视参数
     * @param listener  文件监听器
     * @throws Throwable 抛出任何异常
     */
    @Override
    public synchronized void doAddListener(FileWatch fileWatch, FileEventListener listener)
            throws Throwable {
        int watch =
                JNotify.addWatch(
                        fileWatch.getRoot().toString(),
                        JNotify.FILE_ANY,
                        fileWatch.isWatchSubPath(),
                        new JNotifyListenerImpl(listener));
        putData(fileWatch, watch);
    }

    /**
     * 删除文件监听器
     *
     * @param fileWatch 文件监视参数
     * @throws Throwable 抛出任何异常
     */
    @Override
    public synchronized void doDelListener(FileWatch fileWatch) throws Throwable {
        Integer data = (Integer) getData(fileWatch);
        if (data != null) {
            JNotify.removeWatch(data);
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
        thread = null;
        locked.set(false);
        locked.notifyAll();
    }

    /**
     * 启动文件监视器
     *
     * @throws Throwable 抛出任何异常
     */
    @Override
    public synchronized void start() throws Throwable {
        super.start();
        if (thread == null) {
            locked.set(true);
            thread = getEventThreadFactory().newThread(HOLD);
            thread.start();
        }
    }

    private static class JNotifyListenerImpl implements JNotifyListener {

        private final FileEventListener fileListener;

        private JNotifyListenerImpl(FileEventListener fileListener) {
            this.fileListener = fileListener;
        }

        /**
         * 文件创建后调用
         *
         * @param watchId      监视Id
         * @param watchPath    被监视的最上层路径
         * @param relativePath 创建的文件相对watchPath的相对路径
         */
        @Override
        public void fileCreated(int watchId, String watchPath, String relativePath) {
            Path path = Paths.get(watchPath, relativePath);
            fileListener.emit(path, FileWatch.buildEventMark(path, true, false, false));
        }

        /**
         * 文件删除后调用 删除一个文件夹时：
         *
         * <p>1.若放入回收站，则只有该文件夹的delete通知（从回收站删除时不再有delete通知）；
         * 2.若不放入回收站直接删除，则该文件夹下所有文件和文件夹（包括子目录）都会有delete通知。
         *
         * @param watchId      监视Id
         * @param watchPath    被监视的最上层路径
         * @param relativePath 删除的文件相对watchPath的相对路径
         */
        @Override
        public void fileDeleted(int watchId, String watchPath, String relativePath) {
            Path path = Paths.get(watchPath, relativePath);
            fileListener.emit(path, FileWatch.buildEventMark(path, false, true, false));
        }

        /**
         * 文件修改后调用 当文件夹下的直接文件或文件夹发生【创建、重命名、删除】动作时，该文件夹也会发生变化；【修改】文件时文件夹不发生变化 如：被监视的文件夹为/001, 其下有文件夹
         * ./123/456 其中： 1.创建文件夹 ./123/456/tmp 2.创建文件 ./123/456/1.txt, 3.重命名文件夹 ./123/456/tmp ->
         * ./123/456/789 4.重命名文件 ./123/456/1.txt -> ./123/456/2.txt 5.删除文件夹 ./123/456/789 6.删除文件
         * ./123/456/2.txt 除了会触发对应路径的【创建、重命名、删除】事件外，还会触发文件夹 ./123/456 的【修改】事件 但： 1.修改文件 ./123/456/1.txt
         * 2.修改文件夹 ./123/456/789 （比如用touch 789对文件夹进行修改） 只会触发对应路径的【修改】事件，不会触发 ./123/456 的【修改】事件
         *
         * @param watchId      监视Id
         * @param watchPath    被监视的最上层路径
         * @param relativePath 修改的文件相对watchPath的相对路径
         */
        @Override
        public void fileModified(int watchId, String watchPath, String relativePath) {
            Path path = Paths.get(watchPath, relativePath);
            fileListener.emit(path, FileWatch.buildEventMark(path, false, false, true));
        }

        /**
         * 文件重命名后调用
         *
         * @param watchId         监视Id
         * @param watchPath       被监视的最上层路径
         * @param oldRelativePath 修改前文件名（相对watchPath的相对路径）
         * @param newRelativePath 修改后文件名（相对watchPath的相对路径）
         */
        @Override
        public void fileRenamed(
                int watchId, String watchPath, String oldRelativePath, String newRelativePath) {
            Path newPath = Paths.get(watchPath, newRelativePath);
            fileListener.emit(newPath, FileWatch.buildEventMark(newPath, true, false, false));
            Path oldPath = Paths.get(watchPath, oldRelativePath);
            fileListener.emit(oldPath, FileWatch.buildEventMark(oldPath, false, true, false));
        }
    }
}

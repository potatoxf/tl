package pxf.tlx.monitor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.api.JavaEnvironment;
import pxf.tl.concurrent.NamedThreadFactory;
import pxf.tl.io.FolderMapping;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * 文件监视器
 *
 * @author potatoxf
 */
public abstract class FileMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileMonitor.class);
    /**
     * 文件监听数据
     */
    private final Map<FileWatch, Data> fileWatchDataMap = new LinkedHashMap<>();
    /**
     * 事件线程工厂
     */
    private final ThreadFactory eventThreadFactory;
    /**
     * 线程池服务
     */
    private final ExecutorService executorService;

    protected FileMonitor(ExecutorService workerExecutorService) {
        this.eventThreadFactory = new NamedThreadFactory(getClass().getSimpleName(), false, null);
        this.executorService = Objects.requireNonNull(workerExecutorService);
    }

    /**
     * 获取默认文件监听器
     *
     * @return
     */
    public static FileMonitor getFileMonitor() {
        return Manager.instance;
    }

    /**
     * 获取文件监视参数
     *
     * @return {@code List<FileWatch>}
     */
    public final synchronized boolean contains(FileWatch fileWatch) {
        return fileWatchDataMap.containsKey(fileWatch);
    }

    /**
     * 获取文件监视参数
     *
     * @return {@code List<FileWatch>}
     */
    public final synchronized List<FileWatch> getFileWatches() {
        return new ArrayList<>(fileWatchDataMap.keySet());
    }

    /**
     * 添加文件监听器
     *
     * @param fileWatch 文件监视参数
     * @param listener  文件监听器
     * @throws Throwable 抛出处理失败的异常信息
     */
    public final synchronized void addListener(FileWatch fileWatch, FileListener listener)
            throws Throwable {
        if (fileWatchDataMap.containsKey(fileWatch)) {
            throw new IllegalArgumentException("The file monitor of '" + fileWatch + "' already exists");
        }
        putData(fileWatch, null);
        doAddListener(fileWatch, wrapper(fileWatch, listener));
    }

    /**
     * 添加文件监听器
     *
     * @param fileWatch 文件监视参数
     * @param listener  文件监听器
     * @throws Throwable 抛出任何异常
     */
    protected abstract void doAddListener(FileWatch fileWatch, FileEventListener listener)
            throws Throwable;

    /**
     * 删除文件监听器
     *
     * @param fileWatch 文件监视参数
     * @throws Throwable 抛出任何异常
     */
    public final synchronized void delListener(FileWatch fileWatch) throws Throwable {
        fileWatchDataMap.remove(fileWatch);
        doDelListener(fileWatch);
    }

    /**
     * 删除文件监听器
     *
     * @param fileWatch 文件监视参数
     * @throws Throwable 抛出任何异常
     */
    public abstract void doDelListener(FileWatch fileWatch) throws Throwable;

    /**
     * 停止文件监视器
     *
     * @throws Throwable 抛出任何异常
     */
    public synchronized void stop() throws Throwable {
        for (FileWatch fileWatch : fileWatchDataMap.keySet()) {
            delListener(fileWatch);
        }
        fileWatchDataMap.clear();
    }

    /**
     * 启动文件监视器
     *
     * @throws Throwable 抛出任何异常
     */
    public synchronized void start() throws Throwable {
    }

    /**
     * 存入自定义数据
     *
     * @param fileWatch 文件监视参数
     * @param data      自定义数据
     */
    protected final synchronized void putData(FileWatch fileWatch, Object data) {
        fileWatchDataMap.put(fileWatch, new Data(data));
    }

    /**
     * 取出自定义数据
     *
     * @param fileWatch 文件监视参数
     * @return 自定义数据
     */
    protected final synchronized Object getData(FileWatch fileWatch) {
        return fileWatchDataMap.get(fileWatch).data;
    }

    /**
     * 获取事件线程工厂
     *
     * @return {@code ThreadFactory}
     */
    protected final ThreadFactory getEventThreadFactory() {
        return eventThreadFactory;
    }

    /**
     * 将文件监听器包裹成内部文件事件监听器
     *
     * @param fileListener 文件监听器
     * @return {@code FileEventListener}
     */
    private FileEventListener wrapper(FileWatch fileWatch, FileListener fileListener) {
        return (path, event) ->
                executorService.submit(new FileListenerRunnable(fileWatch, path, event, fileListener));
    }

    /**
     * 文件事件监听器
     */
    protected interface FileEventListener {
        /**
         * @param path
         * @param event
         */
        void emit(Path path, int event);
    }

    /**
     * 文件监视器管理器
     */
    private static class Manager {

        /**
         * 文件监视器管理器中额单例
         */
        private static final FileMonitor instance;

        static {
            FileMonitor fileMonitor;
            ExecutorService executorService = Executors.newCachedThreadPool();
            try {
                FolderMapping folderMapping = new FolderMapping("META-INF/native", JavaEnvironment.USER_DIR);
                //todo
//                if (JavaEnvironment.OS_NAME) {
//                    folderMapping.init("windows32/jnotify.dll");
//                    folderMapping.init("windows64/jnotify_64bit.dll");
//                } else if (JavaEnvironment.IS_OS_MAC_OSX) {
//                    folderMapping.init("osx/libjnotify.jnilib");
//                } else if (JavaEnvironment.IS_OS_UNIX) {
//                    folderMapping.init("linux32/libjnotify.so");
//                    folderMapping.init("linux64/libjnotify.so");
//                } else {
//                    throw new RuntimeException("Jnotify System modification is not supported");
//                }
                fileMonitor = new EventFileMonitor(executorService);
            } catch (Exception e) {
                fileMonitor = new LoopFileMonitor(executorService, 1000);
            }
            instance = fileMonitor;
        }
    }

    private static class Data {
        private final Object data;

        public Data(Object data) {
            this.data = data;
        }
    }

    /**
     * 文件监听器的多线程调用
     */
    private static class FileListenerRunnable implements Runnable {
        private final FileListener instance;
        private final FileWatch fileWatch;
        private final Path path;
        private final int event;

        public FileListenerRunnable(FileWatch fileWatch, Path path, int event, FileListener instance) {
            this.instance = instance;
            this.fileWatch = fileWatch;
            this.path = path;
            this.event = event;
        }

        @Override
        public void run() {
            if (fileWatch.isValidEvent(event)) {
                Pattern regexpPath = fileWatch.getRegexpPath();
                if (regexpPath != null && !regexpPath.matcher(path.toString()).matches()) {
                    return;
                }
                Predicate<Path> pathFilter = fileWatch.getPathFilter();
                if (pathFilter != null && !pathFilter.test(path)) {
                    return;
                }
                if (!fileWatch.isWatchSubPath()
                        && fileWatch.getRoot().relativize(path).getNameCount() != 1) {
                    return;
                }
                LOGGER.info(
                        "The "
                                + (FileWatch.isFile(event) ? "file" : "directory")
                                + " of ["
                                + path
                                + "] trigger event <"
                                + (FileWatch.isCreated(event)
                                ? "created:"
                                : (FileWatch.isDeleted(event) ? "deleted" : "updated"))
                                + ">");
                instance.emit(fileWatch, path, event);
            }
        }
    }
}

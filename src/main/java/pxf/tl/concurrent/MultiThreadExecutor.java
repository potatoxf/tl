package pxf.tl.concurrent;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author potatoxf
 */
public abstract class MultiThreadExecutor<Key> {

    private final AtomicBoolean isActive = new AtomicBoolean(false);
    private final ExecutorService executorService;
    private final ConcurrentLinkedQueue<Key> executeList;
    private final Map<Key, TaskInfo> tasks;

    public MultiThreadExecutor() {
        this(5, 10);
    }

    public MultiThreadExecutor(int corePoolSize,
                               int maximumPoolSize) {
        this(corePoolSize, maximumPoolSize, Executors.defaultThreadFactory());
    }

    public MultiThreadExecutor(int corePoolSize,
                               int maximumPoolSize,
                               ThreadFactory threadFactory) {
        this.executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), threadFactory);
        this.executeList = new ConcurrentLinkedQueue<Key>();
        this.tasks = Collections.synchronizedMap(new LinkedHashMap<Key, TaskInfo>());
    }

    public final void addTask(Key key) {
        if (tasks.containsKey(key)) {
            tasks.get(key).increment();
        } else {
            tasks.put(key, new TaskInfo());
            executeList.add(key);
        }
    }

    public final void addTask(Key[] keys) {
        if (keys != null && keys.length != 0) {
            for (Key key : keys) {
                addTask(key);
            }
        }
    }

    public final void addTask(Collection<? extends Key> keys) {
        if (keys != null && !keys.isEmpty()) {
            for (Key key : keys) {
                addTask(key);
            }
        }
    }

    public final void execute() {
        tryExecute();
    }

    public final void shutdown() {
        executorService.shutdown();
    }

    public final void shutdownNow() {
        executorService.shutdown();
    }

    public final boolean isEmpty() {
        return tasks.isEmpty();
    }

    public final void tryAwait() throws InterruptedException {
        if (!isEmpty()) {
            synchronized (isActive) {
                while (!isEmpty()) {
                    isActive.wait();
                }
            }
        }
    }

    protected abstract Runnable createTask(Key key);

    private void tryExecute() {
        if (!isActive.get()) {
            executorService.execute(new TaskDispatcher());
        }
    }

    private void tryNotifyAll() {
        if (isEmpty()) {
            synchronized (isActive) {
                isActive.notifyAll();
            }
        }
    }

    private boolean isRunningTask(Key key) {
        return tasks.get(key).running.get();
    }

    private class TaskDispatcher implements Runnable {

        @Override
        public void run() {
            synchronized (isActive) {
                isActive.set(true);
                while (!executeList.isEmpty()) {
                    executeTask();
                    executeRestTask();
                }
                isActive.set(false);
                tryNotifyAll();
            }
        }

        private void executeTask() {
            Key key;
            while ((key = executeList.poll()) != null) {
                TaskInfo taskInfo = tasks.get(key);
                if (isRunningTask(key)) {
                    taskInfo.increment();
                } else {
                    Runnable task = createTask(key);
                    if (task != null) {
                        taskInfo.start();
                        executorService.execute(new WrapperRunnable(key, task));
                    } else {
                        tasks.remove(key);
                    }
                }
            }
        }

        private void executeRestTask() {
            Set<Key> keys = tasks.keySet();
            for (Key key : keys) {
                if (!isRunningTask(key)) {
                    executeList.add(key);
                }
            }
            executeTask();
        }
    }

    private class WrapperRunnable implements Runnable {
        private final Key key;
        private final Runnable runnable;

        private WrapperRunnable(Key key, Runnable runnable) {
            this.key = key;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            runnable.run();
            TaskInfo taskInfo = tasks.get(key);
            if (!taskInfo.hasCount()) {
                tasks.remove(key);
            } else {
                taskInfo.end();
            }
            tryNotifyAll();
            System.out.println(key + "：执行完成");
        }

    }

    private static class TaskInfo {
        private final AtomicBoolean running = new AtomicBoolean(false);
        private final AtomicInteger count = new AtomicInteger(1);

        boolean isRunning() {
            return running.get();
        }

        boolean hasCount() {
            return count.get() > 0;
        }

        void start() {
            if (!isRunning()) {
                running.set(true);
                count.decrementAndGet();
            }
        }

        void end() {
            if (isRunning()) {
                running.set(false);
                count.set(1);
            }
        }

        void increment() {
            count.incrementAndGet();
        }
    }

}

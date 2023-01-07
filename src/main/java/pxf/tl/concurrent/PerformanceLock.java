package pxf.tl.concurrent;


import pxf.tl.exception.IllegalCallException;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 性能锁，这主要场景如下：
 *
 * <p>可能会出现并发，但是处理一段时间后可能不用加锁了，因为没有写操作。因此在这种情况就需要把锁清除掉，从此以后就不加锁。
 *
 * <p>注意：如果逻辑中关闭了该锁（锁失效），则不能有效保护读写并发数据，此时只适合全读
 *
 * @author potatoxf
 */
public class PerformanceLock {

    private final AtomicBoolean lockFailure = new AtomicBoolean(false);
    private final ReentrantSpinLock reentrantSpinLock = new ReentrantSpinLock();
    private volatile ReentrantReadWriteLock lock;

    /**
     * 写锁操作
     *
     * @param op 加锁 {@code true}，解锁 {@code false}
     * @return 如果返回false则锁已经失效，否则锁正常使用
     */
    public final boolean writeLock(boolean op) {
        if (checkLockFailure()) {
            return false;
        }
        initLock();
        if (op) {
            // 锁
            lock.writeLock().lock();
        } else {
            // 解锁
            lock.writeLock().unlock();
        }
        return true;
    }

    /**
     * 锁是否失效
     *
     * @return 如果失效为 {@code true}，否则 {@code false}
     */
    public boolean isLockFailure() {
        return lockFailure.get();
    }

    /**
     * 读锁操作
     *
     * @param op 加锁 {@code true}，解锁 {@code false}
     * @return 如果返回false则锁已经失效，否则锁正常使用
     */
    public final boolean readLock(boolean op) {
        if (checkLockFailure()) {
            return false;
        }
        initLock();
        // 锁
        if (op) {
            lock.readLock().lock();
        } else {
            // 解锁
            lock.readLock().unlock();
        }
        return true;
    }

    /**
     * 放弃锁
     *
     * <p>为什么要放弃锁内？
     *
     * <p>因为会有一种这种情况，当数据初始化完毕后就不在需要这个锁了，这也意味这剩下的操作只有读了
     *
     * @return 如果返回false则锁已经失效，否则锁正常使用
     * @throws IllegalCallException 当锁从未使用过调用该方法抛出异常，或者写锁未完全释放掉
     */
    public final boolean abandon() {
        if (lock == null && !lockFailure.get()) {
            throw new IllegalCallException("Error call!");
        }
        if (checkLockFailure()) {
            return false;
        }
        try {
            reentrantSpinLock.lock();
            lockFailure.compareAndSet(false, true);
            int writeHoldCount = lock.getWriteHoldCount();
            if (writeHoldCount == 0) {
                int readLockCount = lock.getReadLockCount();
                if (readLockCount != 0) {
                    for (int i = 0; i < readLockCount; i++) {
                        lock.readLock().unlock();
                    }
                }
                lock = null;
            } else {
                throw new IllegalCallException("There are threads holding write locks");
            }
        } finally {
            reentrantSpinLock.unlock();
        }
        return true;
    }

    /**
     * 检查锁是否失效
     *
     * @return 如果失效为 {@code true}，否则 {@code false}
     */
    private boolean checkLockFailure() {
        return isLockFailure();
    }

    /**
     * 初始化锁
     */
    private void initLock() {
        if (lock == null) {
            try {
                reentrantSpinLock.lock();
                if (lock == null) {
                    lock = new ReentrantReadWriteLock();
                }
            } finally {
                reentrantSpinLock.unlock();
            }
        }
    }
}

package pxf.tl.concurrent;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 可变可重入锁
 *
 * @author potatoxf
 */
public class VariableReentrantReadWriteLock {
    private final ReentrantReadWriteLock lock;

    public VariableReentrantReadWriteLock(boolean isThreadSafe) {
        if (isThreadSafe) {
            lock = new ReentrantReadWriteLock();
        } else {
            lock = null;
        }
    }

    /**
     * @return
     */
    public boolean isThreadSafe() {
        return lock != null;
    }

    /**
     * 获取锁，可能为null
     *
     * @return ReentrantReadWriteLock
     */
    public ReentrantReadWriteLock getLock() {
        return lock;
    }

    /**
     * 锁写锁
     */
    public void lockWriteLock() {
        if (lock != null) {
            lock.writeLock().lock();
        }
    }

    /**
     * 解锁写锁
     */
    public void unlockWriteLock() {
        if (lock != null) {
            lock.writeLock().unlock();
        }
    }

    /**
     * 锁读锁
     */
    public void lockReadLock() {
        if (lock != null) {
            lock.readLock().lock();
        }
    }

    /**
     * 解锁读锁
     */
    public void unlockReadLock() {
        if (lock != null) {
            lock.readLock().unlock();
        }
    }
}

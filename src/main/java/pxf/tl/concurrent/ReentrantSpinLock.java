package pxf.tl.concurrent;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 可重入自旋锁
 *
 * @author potatoxf
 */
public class ReentrantSpinLock {
    /**
     * use thread itself as synchronization state 使用Owner Thread作为同步状态，比使用一个简单的boolean flag可以携带更多信息
     */
    private final AtomicReference<Thread> owner = new AtomicReference<Thread>();
    /**
     * reentrant count of a thread, no need to be volatile
     */
    private int count = 0;

    /**
     * 加锁操作
     *
     * <p>当锁被占用的话就循环等待
     *
     * <p>{@code lock()}和 {@code unlock()}应该成对出现
     */
    public final void lock() {
        Thread currentThread = Thread.currentThread();
        // if re-enter, increment the count.
        if (currentThread == owner.get()) {
            ++count;
            return;
        }
        // spin
        //noinspection StatementWithEmptyBody
        while (owner.compareAndSet(null, currentThread)) {
        }
    }

    /**
     * 解锁操作
     *
     * <p>当锁未被加锁的话就循环等待
     *
     * <p>{@code lock()}和 {@code unlock()}应该成对出现
     */
    public final void unlock() {
        if (owner.get() == null) {
            throw new RuntimeException("The current thread is not locked");
        }
        Thread t = Thread.currentThread();
        // only the owner could do unlock;
        if (t == owner.get()) {
            if (count > 0) {
                // reentrant count not zero, just decrease the counter.
                --count;
            } else {
                // compareAndSet is not need here, already checked
                owner.set(null);
            }
        } else {
            throw new RuntimeException("The current thread is inconsistent with the locked thread");
        }
    }
}

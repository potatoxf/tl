package pxf.tl.lang;


import pxf.tl.concurrent.IntegerThreadLocal;
import pxf.tl.concurrent.VariableReentrantReadWriteLock;

import java.util.*;

/**
 * 抽象注册链
 *
 * <p>它有线程安全和不安全两种模式
 *
 * <p>线程安全模式，只对于链表的访问是安全的和注册是安全的，并不能保证链表内的元素是线程安全的
 *
 * @author potatoxf
 */
public abstract class AbstractChain<T> implements Iterable<T>, Iterator<T> {

    private final VariableReentrantReadWriteLock lock;
    private final IntegerThreadLocal indexThread;
    private final List<T> chain = new LinkedList<T>();
    private boolean isFixed = false;

    public AbstractChain() {
        this(false);
    }

    public AbstractChain(boolean isThreadSafe) {
        lock = new VariableReentrantReadWriteLock(isThreadSafe);
        indexThread = new IntegerThreadLocal();
    }

    /**
     * 是否线程安全
     *
     * @return 如果线程安全返回true，否则false
     */
    public boolean isThreadSafe() {
        return lock.isThreadSafe();
    }

    /**
     * 注册元素
     *
     * @param ts 元素数组
     * @throws IllegalStateException 如果固定后添加元素则会抛出该异常
     */
    @SafeVarargs
    public final AbstractChain<T> register(T... ts) {
        if (ts != null) {
            try {
                lock.lockWriteLock();
                if (isFixed) {
                    throw new IllegalStateException(
                            "The linked list has been fixed and the element cannot be registered");
                }
                Collections.addAll(chain, ts);
            } finally {
                lock.unlockWriteLock();
            }
        }
        return this;
    }

    /**
     * 固定链表，固定后无法添加元素
     */
    public final AbstractChain<T> fixed() {
        try {
            lock.lockWriteLock();
            isFixed = true;
        } finally {
            lock.unlockWriteLock();
        }
        return this;
    }

    /**
     * 排序链表
     *
     * @param comparator 比较器
     */
    public final void sort(Comparator<T> comparator) {
        if (comparator != null) {
            try {
                lock.lockWriteLock();
                chain.sort(comparator);
            } finally {
                lock.unlockWriteLock();
            }
        }
    }

    /**
     * 大小
     *
     * @return 大小
     */
    public final int size() {
        try {
            lock.unlockReadLock();
            return chain.size();
        } finally {
            lock.unlockReadLock();
        }
    }

    /**
     * 是否有下一个元素
     *
     * @return 没有下一个元素返回false，否则返回true
     */
    @Override
    public final boolean hasNext() {
        try {
            lock.lockReadLock();
            int index = indexThread.get();
            if (index < chain.size()) {
                return true;
            } else {
                indexThread.remove();
                return false;
            }
        } finally {
            lock.unlockReadLock();
        }
    }

    /**
     * 下一个元素
     *
     * @return 下一个元素
     */
    @Override
    public final T next() {
        try {
            lock.lockReadLock();
            return chain.get(indexThread.getAndIncrease());
        } finally {
            lock.unlockReadLock();
        }
    }

    /**
     * 不支持移除操作
     */
    @Override
    public final void remove() {
        throw new UnsupportedOperationException("remove");
    }

    /**
     * 生成迭代器拷贝
     *
     * @return {@code Iterator<T>}
     */
    @Override
    public Iterator<T> iterator() {
        try {
            lock.lockReadLock();
            return new ArrayList<T>(chain).iterator();
        } finally {
            lock.unlockReadLock();
        }
    }
}

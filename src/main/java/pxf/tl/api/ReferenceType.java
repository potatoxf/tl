package pxf.tl.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.*;

/**
 * 引用类型
 * <pre>
 * 1. {@link SoftReference} 软引用，在GC报告内存不足时会被GC回收
 * 2. {@link WeakReference} 弱引用，在GC时发现弱引用会回收其对象
 * 3. {@link PhantomReference} 虚引用，在GC时发现虚引用对象，会将{@link PhantomReference}插入{@link ReferenceQueue}。 此时对象未被真正回收，要等到{@link ReferenceQueue}被真正处理后才会被回收。
 * </pre>
 *
 * @author potatoxf
 */
public enum ReferenceType {
    /**
     * 软引用，在GC报告内存不足时会被GC回收
     */
    SOFT,
    /**
     * 弱引用，在GC时发现弱引用会回收其对象
     */
    WEAK,
    /**
     * 虚引用，在GC时发现虚引用对象，会将{@link PhantomReference}插入{@link ReferenceQueue}。 <br>
     * 此时对象未被真正回收，要等到{@link ReferenceQueue}被真正处理后才会被回收。
     */
    PHANTOM;

    /**
     * 获得引用
     *
     * @param <T>      被引用对象类型
     * @param type     引用类型枚举
     * @param referent 被引用对象
     * @return {@link Reference}
     */
    @Nonnull
    public static <T> Reference<T> create(@Nonnull ReferenceType type, @Nonnull T referent) {
        return create(type, referent, null);
    }

    /**
     * 获得引用
     *
     * @param <T>      被引用对象类型
     * @param type     引用类型枚举
     * @param referent 被引用对象
     * @param queue    引用队列
     * @return {@link Reference}
     */
    @Nonnull
    public static <T> Reference<T> create(@Nonnull ReferenceType type, @Nonnull T referent, @Nullable ReferenceQueue<T> queue) {
        return switch (type) {
            case SOFT -> new SoftReference<>(referent, queue);
            case WEAK -> new WeakReference<>(referent, queue);
            case PHANTOM -> new PhantomReference<>(referent, queue);
        };
    }
}

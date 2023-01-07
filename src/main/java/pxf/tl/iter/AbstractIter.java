package pxf.tl.iter;

import pxf.tl.function.LoopConsumer;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * 带有计算属性的遍历器<br>
 * 通过继承此抽象遍历器，实现{@link #doNext()}计算下一个节点，即可完成节点遍历<br>
 * 当调用{@link #hasNext()}时将此方法产生的节点缓存，直到调用{@link #next()}取出<br>
 * 当无下一个节点时，须返回{@code null}表示遍历结束
 *
 * @param <T> 节点类型
 * @author potatoxf
 */
public abstract class AbstractIter<T> implements Iter<T>, Closeable {
    /**
     * 当前值
     */
    private T currentValue;
    /**
     * The element size
     */
    private int size = 0;
    /**
     * 遍历中
     */
    private boolean running = false;
    /**
     * 是否结束
     */
    private boolean finish = false;
    /**
     * 是否重新遍历
     */
    private boolean again = false;
    /**
     * 是否第一次遍历
     */
    private boolean first = true;

    @Override
    public final boolean hasNext() {
        final boolean f = finish;
        final T cv = currentValue;
        running = true;
        if (cv != null) {
            // 用户读取了节点，但是没有使用
            return true;
        } else if (f) {
            // 读取结束
            return false;
        } else {
            if (!isSupportAgain()) {
                throw new UnsupportedOperationException("Unsupported Reset Operation");
            }
        }

        again = false;
        T result = doNext();

        if (result == null) {
            // 不再有新的节点，结束
            finish = !again;
            first = false;
            running = false;
            return false;
        } else {
            currentValue = result;
            if (first) {
                size++;
            }
            return true;
        }
    }

    @Override
    public final T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more lines");
        }
        T result = currentValue;
        // 清空cache，表示此节点读取完毕，下次计算新节点
        currentValue = null;
        return result;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Nonnull
    @Override
    public final Iterator<T> iterator() {
        return new ReadOnlyIter<>(this);
    }

    /**
     * 循环遍历 {@link Iterator}，使用{@link Consumer} 接受遍历的每条数据，并针对每条数据做处理
     *
     * @param consumer {@link Consumer} 遍历的每条数据处理器
     */
    public final void forEach(@Nonnull LoopConsumer<T> consumer) {
        int index = 0;
        Boolean b;
        while (this.hasNext()) {
            b = consumer.accept(this.next(), index);
            if (b == null) {
                return;
            }
            if (b) {
                index++;
            }
        }
    }

    /**
     * The element size
     *
     * @return return the element size
     */
    public final int size() {
        return size;
    }

    /**
     * 重置迭代器
     */
    public void reset() {
        this.size = 0;
        this.running = false;
        this.finish = false;
        this.again = false;
        this.first = true;
        this.currentValue = null;
    }

    /**
     * 再一次迭代
     */
    public void again() {
        this.running = false;
        this.finish = false;
        this.again = true;
        this.first = false;
        this.currentValue = null;

    }

    /**
     * 手动结束遍历器，用于关闭操作等
     */
    @Override
    public void close() throws IOException {
        this.running = false;
        this.finish = false;
        this.again = false;
        this.first = false;
        this.currentValue = null;
    }

    protected boolean isRunning() {
        return running;
    }

    /**
     * 是否支持重复迭代
     *
     * @return 如果支持返回true，否则返回false
     */
    protected boolean isSupportAgain() {
        return false;
    }

    /**
     * 获取下一个值，当无下一个节点时，须返回{@code null}表示遍历结束
     *
     * @return 节点值
     */
    protected abstract T doNext();
}

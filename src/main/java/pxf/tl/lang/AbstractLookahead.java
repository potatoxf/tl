package pxf.tl.lang;

import pxf.tl.help.Whether;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract data structure for providing streams of items with a lookahead.
 *
 * <p>用于提供元素前瞻性预览
 *
 * <p>Items provided by subclasses of this are processed one after each other. However, using {@link
 * #next()} or {@link #next(int)} one can peek at upcoming items without consuming the current one.
 *
 * @param <T> the type of the elements kept in the stream
 * @author potatoxf
 */
public abstract class AbstractLookahead<T> {
    /**
     * Internal buffer containing items which where already created due to lookahead.
     */
    protected List<T> itemBuffer = new LinkedList<>();

    /**
     * Determines if the end of the underlying data source has been reached.
     */
    protected boolean endReached = false;

    /**
     * Once the end of the underlying input was reached, an end of input indicator is created and
     * constantly returned for all calls of current and next.
     */
    protected T endOfInputIndicator;

    /**
     * Returns the item the stream is currently pointing at.
     *
     * <p>This method does not change the internal state. Therefore it can be called several times and
     * will always return the same result.
     *
     * @return the item the stream is currently pointing at.
     */
    public T current() {
        return next(0);
    }

    /**
     * Returns the next item after the current one in the stream.
     *
     * <p>This method does not change the internal state. Therefore it can be called several times and
     * will always return the same result.
     *
     * @return the next item in the stream. This will be the current item, after a call to {@link
     * #consume()}
     */
    public T next() {
        return next(1);
    }

    /**
     * Returns the next n-th item in the stream.
     *
     * <p>Calling this method with 0 as parameter, will return the current item. Calling it with 1
     * will return the same item as a call to <tt>next()</tt>.
     *
     * <p>This method does not change the internal state. Therefore it can be called several times and
     * will always return the same result.
     *
     * @param offset the number of items to skip
     * @return the n-th item in the stream
     */
    public T next(int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("The item offset < 0");
        }
        while (offset >= itemBuffer.size() && !endReached) {
            T item = fetch();
            if (item != null) {
                itemBuffer.add(item);
            } else {
                endReached = true;
            }
        }
        if (offset >= itemBuffer.size()) {
            if (endOfInputIndicator == null) {
                endOfInputIndicator = endOfInput();
            }
            return endOfInputIndicator;
        } else {
            return itemBuffer.get(offset);
        }
    }

    /**
     * Creates the end of input indicator item.
     *
     * <p>This method will be only called apply, as the indicator is cached.
     *
     * @return a special item which marks the end of the input
     */
    protected abstract T endOfInput();

    /**
     * Fetches the next item from the stream.
     *
     * @return the next item in the stream or <tt>null</tt> to indicate that the end was reached
     */
    protected abstract T fetch();

    /**
     * Removes and returns the current item from the stream.
     *
     * <p>After this method was called, all calls to {@link #current()} will then return the item,
     * which was previously returned by {@link #next()}
     *
     * @return the item which is being removed from the stream
     */
    public T consume() {
        T result = current();
        consume(1);
        return result;
    }

    /**
     * Consumes (removes) <tt>itemCount</tt> at apply.
     *
     * <p>Removes the given number of items from the stream.
     *
     * @param itemCount the number of items to remove
     */
    public void consume(int itemCount) {
        if (itemCount < 0) {
            throw new IllegalArgumentException("The item count < 0");
        }
        while (itemCount-- > 0) {
            if (!Whether.empty(itemBuffer)) {
                itemBuffer.remove(0);
            } else {
                if (endReached) {
                    return;
                }
                T item = fetch();
                if (item == null) {
                    endReached = true;
                }
            }
        }
    }
}

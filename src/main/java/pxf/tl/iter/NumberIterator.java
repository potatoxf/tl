package pxf.tl.iter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author potatoxf
 */
public class NumberIterator extends AbstractIter<Integer> {

    private final int hi;
    private final AtomicInteger lo;
    private final boolean isIncrement;

    /**
     * @param lo
     * @param hi
     */
    public NumberIterator(int lo, int hi) {
        this(lo, hi, true);
    }

    /**
     * @param lo
     * @param hi
     * @param isIncrement
     */
    public NumberIterator(int lo, int hi, boolean isIncrement) {
        if (isIncrement) {
            this.lo = new AtomicInteger(lo);
            this.hi = hi;
        } else {
            this.lo = new AtomicInteger(hi);
            this.hi = lo;
        }
        this.isIncrement = isIncrement;
    }

    /**
     * 计算新的节点，通过实现此方法，当调用{@link #hasNext()}时将此方法产生的节点缓存，直到调用{@link #next()}取出<br>
     * 当无下一个节点时，须返回{@code null}表示遍历结束
     *
     * @return 节点值
     */
    @Override
    protected Integer doNext() {
        if (isIncrement ? lo.get() < hi : lo.get() > hi) {
            return isIncrement ? lo.getAndIncrement() : lo.getAndDecrement();
        }
        return null;
    }
}

package pxf.tl.iter;


import pxf.tl.help.Assert;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 综合迭代器
 *
 * @author potatoxf
 */
public class CompositeIterator<E> extends AbstractIter<E> implements Iterator<E> {

    /**
     *
     */
    private final Set<Iterator<E>> iterators = new LinkedHashSet<>();
    /**
     *
     */
    private boolean inUse = false;

    /**
     * @param iterator
     */
    public void add(Iterator<E> iterator) {
        Assert.beTrue(
                !this.inUse,
                "You can no longer add iterators to a composite iterator that's already in use");
        if (this.iterators.contains(iterator)) {
            throw new IllegalArgumentException("You cannot add the same iterator twice");
        }
        this.iterators.add(iterator);
    }

    /**
     * 计算新的节点，通过实现此方法，当调用{@link #hasNext()}时将此方法产生的节点缓存，直到调用{@link #next()}取出<br>
     * 当无下一个节点时，须返回{@code null}表示遍历结束
     *
     * @return 节点值
     */
    @Override
    protected E doNext() {
        this.inUse = true;
        for (Iterator<E> iterator : this.iterators) {
            if (iterator.hasNext()) {
                return iterator.next();
            }
        }
        return null;
    }
}

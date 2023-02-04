package pxf.tl.iter;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pxf.tl.help.Assert;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 包装 {@link NodeList} 的{@link Iterator}
 *
 * <p>此 iterator 不支持 {@link #remove()} 方法。
 *
 * @author potatoxf
 * @see NodeList
 */
public class NodeListIter implements Iter<Node> {

    private final NodeList nodeList;
    /**
     * 当前位置索引
     */
    private int index = 0;

    /**
     * 构造, 根据给定{@link NodeList} 创建{@code NodeListIterator}
     *
     * @param nodeList {@link NodeList}，非空
     */
    public NodeListIter(final NodeList nodeList) {
        this.nodeList = Assert.notNull(nodeList, "NodeList must not be null.");
    }

    @Override
    public boolean hasNext() {
        return nodeList != null && index < nodeList.getLength();
    }

    @Override
    public Node next() {
        if (nodeList != null && index < nodeList.getLength()) {
            return nodeList.item(index++);
        }
        throw new NoSuchElementException("underlying nodeList has no more elements");
    }

    /**
     * Throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException(
                "remove() method not supported for a NodeListIterator.");
    }

    @Override
    public void reset() {
        this.index = 0;
    }
}

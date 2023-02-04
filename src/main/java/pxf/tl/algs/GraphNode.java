package pxf.tl.algs;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author potatoxf
 */
public abstract class GraphNode<V, N extends GraphNode<V, N>> extends Node<V, N> {
    private final List<N> list = new CopyOnWriteArrayList<>();

    protected GraphNode(V v) {
        super(v, false);
    }

    /**
     * @param node
     */
    public void addNode(N node) {
        list.add(node);
    }

    public void delNode(N node) {
        list.remove(node);
    }

    /**
     * @return
     */
    public List<N> list() {
        return list;
    }

    /**
     * @param comparator
     */
    public void sort(Comparator<N> comparator) {
        list.sort(comparator);
    }
}

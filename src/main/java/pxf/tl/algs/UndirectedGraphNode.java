package pxf.tl.algs;

/**
 * @author potatoxf
 */
public class UndirectedGraphNode<V> extends GraphNode<V, UndirectedGraphNode<V>> {

    protected UndirectedGraphNode(V v) {
        super(v);
    }
}

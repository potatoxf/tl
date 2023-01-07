package pxf.tl.algs;

/**
 * @author potatoxf
 */
class DirectedGraphNode<V> extends GraphNode<V, DirectedGraphNode<V>> {

    protected DirectedGraphNode(V v) {
        super(v);
    }
}

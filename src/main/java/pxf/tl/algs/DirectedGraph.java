package pxf.tl.algs;

/**
 * @author potatoxf
 */
public class DirectedGraph<T> extends Graph<T, DirectedGraphNode<T>> {

    @Override
    public void addEdge(T v, T w) {
        DirectedGraphNode<T> vGraphNode = getNodeOrCreate(v);
        DirectedGraphNode<T> wGraphNode = getNodeOrCreate(w);
        vGraphNode.addNode(wGraphNode);
    }

    @Override
    protected DirectedGraphNode<T> createGraphNode(T v) {
        return new DirectedGraphNode<>(v);
    }
}

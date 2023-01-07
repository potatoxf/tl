package pxf.tl.algs;

/**
 * @author potatoxf
 */
public class UndirectedGraph<T> extends Graph<T, UndirectedGraphNode<T>> {

    @Override
    public void addEdge(T v, T w) {
        UndirectedGraphNode<T> vGraphNode = getNodeOrCreate(v);
        UndirectedGraphNode<T> wGraphNode = getNodeOrCreate(w);
        vGraphNode.addNode(wGraphNode);
        wGraphNode.addNode(vGraphNode);
    }

    @Override
    protected UndirectedGraphNode<T> createGraphNode(T v) {
        return new UndirectedGraphNode<>(v);
    }
}

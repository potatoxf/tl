package pxf.tl.algs;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author potatoxf
 */
public abstract class Graph<T, N extends GraphNode<T, N>> {
    /**
     * 邻接表
     */
    private final Map<T, N> adjacencyList = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * 边数
     */
    private final AtomicInteger edge = new AtomicInteger(0);

    public void sort(Comparator<T> comparator) {
        adjacencyList.forEach((k, v) -> v.sort((o1, o2) -> comparator.compare(o1.value(), o2.value())));
    }

    public int vertexSize() {
        return adjacencyList.size();
    }

    public int edgeSize() {
        return edge.get();
    }

    public Iterable<T> vertex() {
        return new ArrayList<>(adjacencyList.keySet());
    }

    public int degree(T v) {
        N graphNode = adjacencyList.get(v);
        if (graphNode == null) {
            return 0;
        }
        return graphNode.list().size();
    }

    public int maxDegree() {
        int max = 0;
        for (T vertex : vertex()) {
            int degree = degree(vertex);
            if (degree > max) {
                max = degree;
            }
        }
        return max;
    }

    public int avgDegree() {
        return 2 * edgeSize() / vertexSize();
    }

    public int numberOfSelfLoops() {
        int count = 0;
        for (T v : vertex()) {
            for (T w : adj(v)) {
                if (v.equals(w)) {
                    count++;
                }
            }
        }
        return count;
    }

    public abstract void addEdge(T v, T w);

    public boolean containsEdge(T v, T w) {
        N vGraphNode = adjacencyList.get(v);
        if (vGraphNode != null) {
            N wGraphNode = adjacencyList.get(w);
            return vGraphNode.list().contains(wGraphNode);
        }
        return false;
    }

    public Iterable<T> adj(T v) {
        N graphNode = adjacencyList.get(v);
        if (graphNode == null) {
            return null;
        }
        return graphNode.list().stream().map(Node::value).collect(Collectors.toList());
    }

    protected N getNodeOrCreate(T vertex) {
        N graphNode = adjacencyList.get(vertex);
        if (graphNode == null) {
            graphNode = createGraphNode(vertex);
            adjacencyList.put(vertex, graphNode);
        }
        return graphNode;
    }

    protected abstract N createGraphNode(T v);

    /**
     * Returns a string representation of the object. In general, the {@code toString} method returns
     * a string that "textually represents" this object. The result should be a concise but
     * informative representation that is easy for a person to read. It is recommended that all
     * subclasses override this method.
     *
     * <p>The {@code toString} method for class {@code Object} returns a string consisting of the name
     * of the class of which the object is an instance, the at-sign character `{@code @}', and the
     * unsigned hexadecimal representation of the hash code of the object. In other words, this method
     * returns a string equal to the value of:
     *
     * <blockquote>
     *
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre>
     *
     * </blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(vertexSize()).append(" vertices, ").append(edgeSize()).append(" edges\n");
        for (T v : vertex()) {
            stringBuilder.append(v).append(": ");
            for (T w : adj(v)) {
                stringBuilder.append(w).append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}

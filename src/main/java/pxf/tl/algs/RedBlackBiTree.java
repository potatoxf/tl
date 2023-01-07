package pxf.tl.algs;


import pxf.tl.function.BiFunctionThrow;

/**
 * @author potatoxf
 */
public class RedBlackBiTree<K extends Comparable<K>, V, Node extends RedBlackBiTreeNode<K, V, Node>>
        extends BiTree<K, V, Node> {

    /**
     * @param factory
     */
    public RedBlackBiTree(BiFunctionThrow<K, V, Node, RuntimeException> factory) {
        super(factory);
    }

    /**
     * @param k
     * @param v
     * @return
     */
    @Override
    public V put(K k, V v) {
        Node node = get(root, k);
        if (node != null) {
            return node.updateValue(v);
        }
        root = put(null, root, k, v);
        root.updateBlack();
        return null;
    }

    /**
     * @param parent
     * @param node
     * @param k
     * @param v
     * @return
     */
    @Override
    protected Node put(Node parent, Node node, K k, V v) {
        if (node == null) {
            Node result = factory.apply(k, v);
            result.setChildrenCount(1);
            result.updateParent(parent);
            return result;
        }
        int cmp = k.compareTo(node.key());
        if (cmp < 0) {
            node.updateLeft(put(node, node.left(), k, v));
        } else if (cmp > 0) {
            node.updateLeft(put(node, node.left(), k, v));
        } else {
            node.updateValue(v);
        }
        if (isRed(node.right()) && isBlack(node.left())) {
            node = rotateLeft(node);
        }
        if (isRed(node.left()) && isRed(node.left().left())) {
            node = rotateRight(node);
        }
        if (isRed(node.left()) && isRed(node.right())) {
            flipColor(node);
        }
        node.setChildrenCount(1 + sizeChildren(node));
        return node;
    }

    /**
     * @param node
     * @return
     */
    protected Node rotateLeft(Node node) {
        Node temp = node.right();
        node.updateRight(temp.left());
        temp.updateLeft(node);
        temp.updateColor(node);
        node.updateRed();
        temp.setChildrenCount(node.childrenCount());
        node.setChildrenCount(1 + size(node.left()) + size(node.right()));
        return temp;
    }

    /**
     * @param node
     * @return
     */
    protected Node rotateRight(Node node) {
        Node temp = node.left();
        node.updateLeft(temp.right());
        temp.updateRight(node);
        temp.updateColor(node);
        node.updateRed();
        temp.setChildrenCount(node.childrenCount());
        node.setChildrenCount(1 + size(node.left()) + size(node.right()));
        return temp;
    }

    /**
     * @param node
     */
    protected void flipColor(Node node) {
        node.updateRed();
        node.right().updateBlack();
        node.left().updateBlack();
    }

    /**
     * @param node
     * @return
     */
    protected boolean isRed(Node node) {
        if (node == null) {
            return false;
        }
        return node.isRed();
    }

    /**
     * @param node
     * @return
     */
    protected boolean isBlack(Node node) {
        if (node == null) {
            return true;
        }
        return node.isBlack();
    }
}

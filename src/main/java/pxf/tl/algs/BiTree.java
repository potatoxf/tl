package pxf.tl.algs;


import pxf.tl.api.Sized;
import pxf.tl.function.BiFunctionThrow;
import pxf.tl.function.PredicateThrow;

/**
 * 二叉树
 *
 * @param <K>
 * @param <V>
 * @param <Node>
 * @author potatoxf
 */
public class BiTree<K extends Comparable<K>, V, Node extends BiTreeNode<K, V, Node>> implements Sized {

    /**
     *
     */
    protected final BiFunctionThrow<K, V, Node, RuntimeException> factory;
    /**
     *
     */
    protected Node root;

    /**
     * @param factory
     */
    public BiTree(BiFunctionThrow<K, V, Node, RuntimeException> factory) {
        this.factory = factory;
    }

    /**
     * 元素个数
     *
     * @return 元素个数
     */
    @Override
    public int size() {
        return size(root);
    }

    /**
     * 结点元素个数
     *
     * @param node 节点
     * @return 元素个数
     */
    protected int size(Node node) {
        if (node == null) {
            return 0;
        }
        return node.childrenCount();
    }

    /**
     * @param node
     * @return
     */
    protected int sizeChildren(Node node) {
        int result = 0;
        Node left = node.left();
        if (left != null) {
            result += left.childrenCount();
        }
        Node right = node.right();
        if (right != null) {
            result += right.childrenCount();
        }
        return result;
    }

    /**
     * @param k
     * @return
     */
    public boolean containsKey(K k) {
        return get(root, k) != null;
    }

    /**
     * @param v
     * @return
     */
    public boolean containsValue(V v) {
        return findValue(root, v) != null;
    }

    /**
     * @param node
     * @param v
     * @return
     */
    private Node findValue(Node node, final V v) {
        return find(node, value -> v != null && v.equals(value.value()) || v == value.value());
    }

    /**
     * @param node
     * @param suspensionConditions
     * @return
     */
    private Node find(Node node, PredicateThrow<Node, RuntimeException> suspensionConditions) {
        if (node == null) {
            return null;
        }
        boolean isLeft = true;
        Node find = null;
        while (node != null) {
            if (suspensionConditions.test(node)) {
                find = node;
                break;
            }
            if (isLeft) {
                find = node.left();
            } else {
                find = node.right();
            }
            if (find != null) {
                node = find;
                isLeft = true;
            } else {
                if (isLeft) {
                    find = node.right();
                }
                if (find == null) {
                    node = node.parent();
                    isLeft = false;
                } else {
                    node = find;
                    isLeft = true;
                }
            }
        }
        return find;
    }

    /**
     * @param k
     * @return
     */
    public V get(K k) {
        Node node = get(root, k);
        if (node != null) {
            return node.value();
        }
        return null;
    }

    /**
     * @param node
     * @param k
     * @return
     */
    protected Node get(Node node, K k) {
        int cmp;
        while (node != null) {
            cmp = k.compareTo(node.key());
            if (cmp < 0) {
                node = node.left();
            } else if (cmp > 0) {
                node = node.right();
            } else {
                return node;
            }
        }
        return null;
    }

    /**
     * @param k
     * @param v
     * @return
     */
    public V put(K k, V v) {
        Node node = get(root, k);
        if (node != null) {
            return node.updateValue(v);
        }
        root = put(null, root, k, v);
        return null;
    }

    /**
     * @param parent
     * @param node
     * @param k
     * @param v
     * @return
     */
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
        node.setChildrenCount(1 + sizeChildren(node));
        return node;
    }

    /**
     * @param k
     * @return
     */
    public V select(int k) {
        Node node = select(root, k);
        if (node != null) {
            return node.value();
        }
        return null;
    }

    /**
     * @param node
     * @param k
     * @return
     */
    protected Node select(Node node, int k) {
        while (node != null) {
            int t = size(node.left());
            if (t < k) {
                node = node.right();
                k = k - 1 - t;
            } else if (t > k) {
                node = node.left();
            } else {
                return node;
            }
        }
        return null;
    }

    /**
     * @param k
     * @return
     */
    public int rank(K k) {
        return rank(root, k);
    }

    /**
     * @param node
     * @param k
     * @return
     */
    protected int rank(Node node, K k) {
        int result = 0;
        while (node != null) {
            int cmp = k.compareTo(node.key());
            if (cmp < 0) {
                node = node.left();
            } else if (cmp > 0) {
                node = node.right();
                result += 1 + size(node.left());
            } else {
                result += size(node.left());
            }
        }
        return result;
    }

    /**
     * @param k
     * @return
     */
    public K floor(K k) {
        Node node = floor(root, k);
        if (node != null) {
            return node.key();
        }
        return null;
    }

    /**
     * @param node
     * @param k
     * @return
     */
    protected Node floor(Node node, K k) {
        while (node != null) {
            int cmp = k.compareTo(node.key());
            if (cmp < 0) {
                node = node.left();
            } else if (cmp > 0) {
                Node temp = node.right();
                if (temp != null) {
                    return temp;
                } else {
                    return node;
                }
            } else {
                return node;
            }
        }
        return null;
    }

    /**
     * @param k
     * @return
     */
    public K ceiling(K k) {
        Node node = ceiling(root, k);
        if (node != null) {
            return node.key();
        }
        return null;
    }

    /**
     * @param node
     * @param k
     * @return
     */
    protected Node ceiling(Node node, K k) {
        while (node != null) {
            int cmp = k.compareTo(node.key());
            if (cmp > 0) {
                node = node.right();
            } else if (cmp < 0) {
                Node temp = node.left();
                if (temp != null) {
                    return temp;
                } else {
                    return node;
                }
            } else {
                return node;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public K min() {
        Node min = min(root);
        if (min != null) {
            return min.key();
        }
        return null;
    }

    /**
     * @param node
     * @return
     */
    protected Node min(Node node) {
        if (node == null) {
            return null;
        }
        int cmp;
        while (node.left() != null) {
            node = node.left();
        }
        return node;
    }

    /**
     * @return
     */
    public K max() {
        Node max = max(root);
        if (max != null) {
            return max.key();
        }
        return null;
    }

    /**
     * @param node
     * @return
     */
    protected Node max(Node node) {
        if (node == null) {
            return null;
        }
        int cmp;
        while (node.right() != null) {
            node = node.right();
        }
        return node;
    }

    /**
     * @return
     */
    public K deleteMinAndGetKey() {
        Node node = deleteMin(root);
        if (node != null) {
            return node.key();
        }
        return null;
    }

    /**
     * @return
     */
    public V deleteMinAndGetValue() {
        Node node = deleteMin(root);
        if (node != null) {
            return node.value();
        }
        return null;
    }

    /**
     * @param node
     * @return
     */
    protected Node deleteMin(Node node) {
        if (node == null) {
            return null;
        }
        while (node.left() != null) {
            node = node.left();
        }
        Node parent = node.parent();
        if (parent != null) {
            Node min = min(node.right());
            parent.updateLeft(min);
            if (min != null) {
                replaceNode(node, min);
            } else {
                clearReference(node);
            }
        }
        return node;
    }

    /**
     * @return
     */
    public K deleteMaxAndGetKey() {
        Node node = deleteMax(root);
        if (node != null) {
            return node.key();
        }
        return null;
    }

    /**
     * @return
     */
    public V deleteMaxAndGetValue() {
        Node node = deleteMax(root);
        if (node != null) {
            return node.value();
        }
        return null;
    }

    /**
     * @param node
     * @return
     */
    protected Node deleteMax(Node node) {
        if (node == null) {
            return null;
        }
        while (node.right() != null) {
            node = node.right();
        }
        Node parent = node.parent();
        if (parent != null) {
            Node max = max(node.left());
            parent.updateRight(max);
            if (max != null) {
                replaceNode(node, max);
            } else {
                clearReference(node);
            }
        }
        return node;
    }

    /**
     * @param k
     * @return
     */
    public V delete(K k) {
        Node node = delete(root, k);
        if (node != null) {
            return node.value();
        }
        return null;
    }

    /**
     * @param node
     * @param k
     * @return
     */
    protected Node delete(Node node, K k) {
        int cmp;
        while (node != null) {
            cmp = k.compareTo(node.key());
            if (cmp < 0) {
                node = node.left();
            } else if (cmp > 0) {
                node = node.right();
            } else {
                int s = size(node.right()) - size(node.left());
                if (s > 0) {
                    Node min = deleteMin(node.right());
                    if (min != null) {
                        replaceNode(node, min);
                    }
                } else {
                    Node max = deleteMax(node.left());
                    if (max != null) {
                        replaceNode(node, max);
                    }
                }
                return node;
            }
        }
        return null;
    }

    /**
     * @param replaced
     * @param target
     */
    protected void replaceNode(Node replaced, Node target) {
        Node parent = target.parent();
        target.updateParent(replaced.parent());
        target.updateLeft(replaced.left());
        target.updateRight(replaced.right());
        clearReference(replaced);
        updateSize(parent);
    }

    /**
     * @param node
     */
    protected void clearReference(Node node) {
        node.updateParent(null);
        node.updateLeft(null);
        node.updateRight(null);
    }

    /**
     * @param node
     */
    protected void updateSize(Node node) {
        while (node != null) {
            node.setChildrenCount(sizeChildren(node));
            node = node.parent();
        }
    }
}

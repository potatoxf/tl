package pxf.tl.algs;

import java.util.List;

/**
 * 树结点数据
 *
 * @param <Key>
 * @param <Node>
 */
public interface TreeTableNode<Key, Node extends TreeTableNode<Key, Node>> {
    /**
     * 获取当前Key
     *
     * @return {@code Key}
     */
    Key key();

    /**
     * 获取当前父级Key
     *
     * @return {@code Key}
     */
    Key parentKey();

    /**
     * 设置父节点引用
     *
     * @param children 孩子节点引用
     */
    default void setChildren(List<Node> children) {
    }

    /**
     * 设置父节点引用
     *
     * @param parent 父节点引用
     */
    default void setParent(Node parent) {
    }
}

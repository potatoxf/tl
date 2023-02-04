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
     * 获取孩子节点引用列表
     *
     * @return 孩子节点引用
     */
    default List<Node> getChildren() {
        return null;
    }

    /**
     * 设置孩子节点引用列表
     *
     * @param children 孩子节点引用
     */
    default void setChildren(List<Node> children) {
    }

    /**
     * 获取父节点引用
     *
     * @return 父节点引用
     */
    default Node getParent() {
        return null;
    }

    /**
     * 设置父节点引用
     *
     * @param parent 父节点引用
     */
    default void setParent(Node parent) {
    }
}

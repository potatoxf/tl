package pxf.tl.algs;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 二叉树树结点，对二叉树的数据结构的结点进行抽象
 *
 * @author potatoxf
 */
public abstract class BiTreeNode<K, V, N extends BiTreeNode<K, V, N>> extends TreeNode<K, V, N> {
    /**
     * 左节点
     */
    private final AtomicReference<N> left = new AtomicReference<>(null);

    /**
     * 右节点
     */
    private final AtomicReference<N> right = new AtomicReference<>(null);

    public BiTreeNode(K key, V value) {
        super(key, value);
    }

    /**
     * 返回左节点
     *
     * @return {@code N}左节点
     */
    public N left() {
        return left.getAcquire();
    }

    /**
     * 更新返回左节点
     *
     * @param left {@code N}左节点
     * @return {@code N}原来的左节点
     */
    public N updateLeft(N left) {
        return this.left.getAndSet(left);
    }

    /**
     * 返回右节点
     *
     * @return {@code N}右节点
     */
    public N right() {
        return right.getAcquire();
    }

    /**
     * 更新返回右节点
     *
     * @param right {@code N}右节点
     * @return {@code N}原来的右节点
     */
    public N updateRight(N right) {
        return this.right.getAndSet(right);
    }
}

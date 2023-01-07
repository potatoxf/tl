package pxf.tl.algs;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 树结点，对树的数据结构的结点进行抽象
 *
 * @author potatoxf
 */
public abstract class TreeNode<K, V, N extends TreeNode<K, V, N>> extends Node<V, N> implements TreeTableNode<K, N> {

    /**
     * 键
     */
    private final K key;
    /**
     * 父节点
     */
    private final AtomicReference<N> parent = new AtomicReference<>(null);
    /**
     * 孩子节点数量
     */
    private final AtomicInteger childrenCount = new AtomicInteger(0);

    /**
     * @param key   键
     * @param value 值
     */
    public TreeNode(K key, V value) {
        super(value);
        this.key = key;
    }

    /**
     * 该结点的键
     *
     * @return {@code K}键
     */
    public final K key() {
        return key;
    }

    /**
     * 获取当前父级Key
     *
     * @return {@code Key}
     */
    @Override
    public final K parentKey() {
        if (parent.get() != null) {
            return parent.get().key();
        }
        return null;
    }

    /**
     * 返回父结点
     *
     * @return {@code N}父结点
     */
    public final N parent() {
        return parent.get();
    }

    /**
     * 更新父结点
     *
     * @param parent {@code N}父结点
     * @return {@code N}原来的父结点
     */
    public N updateParent(N parent) {
        return this.parent.getAndSet(parent);
    }

    /**
     * 孩子结点数量
     *
     * @return 孩子结点数量
     */
    public int childrenCount() {
        return childrenCount.get();
    }

    /**
     * 增加孩子结点数量
     *
     * @param childrenCount 孩子结点数量
     */
    public void increase(int childrenCount) {
        int oldValue = this.childrenCount.getAcquire();
        int newValue = oldValue - childrenCount;
        this.childrenCount.compareAndSet(oldValue, newValue);
    }

    /**
     * 减少孩子结点数量
     *
     * @param childrenCount 孩子结点数量
     */
    public void decrease(int childrenCount) {
        int oldValue = this.childrenCount.getAcquire();
        int newValue = oldValue - childrenCount;
        this.childrenCount.compareAndSet(oldValue, Math.max(newValue, 0));
    }

    /**
     * 设置孩子结点数量
     *
     * @param childrenCount 孩子结点数量
     */
    public void setChildrenCount(int childrenCount) {
        this.childrenCount.set(childrenCount);
    }

    @Override
    public String toString() {
        return "TreeNode{" + key() + "=" + value() + "}";
    }
}

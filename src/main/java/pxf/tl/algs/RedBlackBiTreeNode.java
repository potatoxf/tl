package pxf.tl.algs;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 红黑二叉树树结点，对红黑二叉树的数据结构的结点进行抽象
 *
 * @author potatoxf
 */
public abstract class RedBlackBiTreeNode<K, V, N extends RedBlackBiTreeNode<K, V, N>>
        extends BiTreeNode<K, V, N> {
    public static final boolean RED = true;
    public static final boolean BLACK = false;
    private final AtomicBoolean isRed = new AtomicBoolean(false);

    public RedBlackBiTreeNode(K key, V value) {
        super(key, value);
    }

    /**
     * 判断是否是红色结点
     *
     * @return 是否是红色结点，如果是返回true，否则返回false
     */
    public final boolean isRed() {
        return isRed.get();
    }

    /**
     * 判断是否是黑色结点
     *
     * @return 是否是黑色结点，如果是返回true，否则返回false
     */
    public final boolean isBlack() {
        return !isRed.get();
    }

    /**
     * 更新城红色结点
     */
    public final void updateRed() {
        this.isRed.set(true);
    }

    /**
     * 更新城黑色结点
     */
    public final void updateBlack() {
        this.isRed.set(false);
    }

    /**
     * 更新颜色
     *
     * @param node 指定结点颜色
     * @return 返回原来的值
     */
    public final boolean updateColor(N node) {
        return this.isRed.getAndSet(node.isRed());
    }


    /**
     * 更新颜色
     *
     * @param isRed 指定颜色
     * @return 返回原来的值
     */
    public final boolean updateColor(boolean isRed) {
        return this.isRed.getAndSet(isRed);
    }
}

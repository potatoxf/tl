package pxf.tl.algs;


import pxf.tl.help.Whether;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 算法工具类
 *
 * @author potatoxf
 */
public final class AlgHelper {

    private AlgHelper() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    /**
     * 构建树型结构数据
     *
     * @param data 树型数据集合
     * @param pid  指定父级节点ID
     * @param <I>  ID类型
     * @param <T>  数据类型
     * @return 返回指定PID孩子节点数据列表
     */
    public static <I, T extends TreeTableNode<I, T>> List<T> buildTreeNodeData(Collection<T> data, I pid) {
        return buildTree(data, pid, TreeTableNode::key, TreeTableNode::parentKey, TreeTableNode::setChildren, TreeTableNode::setParent);
    }

    /**
     * 构建树型结构数据
     *
     * @param data           树型数据集合
     * @param pid            指定父级节点ID
     * @param idGetter       ID获取器
     * @param pidGetter      父级ID获取器
     * @param childrenSetter 孩子节点设置器
     * @param parentSetter   父级节点设置器
     * @param <I>            ID类型
     * @param <T>            数据类型
     * @return 返回指定PID孩子节点数据列表
     */
    public static <I, T> List<T> buildTree(
            Collection<T> data,
            I pid,
            Function<T, I> idGetter,
            Function<T, I> pidGetter,
            BiConsumer<T, List<T>> childrenSetter,
            BiConsumer<T, T> parentSetter) {
        Map<I, List<T>> parentChildrenMap =
                data.stream().filter(Whether::noNvl).collect(Collectors.groupingBy(pidGetter));
        List<T> rootList = parentChildrenMap.get(pid);
        if (Whether.noEmpty(rootList)) {
            Queue<T> queue = new LinkedList<>(rootList);
            while (!Whether.empty(queue)) {
                T parent = queue.poll();
                I id = idGetter.apply(parent);
                // 找到当前孩子
                List<T> children = parentChildrenMap.get(id);
                if (Whether.noEmpty(children)) {
                    childrenSetter.accept(parent, children);
                    if (parentSetter != null) {
                        children.forEach(e -> parentSetter.accept(e, parent));
                    }
                    queue.addAll(children);
                }
            }
            return rootList;
        }
        return Collections.emptyList();
    }

    /**
     * 找到树的节点
     *
     * @param data           树型数据
     * @param id             指定节点ID
     * @param idGetter       ID获取器
     * @param childrenGetter 孩子节点获取器
     * @param <I>            ID类型
     * @param <T>            数据类型
     * @return 返回树节点数据
     */
    public static <I, T> T findTreeNode(
            Collection<T> data, I id, Function<T, I> idGetter, Function<T, Collection<T>> childrenGetter) {
        if (Whether.noEmpty(data)) {
            for (T datum : data) {
                I i = idGetter.apply(datum);
                if ((id == null && i == null) || i.equals(id)) {
                    return datum;
                } else {
                    T subTreeRoot =
                            findTreeNode(childrenGetter.apply(datum), id, idGetter, childrenGetter);
                    if (subTreeRoot != null) {
                        return subTreeRoot;
                    }
                }
            }
        }
        return null;
    }

}

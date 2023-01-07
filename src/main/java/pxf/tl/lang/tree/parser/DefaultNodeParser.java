package pxf.tl.lang.tree.parser;


import pxf.tl.algs.tree.Tree;
import pxf.tl.help.Whether;
import pxf.tl.lang.tree.TreeNode;

import java.util.Map;

/**
 * 默认的简单转换器
 *
 * @param <T> ID类型
 * @author potatoxf
 */
public class DefaultNodeParser<T> implements NodeParser<TreeNode<T>, T> {

    @Override
    public void parse(TreeNode<T> treeNode, Tree<T> tree) {
        tree.setId(treeNode.getId());
        tree.setParentId(treeNode.getParentId());
        tree.setWeight(treeNode.getWeight());
        tree.setName(treeNode.getName());

        // 扩展字段
        final Map<String, Object> extra = treeNode.getExtra();
        if (Whether.noEmpty(extra)) {
            extra.forEach(tree::putExtra);
        }
    }
}

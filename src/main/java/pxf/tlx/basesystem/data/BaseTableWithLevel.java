package pxf.tlx.basesystem.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tl.algs.TreeTableNode;

import java.io.Serializable;
import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.util.List;

/**
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class BaseTableWithLevel<Id extends Serializable & Comparable<Id> & Constable & ConstantDesc,
        Entity extends BaseTableWithLevel<Id, Entity>> extends BaseTable<Id, Entity> implements TreeTableNode<Id, Entity> {
    @ApiModelProperty(value = "父级ID")
    private Id pid;
    @ApiModelProperty(value = "子类实体")
    private List<Entity> children;

    /**
     * 获取当前Key
     *
     * @return {@code Key}
     */
    @Override
    public Id key() {
        return getId();
    }

    /**
     * 获取当前父级Key
     *
     * @return {@code Key}
     */
    @Override
    public Id parentKey() {
        return getPid();
    }

    /**
     * 设置父节点引用
     *
     * @param children 孩子节点引用
     */
    @Override
    public void setChildren(List<Entity> children) {
        this.children = children;
    }
}

package pxf.tlx.basesystem.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tl.api.This;

import java.io.Serializable;
import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;

/**
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class BaseSafeTable<Id extends Serializable & Comparable<Id> & Constable & ConstantDesc,
        Entity extends BaseSafeTable<Id, Entity>>
        extends BaseTable<Id, Entity>
        implements This<Entity>, Serializable {
    @ApiModelProperty(value = "删除标志,默认0，未删除，1已删除")
    private boolean deleteFlag;
    @ApiModelProperty(value = "版本，默认0")
    private long revision;
    @ApiModelProperty(value = "创建者")
    private String createdBy;
    @ApiModelProperty(value = "更新者")
    private String updatedBy;
}

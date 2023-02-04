package pxf.tlx.basesystem.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tl.api.This;
import pxf.tlx.mybatis.AutoFill;
import pxf.tlx.mybatis.TimeAutoFillValueHandler;

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
public abstract class BaseTable<Id extends Serializable & Comparable<Id> & Constable & ConstantDesc,
        Entity extends BaseTable<Id, Entity>>
        extends BaseId<Id, Entity>
        implements This<Entity>, Serializable {
    @AutoFill(value = TimeAutoFillValueHandler.class, isUpdate = false)
    @ApiModelProperty(value = "创建时间")
    private Long createdTime;
    @AutoFill(value = TimeAutoFillValueHandler.class)
    @ApiModelProperty(value = "更新时间")
    private Long updatedTime;
}

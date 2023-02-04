package pxf.tlx.basesystem.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import pxf.tl.api.This;
import pxf.tl.util.ToolBytecode;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;

/**
 * @author potatoxf
 */
@SuperBuilder
@ToString
@NoArgsConstructor
public abstract class BaseId<Id extends Serializable & Comparable<Id> & Constable & ConstantDesc,
        Entity extends BaseId<Id, Entity>> implements This<Entity>, Serializable {
    @ApiModelProperty(value = "ID")
    private Id id;

    @Nonnull
    @SuppressWarnings("unchecked")
    public final Class<Id> getIdType() {
        return (Class<Id>) ToolBytecode.extractGenericClass(thisClass$(), BaseTable.class, 0);
    }

    public boolean supportId() {
        return true;
    }

    public Id getId() {
        if (!supportId()) {
            throw new UnsupportedOperationException("Not support Id column");
        }
        return id;
    }

    public void setId(Id id) {
        if (!supportId()) {
            throw new UnsupportedOperationException("Not support Id column");
        }
        this.id = id;
    }
}

package pxf.tlx.db.base.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.db.base.BaseTable;

/**
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("组织访问关系")
public class OrgAccess extends BaseTable<Integer, OrgAccess> {
    @ApiModelProperty(value = "菜单ID")
    private Integer menuId;
}

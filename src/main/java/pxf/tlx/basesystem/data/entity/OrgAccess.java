package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.data.BaseTable;

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

    @ApiModelProperty(value = "菜单权限，查询")
    private Boolean menuSelect;

    @ApiModelProperty(value = "菜单权限，删除")
    private Boolean menuDelete;

    @ApiModelProperty(value = "菜单权限，新增")
    private Boolean menuInsert;

    @ApiModelProperty(value = "菜单权限，修改")
    private Boolean menuUpdate;

    @ApiModelProperty(value = "菜单权限，报表数据")
    private Boolean menuReport;
}

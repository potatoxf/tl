package pxf.tlx.db.base.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.db.base.BaseTableWithLevel;

/**
 * 系统菜单
 *
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("系统菜单")
public class AccessMenu extends BaseTableWithLevel<Integer, AccessMenu> {
    @ApiModelProperty(value = "链接ID")
    private Integer linkId;
    @ApiModelProperty(value = "菜单名称")
    private String designation;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "顺序")
    private int ord;
    @ApiModelProperty(value = "描述")
    private String desc;

    private AccessLink accessLink;
}

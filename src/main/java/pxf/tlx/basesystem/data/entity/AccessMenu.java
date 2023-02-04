package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.data.BaseTableWithLevel;

import java.util.Set;

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
    @ApiModelProperty(value = "菜单名称")
    private String designation;
    @ApiModelProperty(value = "菜单标题")
    private String title;
    @ApiModelProperty(value = "访问路径")
    private String way;
    @ApiModelProperty(value = "重定向路径")
    private String redirect;
    @ApiModelProperty(value = "权限键")
    private Set<String> permission;
    @ApiModelProperty(value = "页面链接ID")
    private Integer linkId;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "顺序")
    private int ord;
    @ApiModelProperty(value = "描述")
    private String desc;

    private AccessLink accessLink;
}

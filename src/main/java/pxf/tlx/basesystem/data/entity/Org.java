package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.data.BaseTableWithLevel;

/**
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("组织")
public class Org extends BaseTableWithLevel<Integer, Org> {
    @ApiModelProperty(value = "权限键")
    private String perm;
    @ApiModelProperty(value = "名称")
    private String designation;
    @ApiModelProperty(value = "类型,1是单位，2是部门，4是角色，8小组,16是人员")
    private Integer category;
    @ApiModelProperty(value = "账户ID")
    private String accountId;
}

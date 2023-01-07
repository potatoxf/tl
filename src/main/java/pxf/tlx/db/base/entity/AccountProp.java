package pxf.tlx.db.base.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.db.base.BaseId;

/**
 * 账户属性
 *
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("账户属性")
public class AccountProp extends BaseId<String, AccountProp> {
    @ApiModelProperty(value = "分类")
    private String catalog;
    @ApiModelProperty(value = "名称")
    private String designation;
    @ApiModelProperty(value = "值")
    private String valueData;
}

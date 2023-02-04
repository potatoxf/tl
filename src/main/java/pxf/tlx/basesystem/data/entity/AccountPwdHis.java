package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.data.BaseTable;

/**
 * 账户密码历史
 *
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("账户密码历史")
public class AccountPwdHis extends BaseTable<String, AccountPwdHis> {
    @ApiModelProperty(value = "密码")
    private String pwd;
}

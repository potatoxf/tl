package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.data.BaseSafeTableWithLevel;

import java.util.List;

/**
 * 账户
 *
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("账户")
public class Account extends BaseSafeTableWithLevel<String, Account> {
    @ApiModelProperty(value = "用户名")
    private String token;
    @ApiModelProperty(value = "密码")
    private String pwd;
    @ApiModelProperty(value = "名称")
    private String nickname;
    @ApiModelProperty(value = "全拼")
    private String fpy;
    @ApiModelProperty(value = "简拼")
    private String spy;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "手机号码")
    private String phone;
    @ApiModelProperty(value = "错误次数")
    private Boolean error;
    @ApiModelProperty(value = "账户是否锁定")
    private Boolean locked;
    @ApiModelProperty(value = "状态标识")
    private int statusFlag;
    private List<AccountProp> accountPropList;
}

package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.data.BaseTable;

/**
 * 账户登录日志
 *
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("账户登录日志")
public class AccountConLog extends BaseTable<String, AccountConLog> {
    @ApiModelProperty(value = "ip地址，ipv4或ipv6")
    private String ip;
    @ApiModelProperty(value = "登录设备")
    private String machine;
    @ApiModelProperty(value = "代理设备")
    private String agent;
    @ApiModelProperty(value = "方式，0=WEB,1=手机,2=电脑")
    private Integer methods;
}

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
@ApiModel("账户操作日志")
public class AccountOpLog extends BaseTable<String, AccountOpLog> {
    @ApiModelProperty(value = "ip地址，ipv4或ipv6")
    private String ip;

    @ApiModelProperty(value = "访问路径")
    private String path;

    @ApiModelProperty(value = "请求体")
    private String requestBody;

    @ApiModelProperty(value = "响应体")
    private String responseBody;

    @ApiModelProperty(value = "是否成功；0=失败，1=成功；")
    private Boolean success;

    @ApiModelProperty(value = "异常信息")
    private String exception;
}

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
@ApiModel("CORS匹配设置")
public class SystemCors extends BaseTable<Integer, SystemCors> {

    @ApiModelProperty(value = "匹配模式")
    private String pattern;

    @ApiModelProperty(value = "Access-Control-Allow-Origin")
    private String origin;

    @ApiModelProperty(value = "Access-Control-Allow-Methods")
    private String methods;

    @ApiModelProperty(value = "Access-Control-Allow-Headers")
    private String headers;

    @ApiModelProperty(value = "Access-Control-Allow-Credentials")
    private boolean credentials;

    @ApiModelProperty(value = "Access-Control-Allow-Max-Age")
    private Long maxAge;
}

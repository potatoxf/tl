package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.data.BaseTable;

/**
 * 系统枚举
 *
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("系统枚举")
public class SystemEnum extends BaseTable<Integer, SystemEnum> {
    @ApiModelProperty(value = "模块")
    private String module;
    @ApiModelProperty(value = "类别")
    private String catalog;
    @ApiModelProperty(value = "识别符")
    private String token;
    @ApiModelProperty(value = "名称")
    private String designation;
    @ApiModelProperty(value = "整型值")
    private Integer integerValue;
    @ApiModelProperty(value = "字符串值")
    private String stringValue;
    @ApiModelProperty(value = "描述")
    private String notes;
    @ApiModelProperty(value = "顺序")
    private int ord;
}

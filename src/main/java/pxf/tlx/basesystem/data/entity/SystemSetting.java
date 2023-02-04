package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.data.BaseTable;

/**
 * 系统设置
 *
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("系统设置")
public class SystemSetting extends BaseTable<Integer, SystemSetting> {
    @ApiModelProperty(value = "模块")
    private String module;
    @ApiModelProperty(value = "名称")
    private String designation;
    @ApiModelProperty(value = "值数据")
    private String valueData;
    @ApiModelProperty(value = "值类型")
    private Integer valueType;
    @ApiModelProperty(value = "描述")
    private String notes;
}

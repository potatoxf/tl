package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.data.BaseTable;

/**
 * 系统目录
 *
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("系统文件")
public class SystemDir extends BaseTable<Integer, SystemDir> {
    @ApiModelProperty(value = "盘符")
    private String driver;
    @ApiModelProperty(value = "路径")
    private String path;
    @ApiModelProperty(value = "是否锁定")
    private boolean locked;
    @ApiModelProperty(value = "大小")
    private double size;
    @ApiModelProperty(value = "描述")
    private String notes;
}

package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.data.BaseTable;

/**
 * 系统文件
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
public class SystemFile extends BaseTable<String, SystemFile> {
    @ApiModelProperty(value = "目录ID")
    private Integer dirId;
    @ApiModelProperty(value = "文件路径")
    private String path;
    @ApiModelProperty(value = "文件名称")
    private String designation;
    @ApiModelProperty(value = "文件类型")
    private String suffix;
    @ApiModelProperty(value = "文件大小")
    private double size;
    @ApiModelProperty(value = "文件描述")
    private String notes;
}

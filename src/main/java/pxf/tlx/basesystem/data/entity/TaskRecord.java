package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.TaskStatus;
import pxf.tlx.basesystem.data.BaseTable;

/**
 * 任务记录
 *
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("任务记录")
public class TaskRecord extends BaseTable<Integer, SystemSetting> {
    @ApiModelProperty(value="任务编号")
    private String taskNo;
    @ApiModelProperty(value="执行时间格式值")
    private String timeKey;
    @ApiModelProperty(value="任务状态")
    private TaskStatus status;
    @ApiModelProperty(value="失败统计数")
    private Integer failCount;
    @ApiModelProperty(value="失败错误描述")
    private String failReason;
    @ApiModelProperty(value="执行时间")
    private Long executeTime;
}

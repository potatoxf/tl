package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.TaskStatus;
import pxf.tlx.basesystem.data.BaseTable;

/**
 * 任务信息
 *
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("任务信息")
public class TaskInformation extends BaseTable<Integer, SystemSetting> {
    @ApiModelProperty(value="任务编号")
    private String taskNo;
    @ApiModelProperty(value="任务名称")
    private String taskName;
    @ApiModelProperty(value="定时规则表达式")
    private String schedulerRule;
    @ApiModelProperty(value="执行方")
    private String executorNo;
    @ApiModelProperty(value="发送方式")
    private Integer sendType;
    @ApiModelProperty(value="请求地址")
    private String url;
    @ApiModelProperty(value="执行参数")
    private String executeParameter;
    @ApiModelProperty(value="执行时间格式值")
    private String timeKey;
    @ApiModelProperty(value="冻结时间")
    private Long frozenTime;
    @ApiModelProperty(value="解冻时间")
    private Long unfrozenTime;
    @ApiModelProperty(value="状态")
    private TaskStatus status;
    @ApiModelProperty(value="版本号：需要乐观锁控制")
    private Integer version;
}

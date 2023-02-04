package pxf.tlx.basesystem.data.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pxf.tlx.basesystem.data.BaseTable;

/**
 * 任务错误
 *
 * @author potatoxf
 */
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("任务错误")
public class TaskError extends BaseTable<Integer, SystemSetting> {
    @ApiModelProperty(value="任务执行记录Id")
    private Integer taskRecordId;
    @ApiModelProperty(value="信息关键字")
    private String errorKey;
    @ApiModelProperty(value="信息内容")
    private String errorValue;
}

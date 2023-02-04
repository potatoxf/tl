package pxf.tlx.basesystem.action;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tlx.basesystem.TaskStatus;
import pxf.tlx.basesystem.data.entity.TaskError;
import pxf.tlx.basesystem.data.entity.TaskInformation;
import pxf.tlx.basesystem.data.entity.TaskRecord;
import pxf.tlx.basesystem.data.mapper.TaskMapper;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author potatoxf
 */
@Getter
public final class TaskAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAction.class);
    private final TaskMapper taskMapper;

    public TaskAction(@Nonnull TaskMapper taskMapper) {
        this.taskMapper = Objects.requireNonNull(taskMapper);
    }

    public List<TaskInformation> pageTaskList() {
        Map<String, Object> map = new HashMap<>();
        return taskMapper.lookupTaskInformation(map);
    }

    public TaskRecord addTaskRecords(@Nonnull String taskNo) {
        TaskRecord taskRecord;
        TaskInformation taskInformation = taskMapper.getTaskInformationByTaskNo(taskNo);
        if (null == taskInformation
                || TaskStatus.TASK_FROZEN == taskInformation.getStatus()) {
            return null;
        }
        long currentTimeMillis = System.currentTimeMillis();
        TaskInformation task = new TaskInformation();
        task.setId(taskInformation.getId());
        task.setUpdatedTime(currentTimeMillis);
        updateTask(task);
        LOGGER.info(
                "taskNo={},taskName={}更新最后修改时间成功",
                taskInformation.getTaskNo(),
                taskInformation.getTaskName());

        taskRecord = new TaskRecord();
        taskRecord.setTaskNo(taskNo);
        taskRecord.setTimeKey(taskInformation.getTimeKey());
        taskRecord.setExecuteTime(currentTimeMillis);
        taskRecord.setStatus(TaskStatus.TASK_INIT);
        taskRecord.setFailCount(0);
        taskRecord.setFailReason("");
        taskRecord.setCreatedTime(currentTimeMillis);
        taskRecord.setUpdatedTime(currentTimeMillis);
        taskMapper.addTaskRecord(taskRecord);
        return taskRecord;
    }

    public boolean addTask(TaskInformation taskInformation) {
        String taskNo = taskInformation.getTaskNo();
        taskInformation.setVersion(0);
        int count = taskMapper.countTaskInformationByTaskNo(taskNo);
        // 判断是否重复任务编号
        if (count > 0) {
            return false;
        }
        int insert = taskMapper.addTaskInformation(taskInformation);
        return insert >= 1;
    }

    public void addTaskError(Integer id, String errorKey, String errorValue) {
        TaskError taskError = new TaskError();
        taskError.setTaskRecordId(id);
        taskError.setErrorKey(errorKey);
        taskError.setErrorValue(errorValue);
        taskMapper.addTaskError(taskError);
    }

    public TaskStatus updateTask(TaskInformation taskInformation) {
        int count = taskMapper.countTaskInformationByTaskNo(taskInformation.getTaskNo());
        // 判断是否重复任务编号
        if (count >= 2) {
            return TaskStatus.TASK_NO_EXIST;
        }
        TaskStatus taskStatus = taskInformation.getStatus();
        // 设置解冻时间或冻结时间及最后修改时间
        if (TaskStatus.TASK_FROZEN == taskStatus) {
            taskInformation.setFrozenTime(System.currentTimeMillis());
        } else if (TaskStatus.TASK_UNFROZEN == taskStatus) {
            taskInformation.setUnfrozenTime(System.currentTimeMillis());
        }
        taskInformation.setUpdatedTime(System.currentTimeMillis());
        int updateCount = taskMapper.modifyTaskInformationByIdSelective(taskInformation);
        // 乐观锁控制并发修改
        if (updateCount < 1) {
            return TaskStatus.TASK_UPDATE_FAIL;
        }
        return TaskStatus.TASK_OK;
    }

    public void updateRecordById(Integer count, Integer id) {
        TaskRecord records = new TaskRecord();
        records.setId(id);
        records.setFailCount(count);
        records.setUpdatedTime(System.currentTimeMillis());
        if (count > 0) {
            records.setStatus(TaskStatus.TASK_FAIL);
        } else {
            records.setStatus(TaskStatus.TASK_OK);
        }
        taskMapper.modifyTaskRecordByIdSelective(records);
    }

}

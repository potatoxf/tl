package pxf.tlx.basesystem.data.mapper;

import org.apache.ibatis.annotations.Mapper;
import pxf.tlx.basesystem.TaskStatus;
import pxf.tlx.basesystem.data.entity.TaskError;
import pxf.tlx.basesystem.data.entity.TaskInformation;
import pxf.tlx.basesystem.data.entity.TaskRecord;

import java.util.List;
import java.util.Map;
/**
 * @author potatoxf
 */
@Mapper
public interface TaskMapper {

    //------------------------------------------------------------------------------------------------------------------
    //TaskError
    //------------------------------------------------------------------------------------------------------------------

    TaskError getTaskErrorByRecordId(String recordId);

    TaskError getTaskErrorById(Integer id);

    int removeTaskErrorById(Integer id);

    int addTaskError(TaskError record);

    int addTaskErrorSelective(TaskError record);

    int modifyTaskErrorById(TaskError record);

    int modifyTaskErrorByIdSelective(TaskError record);

    //------------------------------------------------------------------------------------------------------------------
    //TaskInformation
    //------------------------------------------------------------------------------------------------------------------

    List<TaskInformation> lookupTaskInformation(Map<String, Object> map);

    List<TaskInformation> listTaskInformationByStatus(TaskStatus status);

    int countTaskInformationByTaskNo(String taskNo);

    TaskInformation getTaskInformationByTaskNo(String taskNo);

    TaskInformation getTaskInformationById(Integer id);

    int removeTaskInformationById(Integer id);

    int addTaskInformation(TaskInformation record);

    int addTaskInformationSelective(TaskInformation record);

    int modifyTaskInformationByIdSelective(TaskInformation record);

    int modifyTaskInformationById(TaskInformation record);

    //------------------------------------------------------------------------------------------------------------------
    //TaskRecord
    //------------------------------------------------------------------------------------------------------------------

    List<TaskRecord> listTaskRecordByTaskNo(String taskNo);

    TaskRecord getTaskRecordById(Integer id);

    int removeTaskRecordById(Integer id);

    int addTaskRecord(TaskRecord record);

    int addTaskRecordSelective(TaskRecord record);

    int modifyTaskRecordById(TaskRecord record);

    int modifyTaskRecordByIdSelective(TaskRecord record);
}

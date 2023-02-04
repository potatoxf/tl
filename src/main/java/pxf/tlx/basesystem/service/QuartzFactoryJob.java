package pxf.tlx.basesystem.service;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.help.Safe;
import pxf.tl.util.ToolException;
import pxf.tlx.basesystem.TaskStatus;
import pxf.tlx.basesystem.action.TaskAction;
import pxf.tlx.basesystem.data.entity.TaskInformation;
import pxf.tlx.basesystem.data.entity.TaskRecord;
import pxf.tlx.spring.configuration.SpringConfiguration;

import java.util.concurrent.atomic.AtomicInteger;

@DisallowConcurrentExecution
public class QuartzFactoryJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(QuartzFactoryJob.class);

    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        atomicInteger.set(0);

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String id = jobDataMap.getString("id");
        String taskNo = jobDataMap.getString("taskNo");
        String executorNo = jobDataMap.getString("executorNo");
        String sendType = jobDataMap.getString("sendType");
        String url = jobDataMap.getString("url");
        String executeParameter = jobDataMap.getString("executeParameter");
        logger.info(
                "定时任务被执行:taskNo={},executorNo={},sendType={},url={},executeParameter={}",
                taskNo,
                executorNo,
                sendType,
                url,
                executeParameter);
        TaskAction taskAction =
                (TaskAction) SpringConfiguration.getBean("taskAction");
        QuartzService quartzService =
                (QuartzServiceImpl) SpringConfiguration.getBean("quartzServiceImpl");
        TaskRecord records = null;
        try {
            // 保存定时任务的执行记录
            records = taskAction.addTaskRecords(taskNo);
            if (null == records || TaskStatus.TASK_INIT != records.getStatus()) {
                logger.info("taskNo={}保存执行记录失败", taskNo);
                return;
            }

        } catch (Exception ex) {
            atomicInteger.incrementAndGet();
            taskAction.addTaskError(records.getId(),
                    taskNo + ":" + ex.getMessage(),
                    ToolException.getStackTraceMessage(ex));
        }

        taskAction.updateRecordById(atomicInteger.get(), records.getId());
        TaskInformation taskInformation = new TaskInformation();
        taskInformation.setId(Safe.toInteger(id, -1));
        taskInformation.setUpdatedTime(System.currentTimeMillis());
        taskAction.updateTask(taskInformation);
    }
}

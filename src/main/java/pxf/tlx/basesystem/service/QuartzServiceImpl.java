package pxf.tlx.basesystem.service;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import pxf.tl.util.ToolException;
import pxf.tlx.basesystem.TaskStatus;
import pxf.tlx.basesystem.action.TaskAction;
import pxf.tlx.basesystem.data.entity.TaskInformation;
import pxf.tlx.basesystem.data.entity.TaskRecord;
import pxf.tlx.basesystem.data.mapper.TaskMapper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class QuartzServiceImpl implements QuartzService, InitializingBean {

    public static final String QUARTZ_TOPIC = "quartztopic";
    private static final Logger logger = LoggerFactory.getLogger(QuartzServiceImpl.class);
    private AtomicInteger atomicInteger;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskAction taskAction;

    @Autowired
    private Scheduler scheduler;


    /**
     * 启动 or 暂停定时任务
     */
    @Override
    public TaskStatus startTask(String taskNo) throws SchedulerException {
        TaskInformation taskInformation = taskMapper.getTaskInformationByTaskNo(taskNo);
        if (taskInformation == null) {
            return TaskStatus.TASK_NO_DATA;
        }
        TaskStatus status = taskInformation.getStatus();
        long currentTimeMillis = System.currentTimeMillis();
        TaskInformation task = new TaskInformation();
        task.setId(taskInformation.getId());
        task.setVersion(taskInformation.getVersion());
        // 说明要暂停
        if (TaskStatus.TASK_UNFROZEN == status) {
            scheduler.deleteJob(new JobKey(taskNo));
            task.setFrozenTime(currentTimeMillis);
            task.setStatus(TaskStatus.TASK_FROZEN);
            // 说明要启动
        } else if (TaskStatus.TASK_FROZEN == status) {
            scheduler.deleteJob(new JobKey(taskNo));
            this.schedule(taskInformation, scheduler);
            task.setUnfrozenTime(currentTimeMillis);
            task.setStatus(TaskStatus.TASK_UNFROZEN);
        }
        task.setUpdatedTime(currentTimeMillis);
        taskMapper.modifyTaskInformationByIdSelective(task);
        return TaskStatus.TASK_OK;
    }

    /**
     * 初始化加载定时任务
     */
    @Override
    public void initTask() {
        List<TaskInformation> unfrozenTasks =
                taskMapper.listTaskInformationByStatus(TaskStatus.TASK_UNFROZEN);
        if (unfrozenTasks == null || unfrozenTasks.isEmpty()) {
            logger.info("没有需要初始化加载的定时任务");
            return;
        }
        for (TaskInformation unfrozenTask : unfrozenTasks) {
            try {
                this.schedule(unfrozenTask, scheduler);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 初始化加载定时任务
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.initTask();
    }


    public void schedule(TaskInformation quartzTaskInfo, Scheduler scheduler)
            throws SchedulerException {
        TriggerKey triggerKey =
                TriggerKey.triggerKey(quartzTaskInfo.getTaskNo(), Scheduler.DEFAULT_GROUP);
        JobDetail jobDetail =
                JobBuilder.newJob(QuartzFactoryJob.class)
                        .withDescription(quartzTaskInfo.getTaskName())
                        .withIdentity(quartzTaskInfo.getTaskNo(), Scheduler.DEFAULT_GROUP)
                        .build();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("id", quartzTaskInfo.getId().toString());
        jobDataMap.put("taskNo", quartzTaskInfo.getTaskNo());
        jobDataMap.put("executorNo", quartzTaskInfo.getExecutorNo());
        jobDataMap.put("sendType", quartzTaskInfo.getSendType());
        jobDataMap.put("url", quartzTaskInfo.getUrl());
        jobDataMap.put("executeParameter", quartzTaskInfo.getExecuteParameter());
        CronScheduleBuilder cronScheduleBuilder =
                CronScheduleBuilder.cronSchedule(quartzTaskInfo.getSchedulerRule());
        CronTrigger cronTrigger =
                TriggerBuilder.newTrigger()
                        .withDescription(quartzTaskInfo.getTaskName())
                        .withIdentity(triggerKey)
                        .withSchedule(cronScheduleBuilder)
                        .build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
        logger.info(
                "taskNo={},taskName={},scheduleRule={} load to quartz success!",
                quartzTaskInfo.getTaskNo(),
                quartzTaskInfo.getTaskName(),
                quartzTaskInfo.getSchedulerRule());
    }

    /**
     * 立即运行一次定时任务
     */
    @Override
    public TaskStatus runTaskNow(String taskNo) {
        atomicInteger = new AtomicInteger(0);
        TaskInformation taskInformations = taskMapper.getTaskInformationByTaskNo(taskNo);
        if (taskInformations == null) {
            return TaskStatus.TASK_NO_DATA;
        }
        Integer id = taskInformations.getId();
        Integer sendType = taskInformations.getSendType();
        String executorNo = taskInformations.getExecutorNo();
        String url = taskInformations.getUrl();
        String executeParameter = taskInformations.getExecuteParameter();
        logger.info(
                "定时任务被执行:taskNo={},executorNo={},sendType={},url={},executeParameter={}",
                taskNo,
                executorNo,
                sendType,
                url,
                executeParameter);
        TaskRecord records = null;
        try {
            // 保存定时任务的执行记录
            records = taskAction.addTaskRecords(taskNo);
            if (null == records || TaskStatus.TASK_INIT != records.getStatus()) {
                logger.info("taskNo={}立即运行失--->>保存执行记录失败", taskNo);
                return TaskStatus.TASK_FAIL;
            }

        } catch (Exception ex) {
            logger.error("");
            atomicInteger.incrementAndGet();
            taskAction.addTaskError(records.getId(),
                    taskNo + ":" + ex.getMessage(),
                    ToolException.getStackTraceMessage(ex));
        }
        // 更改record表的执行状态、最后修改时间、失败个数
        taskAction.updateRecordById(atomicInteger.get(), records.getId());

        // 更新taskinfo表的最后修改时间
        TaskInformation taskInformation = new TaskInformation();
        taskInformation.setId(id);
        taskInformation.setUpdatedTime(System.currentTimeMillis());
        taskAction.updateTask(taskInformation);
        return TaskStatus.TASK_OK;
    }
}

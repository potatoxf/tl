package pxf.tlx.basesystem.service;

import org.quartz.SchedulerException;
import pxf.tlx.basesystem.TaskStatus;

public interface QuartzService {

    TaskStatus startTask(String taskNo) throws SchedulerException;

    void initTask();

    TaskStatus runTaskNow(String taskNo);
}

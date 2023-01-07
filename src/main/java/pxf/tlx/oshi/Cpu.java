package pxf.tlx.oshi;

import lombok.Data;
import oshi.hardware.CentralProcessor;
import pxf.tl.util.ToolThread;

/**
 * CPU负载信息
 *
 * @author potatoxf
 */
@Data
public class Cpu {
    /**
     * CPU核心数
     */
    final int coreNum;
    /**
     * CPU型号信息
     */
    final String model;
    final long nice;
    final long irq;
    final long softIrq;
    final long steal;
    /**
     * CPU系统使用率
     */
    final long sys;
    /**
     * CPU用户使用率
     */
    final long user;
    /**
     * CPU当前等待率
     */
    final long wait;
    /**
     * CPU当前空闲率
     */
    final long free;
    /**
     * 获取CPU总的使用率
     */
    final long total;
    /**
     * 获取用户+系统的总的CPU使用率
     */
    final long used;

    /**
     * 构造，等待时间为用于计算在一定时长内的CPU负载情况，如传入1000表示最近1秒的负载情况
     *
     * @param waitingTime 设置等待时间，单位毫秒
     */
    public Cpu(long waitingTime) {
        CentralProcessor processor = OshiHelper.getProcessor();
        this.coreNum = processor.getLogicalProcessorCount();
        this.model = processor.toString();
        // CPU信息
        final long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 这里必须要设置延迟
        ToolThread.sleep(waitingTime);
        final long[] ticks = processor.getSystemCpuLoadTicks();

        this.free = tick(prevTicks, ticks, CentralProcessor.TickType.IDLE);
        this.nice = tick(prevTicks, ticks, CentralProcessor.TickType.NICE);
        this.irq = tick(prevTicks, ticks, CentralProcessor.TickType.IRQ);
        this.softIrq = tick(prevTicks, ticks, CentralProcessor.TickType.SOFTIRQ);
        this.steal = tick(prevTicks, ticks, CentralProcessor.TickType.STEAL);
        this.sys = tick(prevTicks, ticks, CentralProcessor.TickType.SYSTEM);
        this.user = tick(prevTicks, ticks, CentralProcessor.TickType.USER);
        this.wait = tick(prevTicks, ticks, CentralProcessor.TickType.IOWAIT);

        this.used = 100 - free;
        this.total = Math.max(user + nice + sys + free + wait + irq + softIrq + steal, 0);
    }

    /**
     * 获取一段时间内的CPU负载标记差
     *
     * @param prevTicks 开始的ticks
     * @param ticks     结束的ticks
     * @param tickType  tick类型
     * @return 标记差
     */
    private static long tick(long[] prevTicks, long[] ticks, CentralProcessor.TickType tickType) {
        return ticks[tickType.getIndex()] - prevTicks[tickType.getIndex()];
    }
}

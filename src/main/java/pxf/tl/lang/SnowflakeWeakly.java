package pxf.tl.lang;


import pxf.tl.util.ToolTime;

/**
 * 弱雪花ID算法
 *
 * @author potatoxf
 */
public final class SnowflakeWeakly {
    /**
     * 默认实例
     */
    public static final SnowflakeWeakly INSTANCE = new SnowflakeWeakly();

    private static final long DEFAULT_START_TIMESTAMP = 1610713610939L;
    /**
     * 序列在ID中占的位数
     */
    private static final int SEQUENCE_LEN = 3;
    /**
     * 生成序列ID的掩码( SEQUENCE_LEN 位所对应的最大整数值)
     */
    private static final int SEQUENCE_MASK = ~(-1 << SEQUENCE_LEN);
    /**
     * 时间截向 左移位数 - SEQUENCE_LEN
     */
    private static final int TIMESTAMP_MOVE_BITS = SEQUENCE_LEN;
    /**
     * 毫秒内序列(0~4095)
     */
    private int sequence = 0;
    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    private SnowflakeWeakly() {
    }

    /**
     * 线程安全的获得下一个ID的方法
     *
     * @return 返回long类型的ID
     */
    public synchronized int nextId() {
        return ((((int) ((this.lastTimestamp = this.generatorTimeMillis()) - DEFAULT_START_TIMESTAMP))
                << TIMESTAMP_MOVE_BITS)
                | sequence)
                & Integer.MAX_VALUE;
    }

    /**
     * 线程安全的获得下一个ID的方法
     *
     * @return 返回long类型的ID
     */
    public synchronized String nextIdString() {
        return nextIdString(null);
    }

    /**
     * 线程安全的获得下一个ID的方法
     *
     * @return 返回long类型的ID
     */
    public synchronized String nextIdString(String prefix) {
        long id = nextId();
        if (prefix != null) {
            return prefix + id;
        }
        return String.valueOf(id);
    }

    private synchronized long generatorTimeMillis() {
        long timestamp = ToolTime.currentTimeMillis(lastTimestamp);
        // 如果是同一时间生成的，则进行毫秒内序列
        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1) & SEQUENCE_MASK;
            // 毫秒内序列溢出 即 序列 > 4095
            if (this.sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = ToolTime.nextTimeMillis(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            this.sequence = 0;
        }
        return timestamp;
    }
}

package pxf.tl.math.id;

import pxf.tl.date.SystemClock;
import pxf.tl.help.Assert;
import pxf.tl.util.ToolRandom;
import pxf.tl.util.ToolString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Twitter的Snowflake 算法<br>
 * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
 *
 * <p>snowflake的结构如下(每部分用-分开):<br>
 *
 * <pre>
 * 符号位（1bit）- 时间戳相对值（41bit）- 数据中心标志（5bit）- 机器标志（5bit）- 递增序号（12bit）
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * </pre>
 *
 * <p>第一位为未使用(符号位表示正数)，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
 * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点）<br>
 * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
 *
 * <p>并且可以通过生成的id反推出生成时间,datacenterId和workerId
 *
 * <p>参考：http://www.cnblogs.com/relucent/p/4955340.html<br>
 * 关于长度是18还是19的问题见：https://blog.csdn.net/unifirst/article/details/80408050
 *
 * @author potatoxf
 */
public final class Snowflake implements Serializable {
    /**
     *
     */
    public static final Snowflake INSTANCE = Snowflake.of(0, 0);
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 20230101
     */
    private static final long DEFAULT_START_TIMESTAMP = 1672531200000L;
    /**
     * 最大字典ID长度
     */
    private static final int DICT_ID_AREA_LNE = 5;
    /**
     * 最大字典ID，最大支持机器节点数0~31，一共32个
     */
    private static final int MAX_DICT_ID = ~(-1 << DICT_ID_AREA_LNE);
    /**
     * 最大ID长度
     */
    private static final int ID_AREA_LNE = 5;
    /**
     * 最大ID，最大支持数据中心节点数0~31，一共32个
     */
    private static final int MAX_ID = ~(-1 << ID_AREA_LNE);
    /**
     * 序列在ID中占的位数，序列号12位（表示只允许workId的范围为：0-4095）
     */
    private static final int SEQUENCE_LEN = 12;
    /**
     * 生成序列ID的掩码( SEQUENCE_LEN 位所对应的最大整数值)，这里为4095 (0b111111111111=0xfff=4095)
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_LEN);
    /**
     * 机器ID左移位数 - SEQUENCE_LEN (即末sequence所占用的位数)
     * 机器节点左移12位
     */
    private static final int ID_MOVE_BITS = SEQUENCE_LEN;
    /**
     * 数据标识id左移位数 - (ID_MOVE_BITS + ID_AREA_LNE)
     * 数据中心节点左移17位
     */
    private static final int DICT_ID_MOVE_BITS = ID_MOVE_BITS + ID_AREA_LNE;
    /**
     * 时间截向 左移位数 - (ID_MOVE_BITS + ID_AREA_LNE + SEQUENCE_LEN)
     * 时间毫秒数左移22位
     */
    private static final int TIMESTAMP_MOVE_BITS = DICT_ID_MOVE_BITS + DICT_ID_AREA_LNE;
    /**
     * 默认回拨时间，2S
     */
    public static long DEFAULT_TIME_OFFSET = 2000L;
    /**
     * 缓存雪花ID生成器
     */
    private static final Map<String, Snowflake> CACHE = new ConcurrentHashMap<>(2);

    /**
     * 初始化时间点
     */
    private final long startTimestamp;
    /**
     * DICT ID(0~31)
     */
    private final int dictId;
    /**
     * DICT ID转为二进制值
     */
    private final int dictIdBitValue;
    /**
     * ID(0~31)
     */
    private final int id;
    /**
     * ID转为二进制值
     */
    private final int idBitValue;
    /**
     * 自增序号，当高频模式下时，同一毫秒内生成N个ID，则这个序号在同一毫秒下，自增以避免ID重复。毫秒内序列(0~4095)
     */
    private long sequence = 0L;
    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;
    /**
     *
     */
    private final boolean useSystemClock;
    /**
     * 允许的时钟回拨毫秒数
     */
    private final long timeOffset;
    /**
     * 当在低频模式下时，序号始终为0，导致生成ID始终为偶数<br>
     * 此属性用于限定一个随机上限，在不同毫秒下生成序号时，给定一个随机数，避免偶数问题。<br>
     * 注意次数必须小于{@link #SEQUENCE_MASK}，{@code 0}表示不使用随机数。<br>
     * 这个上限不包括值本身。
     */
    private final long randomSequenceLimit;

    /**
     * 创建雪花算法生成器 {@code Snowflake}设置开始时间，并且只能设置一次
     */
    public static synchronized Snowflake of() {
        return Snowflake.of(0, 0);
    }

    /**
     * 创建雪花算法生成器 {@code Snowflake}设置开始时间，并且只能设置一次
     *
     * @param dictId 分类ID
     * @param id     ID
     */
    public static synchronized Snowflake of(int dictId, int id) {
        return Snowflake.of(DEFAULT_START_TIMESTAMP, dictId, id, false, DEFAULT_TIME_OFFSET, 0);
    }

    /**
     * 创建雪花算法生成器 {@code Snowflake}设置开始时间，并且只能设置一次
     *
     * @param systemStartTimestamp 系统开始时间戳
     * @param dictId               分类ID
     * @param id                   ID
     */
    public static synchronized Snowflake of(long systemStartTimestamp,
                                            int dictId,
                                            int id,
                                            boolean isUseSystemClock,
                                            long timeOffset,
                                            long randomSequenceLimit) {
        if (dictId > DICT_ID_AREA_LNE || dictId < 0) {
            throw new IllegalArgumentException(
                    String.format(
                            "The dictionary id can't be greater than %d or less than 0", DICT_ID_AREA_LNE));
        }
        if (id > ID_AREA_LNE || id < 0) {
            throw new IllegalArgumentException(
                    String.format("DataCenter Id can't be greater than %d or less than 0", ID_AREA_LNE));
        }
        if (systemStartTimestamp > System.currentTimeMillis()) {
            throw new IllegalArgumentException(
                    "The system start timestamp must be less then current timestamp");
        }
        String key = dictId + "-" + id;
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }
        Snowflake snowflake = new Snowflake(systemStartTimestamp, dictId, id, isUseSystemClock, timeOffset, randomSequenceLimit);
        CACHE.put(key, snowflake);
        return snowflake;
    }

    /**
     * @param systemStartTimestamp 初始化时间起点（null表示默认起始日期）,后期修改会导致id重复,如果要修改连workerId dataCenterId，慎用
     * @param dictId               工作机器节点id
     * @param id                   数据中心id
     * @param isUseSystemClock     是否使用{@link SystemClock} 获取当前时间戳
     * @param timeOffset           允许时间回拨的毫秒数
     * @param randomSequenceLimit  限定一个随机上限，在不同毫秒下生成序号时，给定一个随机数，避免偶数问题，0表示无随机，上限不包括值本身。
     */
    private Snowflake(
            long systemStartTimestamp,
            int dictId,
            int id,
            boolean isUseSystemClock,
            long timeOffset,
            long randomSequenceLimit) {
        this.startTimestamp = systemStartTimestamp;
        this.dictId = Assert.checkBetween(dictId, 0, MAX_DICT_ID);
        this.dictIdBitValue = dictId << DICT_ID_MOVE_BITS;
        this.id = Assert.checkBetween(id, 0, MAX_ID);
        this.idBitValue = id << ID_MOVE_BITS;
        this.useSystemClock = isUseSystemClock;
        this.timeOffset = timeOffset;
        this.randomSequenceLimit = Assert.checkBetween(randomSequenceLimit, 0, SEQUENCE_MASK);
    }

    /**
     * 获取ID
     *
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * 根据Snowflake的ID，获取机器id
     *
     * @param id snowflake算法生成的id
     * @return 所属机器的id
     */
    public long getId(long id) {
        return id >> ID_MOVE_BITS & ~(-1L << DICT_ID_AREA_LNE);
    }

    /**
     * 获取DICT ID
     *
     * @return DICT ID
     */
    public int getDictId() {
        return dictId;
    }

    /**
     * 根据Snowflake的ID，获取数据中心id
     *
     * @param id snowflake算法生成的id
     * @return 所属数据中心
     */
    public long getDictId(long id) {
        return id >> DICT_ID_MOVE_BITS & ~(-1L << ID_AREA_LNE);
    }

    /**
     * 根据Snowflake的ID，获取生成时间
     *
     * @param id snowflake算法生成的id
     * @return 生成的时间
     */
    public long getGenerateTimestamp(long id) {
        return (id >> TIMESTAMP_MOVE_BITS & ~(-1L << 41L)) + startTimestamp;
    }

    /**
     * 下一个ID
     *
     * @return ID
     */
    public synchronized long nextId() {
        long timestamp = currentTimeMillis();
        if (timestamp < this.lastTimestamp) {
            if (this.lastTimestamp - timestamp < timeOffset) {
                // 容忍指定的回拨，避免NTP校时造成的异常
                timestamp = lastTimestamp;
            } else {
                // 如果服务器时间有问题(时钟后退) 报错。
                throw new IllegalStateException(
                        ToolString.format(
                                "Clock moved backwards. Refusing to generate id for {}ms",
                                lastTimestamp - timestamp));
            }
        }

        if (timestamp == this.lastTimestamp) {
            final long sequence = (this.sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
            this.sequence = sequence;
        } else {
            // issue#I51EJY
            if (randomSequenceLimit > 1) {
                sequence = ToolRandom.randomLong(randomSequenceLimit);
            } else {
                sequence = 0L;
            }
        }

        lastTimestamp = timestamp;

        return ((timestamp - startTimestamp) << TIMESTAMP_MOVE_BITS) | dictIdBitValue | idBitValue | sequence;
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
        return Long.toString(nextId());
    }

    /**
     * 循环等待下一个时间
     *
     * @param lastTimestamp 上次记录的时间
     * @return 下一个时间
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        // 循环直到操作系统时间戳变化
        while (timestamp == lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        if (timestamp < lastTimestamp) {
            // 如果发现新的时间戳比上次记录的时间戳数值小，说明操作系统时间发生了倒退，报错
            throw new IllegalStateException(
                    ToolString.format(
                            "Clock moved backwards. Refusing to generate id for {}ms",
                            lastTimestamp - timestamp));
        }
        return timestamp;
    }

    /**
     * 生成时间戳
     *
     * @return 时间戳
     */
    private long currentTimeMillis() {
        return this.useSystemClock ? SystemClock.now() : System.currentTimeMillis();
    }

    @Override
    public int hashCode() {
        int result = dictIdBitValue;
        result = 31 * result + idBitValue;
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Snowflake snowflake = (Snowflake) object;
        return dictIdBitValue == snowflake.dictIdBitValue && idBitValue == snowflake.idBitValue;
    }

    @Override
    public String toString() {
        return (dictIdBitValue >> DICT_ID_AREA_LNE) + "-" + (idBitValue >> ID_AREA_LNE);
    }

}

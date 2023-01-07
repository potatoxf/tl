package pxf.tl.math.id;


import pxf.tl.api.JavaEnvironment;
import pxf.tl.exception.UtilException;
import pxf.tl.help.Assert;
import pxf.tl.net.NetUtil;
import pxf.tl.util.ToolNumber;
import pxf.tl.util.ToolRandom;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * ID生成器工具类，此工具类中主要封装：
 *
 * <pre>
 * 1. 唯一性ID生成器：UUID、ObjectId（MongoDB）、Snowflake
 * </pre>
 *
 * <p>ID相关文章见：http://calvin1978.blogcn.com/articles/uuid.html
 *
 * @author potatoxf
 */
public final class IDHelper {
    /**
     * @return
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }


    /**
     * 获取UUID
     *
     * @return UUID
     */
    public static String randomUUID32() {
        return UUID.randomUUID().toString().replaceAll("-", "").trim();
    }

    /**
     * 获取UUID
     *
     * @return UUID
     */
    public static String randomUUID22() {
        UUID uuid = UUID.randomUUID();
        return ToolNumber.toRadix64(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    /**
     * 创建MongoDB ID生成策略实现<br>
     * ObjectId由以下几部分组成：
     *
     * <pre>
     * 1. Time 时间戳。
     * 2. Machine 所在主机的唯一标识符，一般是机器主机名的散列值。
     * 3. PID 进程ID。确保同一机器中不冲突
     * 4. INC 自增计数器。确保同一秒内产生objectId的唯一性。
     * </pre>
     *
     * <p>参考：http://blog.csdn.net/qxc1281/article/details/54021882
     *
     * @return ObjectId
     */
    public static String objectId() {
        return ObjectId.next();
    }

    /**
     * 获取数据中心ID<br>
     * 数据中心ID依赖于本地网卡MAC地址。
     *
     * <p>此算法来自于mybatis-plus#Sequence
     *
     * @param maxDatacenterId 最大的中心ID
     * @return 数据中心ID
     */
    public static long getDataCenterId(long maxDatacenterId) {
        Assert.isTrue(maxDatacenterId > 0, "maxDatacenterId must be > 0");
        if (maxDatacenterId == Long.MAX_VALUE) {
            maxDatacenterId -= 1;
        }
        long id = 1L;
        byte[] mac = null;
        try {
            mac = NetUtil.getLocalHardwareAddress();
        } catch (UtilException ignore) {
            // ignore
        }
        if (null != mac) {
            id =
                    ((0x000000FF & (long) mac[mac.length - 2])
                            | (0x0000FF00 & (((long) mac[mac.length - 1]) << 8)))
                            >> 6;
            id = id % (maxDatacenterId + 1);
        }

        return id;
    }

    /**
     * 获取机器ID，使用进程ID配合数据中心ID生成<br>
     * 机器依赖于本进程ID或进程名的Hash值。
     *
     * <p>此算法来自于mybatis-plus#Sequence
     *
     * @param datacenterId 数据中心ID
     * @param maxWorkerId  最大的机器节点ID
     * @return ID
     */
    public static long getWorkerId(long datacenterId, long maxWorkerId) {
        final StringBuilder mpid = new StringBuilder();
        mpid.append(datacenterId);
        try {
            mpid.append(JavaEnvironment.PID);
        } catch (UtilException igonre) {
            // ignore
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 获取随机NanoId
     *
     * @return 随机NanoId
     */
    public static String nanoId() {
        return NanoId.randomNanoId();
    }

    /**
     * 获取随机NanoId
     *
     * @param size ID中的字符数量
     * @return 随机NanoId
     */
    public static String nanoId(int size) {
        return NanoId.randomNanoId(size);
    }

    /**
     * @return
     */
    public static String sequenceDateId12() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        return ((int) (ToolRandom.randomDouble() * (9999 - 1000 + 1)) + 1000) + str;// 当前时间
    }
}

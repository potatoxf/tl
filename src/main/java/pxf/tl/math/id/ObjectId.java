package pxf.tl.math.id;


import pxf.tl.api.JavaEnvironment;
import pxf.tl.date.DateUtil;
import pxf.tl.util.ToolRandom;
import pxf.tl.util.ToolString;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MongoDB ID生成策略实现<br>
 * ObjectId由以下几部分组成：
 *
 * <pre>
 * 1. Time 时间戳。
 * 2. Machine 所在主机的唯一标识符，一般是机器主机名的散列值。
 * 3. PID 进程ID。确保同一机器中不冲突
 * 4. INC 自增计数器。确保同一秒内产生objectId的唯一性。
 * </pre>
 *
 * <table summary="" border="1">
 *     <tr>
 *         <td>时间戳</td>
 *         <td>机器ID</td>
 *         <td>进程ID</td>
 *         <td>自增计数器</td>
 *     </tr>
 *     <tr>
 *         <td>4</td>
 *         <td>3</td>
 *         <td>2</td>
 *         <td>3</td>
 *     </tr>
 * </table>
 * <p>
 * 参考：http://blog.csdn.net/qxc1281/article/details/54021882
 *
 * @author potatoxf
 */
public class ObjectId {

    /**
     * 线程安全的下一个随机数,每次生成自增+1
     */
    private static final AtomicInteger NEXT_INC = new AtomicInteger(ToolRandom.randomInt());

    /**
     * 给定的字符串是否为有效的ObjectId
     *
     * @param s 字符串
     * @return 是否为有效的ObjectId
     */
    public static boolean isValid(String s) {
        if (s == null) {
            return false;
        }
        s = ToolString.removeAll(s, "-");
        final int len = s.length();
        if (len != 24) {
            return false;
        }

        char c;
        for (int i = 0; i < len; i++) {
            c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                continue;
            }
            if (c >= 'a' && c <= 'f') {
                continue;
            }
            if (c >= 'A' && c <= 'F') {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * 获取一个objectId的bytes表现形式
     *
     * @return objectId
     */
    public static byte[] nextBytes() {
        final ByteBuffer bb = ByteBuffer.wrap(new byte[12]);
        bb.putInt((int) DateUtil.currentSeconds()); // 4位
        bb.putInt(JavaEnvironment.MACHINE); // 4位
        bb.putInt(NEXT_INC.getAndIncrement()); // 4位

        return bb.array();
    }

    /**
     * 获取一个objectId用下划线分割
     *
     * @return objectId
     */
    public static String next() {
        return next(false);
    }

    /**
     * 获取一个objectId
     *
     * @param withHyphen 是否包含分隔符
     * @return objectId
     */
    public static String next(boolean withHyphen) {
        byte[] array = nextBytes();
        final StringBuilder buf = new StringBuilder(withHyphen ? 26 : 24);
        int t;
        for (int i = 0; i < array.length; i++) {
            if (withHyphen && i % 4 == 0 && i != 0) {
                buf.append("-");
            }
            t = array[i] & 0xff;
            if (t < 16) {
                buf.append('0');
            }
            buf.append(Integer.toHexString(t));
        }
        return buf.toString();
    }
}

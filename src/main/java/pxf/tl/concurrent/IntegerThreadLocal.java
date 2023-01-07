package pxf.tl.concurrent;

/**
 * Integer类型线程安全容器
 *
 * @author potatoxf
 */
public class IntegerThreadLocal extends ThreadLocal<Integer> {

    /**
     * @return
     */
    @Override
    protected Integer initialValue() {
        return 0;
    }

    /**
     * 获取并增加值
     *
     * @return 返回值
     */
    public int getAndIncrease() {
        int result = get();
        set(result + 1);
        return result;
    }

    /**
     * 获取并减少值
     *
     * @return 返回值
     */
    public int getAndDecrease() {
        int result = get();
        set(result - 1);
        return result;
    }

    /**
     * 增加并获取值
     *
     * @return 返回值
     */
    public int increaseAndGet() {
        int result = get();
        set(++result);
        return result;
    }

    /**
     * 减少并获取值
     *
     * @return 返回值
     */
    public int decreaseAndGet() {
        int result = get();
        set(--result);
        return result;
    }
}

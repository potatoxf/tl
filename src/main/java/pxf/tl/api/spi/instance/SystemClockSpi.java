package pxf.tl.api.spi.instance;


import pxf.tl.api.spi.Spi;
import pxf.tl.collection.map.Parametric;

/**
 * 系统时钟Spi
 *
 * @author potatoxf
 */
public final class SystemClockSpi extends Spi<SystemClock> {

    /**
     * 是否单例
     *
     * @return 如果是单例返回true，否则返回false
     */
    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * 构造类型
     *
     * @return Class<T>，不允许为null
     */
    @Override
    public Class<SystemClock> type() {
        return SystemClock.class;
    }

    /**
     * 加载
     *
     * @param parametric 参数，可能为null
     * @return T
     */
    @Override
    public SystemClock load(Parametric parametric) {
        return new SystemClock();
    }
}

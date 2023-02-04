package pxf.tl.api.spi;


import pxf.tl.collection.map.Parametric;

/**
 * Spi超级接口
 *
 * @author potatoxf
 */
public abstract class Spi<T extends SpiApi> {

    /**
     * 是否单例
     *
     * @return 如果是单例返回true，否则返回false
     */
    public boolean isSingleton() {
        return false;
    }

    /**
     * 构造类型
     *
     * @return Class<T>，不允许为null
     */
    public abstract Class<T> type();

    /**
     * 加载
     *
     * @return T
     */
    public final T load() {
        return load(new Parametric());
    }

    /**
     * 加载
     *
     * @param parametric 参数，可能为null
     * @return T
     */
    public abstract T load(Parametric parametric);
}

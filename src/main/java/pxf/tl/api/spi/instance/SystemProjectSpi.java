package pxf.tl.api.spi.instance;


import pxf.tl.api.spi.Spi;
import pxf.tl.collection.map.Parametric;

/**
 * 项目Spi
 *
 * @author potatoxf
 */
public class SystemProjectSpi extends Spi<SystemProject> {

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
    public Class<SystemProject> type() {
        return SystemProject.class;
    }

    /**
     * 加载
     *
     * @param parametric 参数，可能为null
     * @return T
     */
    @Override
    public SystemProject load(Parametric parametric) {
        return new SystemProject();
    }
}

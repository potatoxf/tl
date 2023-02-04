package pxf.tl.api;

/**
 * 克隆支持接口
 *
 * @param <T> 实现克隆接口的类型
 * @author potatoxf
 */
public interface GenericsCloneable<T> extends Cloneable {

    /**
     * 克隆当前对象，浅复制
     *
     * @return 克隆后的对象
     */
    T clone();
}

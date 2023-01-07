package pxf.tl.api;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * 建造者模式接口定义
 *
 * @param <T> 建造对象类型
 * @author potatoxf
 */
public interface Builder<T> extends Supplier<T>, Serializable {
    /**
     * 构建
     *
     * @return 被构建的对象
     */
    T build();

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    default T get() {
        return build();
    }
}

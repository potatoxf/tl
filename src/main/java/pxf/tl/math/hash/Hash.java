package pxf.tl.math.hash;

/**
 * Hash计算接口
 *
 * @param <T> 被计算hash的对象类型
 * @author potatoxf
 */
@FunctionalInterface
public interface Hash<T> {
    /**
     * 计算Hash值
     *
     * @param t 对象
     * @return hash
     */
    Number hash(T t);
}

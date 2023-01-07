package pxf.tl.lang;


import pxf.tl.collection.map.UnModificationMap;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 实例管理容器
 *
 * @author potatoxf
 */
public interface InstanceManager<ArgKey> {

    /**
     * 实例容器
     */
    Map<Object, InstanceManager<?>> INSTANCES = new UnModificationMap<>(new ConcurrentHashMap<>());

    /**
     * 获取实例
     *
     * @param key      参数键
     * @param creator  实例创建器
     * @param <ArgKey> 参数类型
     * @param <T>      实例类型
     * @return 返回创建实例或缓存实例
     * @throws ClassCastException 如果键对于的数据实例类型不对，则抛出该异常
     */
    @SuppressWarnings("unchecked")
    static <ArgKey, T extends InstanceManager<ArgKey>> T of(
            @Nonnull final ArgKey key, @Nonnull final Function<ArgKey, T> creator) {
        if (key == null) {
            throw new IllegalArgumentException("The key must be no null");
        }
        T t = null;
        if (!INSTANCES.containsKey(key)) {
            synchronized (INSTANCES) {
                if (!INSTANCES.containsKey(key)) {
                    t = creator.apply(key);
                    if (t == null) {
                        throw new RuntimeException("Returning null values is not allowed");
                    }
                    INSTANCES.put(key, t);
                }
            }
        }

        if (t == null) {
            t = (T) INSTANCES.get(key);
        }
        return t;
    }
}

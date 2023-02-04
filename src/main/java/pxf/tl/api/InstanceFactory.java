package pxf.tl.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.function.SupplierThrow;
import pxf.tl.help.New;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实例工厂
 * <p>
 * 保存实例或者实例获取器
 *
 * @author potatoxf
 */
public class InstanceFactory<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceFactory.class);
    private final Map<K, V> instanceMap = new ConcurrentHashMap<>();
    private final Map<K, SupplierThrow<V, Throwable>> instanceSupplierMap = new ConcurrentHashMap<>();

    /**
     * 键集合
     *
     * @return {@code Set<K>}
     */
    public Set<K> getKeySet() {
        return Collections.unmodifiableSet(New.set(false, instanceMap.keySet(), instanceSupplierMap.keySet()));
    }

    /**
     * 获取实例，如果不存在实例，则通过实例提供器获取
     *
     * @param key 键
     * @return 如果不存在实例或实例提供器，则返回null
     */
    public V getInstance(@Nonnull K key) {
        V value = instanceMap.get(key);
        if (value != null) {
            return value;
        }
        return getNewInstance(key);
    }

    /**
     * 获取新的实例
     *
     * @param key 键
     * @return 如果不存在实例提供器，则返回null
     */
    public V getNewInstance(@Nonnull K key) {
        SupplierThrow<V, Throwable> instanceSupplier = instanceSupplierMap.get(key);
        if (instanceSupplier != null) {
            try {
                return instanceSupplier.getThrow();
            } catch (Throwable e) {
                LOGGER.error("Error to get value or get value supplier to create value by " + key, e);
            }
        }
        return null;
    }

    /**
     * 是否包含实例
     *
     * @param key 键
     * @return 如果包含实例则返回true，否则返回false
     */
    public boolean containsInstance(@Nonnull K key) {
        return instanceMap.containsKey(key);
    }

    /**
     * 是否包含实例提供器
     *
     * @param key 键
     * @return 如果包含实例提供器则返回true，否则返回false
     */
    public boolean containsInstanceSupplier(@Nonnull K key) {
        return instanceSupplierMap.containsKey(key);
    }

    /**
     * 注册实例
     *
     * @param key      键
     * @param instance 实例
     * @return this
     */
    public InstanceFactory<K, V> registryInstance(@Nonnull K key, @Nonnull V instance) {
        this.instanceMap.put(key, instance);
        return this;
    }

    /**
     * 注册实例
     *
     * @param key              键
     * @param instanceSupplier 实例提供器
     * @return this
     */
    public InstanceFactory<K, V> registryInstanceSupplier(@Nonnull K key, @Nonnull SupplierThrow<V, Throwable> instanceSupplier) {
        instanceSupplierMap.put(key, instanceSupplier);
        return this;
    }

    /**
     * 移除指定键实例
     *
     * @param key 键
     */
    public void remove(K key) {
        instanceMap.remove(key);
        instanceSupplierMap.remove(key);
    }

    /**
     * 清除所有Singleton对象
     */
    public void destroy() {
        instanceMap.clear();
        instanceSupplierMap.clear();
    }
}

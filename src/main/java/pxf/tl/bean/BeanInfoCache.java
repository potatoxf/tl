package pxf.tl.bean;


import pxf.tl.collection.map.ReferenceConcurrentMap;
import pxf.tl.collection.map.WeakConcurrentMap;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.function.Function;

/**
 * Bean属性缓存<br>
 * 缓存用于防止多次反射造成的性能问题
 *
 * @author potatoxf
 */
public enum BeanInfoCache {
    INSTANCE;

    private final WeakConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> pdCache =
            new WeakConcurrentMap<>();
    private final WeakConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> ignoreCasePdCache =
            new WeakConcurrentMap<>();

    /**
     * 获得属性名和{@link PropertyDescriptor}Map映射
     *
     * @param beanClass  Bean的类
     * @param ignoreCase 是否忽略大小写
     * @return 属性名和{@link PropertyDescriptor}Map映射
     */
    public Map<String, PropertyDescriptor> getPropertyDescriptorMap(
            Class<?> beanClass, boolean ignoreCase) {
        return getCache(ignoreCase).get(beanClass);
    }

    /**
     * 获得属性名和{@link PropertyDescriptor}Map映射
     *
     * @param beanClass  Bean的类
     * @param ignoreCase 是否忽略大小写
     * @param supplier   缓存对象产生函数
     * @return 属性名和{@link PropertyDescriptor}Map映射
     */
    public Map<String, PropertyDescriptor> getPropertyDescriptorMap(
            Class<?> beanClass, boolean ignoreCase, Function<Class<?>, Map<String, PropertyDescriptor>> supplier) {
        return getCache(ignoreCase)
                .computeIfAbsent(beanClass, supplier);
    }

    /**
     * 加入缓存
     *
     * @param beanClass                      Bean的类
     * @param fieldNamePropertyDescriptorMap 属性名和{@link PropertyDescriptor}Map映射
     * @param ignoreCase                     是否忽略大小写
     */
    public void putPropertyDescriptorMap(
            Class<?> beanClass,
            Map<String, PropertyDescriptor> fieldNamePropertyDescriptorMap,
            boolean ignoreCase) {
        getCache(ignoreCase).put(beanClass, fieldNamePropertyDescriptorMap);
    }

    /**
     * 清空缓存
     */
    public void clear() {
        this.pdCache.clear();
        this.ignoreCasePdCache.clear();
    }

    /**
     * 根据是否忽略字段名的大小写，返回不用Cache对象
     *
     * @param ignoreCase 是否忽略大小写
     * @return {@link ReferenceConcurrentMap}
     */
    private ReferenceConcurrentMap<Class<?>, Map<String, PropertyDescriptor>> getCache(
            boolean ignoreCase) {
        return ignoreCase ? ignoreCasePdCache : pdCache;
    }
}

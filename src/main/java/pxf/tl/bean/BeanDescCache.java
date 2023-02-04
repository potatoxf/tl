package pxf.tl.bean;


import pxf.tl.collection.map.WeakConcurrentMap;

import java.util.function.Function;

/**
 * Bean属性缓存<br>
 * 缓存用于防止多次反射造成的性能问题
 *
 * @author potatoxf
 */
public enum BeanDescCache {
    INSTANCE;

    private final WeakConcurrentMap<Class<?>, BeanDesc> bdCache = new WeakConcurrentMap<>();

    /**
     * 获得属性名和{@link BeanDesc}Map映射
     *
     * @param beanClass Bean的类
     * @param supplier  对象不存在时创建对象的函数
     * @return 属性名和{@link BeanDesc}映射
     */
    public BeanDesc getBeanDesc(Class<?> beanClass, Function<Class<?>, BeanDesc> supplier) {
        return bdCache.computeIfAbsent(beanClass, supplier);
    }

    /**
     * 清空全局的Bean属性缓存
     */
    public void clear() {
        this.bdCache.clear();
    }
}

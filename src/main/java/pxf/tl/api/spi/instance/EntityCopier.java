package pxf.tl.api.spi.instance;


import pxf.tl.api.spi.SpiApi;
import pxf.tl.function.SupplierThrow;

import java.util.Map;

/**
 * 属性复制器
 *
 * <p>1. 将Bean中属性复制到另一个Bean 2. 将Bean中属性复制到另一个Map 3. 将Map中属性复制到另一个Bean
 *
 * @author potatoxf
 */
public interface EntityCopier extends SpiApi {

    /**
     * 复制同一个实体的属性
     *
     * @param src  源实体
     * @param dist 目标实体
     * @param <T>  类型
     */
    <T> void copySame(T src, T dist);

    /**
     * 复制同一个实体的属性
     *
     * @param src           源实体
     * @param dist          目标实体
     * @param typeConverter 类型转换器
     * @param <T>           类型
     */
    <T> void copySame(T src, T dist, TypeConverter typeConverter);

    /**
     * 复制同一个实体的属性
     *
     * @param src       源实体
     * @param distClass 目标实体类
     * @param <T>       类型
     * @return 返回复制新的实体
     */
    <T> T copySame(T src, Class<T> distClass);

    /**
     * 复制同一个实体的属性
     *
     * @param src           源实体
     * @param distClass     目标实体类
     * @param typeConverter 类型转换器
     * @param <T>           类型
     * @return 返回复制新的实体
     */
    <T> T copySame(T src, Class<T> distClass, TypeConverter typeConverter);

    /**
     * 复制任何一个实体的属性
     *
     * @param src  源实体
     * @param dist 目标实体
     */
    void copyAny(Object src, Object dist);

    /**
     * 复制任何一个实体的属性
     *
     * @param src           源实体
     * @param dist          目标实体
     * @param typeConverter 类型转换器
     */
    void copyAny(Object src, Object dist, TypeConverter typeConverter);

    /**
     * 复制任何一个实体的属性
     *
     * @param src       源实体
     * @param distClass 目标实体类
     * @return 返回复制新的实体
     */
    Object copyAny(Object src, Class<?> distClass);

    /**
     * 复制任何一个实体的属性
     *
     * @param src           源实体
     * @param distClass     目标实体类
     * @param typeConverter 类型转换器
     * @return 返回复制新的实体
     */
    Object copyAny(Object src, Class<?> distClass, TypeConverter typeConverter);

    /**
     * 复制任何一个实体的属性到 {@code Map<String, Object>}
     *
     * @param src 源实体
     * @return {@code Map<String, Object>}
     */
    Map<String, Object> copyToMap(Object src);

    /**
     * 复制任何一个实体的属性到 {@code Map<String, Object>}
     *
     * @param src     源实体
     * @param factory 容器工厂
     * @return {@code Map<String, Object>}
     */
    Map<String, Object> copyToMap(
            Object src, SupplierThrow<Map<String, Object>, RuntimeException> factory);

    /**
     * 复制Map内容到实体属性里
     *
     * @param src  源Map
     * @param dist 目标实体
     */
    void copyFromMap(Map<String, Object> src, Object dist);

    /**
     * 复制Map内容到实体属性里
     *
     * @param src       源Map
     * @param distClass 目标实体类
     * @return 目标实体
     */
    Object copyFromMap(Map<String, Object> src, Class<?> distClass);

    /**
     * 类型转换器
     */
    interface TypeConverter {

        /**
         * 转换类型
         *
         * @param value      转换值
         * @param targetType 目标类型
         * @param context    环境
         * @return 转换后的值
         */
        Object convert(Object value, Class<?> targetType, Object context);
    }
}

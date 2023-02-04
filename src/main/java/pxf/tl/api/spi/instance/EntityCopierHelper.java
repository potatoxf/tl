package pxf.tl.api.spi.instance;


import pxf.tl.api.spi.SpiException;
import pxf.tl.api.spi.SpiManager;
import pxf.tl.function.SupplierThrow;

import java.util.Map;

/**
 * 属性复制器助手类
 *
 * @author potatoxf
 */
public final class EntityCopierHelper {

    public static EntityCopier getInstance() {
        try {
            return SpiManager.getServiceInstance(EntityCopier.class, null);
        } catch (SpiException e) {
            throw new RuntimeException("Entity Copier that could not be obtained", e);
        }
    }

    /**
     * 复制同一个实体的属性
     *
     * @param src  源实体
     * @param dist 目标实体
     */
    public static <T> void copySame(T src, T dist) {
        getInstance().copySame(src, dist);
    }

    /**
     * 复制同一个实体的属性
     *
     * @param src           源实体
     * @param dist          目标实体
     * @param typeConverter 类型转换器
     */
    public static <T> void copySame(T src, T dist, EntityCopier.TypeConverter typeConverter) {
        getInstance().copySame(src, dist, typeConverter);
    }

    /**
     * 复制同一个实体的属性
     *
     * @param src       源实体
     * @param distClass 目标实体类
     * @return 返回复制新的实体
     */
    public static <T> T copySame(T src, Class<T> distClass) {
        return getInstance().copySame(src, distClass);
    }

    /**
     * 复制同一个实体的属性
     *
     * @param src           源实体
     * @param distClass     目标实体类
     * @param typeConverter 类型转换器
     * @return 返回复制新的实体
     */
    public static <T> T copySame(
            T src, Class<T> distClass, EntityCopier.TypeConverter typeConverter) {
        return getInstance().copySame(src, distClass, typeConverter);
    }

    /**
     * 复制任何一个实体的属性
     *
     * @param src  源实体
     * @param dist 目标实体
     */
    public static void copyAny(Object src, Object dist) {
        getInstance().copyAny(src, dist);
    }

    /**
     * 复制任何一个实体的属性
     *
     * @param src           源实体
     * @param dist          目标实体
     * @param typeConverter 类型转换器
     */
    public static void copyAny(Object src, Object dist, EntityCopier.TypeConverter typeConverter) {
        getInstance().copyAny(src, dist, typeConverter);
    }

    /**
     * 复制任何一个实体的属性
     *
     * @param src       源实体
     * @param distClass 目标实体类
     * @return 返回复制新的实体
     */
    public static Object copyAny(Object src, Class<?> distClass) {
        return getInstance().copyAny(src, distClass);
    }

    /**
     * 复制任何一个实体的属性
     *
     * @param src           源实体
     * @param distClass     目标实体类
     * @param typeConverter 类型转换器
     * @return 返回复制新的实体
     */
    public static Object copyAny(
            Object src, Class<?> distClass, EntityCopier.TypeConverter typeConverter) {
        return getInstance().copyAny(src, distClass, typeConverter);
    }

    /**
     * 复制任何一个实体的属性到 {@code Map<String, Object>}
     *
     * @param src 源实体
     * @return {@code Map<String, Object>}
     */
    public static Map<String, Object> copyToMap(Object src) {
        return getInstance().copyToMap(src);
    }

    /**
     * 复制任何一个实体的属性到 {@code Map<String, Object>}
     *
     * @param src     源实体
     * @param factory 容器工厂
     * @return {@code Map<String, Object>}
     */
    public static Map<String, Object> copyToMap(
            Object src, SupplierThrow<Map<String, Object>, RuntimeException> factory) {
        return getInstance().copyToMap(src, factory);
    }

    /**
     * 复制Map内容到实体属性里
     *
     * @param src  源Map
     * @param dist 目标实体
     */
    public static void copyFromMap(Map<String, Object> src, Object dist) {
        getInstance().copyFromMap(src, dist);
    }

    /**
     * 复制Map内容到实体属性里
     *
     * @param src       源Map
     * @param distClass 目标实体类
     * @return 目标实体
     */
    public static Object copyFromMap(Map<String, Object> src, Class<?> distClass) {
        return getInstance().copyFromMap(src, distClass);
    }
}

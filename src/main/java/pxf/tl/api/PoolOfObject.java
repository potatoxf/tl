package pxf.tl.api;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.lang.annotation.*;
import java.util.Map;
import java.util.Set;

/**
 * @author potatoxf
 */
public interface PoolOfObject {
    /**
     * 空字节数组输入流
     */
    ByteArrayInputStream EMPTY_BYTE_ARRAY_INPUT_STREAM = new ByteArrayInputStream(PoolOfArray.EMPTY_BYTE_ARRAY);

    /**
     * 原生类型兼容等级
     */
    InstanceSupplier<Map<Class<?>, Integer>> PRIMITIVE_COMPATIBILITY = InstanceSupplier.of(() -> Map.ofEntries(
            Map.entry(boolean.class, -1),
            Map.entry(char.class, -1),
            Map.entry(byte.class, 1),
            Map.entry(short.class, 2),
            Map.entry(int.class, 3),
            Map.entry(long.class, 4),
            Map.entry(float.class, 5),
            Map.entry(double.class, 6)
    ));

    /**
     * 原生类型字节长度
     */
    InstanceSupplier<Map<Class<?>, Integer>> PRIMITIVE_BYTE_LENGTH = InstanceSupplier.of(() -> Map.ofEntries(
            Map.entry(boolean.class, 1),
            Map.entry(char.class, 2),
            Map.entry(byte.class, 1),
            Map.entry(short.class, 2),
            Map.entry(int.class, 4),
            Map.entry(long.class, 8),
            Map.entry(float.class, 4),
            Map.entry(double.class, 8)
    ));
    /**
     * 原生类型默认值
     */
    InstanceSupplier<Map<Class<?>, Object>> PRIMITIVE_DEFAULT_VALUE = InstanceSupplier.of(() -> Map.ofEntries(
            Map.entry(boolean.class, false),
            Map.entry(char.class, ((char) 0)),
            Map.entry(byte.class, PoolOfCommon.ZERO_BYTE),
            Map.entry(short.class, PoolOfCommon.ZERO_FLOAT),
            Map.entry(int.class, PoolOfCommon.ZERO_INT),
            Map.entry(long.class, PoolOfCommon.ZERO_LONG),
            Map.entry(float.class, PoolOfCommon.ZERO_FLOAT),
            Map.entry(double.class, PoolOfCommon.ZERO_DOUBLE)
    ));

    /**
     * 原生类型对应包装类型
     */
    InstanceSupplier<Map<Class<?>, Class<?>>> PRIMITIVE_TO_WRAPPER = InstanceSupplier.of(() -> Map.ofEntries(
            Map.entry(boolean.class, Boolean.class),
            Map.entry(char.class, Character.class),
            Map.entry(byte.class, Byte.class),
            Map.entry(short.class, Short.class),
            Map.entry(int.class, Integer.class),
            Map.entry(long.class, Long.class),
            Map.entry(float.class, Float.class),
            Map.entry(double.class, Double.class),
            Map.entry(void.class, Void.class),
            Map.entry(boolean[].class, Boolean[].class),
            Map.entry(char[].class, Character[].class),
            Map.entry(byte[].class, Byte[].class),
            Map.entry(short[].class, Short[].class),
            Map.entry(int[].class, Integer[].class),
            Map.entry(long[].class, Long[].class),
            Map.entry(float[].class, Float[].class),
            Map.entry(double[].class, Double[].class)
    ));

    /**
     * 原生类型名对应原生类型
     */
    InstanceSupplier<Map<String, Class<?>>> PRIMITIVE_NAME_TO_PRIMITIVE = InstanceSupplier.of(() -> Map.ofEntries(
            Map.entry(boolean.class.getName(), boolean.class),
            Map.entry(char.class.getName(), char.class),
            Map.entry(byte.class.getName(), byte.class),
            Map.entry(short.class.getName(), short.class),
            Map.entry(int.class.getName(), int.class),
            Map.entry(long.class.getName(), long.class),
            Map.entry(float.class.getName(), float.class),
            Map.entry(double.class.getName(), double.class),
            Map.entry(void.class.getName(), void.class),
            Map.entry(boolean[].class.getName(), boolean[].class),
            Map.entry(char[].class.getName(), char[].class),
            Map.entry(byte[].class.getName(), byte[].class),
            Map.entry(short[].class.getName(), short[].class),
            Map.entry(int[].class.getName(), int[].class),
            Map.entry(long[].class.getName(), long[].class),
            Map.entry(float[].class.getName(), float[].class),
            Map.entry(double[].class.getName(), double[].class)
    ));

    /**
     * 包装类型对应原生类型
     */
    InstanceSupplier<Map<Class<?>, Class<?>>> WRAPPER_TO_PRIMITIVE = InstanceSupplier.of(() -> Map.ofEntries(
            Map.entry(Boolean.class, boolean.class),
            Map.entry(Character.class, char.class),
            Map.entry(Byte.class, byte.class),
            Map.entry(Short.class, short.class),
            Map.entry(Integer.class, int.class),
            Map.entry(Long.class, long.class),
            Map.entry(Float.class, float.class),
            Map.entry(Double.class, double.class),
            Map.entry(Void.class, void.class),
            Map.entry(Boolean[].class, boolean[].class),
            Map.entry(Character[].class, char[].class),
            Map.entry(Byte[].class, byte[].class),
            Map.entry(Short[].class, short[].class),
            Map.entry(Integer[].class, int[].class),
            Map.entry(Long[].class, long[].class),
            Map.entry(Float[].class, float[].class),
            Map.entry(Double[].class, double[].class)
    ));
    /**
     * 元注解
     */
    InstanceSupplier<Set<Class<? extends Annotation>>> META_ANNOTATIONS = InstanceSupplier.of(() -> Set.of(
            Target.class,
            Retention.class,
            Inherited.class,
            Documented.class,
            SuppressWarnings.class,
            Override.class,
            Deprecated.class
    ));

    /**
     * 原始类转为包装类，非原始类返回原类
     *
     * @param clazz 原始类
     * @return 包装类
     */
    @Nonnull
    static Class<?> wrap(@Nonnull Class<?> clazz) {
        if (!clazz.isPrimitive()) {
            return clazz;
        }
        Class<?> result = PRIMITIVE_TO_WRAPPER.get().get(clazz);
        return (null == result) ? clazz : result;
    }

    /**
     * 包装类转为原始类，非包装类返回原类
     *
     * @param clazz 包装类
     * @return 原始类
     */
    @Nonnull
    static Class<?> unwrap(@Nonnull Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return clazz;
        }
        Class<?> result = WRAPPER_TO_PRIMITIVE.get().get(clazz);
        return (null == result) ? clazz : result;
    }
}

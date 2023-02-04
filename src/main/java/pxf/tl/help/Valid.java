package pxf.tl.help;

import pxf.tl.api.PoolOfCommon;
import pxf.tl.function.FunctionThrow;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 有效类
 *
 * @author potatoxf
 */
@SuppressWarnings("unchecked")
public final class Valid {
    private Valid() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    /**
     * @param predicate
     * @param trueValue
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T> T val(boolean predicate, T trueValue, T defaultValue) {
        return predicate ? (trueValue != null ? trueValue : defaultValue) : defaultValue;
    }

    /**
     * 如果值为null，返回默认值
     *
     * @param value        验证值
     * @param defaultValue 默认值
     * @return 如果值为null，返回默认值
     */
    public static boolean val(Boolean value, boolean defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 如果值为null，返回默认值
     *
     * @param value        验证值
     * @param defaultValue 默认值
     * @return 如果值为null，返回默认值
     */
    public static byte val(Byte value, byte defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 如果值为null，返回默认值
     *
     * @param value        验证值
     * @param defaultValue 默认值
     * @return 如果值为null，返回默认值
     */
    public static char val(Character value, char defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 如果值为null，返回默认值
     *
     * @param value        验证值
     * @param defaultValue 默认值
     * @return 如果值为null，返回默认值
     */
    public static short val(Short value, short defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 如果值为null，返回默认值
     *
     * @param value        验证值
     * @param defaultValue 默认值
     * @return 如果值为null，返回默认值
     */
    public static int val(Integer value, int defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 如果值为null，返回默认值
     *
     * @param value        验证值
     * @param defaultValue 默认值
     * @return 如果值为null，返回默认值
     */
    public static long val(Long value, long defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 如果值为null，返回默认值
     *
     * @param value        验证值
     * @param defaultValue 默认值
     * @return 如果值为null，返回默认值
     */
    public static float val(Float value, float defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 如果值为null，返回默认值
     *
     * @param value        验证值
     * @param defaultValue 默认值
     * @return 如果值为null，返回默认值
     */
    public static double val(Double value, double defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 如果值为null，返回空字符串
     *
     * @param value 验证值
     * @return 如果值为null，返回空字符串
     */
    public static String val(String value) {
        return value != null ? value : "";
    }

    /**
     * 如果值为null，返回空List
     *
     * @param value 验证值
     * @return 如果值为null，返回空List
     */
    public static <T> Iterable<T> val(Iterable<T> value) {
        return value != null ? value : PoolOfCommon.iterable();
    }

    /**
     * 如果值为null，返回空List
     *
     * @param value 验证值
     * @return 如果值为null，返回空List
     */
    public static <T> Iterator<T> val(Iterator<T> value) {
        return value != null ? value : Collections.emptyIterator();
    }

    /**
     * 如果值为null，返回空Map
     *
     * @param value 验证值
     * @return 如果值为null，返回空Map
     */
    public static <K, V> Map<K, V> val(Map<K, V> value) {
        return value != null ? value : (Map<K, V>) Collections.emptyMap();
    }

    /**
     * 如果值为null，返回空List
     *
     * @param value 验证值
     * @return 如果值为null，返回空List
     */
    public static <T> List<T> val(List<T> value) {
        return value != null ? value : (List<T>) Collections.emptyList();
    }

    /**
     * 如果值为null，返回空Set
     *
     * @param value 验证值
     * @return 如果值为null，返回空Set
     */
    public static <T> Set<T> val(Set<T> value) {
        return value != null ? value : (Set<T>) Collections.emptySet();
    }

    /**
     * 如果值为null，返回默认值
     *
     * @param value 验证值
     * @return 如果值为null，返回默认值
     */
    public static <E> E val(E value, E defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 如果值为null，返回默认值
     *
     * @param value 验证值
     * @return 如果值为null，返回默认值
     */
    public static <E, R> R val(E value, FunctionThrow<E, R, RuntimeException> getter, R defaultValue) {
        if (value != null) {
            R r = getter.apply(value);
            if (r != null) {
                return r;
            }
        }
        return defaultValue;
    }

    /**
     * 如果值为null，返回UTF-8
     *
     * @param charset 字符集
     * @return 如果值为null，返回默认值
     */
    public static Charset val(Charset charset) {
        return charset != null ? charset : StandardCharsets.UTF_8;
    }

    /**
     * 如果值为null，返回空字符串，否则返回
     *
     * @param value 验证值
     * @return 如果值为null，返回空字符串，否则返回
     */
    public static String string(String value) {
        return Whether.noEmpty(value) ? value : "";
    }

    /**
     * 如果值为null，返回空字符串，否则返回toString()
     *
     * @param value 验证值
     * @return 如果值为null，返回空字符串，否则返回toString()
     */
    public static String string(Object value) {
        return value != null
                ? (value instanceof String ? Valid.string(value.toString()) : value.toString())
                : "";
    }

    /**
     * 如果值为null，返回空字符串，否则返回toString()
     *
     * @param value        验证值
     * @param defaultValue 默认值
     * @return 如果值为null，返回空字符串，否则返回toString()
     */
    public static String string(Object value, String defaultValue) {
        return value != null
                ? (value instanceof String ? Valid.string(value.toString()) : value.toString())
                : defaultValue;
    }

    /**
     * 获取有效小数组，如果为null，则返回空数组
     *
     * @param arr 数组
     * @return 获取有效小数组，如果为null，则返回空数组
     */
    public static boolean[] arr(boolean[] arr) {
        return arr == null ? new boolean[0] : arr;
    }

    /**
     * 获取有效小数组，如果为null，则返回空数组
     *
     * @param arr 数组
     * @return 获取有效小数组，如果为null，则返回空数组
     */
    public static byte[] arr(byte[] arr) {
        return arr == null ? new byte[0] : arr;
    }

    /**
     * 获取有效小数组，如果为null，则返回空数组
     *
     * @param arr 数组
     * @return 获取有效小数组，如果为null，则返回空数组
     */
    public static short[] arr(short[] arr) {
        return arr == null ? new short[0] : arr;
    }

    /**
     * 获取有效小数组，如果为null，则返回空数组
     *
     * @param arr 数组
     * @return 获取有效小数组，如果为null，则返回空数组
     */
    public static int[] arr(int[] arr) {
        return arr == null ? new int[0] : arr;
    }

    /**
     * 获取有效小数组，如果为null，则返回空数组
     *
     * @param arr 数组
     * @return 获取有效小数组，如果为null，则返回空数组
     */
    public static long[] arr(long[] arr) {
        return arr == null ? new long[0] : arr;
    }

    /**
     * 获取有效小数组，如果为null，则返回空数组
     *
     * @param arr 数组
     * @return 获取有效小数组，如果为null，则返回空数组
     */
    public static float[] arr(float[] arr) {
        return arr == null ? new float[0] : arr;
    }

    /**
     * 获取有效小数组，如果为null，则返回空数组
     *
     * @param arr 数组
     * @return 获取有效小数组，如果为null，则返回空数组
     */
    public static double[] arr(double[] arr) {
        return arr == null ? new double[0] : arr;
    }

    /**
     * 获取有效小数组，如果为null，则返回空数组
     *
     * @param arr 数组
     * @return 获取有效小数组，如果为null，则返回空数组
     */
    public static Class<?>[] arr(Class<?>[] arr) {
        if (arr == null) {
            return new Class<?>[0];
        }
        int c = 0;
        for (Class<?> e : arr) {
            if (e != null) {
                c++;
            }
        }
        Class<?>[] results = new Class<?>[c];
        c = 0;
        for (Class<?> e : arr) {
            if (e != null) {
                results[c++] = e;
            }
        }
        return results;
    }

    /**
     * 获取有效小数组，如果为null，则返回空数组
     *
     * @param arr 数组
     * @return 获取有效小数组，如果为null，则返回空数组
     */
    public static Object[] arr(Object[] arr) {
        if (arr == null) {
            return new Class<?>[0];
        }
        int c = 0;
        for (Object e : arr) {
            if (e != null) {
                c++;
            }
        }
        Object[] results = new Object[c];
        c = 0;
        for (Object e : arr) {
            if (e != null) {
                results[c++] = e;
            }
        }
        return results;
    }

    /**
     * 获取有效小数组，如果为null，则返回空数组
     *
     * @param arr 数组
     * @return 获取有效小数组，如果为null，则返回空数组
     */
    public static String[] arr(String[] arr) {
        if (arr == null) {
            return new String[0];
        }
        int c = 0;
        for (String e : arr) {
            if (e != null) {
                c++;
            }
        }
        String[] results = new String[c];
        c = 0;
        for (String e : arr) {
            if (e != null) {
                results[c++] = e;
            }
        }
        return results;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    public static boolean ele(boolean[] arr, int index, boolean defaultValue) {
        return arr.length > index ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    public static byte ele(byte[] arr, int index, byte defaultValue) {
        return arr.length > index ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    public static char ele(char[] arr, int index, char defaultValue) {
        return arr.length > index ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    public static short ele(short[] arr, int index, short defaultValue) {
        return arr.length > index ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    public static int ele(int[] arr, int index, int defaultValue) {
        return arr.length > index ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    public static long ele(long[] arr, int index, long defaultValue) {
        return arr.length > index ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    public static float ele(float[] arr, int index, float defaultValue) {
        return arr.length > index ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    public static double ele(double[] arr, int index, double defaultValue) {
        return arr.length > index ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回空字符串
     *
     * @param arr   数组
     * @param index 索引
     * @return 获取数组有效值，如果不有效，则返回空字符串
     */
    public static String ele(String[] arr, int index) {
        return arr.length > index ? arr[index] : "";
    }

    /**
     * 获取数组有效值，如果不有效，则返回null
     *
     * @param arr   数组
     * @param index 索引
     * @return 获取数组有效值，如果不有效，则返回null
     */
    public static <E> E ele(E[] arr, int index) {
        return arr.length > index ? arr[index] : null;
    }

    /**
     * 如果值大于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值大于目标值返回原值，否则返回默认值
     */
    public static byte gt(Byte value, byte target, byte defaultValue) {
        return value == null ? defaultValue : (value > target ? value : defaultValue);
    }

    /**
     * 如果值大于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值大于目标值返回原值，否则返回默认值
     */
    public static short gt(Short value, short target, short defaultValue) {
        return value == null ? defaultValue : (value > target ? value : defaultValue);
    }

    /**
     * 如果值大于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值大于目标值返回原值，否则返回默认值
     */
    public static int gt(Integer value, int target, int defaultValue) {
        return value == null ? defaultValue : (value > target ? value : defaultValue);
    }

    /**
     * 如果值大于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值大于目标值返回原值，否则返回默认值
     */
    public static long gt(Long value, long target, long defaultValue) {
        return value == null ? defaultValue : (value > target ? value : defaultValue);
    }

    /**
     * 如果值大于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值大于目标值返回原值，否则返回默认值
     */
    public static float gt(Float value, float target, float defaultValue) {
        return value == null ? defaultValue : (value > target ? value : defaultValue);
    }

    /**
     * 如果值大于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值大于目标值返回原值，否则返回默认值
     */
    public static double gt(Double value, double target, double defaultValue) {
        return value == null ? defaultValue : (value > target ? value : defaultValue);
    }

    /**
     * 如果值大于等于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值大于等于目标值返回原值，否则返回默认值
     */
    public static byte gtEq(Byte value, byte target, byte defaultValue) {
        return value == null ? defaultValue : (value >= target ? value : defaultValue);
    }

    /**
     * 如果值大于等于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值大于等于目标值返回原值，否则返回默认值
     */
    public static short gtEq(Short value, short target, short defaultValue) {
        return value == null ? defaultValue : (value >= target ? value : defaultValue);
    }

    /**
     * 如果值大于等于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值大于等于目标值返回原值，否则返回默认值
     */
    public static int gtEq(Integer value, int target, int defaultValue) {
        return value == null ? defaultValue : (value >= target ? value : defaultValue);
    }

    /**
     * 如果值大于等于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值大于等于目标值返回原值，否则返回默认值
     */
    public static long gtEq(Long value, long target, long defaultValue) {
        return value == null ? defaultValue : (value >= target ? value : defaultValue);
    }

    /**
     * 如果值大于等于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值大于等于目标值返回原值，否则返回默认值
     */
    public static float gtEq(Float value, float target, float defaultValue) {
        return value == null ? defaultValue : (value >= target ? value : defaultValue);
    }

    /**
     * 如果值大于等于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值大于等于目标值返回原值，否则返回默认值
     */
    public static double gtEq(Double value, double target, double defaultValue) {
        return value == null ? defaultValue : (value >= target ? value : defaultValue);
    }

    /**
     * 如果值小于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值小于目标值返回原值，否则返回默认值
     */
    public static byte lt(Byte value, byte target, byte defaultValue) {
        return value == null ? defaultValue : (value < target ? value : defaultValue);
    }

    /**
     * 如果值小于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值小于目标值返回原值，否则返回默认值
     */
    public static short lt(Short value, short target, short defaultValue) {
        return value == null ? defaultValue : (value < target ? value : defaultValue);
    }

    /**
     * 如果值小于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值小于目标值返回原值，否则返回默认值
     */
    public static int lt(Integer value, int target, int defaultValue) {
        return value == null ? defaultValue : (value < target ? value : defaultValue);
    }

    /**
     * 如果值小于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值小于目标值返回原值，否则返回默认值
     */
    public static long lt(Long value, long target, long defaultValue) {
        return value == null ? defaultValue : (value < target ? value : defaultValue);
    }

    /**
     * 如果值小于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值小于目标值返回原值，否则返回默认值
     */
    public static float lt(Float value, float target, float defaultValue) {
        return value == null ? defaultValue : (value < target ? value : defaultValue);
    }

    /**
     * 如果值小于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值小于目标值返回原值，否则返回默认值
     */
    public static double lt(Double value, double target, double defaultValue) {
        return value == null ? defaultValue : (value < target ? value : defaultValue);
    }

    /**
     * 如果值小于等于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值小于等于目标值返回原值，否则返回默认值
     */
    public static byte ltEq(Byte value, byte target, byte defaultValue) {
        return value == null ? defaultValue : (value <= target ? value : defaultValue);
    }

    /**
     * 如果值小于等于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值小于等于目标值返回原值，否则返回默认值
     */
    public static short ltEq(Short value, short target, short defaultValue) {
        return value == null ? defaultValue : (value <= target ? value : defaultValue);
    }

    /**
     * 如果值小于等于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值小于等于目标值返回原值，否则返回默认值
     */
    public static int ltEq(Integer value, int target, int defaultValue) {
        return value == null ? defaultValue : (value <= target ? value : defaultValue);
    }

    /**
     * 如果值小于等于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值小于等于目标值返回原值，否则返回默认值
     */
    public static long ltEq(Integer value, long target, long defaultValue) {
        return value == null ? defaultValue : (value <= target ? value : defaultValue);
    }

    /**
     * 如果值小于等于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值小于等于目标值返回原值，否则返回默认值
     */
    public static float ltEq(Float value, float target, float defaultValue) {
        return value == null ? defaultValue : (value <= target ? value : defaultValue);
    }

    /**
     * 如果值小于等于目标值返回原值，否则返回默认值
     *
     * @param value        值
     * @param target       目标值
     * @param defaultValue 默认值
     * @return 如果值小于等于目标值返回原值，否则返回默认值
     */
    public static double ltEq(Double value, double target, double defaultValue) {
        return value == null ? defaultValue : (value <= target ? value : defaultValue);
    }
}

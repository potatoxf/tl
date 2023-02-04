package pxf.tl.convert;


import pxf.tl.convert.impl.CollectionConverter;
import pxf.tl.convert.impl.EnumConverter;
import pxf.tl.convert.impl.MapConverter;
import pxf.tl.lang.TypeReference;
import pxf.tl.util.ToolBytecode;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 类型转换器
 *
 * @author potatoxf
 */
public class Convert {

    /**
     * 转换为字符串<br>
     * 如果给定的值为null，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static String toStr(Object value, String defaultValue) {
        return convertQuietly(String.class, value, defaultValue);
    }

    /**
     * 转换为字符串<br>
     * 如果给定的值为{@code null}，或者转换失败，返回默认值{@code null}<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static String toStr(Object value) {
        return toStr(value, null);
    }

    /**
     * 转换为String数组
     *
     * @param value 被转换的值
     * @return String数组
     */
    public static String[] toStrArray(Object value) {
        return convert(String[].class, value);
    }

    /**
     * 转换为byte<br>
     * 如果给定的值为{@code null}，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Byte toByte(Object value, Byte defaultValue) {
        return convertQuietly(Byte.class, value, defaultValue);
    }

    /**
     * 转换为byte<br>
     * 如果给定的值为{@code null}，或者转换失败，返回默认值{@code null}<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Byte toByte(Object value) {
        return toByte(value, null);
    }

    /**
     * 转换为Short<br>
     * 如果给定的值为{@code null}，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Short toShort(Object value, Short defaultValue) {
        return convertQuietly(Short.class, value, defaultValue);
    }

    /**
     * 转换为int<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Integer toInt(Object value, Integer defaultValue) {
        return convertQuietly(Integer.class, value, defaultValue);
    }

    /**
     * 转换为int<br>
     * 如果给定的值为{@code null}，或者转换失败，返回默认值{@code null}<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Integer toInt(Object value) {
        return toInt(value, null);
    }

    /**
     * 转换为long<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Long toLong(Object value, Long defaultValue) {
        return convertQuietly(Long.class, value, defaultValue);
    }

    /**
     * 转换为double<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Double toDouble(Object value, Double defaultValue) {
        return convertQuietly(Double.class, value, defaultValue);
    }

    /**
     * 转换为Float<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Float toFloat(Object value, Float defaultValue) {
        return convertQuietly(Float.class, value, defaultValue);
    }

    /**
     * 转换为boolean<br>
     * String支持的值为：true、false、yes、ok、no，1,0 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Boolean toBool(Object value, Boolean defaultValue) {
        return convertQuietly(Boolean.class, value, defaultValue);
    }

    /**
     * 转换为BigInteger<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static BigInteger toBigInteger(Object value, BigInteger defaultValue) {
        return convertQuietly(BigInteger.class, value, defaultValue);
    }

    /**
     * 转换为BigInteger<br>
     * 如果给定的值为空，或者转换失败，返回默认值{@code null}<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static BigInteger toBigInteger(Object value) {
        return toBigInteger(value, null);
    }

    /**
     * 转换为BigDecimal<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
        return convertQuietly(BigDecimal.class, value, defaultValue);
    }

    /**
     * 转换为BigDecimal<br>
     * 如果给定的值为空，或者转换失败，返回null<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static BigDecimal toBigDecimal(Object value) {
        return toBigDecimal(value, null);
    }

    /**
     * 转换为Date<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Date toDate(Object value, Date defaultValue) {
        return convertQuietly(Date.class, value, defaultValue);
    }

    /**
     * LocalDateTime<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static LocalDateTime toLocalDateTime(Object value, LocalDateTime defaultValue) {
        return convertQuietly(LocalDateTime.class, value, defaultValue);
    }

    /**
     * 转换为LocalDateTime<br>
     * 如果给定的值为空，或者转换失败，返回{@code null}<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static LocalDateTime toLocalDateTime(Object value) {
        return toLocalDateTime(value, null);
    }

    /**
     * Instant<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Date toInstant(Object value, Date defaultValue) {
        return convertQuietly(Instant.class, value, defaultValue);
    }

    /**
     * 转换为Date<br>
     * 如果给定的值为空，或者转换失败，返回{@code null}<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Date toDate(Object value) {
        return toDate(value, null);
    }

    /**
     * 转换为Enum对象<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     *
     * @param <E>          枚举类型
     * @param clazz        Enum的Class
     * @param value        值
     * @param defaultValue 默认值
     * @return Enum
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value, E defaultValue) {
        return (E) (new EnumConverter(clazz)).convertQuietly(value, defaultValue);
    }

    /**
     * 转换为Enum对象<br>
     * 如果给定的值为空，或者转换失败，返回默认值{@code null}<br>
     *
     * @param <E>   枚举类型
     * @param clazz Enum的Class
     * @param value 值
     * @return Enum
     */
    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value) {
        return toEnum(clazz, value, null);
    }

    /**
     * 转换为集合类
     *
     * @param collectionType 集合类型
     * @param elementType    集合中元素类型
     * @param value          被转换的值
     * @return {@link Collection}
     */
    public static Collection<?> toCollection(
            Class<?> collectionType, Class<?> elementType, Object value) {
        return new CollectionConverter(collectionType, elementType).convert(value, null);
    }

    /**
     * 转换为ArrayList，元素类型默认Object
     *
     * @param value 被转换的值
     * @return {@link List}
     */
    public static List<?> toList(Object value) {
        return convert(List.class, value);
    }

    /**
     * 转换为ArrayList
     *
     * @param <T>         元素类型
     * @param elementType 集合中元素类型
     * @param value       被转换的值
     * @return {@link ArrayList}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(Class<T> elementType, Object value) {
        return (List<T>) toCollection(ArrayList.class, elementType, value);
    }

    /**
     * 转换为HashSet
     *
     * @param <T>         元素类型
     * @param elementType 集合中元素类型
     * @param value       被转换的值
     * @return {@link HashSet}
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> toSet(Class<T> elementType, Object value) {
        return (Set<T>) toCollection(HashSet.class, elementType, value);
    }

    /**
     * 转换为Map，若value原本就是Map，则转为原始类型，若不是则默认转为HashMap
     *
     * @param <K>       键类型
     * @param <V>       值类型
     * @param keyType   键类型
     * @param valueType 值类型
     * @param value     被转换的值
     * @return {@link Map}
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> toMap(Class<K> keyType, Class<V> valueType, Object value) {
        if (value instanceof Map) {
            return toMap((Class<? extends Map<?, ?>>) value.getClass(), keyType, valueType, value);
        } else {
            return toMap(HashMap.class, keyType, valueType, value);
        }
    }

    /**
     * 转换为Map
     *
     * @param mapType   转后的具体Map类型
     * @param <K>       键类型
     * @param <V>       值类型
     * @param keyType   键类型
     * @param valueType 值类型
     * @param value     被转换的值
     * @return {@link Map}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <K, V> Map<K, V> toMap(
            Class<? extends Map> mapType, Class<K> keyType, Class<V> valueType, Object value) {
        return (Map<K, V>) new MapConverter(mapType, keyType, valueType).convert(value, null);
    }

    /**
     * 转换值为指定类型，类型采用字符串表示
     *
     * @param <T>       目标类型
     * @param className 类的字符串表示
     * @param value     值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convertByClassName(String className, Object value) throws ConvertException {
        return convert((Type) ToolBytecode.forClassNameSilent(className), value);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>   目标类型
     * @param type  类型
     * @param value 值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(Class<T> type, Object value) throws ConvertException {
        return convert((Type) type, value);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>       目标类型
     * @param reference 类型参考，用于持有转换后的泛型类型
     * @param value     值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(TypeReference<T> reference, Object value) throws ConvertException {
        return convert(reference.getType(), value, null);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>   目标类型
     * @param type  类型
     * @param value 值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(Type type, Object value) throws ConvertException {
        return convert(type, value, null);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>          目标类型
     * @param type         类型
     * @param value        值
     * @param defaultValue 默认值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(Class<T> type, Object value, T defaultValue) throws ConvertException {
        return convert((Type) type, value, defaultValue);
    }

    /**
     * 转换值为指定类型
     *
     * @param <T>          目标类型
     * @param type         类型
     * @param value        值
     * @param defaultValue 默认值
     * @return 转换后的值
     * @throws ConvertException 转换器不存在
     */
    public static <T> T convert(Type type, Object value, T defaultValue) throws ConvertException {
        return convertWithCheck(type, value, defaultValue, false);
    }

    /**
     * 转换值为指定类型，不抛异常转换<br>
     * 当转换失败时返回{@code null}
     *
     * @param <T>   目标类型
     * @param type  目标类型
     * @param value 值
     * @return 转换后的值，转换失败返回null
     */
    public static <T> T convertQuietly(Type type, Object value) {
        return convertQuietly(type, value, null);
    }

    /**
     * 转换值为指定类型，不抛异常转换<br>
     * 当转换失败时返回默认值
     *
     * @param <T>          目标类型
     * @param type         目标类型
     * @param value        值
     * @param defaultValue 默认值
     * @return 转换后的值
     */
    public static <T> T convertQuietly(Type type, Object value, T defaultValue) {
        return convertWithCheck(type, value, defaultValue, true);
    }

    /**
     * 转换值为指定类型，可选是否不抛异常转换<br>
     * 当转换失败时返回默认值
     *
     * @param <T>          目标类型
     * @param type         目标类型
     * @param value        值
     * @param defaultValue 默认值
     * @param quietly      是否静默转换，true不抛异常
     * @return 转换后的值
     */
    public static <T> T convertWithCheck(Type type, Object value, T defaultValue, boolean quietly) {
        final ConverterRegistry registry = ConverterRegistry.getInstance();
        try {
            return registry.convert(type, value, defaultValue);
        } catch (Exception e) {
            if (quietly) {
                return defaultValue;
            }
            throw e;
        }
    }

    // ----------------------------------------------------------------------- 全角半角转换

    // -------------------------------------------------------------------------- 数字和英文转换

    /**
     * 将阿拉伯数字转为英文表达方式
     *
     * @param number {@link Number}对象
     * @return 英文表达式
     */
    public static String numberToWord(Number number) {
        return NumberWordFormatter.format(number);
    }

    /**
     * 将阿拉伯数字转为精简表示形式，例如:
     *
     * <pre>
     *     1200 -》 1.2k
     * </pre>
     *
     * @param number {@link Number}对象
     * @return 英文表达式
     */
    public static String numberToSimple(Number number) {
        return NumberWordFormatter.formatSimple(number.longValue());
    }

    /**
     * 将阿拉伯数字转为中文表达方式
     *
     * @param number           数字
     * @param isUseTraditional 是否使用繁体字（金额形式）
     * @return 中文
     */
    public static String numberToChinese(double number, boolean isUseTraditional) {
        return NumberChineseFormatter.format(number, isUseTraditional);
    }

    /**
     * 数字中文表示形式转数字
     *
     * <ul>
     *   <li>一百一十二 -》 112
     *   <li>一千零一十二 -》 1012
     * </ul>
     *
     * @param number 数字中文表示
     * @return 数字
     */
    public static int chineseToNumber(String number) {
        return NumberChineseFormatter.chineseToNumber(number);
    }

    /**
     * 金额转为中文形式
     *
     * @param n 数字
     * @return 中文大写数字
     */
    public static String digitToChinese(Number n) {
        if (null == n) {
            return "零";
        }
        return NumberChineseFormatter.format(n.doubleValue(), true, true);
    }

    /**
     * 中文大写数字金额转换为数字，返回结果以元为单位的BigDecimal类型数字<br>
     * 如： “陆万柒仟伍佰伍拾陆元叁角贰分”返回“67556.32” “叁角贰分”返回“0.32”
     *
     * @param chineseMoneyAmount 中文大写数字金额
     * @return 返回结果以元为单位的BigDecimal类型数字
     */
    public static BigDecimal chineseMoneyToNumber(String chineseMoneyAmount) {
        return NumberChineseFormatter.chineseMoneyToNumber(chineseMoneyAmount);
    }
}

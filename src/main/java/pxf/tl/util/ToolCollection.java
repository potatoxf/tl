package pxf.tl.util;


import pxf.tl.api.JavaEnvironment;
import pxf.tl.bean.BeanUtil;
import pxf.tl.collection.map.CaseInsensitiveMap;
import pxf.tl.collection.TransCollection;
import pxf.tl.collection.UniqueKeySet;
import pxf.tl.collection.map.MapUtil;
import pxf.tl.comparator.PinyinComparator;
import pxf.tl.comparator.PropertyComparator;
import pxf.tl.comparator.ToolCompare;
import pxf.tl.convert.Convert;
import pxf.tl.exception.UtilException;
import pxf.tl.function.FunctionThrow;
import pxf.tl.function.LoopEntryConsumer;
import pxf.tl.help.Assert;
import pxf.tl.help.New;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.iter.*;
import pxf.tl.function.Editor;
import pxf.tl.math.hash.Hash32;
import pxf.tl.text.StrJoiner;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 集合相关工具类
 *
 * <p>此工具方法针对{@link Collection}及其实现类封装的工具。
 *
 * <p>由于{@link Collection} 实现了{@link Iterable}接口，因此部分工具此类不提供，而是在中提供
 *
 * @author potatoxf
 */
public final class ToolCollection {

    private static final boolean FIRST_PAGE_ZERO = JavaEnvironment.getSystemProperty("TL_IS_FIRST_PAGE_ZERO", false, s -> Safe.toBoolean(s, false));

    private ToolCollection() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    /**
     * @param main
     * @param others
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> Set<T> differenceSet(Collection<T> main, Collection<T>... others) {
        Set<T> result = new HashSet<>(main);
        for (Collection<T> other : others) {
            result.removeAll(other);
        }
        return result;
    }

    /**
     * 通过字符串将集合里的元素去重
     *
     * @param inputs 输入集合
     * @param <T>    元素类型
     * @return 去重后的集合
     */
    public static <T> Collection<T> distinctByString(Collection<T> inputs) {
        return distinctByString(inputs, null);
    }

    /**
     * 通过字符串将集合里的元素去重
     *
     * @param inputs          输入集合
     * @param toStringHandler 将元素转换成字符串
     * @param <T>             元素类型
     * @return 去重后的集合
     */
    public static <T> Collection<T> distinctByString(
            Collection<T> inputs, Function<T, String> toStringHandler) {
        if (toStringHandler == null) {
            toStringHandler = T::toString;
        }
        Map<String, T> distinct = new LinkedHashMap<>(inputs.size());
        for (T t : inputs) {
            distinct.put(toStringHandler.apply(t), t);
        }
        return distinct.values();
    }

    /**
     * @param removedCollection
     * @param targetCollection
     * @param condition
     * @param <T>
     */
    public static <T> void removeAllByCondition(
            Collection<? extends T> removedCollection,
            Collection<? extends T> targetCollection,
            BiPredicate<? super T, ? super T> condition) {
        Iterator<? extends T> it1 = removedCollection.iterator();
        while (it1.hasNext()) {
            T a1 = it1.next();
            Iterator<? extends T> it2 = targetCollection.iterator();
            boolean isFind = false;
            while (it2.hasNext()) {
                T a2 = it2.next();
                if (condition.test(a1, a2)) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                it1.remove();
            }
        }
    }

    /**
     * @param collection1
     * @param collection2
     * @param targetExtractor
     * @param targetComparator
     * @param targetCondition
     */
    public static <E, T> List<T>[] compareListByCondition(
            final Collection<E> collection1,
            final Collection<E> collection2,
            final Function<E, T> targetExtractor,
            final Comparator<T> targetComparator,
            final BiPredicate<T, T> targetCondition) {

        Stream<T> stream1 = differenceSet(collection1, collection2).stream().map(targetExtractor);
        if (targetComparator != null) {
            stream1 = stream1.sorted(targetComparator);
        }
        List<T> list1 = stream1.collect(Collectors.toList());

        Stream<T> stream2 = differenceSet(collection2, collection1).stream().map(targetExtractor);
        if (targetComparator != null) {
            stream2 = stream2.sorted(targetComparator);
        }
        List<T> list2 = stream2.collect(Collectors.toList());

        removeAllByCondition(list1, list2, targetCondition);
        removeAllByCondition(list2, list1, targetCondition);
        return new List[]{list1, list2};
    }

    /**
     * 将所有数组元素添加到集合中
     *
     * @param container 集合容器
     * @param elements  元素数组
     */
    public static void addAll(Collection<Boolean> container, boolean... elements) {
        for (boolean element : elements) {
            container.add(element);
        }
    }

    /**
     * 将所有数组元素添加到集合中
     *
     * @param container 集合容器
     * @param elements  元素数组
     */
    public static void addAll(Collection<Byte> container, byte... elements) {
        for (byte element : elements) {
            container.add(element);
        }
    }

    /**
     * 将所有数组元素添加到集合中
     *
     * @param container 集合容器
     * @param elements  元素数组
     */
    public static void addAll(Collection<Character> container, char... elements) {
        for (char element : elements) {
            container.add(element);
        }
    }

    /**
     * 将所有数组元素添加到集合中
     *
     * @param container 集合容器
     * @param elements  元素数组
     */
    public static void addAll(Collection<Short> container, short... elements) {
        for (short element : elements) {
            container.add(element);
        }
    }

    /**
     * 将所有数组元素添加到集合中
     *
     * @param container 集合容器
     * @param elements  元素数组
     */
    public static void addAll(Collection<Integer> container, int... elements) {
        for (int element : elements) {
            container.add(element);
        }
    }

    /**
     * 将所有数组元素添加到集合中
     *
     * @param container 集合容器
     * @param elements  元素数组
     */
    public static void addAll(Collection<Long> container, long... elements) {
        for (long element : elements) {
            container.add(element);
        }
    }

    /**
     * 将所有数组元素添加到集合中
     *
     * @param container 集合容器
     * @param elements  元素数组
     */
    public static void addAll(Collection<Float> container, float... elements) {
        for (float element : elements) {
            container.add(element);
        }
    }

    /**
     * 将所有数组元素添加到集合中
     *
     * @param container 集合容器
     * @param elements  元素数组
     */
    public static void addAll(Collection<Double> container, double... elements) {
        for (double element : elements) {
            container.add(element);
        }
    }

    /**
     * 加入全部
     *
     * @param <T>        集合元素类型
     * @param collection 被加入的集合 {@link Collection}
     * @param values     要加入的内容数组
     * @return 原集合
     */
    public static <T> Collection<T> addAll(Collection<T> collection, T[] values) {
        if (null != collection && null != values) {
            for (T element : values)
                if (element != null) {
                    collection.add(element);
                }
        }
        return collection;
    }

    /**
     * 加入全部
     *
     * @param <T>        集合元素类型
     * @param collection 被加入的集合 {@link Collection}
     * @param iterator   要加入的{@link Iterator}
     * @return 原集合
     */
    public static <T> Collection<T> addAll(Collection<T> collection, Iterator<T> iterator) {
        if (null != collection && null != iterator) {
            while (iterator.hasNext()) {
                collection.add(iterator.next());
            }
        }
        return collection;
    }

    /**
     * 加入全部
     *
     * @param <T>        集合元素类型
     * @param collection 被加入的集合 {@link Collection}
     * @param iterable   要加入的内容{@link Iterable}
     * @return 原集合
     */
    public static <T> Collection<T> addAll(Collection<T> collection, Iterable<T> iterable) {
        if (iterable == null) {
            return collection;
        }
        return addAll(collection, iterable.iterator());
    }

    /**
     * 加入全部
     *
     * @param <T>         集合元素类型
     * @param collection  被加入的集合 {@link Collection}
     * @param enumeration 要加入的内容{@link Enumeration}
     * @return 原集合
     */
    public static <T> Collection<T> addAll(Collection<T> collection, Enumeration<T> enumeration) {
        if (null != collection && null != enumeration) {
            while (enumeration.hasMoreElements()) {
                collection.add(enumeration.nextElement());
            }
        }
        return collection;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param container    Map集合
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    public static boolean getBooleanValue(Map<?, ?> container, Object key, boolean defaultValue) {
        Boolean value = getBooleanValue(container, key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param container    Map集合
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    public static char getCharValue(Map<?, ?> container, Object key, char defaultValue) {
        Character value = getCharacterValue(container, key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param container    Map集合
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    public static byte getByteValue(Map<?, ?> container, Object key, byte defaultValue) {
        Byte value = getByteValue(container, key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param container    Map集合
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    public static short getShortValue(Map<?, ?> container, Object key, short defaultValue) {
        Short value = getShortValue(container, key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param container    Map集合
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    public static int getIntValue(Map<?, ?> container, Object key, int defaultValue) {
        Integer value = getIntegerValue(container, key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param container    Map集合
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    public static long getLongValue(Map<?, ?> container, Object key, long defaultValue) {
        Long value = getLongValue(container, key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param container    Map集合
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    public static float getFloatValue(Map<?, ?> container, Object key, float defaultValue) {
        Float value = getFloatValue(container, key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param container    Map集合
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    public static double getDoubleValue(Map<?, ?> container, Object key, double defaultValue) {
        Double value = getDoubleValue(container, key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static Boolean getBooleanValue(Map<?, ?> container, Object key) {
        if (container != null) {
            Object value = container.get(key);
            if (value != null) {
                if (value instanceof Boolean) {
                    return (Boolean) value;
                } else {
                    Number numberValue = getNumberValue(container, key);
                    if (numberValue != null) {
                        return numberValue.intValue() != 0;
                    } else {
                        String booleanString;
                        if (value instanceof String) {
                            booleanString = (String) value;
                        } else {
                            booleanString = value.toString();
                        }
                        if ("true".equalsIgnoreCase(booleanString)) {
                            return true;
                        }
                        if ("false".equalsIgnoreCase(booleanString)) {
                            return false;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static Character getCharacterValue(Map<?, ?> container, Object key) {
        if (container != null) {
            Object value = container.get(key);
            if (value != null) {
                if (value instanceof Character) {
                    return (Character) value;
                } else {
                    Number numberValue = getNumberValue(container, key);
                    if (numberValue != null) {
                        int i = numberValue.intValue();
                        if (i >= 0 && i <= 9) {
                            return String.valueOf(i).charAt(0);
                        }
                    } else {
                        CharSequence string;
                        if (value instanceof CharSequence) {
                            string = (CharSequence) value;
                        } else {
                            string = value.toString();
                        }
                        if (string.length() == 1) {
                            return string.charAt(0);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static Byte getByteValue(Map<?, ?> container, Object key) {
        Number numberValue = getNumberValue(container, key);
        if (numberValue != null) {
            return numberValue.byteValue();
        }
        return null;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static Short getShortValue(Map<?, ?> container, Object key) {
        Number numberValue = getNumberValue(container, key);
        if (numberValue != null) {
            return numberValue.shortValue();
        }
        return null;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static Integer getIntegerValue(Map<?, ?> container, Object key) {
        Number numberValue = getNumberValue(container, key);
        if (numberValue != null) {
            return numberValue.intValue();
        }
        return null;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static Long getLongValue(Map<?, ?> container, Object key) {
        Number numberValue = getNumberValue(container, key);
        if (numberValue != null) {
            return numberValue.longValue();
        }
        return null;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static Float getFloatValue(Map<?, ?> container, Object key) {
        Number numberValue = getNumberValue(container, key);
        if (numberValue != null) {
            return numberValue.floatValue();
        }
        return null;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static Double getDoubleValue(Map<?, ?> container, Object key) {
        Number numberValue = getNumberValue(container, key);
        if (numberValue != null) {
            return numberValue.doubleValue();
        }
        return null;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static BigInteger getBigIntegerValue(Map<?, ?> container, Object key) {
        Number numberValue = getNumberValue(container, key);
        if (numberValue != null) {
            if (numberValue instanceof BigDecimal) {
                return ((BigDecimal) numberValue).toBigIntegerExact();
            } else {
                return BigInteger.valueOf(numberValue.longValue());
            }
        }
        return null;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static BigDecimal getBigDecimalValue(Map<?, ?> container, Object key) {
        Number numberValue = getNumberValue(container, key);
        if (numberValue != null) {
            if (numberValue instanceof BigDecimal) {
                return (BigDecimal) numberValue;
            } else {
                return BigDecimal.valueOf(numberValue.doubleValue());
            }
        }
        return null;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static String getStringValue(Map<?, ?> container, Object key) {
        return getStringValue(container, key, null);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static String getStringValue(Map<?, ?> container, Object key, String defaultValue) {
        if (container != null) {
            Object value = container.get(key);
            if (value != null) {
                if (value instanceof String) {
                    return (String) value;
                } else {
                    return value.toString();
                }
            }
        }
        return defaultValue;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static Object getObjectValue(Map<?, ?> container, Object key) {
        return getObjectValue(container, key, null);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param container Map集合
     * @param key       键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    public static Object getObjectValue(Map<?, ?> container, Object key, Object defaultValue) {
        if (container != null) {
            Object value = container.get(key);
            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }

    /**
     * 获取数字值
     *
     * @param container Map容器
     * @param key       键
     * @return 返回数字元素
     */
    private static Number getNumberValue(Map<?, ?> container, Object key) {
        if (container != null) {
            Object value = container.get(key);
            if (value != null) {
                if (value instanceof Number) {
                    return (Number) value;
                } else {
                    String numberString;
                    if (value instanceof String) {
                        numberString = (String) value;
                    } else {
                        numberString = value.toString();
                    }
                    try {
                        return new BigDecimal(numberString);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return null;
    }

    /**
     * 构造不可修改Set
     *
     * @param values 一系列值
     * @param <T>    类型
     * @return 返回Set
     */
    @SafeVarargs
    public static <T> Set<T> ofUnmodifiableSet(T... values) {
        return Collections.unmodifiableSet(ofSet(values));
    }

    /**
     * 构造Set
     *
     * @param values 一系列值
     * @param <T>    类型
     * @return 返回Set
     */
    @SafeVarargs
    public static <T> Set<T> ofSet(T... values) {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, values);
        return set;
    }

    /**
     * 构造Set
     *
     * @param values 一系列值
     * @param <T>    类型
     * @return 返回Set
     */
    @SafeVarargs
    public static <T> Set<T> ofLinkedSet(T... values) {
        Set<T> set = new LinkedHashSet<T>();
        Collections.addAll(set, values);
        return set;
    }

    /**
     * 构造不可修改List
     *
     * @param values 一系列值
     * @param <T>    类型
     * @return 返回List
     */
    @SafeVarargs
    public static <T> List<T> ofUnmodifiableList(T... values) {
        return Collections.unmodifiableList(ofList(values));
    }

    /**
     * 构造List
     *
     * @param values 一系列值
     * @param <T>    类型
     * @return 返回List
     */
    @SafeVarargs
    public static <T> List<T> ofList(T... values) {
        List<T> list = new ArrayList<>(values.length);
        Collections.addAll(list, values);
        return list;
    }

    /**
     * 处理Map的值映射，例如{@code Map<K,V>--->Map<K,NV>}
     *
     * <p>默认： 跳过空值 跳过异常
     *
     * @param input        输入{@code Map<K,V>}
     * @param factoryThrow 值处理器
     * @param <K>          键类型
     * @param <V>          值类型
     * @param <NV>         新值类型
     * @param <E>          异常类型
     */
    public static <K, V, NV, E extends Throwable> Map<K, NV> processMapValue(
            Map<K, V> input, FunctionThrow<V, NV, E> factoryThrow) {
        return processMapValue(input, factoryThrow, true);
    }

    /**
     * 处理Map的值映射，例如{@code Map<K,V>--->Map<K,NV>}
     *
     * <p>默认： 跳过异常
     *
     * @param input        输入{@code Map<K,V>}
     * @param factoryThrow 值处理器
     * @param isSkipNull   是否跳过空值，如果不跳过空值则值可能为null
     * @param <K>          键类型
     * @param <V>          值类型
     * @param <NV>         新值类型
     * @param <E>          异常类型
     */
    public static <K, V, NV, E extends Throwable> Map<K, NV> processMapValue(
            Map<K, V> input, FunctionThrow<V, NV, E> factoryThrow, boolean isSkipNull) {
        Map<K, NV> container = new HashMap<K, NV>(input.size(), 1);
        processMapValue(input, container, factoryThrow, isSkipNull);
        return container;
    }

    /**
     * 处理Map的值映射，例如{@code Map<K,V>--->Map<K,NV>}
     *
     * <p>默认： 跳过空值 跳过异常
     *
     * @param input        输入{@code Map<K,V>}
     * @param container    容器{@code Map<K,NV>}
     * @param factoryThrow 值处理器
     * @param <K>          键类型
     * @param <V>          值类型
     * @param <NV>         新值类型
     * @param <E>          异常类型
     */
    public static <K, V, NV, E extends Throwable> void processMapValue(
            Map<K, V> input, Map<K, NV> container, FunctionThrow<V, NV, E> factoryThrow) {
        processMapValue(input, container, factoryThrow, true);
    }

    /**
     * 处理Map的值映射，例如{@code Map<K,V>--->Map<K,NV>}
     *
     * <p>默认： 跳过异常
     *
     * @param input        输入{@code Map<K,V>}
     * @param container    容器{@code Map<K,NV>}
     * @param factoryThrow 值处理器
     * @param isSkipNull   是否跳过空值，如果不跳过空值则值可能为null
     * @param <K>          键类型
     * @param <V>          值类型
     * @param <NV>         新值类型
     * @param <E>          异常类型
     */
    public static <K, V, NV, E extends Throwable> void processMapValue(
            Map<K, V> input,
            Map<K, NV> container,
            FunctionThrow<V, NV, E> factoryThrow,
            boolean isSkipNull) {
        try {
            doProcessMap(input, container, factoryThrow, isSkipNull, true);
        } catch (Throwable ignored) {
            // haven't exception
        }
    }

    /**
     * 处理Map的值映射，例如{@code Map<K,V>--->Map<K,NV>}
     *
     * <p>默认： 跳过空值 不跳过异常
     *
     * @param input        输入{@code Map<K,V>}
     * @param factoryThrow 值处理器
     * @param <K>          键类型
     * @param <V>          值类型
     * @param <NV>         新值类型
     * @param <E>          异常类型
     * @throws E 如果失败抛出异常
     */
    public static <K, V, NV, E extends Throwable> Map<K, NV> processMapValueThrow(
            Map<K, V> input, FunctionThrow<V, NV, E> factoryThrow) throws E {
        return processMapValueThrow(input, factoryThrow, true);
    }

    /**
     * 处理Map的值映射，例如{@code Map<K,V>--->Map<K,NV>}
     *
     * <p>默认： 不跳过异常
     *
     * @param input        输入{@code Map<K,V>}
     * @param factoryThrow 值处理器
     * @param isSkipNull   是否跳过空值，如果不跳过空值则值可能为null
     * @param <K>          键类型
     * @param <V>          值类型
     * @param <NV>         新值类型
     * @param <E>          异常类型
     * @throws E 如果失败抛出异常
     */
    public static <K, V, NV, E extends Throwable> Map<K, NV> processMapValueThrow(
            Map<K, V> input, FunctionThrow<V, NV, E> factoryThrow, boolean isSkipNull) throws E {
        return processMapValueThrow(input, factoryThrow, isSkipNull, false);
    }

    /**
     * 处理Map的值映射，例如{@code Map<K,V>--->Map<K,NV>}
     *
     * @param input           输入{@code Map<K,V>}
     * @param factoryThrow    值处理器
     * @param isSkipNull      是否跳过空值，如果不跳过空值则值可能为null
     * @param isSkipException 是否跳过异常，如果不跳过异常则发生异常的时候会终止循环
     * @param <K>             键类型
     * @param <V>             值类型
     * @param <NV>            新值类型
     * @param <E>             异常类型
     * @throws E 如果失败抛出异常
     */
    public static <K, V, NV, E extends Throwable> Map<K, NV> processMapValueThrow(
            Map<K, V> input,
            FunctionThrow<V, NV, E> factoryThrow,
            boolean isSkipNull,
            boolean isSkipException)
            throws E {
        Map<K, NV> container = new HashMap<K, NV>(input.size(), 1);
        doProcessMap(input, container, factoryThrow, isSkipNull, isSkipException);
        return container;
    }

    /**
     * 处理Map的值映射，例如{@code Map<K,V>--->Map<K,NV>}
     *
     * <p>默认： 跳过空值 不跳过异常
     *
     * @param input        输入{@code Map<K,V>}
     * @param container    容器{@code Map<K,NV>}
     * @param factoryThrow 值处理器
     * @param <K>          键类型
     * @param <V>          值类型
     * @param <NV>         新值类型
     * @param <E>          异常类型
     * @throws E 如果失败抛出异常
     */
    public static <K, V, NV, E extends Throwable> void processMapValueThrow(
            Map<K, V> input, Map<K, NV> container, FunctionThrow<V, NV, E> factoryThrow) throws E {
        doProcessMap(input, container, factoryThrow, true, false);
    }

    /**
     * 处理Map的值映射，例如{@code Map<K,V>--->Map<K,NV>}
     *
     * <p>默认： 不跳过异常
     *
     * @param input        输入{@code Map<K,V>}
     * @param container    容器{@code Map<K,NV>}
     * @param factoryThrow 值处理器
     * @param isSkipNull   是否跳过空值，如果不跳过空值则值可能为null
     * @param <K>          键类型
     * @param <V>          值类型
     * @param <NV>         新值类型
     * @param <E>          异常类型
     * @throws E 如果失败抛出异常
     */
    public static <K, V, NV, E extends Throwable> void processMapValueThrow(
            Map<K, V> input,
            Map<K, NV> container,
            FunctionThrow<V, NV, E> factoryThrow,
            boolean isSkipNull)
            throws E {
        doProcessMap(input, container, factoryThrow, isSkipNull, false);
    }

    /**
     * 处理Map的值映射，例如{@code Map<K,V>--->Map<K,NV>}
     *
     * @param input           输入{@code Map<K,V>}
     * @param container       容器{@code Map<K,NV>}
     * @param factoryThrow    值处理器
     * @param isSkipNull      是否跳过空值，如果不跳过空值则值可能为null
     * @param isSkipException 是否跳过异常，如果不跳过异常则发生异常的时候会终止循环
     * @param <K>             键类型
     * @param <V>             值类型
     * @param <NV>            新值类型
     * @param <E>             异常类型
     * @throws E 如果失败抛出异常
     */
    private static <K, V, NV, E extends Throwable> void doProcessMap(
            Map<K, V> input,
            Map<K, NV> container,
            FunctionThrow<V, NV, E> factoryThrow,
            boolean isSkipNull,
            boolean isSkipException)
            throws E {
        for (Entry<K, V> entry : input.entrySet()) {
            try {
                NV newValue = factoryThrow.apply(entry.getValue());
                if (newValue != null || !isSkipNull) {
                    container.put(entry.getKey(), newValue);
                }
            } catch (Throwable e) {
                if (!isSkipException) {
                    //noinspection unchecked
                    throw (E) e;
                }
            }
        }
    }

    /**
     * 将Map中的值转换成数组
     *
     * @param map  {@code Map<K,Object>}
     * @param keys 键
     * @param <K>  键类型
     * @return 返回数组
     */
    @SafeVarargs
    public static <K> Object[] toValueArray(Map<K, Object> map, K... keys) {
        Object[] result = new Object[keys.length];
        for (int i = 0; i < keys.length; i++) {
            result[i] = map.get(keys[i]);
        }
        return result;
    }

    /**
     * 将Map转换成
     *
     * @param input {@code Map<?, T>}
     * @return {@code Map<String, Object>}
     */
    public static <T> Map<String, T> toStringObjectMap(Map<?, T> input) {
        Map<String, T> map = new HashMap<String, T>(input.size(), 1);
        for (Entry<?, T> entry : input.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof String) {
                map.put(key.toString(), entry.getValue());
            }
        }
        return map;
    }

    /**
     * 反置容器Map
     *
     * @param input     输入Map
     * @param container 容器Map
     * @param <K1>      键类型
     * @param <K2>      键类型
     */
    public static <K1, K2> void reserveMap(
            Map<? extends K1, ? extends K2> input, Map<? super K2, ? super K1> container) {
        for (K1 k1 : input.keySet()) {
            container.put(input.get(k1), k1);
        }
    }

    /**
     * 将列表的Map按照指定键进行分组
     *
     * @param mapList ma列表
     * @param keys    多个键值
     * @param <K>     键类型
     * @param <V>     值类型
     * @return {@code List<Map<K, V>>>}
     */
    public static <K, V> Map<Map<K, V>, List<Map<K, V>>> groupBy(
            List<? extends Map<K, V>> mapList, K[] keys) {
        Map<Map<K, V>, List<Map<K, V>>> result = new LinkedHashMap<>();
        for (Map<K, V> entry : mapList) {
            Map<K, V> mapKey = new LinkedHashMap<>();
            for (K key : keys) {
                mapKey.put(key, entry.get(key));
            }
            result.computeIfAbsent(mapKey, k -> new LinkedList<>()).add(entry);
        }
        return result;
    }

    public static <T> Set<T> toSet(T[] input) {
        return toSet(input, t -> t);
    }

    public static <T> Set<T> toSet(Collection<T> input) {
        return toSet(input, t -> t);
    }

    public static <T, R> Set<R> toSet(T[] input, Function<? super T, ? extends R> converter) {
        return input == null || input.length == 0
                ? new LinkedHashSet<>()
                : Arrays.stream(input)
                .map(converter)
                .filter(Whether::noNvl)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static <T, R> Set<R> toSet(
            Collection<T> input, Function<? super T, ? extends R> converter) {
        return input == null
                ? new LinkedHashSet<>()
                : input.stream()
                .map(converter)
                .filter(Whether::noNvl)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static <T> List<T> unmodifiableList(List<? extends T> list, boolean isPossibleNull) {
        return list == null
                ? (isPossibleNull ? null : Collections.emptyList())
                : Collections.unmodifiableList(list);
    }

    public static <T> Set<T> unmodifiableSet(Set<? extends T> set, boolean isPossibleNull) {
        return set == null
                ? (isPossibleNull ? null : Collections.emptySet())
                : Collections.unmodifiableSet(set);
    }

    public static <K, V> Map<K, V> unmodifiableMap(
            Map<? extends K, ? extends V> map, boolean isPossibleNull) {
        return map == null
                ? (isPossibleNull ? null : Collections.emptyMap())
                : Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, Integer> sortMap(
            @Nonnull Map<K, V> sortedMap,
            @Nonnull Comparator<K> comparator,
            @Nonnull Map<K, Integer> sortResult) {
        List<K> list = sortMap(sortedMap, comparator);
        IntStream.range(0, list.size()).forEach(i -> sortResult.put(list.get(i), i));
        return sortResult;
    }

    public static <K, V> List<K> sortMap(
            @Nonnull Map<K, V> sortedMap, @Nonnull Comparator<K> comparator) {
        List<K> list = new ArrayList<>(sortedMap.size());
        sortedMap.keySet().stream().sorted(comparator).forEach(list::add);
        return list;
    }

    public static <K, V> Map<K, V> filterMap(
            @Nonnull Map<K, V> filteredMap,
            @Nonnull BiPredicate<K, V> condition,
            @Nonnull Map<K, V> filterResult) {
        filteredMap.forEach(
                (k, v) -> {
                    if (condition.test(k, v)) {
                        filterResult.put(k, v);
                    }
                });
        return filterResult;
    }

    /**
     * 两个集合的并集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留最多的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c, c]，此结果中只保留了三个c
     *
     * @param <T>   集合元素类型
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 并集的集合，返回 {@link ArrayList}
     */
    public static <T> Collection<T> union(Collection<T> coll1, Collection<T> coll2) {
        if (Whether.empty(coll1) && Whether.empty(coll2)) {
            return new ArrayList<>();
        }
        if (Whether.empty(coll1)) {
            return new ArrayList<>(coll2);
        } else if (Whether.empty(coll2)) {
            return new ArrayList<>(coll1);
        }

        final ArrayList<T> list = new ArrayList<>(Math.max(coll1.size(), coll2.size()));
        final Map<T, Integer> map1 = countMap(coll1);
        final Map<T, Integer> map2 = countMap(coll2);
        final Set<T> elts = New.set(true, coll2);
        elts.addAll(coll1);
        int m;
        for (T t : elts) {
            m = Math.max(Convert.toInt(map1.get(t), 0), Convert.toInt(map2.get(t), 0));
            for (int i = 0; i < m; i++) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * 多个集合的并集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留最多的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c, c]，此结果中只保留了三个c
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 并集的集合，返回 {@link ArrayList}
     */
    @SafeVarargs
    public static <T> Collection<T> union(
            Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
        Collection<T> union = union(coll1, coll2);
        for (Collection<T> coll : otherColls) {
            if (Whether.empty(coll)) {
                continue;
            }
            union = union(union, coll);
        }
        return union;
    }

    /**
     * 多个集合的非重复并集，类似于SQL中的“UNION DISTINCT”<br>
     * 针对一个集合中存在多个相同元素的情况，只保留一个<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c]，此结果中只保留了一个c
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 并集的集合，返回 {@link LinkedHashSet}
     */
    @SafeVarargs
    public static <T> Set<T> unionDistinct(
            Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
        final Set<T> result;
        if (Whether.empty(coll1)) {
            result = new LinkedHashSet<>();
        } else {
            result = new LinkedHashSet<>(coll1);
        }

        if (Whether.noEmpty(coll2)) {
            result.addAll(coll2);
        }

        if (Whether.noEmpty(otherColls)) {
            for (Collection<T> otherColl : otherColls) {
                if (Whether.empty(otherColl)) {
                    continue;
                }
                result.addAll(otherColl);
            }
        }

        return result;
    }

    /**
     * 多个集合的完全并集，类似于SQL中的“UNION ALL”<br>
     * 针对一个集合中存在多个相同元素的情况，保留全部元素<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c, c, a, b, c, c]
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 并集的集合，返回 {@link ArrayList}
     */
    @SafeVarargs
    public static <T> List<T> unionAll(
            Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
        final List<T> result;
        if (Whether.empty(coll1)) {
            result = new ArrayList<>();
        } else {
            result = new ArrayList<>(coll1);
        }

        if (Whether.noEmpty(coll2)) {
            result.addAll(coll2);
        }

        if (Whether.noEmpty(otherColls)) {
            for (Collection<T> otherColl : otherColls) {
                if (Whether.empty(otherColl)) {
                    continue;
                }
                result.addAll(otherColl);
            }
        }

        return result;
    }

    /**
     * 两个集合的交集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留最少的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c]，此结果中只保留了两个c
     *
     * @param <T>   集合元素类型
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 交集的集合，返回 {@link ArrayList}
     */
    public static <T> Collection<T> intersection(Collection<T> coll1, Collection<T> coll2) {
        if (Whether.noEmpty(coll1) && Whether.noEmpty(coll2)) {
            final ArrayList<T> list = new ArrayList<>(Math.min(coll1.size(), coll2.size()));
            final Map<T, Integer> map1 = countMap(coll1);
            final Map<T, Integer> map2 = countMap(coll2);
            final Set<T> elts = New.set(false, coll2);
            int m;
            for (T t : elts) {
                m = Math.min(Convert.toInt(map1.get(t), 0), Convert.toInt(map2.get(t), 0));
                for (int i = 0; i < m; i++) {
                    list.add(t);
                }
            }
            return list;
        }

        return new ArrayList<>();
    }

    /**
     * 多个集合的交集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留最少的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c]，此结果中只保留了两个c
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 交集的集合，返回 {@link ArrayList}
     */
    @SafeVarargs
    public static <T> Collection<T> intersection(
            Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
        Collection<T> intersection = intersection(coll1, coll2);
        if (Whether.empty(intersection)) {
            return intersection;
        }
        for (Collection<T> coll : otherColls) {
            intersection = intersection(intersection, coll);
            if (Whether.empty(intersection)) {
                return intersection;
            }
        }
        return intersection;
    }

    /**
     * 多个集合的交集<br>
     * 针对一个集合中存在多个相同元素的情况，只保留一个<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c]，此结果中只保留了一个c
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 交集的集合，返回 {@link LinkedHashSet}
     */
    @SafeVarargs
    public static <T> Set<T> intersectionDistinct(
            Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
        final Set<T> result;
        if (Whether.empty(coll1) || Whether.empty(coll2)) {
            // 有一个空集合就直接返回空
            return new LinkedHashSet<>();
        } else {
            result = new LinkedHashSet<>(coll1);
        }

        if (Whether.noEmpty(otherColls)) {
            for (Collection<T> otherColl : otherColls) {
                if (Whether.noEmpty(otherColl)) {
                    result.retainAll(otherColl);
                } else {
                    // 有一个空集合就直接返回空
                    return new LinkedHashSet<>();
                }
            }
        }

        result.retainAll(coll2);

        return result;
    }

    /**
     * 两个集合的差集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留两个集合中此元素个数差的个数<br>
     * 例如：
     *
     * <pre>
     *     disjunction([a, b, c, c, c], [a, b, c, c]) -》 [c]
     *     disjunction([a, b], [])                    -》 [a, b]
     *     disjunction([a, b, c], [b, c, d])          -》 [a, d]
     * </pre>
     * <p>
     * 任意一个集合为空，返回另一个集合<br>
     * 两个集合无差集则返回空集合
     *
     * @param <T>   集合元素类型
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 差集的集合，返回 {@link ArrayList}
     */
    public static <T> Collection<T> disjunction(Collection<T> coll1, Collection<T> coll2) {
        if (Whether.empty(coll1)) {
            return coll2;
        }
        if (Whether.empty(coll2)) {
            return coll1;
        }

        final List<T> result = new ArrayList<>();
        final Map<T, Integer> map1 = countMap(coll1);
        final Map<T, Integer> map2 = countMap(coll2);
        final Set<T> elts = New.set(false, coll1, coll2);
        int m;
        for (T t : elts) {
            m = Math.abs(Convert.toInt(map1.get(t), 0) - Convert.toInt(map2.get(t), 0));
            for (int i = 0; i < m; i++) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * 计算集合的单差集，即只返回【集合1】中有，但是【集合2】中没有的元素，例如：
     *
     * <pre>
     *     subtract([1,2,3,4],[2,3,4,5]) -》 [1]
     * </pre>
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @param <T>   元素类型
     * @return 单差集
     */
    public static <T> Collection<T> subtract(Collection<T> coll1, Collection<T> coll2) {
        Collection<T> result = ToolObject.clone(coll1);
        if (null == result) {
            result = ToolCollection.create(coll1.getClass());
            result.addAll(coll1);
        }
        result.removeAll(coll2);
        return result;
    }

    /**
     * 计算集合的单差集，即只返回【集合1】中有，但是【集合2】中没有的元素，例如：
     *
     * <pre>
     *     subtractToList([1,2,3,4],[2,3,4,5]) -》 [1]
     * </pre>
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @param <T>   元素类型
     * @return 单差集
     */
    public static <T> List<T> subtractToList(Collection<T> coll1, Collection<T> coll2) {

        if (Whether.empty(coll1)) {
            return Collections.emptyList();
        }
        if (Whether.empty(coll2)) {
            return New.list(true, coll1);
        }

        // 将被交数用链表储存，防止因为频繁扩容影响性能
        final List<T> result = new LinkedList<>();
        Set<T> set = new HashSet<>(coll2);
        for (T t : coll1) {
            if (false == set.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * 判断指定集合是否包含指定值，如果集合为空（null或者空），返回{@code false}，否则找到元素返回{@code true}
     *
     * @param collection 集合
     * @param value      需要查找的值
     * @return 如果集合为空（null或者空），返回{@code false}，否则找到元素返回{@code true}
     * @throws ClassCastException   如果类型不一致会抛出转换异常
     * @throws NullPointerException 当指定的元素 值为 null ,或集合类不支持null 时抛出该异常
     * @see Collection#contains(Object)
     */
    public static boolean contains(Collection<?> collection, Object value) {
        return Whether.noEmpty(collection) && collection.contains(value);
    }

    /**
     * 判断指定集合是否包含指定值，如果集合为空（null或者空），返回{@code false}，否则找到元素返回{@code true}
     *
     * @param collection 集合
     * @param value      需要查找的值
     * @return 果集合为空（null或者空），返回{@code false}，否则找到元素返回{@code true}
     */
    public static boolean safeContains(Collection<?> collection, Object value) {

        try {
            return contains(collection, value);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    /**
     * 自定义函数判断集合是否包含某类值
     *
     * @param collection  集合
     * @param containFunc 自定义判断函数
     * @param <T>         值类型
     * @return 是否包含自定义规则的值
     */
    public static <T> boolean contains(Collection<T> collection, Predicate<T> containFunc) {
        if (Whether.empty(collection)) {
            return false;
        }
        for (T t : collection) {
            if (containFunc.test(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 其中一个集合在另一个集合中是否至少包含一个元素，即是两个集合是否至少有一个共同的元素
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 其中一个集合在另一个集合中是否至少包含一个元素
     * @see #intersection
     */
    public static boolean containsAny(Collection<?> coll1, Collection<?> coll2) {
        if (Whether.empty(coll1) || Whether.empty(coll2)) {
            return false;
        }
        if (coll1.size() < coll2.size()) {
            for (Object object : coll1) {
                if (coll2.contains(object)) {
                    return true;
                }
            }
        } else {
            for (Object object : coll2) {
                if (coll1.contains(object)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 集合1中是否包含集合2中所有的元素，即集合2是否为集合1的子集
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 集合1中是否包含集合2中所有的元素
     */
    public static boolean containsAll(Collection<?> coll1, Collection<?> coll2) {
        if (Whether.empty(coll1)) {
            return Whether.empty(coll2);
        }

        if (Whether.empty(coll2)) {
            return true;
        }

        if (coll1.size() < coll2.size()) {
            return false;
        }

        for (Object object : coll2) {
            if (false == coll1.contains(object)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据集合返回一个元素计数的 {@link Map}<br>
     * 所谓元素计数就是假如这个集合中某个元素出现了n次，那将这个元素做为key，n做为value<br>
     * 例如：[a,b,c,c,c] 得到：<br>
     * a: 1<br>
     * b: 1<br>
     * c: 3<br>
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return {@link Map}
     * @see ToolCollection#countMap(Iterator)
     */
    public static <T> Map<T, Integer> countMap(Iterable<T> collection) {
        return countMap(null == collection ? null : collection.iterator());
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @param func        集合元素转换器，将元素转换为字符串
     * @return 连接后的字符串
     * @see ToolCollection#join(Iterator, CharSequence, Function)
     */
    public static <T> String join(
            Iterable<T> iterable, CharSequence conjunction, Function<T, ? extends CharSequence> func) {
        if (null == iterable) {
            return null;
        }
        return join(iterable.iterator(), conjunction, func);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @return 连接后的字符串
     * @see ToolCollection#join(Iterator, CharSequence)
     */
    public static <T> String join(Iterable<T> iterable, CharSequence conjunction) {
        if (null == iterable) {
            return null;
        }
        return join(iterable.iterator(), conjunction);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @param prefix      每个元素添加的前缀，null表示不添加
     * @param suffix      每个元素添加的后缀，null表示不添加
     * @return 连接后的字符串
     */
    public static <T> String join(
            Iterable<T> iterable, CharSequence conjunction, String prefix, String suffix) {
        if (null == iterable) {
            return null;
        }
        return join(iterable.iterator(), conjunction, prefix, suffix);
    }

    /**
     * 切取部分数据<br>
     * 切取后的栈将减少这些元素
     *
     * @param <T>             集合元素类型
     * @param surplusAlaDatas 原数据
     * @param partSize        每部分数据的长度
     * @return 切取出的数据或null
     */
    public static <T> List<T> popPart(Stack<T> surplusAlaDatas, int partSize) {
        if (Whether.empty(surplusAlaDatas)) {
            return Collections.emptyList();
        }

        final List<T> currentAlaDatas = new ArrayList<>();
        int size = surplusAlaDatas.size();
        // 切割
        if (size > partSize) {
            for (int i = 0; i < partSize; i++) {
                currentAlaDatas.add(surplusAlaDatas.pop());
            }
        } else {
            for (int i = 0; i < size; i++) {
                currentAlaDatas.add(surplusAlaDatas.pop());
            }
        }
        return currentAlaDatas;
    }

    /**
     * 切取部分数据<br>
     * 切取后的栈将减少这些元素
     *
     * @param <T>             集合元素类型
     * @param surplusAlaDatas 原数据
     * @param partSize        每部分数据的长度
     * @return 切取出的数据或null
     */
    public static <T> List<T> popPart(Deque<T> surplusAlaDatas, int partSize) {
        if (Whether.empty(surplusAlaDatas)) {
            return Collections.emptyList();
        }

        final List<T> currentAlaDatas = new ArrayList<>();
        int size = surplusAlaDatas.size();
        // 切割
        if (size > partSize) {
            for (int i = 0; i < partSize; i++) {
                currentAlaDatas.add(surplusAlaDatas.pop());
            }
        } else {
            for (int i = 0; i < size; i++) {
                currentAlaDatas.add(surplusAlaDatas.pop());
            }
        }
        return currentAlaDatas;
    }

    // -----------------------------------------------------------------------------------------------
    // new HashSet

    /**
     * 创建新的集合对象
     *
     * @param <T>            集合类型
     * @param collectionType 集合类型
     * @return 集合类型对应的实例
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Collection<T> create(Class<?> collectionType) {
        Collection<T> list;
        if (collectionType.isAssignableFrom(AbstractCollection.class)) {
            // 抽象集合默认使用ArrayList
            list = new ArrayList<>();
        }

        // Set
        else if (collectionType.isAssignableFrom(HashSet.class)) {
            list = new HashSet<>();
        } else if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
            list = new LinkedHashSet<>();
        } else if (collectionType.isAssignableFrom(TreeSet.class)) {
            list =
                    new TreeSet<>(
                            (o1, o2) -> {
                                // 优先按照对象本身比较，如果没有实现比较接口，默认按照toString内容比较
                                if (o1 instanceof Comparable) {
                                    return ((Comparable<T>) o1).compareTo(o2);
                                }
                                return ToolCompare.compare(o1.toString(), o2.toString());
                            });
        } else if (collectionType.isAssignableFrom(EnumSet.class)) {
            list =
                    (Collection<T>) EnumSet.noneOf((Class<Enum>) ToolBytecode.getTypeArgument(collectionType));
        }

        // List
        else if (collectionType.isAssignableFrom(ArrayList.class)) {
            list = new ArrayList<>();
        } else if (collectionType.isAssignableFrom(LinkedList.class)) {
            list = new LinkedList<>();
        }

        // Others，直接实例化
        else {
            try {
                list = (Collection<T>) ToolBytecode.createInstanceSilent(collectionType);
            } catch (Exception e) {
                // 无法创建当前类型的对象，尝试创建父类型对象
                final Class<?> superclass = collectionType.getSuperclass();
                if (null != superclass && collectionType != superclass) {
                    return create(superclass);
                }
                throw new UtilException(e);
            }
        }
        return list;
    }

    /**
     * 去重集合
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return {@link ArrayList}
     */
    public static <T> ArrayList<T> distinct(Collection<T> collection) {
        if (Whether.empty(collection)) {
            return new ArrayList<>();
        } else if (collection instanceof Set) {
            return new ArrayList<>(collection);
        } else {
            return new ArrayList<>(new LinkedHashSet<>(collection));
        }
    }

    /**
     * 根据函数生成的KEY去重集合，如根据Bean的某个或者某些字段完成去重。<br>
     * 去重可选是保留最先加入的值还是后加入的值
     *
     * @param <T>             集合元素类型
     * @param <K>             唯一键类型
     * @param collection      集合
     * @param uniqueGenerator 唯一键生成器
     * @param override        是否覆盖模式，如果为{@code true}，加入的新值会覆盖相同key的旧值，否则会忽略新加值
     * @return {@link ArrayList}
     */
    public static <T, K> List<T> distinct(
            Collection<T> collection, Function<T, K> uniqueGenerator, boolean override) {
        if (Whether.empty(collection)) {
            return new ArrayList<>();
        }

        final UniqueKeySet<K, T> set = new UniqueKeySet<>(true, uniqueGenerator);
        if (override) {
            set.addAll(collection);
        } else {
            set.addAllIfAbsent(collection);
        }
        return new ArrayList<>(set);
    }

    /**
     * 截取集合的部分
     *
     * @param <T>        集合元素类型
     * @param collection 被截取的数组
     * @param start      开始位置（包含）
     * @param end        结束位置（不包含）
     * @return 截取后的数组，当开始位置超过最大时，返回null
     */
    public static <T> List<T> sub(Collection<T> collection, int start, int end) {
        return sub(collection, start, end, 1);
    }

    /**
     * 截取集合的部分
     *
     * @param <T>        集合元素类型
     * @param collection 被截取的数组
     * @param start      开始位置（包含）
     * @param end        结束位置（不包含）
     * @param step       步进
     * @return 截取后的数组，当开始位置超过最大时，返回空集合
     */
    public static <T> List<T> sub(Collection<T> collection, int start, int end, int step) {
        if (Whether.empty(collection)) {
            return Collections.emptyList();
        }

        final List<T> list =
                collection instanceof List ? (List<T>) collection : New.list(false, collection);
        return sub(list, start, end, step);
    }

    /**
     * 对集合按照指定长度分段，每一个段为单独的集合，返回这个集合的列表
     *
     * <p>需要特别注意的是，此方法调用{@link List#subList(int, int)}切分List， 此方法返回的是原List的视图，也就是说原List有变更，切分后的结果也会变更。
     *
     * @param <T>  集合元素类型
     * @param list 列表
     * @param size 每个段的长度
     * @return 分段列表
     * @deprecated 请使用 {@link ToolCollection#partition(List, int)}
     */
    @Deprecated
    public static <T> List<List<T>> splitList(List<T> list, int size) {
        return partition(list, size);
    }

    /**
     * 对集合按照指定长度分段，每一个段为单独的集合，返回这个集合的列表
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param size       每个段的长度
     * @return 分段列表
     */
    public static <T> List<List<T>> split(Collection<T> collection, int size) {
        final List<List<T>> result = new ArrayList<>();
        if (Whether.empty(collection)) {
            return result;
        }

        ArrayList<T> subList = new ArrayList<>(size);
        for (T t : collection) {
            if (subList.size() >= size) {
                result.add(subList);
                subList = new ArrayList<>(size);
            }
            subList.add(t);
        }
        result.add(subList);
        return result;
    }

    /**
     * 编辑，此方法产生一个新集合<br>
     * 编辑过程通过传入的Editor实现来返回需要的元素内容，这个Editor实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，如果返回null表示这个元素对象抛弃
     * 2、修改元素对象，返回集合中为修改后的对象
     * </pre>
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param editor     编辑器接口，{@code null}返回原集合
     * @return 过滤后的集合
     */
    public static <T> Collection<T> edit(Collection<T> collection, Editor<T> editor) {
        if (null == collection || null == editor) {
            return collection;
        }

        final Collection<T> collection2 = create(collection.getClass());
        if (Whether.empty(collection)) {
            return collection2;
        }

        T modified;
        for (T t : collection) {
            modified = editor.edit(t);
            if (null != modified) {
                collection2.add(modified);
            }
        }
        return collection2;
    }

    /**
     * 过滤<br>
     * 过滤过程通过传入的Filter实现来过滤返回需要的元素内容，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Predicate#test(Object)}方法返回true的对象将被加入结果集合中
     * </pre>
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param filter     过滤器，{@code null}返回原集合
     * @return 过滤后的数组
     */
    public static <T> Collection<T> filterNew(Collection<T> collection, Predicate<T> filter) {
        if (null == collection || null == filter) {
            return collection;
        }
        return edit(collection, t -> filter.test(t) ? t : null);
    }

    /**
     * 去掉集合中的多个元素，此方法直接修改原集合
     *
     * @param <T>         集合类型
     * @param <E>         集合元素类型
     * @param collection  集合
     * @param elesRemoved 被去掉的元素数组
     * @return 原集合
     */
    @SuppressWarnings("unchecked")
    public static <T extends Collection<E>, E> T removeAny(T collection, E... elesRemoved) {
        collection.removeAll(New.set(false, elesRemoved));
        return collection;
    }

    /**
     * 去除指定元素，此方法直接修改原集合
     *
     * @param <T>        集合类型
     * @param <E>        集合元素类型
     * @param collection 集合
     * @param filter     过滤器
     * @return 处理后的集合
     */
    public static <T extends Collection<E>, E> T filter(T collection, final Predicate<E> filter) {
        return filter(collection, filter);
    }

    /**
     * 去除{@code null} 元素，此方法直接修改原集合
     *
     * @param <T>        集合类型
     * @param <E>        集合元素类型
     * @param collection 集合
     * @return 处理后的集合
     */
    public static <T extends Collection<E>, E> T removeNull(T collection) {
        return filter(collection, Objects::nonNull);
    }

    /**
     * 去除{@code null}或者"" 元素，此方法直接修改原集合
     *
     * @param <T>        集合类型
     * @param <E>        集合元素类型
     * @param collection 集合
     * @return 处理后的集合
     */
    public static <T extends Collection<E>, E extends CharSequence> T removeEmpty(T collection) {
        return filter(collection, Whether::noEmpty);
    }

    /**
     * 去除{@code null}或者""或者空白字符串 元素，此方法直接修改原集合
     *
     * @param <T>        集合类型
     * @param <E>        集合元素类型
     * @param collection 集合
     * @return 处理后的集合
     */
    public static <T extends Collection<E>, E extends CharSequence> T removeBlank(T collection) {
        return filter(collection, Whether::noBlank);
    }

    /**
     * 移除集合中的多个元素，并将结果存放到指定的集合 此方法直接修改原集合
     *
     * @param <T>              集合类型
     * @param <E>              集合元素类型
     * @param resultCollection 存放移除结果的集合
     * @param targetCollection 被操作移除元素的集合
     * @param predicate        用于是否移除判断的过滤器
     * @return 移除结果的集合
     */
    public static <T extends Collection<E>, E> T removeWithAddIf(
            T targetCollection, T resultCollection, Predicate<E> predicate) {
        Objects.requireNonNull(predicate);
        final Iterator<E> each = targetCollection.iterator();
        while (each.hasNext()) {
            E next = each.next();
            if (predicate.test(next)) {
                resultCollection.add(next);
                each.remove();
            }
        }
        return resultCollection;
    }

    /**
     * 移除集合中的多个元素，并将结果存放到生成的新集合中后返回<br>
     * 此方法直接修改原集合
     *
     * @param <T>              集合类型
     * @param <E>              集合元素类型
     * @param targetCollection 被操作移除元素的集合
     * @param predicate        用于是否移除判断的过滤器
     * @return 移除结果的集合
     */
    public static <T extends Collection<E>, E> List<E> removeWithAddIf(
            T targetCollection, Predicate<E> predicate) {
        final List<E> removed = new ArrayList<>();
        removeWithAddIf(targetCollection, removed, predicate);
        return removed;
    }

    /**
     * 通过Editor抽取集合元素中的某些值返回为新列表<br>
     * 例如提供的是一个Bean列表，通过Editor接口实现获取某个字段值，返回这个字段值组成的新列表
     *
     * @param collection 原集合
     * @param editor     编辑器
     * @return 抽取后的新列表
     */
    public static List<Object> extract(Iterable<?> collection, Editor<Object> editor) {
        return extract(collection, editor, false);
    }

    /**
     * 通过Editor抽取集合元素中的某些值返回为新列表<br>
     * 例如提供的是一个Bean列表，通过Editor接口实现获取某个字段值，返回这个字段值组成的新列表
     *
     * @param collection 原集合
     * @param editor     编辑器
     * @param ignoreNull 是否忽略空值
     * @return 抽取后的新列表
     * @see #map(Iterable, Function, boolean)
     */
    public static List<Object> extract(
            Iterable<?> collection, Editor<Object> editor, boolean ignoreNull) {
        return map(collection, editor::edit, ignoreNull);
    }

    /**
     * 通过func自定义一个规则，此规则将原集合中的元素转换成新的元素，生成新的列表返回<br>
     * 例如提供的是一个Bean列表，通过Function接口实现获取某个字段值，返回这个字段值组成的新列表
     *
     * @param <T>        集合元素类型
     * @param <R>        返回集合元素类型
     * @param collection 原集合
     * @param func       编辑函数
     * @param ignoreNull 是否忽略空值，这里的空值包括函数处理前和处理后的null值
     * @return 抽取后的新列表
     */
    public static <T, R> List<R> map(
            Iterable<T> collection, Function<? super T, ? extends R> func, boolean ignoreNull) {
        final List<R> fieldValueList = new ArrayList<>();
        if (null == collection) {
            return fieldValueList;
        }

        R value;
        for (T t : collection) {
            if (null == t && ignoreNull) {
                continue;
            }
            value = func.apply(t);
            if (null == value && ignoreNull) {
                continue;
            }
            fieldValueList.add(value);
        }
        return fieldValueList;
    }

    /**
     * 获取给定Bean列表中指定字段名对应字段值的列表<br>
     * 列表元素支持Bean与Map
     *
     * @param collection Bean集合或Map集合
     * @param fieldName  字段名或map的键
     * @return 字段值列表
     */
    public static List<Object> getFieldValues(Iterable<?> collection, final String fieldName) {
        return getFieldValues(collection, fieldName, false);
    }

    /**
     * 获取给定Bean列表中指定字段名对应字段值的列表<br>
     * 列表元素支持Bean与Map
     *
     * @param collection Bean集合或Map集合
     * @param fieldName  字段名或map的键
     * @param ignoreNull 是否忽略值为{@code null}的字段
     * @return 字段值列表
     */
    public static List<Object> getFieldValues(
            Iterable<?> collection, final String fieldName, boolean ignoreNull) {
        return map(
                collection,
                bean -> {
                    if (bean instanceof Map) {
                        return ((Map<?, ?>) bean).get(fieldName);
                    } else {
                        return ToolBytecode.getFieldValueAsSafeSilent(bean, fieldName);
                    }
                },
                ignoreNull);
    }

    /**
     * 获取给定Bean列表中指定字段名对应字段值的列表<br>
     * 列表元素支持Bean与Map
     *
     * @param <T>         元素类型
     * @param collection  Bean集合或Map集合
     * @param fieldName   字段名或map的键
     * @param elementType 元素类型类
     * @return 字段值列表
     */
    public static <T> List<T> getFieldValues(
            Iterable<?> collection, final String fieldName, final Class<T> elementType) {
        List<Object> fieldValues = getFieldValues(collection, fieldName);
        return Convert.toList(elementType, fieldValues);
    }


    /**
     * 查找第一个匹配元素对象
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param filter     过滤器，满足过滤条件的第一个元素将被返回
     * @return 满足过滤条件的第一个元素
     */
    public static <T> T findOne(Iterable<T> collection, Predicate<T> filter) {
        if (null != collection) {
            for (T t : collection) {
                if (filter.test(t)) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * 查找第一个匹配元素对象<br>
     * 如果集合元素是Map，则比对键和值是否相同，相同则返回<br>
     * 如果为普通Bean，则通过反射比对元素字段名对应的字段值是否相同，相同则返回<br>
     * 如果给定字段值参数是{@code null} 且元素对象中的字段值也为{@code null}则认为相同
     *
     * @param <T>        集合元素类型
     * @param collection 集合，集合元素可以是Bean或者Map
     * @param fieldName  集合元素对象的字段名或map的键
     * @param fieldValue 集合元素对象的字段值或map的值
     * @return 满足条件的第一个元素
     */
    public static <T> T findOneByField(
            Iterable<T> collection, final String fieldName, final Object fieldValue) {
        return findOne(
                collection,
                t -> {
                    if (t instanceof Map) {
                        final Map<?, ?> map = (Map<?, ?>) t;
                        final Object value = map.get(fieldName);
                        return ToolObject.equal(value, fieldValue);
                    }

                    // 普通Bean
                    final Object value = ToolBytecode.getFieldValueAsSafeSilent(t, fieldName);
                    return ToolObject.equal(value, fieldValue);
                });
    }

    /**
     * 集合中匹配规则的数量
     *
     * @param <T>       集合元素类型
     * @param iterable  {@link Iterable}
     * @param predicate 匹配器，为空则全部匹配
     * @return 匹配数量
     */
    public static <T> int count(Iterable<T> iterable, Predicate<T> predicate) {
        int count = 0;
        if (null != iterable) {
            for (T t : iterable) {
                if (null == predicate || predicate.test(t)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 获取匹配规则定义中匹配到元素的第一个位置<br>
     * 此方法对于某些无序集合的位置信息，以转换为数组后的位置为准。
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param predicate  匹配器，为空则全部匹配
     * @return 第一个位置
     */
    public static <T> int indexOf(Collection<T> collection, Predicate<T> predicate) {
        if (Whether.noEmpty(collection)) {
            int index = 0;
            for (T t : collection) {
                if (null == predicate || predicate.test(t)) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    /**
     * 获取匹配规则定义中匹配到元素的最后位置<br>
     * 此方法对于某些无序集合的位置信息，以转换为数组后的位置为准。
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param predicate  匹配器，为空则全部匹配
     * @return 最后一个位置
     */
    public static <T> int lastIndexOf(Collection<T> collection, Predicate<T> predicate) {
        if (collection instanceof List) {
            // List的查找最后一个有优化算法
            return lastIndexOf((List<T>) collection, predicate);
        }
        int matchIndex = -1;
        if (Whether.noEmpty(collection)) {
            int index = collection.size();
            for (T t : collection) {
                if (null == predicate || predicate.test(t)) {
                    matchIndex = index;
                }
                index--;
            }
        }
        return matchIndex;
    }

    /**
     * 获取匹配规则定义中匹配到元素的所有位置<br>
     * 此方法对于某些无序集合的位置信息，以转换为数组后的位置为准。
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param predicate  匹配器，为空则全部匹配
     * @return 位置数组
     */
    public static <T> int[] indexOfAll(Collection<T> collection, Predicate<T> predicate) {
        final List<Integer> indexList = new ArrayList<>();
        if (null != collection) {
            int index = 0;
            for (T t : collection) {
                if (null == predicate || predicate.test(t)) {
                    indexList.add(index);
                }
                index++;
            }
        }
        return Convert.convert(int[].class, indexList);
    }

    // ---------------------------------------------------------------------- zip

    /**
     * 映射键值（参考Python的zip()函数）<br>
     * 例如：<br>
     * keys = a,b,c,d<br>
     * values = 1,2,3,4<br>
     * delimiter = , 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param keys      键列表
     * @param values    值列表
     * @param delimiter 分隔符
     * @param isOrder   是否有序
     * @return Map
     */
    public static Map<String, String> zip(
            String keys, String values, String delimiter, boolean isOrder) {
        return ToolArray.zip(
                ToolString.split(keys, delimiter),
                ToolString.split(values, delimiter),
                isOrder);
    }

    /**
     * 映射键值（参考Python的zip()函数），返回Map无序<br>
     * 例如：<br>
     * keys = a,b,c,d<br>
     * values = 1,2,3,4<br>
     * delimiter = , 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param keys      键列表
     * @param values    值列表
     * @param delimiter 分隔符
     * @return Map
     */
    public static Map<String, String> zip(String keys, String values, String delimiter) {
        return zip(keys, values, delimiter, false);
    }

    /**
     * 映射键值（参考Python的zip()函数）<br>
     * 例如：<br>
     * keys = [a,b,c,d]<br>
     * values = [1,2,3,4]<br>
     * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param <K>    键类型
     * @param <V>    值类型
     * @param keys   键列表
     * @param values 值列表
     * @return Map
     */
    public static <K, V> Map<K, V> zip(Collection<K> keys, Collection<V> values) {
        if (Whether.empty(keys) || Whether.empty(values)) {
            return MapUtil.empty();
        }

        int entryCount = Math.min(keys.size(), values.size());
        final Map<K, V> map = New.map(entryCount);

        final Iterator<K> keyIterator = keys.iterator();
        final Iterator<V> valueIterator = values.iterator();
        while (entryCount > 0) {
            map.put(keyIterator.next(), valueIterator.next());
            entryCount--;
        }

        return map;
    }

    /**
     * 将集合转换为排序后的TreeSet
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param comparator 比较器
     * @return treeSet
     */
    public static <T> TreeSet<T> toTreeSet(Collection<T> collection, Comparator<T> comparator) {
        final TreeSet<T> treeSet = new TreeSet<>(comparator);
        treeSet.addAll(collection);
        return treeSet;
    }

    /**
     * 行转列，合并相同的键，值合并为列表<br>
     * 将Map列表中相同key的值组成列表做为Map的value<br>
     * 是{@link #toMapList(Map)}的逆方法<br>
     * 比如传入数据：
     *
     * <pre>
     * [
     *  {a: 1, b: 1, c: 1}
     *  {a: 2, b: 2}
     *  {a: 3, b: 3}
     *  {a: 4}
     * ]
     * </pre>
     *
     * <p>结果是：
     *
     * <pre>
     * {
     *   a: [1,2,3,4]
     *   b: [1,2,3,]
     *   c: [1]
     * }
     * </pre>
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param mapList Map列表
     * @return Map
     * @see MapUtil#toListMap(Iterable)
     */
    public static <K, V> Map<K, List<V>> toListMap(Iterable<? extends Map<K, V>> mapList) {
        return MapUtil.toListMap(mapList);
    }

    /**
     * 列转行。将Map中值列表分别按照其位置与key组成新的map。<br>
     * 是{@link #toListMap(Iterable)}的逆方法<br>
     * 比如传入数据：
     *
     * <pre>
     * {
     *   a: [1,2,3,4]
     *   b: [1,2,3,]
     *   c: [1]
     * }
     * </pre>
     *
     * <p>结果是：
     *
     * <pre>
     * [
     *  {a: 1, b: 1, c: 1}
     *  {a: 2, b: 2}
     *  {a: 3, b: 3}
     *  {a: 4}
     * ]
     * </pre>
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param listMap 列表Map
     * @return Map列表
     * @see MapUtil#toMapList(Map)
     */
    public static <K, V> List<Map<K, V>> toMapList(Map<K, ? extends Iterable<V>> listMap) {
        return MapUtil.toMapList(listMap);
    }

    /**
     * 将另一个列表中的元素加入到列表中，如果列表中已经存在此元素则忽略之
     *
     * @param <T>       集合元素类型
     * @param list      列表
     * @param otherList 其它列表
     * @return 此列表
     */
    public static <T> List<T> addAllIfNotContains(List<T> list, List<T> otherList) {
        for (T t : otherList) {
            if (false == list.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * 获取集合中指定多个下标的元素值，下标可以为负数，例如-1表示最后一个元素
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param indexes    下标，支持负数
     * @return 元素值列表
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getAny(Collection<T> collection, int... indexes) {
        final int size = collection.size();
        final ArrayList<T> result = new ArrayList<>();
        if (collection instanceof List) {
            final List<T> list = ((List<T>) collection);
            for (int index : indexes) {
                if (index < 0) {
                    index += size;
                }
                result.add(list.get(index));
            }
        } else {
            final Object[] array = collection.toArray();
            for (int index : indexes) {
                if (index < 0) {
                    index += size;
                }
                result.add((T) array[index]);
            }
        }
        return result;
    }

    /**
     * 获取集合的最后一个元素
     *
     * @param <T>        集合元素类型
     * @param collection {@link Collection}
     * @return 最后一个元素
     */
    public static <T> T getLast(Collection<T> collection) {
        return Safe.atIdx(collection, -1, null);
    }

    /**
     * 从Map中获取指定键列表对应的值列表<br>
     * 如果key在map中不存在或key对应值为null，则返回值列表对应位置的值也为null
     *
     * @param <K>  键类型
     * @param <V>  值类型
     * @param map  {@link Map}
     * @param keys 键列表
     * @return 值列表
     */
    @SuppressWarnings("unchecked")
    public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, K... keys) {
        return MapUtil.valuesOfKeys(map, AnyIter.ofArray(keys));
    }

    /**
     * 从Map中获取指定键列表对应的值列表<br>
     * 如果key在map中不存在或key对应值为null，则返回值列表对应位置的值也为null
     *
     * @param <K>  键类型
     * @param <V>  值类型
     * @param map  {@link Map}
     * @param keys 键列表
     * @return 值列表
     */
    public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, Iterable<K> keys) {
        return valuesOfKeys(map, keys.iterator());
    }

    /**
     * 从Map中获取指定键列表对应的值列表<br>
     * 如果key在map中不存在或key对应值为null，则返回值列表对应位置的值也为null
     *
     * @param <K>  键类型
     * @param <V>  值类型
     * @param map  {@link Map}
     * @param keys 键列表
     * @return 值列表
     */
    public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, Iterator<K> keys) {
        return MapUtil.valuesOfKeys(map, keys);
    }

    // ------------------------------------------------------------------------------------------------- sort

    /**
     * 排序集合，排序不会修改原集合
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param comparator 比较器
     * @return treeSet
     */
    public static <T> List<T> sort(Collection<T> collection, Comparator<? super T> comparator) {
        List<T> list = new ArrayList<>(collection);
        list.sort(comparator);
        return list;
    }

    /**
     * 根据Bean的属性排序
     *
     * @param <T>        元素类型
     * @param collection 集合，会被转换为List
     * @param property   属性名
     * @return 排序后的List
     */
    public static <T> List<T> sortByProperty(Collection<T> collection, String property) {
        return sort(collection, new PropertyComparator<>(property));
    }

    /**
     * 根据汉字的拼音顺序排序
     *
     * @param collection 集合，会被转换为List
     * @return 排序后的List
     */
    public static List<String> sortByPinyin(Collection<String> collection) {
        return sort(collection, new PinyinComparator());
    }

    /**
     * 排序Map
     *
     * @param <K>        键类型
     * @param <V>        值类型
     * @param map        Map
     * @param comparator Entry比较器
     * @return {@link TreeMap}
     */
    public static <K, V> TreeMap<K, V> sort(Map<K, V> map, Comparator<? super K> comparator) {
        final TreeMap<K, V> result = new TreeMap<>(comparator);
        result.putAll(map);
        return result;
    }

    /**
     * 通过Entry排序，可以按照键排序，也可以按照值排序，亦或者两者综合排序
     *
     * @param <K>             键类型
     * @param <V>             值类型
     * @param entryCollection Entry集合
     * @param comparator      {@link Comparator}
     * @return {@link LinkedList}
     */
    public static <K, V> LinkedHashMap<K, V> sortToMap(
            Collection<Entry<K, V>> entryCollection, Comparator<Entry<K, V>> comparator) {
        List<Entry<K, V>> list = new LinkedList<>(entryCollection);
        list.sort(comparator);

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 通过Entry排序，可以按照键排序，也可以按照值排序，亦或者两者综合排序
     *
     * @param <K>        键类型
     * @param <V>        值类型
     * @param map        被排序的Map
     * @param comparator {@link Comparator}
     * @return {@link LinkedList}
     */
    public static <K, V> LinkedHashMap<K, V> sortByEntry(
            Map<K, V> map, Comparator<Entry<K, V>> comparator) {
        return sortToMap(map.entrySet(), comparator);
    }

    /**
     * 将Set排序（根据Entry的值）
     *
     * @param <K>        键类型
     * @param <V>        值类型
     * @param collection 被排序的{@link Collection}
     * @return 排序后的Set
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <K, V> List<Entry<K, V>> sortEntryToList(Collection<Entry<K, V>> collection) {
        List<Entry<K, V>> list = new LinkedList<>(collection);
        list.sort(
                (o1, o2) -> {
                    V v1 = o1.getValue();
                    V v2 = o2.getValue();

                    if (v1 instanceof Comparable) {
                        return ((Comparable) v1).compareTo(v2);
                    } else {
                        return v1.toString().compareTo(v2.toString());
                    }
                });
        return list;
    }

    // ------------------------------------------------------------------------------------------------- forEach

    /**
     * 循环遍历Map，使用{@link LoopEntryConsumer} 接受遍历的每条数据，并针对每条数据做处理<br>
     * 和JDK8中的map.forEach不同的是，此方法支持index
     *
     * @param <K>        Key类型
     * @param <V>        Value类型
     * @param map        {@link Map}
     * @param kvConsumer {@link LoopEntryConsumer} 遍历的每条数据处理器
     */
    public static <K, V> void forEach(Map<K, V> map, LoopEntryConsumer<K, V> kvConsumer) {
        if (map == null) {
            return;
        }
        int index = 0;
        for (Entry<K, V> entry : map.entrySet()) {
            kvConsumer.accept(entry.getKey(), entry.getValue(), index);
            index++;
        }
    }

    /**
     * 分组，按照{@link Hash32}接口定义的hash算法，集合中的元素放入hash值对应的子列表中
     *
     * @param <T>        元素类型
     * @param collection 被分组的集合
     * @param hash       Hash值算法，决定元素放在第几个分组的规则
     * @return 分组后的集合
     */
    public static <T> List<List<T>> group(Collection<T> collection, Hash32<T> hash) {
        final List<List<T>> result = new ArrayList<>();
        if (Whether.empty(collection)) {
            return result;
        }
        if (null == hash) {
            // 默认hash算法，按照元素的hashCode分组
            hash = t -> (null == t) ? 0 : t.hashCode();
        }

        int index;
        List<T> subList;
        for (T t : collection) {
            index = hash.hash32(t);
            if (result.size() - 1 < index) {
                while (result.size() - 1 < index) {
                    result.add(null);
                }
                result.set(index, New.list(false, t));
            } else {
                subList = result.get(index);
                if (null == subList) {
                    result.set(index, New.list(false, t));
                } else {
                    subList.add(t);
                }
            }
        }
        return result;
    }

    /**
     * 根据元素的指定字段名分组，非Bean都放在第一个分组中
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param fieldName  元素Bean中的字段名，非Bean都放在第一个分组中
     * @return 分组列表
     */
    public static <T> List<List<T>> groupByField(Collection<T> collection, final String fieldName) {
        return group(
                collection,
                new Hash32<T>() {
                    private final List<Object> fieldNameList = new ArrayList<>();

                    @Override
                    public int hash32(T t) {
                        if (null == t || false == BeanUtil.isBean(t.getClass())) {
                            // 非Bean放在同一子分组中
                            return 0;
                        }
                        final Object value = ToolBytecode.getFieldValueAsSafeSilent(t, fieldName);
                        int hash = fieldNameList.indexOf(value);
                        if (hash < 0) {
                            fieldNameList.add(value);
                            return fieldNameList.size() - 1;
                        } else {
                            return hash;
                        }
                    }
                });
    }

    /**
     * 获取指定Map列表中所有的Key
     *
     * @param <K>           键类型
     * @param mapCollection Map列表
     * @return key集合
     */
    public static <K> Set<K> keySet(Collection<Map<K, ?>> mapCollection) {
        if (Whether.empty(mapCollection)) {
            return new HashSet<>();
        }
        final HashSet<K> set = new HashSet<>(mapCollection.size() * 16);
        for (Map<K, ?> map : mapCollection) {
            set.addAll(map.keySet());
        }

        return set;
    }

    /**
     * 获取指定Map列表中所有的Value
     *
     * @param <V>           值类型
     * @param mapCollection Map列表
     * @return Value集合
     */
    public static <V> List<V> values(Collection<Map<?, V>> mapCollection) {
        final List<V> values = new ArrayList<>();
        for (Map<?, V> map : mapCollection) {
            values.addAll(map.values());
        }

        return values;
    }

    /**
     * 取最大值
     *
     * @param <T>  元素类型
     * @param coll 集合
     * @return 最大值
     * @see Collections#max(Collection)
     */
    public static <T extends Comparable<? super T>> T max(Collection<T> coll) {
        return Collections.max(coll);
    }

    /**
     * 取最小值
     *
     * @param <T>  元素类型
     * @param coll 集合
     * @return 最小值
     * @see Collections#min(Collection)
     */
    public static <T extends Comparable<? super T>> T min(Collection<T> coll) {
        return Collections.min(coll);
    }

    /**
     * 填充List，以达到最小长度
     *
     * @param <T>    集合元素类型
     * @param list   列表
     * @param minLen 最小长度
     * @param padObj 填充的对象
     */
    public static <T> void padLeft(List<T> list, int minLen, T padObj) {
        Objects.requireNonNull(list);
        if (Whether.empty(list)) {
            padRight(list, minLen, padObj);
            return;
        }
        for (int i = list.size(); i < minLen; i++) {
            list.add(0, padObj);
        }
    }

    /**
     * 填充List，以达到最小长度
     *
     * @param <T>    集合元素类型
     * @param list   列表
     * @param minLen 最小长度
     * @param padObj 填充的对象
     */
    public static <T> void padRight(Collection<T> list, int minLen, T padObj) {
        Objects.requireNonNull(list);
        for (int i = list.size(); i < minLen; i++) {
            list.add(padObj);
        }
    }

    /**
     * 使用给定的转换函数，转换源集合为新类型的集合
     *
     * @param <F>        源元素类型
     * @param <T>        目标元素类型
     * @param collection 集合
     * @param function   转换函数
     * @return 新类型的集合
     */
    public static <F, T> Collection<T> trans(
            Collection<F> collection, Function<? super F, ? extends T> function) {
        return new TransCollection<>(collection, function);
    }

    /**
     * 使用给定的map将集合中的原素进行属性或者值的重新设定
     *
     * @param <E>         元素类型
     * @param <K>         替换的键
     * @param <V>         替换的值
     * @param iterable    集合
     * @param map         映射集
     * @param keyGenerate 映射键生成函数
     * @param biConsumer  封装映射到的值函数
     * @author potatoxf
     */
    public static <E, K, V> void setValueByMap(
            Iterable<E> iterable,
            Map<K, V> map,
            Function<E, K> keyGenerate,
            BiConsumer<E, V> biConsumer) {
        iterable.forEach(
                x ->
                        Optional.ofNullable(map.get(keyGenerate.apply(x)))
                                .ifPresent(y -> biConsumer.accept(x, y)));
    }

    // ----------------------------------------------------------------------------------------------
    // Interface start

    /**
     * 获取Collection或者iterator的大小，此方法可以处理的对象类型如下：
     *
     * <ul>
     *   <li>Collection - the collection size
     *   <li>Map - the map size
     *   <li>Array - the array size
     *   <li>Iterator - the number of elements remaining in the iterator
     *   <li>Enumeration - the number of elements remaining in the enumeration
     * </ul>
     *
     * @param object 可以为空的对象
     * @return 如果object为空则返回0
     * @throws IllegalArgumentException 参数object不是Collection或者iterator
     */
    public static int size(final Object object) {
        if (object == null) {
            return 0;
        }

        int total = 0;
        if (object instanceof Map<?, ?>) {
            total = ((Map<?, ?>) object).size();
        } else if (object instanceof Collection<?>) {
            total = ((Collection<?>) object).size();
        } else if (object instanceof Iterable<?>) {
            total = size((Iterable<?>) object);
        } else if (object instanceof Iterator<?>) {
            total = size((Iterator<?>) object);
        } else if (object instanceof Enumeration<?>) {
            final Enumeration<?> it = (Enumeration<?>) object;
            while (it.hasMoreElements()) {
                total++;
                it.nextElement();
            }
        } else if (Whether.arrayObj(object)) {
            total = ToolArray.length(object);
        } else {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
        return total;
    }

    /**
     * 判断两个{@link Collection} 是否元素和顺序相同，返回{@code true}的条件是：
     *
     * <ul>
     *   <li>两个{@link Collection}必须长度相同
     *   <li>两个{@link Collection}元素相同index的对象必须equals，满足{@link Objects#equals(Object, Object)}
     * </ul>
     * <p>
     * 此方法来自Apache-Commons-Collections4。
     *
     * @param list1 列表1
     * @param list2 列表2
     * @return 是否相同
     */
    public static boolean isEqualList(final Collection<?> list1, final Collection<?> list2) {
        if (list1 == list2) {
            return true;
        }
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }

        return isEqualList(list1, list2);
    }


    //-----------------------------


    /**
     * 将字符串串构建器内容添加至容器里并清除字符串串构建器里的内容
     *
     * @param container     字符串容器
     * @param stringBuilder 字符串串构建器
     */
    public static void addStringAndClear(Collection<String> container, StringBuilder stringBuilder) {
        String string = Safe.clear(stringBuilder);
        if (string.length() != 0) {
            container.add(string);
        }
    }

    /**
     * 将字符串串构建器内容添加至容器里并清除字符串串构建器里的内容
     *
     * @param container    字符串容器
     * @param stringBuffer 字符串串构建器
     */
    public static void addStringAndClear(Collection<String> container, StringBuffer stringBuffer) {
        String string = Safe.clear(stringBuffer);
        if (string.length() != 0) {
            container.add(string);
        }
    }


    /**
     * 将Map转换成
     *
     * @param input {@code Map<?, ? extends T>}
     * @return {@code CaseInsensitiveMap<String, Object>}
     */
    public static <T> CaseInsensitiveMap<String, T> toStringObjectCaseInsensitiveMap(
            Map<?, ? extends T> input) {
        CaseInsensitiveMap<String, T> map = new CaseInsensitiveMap<String, T>();
        for (Map.Entry<?, ? extends T> entry : input.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof String) {
                map.put(key.toString(), entry.getValue());
            }
        }
        return map;
    }

    /**
     * 根据集合返回一个元素计数的 {@link Map}<br>
     * 所谓元素计数就是假如这个集合中某个元素出现了n次，那将这个元素做为key，n做为value<br>
     * 例如：[a,b,c,c,c] 得到：<br>
     * a: 1<br>
     * b: 1<br>
     * c: 3<br>
     *
     * @param <T>  集合元素类型
     * @param iter {@link Iterator}，如果为null返回一个空的Map
     * @return {@link Map}
     */
    public static <T> Map<T, Integer> countMap(Iterator<T> iter) {
        final HashMap<T, Integer> countMap = new HashMap<>();
        if (null != iter) {
            T t;
            while (iter.hasNext()) {
                t = iter.next();
                countMap.put(t, countMap.getOrDefault(t, 0) + 1);
            }
        }
        return countMap;
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static <T> String join(Iterator<T> iterator, CharSequence conjunction) {
        return StrJoiner.of(conjunction).append(iterator).toString();
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @param prefix      每个元素添加的前缀，null表示不添加
     * @param suffix      每个元素添加的后缀，null表示不添加
     * @return 连接后的字符串
     */
    public static <T> String join(
            Iterator<T> iterator, CharSequence conjunction, String prefix, String suffix) {
        return StrJoiner.of(conjunction, prefix, suffix)
                // 每个元素都添加前后缀
                .setWrapElement(true)
                .append(iterator)
                .toString();
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @param func        集合元素转换器，将元素转换为字符串
     * @return 连接后的字符串
     */
    public static <T> String join(
            Iterator<T> iterator, CharSequence conjunction, Function<T, ? extends CharSequence> func) {
        if (null == iterator) {
            return null;
        }

        return StrJoiner.of(conjunction).append(iterator, func).toString();
    }

    /**
     * 获得{@link Iterator}对象的元素类型（通过第一个非空元素判断）<br>
     * 注意，此方法至少会调用多次next方法
     *
     * @param iterator {@link Iterator}，为 {@code null}返回{@code null}
     * @return 元素类型，当列表为空或元素全部为{@code null}时，返回{@code null}
     */
    public static Class<?> getElementType(Iterator<?> iterator) {
        Optional<?> first = AnyIter.ofIterator(iterator).getFirst();
        return first.<Class<?>>map(Object::getClass).orElse(null);
    }

    /**
     * 编辑，此方法产生一个新{@link ArrayList}<br>
     * 编辑过程通过传入的Editor实现来返回需要的元素内容，这个Editor实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，如果返回null表示这个元素对象抛弃
     * 2、修改元素对象，返回集合中为修改后的对象
     * </pre>
     *
     * @param <T>    集合元素类型
     * @param iter   集合
     * @param editor 编辑器接口, {@code null}表示不编辑
     * @return 过滤后的集合
     */
    public static <T> List<T> edit(Iterable<T> iter, Editor<T> editor) {
        final List<T> result = new ArrayList<>();
        if (null == iter) {
            return result;
        }

        T modified;
        for (T t : iter) {
            modified = (null == editor) ? t : editor.edit(t);
            if (null != modified) {
                result.add(modified);
            }
        }
        return result;
    }

    /**
     * 过滤集合，此方法在原集合上直接修改<br>
     * 通过实现Filter接口，完成元素的过滤，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Predicate#test(Object)}方法返回false的对象将被使用{@link Iterator#remove()}方法移除
     * </pre>
     *
     * @param <T>    集合类型
     * @param <E>    集合元素类型
     * @param iter   集合
     * @param filter 过滤器接口
     * @return 编辑后的集合
     */
    public static <T extends Iterable<E>, E> T filter(T iter, Predicate<E> filter) {
        if (null == iter) {
            return null;
        }

        filter(iter.iterator(), filter);

        return iter;
    }

    /**
     * 过滤集合，此方法在原集合上直接修改<br>
     * 通过实现Filter接口，完成元素的过滤，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Predicate#test(Object)}方法返回false的对象将被使用{@link Iterator#remove()}方法移除
     * </pre>
     *
     * @param <E>    集合元素类型
     * @param iter   集合
     * @param filter 过滤器接口，删除{@link Predicate#test(Object)}为{@code false}的元素
     * @return 编辑后的集合
     */
    public static <E> Iterator<E> filter(Iterator<E> iter, Predicate<E> filter) {
        if (null == iter || null == filter) {
            return iter;
        }

        while (iter.hasNext()) {
            if (false == filter.test(iter.next())) {
                iter.remove();
            }
        }
        return iter;
    }

    /**
     * 获取一个新的，用于过滤指定元素
     *
     * @param iterator 被包装的 {@link Iterator}
     * @param filter   过滤断言，当{@link Predicate#test(Object)}为{@code true}时保留元素，{@code false}抛弃元素
     * @param <E>      元素类型
     */
    public static <E> AnyIter<E> filtered(
            final Iterator<? extends E> iterator, final Predicate<? super E> filter) {
        return AnyIter.ofIterator(true, filter, iterator);
    }

    /**
     * 按照给定函数，转换{@link Iterator}为另一种类型的{@link Iterator}
     *
     * @param <F>      源元素类型
     * @param <T>      目标元素类型
     * @param iterator 源{@link Iterator}
     * @param function 转换函数
     * @return 转换后的{@link Iterator}
     */
    public static <F, T> Iterator<T> trans(
            Iterator<F> iterator, Function<? super F, ? extends T> function) {
        return new TransIter<>(iterator, function);
    }

    /**
     * 返回 Iterable 对象的元素数量
     *
     * @param iterable Iterable对象
     * @return Iterable对象的元素数量
     */
    public static int size(Iterable<?> iterable) {
        if (null == iterable) {
            return 0;
        }

        if (iterable instanceof Collection<?>) {
            return ((Collection<?>) iterable).size();
        } else {
            return size(iterable.iterator());
        }
    }

    /**
     * 返回 Iterator 对象的元素数量
     *
     * @param iterator Iterator对象
     * @return Iterator对象的元素数量
     */
    public static int size(Iterator<?> iterator) {
        int size = 0;
        if (iterator != null) {
            while (iterator.hasNext()) {
                iterator.next();
                size++;
            }
        }
        return size;
    }

    /**
     * 判断两个{@link Iterable} 是否元素和顺序相同，返回{@code true}的条件是：
     *
     * <ul>
     *   <li>两个{@link Iterable}必须长度相同
     *   <li>两个{@link Iterable}元素相同index的对象必须equals，满足{@link Objects#equals(Object, Object)}
     * </ul>
     * <p>
     * 此方法来自Apache-Commons-Collections4。
     *
     * @param list1 列表1
     * @param list2 列表2
     * @return 是否相同
     */
    public static boolean isEqualList(Iterable<?> list1, Iterable<?> list2) {
        if (list1 == list2) {
            return true;
        }

        final Iterator<?> it1 = list1.iterator();
        final Iterator<?> it2 = list2.iterator();
        Object obj1;
        Object obj2;
        while (it1.hasNext() && it2.hasNext()) {
            obj1 = it1.next();
            obj2 = it2.next();

            if (false == Objects.equals(obj1, obj2)) {
                return false;
            }
        }

        // 当两个Iterable长度不一致时返回false
        return false == (it1.hasNext() || it2.hasNext());
    }

    /**
     * 遍历{@link Iterator}<br>
     * 当consumer为{@code null}表示不处理，但是依旧遍历{@link Iterator}
     *
     * @param iterator {@link Iterator}
     * @param consumer 节点消费，{@code null}表示不处理
     * @param <E>      元素类型
     */
    public static <E> void forEach(final Iterator<E> iterator, final Consumer<? super E> consumer) {
        if (iterator != null) {
            while (iterator.hasNext()) {
                final E element = iterator.next();
                if (null != consumer) {
                    consumer.accept(element);
                }
            }
        }
    }

    /**
     * 拼接 {@link Iterator}为字符串
     *
     * @param iterator {@link Iterator}
     * @param <E>      元素类型
     * @return 字符串
     */
    public static <E> String toStr(final Iterator<E> iterator) {
        return toStr(iterator, ToolObject::toString);
    }

    /**
     * 拼接 {@link Iterator}为字符串
     *
     * @param iterator  {@link Iterator}
     * @param transFunc 元素转字符串函数
     * @param <E>       元素类型
     * @return 字符串
     */
    public static <E> String toStr(
            final Iterator<E> iterator, final Function<? super E, String> transFunc) {
        return toStr(iterator, transFunc, ", ", "[", "]");
    }

    /**
     * 拼接 {@link Iterator}为字符串
     *
     * @param iterator  {@link Iterator}
     * @param transFunc 元素转字符串函数
     * @param delimiter 分隔符
     * @param prefix    前缀
     * @param suffix    后缀
     * @param <E>       元素类型
     * @return 字符串
     */
    public static <E> String toStr(
            final Iterator<E> iterator,
            final Function<? super E, String> transFunc,
            final String delimiter,
            final String prefix,
            final String suffix) {
        final StrJoiner strJoiner = StrJoiner.of(delimiter, prefix, suffix);
        strJoiner.append(iterator, transFunc);
        return strJoiner.toString();
    }

    /**
     * 针对List排序，排序会修改原List
     *
     * @param <T>  元素类型
     * @param list 被排序的List
     * @param c    {@link Comparator}
     * @return 原list
     * @see Collections#sort(List, Comparator)
     */
    public static <T> List<T> sort(List<T> list, Comparator<? super T> c) {
        if (Whether.empty(list)) {
            return list;
        }
        list.sort(c);
        return list;
    }

    /**
     * 根据Bean的属性排序
     *
     * @param <T>      元素类型
     * @param list     List
     * @param property 属性名
     * @return 排序后的List
     */
    public static <T> List<T> sortByProperty(List<T> list, String property) {
        return sort(list, new PropertyComparator<>(property));
    }

    /**
     * 根据汉字的拼音顺序排序
     *
     * @param list List
     * @return 排序后的List
     */
    public static List<String> sortByPinyin(List<String> list) {
        return sort(list, new PinyinComparator());
    }

    /**
     * 反序给定List，会在原List基础上直接修改
     *
     * @param <T>  元素类型
     * @param list 被反转的List
     * @return 反转后的List
     */
    public static <T> List<T> reverse(List<T> list) {
        Collections.reverse(list);
        return list;
    }

    /**
     * 反序给定List，会创建一个新的List，原List数据不变
     *
     * @param <T>  元素类型
     * @param list 被反转的List
     * @return 反转后的List
     */
    public static <T> List<T> reverseNew(List<T> list) {
        List<T> list2 = ToolObject.clone(list);
        if (null == list2) {
            // 不支持clone
            list2 = new ArrayList<>(list);
        }
        return reverse(list2);
    }

    /**
     * 设置或增加元素。当index小于List的长度时，替换指定位置的值，否则在尾部追加
     *
     * @param <T>     元素类型
     * @param list    List列表
     * @param index   位置
     * @param element 新元素
     * @return 原List
     */
    public static <T> List<T> setOrAppend(List<T> list, int index, T element) {
        Assert.notNull(list, "List must be not null !");
        if (index < list.size()) {
            list.set(index, element);
        } else {
            list.add(element);
        }
        return list;
    }

    /**
     * 在指定位置设置元素。当index小于List的长度时，替换指定位置的值，否则追加{@code null}直到到达index后，设置值
     *
     * @param <T>     元素类型
     * @param list    List列表
     * @param index   位置
     * @param element 新元素
     * @return 原List
     */
    public static <T> List<T> setOrPadding(List<T> list, int index, T element) {
        return setOrPadding(list, index, element, null);
    }

    /**
     * 在指定位置设置元素。当index小于List的长度时，替换指定位置的值，否则追加{@code paddingElement}直到到达index后，设置值
     *
     * @param <T>            元素类型
     * @param list           List列表
     * @param index          位置
     * @param element        新元素
     * @param paddingElement 填充的值
     * @return 原List
     */
    public static <T> List<T> setOrPadding(List<T> list, int index, T element, T paddingElement) {
        Assert.notNull(list, "List must be not null !");
        final int size = list.size();
        if (index < size) {
            list.set(index, element);
        } else {
            for (int i = size; i < index; i++) {
                list.add(paddingElement);
            }
            list.add(element);
        }
        return list;
    }

    /**
     * 截取集合的部分
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @return 截取后的数组，当开始位置超过最大时，返回空的List
     */
    public static <T> List<T> sub(List<T> list, int start, int end) {
        return sub(list, start, end, 1);
    }

    /**
     * 截取集合的部分<br>
     * 此方法与{@link List#subList(int, int)} 不同在于子列表是新的副本，操作子列表不会影响原列表。
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @param step  步进
     * @return 截取后的数组，当开始位置超过最大时，返回空的List
     */
    public static <T> List<T> sub(List<T> list, int start, int end, int step) {
        if (list == null) {
            return null;
        }

        if (Whether.empty(list)) {
            return new ArrayList<>(0);
        }

        final int size = list.size();
        if (start < 0) {
            start += size;
        }
        if (end < 0) {
            end += size;
        }
        if (start == size) {
            return new ArrayList<>(0);
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > size) {
            if (start >= size) {
                return new ArrayList<>(0);
            }
            end = size;
        }

        if (step < 1) {
            step = 1;
        }

        final List<T> result = new ArrayList<>();
        for (int i = start; i < end; i += step) {
            result.add(list.get(i));
        }
        return result;
    }

    /**
     * 获取匹配规则定义中匹配到元素的最后位置<br>
     * 此方法对于某些无序集合的位置信息，以转换为数组后的位置为准。
     *
     * @param <T>       元素类型
     * @param list      List集合
     * @param predicate 匹配器，为空则全部匹配
     * @return 最后一个位置
     */
    public static <T> int lastIndexOf(List<T> list, Predicate<T> predicate) {
        if (null != list) {
            final int size = list.size();
            if (size > 0) {
                for (int i = size - 1; i >= 0; i--) {
                    if (null == predicate || predicate.test(list.get(i))) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 通过传入分区长度，将指定列表分区为不同的块，每块区域的长度相同（最后一块可能小于长度）<br>
     * 分区是在原List的基础上进行的，返回的分区是不可变的抽象列表，原列表元素变更，分区中元素也会变更。
     *
     * <p>需要特别注意的是，此方法调用{@link List#subList(int, int)}切分List， 此方法返回的是原List的视图，也就是说原List有变更，切分后的结果也会变更。
     *
     * @param list 列表，为空时返回
     * @param size 每个段的长度，当长度超过list长度时，size按照list长度计算，即只返回一个节点
     * @param <T>  集合元素类型
     * @return 分段列表
     */
    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (Whether.empty(list)) {
            return Collections.emptyList();
        }

        return (list instanceof RandomAccess)
                ? new RandomAccessPartition<>(list, size)
                : new Partition<>(list, size);
    }

    /**
     * 对集合按照指定长度分段，每一个段为单独的集合，返回这个集合的列表
     *
     * <p>需要特别注意的是，此方法调用{@link List#subList(int, int)}切分List， 此方法返回的是原List的视图，也就是说原List有变更，切分后的结果也会变更。
     *
     * @param list 列表，为空时返回
     * @param size 每个段的长度，当长度超过list长度时，size按照list长度计算，即只返回一个节点
     * @param <T>  集合元素类型
     * @return 分段列表
     * @see #partition(List, int)
     */
    public static <T> List<List<T>> split(List<T> list, int size) {
        return partition(list, size);
    }

    /**
     * 将集合平均分成多个list，返回这个集合的列表
     *
     * <p>例：
     *
     * <pre>
     *     ListUtil.splitAvg(null, 3);	// []
     *     ListUtil.splitAvg(Arrays.asList(1, 2, 3, 4), 2);	// [[1, 2], [3, 4]]
     *     ListUtil.splitAvg(Arrays.asList(1, 2, 3), 5);	// [[1], [2], [3], [], []]
     *     ListUtil.splitAvg(Arrays.asList(1, 2, 3), 2);	// [[1, 2], [3]]
     * </pre>
     *
     * @param <T>   集合元素类型
     * @param list  集合
     * @param limit 要均分成几个list
     * @return 分段列表
     * @author potatoxf
     */
    public static <T> List<List<T>> splitAvg(List<T> list, int limit) {
        if (Whether.empty(list)) {
            return Collections.emptyList();
        }

        return (list instanceof RandomAccess)
                ? new RandomAccessAvgPartition<>(list, limit)
                : new AvgPartition<>(list, limit);
    }

    /**
     * 将指定元素交换到指定索引位置,其他元素的索引值不变<br>
     * 交换会修改原List<br>
     * 如果集合中有多个相同元素，只交换第一个找到的元素
     *
     * @param <T>         元素类型
     * @param list        列表
     * @param element     需交换元素
     * @param targetIndex 目标索引
     */
    public static <T> void swapTo(List<T> list, T element, Integer targetIndex) {
        if (Whether.noEmpty(list)) {
            final int index = list.indexOf(element);
            if (index >= 0) {
                Collections.swap(list, index, targetIndex);
            }
        }
    }

    /**
     * 将指定元素交换到指定元素位置,其他元素的索引值不变<br>
     * 交换会修改原List<br>
     * 如果集合中有多个相同元素，只交换第一个找到的元素
     *
     * @param <T>           元素类型
     * @param list          列表
     * @param element       需交换元素
     * @param targetElement 目标元素
     */
    public static <T> void swapElement(List<T> list, T element, T targetElement) {
        if (Whether.noEmpty(list)) {
            final int targetIndex = list.indexOf(targetElement);
            if (targetIndex >= 0) {
                swapTo(list, element, targetIndex);
            }
        }
    }


    //------------------------------------------------------------------------------------------------------------------
    //分页工具
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 将多个集合排序并显示不同的段落（分页）<br>
     * 采用{@link BoundedPriorityQueue}实现分页取局部
     *
     * @param pageNo      页码，从0开始计数，0表示第一页
     * @param pageSize    每页的条目数
     * @param comparator  比较器
     * @param collections 集合数组
     * @param <T>         集合元素类型
     * @return 分页后的段落内容
     */
    @SafeVarargs
    public static <T> List<T> sortPageAll(
            int pageNo, int pageSize, Comparator<T> comparator, Collection<T>... collections) {
        final List<T> list = new ArrayList<>(pageNo * pageSize);
        for (Collection<T> coll : collections) {
            list.addAll(coll);
        }
        if (null != comparator) {
            list.sort(comparator);
        }

        return page(pageNo, pageSize, list);
    }

    /**
     * 对指定List分页取值
     *
     * @param pageNo   页码，第一页的页码取决于 ，默认0
     * @param pageSize 每页的条目数
     * @param list     列表
     * @param <T>      集合元素类型
     * @return 分页后的段落内容
     */
    public static <T> List<T> page(int pageNo, int pageSize, List<T> list) {
        if (Whether.empty(list)) {
            return new ArrayList<>(0);
        }

        int resultSize = list.size();
        // 每页条目数大于总数直接返回所有
        if (resultSize <= pageSize) {
            if (pageNo < ((FIRST_PAGE_ZERO ? 0 : 1) + 1)) {
                return Collections.unmodifiableList(list);
            } else {
                // 越界直接返回空
                return new ArrayList<>(0);
            }
        }
        // 相乘可能会导致越界 临时用long
        if (((long) (pageNo - (FIRST_PAGE_ZERO ? 0 : 1)) * pageSize) > resultSize) {
            // 越界直接返回空
            return new ArrayList<>(0);
        }

        final int[] startEnd = getPageIndex(pageNo, pageSize);
        if (startEnd[1] > resultSize) {
            startEnd[1] = resultSize;
            if (startEnd[0] > startEnd[1]) {
                return new ArrayList<>(0);
            }
        }

        return sub(list, startEnd[0], startEnd[1]);
    }

    /**
     * 对指定List进行分页，逐页返回数据
     *
     * @param <T>              集合元素类型
     * @param list             源数据列表
     * @param pageSize         每页的条目数
     * @param pageListConsumer 单页数据函数式返回
     */
    public static <T> void page(List<T> list, int pageSize, Consumer<List<T>> pageListConsumer, int pageNo) {
        if (Whether.empty(list) || pageSize <= 0) {
            return;
        }

        final int total = list.size();
        final int totalPage = computeTotalPage(total, pageSize);
        for (; pageNo < totalPage + pageNo; pageNo++) {
            // 获取当前页在列表中对应的起止序号
            final int[] startEnd = getPageIndex(pageNo, pageSize);
            if (startEnd[1] > total) {
                startEnd[1] = total;
            }

            // 返回数据
            pageListConsumer.accept(sub(list, startEnd[0], startEnd[1]));
        }
    }

    /**
     * 将页数和每页条目数转换为开始位置和结束位置<br>
     * 此方法用于包括结束位置的分页方法<br>
     * 例如：
     *
     * <pre>
     * 页码：0，每页10 =》 [0, 10]
     * 页码：1，每页10 =》 [10, 20]
     * ……
     * </pre>
     *
     * <p>当 设置为1时：
     *
     * <pre>
     * 页码：1，每页10 =》 [0, 10]
     * 页码：2，每页10 =》 [10, 20]
     * ……
     * </pre>
     *
     * @param pageNo   页码（从0计数）
     * @param pageSize 每页条目数
     * @return 第一个数为开始位置，第二个数为结束位置
     */
    public static int[] getPageIndex(int pageNo, int pageSize) {
        int start = (Math.max(pageNo, FIRST_PAGE_ZERO ? 0 : 1)
                - (FIRST_PAGE_ZERO ? 0 : 1))
                * (pageSize = pageSize < 1 ? 0 : pageSize),
                end = start + pageSize;
        return new int[]{start, end};
    }

    /**
     * 根据总数计算总页数
     *
     * @param totalCount 总数
     * @param pageSize   每页数
     * @return 总页数
     */
    public static int computeTotalPage(long totalCount, int pageSize) {
        if (pageSize == 0) {
            return 0;
        }
        return Math.toIntExact((totalCount / pageSize + (totalCount % pageSize == 0 ? 0 : 1)));
    }

    /**
     * 分页彩虹算法<br>
     * 来自：<a
     * href="https://github.com/iceroot/iceroot/blob/master/src/main/java/com/icexxx/util/IceUtil.java">
     * https://github.com/iceroot/iceroot/blob/master/src/main/java/com/icexxx/util/IceUtil.java</a>
     * <br>
     * 通过传入的信息，生成一个分页列表显示
     *
     * @param pageNo       当前页
     * @param totalPage    总页数
     * @param displayCount 每屏展示的页数
     * @return 分页条
     */
    public static int[] rainbowPage(int pageNo, int totalPage, int displayCount) {
        // displayCount % 2
        boolean isEven = (displayCount & 1) == 0;
        int left = displayCount >> 1;
        int right = displayCount >> 1;

        int length = displayCount;
        if (isEven) {
            right++;
        }
        if (totalPage < displayCount) {
            length = totalPage;
        }
        int[] result = new int[length];
        if (totalPage >= displayCount) {
            if (pageNo <= left) {
                for (int i = 0; i < result.length; i++) {
                    result[i] = i + 1;
                }
            } else if (pageNo > totalPage - right) {
                for (int i = 0; i < result.length; i++) {
                    result[i] = i + totalPage - displayCount + 1;
                }
            } else {
                for (int i = 0; i < result.length; i++) {
                    result[i] = i + pageNo - left + (isEven ? 1 : 0);
                }
            }
        } else {
            for (int i = 0; i < result.length; i++) {
                result[i] = i + 1;
            }
        }
        return result;
    }
}

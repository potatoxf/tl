package pxf.tl.util;


import pxf.tl.api.PoolOfArray;
import pxf.tl.collection.UniqueKeySet;
import pxf.tl.comparator.ToolCompare;
import pxf.tl.exception.UtilException;
import pxf.tl.function.FunctionThrow;
import pxf.tl.help.Assert;
import pxf.tl.help.New;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.function.Editor;
import pxf.tl.text.StrJoiner;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 数组工具类
 *
 * @author potatoxf
 */
public final class ToolArray {

    private ToolArray() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    /**
     * 返回数组中第一个非空元素
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 非空元素，如果不存在非空元素或数组为空，返回{@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> T firstNonNull(T... array) {
        return firstMatch(Whether::noNvl, array);
    }

    /**
     * 返回数组中第一个匹配规则的值
     *
     * @param <T>       数组元素类型
     * @param predicate 匹配接口，实现此接口自定义匹配规则
     * @param array     数组
     * @return 匹配元素，如果不存在匹配元素或数组为空，返回 {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> T firstMatch(Predicate<T> predicate, T... array) {
        final int index = matchIndex(predicate, array);
        if (index < 0) {
            return null;
        }

        return array[index];
    }

    /**
     * 返回数组中第一个匹配规则的值的位置
     *
     * @param <T>       数组元素类型
     * @param predicate 匹配接口，实现此接口自定义匹配规则
     * @param array     数组
     * @return 匹配到元素的位置，-1表示未匹配到
     */
    @SuppressWarnings("unchecked")
    public static <T> int matchIndex(Predicate<T> predicate, T... array) {
        return matchIndex(predicate, 0, array);
    }

    /**
     * 返回数组中第一个匹配规则的值的位置
     *
     * @param <T>               数组元素类型
     * @param predicate         匹配接口，实现此接口自定义匹配规则
     * @param beginIndexInclude 检索开始的位置
     * @param array             数组
     * @return 匹配到元素的位置，-1表示未匹配到
     */
    @SuppressWarnings("unchecked")
    public static <T> int matchIndex(Predicate<T> predicate, int beginIndexInclude, T... array) {
        Assert.notNull(predicate, "Matcher must be not null !");
        if (Whether.noEmpty(array)) {
            for (int i = beginIndexInclude; i < array.length; i++) {
                if (predicate.test(array[i])) {
                    return i;
                }
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 获取数组对象的元素类型
     *
     * @param array 数组对象
     * @return 元素类型
     */
    public static Class<?> getComponentType(Object array) {
        return null == array ? null : array.getClass().getComponentType();
    }

    /**
     * 获取数组对象的元素类型
     *
     * @param arrayClass 数组类
     * @return 元素类型
     */
    public static Class<?> getComponentType(Class<?> arrayClass) {
        return null == arrayClass ? null : arrayClass.getComponentType();
    }

    /**
     * 根据数组元素类型，获取数组的类型<br>
     * 方法是通过创建一个空数组从而获取其类型
     *
     * @param componentType 数组元素类型
     * @return 数组类型
     */
    public static Class<?> getArrayType(Class<?> componentType) {
        return Array.newInstance(componentType, 0).getClass();
    }

    /**
     * 强转数组类型<br>
     * 强制转换的前提是数组元素类型可被强制转换<br>
     * 强制转换后会生成一个新数组
     *
     * @param type     数组类型或数组元素类型
     * @param arrayObj 原数组
     * @return 转换后的数组类型
     * @throws NullPointerException     提供参数为空
     * @throws IllegalArgumentException 参数arrayObj不是数组
     */
    public static Object[] cast(Class<?> type, Object arrayObj)
            throws NullPointerException, IllegalArgumentException {
        if (null == arrayObj) {
            throw new NullPointerException("Argument [arrayObj] is null !");
        }
        if (!Whether.arrayType(type)) {
            throw new IllegalArgumentException("Argument [arrayObj] is not array !");
        }

        final Class<?> componentType = type.isArray() ? type.getComponentType() : type;
        final Object[] array = (Object[]) arrayObj;
        final Object[] result = New.array(componentType, array.length);
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

    /**
     * 将新元素添加到已有数组中<br>
     * 添加新元素会生成一个新的数组，不影响原数组
     *
     * @param <T>         数组元素类型
     * @param buffer      已有数组
     * @param newElements 新元素
     * @return 新数组
     */
    @SafeVarargs
    public static <T> T[] append(T[] buffer, T... newElements) {
        if (Whether.empty(buffer)) {
            return newElements;
        }
        return insert(buffer, buffer.length, newElements);
    }

    /**
     * 将新元素添加到已有数组中<br>
     * 添加新元素会生成一个新的数组，不影响原数组
     *
     * @param <T>         数组元素类型
     * @param array       已有数组
     * @param newElements 新元素
     * @return 新数组
     */
    @SafeVarargs
    public static <T> Object append(Object array, T... newElements) {
        if (Whether.empty(array)) {
            return newElements;
        }
        return insert(array, length(array), newElements);
    }

    /**
     * 将元素值设置为数组的某个位置，当给定的index大于数组长度，则追加
     *
     * @param <T>    数组元素类型
     * @param buffer 已有数组
     * @param index  位置，大于长度追加，否则替换
     * @param value  新值
     * @return 新数组或原有数组
     */
    public static <T> T[] setOrAppend(T[] buffer, int index, T value) {
        if (index < buffer.length) {
            Array.set(buffer, index, value);
            return buffer;
        } else {
            if (Whether.empty(buffer)) {
                // issue#I5APJE
                // 可变长类型在buffer为空的情况下，类型会被擦除，导致报错，此处修正
                final T[] values = New.array(value.getClass(), 1);
                values[0] = value;
                return append(buffer, values);
            }
            return append(buffer, value);
        }
    }

    /**
     * 将元素值设置为数组的某个位置，当给定的index大于数组长度，则追加
     *
     * @param array 已有数组
     * @param index 位置，大于长度追加，否则替换
     * @param value 新值
     * @return 新数组或原有数组
     */
    public static Object setOrAppend(Object array, int index, Object value) {
        if (index < length(array)) {
            Array.set(array, index, value);
            return array;
        } else {
            return append(array, value);
        }
    }

    /**
     * 将新元素插入到到已有数组中的某个位置<br>
     * 添加新元素会生成一个新数组或原有数组<br>
     * 如果插入位置为为负数，那么生成一个由插入元素顺序加已有数组顺序的新数组
     *
     * @param <T>    数组元素类型
     * @param buffer 已有数组
     * @param index  位置，大于长度追加，否则替换，&lt;0表示从头部追加
     * @param values 新值
     * @return 新数组或原有数组
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T[] replace(T[] buffer, int index, T... values) {
        if (Whether.empty(values)) {
            return buffer;
        }
        if (Whether.empty(buffer)) {
            return values;
        }
        if (index < 0) {
            // 从头部追加
            return insert(buffer, 0, values);
        }
        if (index >= buffer.length) {
            // 超出长度，尾部追加
            return append(buffer, values);
        }

        if (buffer.length >= values.length + index) {
            System.arraycopy(values, 0, buffer, index, values.length);
            return buffer;
        }

        // 替换长度大于原数组长度，新建数组
        int newArrayLength = index + values.length;
        final T[] result = New.array(buffer.getClass().getComponentType(), newArrayLength);
        System.arraycopy(buffer, 0, result, 0, index);
        System.arraycopy(values, 0, result, index, values.length);
        return result;
    }

    /**
     * 将新元素插入到到已有数组中的某个位置<br>
     * 添加新元素会生成一个新的数组，不影响原数组<br>
     * 如果插入位置为为负数，从原数组从后向前计数，若大于原数组长度，则空白处用null填充
     *
     * @param <T>         数组元素类型
     * @param buffer      已有数组
     * @param index       插入位置，此位置为对应此位置元素之前的空档
     * @param newElements 新元素
     * @return 新数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] insert(T[] buffer, int index, T... newElements) {
        return (T[]) insert((Object) buffer, index, newElements);
    }

    /**
     * 将新元素插入到到已有数组中的某个位置<br>
     * 添加新元素会生成一个新的数组，不影响原数组<br>
     * 如果插入位置为为负数，从原数组从后向前计数，若大于原数组长度，则空白处用null填充
     *
     * @param <T>         数组元素类型
     * @param array       已有数组
     * @param index       插入位置，此位置为对应此位置元素之前的空档
     * @param newElements 新元素
     * @return 新数组
     */
    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    public static <T> Object insert(Object array, int index, T... newElements) {
        if (Whether.empty(newElements)) {
            return array;
        }
        if (Whether.empty(array)) {
            return newElements;
        }

        final int len = length(array);
        if (index < 0) {
            index = (index % len) + len;
        }

        final T[] result =
                New.array(array.getClass().getComponentType(), Math.max(len, index) + newElements.length);
        System.arraycopy(array, 0, result, 0, Math.min(len, index));
        System.arraycopy(newElements, 0, result, index, newElements.length);
        if (index < len) {
            System.arraycopy(array, index, result, index + newElements.length, len - index);
        }
        return result;
    }

    /**
     * 生成一个新的重新设置大小的数组<br>
     * 调整大小后拷贝原数组到新数组下。扩大则占位前N个位置，缩小则截断
     *
     * @param <T>           数组元素类型
     * @param data          原数组
     * @param newSize       新的数组大小
     * @param componentType 数组元素类型
     * @return 调整后的新数组
     */
    public static <T> T[] resize(T[] data, int newSize, Class<?> componentType) {
        if (newSize < 0) {
            return data;
        }

        final T[] newArray = New.array(componentType, newSize);
        if (newSize > 0 && Whether.noEmpty(data)) {
            System.arraycopy(data, 0, newArray, 0, Math.min(data.length, newSize));
        }
        return newArray;
    }

    /**
     * 生成一个新的重新设置大小的数组<br>
     * 调整大小后拷贝原数组到新数组下。扩大则占位前N个位置，其它位置补充0，缩小则截断
     *
     * @param array   原数组
     * @param newSize 新的数组大小
     * @return 调整后的新数组
     */
    public static Object resize(Object array, int newSize) {
        if (newSize < 0) {
            return array;
        }
        if (null == array) {
            return null;
        }
        final int length = length(array);
        final Object newArray = Array.newInstance(array.getClass().getComponentType(), newSize);
        if (newSize > 0 && Whether.noEmpty(array)) {
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(array, 0, newArray, 0, Math.min(length, newSize));
        }
        return newArray;
    }

    /**
     * 生成一个新的重新设置大小的数组<br>
     * 新数组的类型为原数组的类型，调整大小后拷贝原数组到新数组下。扩大则占位前N个位置，缩小则截断
     *
     * @param <T>     数组元素类型
     * @param buffer  原数组
     * @param newSize 新的数组大小
     * @return 调整后的新数组
     */
    public static <T> T[] resize(T[] buffer, int newSize) {
        return resize(buffer, newSize, buffer.getClass().getComponentType());
    }

    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param <T>    数组元素类型
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    @SafeVarargs
    public static <T> T[] addAll(T[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        int length = 0;
        for (T[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }
        T[] result = New.array(arrays.getClass().getComponentType().getComponentType(), length);

        length = 0;
        for (T[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    /**
     * 包装 {@link System#arraycopy(Object, int, Object, int, int)}<br>
     * 数组复制
     *
     * @param src     源数组
     * @param srcPos  源数组开始位置
     * @param dest    目标数组
     * @param destPos 目标数组开始位置
     * @param length  拷贝数组长度
     * @return 目标数组
     */
    public static Object copy(Object src, int srcPos, Object dest, int destPos, int length) {
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(src, srcPos, dest, destPos, length);
        return dest;
    }

    /**
     * 包装 {@link System#arraycopy(Object, int, Object, int, int)}<br>
     * 数组复制，缘数组和目标数组都是从位置0开始复制
     *
     * @param src    源数组
     * @param dest   目标数组
     * @param length 拷贝数组长度
     * @return 目标数组
     */
    public static Object copy(Object src, Object dest, int length) {
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(src, 0, dest, 0, length);
        return dest;
    }

    /**
     * 克隆数组
     *
     * @param <T>   数组元素类型
     * @param array 被克隆的数组
     * @return 新数组
     */
    public static <T> T[] clone(T[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 克隆数组，如果非数组返回{@code null}
     *
     * @param <T> 数组元素类型
     * @param obj 数组对象
     * @return 克隆后的数组对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T clone(final T obj) {
        if (null == obj) {
            return null;
        }
        if (Whether.arrayObj(obj)) {
            final Object result;
            final Class<?> componentType = obj.getClass().getComponentType();
            if (componentType.isPrimitive()) { // 原始类型
                int length = Array.getLength(obj);
                result = Array.newInstance(componentType, length);
                while (length-- > 0) {
                    Array.set(result, length, Array.get(obj, length));
                }
            } else {
                result = ((Object[]) obj).clone();
            }
            return (T) result;
        }
        return null;
    }

    /**
     * 编辑数组<br>
     * 编辑过程通过传入的Editor实现来返回需要的元素内容，这个Editor实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，如果返回{@code null}表示这个元素对象抛弃
     * 2、修改元素对象，返回集合中为修改后的对象
     * </pre>
     *
     * <p>
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param editor 编辑器接口，{@code null}返回原集合
     * @return 编辑后的数组
     */
    public static <T> T[] edit(T[] array, Editor<T> editor) {
        if (null == editor) {
            return array;
        }

        final ArrayList<T> list = new ArrayList<>(array.length);
        T modified;
        for (T t : array) {
            modified = editor.edit(t);
            if (null != modified) {
                list.add(modified);
            }
        }
        final T[] result = New.array(array.getClass().getComponentType(), list.size());
        return list.toArray(result);
    }

    /**
     * 过滤<br>
     * 过滤过程通过传入的Filter实现来过滤返回需要的元素内容，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Predicate#test(Object)}方法返回true的对象将被加入结果集合中
     * </pre>
     *
     * @param <T>       数组元素类型
     * @param array     数组
     * @param predicate 过滤器接口，用于定义过滤规则，{@code null}返回原集合
     * @return 过滤后的数组
     */
    public static <T> T[] filter(T[] array, Predicate<T> predicate) {
        if (null == array || null == predicate) {
            return array;
        }
        return edit(array, t -> predicate.test(t) ? t : null);
    }

    /**
     * 去除{@code null} 元素
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 处理后的数组
     */
    public static <T> T[] removeNull(T[] array) {
        return edit(
                array,
                t -> {
                    // 返回null便不加入集合
                    return t;
                });
    }

    /**
     * 去除{@code null}或者"" 元素
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 处理后的数组
     */
    public static <T extends CharSequence> T[] removeEmpty(T[] array) {
        return filter(array, Whether::noEmpty);
    }

    /**
     * 去除{@code null}或者""或者空白字符串 元素
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 处理后的数组
     */
    public static <T extends CharSequence> T[] removeBlank(T[] array) {
        return filter(array, Whether::noBlank);
    }

    /**
     * 数组元素中的null转换为""
     *
     * @param array 数组
     * @return 新数组
     */
    public static String[] nullToEmpty(String[] array) {
        return edit(array, t -> null == t ? ToolString.EMPTY : t);
    }

    /**
     * 映射键值（参考Python的zip()函数）<br>
     * 例如：<br>
     * keys = [a,b,c,d]<br>
     * values = [1,2,3,4]<br>
     * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param <K>     Key类型
     * @param <V>     Value类型
     * @param keys    键列表
     * @param values  值列表
     * @param isOrder 是否有序
     * @return Map
     */
    public static <K, V> Map<K, V> zip(K[] keys, V[] values, boolean isOrder) {
        if (Whether.empty(keys) || Whether.empty(values)) {
            return null;
        }

        final int size = Math.min(keys.length, values.length);
        final Map<K, V> map = New.map(isOrder, size);
        for (int i = 0; i < size; i++) {
            map.put(keys[i], values[i]);
        }

        return map;
    }

    /**
     * 映射键值（参考Python的zip()函数），返回Map无序<br>
     * 例如：<br>
     * keys = [a,b,c,d]<br>
     * values = [1,2,3,4]<br>
     * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param <K>    Key类型
     * @param <V>    Value类型
     * @param keys   键列表
     * @param values 值列表
     * @return Map
     */
    public static <K, V> Map<K, V> zip(K[] keys, V[] values) {
        return zip(keys, values, false);
    }

    // ------------------------------------------------------------------- indexOf and lastIndexOf and
    // contains

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param <T>               数组类型
     * @param array             数组
     * @param value             被检查的元素
     * @param beginIndexInclude 检索开始的位置
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static <T> int indexOf(T[] array, Object value, int beginIndexInclude) {
        return matchIndex((obj) -> ToolObject.equal(value, obj), beginIndexInclude, array);
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param <T>   数组类型
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static <T> int indexOf(T[] array, Object value) {
        return matchIndex((obj) -> ToolObject.equal(value, obj), array);
    }

    /**
     * 返回数组中指定元素所在位置，忽略大小写，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOfIgnoreCase(CharSequence[] array, CharSequence value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (ToolString.equalsIgnoreCase(array[i], value)) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param <T>   数组类型
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static <T> int lastIndexOf(T[] array, Object value) {
        if (Whether.empty(array)) {
            return INDEX_NOT_FOUND;
        }
        return lastIndexOf(array, value, array.length - 1);
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param <T>        数组类型
     * @param array      数组
     * @param value      被检查的元素
     * @param endInclude 查找方式为从后向前查找，查找的数组结束位置，一般为array.length-1
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static <T> int lastIndexOf(T[] array, Object value, int endInclude) {
        if (Whether.noEmpty(array)) {
            for (int i = endInclude; i >= 0; i--) {
                if (ToolObject.equal(value, array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static <T> boolean contains(T[] array, T value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含指定元素中的任意一个
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param values 被检查的多个元素
     * @return 是否包含指定元素中的任意一个
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean containsAny(T[] array, T... values) {
        for (T value : values) {
            if (contains(array, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数组中是否包含指定元素中的全部
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param values 被检查的多个元素
     * @return 是否包含指定元素中的全部
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean containsAll(T[] array, T... values) {
        for (T value : values) {
            if (!contains(array, value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 数组中是否包含元素，忽略大小写
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean containsIgnoreCase(CharSequence[] array, CharSequence value) {
        return indexOfIgnoreCase(array, value) > INDEX_NOT_FOUND;
    }

    // ------------------------------------------------------------------- Wrap and unwrap

    /**
     * 包装数组对象
     *
     * @param obj 对象，可以是对象数组或者基本类型数组
     * @return 包装类型数组或对象数组
     * @throws UtilException 对象为非数组
     */
    public static Object[] wrap(Object obj) {
        if (null == obj) {
            return null;
        }
        if (Whether.arrayObj(obj)) {
            try {
                return (Object[]) obj;
            } catch (Exception e) {
                final String className = obj.getClass().getComponentType().getName();
                switch (className) {
                    case "long":
                        return wrap((long[]) obj);
                    case "int":
                        return wrap((int[]) obj);
                    case "short":
                        return wrap((short[]) obj);
                    case "char":
                        return wrap((char[]) obj);
                    case "byte":
                        return wrap((byte[]) obj);
                    case "boolean":
                        return wrap((boolean[]) obj);
                    case "float":
                        return wrap((float[]) obj);
                    case "double":
                        return wrap((double[]) obj);
                    default:
                        throw new UtilException(e);
                }
            }
        }
        throw new UtilException(String.format("[%s] is not Array!", obj.getClass()));
    }

    /**
     * 获取数组对象中指定index的值，支持负数，例如-1表示倒数第一个值<br>
     * 如果数组下标越界，返回null
     *
     * @param <T>   数组元素类型
     * @param array 数组对象
     * @param index 下标，支持负数
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object array, int index) {
        if (null == array) {
            return null;
        }

        if (index < 0) {
            index += Array.getLength(array);
        }
        try {
            return (T) Array.get(array, index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * 获取数组中指定多个下标元素值，组成新数组
     *
     * @param <T>     数组元素类型
     * @param array   数组，如果提供为{@code null}则返回{@code null}
     * @param indexes 下标列表
     * @return 结果
     */
    public static <T> T[] getAny(Object array, int... indexes) {
        if (null == array) {
            return null;
        }
        if (null == indexes) {
            return New.array(array.getClass().getComponentType(), 0);
        }

        final T[] result = New.array(array.getClass().getComponentType(), indexes.length);
        for (int i = 0; i < indexes.length; i++) {
            result[i] = ToolArray.get(array, indexes[i]);
        }
        return result;
    }

    /**
     * 获取子数组
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static <T> T[] sub(T[] array, int start, int end) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return New.array(array.getClass().getComponentType(), 0);
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return New.array(array.getClass().getComponentType(), 0);
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     */
    public static Object[] sub(Object array, int start, int end) {
        return sub(array, start, end, 1);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @param step  步进
     * @return 新的数组
     */
    public static Object[] sub(Object array, int start, int end, int step) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new Object[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new Object[0];
            }
            end = length;
        }

        if (step <= 1) {
            step = 1;
        }

        final ArrayList<Object> list = new ArrayList<>();
        for (int i = start; i < end; i += step) {
            list.add(get(array, i));
        }

        return list.toArray();
    }

    /**
     * 数组或集合转String
     *
     * @param obj 集合或数组对象
     * @return 数组字符串，与集合转字符串格式相同
     */
    public static String toString(Object obj) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        } else if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        } else if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        } else if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        } else if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        } else if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        } else if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        } else if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        } else if (Whether.arrayObj(obj)) {
            // 对象数组
            try {
                return Arrays.deepToString((Object[]) obj);
            } catch (Exception ignore) {
                // ignore
            }
        }

        return obj.toString();
    }

    /**
     * 获取数组长度<br>
     * 如果参数为{@code null}，返回0
     *
     * <pre>
     * HelpArray.length(null)            = 0
     * HelpArray.length([])              = 0
     * HelpArray.length([null])          = 1
     * HelpArray.length([true, false])   = 2
     * HelpArray.length([1, 2, 3])       = 3
     * HelpArray.length(["a", "b", "c"]) = 3
     * </pre>
     *
     * @param array 数组对象
     * @return 数组长度
     * @throws IllegalArgumentException 如果参数不为数组，抛出此异常
     * @see Array#getLength(Object)
     */
    public static int length(Object array) throws IllegalArgumentException {
        if (null == array) {
            return 0;
        }
        return Array.getLength(array);
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param <T>         被处理的集合
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static <T> String join(T[] array, CharSequence conjunction) {
        return join(array, conjunction, null, null);
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param <T>       被处理的集合
     * @param array     数组
     * @param delimiter 分隔符
     * @param prefix    每个元素添加的前缀，null表示不添加
     * @param suffix    每个元素添加的后缀，null表示不添加
     * @return 连接后的字符串
     */
    public static <T> String join(T[] array, CharSequence delimiter, String prefix, String suffix) {
        if (null == array) {
            return null;
        }

        return StrJoiner.of(delimiter, prefix, suffix)
                // 每个元素都添加前后缀
                .setWrapElement(true)
                .append(array)
                .toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param <T>         被处理的集合
     * @param array       数组
     * @param conjunction 分隔符
     * @param editor      每个元素的编辑器，null表示不编辑
     * @return 连接后的字符串
     */
    public static <T> String join(T[] array, CharSequence conjunction, Editor<T> editor) {
        return StrJoiner.of(conjunction)
                .append(array, (t) -> String.valueOf(editor.edit(t)))
                .toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(Object array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }
        if (false == Whether.arrayObj(array)) {
            throw new IllegalArgumentException(
                    String.format("[%s] is not a Array!", array.getClass()));
        }

        return StrJoiner.of(conjunction).append(array).toString();
    }

    // ---------------------------------------------------------------------- remove

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param <T>   数组元素类型
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] remove(T[] array, int index) throws IllegalArgumentException {
        return (T[]) remove((Object) array, index);
    }

    // ---------------------------------------------------------------------- removeEle

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param <T>     数组元素类型
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static <T> T[] removeEle(T[] array, T element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    // ---------------------------------------------------------------------- Reverse array

    /**
     * 反转数组，会变更原数组
     *
     * @param <T>                 数组元素类型
     * @param array               数组，会变更
     * @param startIndexInclusive 开始位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     */
    public static <T> T[] reverse(
            T[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (Whether.empty(array)) {
            return array;
        }
        int i = Math.max(startIndexInclusive, 0);
        int j = Math.min(array.length, endIndexExclusive) - 1;
        T tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param <T>   数组元素类型
     * @param array 数组，会变更
     * @return 变更后的原数组
     */
    public static <T> T[] reverse(T[] array) {
        return reverse(array, 0, array.length);
    }

    // ------------------------------------------------------------------------------------------------------------ min and max

    /**
     * 取最小值
     *
     * @param <T>         元素类型
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static <T extends Comparable<? super T>> T min(T[] numberArray) {
        return min(numberArray, null);
    }

    /**
     * 取最小值
     *
     * @param <T>         元素类型
     * @param numberArray 数字数组
     * @param comparator  比较器，null按照默认比较
     * @return 最小值
     */
    public static <T extends Comparable<? super T>> T min(T[] numberArray, Comparator<T> comparator) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        T min = numberArray[0];
        for (T t : numberArray) {
            if (ToolCompare.compare(min, t, comparator) > 0) {
                min = t;
            }
        }
        return min;
    }

    /**
     * 取最大值
     *
     * @param <T>         元素类型
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static <T extends Comparable<? super T>> T max(T[] numberArray) {
        return max(numberArray, null);
    }

    /**
     * 取最大值
     *
     * @param <T>         元素类型
     * @param numberArray 数字数组
     * @param comparator  比较器，null表示默认比较器
     * @return 最大值
     */
    public static <T extends Comparable<? super T>> T max(T[] numberArray, Comparator<T> comparator) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        T max = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (ToolCompare.compare(max, numberArray[i], comparator) < 0) {
                max = numberArray[i];
            }
        }
        return max;
    }

    // 使用Fisher–Yates洗牌算法，以线性时间复杂度打乱数组顺序

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param <T>   元素类型
     * @param array 数组，会变更
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static <T> T[] shuffle(T[] array) {
        return shuffle(array, ToolRandom.getRandom());
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param <T>    元素类型
     * @param array  数组，会变更
     * @param random 随机数生成器
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static <T> T[] shuffle(T[] array, Random random) {
        if (array == null || random == null || array.length <= 1) {
            return array;
        }

        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }

        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param <T>    元素类型
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     */
    public static <T> T[] swap(T[] array, int index1, int index2) {
        if (Whether.empty(array)) {
            throw new IllegalArgumentException("Array must not empty !");
        }
        T tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组对象
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     */
    public static Object swap(Object array, int index1, int index2) {
        if (Whether.empty(array)) {
            throw new IllegalArgumentException("Array must not empty !");
        }
        Object tmp = get(array, index1);
        Array.set(array, index1, Array.get(array, index2));
        Array.set(array, index2, tmp);
        return array;
    }

    /**
     * 计算{@code null}或空元素对象的个数，通过{@link Whether#empty(Object)} 判断元素
     *
     * @param args 被检查的对象,一个或者多个
     * @return 存在{@code null}的数量
     */
    public static int emptyCount(Object... args) {
        int count = 0;
        if (Whether.noEmpty(args)) {
            for (Object element : args) {
                if (Whether.empty(element)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 去重数组中的元素，去重后生成新的数组，原数组不变<br>
     * 此方法通过{@link LinkedHashSet} 去重
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 去重后的数组
     */
    public static <T> T[] distinct(T[] array) {
        if (Whether.empty(array)) {
            return array;
        }

        final Set<T> set = new LinkedHashSet<>(array.length, 1);
        Collections.addAll(set, array);
        return New.array(set, array.getClass().getComponentType());
    }

    /**
     * 去重数组中的元素，去重后生成新的数组，原数组不变<br>
     * 此方法通过{@link LinkedHashSet} 去重
     *
     * @param <T>             数组元素类型
     * @param <K>             唯一键类型
     * @param array           数组
     * @param uniqueGenerator 唯一键生成器
     * @param override        是否覆盖模式，如果为{@code true}，加入的新值会覆盖相同key的旧值，否则会忽略新加值
     * @return 去重后的数组
     */
    @SuppressWarnings("unchecked")
    public static <T, K> T[] distinct(T[] array, Function<T, K> uniqueGenerator, boolean override) {
        if (Whether.empty(array)) {
            return array;
        }

        final UniqueKeySet<K, T> set = new UniqueKeySet<>(true, uniqueGenerator);
        if (override) {
            Collections.addAll(set, array);
        } else {
            for (T t : array) {
                set.addIfAbsent(t);
            }
        }

        if (set.size() == array.length) {
            return array;
        }
        return New.array(set, array.getClass().getComponentType());
    }

    /**
     * 按照指定规则，将一种类型的数组转换为另一种类型
     *
     * @param array               被转换的数组
     * @param targetComponentType 目标的元素类型
     * @param func                转换规则函数
     * @param <T>                 原数组类型
     * @param <R>                 目标数组类型
     * @return 转换后的数组
     */
    public static <T, R> R[] map(
            T[] array, Class<R> targetComponentType, Function<? super T, ? extends R> func) {
        final R[] result = New.array(targetComponentType, array.length);
        for (int i = 0; i < array.length; i++) {
            result[i] = func.apply(array[i]);
        }
        return result;
    }

    /**
     * 按照指定规则，将一种类型的数组转换为另一种类型
     *
     * @param array               被转换的数组
     * @param targetComponentType 目标的元素类型
     * @param func                转换规则函数
     * @param <T>                 原数组类型
     * @param <R>                 目标数组类型
     * @return 转换后的数组
     */
    public static <T, R> R[] map(
            Object array, Class<R> targetComponentType, Function<? super T, ? extends R> func) {
        final int length = length(array);
        final R[] result = New.array(targetComponentType, length);
        for (int i = 0; i < length; i++) {
            result[i] = func.apply(get(array, i));
        }
        return result;
    }

    /**
     * 按照指定规则，将一种类型的数组元素提取后转换为{@link List}
     *
     * @param array 被转换的数组
     * @param func  转换规则函数
     * @param <T>   原数组类型
     * @param <R>   目标数组类型
     * @return 转换后的数组
     */
    public static <T, R> List<R> map(T[] array, Function<? super T, ? extends R> func) {
        return Arrays.stream(array).map(func).collect(Collectors.toList());
    }

    /**
     * 按照指定规则，将一种类型的数组元素提取后转换为{@link Set}
     *
     * @param array 被转换的数组
     * @param func  转换规则函数
     * @param <T>   原数组类型
     * @param <R>   目标数组类型
     * @return 转换后的数组
     */
    public static <T, R> Set<R> mapToSet(T[] array, Function<? super T, ? extends R> func) {
        return Arrays.stream(array).map(func).collect(Collectors.toSet());
    }

    /**
     * 判断两个数组是否相等，判断依据包括数组长度和每个元素都相等。
     *
     * @param array1 数组1
     * @param array2 数组2
     * @return 是否相等
     */
    public static boolean equals(Object array1, Object array2) {
        if (array1 == array2) {
            return true;
        }
        if (Whether.anyNvl(array1, array2)) {
            return false;
        }

        Assert.isTrue(Whether.arrayObj(array1), "First is not a Array !");
        Assert.isTrue(Whether.arrayObj(array2), "Second is not a Array !");

        if (array1 instanceof long[]) {
            return Arrays.equals((long[]) array1, (long[]) array2);
        } else if (array1 instanceof int[]) {
            return Arrays.equals((int[]) array1, (int[]) array2);
        } else if (array1 instanceof short[]) {
            return Arrays.equals((short[]) array1, (short[]) array2);
        } else if (array1 instanceof char[]) {
            return Arrays.equals((char[]) array1, (char[]) array2);
        } else if (array1 instanceof byte[]) {
            return Arrays.equals((byte[]) array1, (byte[]) array2);
        } else if (array1 instanceof double[]) {
            return Arrays.equals((double[]) array1, (double[]) array2);
        } else if (array1 instanceof float[]) {
            return Arrays.equals((float[]) array1, (float[]) array2);
        } else if (array1 instanceof boolean[]) {
            return Arrays.equals((boolean[]) array1, (boolean[]) array2);
        } else {
            // Not an array of primitives
            return Arrays.deepEquals((Object[]) array1, (Object[]) array2);
        }
    }

    /**
     * 查找子数组的位置
     *
     * @param array    数组
     * @param subArray 子数组
     * @param <T>      数组元素类型
     * @return 子数组的开始位置，即子数字第一个元素在数组中的位置
     */
    public static <T> boolean isSub(T[] array, T[] subArray) {
        return indexOfSub(array, subArray) > INDEX_NOT_FOUND;
    }

    /**
     * 查找子数组的位置
     *
     * @param array    数组
     * @param subArray 子数组
     * @param <T>      数组元素类型
     * @return 子数组的开始位置，即子数字第一个元素在数组中的位置
     */
    public static <T> int indexOfSub(T[] array, T[] subArray) {
        return indexOfSub(array, 0, subArray);
    }

    /**
     * 查找子数组的位置
     *
     * @param array        数组
     * @param beginInclude 查找开始的位置（包含）
     * @param subArray     子数组
     * @param <T>          数组元素类型
     * @return 子数组的开始位置，即子数字第一个元素在数组中的位置
     */
    public static <T> int indexOfSub(T[] array, int beginInclude, T[] subArray) {
        if (Whether.empty(array) || Whether.empty(subArray) || subArray.length > array.length) {
            return INDEX_NOT_FOUND;
        }
        int firstIndex = indexOf(array, subArray[0], beginInclude);
        if (firstIndex < 0 || firstIndex + subArray.length > array.length) {
            return INDEX_NOT_FOUND;
        }

        for (int i = 0; i < subArray.length; i++) {
            if (false == ToolObject.equal(array[i + firstIndex], subArray[i])) {
                return indexOfSub(array, firstIndex + 1, subArray);
            }
        }

        return firstIndex;
    }

    /**
     * 查找最后一个子数组的开始位置
     *
     * @param array    数组
     * @param subArray 子数组
     * @param <T>      数组元素类型
     * @return 最后一个子数组的开始位置，即子数字第一个元素在数组中的位置
     */
    public static <T> int lastIndexOfSub(T[] array, T[] subArray) {
        if (Whether.empty(array) || Whether.empty(subArray)) {
            return INDEX_NOT_FOUND;
        }
        return lastIndexOfSub(array, array.length - 1, subArray);
    }

    /**
     * 查找最后一个子数组的开始位置
     *
     * @param array      数组
     * @param endInclude 查找结束的位置（包含）
     * @param subArray   子数组
     * @param <T>        数组元素类型
     * @return 最后一个子数组的开始位置，即子数字第一个元素在数组中的位置
     */
    public static <T> int lastIndexOfSub(T[] array, int endInclude, T[] subArray) {
        if (Whether.empty(array)
                || Whether.empty(subArray)
                || subArray.length > array.length
                || endInclude < 0) {
            return INDEX_NOT_FOUND;
        }

        int firstIndex = lastIndexOf(array, subArray[0]);
        if (firstIndex < 0 || firstIndex + subArray.length > array.length) {
            return INDEX_NOT_FOUND;
        }

        for (int i = 0; i < subArray.length; i++) {
            if (false == ToolObject.equal(array[i + firstIndex], subArray[i])) {
                return lastIndexOfSub(array, firstIndex - 1, subArray);
            }
        }

        return firstIndex;
    }

    // O(n)时间复杂度检查数组是否有序

    /**
     * 检查数组是否有序，即comparator.compare(array[i], array[i + 1]) &lt;= 0，若传入空数组或空比较器，则返回false
     *
     * @param array      数组
     * @param comparator 比较器
     * @param <T>        数组元素类型
     * @return 数组是否有序
     * @author potatoxf
     */
    public static <T> boolean isSorted(T[] array, Comparator<? super T> comparator) {
        if (array == null || comparator == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (comparator.compare(array[i], array[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查数组是否升序，即array[i].compareTo(array[i + 1]) &lt;= 0，若传入空数组，则返回false
     *
     * @param <T>   数组元素类型，该类型需要实现Comparable接口
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static <T extends Comparable<? super T>> boolean isSorted(T[] array) {
        return isSortedASC(array);
    }

    /**
     * 检查数组是否升序，即array[i].compareTo(array[i + 1]) &lt;= 0，若传入空数组，则返回false
     *
     * @param <T>   数组元素类型，该类型需要实现Comparable接口
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static <T extends Comparable<? super T>> boolean isSortedASC(T[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否降序，即array[i].compareTo(array[i + 1]) &gt;= 0，若传入空数组，则返回false
     *
     * @param <T>   数组元素类型，该类型需要实现Comparable接口
     * @param array 数组
     * @return 数组是否降序
     * @author potatoxf
     */
    public static <T extends Comparable<? super T>> boolean isSortedDESC(T[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].compareTo(array[i + 1]) < 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * 过滤数组
     *
     * @param array   数组
     * @param process 处理器，返回null时过滤掉
     * @param <T>     类型
     * @return T[]
     */
    public static <T, R> R[] filter(T[] array, FunctionThrow<T, R, Throwable> process) {
        if (Whether.empty(array)) {
            return null;
        }
        List<R> list = new ArrayList<>(array.length);
        for (T ele : array) {
            try {
                R t = process.apply(ele);
                if (t != null) {
                    list.add(t);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        if (Whether.empty(list)) {
            return null;
        }
        return (R[]) Arrays.copyOf(list.toArray(), list.size(), array.getClass());
    }

    /**
     * @param array
     * @param otherArray
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> boolean equals(T[] array, U[] otherArray) {
        return equals(array, otherArray, Object::equals);
    }

    /**
     * @param array
     * @param otherArray
     * @param equalPredicate
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> boolean equals(T[] array, U[] otherArray, BiPredicate<T, U> equalPredicate) {
        if (array == otherArray) return true;
        if (array == null || otherArray == null) return false;

        int length = array.length;
        if (otherArray.length != length) return false;

        for (int i = 0; i < length; i++) {
            if (!(array[i] == null
                    ? otherArray[i] == null
                    : equalPredicate.test(array[i], otherArray[i]))) return false;
        }

        return true;
    }

    /**
     * @param array
     * @param otherArray
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> boolean isPresent(T[] array, U[] otherArray) {
        return isPresent(array, otherArray, Object::equals);
    }

    /**
     * @param array
     * @param otherArray
     * @param equalPredicate
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> boolean isPresent(
            T[] array, U[] otherArray, BiPredicate<T, U> equalPredicate) {
        if (array == otherArray) return true;
        if (array == null || otherArray == null) return false;

        int length = array.length;
        if (otherArray.length != length) return false;

        Set<T> set = new HashSet<>(length, 1);
        Collections.addAll(set, array);
        Set<U> otherSet = new HashSet<>(length, 1);
        Collections.addAll(otherSet, otherArray);

        if (set.size() != otherSet.size()) {
            return false;
        }
        for (T t : set) {
            for (U u : otherSet) {
                if (equalPredicate.test(t, u)) {
                    set.remove(t);
                    otherSet.remove(u);
                    break;
                } else {
                    return false;
                }
            }
        }

        return true;
    }


    //------------------------------------------------------------------------------------------------------------------
    //基本类型数组
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 数组中元素未找到的下标，值为-1
     */
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * 生成一个新的重新设置大小的数组<br>
     * 调整大小后拷贝原数组到新数组下。扩大则占位前N个位置，其它位置补充0，缩小则截断
     *
     * @param bytes   原数组
     * @param newSize 新的数组大小
     * @return 调整后的新数组
     */
    public static byte[] resize(byte[] bytes, int newSize) {
        if (newSize < 0) {
            return bytes;
        }
        final byte[] newArray = new byte[newSize];
        if (newSize > 0 && Whether.noEmpty(bytes)) {
            System.arraycopy(bytes, 0, newArray, 0, Math.min(bytes.length, newSize));
        }
        return newArray;
    }

    // ---------------------------------------------------------------------- addAll

    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    public static byte[] addAll(byte[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        // 计算总长度
        int length = 0;
        for (byte[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }

        final byte[] result = new byte[length];
        length = 0;
        for (byte[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    public static int[] addAll(int[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        // 计算总长度
        int length = 0;
        for (int[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }

        final int[] result = new int[length];
        length = 0;
        for (int[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    public static long[] addAll(long[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        // 计算总长度
        int length = 0;
        for (long[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }

        final long[] result = new long[length];
        length = 0;
        for (long[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    public static double[] addAll(double[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        // 计算总长度
        int length = 0;
        for (double[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }

        final double[] result = new double[length];
        length = 0;
        for (double[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    public static float[] addAll(float[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        // 计算总长度
        int length = 0;
        for (float[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }

        final float[] result = new float[length];
        length = 0;
        for (float[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    public static char[] addAll(char[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        // 计算总长度
        int length = 0;
        for (char[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }

        final char[] result = new char[length];
        length = 0;
        for (char[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    public static boolean[] addAll(boolean[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        // 计算总长度
        int length = 0;
        for (boolean[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }

        final boolean[] result = new boolean[length];
        length = 0;
        for (boolean[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    public static short[] addAll(short[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        // 计算总长度
        int length = 0;
        for (short[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }

        final short[] result = new short[length];
        length = 0;
        for (short[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    // ---------------------------------------------------------------------- range

    /**
     * 生成一个从0开始的数字列表<br>
     *
     * @param excludedEnd 结束的数字（不包含）
     * @return 数字列表
     */
    public static int[] range(int excludedEnd) {
        return range(0, excludedEnd, 1);
    }

    /**
     * 生成一个数字列表<br>
     * 自动判定正序反序
     *
     * @param includedStart 开始的数字（包含）
     * @param excludedEnd   结束的数字（不包含）
     * @return 数字列表
     */
    public static int[] range(int includedStart, int excludedEnd) {
        return range(includedStart, excludedEnd, 1);
    }

    /**
     * 生成一个数字列表<br>
     * 自动判定正序反序
     *
     * @param includedStart 开始的数字（包含）
     * @param excludedEnd   结束的数字（不包含）
     * @param step          步进
     * @return 数字列表
     */
    public static int[] range(int includedStart, int excludedEnd, int step) {
        if (includedStart > excludedEnd) {
            int tmp = includedStart;
            includedStart = excludedEnd;
            excludedEnd = tmp;
        }

        if (step <= 0) {
            step = 1;
        }

        int deviation = excludedEnd - includedStart;
        int length = deviation / step;
        if (deviation % step != 0) {
            length += 1;
        }
        int[] range = new int[length];
        for (int i = 0; i < length; i++) {
            range[i] = includedStart;
            includedStart += step;
        }
        return range;
    }

    // ---------------------------------------------------------------------- split

    /**
     * 拆分byte数组为几个等份（最后一份按照剩余长度分配空间）
     *
     * @param array 数组
     * @param len   每个小节的长度
     * @return 拆分后的数组
     */
    public static byte[][] split(byte[] array, int len) {
        int amount = array.length / len;
        final int remainder = array.length % len;
        if (remainder != 0) {
            ++amount;
        }
        final byte[][] arrays = new byte[amount][];
        byte[] arr;
        for (int i = 0; i < amount; i++) {
            if (i == amount - 1 && remainder != 0) {
                // 有剩余，按照实际长度创建
                arr = new byte[remainder];
                System.arraycopy(array, i * len, arr, 0, remainder);
            } else {
                arr = new byte[len];
                System.arraycopy(array, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

    // ----------------------------------------------------------------------
    // indexOf、LastIndexOf、contains

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(long[] array, long value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(long[] array, long value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(long[] array, long value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(int[] array, int value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(int[] array, int value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(int[] array, int value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(short[] array, short value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(short[] array, short value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(short[] array, short value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(char[] array, char value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(char[] array, char value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(char[] array, char value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(byte[] array, byte value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(byte[] array, byte value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(byte[] array, byte value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(double[] array, double value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (ToolNumber.equals(value, array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(double[] array, double value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (ToolNumber.equals(value, array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(double[] array, double value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(float[] array, float value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (ToolNumber.equals(value, array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(float[] array, float value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (ToolNumber.equals(value, array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(float[] array, float value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(boolean[] array, boolean value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(boolean[] array, boolean value) {
        if (null != array) {
            for (int i = array.length - 1; i >= 0; i--) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(boolean[] array, boolean value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    // ------------------------------------------------------------------- Wrap and unwrap

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Integer[] wrap(int... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Integer[0];
        }

        final Integer[] array = new Integer[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i];
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组，null转为0
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static int[] unWrap(Integer... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new int[0];
        }

        final int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = Safe.value(values[i], 0);
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Long[] wrap(long... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Long[0];
        }

        final Long[] array = new Long[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i];
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static long[] unWrap(Long... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new long[0];
        }

        final long[] array = new long[length];
        for (int i = 0; i < length; i++) {
            array[i] = Safe.value(values[i], 0L);
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Character[] wrap(char... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Character[0];
        }

        final Character[] array = new Character[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i];
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static char[] unWrap(Character... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new char[0];
        }

        char[] array = new char[length];
        for (int i = 0; i < length; i++) {
            array[i] = Safe.value(values[i], Character.MIN_VALUE);
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Byte[] wrap(byte... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Byte[0];
        }

        final Byte[] array = new Byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i];
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static byte[] unWrap(Byte... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new byte[0];
        }

        final byte[] array = new byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = Safe.value(values[i], (byte) 0);
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Short[] wrap(short... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Short[0];
        }

        final Short[] array = new Short[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i];
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static short[] unWrap(Short... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new short[0];
        }

        final short[] array = new short[length];
        for (int i = 0; i < length; i++) {
            array[i] = Safe.value(values[i], (short) 0);
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Float[] wrap(float... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Float[0];
        }

        final Float[] array = new Float[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i];
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static float[] unWrap(Float... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new float[0];
        }

        final float[] array = new float[length];
        for (int i = 0; i < length; i++) {
            array[i] = Safe.value(values[i], 0F);
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Double[] wrap(double... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Double[0];
        }

        final Double[] array = new Double[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i];
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static double[] unWrap(Double... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new double[0];
        }

        final double[] array = new double[length];
        for (int i = 0; i < length; i++) {
            array[i] = Safe.value(values[i], 0D);
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Boolean[] wrap(boolean... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new Boolean[0];
        }

        final Boolean[] array = new Boolean[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i];
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static boolean[] unWrap(Boolean... values) {
        if (null == values) {
            return null;
        }
        final int length = values.length;
        if (0 == length) {
            return new boolean[0];
        }

        final boolean[] array = new boolean[length];
        for (int i = 0; i < length; i++) {
            array[i] = Safe.value(values[i], false);
        }
        return array;
    }

    // ------------------------------------------------------------------- sub

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static byte[] sub(byte[] array, int start, int end) {
        int length = Array.getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new byte[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new byte[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static int[] sub(int[] array, int start, int end) {
        int length = Array.getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new int[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new int[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static long[] sub(long[] array, int start, int end) {
        int length = Array.getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new long[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new long[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static short[] sub(short[] array, int start, int end) {
        int length = Array.getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new short[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new short[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static char[] sub(char[] array, int start, int end) {
        int length = Array.getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new char[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new char[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static double[] sub(double[] array, int start, int end) {
        int length = Array.getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new double[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new double[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static float[] sub(float[] array, int start, int end) {
        int length = Array.getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new float[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new float[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static boolean[] sub(boolean[] array, int start, int end) {
        int length = Array.getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new boolean[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new boolean[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    // ------------------------------------------------------------------- remove

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static long[] remove(long[] array, int index) throws IllegalArgumentException {
        return (long[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static int[] remove(int[] array, int index) throws IllegalArgumentException {
        return (int[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static short[] remove(short[] array, int index) throws IllegalArgumentException {
        return (short[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static char[] remove(char[] array, int index) throws IllegalArgumentException {
        return (char[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static byte[] remove(byte[] array, int index) throws IllegalArgumentException {
        return (byte[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static double[] remove(double[] array, int index) throws IllegalArgumentException {
        return (double[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static float[] remove(float[] array, int index) throws IllegalArgumentException {
        return (float[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static boolean[] remove(boolean[] array, int index) throws IllegalArgumentException {
        return (boolean[]) remove((Object) array, index);
    }

    /**
     * 移除数组中对应位置的元素<br>
     * copy from commons-lang
     *
     * @param array 数组对象，可以是对象数组，也可以原始类型数组
     * @param index 位置，如果位置小于0或者大于长度，返回原数组
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public static Object remove(Object array, int index) throws IllegalArgumentException {
        if (null == array) {
            return null;
        }
        int length = Array.getLength(array);
        if (index < 0 || index >= length) {
            return array;
        }

        final Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            // 后半部分
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }

        return result;
    }

    // ---------------------------------------------------------------------- removeEle

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static long[] removeEle(long[] array, long element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static int[] removeEle(int[] array, int element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static short[] removeEle(short[] array, short element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static char[] removeEle(char[] array, char element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static byte[] removeEle(byte[] array, byte element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static double[] removeEle(double[] array, double element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static float[] removeEle(float[] array, float element) throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    /**
     * 移除数组中指定的元素<br>
     * 只会移除匹配到的第一个元素 copy from commons-lang
     *
     * @param array   数组对象，可以是对象数组，也可以原始类型数组
     * @param element 要移除的元素
     * @return 去掉指定元素后的新数组或原数组
     * @throws IllegalArgumentException 参数对象不为数组对象
     */
    public static boolean[] removeEle(boolean[] array, boolean element)
            throws IllegalArgumentException {
        return remove(array, indexOf(array, element));
    }

    // ---------------------------------------------------------------------- reverse

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     */
    public static long[] reverse(
            long[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (Whether.empty(array)) {
            return array;
        }
        int i = Math.max(startIndexInclusive, 0);
        int j = Math.min(array.length, endIndexExclusive) - 1;
        while (j > i) {
            swap(array, i, j);
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     */
    public static long[] reverse(long[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     */
    public static int[] reverse(
            int[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (Whether.empty(array)) {
            return array;
        }
        int i = Math.max(startIndexInclusive, 0);
        int j = Math.min(array.length, endIndexExclusive) - 1;
        while (j > i) {
            swap(array, i, j);
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     */
    public static int[] reverse(int[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     */
    public static short[] reverse(
            short[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (Whether.empty(array)) {
            return array;
        }
        int i = Math.max(startIndexInclusive, 0);
        int j = Math.min(array.length, endIndexExclusive) - 1;
        while (j > i) {
            swap(array, i, j);
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     */
    public static short[] reverse(short[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     */
    public static char[] reverse(
            char[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (Whether.empty(array)) {
            return array;
        }
        int i = Math.max(startIndexInclusive, 0);
        int j = Math.min(array.length, endIndexExclusive) - 1;
        while (j > i) {
            swap(array, i, j);
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     */
    public static char[] reverse(char[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     */
    public static byte[] reverse(
            byte[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (Whether.empty(array)) {
            return array;
        }
        int i = Math.max(startIndexInclusive, 0);
        int j = Math.min(array.length, endIndexExclusive) - 1;
        while (j > i) {
            swap(array, i, j);
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     */
    public static byte[] reverse(byte[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     */
    public static double[] reverse(
            double[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (Whether.empty(array)) {
            return array;
        }
        int i = Math.max(startIndexInclusive, 0);
        int j = Math.min(array.length, endIndexExclusive) - 1;
        while (j > i) {
            swap(array, i, j);
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     */
    public static double[] reverse(double[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     */
    public static float[] reverse(
            float[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (Whether.empty(array)) {
            return array;
        }
        int i = Math.max(startIndexInclusive, 0);
        int j = Math.min(array.length, endIndexExclusive) - 1;
        while (j > i) {
            swap(array, i, j);
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     */
    public static float[] reverse(float[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 其实位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     */
    public static boolean[] reverse(
            boolean[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (Whether.empty(array)) {
            return array;
        }
        int i = Math.max(startIndexInclusive, 0);
        int j = Math.min(array.length, endIndexExclusive) - 1;
        while (j > i) {
            swap(array, i, j);
            j--;
            i++;
        }
        return array;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     */
    public static boolean[] reverse(boolean[] array) {
        return reverse(array, 0, array.length);
    }

    // ------------------------------------------------------------------------------------------------------------ min and max

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static long min(long... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        long min = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static int min(int... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        int min = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static short min(short... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        short min = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static char min(char... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        char min = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static byte min(byte... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        byte min = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static double min(double... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        double min = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static float min(float... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        float min = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static long max(long... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        long max = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static int max(int... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        int max = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static short max(short... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        short max = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static char max(char... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        char max = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static byte max(byte... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        byte max = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static double max(double... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        double max = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static float max(float... numberArray) {
        if (Whether.empty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        float max = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    // ---------------------------------------------------------------------- shuffle

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array 数组，会变更
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static int[] shuffle(int[] array) {
        return shuffle(array, ToolRandom.getRandom());
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array  数组，会变更
     * @param random 随机数生成器
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static int[] shuffle(int[] array, Random random) {
        if (array == null || random == null || array.length <= 1) {
            return array;
        }

        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }

        return array;
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array 数组，会变更
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static long[] shuffle(long[] array) {
        return shuffle(array, ToolRandom.getRandom());
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array  数组，会变更
     * @param random 随机数生成器
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static long[] shuffle(long[] array, Random random) {
        if (array == null || random == null || array.length <= 1) {
            return array;
        }

        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }

        return array;
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array 数组，会变更
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static double[] shuffle(double[] array) {
        return shuffle(array, ToolRandom.getRandom());
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array  数组，会变更
     * @param random 随机数生成器
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static double[] shuffle(double[] array, Random random) {
        if (array == null || random == null || array.length <= 1) {
            return array;
        }

        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }

        return array;
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array 数组，会变更
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static float[] shuffle(float[] array) {
        return shuffle(array, ToolRandom.getRandom());
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array  数组，会变更
     * @param random 随机数生成器
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static float[] shuffle(float[] array, Random random) {
        if (array == null || random == null || array.length <= 1) {
            return array;
        }

        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }

        return array;
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array 数组，会变更
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static boolean[] shuffle(boolean[] array) {
        return shuffle(array, ToolRandom.getRandom());
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array  数组，会变更
     * @param random 随机数生成器
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static boolean[] shuffle(boolean[] array, Random random) {
        if (array == null || random == null || array.length <= 1) {
            return array;
        }

        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }

        return array;
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array 数组，会变更
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static byte[] shuffle(byte[] array) {
        return shuffle(array, ToolRandom.getRandom());
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array  数组，会变更
     * @param random 随机数生成器
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static byte[] shuffle(byte[] array, Random random) {
        if (array == null || random == null || array.length <= 1) {
            return array;
        }

        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }

        return array;
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array 数组，会变更
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static char[] shuffle(char[] array) {
        return shuffle(array, ToolRandom.getRandom());
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array  数组，会变更
     * @param random 随机数生成器
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static char[] shuffle(char[] array, Random random) {
        if (array == null || random == null || array.length <= 1) {
            return array;
        }

        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }

        return array;
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array 数组，会变更
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static short[] shuffle(short[] array) {
        return shuffle(array, ToolRandom.getRandom());
    }

    /**
     * 打乱数组顺序，会变更原数组
     *
     * @param array  数组，会变更
     * @param random 随机数生成器
     * @return 打乱后的数组
     * @author potatoxf
     */
    public static short[] shuffle(short[] array, Random random) {
        if (array == null || random == null || array.length <= 1) {
            return array;
        }

        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }

        return array;
    }

    // ---------------------------------------------------------------------- shuffle

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     */
    public static int[] swap(int[] array, int index1, int index2) {
        if (Whether.empty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        int tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     */
    public static long[] swap(long[] array, int index1, int index2) {
        if (Whether.empty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        long tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     */
    public static double[] swap(double[] array, int index1, int index2) {
        if (Whether.empty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        double tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     */
    public static float[] swap(float[] array, int index1, int index2) {
        if (Whether.empty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        float tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     */
    public static boolean[] swap(boolean[] array, int index1, int index2) {
        if (Whether.empty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        boolean tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     */
    public static byte[] swap(byte[] array, int index1, int index2) {
        if (Whether.empty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        byte tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     */
    public static char[] swap(char[] array, int index1, int index2) {
        if (Whether.empty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        char tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     */
    public static short[] swap(short[] array, int index1, int index2) {
        if (Whether.empty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        short tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSorted(byte[] array) {
        return isSortedASC(array);
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSortedASC(byte[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否降序，即array[i] &gt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否降序
     * @author potatoxf
     */
    public static boolean isSortedDESC(byte[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] < array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSorted(short[] array) {
        return isSortedASC(array);
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSortedASC(short[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否降序，即array[i] &gt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否降序
     * @author potatoxf
     */
    public static boolean isSortedDESC(short[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] < array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSorted(char[] array) {
        return isSortedASC(array);
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSortedASC(char[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否降序，即array[i] &gt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否降序
     * @author potatoxf
     */
    public static boolean isSortedDESC(char[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] < array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSorted(int[] array) {
        return isSortedASC(array);
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSortedASC(int[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否降序，即array[i] &gt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否降序
     * @author potatoxf
     */
    public static boolean isSortedDESC(int[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] < array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSorted(long[] array) {
        return isSortedASC(array);
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSortedASC(long[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否降序，即array[i] &gt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否降序
     * @author potatoxf
     */
    public static boolean isSortedDESC(long[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] < array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSorted(double[] array) {
        return isSortedASC(array);
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSortedASC(double[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否降序，即array[i] &gt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否降序
     * @author potatoxf
     */
    public static boolean isSortedDESC(double[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] < array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSorted(float[] array) {
        return isSortedASC(array);
    }

    /**
     * 检查数组是否升序，即array[i] &lt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否升序
     * @author potatoxf
     */
    public static boolean isSortedASC(float[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查数组是否降序，即array[i] &gt;= array[i+1]，若传入空数组，则返回false
     *
     * @param array 数组
     * @return 数组是否降序
     * @author potatoxf
     */
    public static boolean isSortedDESC(float[] array) {
        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] < array[i + 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 求和
     *
     * @param arr 数组
     */
    public static long sum(byte... arr) {
        long result = 0L;
        for (byte e : arr) {
            result += e;
        }
        return result;
    }

    /**
     * 求和
     *
     * @param arr 数组
     */
    public static long sum(short... arr) {
        long result = 0L;
        for (short e : arr) {
            result += e;
        }
        return result;
    }

    /**
     * 求和
     *
     * @param arr 数组
     */
    public static long sum(int... arr) {
        long result = 0L;
        for (int e : arr) {
            result += e;
        }
        return result;
    }

    /**
     * 求和
     *
     * @param arr 数组
     */
    public static long sum(long... arr) {
        long result = 0L;
        for (long e : arr) {
            result += e;
        }
        return result;
    }

    /**
     * 求和
     *
     * @param arr 数组
     */
    public static float sum(float... arr) {
        float result = 0f;
        for (float e : arr) {
            result += e;
        }
        return result;
    }

    /**
     * 求和
     *
     * @param arr 数组
     */
    public static double sum(double... arr) {
        double result = 0d;
        for (double e : arr) {
            result += e;
        }
        return result;
    }

    /**
     * 精确求和
     *
     * @param arr 数组
     */
    public static BigInteger sumExact(byte... arr) {
        BigInteger result = BigInteger.ZERO;
        long r = 0, t = 0;
        for (byte e : arr) {
            r = t + e;
            if (((t ^ r) & (e ^ r)) < 0) {
                result = result.add(BigInteger.valueOf(t)).add(BigInteger.valueOf(e));
                t = 0;
                r = 0;
            } else {
                t = r;
            }
        }
        if (r != 0) {
            result = result.add(BigInteger.valueOf(r));
        }
        return result;
    }

    /**
     * 精确求和
     *
     * @param arr 数组
     */
    public static BigInteger sumExact(short... arr) {
        BigInteger result = BigInteger.ZERO;
        long r = 0, t = 0;
        for (short e : arr) {
            r = t + e;
            if (((t ^ r) & (e ^ r)) < 0) {
                result = result.add(BigInteger.valueOf(t)).add(BigInteger.valueOf(e));
                t = 0;
                r = 0;
            } else {
                t = r;
            }
        }
        if (r != 0) {
            result = result.add(BigInteger.valueOf(r));
        }
        return result;
    }

    /**
     * 精确求和
     *
     * @param arr 数组
     */
    public static BigInteger sumExact(int... arr) {
        BigInteger result = BigInteger.ZERO;
        long r = 0, t = 0;
        for (int e : arr) {
            r = t + e;
            if (((t ^ r) & (e ^ r)) < 0) {
                result = result.add(BigInteger.valueOf(t)).add(BigInteger.valueOf(e));
                t = 0;
                r = 0;
            } else {
                t = r;
            }
        }
        if (r != 0) {
            result = result.add(BigInteger.valueOf(r));
        }
        return result;
    }

    /**
     * 精确求和
     *
     * @param arr 数组
     */
    public static BigInteger sumExact(long... arr) {
        BigInteger result = BigInteger.ZERO;
        long r = 0, t = 0;
        for (long e : arr) {
            r = t + e;
            if (((t ^ r) & (e ^ r)) < 0) {
                result = result.add(BigInteger.valueOf(t)).add(BigInteger.valueOf(e));
                t = 0;
                r = 0;
            } else {
                t = r;
            }
        }
        if (r != 0) {
            result = result.add(BigInteger.valueOf(r));
        }
        return result;
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param inputs the array to append to (can be {@code null})
     * @param objs   the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static boolean[] addToArray(boolean[] inputs, boolean... objs) {
        if (inputs == null && objs == null) {
            return PoolOfArray.EMPTY_BOOLEAN_ARRAY;
        } else if (inputs == null) {
            return objs;
        } else if (objs == null) {
            return inputs;
        }
        int length = inputs.length + objs.length;
        boolean[] outputs = new boolean[length];
        if (inputs.length != 0) {
            System.arraycopy(inputs, 0, outputs, 0, inputs.length);
        }
        if (objs.length != 0) {
            System.arraycopy(objs, 0, outputs, inputs.length, objs.length);
        }
        return outputs;
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param inputs the array to append to (can be {@code null})
     * @param objs   the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static char[] addToArray(char[] inputs, char... objs) {
        if (inputs == null && objs == null) {
            return PoolOfArray.EMPTY_CHAR_ARRAY;
        } else if (inputs == null) {
            return objs;
        } else if (objs == null) {
            return inputs;
        }
        int length = inputs.length + objs.length;
        char[] outputs = new char[length];
        if (inputs.length != 0) {
            System.arraycopy(inputs, 0, outputs, 0, inputs.length);
        }
        if (objs.length != 0) {
            System.arraycopy(objs, 0, outputs, inputs.length, objs.length);
        }
        return outputs;
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param inputs the array to append to (can be {@code null})
     * @param objs   the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static byte[] addToArray(byte[] inputs, byte... objs) {
        if (inputs == null && objs == null) {
            return PoolOfArray.EMPTY_BYTE_ARRAY;
        } else if (inputs == null) {
            return objs;
        } else if (objs == null) {
            return inputs;
        }
        int length = inputs.length + objs.length;
        byte[] outputs = new byte[length];
        if (inputs.length != 0) {
            System.arraycopy(inputs, 0, outputs, 0, inputs.length);
        }
        if (objs.length != 0) {
            System.arraycopy(objs, 0, outputs, inputs.length, objs.length);
        }
        return outputs;
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param inputs the array to append to (can be {@code null})
     * @param objs   the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static short[] addToArray(short[] inputs, short... objs) {
        if (inputs == null && objs == null) {
            return PoolOfArray.EMPTY_SHORT_ARRAY;
        } else if (inputs == null) {
            return objs;
        } else if (objs == null) {
            return inputs;
        }
        int length = inputs.length + objs.length;
        short[] outputs = new short[length];
        if (inputs.length != 0) {
            System.arraycopy(inputs, 0, outputs, 0, inputs.length);
        }
        if (objs.length != 0) {
            System.arraycopy(objs, 0, outputs, inputs.length, objs.length);
        }
        return outputs;
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param inputs the array to append to (can be {@code null})
     * @param objs   the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static int[] addToArray(int[] inputs, int... objs) {
        if (inputs == null && objs == null) {
            return PoolOfArray.EMPTY_INT_ARRAY;
        } else if (inputs == null) {
            return objs;
        } else if (objs == null) {
            return inputs;
        }
        int length = inputs.length + objs.length;
        int[] outputs = new int[length];
        if (inputs.length != 0) {
            System.arraycopy(inputs, 0, outputs, 0, inputs.length);
        }
        if (objs.length != 0) {
            System.arraycopy(objs, 0, outputs, inputs.length, objs.length);
        }
        return outputs;
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param inputs the array to append to (can be {@code null})
     * @param objs   the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static long[] addToArray(long[] inputs, long... objs) {
        if (inputs == null && objs == null) {
            return PoolOfArray.EMPTY_LONG_ARRAY;
        } else if (inputs == null) {
            return objs;
        } else if (objs == null) {
            return inputs;
        }
        int length = inputs.length + objs.length;
        long[] outputs = new long[length];
        if (inputs.length != 0) {
            System.arraycopy(inputs, 0, outputs, 0, inputs.length);
        }
        if (objs.length != 0) {
            System.arraycopy(objs, 0, outputs, inputs.length, objs.length);
        }
        return outputs;
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param inputs the array to append to (can be {@code null})
     * @param objs   the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static float[] addToArray(float[] inputs, float... objs) {
        if (inputs == null && objs == null) {
            return PoolOfArray.EMPTY_FLOAT_ARRAY;
        } else if (inputs == null) {
            return objs;
        } else if (objs == null) {
            return inputs;
        }
        int length = inputs.length + objs.length;
        float[] outputs = new float[length];
        if (inputs.length != 0) {
            System.arraycopy(inputs, 0, outputs, 0, inputs.length);
        }
        if (objs.length != 0) {
            System.arraycopy(objs, 0, outputs, inputs.length, objs.length);
        }
        return outputs;
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param inputs the array to append to (can be {@code null})
     * @param objs   the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static double[] addToArray(double[] inputs, double... objs) {
        if (inputs == null && objs == null) {
            return PoolOfArray.EMPTY_DOUBLE_ARRAY;
        } else if (inputs == null) {
            return objs;
        } else if (objs == null) {
            return inputs;
        }
        int length = inputs.length + objs.length;
        double[] outputs = new double[length];
        if (inputs.length != 0) {
            System.arraycopy(inputs, 0, outputs, 0, inputs.length);
        }
        if (objs.length != 0) {
            System.arraycopy(objs, 0, outputs, inputs.length, objs.length);
        }
        return outputs;
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param inputs the array to append to (can be {@code null})
     * @param objs   the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static Class<?>[] addToArray(Class<?>[] inputs, Class<?>... objs) {
        return addToArray(Class.class, inputs, objs);
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param inputs the array to append to (can be {@code null})
     * @param objs   the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static String[] addToArray(String[] inputs, String... objs) {
        return addToArray(String.class, inputs, objs);
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param inputs the array to append to (can be {@code null})
     * @param objs   the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static <A, O extends A> A[] addToArray(Class<A> type, A[] inputs, O... objs) {
        if (objs == null || inputs == null) {
            return (A[]) Array.newInstance(type, 0);
        }
        if (objs.length == 0) {
            return inputs;
        }
        int length = inputs.length + objs.length;
        @SuppressWarnings("unchecked")
        A[] outputs = (A[]) Array.newInstance(type, length);
        if (inputs.length != 0) {
            System.arraycopy(inputs, 0, outputs, 0, inputs.length);
        }
        System.arraycopy(objs, 0, outputs, inputs.length, objs.length);
        return outputs;
    }

    /**
     * 合并数组
     *
     * @param inputs 数组
     * @return 返回合并后的数组
     */
    public static String[] mergeArray(String[]... inputs) {
        return mergeArray(String.class, inputs);
    }

    /**
     * 合并数组
     *
     * @param inputs 数组
     * @return 返回合并后的数组
     */
    public static Class<?>[] mergeArray(Class<?>[]... inputs) {
        return mergeArray(Class.class, inputs);
    }

    /**
     * 合并数组
     *
     * @param inputs 数组
     * @return 返回合并后的数组
     */
    public static <A> A[] mergeArray(Class<?> type, A[]... inputs) {
        if (inputs == null || inputs.length == 0) {
            return (A[]) Array.newInstance(type, 0);
        }
        int len = 0;
        for (Object[] input : inputs) {
            len += input.length;
        }
        if (len == 0) {
            return (A[]) Array.newInstance(type, 0);
        }
        A[] outputs = (A[]) Array.newInstance(type, len);
        int p = 0;
        for (A[] input : inputs) {
            int copyLen = input.length;
            System.arraycopy(input, 0, outputs, p, copyLen);
            p += copyLen;
        }
        return outputs;
    }

    /**
     * get the value, returns the absolute min value min
     *
     * @param numbers contains elements
     * @return the absolute min value
     */
    public static byte absMin(byte[] numbers) {
        byte absMinValue = numbers[0];
        for (int i = 1, length = numbers.length; i < length; ++i) {
            if (Math.abs(numbers[i]) < Math.abs(absMinValue)) {
                absMinValue = numbers[i];
            }
        }
        return absMinValue;
    }

    /**
     * get the value, returns the absolute min value min
     *
     * @param numbers contains elements
     * @return the absolute min value
     */
    public static short absMin(short[] numbers) {
        short absMinValue = numbers[0];
        for (int i = 1, length = numbers.length; i < length; ++i) {
            if (Math.abs(numbers[i]) < Math.abs(absMinValue)) {
                absMinValue = numbers[i];
            }
        }
        return absMinValue;
    }

    /**
     * get the value, returns the absolute min value min
     *
     * @param numbers contains elements
     * @return the absolute min value
     */
    public static int absMin(int[] numbers) {
        int absMinValue = numbers[0];
        for (int i = 1, length = numbers.length; i < length; ++i) {
            if (Math.abs(numbers[i]) < Math.abs(absMinValue)) {
                absMinValue = numbers[i];
            }
        }
        return absMinValue;
    }

    /**
     * get the value, returns the absolute min value min
     *
     * @param numbers contains elements
     * @return the absolute min value
     */
    public static long absMin(long[] numbers) {
        long absMinValue = numbers[0];
        for (int i = 1, length = numbers.length; i < length; ++i) {
            if (Math.abs(numbers[i]) < Math.abs(absMinValue)) {
                absMinValue = numbers[i];
            }
        }
        return absMinValue;
    }

    /**
     * get the value, returns the absolute min value min
     *
     * @param numbers contains elements
     * @return the absolute min value
     */
    public static float absMin(float[] numbers) {
        float absMinValue = numbers[0];
        for (int i = 1, length = numbers.length; i < length; ++i) {
            if (Math.abs(numbers[i]) < Math.abs(absMinValue)) {
                absMinValue = numbers[i];
            }
        }
        return absMinValue;
    }

    /**
     * get the value, returns the absolute min value min
     *
     * @param numbers contains elements
     * @return the absolute min value
     */
    public static double absMin(double[] numbers) {
        double absMinValue = numbers[0];
        for (int i = 1, length = numbers.length; i < length; ++i) {
            if (Math.abs(numbers[i]) < Math.abs(absMinValue)) {
                absMinValue = numbers[i];
            }
        }
        return absMinValue;
    }

    /**
     * get the value, return the absolute max value
     *
     * @param numbers contains elements
     * @return the absolute max value
     */
    public static byte absMax(byte[] numbers) {
        byte absMaxValue = numbers[0];
        for (int i = 1, length = numbers.length; i < length; ++i) {
            if (Math.abs(numbers[i]) > Math.abs(absMaxValue)) {
                absMaxValue = numbers[i];
            }
        }
        return absMaxValue;
    }

    /**
     * get the value, return the absolute max value
     *
     * @param numbers contains elements
     * @return the absolute max value
     */
    public static short absMax(short[] numbers) {
        short absMaxValue = numbers[0];
        for (int i = 1, length = numbers.length; i < length; ++i) {
            if (Math.abs(numbers[i]) > Math.abs(absMaxValue)) {
                absMaxValue = numbers[i];
            }
        }
        return absMaxValue;
    }

    /**
     * get the value, return the absolute max value
     *
     * @param numbers contains elements
     * @return the absolute max value
     */
    public static int absMax(int[] numbers) {
        int absMaxValue = numbers[0];
        for (int i = 1, length = numbers.length; i < length; ++i) {
            if (Math.abs(numbers[i]) > Math.abs(absMaxValue)) {
                absMaxValue = numbers[i];
            }
        }
        return absMaxValue;
    }

    /**
     * get the value, return the absolute max value
     *
     * @param numbers contains elements
     * @return the absolute max value
     */
    public static long absMax(long[] numbers) {
        long absMaxValue = numbers[0];
        for (int i = 1, length = numbers.length; i < length; ++i) {
            if (Math.abs(numbers[i]) > Math.abs(absMaxValue)) {
                absMaxValue = numbers[i];
            }
        }
        return absMaxValue;
    }

    /**
     * get the value, return the absolute max value
     *
     * @param numbers contains elements
     * @return the absolute max value
     */
    public static float absMax(float[] numbers) {
        float absMaxValue = numbers[0];
        for (int i = 1, length = numbers.length; i < length; ++i) {
            if (Math.abs(numbers[i]) > Math.abs(absMaxValue)) {
                absMaxValue = numbers[i];
            }
        }
        return absMaxValue;
    }

    /**
     * get the value, return the absolute max value
     *
     * @param numbers contains elements
     * @return the absolute max value
     */
    public static double absMax(double[] numbers) {
        double absMaxValue = numbers[0];
        for (int i = 1, length = numbers.length; i < length; ++i) {
            if (Math.abs(numbers[i]) > Math.abs(absMaxValue)) {
                absMaxValue = numbers[i];
            }
        }
        return absMaxValue;
    }


    /**
     * Convert the given array (which may be a primitive array) to an object array (if necessary of
     * primitive wrapper objects).
     *
     * <p>A {@code null} source value will be converted to an empty Object array.
     *
     * @param source the (potentially primitive) array
     * @return the corresponding object array (never {@code null})
     * @throws IllegalArgumentException if the parameter is not an array
     */
    public static Object[] toObjectArray(Object source) {
        if (source instanceof Object[]) {
            return (Object[]) source;
        }
        if (source == null) {
            return new Object[0];
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        }
        int length = Array.getLength(source);
        if (length == 0) {
            return new Object[0];
        }
        Class<?> wrapperType = Array.get(source, 0).getClass();
        Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; i++) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param inputs the array to append to (can be {@code null})
     * @param obj    the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static <A, O extends A> A[] mergeToArray(A[] inputs, O obj) {
        Class<?> compType = Object.class;
        if (inputs != null) {
            compType = inputs.getClass().getComponentType();
        } else if (obj != null) {
            compType = obj.getClass();
        }
        int newArrLength = (inputs != null ? inputs.length + 1 : 1);
        @SuppressWarnings("unchecked")
        A[] newArr = (A[]) Array.newInstance(compType, newArrLength);
        if (inputs != null) {
            System.arraycopy(inputs, 0, newArr, 0, inputs.length);
        }
        newArr[newArr.length - 1] = obj;
        return newArr;
    }

    /**
     * 合并数组
     *
     * @param inputs 数组
     * @return 返回合并后的数组
     */
    public static boolean[] mergeArray(boolean[]... inputs) {
        if (inputs == null || inputs.length == 0) {
            return new boolean[0];
        }
        int len = 0;
        for (boolean[] input : inputs) {
            len += input.length;
        }
        boolean[] outputs = new boolean[len];
        int p = 0;
        for (boolean[] input : inputs) {
            int copyLen = input.length;
            System.arraycopy(input, 0, outputs, p, copyLen);
            p += copyLen;
        }
        return outputs;
    }

    /**
     * 合并数组
     *
     * @param inputs 数组
     * @return 返回合并后的数组
     */
    public static byte[] mergeArray(byte[]... inputs) {
        if (inputs == null || inputs.length == 0) {
            return new byte[0];
        }
        int len = 0;
        for (byte[] input : inputs) {
            len += input.length;
        }
        byte[] outputs = new byte[len];
        int p = 0;
        for (byte[] input : inputs) {
            int copyLen = input.length;
            System.arraycopy(input, 0, outputs, p, copyLen);
            p += copyLen;
        }
        return outputs;
    }

    /**
     * 合并数组
     *
     * @param inputs 数组
     * @return 返回合并后的数组
     */
    public static short[] mergeArray(short[]... inputs) {
        if (inputs == null || inputs.length == 0) {
            return new short[0];
        }
        int len = 0;
        for (short[] input : inputs) {
            len += input.length;
        }
        short[] outputs = new short[len];
        int p = 0;
        for (short[] input : inputs) {
            int copyLen = input.length;
            System.arraycopy(input, 0, outputs, p, copyLen);
            p += copyLen;
        }
        return outputs;
    }

    /**
     * 合并数组
     *
     * @param inputs 数组
     * @return 返回合并后的数组
     */
    public static int[] mergeArray(int[]... inputs) {
        if (inputs == null || inputs.length == 0) {
            return new int[0];
        }
        int len = 0;
        for (int[] input : inputs) {
            len += input.length;
        }
        int[] outputs = new int[len];
        int p = 0;
        for (int[] input : inputs) {
            int copyLen = input.length;
            System.arraycopy(input, 0, outputs, p, copyLen);
            p += copyLen;
        }
        return outputs;
    }

    /**
     * 合并数组
     *
     * @param inputs 数组
     * @return 返回合并后的数组
     */
    public static long[] mergeArray(long[]... inputs) {
        if (inputs == null || inputs.length == 0) {
            return new long[0];
        }
        int len = 0;
        for (long[] input : inputs) {
            len += input.length;
        }
        long[] outputs = new long[len];
        int p = 0;
        for (long[] input : inputs) {
            int copyLen = input.length;
            System.arraycopy(input, 0, outputs, p, copyLen);
            p += copyLen;
        }
        return outputs;
    }

    /**
     * 合并数组
     *
     * @param inputs 数组
     * @return 返回合并后的数组
     */
    public static float[] mergeArray(float[]... inputs) {
        if (inputs == null || inputs.length == 0) {
            return new float[0];
        }
        int len = 0;
        for (float[] input : inputs) {
            len += input.length;
        }
        float[] outputs = new float[len];
        int p = 0;
        for (float[] input : inputs) {
            int copyLen = input.length;
            System.arraycopy(input, 0, outputs, p, copyLen);
            p += copyLen;
        }
        return outputs;
    }

    /**
     * 合并数组
     *
     * @param inputs 数组
     * @return 返回合并后的数组
     */
    public static double[] mergeArray(double[]... inputs) {
        if (inputs == null || inputs.length == 0) {
            return new double[0];
        }
        int len = 0;
        for (double[] input : inputs) {
            len += input.length;
        }
        double[] outputs = new double[len];
        int p = 0;
        for (double[] input : inputs) {
            int copyLen = input.length;
            System.arraycopy(input, 0, outputs, p, copyLen);
            p += copyLen;
        }
        return outputs;
    }
}

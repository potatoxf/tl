package pxf.tl.util;


import pxf.tl.api.PoolOfString;
import pxf.tl.comparator.ToolCompare;
import pxf.tl.exception.UtilException;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.io.FastByteArrayOutputStream;
import pxf.tl.iter.AnyIter;
import pxf.tl.lang.TextBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;

/**
 * 序列化工具类<br>
 * 注意！此工具类依赖于JDK的序列化机制，某些版本的JDK中可能存在远程注入漏洞。
 *
 * @author potatoxf
 */
public final class ToolObject {

    /**
     * 序列化后拷贝流的方式克隆<br>
     * 对象必须实现Serializable接口
     *
     * @param <T> 对象类型
     * @param obj 被克隆对象
     * @return 克隆后的对象
     * @throws UtilException IO异常和ClassNotFoundException封装
     */
    public static <T> T clone(T obj) {
        if (!(obj instanceof Serializable)) {
            return null;
        }
        return deserialize(serialize(obj));
    }

    /**
     * 序列化<br>
     * 对象必须实现Serializable接口
     *
     * @param <T> 对象类型
     * @param obj 要被序列化的对象
     * @return 序列化后的字节码
     */
    public static <T> byte[] serialize(T obj) {
        if (!(obj instanceof Serializable)) {
            return null;
        }
        final FastByteArrayOutputStream byteOut = new FastByteArrayOutputStream();
        ToolIO.writeObjects(byteOut, false, (Serializable) obj);
        return byteOut.toByteArray();
    }

    /**
     * 反序列化<br>
     * 对象必须实现Serializable接口
     *
     * <p>注意！！！ 此方法不会检查反序列化安全，可能存在反序列化漏洞风险！！！
     *
     * @param <T>   对象类型
     * @param bytes 反序列化的字节码
     * @return 反序列化后的对象
     */
    public static <T> T deserialize(byte[] bytes) {
        return ToolIO.readObj(new ByteArrayInputStream(bytes));
    }

    /**
     * 比较两个对象是否相等，此方法是 {@link #equal(Object, Object)}的别名方法。<br>
     * 相同的条件有两个，满足其一即可：<br>
     *
     * <ol>
     *   <li>obj1 == null &amp;&amp; obj2 == null
     *   <li>obj1.equals(obj2)
     *   <li>如果是BigDecimal比较，0 == obj1.compareTo(obj2)
     * </ol>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     * @see #equal(Object, Object)
     */
    public static boolean equals(Object obj1, Object obj2) {
        return equal(obj1, obj2);
    }

    /**
     * 比较两个对象是否相等。<br>
     * 相同的条件有两个，满足其一即可：<br>
     *
     * <ol>
     *   <li>obj1 == null &amp;&amp; obj2 == null
     *   <li>obj1.equals(obj2)
     *   <li>如果是BigDecimal比较，0 == obj1.compareTo(obj2)
     * </ol>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     * @see Objects#equals(Object, Object)
     */
    public static boolean equal(Object obj1, Object obj2) {
        if (obj1 instanceof BigDecimal && obj2 instanceof BigDecimal) {
            return ToolNumber.equals((BigDecimal) obj1, (BigDecimal) obj2);
        }
        return Objects.equals(obj1, obj2);
    }

    /**
     * 比较两个对象是否不相等。<br>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否不等
     */
    public static boolean notEqual(Object obj1, Object obj2) {
        return !equal(obj1, obj2);
    }

    /**
     * 对象中是否包含元素<br>
     * 支持的对象类型包括：
     *
     * <ul>
     *   <li>String
     *   <li>Collection
     *   <li>Map
     *   <li>Iterator
     *   <li>Enumeration
     *   <li>Array
     * </ul>
     *
     * @param obj     对象
     * @param element 元素
     * @return 是否包含
     */
    public static boolean contains(Object obj, Object element) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            if (element == null) {
                return false;
            }
            return ((String) obj).contains(element.toString());
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).contains(element);
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).containsValue(element);
        }

        if (obj instanceof Iterator) {
            final Iterator<?> iter = (Iterator<?>) obj;
            while (iter.hasNext()) {
                final Object o = iter.next();
                if (equal(o, element)) {
                    return true;
                }
            }
            return false;
        }
        if (obj instanceof Enumeration) {
            final Enumeration<?> enumeration = (Enumeration<?>) obj;
            while (enumeration.hasMoreElements()) {
                final Object o = enumeration.nextElement();
                if (equal(o, element)) {
                    return true;
                }
            }
            return false;
        }
        if (obj.getClass().isArray()) {
            final int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                final Object o = Array.get(obj, i);
                if (equal(o, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否为基本类型，包括包装类型和非包装类型
     *
     * @param object 被检查对象，{@code null}返回{@code false}
     * @return 是否为基本类型
     * @see ToolBytecode#isBasicType(Class)
     */
    public static boolean isBasicType(Object object) {
        if (null == object) {
            return false;
        }
        return ToolBytecode.isBasicType(object.getClass());
    }

    /**
     * 检查是否为有效的数字<br>
     * 检查Double和Float是否为无限大，或者Not a Number<br>
     * 非数字类型和Null将返回true
     *
     * @param obj 被检查类型
     * @return 检查结果，非数字类型和Null将返回true
     */
    public static boolean isValidIfNumber(Object obj) {
        if (obj instanceof Number) {
            return ToolNumber.isValidNumber((Number) obj);
        }
        return true;
    }

    /**
     * {@code null}安全的对象比较，{@code null}对象排在末尾
     *
     * @param <T> 被比较对象类型
     * @param c1  对象1，可以为{@code null}
     * @param c2  对象2，可以为{@code null}
     * @return 比较结果，如果c1 &lt; c2，返回数小于0，c1==c2返回0，c1 &gt; c2 大于0
     * @see Comparator#compare(Object, Object)
     */
    public static <T extends Comparable<? super T>> int compare(T c1, T c2) {
        return ToolCompare.compare(c1, c2);
    }

    /**
     * 获得给定类的第一个泛型参数
     *
     * @param obj 被检查的对象
     * @return {@link Class}
     */
    public static Class<?> getTypeArgument(Object obj) {
        return getTypeArgument(obj, 0);
    }

    /**
     * 获得给定类的第一个泛型参数
     *
     * @param obj   被检查的对象
     * @param index 泛型类型的索引号，即第几个泛型类型
     * @return {@link Class}
     */
    public static Class<?> getTypeArgument(Object obj, int index) {
        return ToolBytecode.getTypeArgument(obj.getClass(), index);
    }

    /**
     * 对象比较
     *
     * @param main          对象1，可以为{@code null}
     * @param other         对象2，可以为{@code null}
     * @param isNullGreater 当被比较对象为null时是否排在前面
     * @return 比较结果，如果c1小于c2，返回数小于0，c1==c2返回0，c1大于c2 大于0，
     * @see java.util.Comparator#compare(Object, Object)
     */
    public static int compareObject(Object main, Object other, boolean isNullGreater) {
        if (main == null && other == null) {
            return 0;
        } else if (main == null) {
            return isNullGreater ? 1 : -1;
        } else if (other == null) {
            return isNullGreater ? -1 : 1;
        } else if (main == other || main.equals(other)) {
            return 0;
        } else {
            Integer result = compareCompared(main, other, isNullGreater);
            if (result != null) {
                return result;
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * 对象比较
     *
     * @param main          对象1，可以为{@code null}
     * @param other         对象2，可以为{@code null}
     * @param isNullGreater 当被比较对象为null时是否排在前面
     * @return 比较结果，如果c1小于c2，返回数小于0，c1==c2返回0，c1大于c2 大于0，
     * @see java.util.Comparator#compare(Object, Object)
     */
    public static <T extends Comparable<? super T>> int compare(T main, T other, boolean isNullGreater) {
        if (main == null && other == null) {
            return 0;
        } else if (main == null) {
            return isNullGreater ? 1 : -1;
        } else if (other == null) {
            return isNullGreater ? -1 : 1;
        } else if (main == other || main.equals(other)) {
            return 0;
        } else {
            return main.compareTo(other);
        }
    }

    /**
     * 对象比较
     *
     * @param main          对象1，可以为{@code null}
     * @param other         对象2，可以为{@code null}
     * @param isNullGreater 当被比较对象为null时是否排在前面
     * @return 比较结果，如果c1小于c2，返回数小于0，c1==c2返回0，c1大于c2 大于0，
     * @see java.util.Comparator#compare(Object, Object)
     */
    @SuppressWarnings("unchecked")
    public static Integer compareCompared(Object main, Object other, boolean isNullGreater) {
        if (main == null && other == null) {
            return 0;
        } else if (main == null) {
            return isNullGreater ? 1 : -1;
        } else if (other == null) {
            return isNullGreater ? -1 : 1;
        } else if (main == other || main.equals(other)) {
            return 0;
        } else {
            Class<?> kc = ToolBytecode.extractGenericClass(main, Comparable.class, 0);
            return (other.getClass() != kc ? null : ((Comparable<Object>) main).compareTo(other));
        }
    }

    /**
     * 比较相同下hashcode
     *
     * @param hashcode 哈希值
     * @param object   对象
     * @param target   目标
     * @return 返回比较的大小
     */
    public static int compareSameHashcode(int hashcode, Object object, Object target) {
        int ohc = object.hashCode();
        int thc = target.hashCode();
        ohc ^= Safe.hashcode(object);
        thc ^= Safe.hashcode(target);
        ohc ^= hashcode;
        thc ^= hashcode;
        if (ohc > thc) {
            return 1;
        } else if (ohc < thc) {
            return -1;
        } else {
            if (object == target || object.equals(target)) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    /**
     * 对象比较
     *
     * @param main          对象1，可以为{@code null}
     * @param other         对象2，可以为{@code null}
     * @param isNullGreater 当被比较对象为null时是否排在前面
     * @return 比较结果，如果c1小于c2，返回数小于0，c1==c2返回0，c1大于c2 大于0，
     * @see java.util.Comparator#compare(Object, Object)
     */
    public static <T extends Comparable<T>> int compareSame(T main, T other, boolean isNullGreater) {
        if (main == null && other == null) {
            return 0;
        } else if (main == null) {
            return isNullGreater ? 1 : -1;
        } else if (other == null) {
            return isNullGreater ? -1 : 1;
        } else if (main == other || main.equals(other)) {
            return 0;
        } else {
            return main.compareTo(other);
        }
    }

    /**
     * 深度克隆对象
     *
     * @param object 对象
     * @param <T>    对象类型
     * @return 返回新的克隆对象，如果返回 {@code null}则表示克隆失败
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepClone(T object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            ois.close();
            return (T) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    String.format(
                            "Deep cloning error, the cloned object must implement Serializable interface, current type [%s]",
                            object.getClass()),
                    e);
        } catch (IOException e) {
            throw new RuntimeException("Deep cloning error, IO Stream Exception", e);
        }
    }

    /**
     * Case insensitive alternative to {@link Enum#valueOf(Class, String)}.
     *
     * @param <E>        the concrete Enum type
     * @param enumValues the array of all Enum constants in question, usually per {@code
     *                   Enum.values()}
     * @param constant   the constant to get the enum value of
     * @throws IllegalArgumentException if the given constant is not found in the given array of enum
     *                                  values. Use {@link pxf.tl.help.Whether#containsConstant(Enum[], String)} as a guard to avoid this exception.
     */
    public static <E extends Enum<?>> E caseInsensitiveValueOf(E[] enumValues, String constant) {
        for (E candidate : enumValues) {
            if (candidate.toString().equalsIgnoreCase(constant)) {
                return candidate;
            }
        }
        throw new IllegalArgumentException(
                "LiteralConstant ["
                        + constant
                        + "] does not exist in enum type "
                        + enumValues.getClass().getComponentType().getName());
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array
     * contents plus the given object.
     *
     * @param array the array to append to (can be {@code null})
     * @param obj   the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static <A, O extends A> A[] addObjectToArray(A[] array, O obj) {
        Class<?> compType = Object.class;
        if (array != null) {
            compType = array.getClass().getComponentType();
        } else if (obj != null) {
            compType = obj.getClass();
        }
        int newArrLength = (array != null ? array.length + 1 : 1);
        @SuppressWarnings("unchecked")
        A[] newArr = (A[]) Array.newInstance(compType, newArrLength);
        if (array != null) {
            System.arraycopy(array, 0, newArr, 0, array.length);
        }
        newArr[newArr.length - 1] = obj;
        return newArr;
    }

    /**
     * Return a String representation of an object's overall identity.
     *
     * @param obj the object (may be {@code null})
     * @return the object's identity as String representation, or an empty String if the object was
     * {@code null}
     */
    public static String getIdentityString(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.getClass().getName() + "@" + getIdentityHexString(obj);
    }

    /**
     * Return a hex String form of an object's identity hash code.
     *
     * @param obj the object
     * @return the object's identity code in hex notation
     */
    public static String getIdentityHexString(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }


    public static String toString(Object any) {
        StringBuilder stringBuilder = new StringBuilder(100);
        toString(any, "{", "}", "", "", ", ", stringBuilder);
        return stringBuilder.toString();
    }

    private static void toString(Object object,
                                 String start,
                                 String end,
                                 String prefix,
                                 String suffix,
                                 String delimiter,
                                 StringBuilder stringBuilder) {
        if (object == null) {
            return;
        }
        Class<?> clz = object.getClass();
        if (clz.isArray()) {
            stringBuilder.append(start);
            for (Object obj : AnyIter.ofObject(true, object, null)) {
                if (obj.getClass().isArray()) {
                    toString(obj, start, end, prefix, suffix, delimiter, stringBuilder);
                } else {
                    stringBuilder
                            .append(prefix)
                            .append(obj)
                            .append(suffix)
                            .append(delimiter);
                }
            }
            if (!delimiter.isEmpty()) {
                stringBuilder.setLength(stringBuilder.length() - delimiter.length());
            }
            stringBuilder.append(end);
        } else {
            stringBuilder
                    .append(prefix)
                    .append(object)
                    .append(suffix);
        }
    }

    public static <T> String toString(
            @Nullable T[] value, @Nullable String delimiter, @Nullable T defaultValue) {
        if (Whether.empty(value)) {
            return PoolOfString.EMPTY;
        }
        delimiter = Safe.value(delimiter, PoolOfString.COMMA);
        int i = 0;
        StringBuilder stringBuilder = new StringBuilder();
        if (defaultValue == null) {
            while (Whether.empty(value[i])) {
                i++;
            }
            stringBuilder.append(value[i]);
            while (i < value.length) {
                if (Whether.noEmpty(value[i])) {
                    stringBuilder.append(delimiter).append(value[i]);
                }
                i++;
            }
        } else {
            stringBuilder.append(Safe.value(value[i], defaultValue));
            while (i < value.length) {
                stringBuilder.append(delimiter).append(Safe.value(value[i], defaultValue));
                i++;
            }
        }
        return stringBuilder.toString();
    }

    public static TextBuilder toFunctionString(
            @Nonnull TextBuilder container,
            @Nonnull String functionName,
            @Nonnull String argName,
            int argLength) {
        if (argLength <= 0) {
            return toFunctionString(container, functionName);
        } else {
            String[] args = new String[argLength];
            for (int i = 0; i < argLength; i++) {
                args[i] = argName + i;
            }
            return toFunctionString(container, functionName, args);
        }
    }

    public static TextBuilder toFunctionString(
            @Nonnull TextBuilder container,
            @Nonnull String functionName,
            @Nullable Class<?>[] argTypes,
            @Nullable String[] argNames) {
        if (Whether.empty(argTypes) && Whether.empty(argNames)) {
            return toFunctionString(container, functionName);
        } else {
            if (argTypes.length != argNames.length) {
                throw new IllegalArgumentException(
                        "The parameter type and the number of names do not match");
            }
            String[] args = new String[argTypes.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = argTypes[i].getSimpleName() + " " + argNames[i];
            }
            return toFunctionString(container, functionName, args);
        }
    }

    public static TextBuilder toFunctionString(
            @Nonnull TextBuilder container,
            @Nonnull String functionName,
            @Nullable String... args) {
        container.append(functionName);
        if (args != null && args.length > 0) {
            container.append("(").append(args[0]);
            for (int i = 1; i < args.length; i++) {
                container.append(", ").append(args[i]);
            }
            container.append(")");
        } else {
            container.append("()");
        }
        return container;
    }
}

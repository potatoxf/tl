package pxf.tl.help;

import pxf.tl.collection.map.MapUtil;
import pxf.tl.util.ToolObject;
import pxf.tl.util.ToolString;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 断言类
 *
 * @author potatoxf
 */
public final class Assert {

    private static final String TEMPLATE_VALUE_MUST_BE_BETWEEN_AND =
            "The value must be between {} and {}.";

    /**
     * Assert a boolean expression, throwing an {@code IllegalStateException} if the expression
     * evaluates to {@code false}.
     *
     * <p>Call {@link #beTrue} if you wish to throw an {@code IllegalArgumentException} on an
     * assertion failure.
     *
     * <pre class="code">Assert.state(id == null, "The id property must not already be initialized");
     * </pre>
     *
     * @param expression a boolean expression
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalStateException if {@code expression} is {@code false}
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Assert a boolean expression, throwing an {@code IllegalArgumentException} if the expression
     * evaluates to {@code false}.
     *
     * <pre class="code">Assert.isTrue(i &gt; 0, "The value must be greater than zero");</pre>
     *
     * @param expression a boolean expression
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalArgumentException if {@code expression} is {@code false}
     */
    public static void beTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an object is {@code null}.
     *
     * <pre class="code">Assert.nvl(value, "The value must be null");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void nvl(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an object is not {@code null}.
     *
     * <pre class="code">Assert.nonvl(clazz, "The class must not be null");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static void nonvl(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the given String is not empty; that is, it must not be {@code null} and not the
     * empty String.
     *
     * <pre class="code">Assert.hasLength(name, "Name must not be empty");</pre>
     *
     * @param text    the String to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the text is empty
     */
    public static void hasLength(String text, String message) {
        if (!Whether.noBlank(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the given String contains valid text content; that is, it must not be {@code null}
     * and must contain at least one non-whitespace character.
     *
     * <pre class="code">Assert.hasText(name, "'name' must not be empty");</pre>
     *
     * @param text    the String to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the text does not contain valid text content
     */
    public static void hasText(String text, String message) {
        if (Whether.blank(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the given text does not contain the given substring.
     *
     * <pre class="code">Assert.doesNotContain(name, "rod", "Name must not contain 'rod'");</pre>
     *
     * @param textToSearch the text to search
     * @param substring    the substring to find within the text
     * @param message      the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the text contains the substring
     */
    public static void doesNotContain(String textToSearch, String substring, String message) {
        if (Whether.noBlank(textToSearch) && Whether.noBlank(substring) && textToSearch.contains(substring)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an array contains elements; that is, it must not be {@code null} and must contain
     * at least one element.
     *
     * <pre class="code">Assert.noEmpty(array, "The array must contain elements");</pre>
     *
     * @param array   the array to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object array is {@code null} or contains no elements
     */
    public static void noEmpty(Object[] array, String message) {
        if (Whether.empty(array)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an array contains no {@code null} elements.
     *
     * <p>Note: Does not complain if the array is empty!
     *
     * <pre class="code">Assert.noNullElements(array, "The array must contain non-null elements");
     * </pre>
     *
     * @param array   the array to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object array contains a {@code null} element
     */
    public static void noNullElements(Object[] array, String message) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }

    /**
     * Assert that a collection contains elements; that is, it must not be {@code null} and must
     * contain at least one element.
     *
     * <pre class="code">Assert.noEmpty(collection, "Collection must contain elements");</pre>
     *
     * @param collection the collection to check
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the collection is {@code null} or contains no elements
     */
    public static void noEmpty(Collection<?> collection, String message) {
        if (Whether.empty(collection)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that a collection contains no {@code null} elements.
     *
     * <p>Note: Does not complain if the collection is empty!
     *
     * <pre class="code">
     * Assert.noNullElements(collection, "Collection must contain non-null elements");</pre>
     *
     * @param collection the collection to check
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the collection contains a {@code null} element
     */
    public static void noNullElements(Collection<?> collection, String message) {
        if (collection != null) {
            for (Object element : collection) {
                if (element == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }

    /**
     * Assert that a Map contains entries; that is, it must not be {@code null} and must contain at
     * least one entry.
     *
     * <pre class="code">Assert.noEmpty(proxyMap, "Map must contain entries");</pre>
     *
     * @param map     the proxyMap to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the proxyMap is {@code null} or contains no entries
     */
    public static void noEmpty(Map<?, ?> map, String message) {
        if (Whether.empty(map)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言值是否在集合里面
     *
     * @param value   值
     * @param array   数组
     * @param message 异常信息
     */
    public static void in(int value, int[] array, String message) {
        if (array != null && array.length != 0) {
            for (int t : array) {
                if (t == value) {
                    return;
                }
            }
        }
        throw new IllegalArgumentException(message);
    }

    /**
     * 断言值是否在集合里面
     *
     * @param value   值
     * @param array   数组
     * @param message 异常信息
     */
    public static void in(long value, long[] array, String message) {
        if (array != null && array.length != 0) {
            for (long t : array) {
                if (t == value) {
                    return;
                }
            }
        }
        throw new IllegalArgumentException(message);
    }

    /**
     * 断言值是否在集合里面
     *
     * @param value   值
     * @param array   数组
     * @param message 异常信息
     * @param <T>     类型
     */
    public static <T> void in(T value, T[] array, String message) {
        if (value != null && array != null && array.length != 0) {
            for (T t : array) {
                if (t == null) {
                    continue;
                }
                if (t.equals(value)) {
                    return;
                }
            }
        }
        throw new IllegalArgumentException(message);
    }

    /**
     * 断言值是否在集合里面
     *
     * @param value      值
     * @param collection 集合
     * @param message    异常信息
     * @param <T>        类型
     */
    public static <T> void in(T value, Collection<T> collection, String message) {
        if (value == null || collection == null || !collection.contains(value)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the provided object is an instance of the provided class.
     *
     * <pre class="code">Assert.instanceOf(Foo.class, foo, "Foo expected");</pre>
     *
     * @param type    the type to check against
     * @param obj     the object to check
     * @param message a message which will be prepended to provide further context. If it is empty or
     *                ends in ":" or ";" or "," or ".", a full exception message will be appended. If it ends in
     *                a space, the name of the offending object's type will be appended. In any other case, a ":"
     *                with a space and the name of the offending object's type will be appended.
     * @throws IllegalArgumentException if the object is not an instance of type
     */
    public static void instanceOf(Class<?> type, Object obj, String message) {
        nonvl(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            instanceCheckFailed(type, obj, message);
        }
    }

    /**
     * Assert that the provided object is an instance of the provided class.
     *
     * <pre class="code">Assert.instanceOf(Foo.class, foo);</pre>
     *
     * @param type the type to check against
     * @param obj  the object to check
     * @throws IllegalArgumentException if the object is not an instance of type
     */
    public static void instanceOf(Class<?> type, Object obj) {
        instanceOf(type, obj, "");
    }

    /**
     * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
     *
     * <pre class="code">Assert.isAssignable(Number.class, myClass, "Number expected");</pre>
     *
     * @param superType the super type to check against
     * @param subType   the sub type to check
     * @param message   a message which will be prepended to provide further context. If it is empty or
     *                  ends in ":" or ";" or "," or ".", a full exception message will be appended. If it ends in
     *                  a space, the name of the offending sub type will be appended. In any other case, a ":" with
     *                  a space and the name of the offending sub type will be appended.
     * @throws IllegalArgumentException if the classes are not assignable
     */
    public static void assignable(Class<?> superType, Class<?> subType, String message) {
        nonvl(superType, "Super type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            assignableCheckFailed(superType, subType, message);
        }
    }

    /**
     * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
     *
     * <pre class="code">Assert.isAssignable(Number.class, myClass);</pre>
     *
     * @param superType the super type to check
     * @param subType   the sub type to check
     * @throws IllegalArgumentException if the classes are not assignable
     */
    public static void assignable(Class<?> superType, Class<?> subType) {
        assignable(superType, subType, "");
    }

    private static void instanceCheckFailed(Class<?> type, Object obj, String msg) {
        String className = (obj != null ? obj.getClass().getName() : "null");
        String result = "";
        boolean defaultMessage = true;
        if (Whether.noBlank(msg)) {
            if (endsWithSeparator(msg)) {
                result = msg + " ";
            } else {
                result = messageWithTypeName(msg, className);
                defaultMessage = false;
            }
        }
        if (defaultMessage) {
            result = result + ("Object of class [" + className + "] must be an instance of " + type);
        }
        throw new IllegalArgumentException(result);
    }

    private static void assignableCheckFailed(Class<?> superType, Class<?> subType, String msg) {
        String result = "";
        boolean defaultMessage = true;
        if (Whether.noBlank(msg)) {
            if (endsWithSeparator(msg)) {
                result = msg + " ";
            } else {
                result = messageWithTypeName(msg, subType);
                defaultMessage = false;
            }
        }
        if (defaultMessage) {
            result = result + (subType + " is not assignable to " + superType);
        }
        throw new IllegalArgumentException(result);
    }

    private static boolean endsWithSeparator(String msg) {
        return (msg.endsWith(":") || msg.endsWith(";") || msg.endsWith(",") || msg.endsWith("."));
    }

    private static String messageWithTypeName(String msg, Object typeName) {
        return msg + (msg.endsWith(" ") ? "" : ": ") + typeName;
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出给定的异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, IllegalArgumentException::new);
     * </pre>
     *
     * @param <X>        异常类型
     * @param expression 布尔值
     * @param supplier   指定断言不通过时抛出的异常
     * @throws X if expression is {@code false}
     */
    public static <X extends Throwable> void isTrue(
            boolean expression, Supplier<? extends X> supplier) throws X {
        if (false == expression) {
            throw supplier.get();
        }
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, "The value must be greater than zero");
     * </pre>
     *
     * @param expression       布尔值
     * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
     * @param params           参数列表
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue(boolean expression, String errorMsgTemplate, Object... params)
            throws IllegalArgumentException {
        isTrue(
                expression,
                () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isTrue(i &gt; 0);
     * </pre>
     *
     * @param expression 布尔值
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isTrue(boolean expression) throws IllegalArgumentException {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出指定类型异常<br>
     * 并使用指定的函数获取错误信息返回
     *
     * <pre class="code">
     *  Assert.isFalse(i &gt; 0, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>           异常类型
     * @param expression    布尔值
     * @param errorSupplier 指定断言不通过时抛出的异常
     * @throws X if expression is {@code false}
     */
    public static <X extends Throwable> void isFalse(boolean expression, Supplier<X> errorSupplier)
            throws X {
        if (expression) {
            throw errorSupplier.get();
        }
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isFalse(i &lt; 0, "The value must not be negative");
     * </pre>
     *
     * @param expression       布尔值
     * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
     * @param params           参数列表
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isFalse(boolean expression, String errorMsgTemplate, Object... params)
            throws IllegalArgumentException {
        isFalse(
                expression,
                () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出 {@code IllegalArgumentException} 异常<br>
     *
     * <pre class="code">
     * Assert.isFalse(i &lt; 0);
     * </pre>
     *
     * @param expression 布尔值
     * @throws IllegalArgumentException if expression is {@code false}
     */
    public static void isFalse(boolean expression) throws IllegalArgumentException {
        isFalse(expression, "[Assertion failed] - this expression must be false");
    }

    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出指定类型异常 并使用指定的函数获取错误信息返回
     *
     * <pre class="code">
     * Assert.isNull(value, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>           异常类型
     * @param object        被检查的对象
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @throws X if the object is not {@code null}
     */
    public static <X extends Throwable> void isNull(Object object, Supplier<X> errorSupplier)
            throws X {
        if (null != object) {
            throw errorSupplier.get();
        }
    }

    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.isNull(value, "The value must be null");
     * </pre>
     *
     * @param object           被检查的对象
     * @param errorMsgTemplate 消息模板，变量使用{}表示
     * @param params           参数列表
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull(Object object, String errorMsgTemplate, Object... params)
            throws IllegalArgumentException {
        isNull(object, () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.isNull(value);
     * </pre>
     *
     * @param object 被检查对象
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public static void isNull(Object object) throws IllegalArgumentException {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    // ----------------------------------------------------------------------------------------------------------- Check not null

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出指定类型异常 并使用指定的函数获取错误信息返回
     *
     * <pre class="code">
     * Assert.notNull(clazz, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <T>           被检查对象泛型类型
     * @param <X>           异常类型
     * @param object        被检查对象
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 被检查后的对象
     * @throws X if the object is {@code null}
     */
    public static <T, X extends Throwable> T notNull(T object, Supplier<X> errorSupplier) throws X {
        if (null == object) {
            throw errorSupplier.get();
        }
        return object;
    }

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link IllegalArgumentException} 异常 Assert that an
     * object is not {@code null} .
     *
     * <pre class="code">
     * Assert.notNull(clazz, "The class must not be null");
     * </pre>
     *
     * @param <T>              被检查对象泛型类型
     * @param object           被检查对象
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 被检查后的对象
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static <T> T notNull(T object, String errorMsgTemplate, Object... params)
            throws IllegalArgumentException {
        return notNull(
                object, () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link IllegalArgumentException} 异常
     *
     * <pre class="code">
     * Assert.notNull(clazz);
     * </pre>
     *
     * @param <T>    被检查对象类型
     * @param object 被检查对象
     * @return 非空对象
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static <T> T notNull(T object) throws IllegalArgumentException {
        return notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    // ----------------------------------------------------------------------------------------------------------- Check empty

    /**
     * 检查给定字符串是否为空，为空抛出自定义异常，并使用指定的函数获取错误信息返回。
     *
     * <pre class="code">
     * Assert.notEmpty(name, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>           异常类型
     * @param <T>           字符串类型
     * @param text          被检查字符串
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 非空字符串
     * @throws X 被检查字符串为空抛出此异常
     */
    public static <T extends CharSequence, X extends Throwable> T notEmpty(
            T text, Supplier<X> errorSupplier) throws X {
        if (Whether.empty(text)) {
            throw errorSupplier.get();
        }
        return text;
    }

    /**
     * 检查给定字符串是否为空，为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notEmpty(name, "Name must not be empty");
     * </pre>
     *
     * @param <T>              字符串类型
     * @param text             被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 非空字符串
     * @throws IllegalArgumentException 被检查字符串为空
     */
    public static <T extends CharSequence> T notEmpty(
            T text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        return notEmpty(
                text, () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 检查给定字符串是否为空，为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notEmpty(name);
     * </pre>
     *
     * @param <T>  字符串类型
     * @param text 被检查字符串
     * @return 被检查的字符串
     * @throws IllegalArgumentException 被检查字符串为空
     */
    public static <T extends CharSequence> T notEmpty(T text) throws IllegalArgumentException {
        return notEmpty(
                text,
                "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出自定义异常。 并使用指定的函数获取错误信息返回
     *
     * <pre class="code">
     * Assert.notBlank(name, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <X>              异常类型
     * @param <T>              字符串类型
     * @param text             被检查字符串
     * @param errorMsgSupplier 错误抛出异常附带的消息生产接口
     * @return 非空字符串
     * @throws X 被检查字符串为空白
     */
    public static <T extends CharSequence, X extends Throwable> T notBlank(
            T text, Supplier<X> errorMsgSupplier) throws X {
        if (Whether.blank(text)) {
            throw errorMsgSupplier.get();
        }
        return text;
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notBlank(name, "Name must not be blank");
     * </pre>
     *
     * @param <T>              字符串类型
     * @param text             被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 非空字符串
     * @throws IllegalArgumentException 被检查字符串为空白
     */
    public static <T extends CharSequence> T notBlank(
            T text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        return notBlank(
                text, () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出 {@link IllegalArgumentException}
     *
     * <pre class="code">
     * Assert.notBlank(name);
     * </pre>
     *
     * @param <T>  字符串类型
     * @param text 被检查字符串
     * @return 非空字符串
     * @throws IllegalArgumentException 被检查字符串为空白
     */
    public static <T extends CharSequence> T notBlank(T text) throws IllegalArgumentException {
        return notBlank(
                text,
                "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    /**
     * 断言给定字符串是否不被另一个字符串包含（即是否为子串） 并使用指定的函数获取错误信息返回
     *
     * <pre class="code">
     * Assert.notContain(name, "rod", ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return ");
     *  });
     * </pre>
     *
     * @param <T>           字符串类型
     * @param <X>           异常类型
     * @param textToSearch  被搜索的字符串
     * @param substring     被检查的子串
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 被检查的子串
     * @throws X 非子串抛出异常
     * @see ToolString#contains(CharSequence, CharSequence)
     */
    public static <T extends CharSequence, X extends Throwable> T notContain(
            CharSequence textToSearch, T substring, Supplier<X> errorSupplier) throws X {
        if (ToolString.contains(textToSearch, substring)) {
            throw errorSupplier.get();
        }
        return substring;
    }

    /**
     * 断言给定字符串是否不被另一个字符串包含（即是否为子串）
     *
     * <pre class="code">
     * Assert.notContain(name, "rod", "Name must not contain 'rod'");
     * </pre>
     *
     * @param textToSearch     被搜索的字符串
     * @param substring        被检查的子串
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的子串
     * @throws IllegalArgumentException 非子串抛出异常
     */
    public static String notContain(
            String textToSearch, String substring, String errorMsgTemplate, Object... params)
            throws IllegalArgumentException {
        return notContain(
                textToSearch,
                substring,
                () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 断言给定字符串是否不被另一个字符串包含（即是否为子串）
     *
     * <pre class="code">
     * Assert.notContain(name, "rod");
     * </pre>
     *
     * @param textToSearch 被搜索的字符串
     * @param substring    被检查的子串
     * @return 被检查的子串
     * @throws IllegalArgumentException 非子串抛出异常
     */
    public static String notContain(String textToSearch, String substring)
            throws IllegalArgumentException {
        return notContain(
                textToSearch,
                substring,
                "[Assertion failed] - this String argument must not contain the substring [{}]",
                substring);
    }

    /**
     * 断言给定数组是否包含元素，数组必须不为 {@code null} 且至少包含一个元素 并使用指定的函数获取错误信息返回
     *
     * <pre class="code">
     * Assert.notEmpty(array, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <T>           数组元素类型
     * @param <X>           异常类型
     * @param array         被检查的数组
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 被检查的数组
     * @throws X if the object array is {@code null} or has no elements
     */
    public static <T, X extends Throwable> T[] notEmpty(T[] array, Supplier<X> errorSupplier)
            throws X {
        if (Whether.empty(array)) {
            throw errorSupplier.get();
        }
        return array;
    }

    /**
     * 断言给定数组是否包含元素，数组必须不为 {@code null} 且至少包含一个元素
     *
     * <pre class="code">
     * Assert.notEmpty(array, "The array must have elements");
     * </pre>
     *
     * @param <T>              数组元素类型
     * @param array            被检查的数组
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的数组
     * @throws IllegalArgumentException if the object array is {@code null} or has no elements
     */
    public static <T> T[] notEmpty(T[] array, String errorMsgTemplate, Object... params)
            throws IllegalArgumentException {
        return notEmpty(
                array, () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 断言给定数组是否包含元素，数组必须不为 {@code null} 且至少包含一个元素
     *
     * <pre class="code">
     * Assert.notEmpty(array, "The array must have elements");
     * </pre>
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     * @return 被检查的数组
     * @throws IllegalArgumentException if the object array is {@code null} or has no elements
     */
    public static <T> T[] notEmpty(T[] array) throws IllegalArgumentException {
        return notEmpty(
                array,
                "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
    }

    /**
     * 断言给定数组是否不包含{@code null}元素，如果数组为空或 {@code null}将被认为不包含 并使用指定的函数获取错误信息返回
     *
     * <pre class="code">
     * Assert.noNullElements(array, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return ");
     *  });
     * </pre>
     *
     * @param <T>           数组元素类型
     * @param <X>           异常类型
     * @param array         被检查的数组
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 被检查的数组
     * @throws X if the object array contains a {@code null} element
     */
    public static <T, X extends Throwable> T[] noNullElements(T[] array, Supplier<X> errorSupplier)
            throws X {
        if (Whether.anyNvl(array)) {
            throw errorSupplier.get();
        }
        return array;
    }

    /**
     * 断言给定数组是否不包含{@code null}元素，如果数组为空或 {@code null}将被认为不包含
     *
     * <pre class="code">
     * Assert.noNullElements(array, "The array must not have null elements");
     * </pre>
     *
     * @param <T>              数组元素类型
     * @param array            被检查的数组
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的数组
     * @throws IllegalArgumentException if the object array contains a {@code null} element
     */
    public static <T> T[] noNullElements(T[] array, String errorMsgTemplate, Object... params)
            throws IllegalArgumentException {
        return noNullElements(
                array, () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 断言给定集合非空 并使用指定的函数获取错误信息返回
     *
     * <pre class="code">
     * Assert.notEmpty(collection, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <E>           集合元素类型
     * @param <T>           集合类型
     * @param <X>           异常类型
     * @param collection    被检查的集合
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 非空集合
     * @throws X if the collection is {@code null} or has no elements
     */
    public static <E, T extends Iterable<E>, X extends Throwable> T notEmpty(
            T collection, Supplier<X> errorSupplier) throws X {
        if (Whether.empty(collection)) {
            throw errorSupplier.get();
        }
        return collection;
    }

    /**
     * 断言给定集合非空
     *
     * <pre class="code">
     * Assert.notEmpty(collection, "Collection must have elements");
     * </pre>
     *
     * @param <E>              集合元素类型
     * @param <T>              集合类型
     * @param collection       被检查的集合
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 非空集合
     * @throws IllegalArgumentException if the collection is {@code null} or has no elements
     */
    public static <E, T extends Iterable<E>> T notEmpty(
            T collection, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        return notEmpty(
                collection,
                () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 断言给定集合非空
     *
     * <pre class="code">
     * Assert.notEmpty(collection);
     * </pre>
     *
     * @param <E>        集合元素类型
     * @param <T>        集合类型
     * @param collection 被检查的集合
     * @return 被检查集合
     * @throws IllegalArgumentException if the collection is {@code null} or has no elements
     */
    public static <E, T extends Iterable<E>> T notEmpty(T collection)
            throws IllegalArgumentException {
        return notEmpty(
                collection,
                "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }

    /**
     * 断言给定Map非空 并使用指定的函数获取错误信息返回
     *
     * <pre class="code">
     * Assert.notEmpty(map, ()-&gt;{
     *      // to query relation message
     *      return new IllegalArgumentException("relation message to return");
     *  });
     * </pre>
     *
     * @param <K>           Key类型
     * @param <V>           Value类型
     * @param <T>           Map类型
     * @param <X>           异常类型
     * @param map           被检查的Map
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 被检查的Map
     * @throws X if the map is {@code null} or has no entries
     * @see MapUtil#isNotEmpty(Map)
     */
    public static <K, V, T extends Map<K, V>, X extends Throwable> T notEmpty(
            T map, Supplier<X> errorSupplier) throws X {
        if (Whether.empty(map)) {
            throw errorSupplier.get();
        }
        return map;
    }

    /**
     * 断言给定Map非空
     *
     * <pre class="code">
     * Assert.notEmpty(map, "Map must have entries");
     * </pre>
     *
     * @param <K>              Key类型
     * @param <V>              Value类型
     * @param <T>              Map类型
     * @param map              被检查的Map
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的Map
     * @throws IllegalArgumentException if the map is {@code null} or has no entries
     */
    public static <K, V, T extends Map<K, V>> T notEmpty(
            T map, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
        return notEmpty(
                map, () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 断言给定Map非空
     *
     * <pre class="code">
     * Assert.notEmpty(map, "Map must have entries");
     * </pre>
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param <T> Map类型
     * @param map 被检查的Map
     * @return 被检查的Map
     * @throws IllegalArgumentException if the map is {@code null} or has no entries
     */
    public static <K, V, T extends Map<K, V>> T notEmpty(T map) throws IllegalArgumentException {
        return notEmpty(
                map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
    }

    /**
     * 断言给定对象是否是给定类的实例
     *
     * <pre class="code">
     * Assert.instanceOf(Foo.class, foo);
     * </pre>
     *
     * @param <T>  被检查对象泛型类型
     * @param type 被检查对象匹配的类型
     * @param obj  被检查对象
     * @return 被检查的对象
     * @throws IllegalArgumentException if the object is not an instance of clazz
     * @see Class#isInstance(Object)
     */
    public static <T> T isInstanceOf(Class<?> type, T obj) {
        return isInstanceOf(type, obj, "Object [{}] is not instanceof [{}]", obj, type);
    }

    /**
     * 断言给定对象是否是给定类的实例
     *
     * <pre class="code">
     * Assert.instanceOf(Foo.class, foo, "foo must be an instance of class Foo");
     * </pre>
     *
     * @param <T>              被检查对象泛型类型
     * @param type             被检查对象匹配的类型
     * @param obj              被检查对象
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查对象
     * @throws IllegalArgumentException if the object is not an instance of clazz
     * @see Class#isInstance(Object)
     */
    public static <T> T isInstanceOf(Class<?> type, T obj, String errorMsgTemplate, Object... params)
            throws IllegalArgumentException {
        notNull(type, "Type to check against must not be null");
        if (false == type.isInstance(obj)) {
            throw new IllegalArgumentException(ToolString.format(errorMsgTemplate, params));
        }
        return obj;
    }

    /**
     * 断言 {@code superType.isAssignableFrom(subType)} 是否为 {@code true}.
     *
     * <pre class="code">
     * Assert.isAssignable(Number.class, myClass);
     * </pre>
     *
     * @param superType 需要检查的父类或接口
     * @param subType   需要检查的子类
     * @throws IllegalArgumentException 如果子类非继承父类，抛出此异常
     */
    public static void isAssignable(Class<?> superType, Class<?> subType)
            throws IllegalArgumentException {
        isAssignable(superType, subType, "{} is not assignable to {})", subType, superType);
    }

    /**
     * 断言 {@code superType.isAssignableFrom(subType)} 是否为 {@code true}.
     *
     * <pre class="code">
     * Assert.isAssignable(Number.class, myClass, "myClass must can be assignable to class Number");
     * </pre>
     *
     * @param superType        需要检查的父类或接口
     * @param subType          需要检查的子类
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @throws IllegalArgumentException 如果子类非继承父类，抛出此异常
     */
    public static void isAssignable(
            Class<?> superType, Class<?> subType, String errorMsgTemplate, Object... params)
            throws IllegalArgumentException {
        notNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(ToolString.format(errorMsgTemplate, params));
        }
    }

    /**
     * 检查boolean表达式，当检查结果为false时抛出 {@code IllegalStateException}。 并使用指定的函数获取错误信息返回
     *
     * <pre class="code">
     * Assert.state(id == null, ()-&gt;{
     *      // to query relation message
     *      return "relation message to return ";
     *  });
     * </pre>
     *
     * @param expression       boolean 表达式
     * @param errorMsgSupplier 错误抛出异常附带的消息生产接口
     * @throws IllegalStateException 表达式为 {@code false} 抛出此异常
     */
    public static void state(boolean expression, Supplier<String> errorMsgSupplier)
            throws IllegalStateException {
        if (false == expression) {
            throw new IllegalStateException(errorMsgSupplier.get());
        }
    }

    /**
     * 检查boolean表达式，当检查结果为false时抛出 {@code IllegalStateException}。
     *
     * <pre class="code">
     * Assert.state(id == null, "The id property must not already be initialized");
     * </pre>
     *
     * @param expression       boolean 表达式
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @throws IllegalStateException 表达式为 {@code false} 抛出此异常
     */
    public static void state(boolean expression, String errorMsgTemplate, Object... params)
            throws IllegalStateException {
        if (false == expression) {
            throw new IllegalStateException(ToolString.format(errorMsgTemplate, params));
        }
    }

    /**
     * 检查boolean表达式，当检查结果为false时抛出 {@code IllegalStateException}。
     *
     * <pre class="code">
     * Assert.state(id == null);
     * </pre>
     *
     * @param expression boolean 表达式
     * @throws IllegalStateException 表达式为 {@code false} 抛出此异常
     */
    public static void state(boolean expression) throws IllegalStateException {
        state(expression, "[Assertion failed] - this state invariant must be true");
    }

    /**
     * 检查下标（数组、集合、字符串）是否符合要求，下标必须满足：
     *
     * <pre>
     * 0 &le; index &lt; size
     * </pre>
     *
     * @param index 下标
     * @param size  长度
     * @return 检查后的下标
     * @throws IllegalArgumentException  如果size &lt; 0 抛出此异常
     * @throws IndexOutOfBoundsException 如果index &lt; 0或者 index &ge; size 抛出此异常
     */
    public static int checkIndex(int index, int size)
            throws IllegalArgumentException, IndexOutOfBoundsException {
        return checkIndex(index, size, "[Assertion failed]");
    }

    /**
     * 检查下标（数组、集合、字符串）是否符合要求，下标必须满足：
     *
     * <pre>
     * 0 &le; index &lt; size
     * </pre>
     *
     * @param index            下标
     * @param size             长度
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 检查后的下标
     * @throws IllegalArgumentException  如果size &lt; 0 抛出此异常
     * @throws IndexOutOfBoundsException 如果index &lt; 0或者 index &ge; size 抛出此异常
     */
    public static int checkIndex(int index, int size, String errorMsgTemplate, Object... params)
            throws IllegalArgumentException, IndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(badIndexMsg(index, size, errorMsgTemplate, params));
        }
        return index;
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param <X>           异常类型
     * @param value         值
     * @param min           最小值（包含）
     * @param max           最大值（包含）
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 经过检查后的值
     * @throws X if value is out of bound
     */
    public static <X extends Throwable> int checkBetween(
            int value, int min, int max, Supplier<? extends X> errorSupplier) throws X {
        if (value < min || value > max) {
            throw errorSupplier.get();
        }

        return value;
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value            值
     * @param min              最小值（包含）
     * @param max              最大值（包含）
     * @param errorMsgTemplate 异常信息模板，类似于"aa{}bb{}cc"
     * @param params           异常信息参数，用于替换"{}"占位符
     * @return 经过检查后的值
     */
    public static int checkBetween(
            int value, int min, int max, String errorMsgTemplate, Object... params) {
        return checkBetween(
                value,
                min,
                max,
                () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 检查后的长度值
     */
    public static int checkBetween(int value, int min, int max) {
        return checkBetween(value, min, max, TEMPLATE_VALUE_MUST_BE_BETWEEN_AND, min, max);
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param <X>           异常类型
     * @param value         值
     * @param min           最小值（包含）
     * @param max           最大值（包含）
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 经过检查后的值
     * @throws X if value is out of bound
     */
    public static <X extends Throwable> long checkBetween(
            long value, long min, long max, Supplier<? extends X> errorSupplier) throws X {
        if (value < min || value > max) {
            throw errorSupplier.get();
        }

        return value;
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value            值
     * @param min              最小值（包含）
     * @param max              最大值（包含）
     * @param errorMsgTemplate 异常信息模板，类似于"aa{}bb{}cc"
     * @param params           异常信息参数，用于替换"{}"占位符
     * @return 经过检查后的值
     */
    public static long checkBetween(
            long value, long min, long max, String errorMsgTemplate, Object... params) {
        return checkBetween(
                value,
                min,
                max,
                () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 检查后的长度值
     */
    public static long checkBetween(long value, long min, long max) {
        return checkBetween(value, min, max, TEMPLATE_VALUE_MUST_BE_BETWEEN_AND, min, max);
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param <X>           异常类型
     * @param value         值
     * @param min           最小值（包含）
     * @param max           最大值（包含）
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @return 经过检查后的值
     * @throws X if value is out of bound
     */
    public static <X extends Throwable> double checkBetween(
            double value, double min, double max, Supplier<? extends X> errorSupplier) throws X {
        if (value < min || value > max) {
            throw errorSupplier.get();
        }

        return value;
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value            值
     * @param min              最小值（包含）
     * @param max              最大值（包含）
     * @param errorMsgTemplate 异常信息模板，类似于"aa{}bb{}cc"
     * @param params           异常信息参数，用于替换"{}"占位符
     * @return 经过检查后的值
     */
    public static double checkBetween(
            double value, double min, double max, String errorMsgTemplate, Object... params) {
        return checkBetween(
                value,
                min,
                max,
                () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 检查后的长度值
     */
    public static double checkBetween(double value, double min, double max) {
        return checkBetween(value, min, max, TEMPLATE_VALUE_MUST_BE_BETWEEN_AND, min, max);
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 检查后的长度值
     */
    public static Number checkBetween(Number value, Number min, Number max) {
        notNull(value);
        notNull(min);
        notNull(max);
        double valueDouble = value.doubleValue();
        double minDouble = min.doubleValue();
        double maxDouble = max.doubleValue();
        if (valueDouble < minDouble || valueDouble > maxDouble) {
            throw new IllegalArgumentException(
                    ToolString.format(TEMPLATE_VALUE_MUST_BE_BETWEEN_AND, min, max));
        }
        return value;
    }

    /**
     * 断言两个对象是否不相等,如果两个对象相等 抛出IllegalArgumentException 异常
     *
     * <pre class="code">
     *   Assert.notEquals(obj1,obj2);
     * </pre>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @throws IllegalArgumentException obj1 must be not equals obj2
     */
    public static void notEquals(Object obj1, Object obj2) {
        notEquals(obj1, obj2, "({}) must be not equals ({})", obj1, obj2);
    }

    /**
     * 断言两个对象是否不相等,如果两个对象相等 抛出IllegalArgumentException 异常
     *
     * <pre class="code">
     *   Assert.notEquals(obj1,obj2,"obj1 must be not equals obj2");
     * </pre>
     *
     * @param obj1             对象1
     * @param obj2             对象2
     * @param errorMsgTemplate 异常信息模板，类似于"aa{}bb{}cc"
     * @param params           异常信息参数，用于替换"{}"占位符
     * @throws IllegalArgumentException obj1 must be not equals obj2
     */
    public static void notEquals(Object obj1, Object obj2, String errorMsgTemplate, Object... params)
            throws IllegalArgumentException {
        notEquals(
                obj1,
                obj2,
                () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 断言两个对象是否不相等,如果两个对象相等,抛出指定类型异常,并使用指定的函数获取错误信息返回
     *
     * @param obj1          对象1
     * @param obj2          对象2
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @param <X>           异常类型
     * @throws X obj1 must be not equals obj2
     */
    public static <X extends Throwable> void notEquals(
            Object obj1, Object obj2, Supplier<X> errorSupplier) throws X {
        if (ToolObject.equals(obj1, obj2)) {
            throw errorSupplier.get();
        }
    }
    // ----------------------------------------------------------------------------------------------------------- Check not equals

    /**
     * 断言两个对象是否相等,如果两个对象不相等 抛出IllegalArgumentException 异常
     *
     * <pre class="code">
     *   Assert.isEquals(obj1,obj2);
     * </pre>
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @throws IllegalArgumentException obj1 must be equals obj2
     */
    public static void equals(Object obj1, Object obj2) {
        equals(obj1, obj2, "({}) must be equals ({})", obj1, obj2);
    }

    /**
     * 断言两个对象是否相等,如果两个对象不相等 抛出IllegalArgumentException 异常
     *
     * <pre class="code">
     *   Assert.isEquals(obj1,obj2,"obj1 must be equals obj2");
     * </pre>
     *
     * @param obj1             对象1
     * @param obj2             对象2
     * @param errorMsgTemplate 异常信息模板，类似于"aa{}bb{}cc"
     * @param params           异常信息参数，用于替换"{}"占位符
     * @throws IllegalArgumentException obj1 must be equals obj2
     */
    public static void equals(Object obj1, Object obj2, String errorMsgTemplate, Object... params)
            throws IllegalArgumentException {
        equals(
                obj1,
                obj2,
                () -> new IllegalArgumentException(ToolString.format(errorMsgTemplate, params)));
    }

    /**
     * 断言两个对象是否相等,如果两个对象不相等,抛出指定类型异常,并使用指定的函数获取错误信息返回
     *
     * @param obj1          对象1
     * @param obj2          对象2
     * @param errorSupplier 错误抛出异常附带的消息生产接口
     * @param <X>           异常类型
     * @throws X obj1 must be equals obj2
     */
    public static <X extends Throwable> void equals(
            Object obj1, Object obj2, Supplier<X> errorSupplier) throws X {
        if (ToolObject.notEqual(obj1, obj2)) {
            throw errorSupplier.get();
        }
    }

    // ----------------------------------------------------------------------------------------------------------- Check is equals

    // -------------------------------------------------------------------------------------------------------------------------------------------- Private method start

    /**
     * 错误的下标时显示的消息
     *
     * @param index  下标
     * @param size   长度
     * @param desc   异常时的消息模板
     * @param params 参数列表
     * @return 消息
     */
    private static String badIndexMsg(int index, int size, String desc, Object... params) {
        if (index < 0) {
            return ToolString.format(
                    "{} ({}) must not be negative", ToolString.format(desc, params), index);
        } else if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        } else { // index >= size
            return ToolString.format(
                    "{} ({}) must be less than size ({})", ToolString.format(desc, params), index, size);
        }
    }
    // -------------------------------------------------------------------------------------------------------------------------------------------- Private method end

}

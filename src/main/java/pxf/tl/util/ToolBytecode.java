package pxf.tl.util;

import pxf.tl.api.PoolOfArray;
import pxf.tl.api.PoolOfCharacter;
import pxf.tl.api.PoolOfObject;
import pxf.tl.api.Tuple;
import pxf.tl.bean.NullWrapperBean;
import pxf.tl.collection.UniqueKeySet;
import pxf.tl.collection.map.WeakConcurrentMap;
import pxf.tl.exception.InvocationTargetRuntimeException;
import pxf.tl.help.New;
import pxf.tl.help.Whether;
import pxf.tl.lang.JarClassLoader;
import pxf.tl.lang.TextBuilder;
import pxf.tl.lang.reflect.LookupFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author potatoxf
 */
@SuppressWarnings("unchecked")
public final class ToolBytecode {

    /**
     * 构造对象缓存
     */
    private static final WeakConcurrentMap<Tuple<Object>, Class<?>> CLASS_CACHE =
            new WeakConcurrentMap<>();
    /**
     * 构造对象缓存
     */
    private static final WeakConcurrentMap<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE =
            new WeakConcurrentMap<>();
    /**
     * 方法缓存
     */
    private static final WeakConcurrentMap<Class<?>, Method[]> METHODS_CACHE =
            new WeakConcurrentMap<>();
    /**
     * 字段缓存
     */
    private static final WeakConcurrentMap<Class<?>, Field[]> FIELDS_CACHE =
            new WeakConcurrentMap<>();

    private ToolBytecode() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    //------------------------------------------------------------------------------------------------------------------
    //ClassLoader
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 获取当前线程的{@link ClassLoader}
     *
     * @return 当前线程的class loader
     * @see Thread#getContextClassLoader()
     */
    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获取系统{@link ClassLoader}
     *
     * @return 系统{@link ClassLoader}
     * @see ClassLoader#getSystemClassLoader()
     */
    public static ClassLoader getSystemClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 创建新的{@link JarClassLoader}，并使用此Classloader加载目录下的class文件和jar文件
     *
     * @param jarOrDir jar文件或者包含jar和class文件的目录
     * @return {@link JarClassLoader}
     */
    public static JarClassLoader getJarClassLoader(File jarOrDir) {
        return JarClassLoader.load(jarOrDir);
    }

    /**
     * 获取{@link ClassLoader}<br>
     * 获取顺序如下：<br>
     *
     * <pre>
     * 1、获取当前线程的ContextClassLoader
     * 2、获取当前类对应的ClassLoader
     * 3、获取系统ClassLoader（{@link ClassLoader#getSystemClassLoader()}）
     * </pre>
     *
     * @return 类加载器
     */
    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = ToolBytecode.getContextClassLoader();
        if (classLoader == null) {
            classLoader = ToolBytecode.class.getClassLoader();
            if (null == classLoader) {
                classLoader = ToolBytecode.getSystemClassLoader();
            }
        }
        return classLoader;
    }

    //------------------------------------------------------------------------------------------------------------------
    //获取Class
    //------------------------------------------------------------------------------------------------------------------


    /**
     * 获取泛型类型
     *
     * @param root   从哪个类型获取
     * @param target 指定泛型所在目标类型
     * @param index  获取第几个
     * @return {@code Class<?>}
     * @throws IllegalArgumentException 如果找不到泛型Class
     */
    @Nonnull
    public static Class<?> extractGenericClass(@Nonnull final Class<?> root, @Nonnull final Class<?> target, final int index) {
        if (index < 0) {
            throw new IllegalArgumentException("The index must be great then or equal 0");
        }
        if (!target.isAssignableFrom(root)) {
            throw new IllegalArgumentException(
                    String.format("No generic class found in [%d] on the parent class of %s", index, target));
        }
        TypeVariable<? extends Class<?>>[] targetParameters = target.getTypeParameters();
        if (index >= targetParameters.length) {
            throw new IllegalArgumentException("The index must be less then " + targetParameters.length);
        }
        Type result = null;
        if (root.isInterface()) {
            result = extractInterfaceClass(root, target, index);
        } else {
            if (target.isInterface()) {
                boolean success = false;
                for (Class<?> currentClass = root; !success && currentClass != null; currentClass = currentClass.getSuperclass()) {
                    ParameterizedType[] childrenParameterizedType = Arrays.stream(currentClass.getGenericInterfaces())
                            .filter(type -> type instanceof ParameterizedType)
                            .map(type -> (ParameterizedType) type)
                            .toArray(ParameterizedType[]::new);
                    if (childrenParameterizedType.length == 0) {
                        continue;
                    }
                    for (int i = 0; !success && i < childrenParameterizedType.length; i++) {
                        if (childrenParameterizedType[i].getRawType() instanceof Class childrenClass) {
                            if (childrenClass == target) {
                                result = extractGenericValue(root, currentClass, childrenParameterizedType[i], index);
                                success = true;
                                break;
                            } else {
                                result = extractInterfaceClass(childrenClass, target, index);
                                if (result instanceof TypeVariable) {
                                    TypeVariable<? extends Class<?>>[] typeParameters = childrenClass.getTypeParameters();
                                    for (int j = 0; j < typeParameters.length; j++) {
                                        if (typeParameters[j] == result) {
                                            result = extractGenericClass(root, childrenClass, j);
                                            success = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                for (Class<?> currentClass = root; currentClass != null; currentClass = currentClass.getSuperclass()) {
                    Type type = currentClass.getGenericSuperclass();
                    if (!(type instanceof ParameterizedType parameterizedType)
                            || parameterizedType.getRawType() != target) {
                        continue;
                    }
                    result = extractGenericValue(root, currentClass, parameterizedType, index);
                    break;
                }
            }
        }
        if (result instanceof Class) {
            return (Class<?>) result;
        }
        throw new IllegalArgumentException(
                String.format("No generic class found in [%d] on the parent class of %s", index, target));
    }

    @Nullable
    private static Type extractInterfaceClass(@Nonnull final Class<?> rootInterface,
                                              @Nonnull final Class<?> targetInterface,
                                              int index) {
        if (rootInterface == targetInterface) {
            return rootInterface.getTypeParameters()[index];
        }
        if (!targetInterface.isInterface()) {
            throw new IllegalArgumentException("The class is interface but the target class is not interface");
        }

        LinkedList<Class<?>> queue = new LinkedList<>();
        queue.add(rootInterface);
        while (!queue.isEmpty()) {
            Class<?> currentInterface = queue.pop();
            for (ParameterizedType childrenParameterizedType : Arrays.stream(currentInterface.getGenericInterfaces())
                    .filter(type -> type instanceof ParameterizedType)
                    .map(type -> (ParameterizedType) type)
                    .toArray(ParameterizedType[]::new)) {
                if (childrenParameterizedType.getRawType() instanceof Class<?> childrenClass) {
                    if (childrenClass == targetInterface) {
                        return extractGenericValue(rootInterface, currentInterface, childrenParameterizedType, index);
                    } else {
                        queue.push(childrenClass);
                    }
                }
            }
        }
        return null;
    }

    private static Type extractGenericValue(@Nonnull Class<?> root,
                                            @Nonnull Class<?> currentClass,
                                            @Nonnull ParameterizedType childrenParameterizedType,
                                            int index) {
        Type[] actualTypeArguments = childrenParameterizedType.getActualTypeArguments();
        if (actualTypeArguments[index] instanceof Class) {
            return forClassName(actualTypeArguments[index].getTypeName());
        } else if (actualTypeArguments[index] instanceof TypeVariable) {
            TypeVariable<? extends Class<?>>[] typeParameters = currentClass.getTypeParameters();
            for (int i = 0; i < typeParameters.length; i++) {
                if (typeParameters[i] == actualTypeArguments[index]) {
                    if (root.isInterface()) {
                        return extractInterfaceClass(root, currentClass, i);
                    } else {
                        return extractGenericClass(root, currentClass, i);
                    }
                }
            }
            return actualTypeArguments[index];
        }
        return null;
    }

    /**
     * 获取类名
     *
     * @param object   对象或者类名
     * @param isSimple 是否简单类名，如果为true，返回不带包名的类名
     * @return 类名
     */
    @Nonnull
    public static String getClassName(@Nonnull Object object, boolean isSimple) {
        return object instanceof Class c ? (isSimple ? c.getSimpleName() : c.getName())
                : (isSimple ? object.getClass().getSimpleName() : object.getClass().getName());
    }

    /**
     * 获取完整类名的短格式如：<br>
     * potatoxf.helper.util.StrUtil -》c.h.c.u.StrUtil
     *
     * @param object 对象或者类名
     * @return 短格式类名
     */
    public static String getAbridgeClassName(@Nonnull Object object) {
        String className = ToolBytecode.getClassName(object, false);
        final String[] names = className.split("\\.");
        if (names.length < 2) {
            return className;
        }

        final int size = names.length;
        final StringBuilder result = ToolString.builder();
        result.append(names[0].charAt(0));
        for (int i = 1; i < size - 1; i++) {
            result.append(PoolOfCharacter.DOT).append(names[i].charAt(0));
        }
        result.append(PoolOfCharacter.DOT).append(names[size - 1]);
        return result.toString();
    }

    /**
     * 将类装换成缩写全限定名字符串
     *
     * @param object 对象或者类名
     * @return 返回缩写全限定名字符串
     */
    public static String toAbridgeClassName(@Nonnull Object object) {
        return toAbridgeClassName(object, 60);
    }

    /**
     * 将类装换成缩写全限定名字符串
     *
     * @param object    对象或者类名
     * @param maxLength 最大长度
     * @return 返回缩写全限定名字符串
     */
    public static String toAbridgeClassName(@Nonnull Object object, final int maxLength) {
        String className = ToolBytecode.getClassName(object, false);
        int limit = Math.max(40, maxLength);
        String[] names = className.split("\\.");
        limit = limit - names.length + 1;
        int count;
        for (count = 0; count < names.length; count++) {
            int total = 0;
            for (int i = 0; i <= count; i++) {
                total += names[names.length - i - 1].length();
            }
            total = total + names.length - count - 1;
            if (total > limit) {
                count--;
                break;
            }
        }
        TextBuilder sb = TextBuilder.of(limit);
        for (int i = 0; i < names.length - count; i++) {
            sb.append(names[i].charAt(0)).append('.');
        }
        for (int i = names.length - count; i < names.length; i++) {
            sb.append(names[i]).append('.');
        }
        sb.setLength(sb.length() - 1)
                .appendRepeat(" ", Math.max(0, maxLength - sb.length()));
        return sb.toString();
    }

    /**
     * 获取Class
     *
     * @param namespace 命名空间
     * @return {@code Class<?>}
     */
    @Nonnull
    public static Class<?> forNamespace(@Nonnull String namespace) {
        int i = namespace.lastIndexOf(".");
        if (i > 0) {
            return ToolBytecode.forClassName(namespace.substring(0, i));
        } else {
            throw new IllegalArgumentException("The namespace format was error for [" + namespace + "]");
        }
    }

    /**
     * 获取Class
     *
     * @param namespace 命名空间
     * @return {@code Class<?>}
     */
    @Nullable
    public static Class<?> forNamespaceSilent(@Nonnull String namespace) {
        int i = namespace.lastIndexOf(".");
        if (i > 0) {
            return ToolBytecode.forClassNameSilent(namespace.substring(0, i));
        } else {
            throw new IllegalArgumentException("The namespace format was error for [" + namespace + "]");
        }
    }

    /**
     * 获取Class
     *
     * @param className 类
     * @return {@code Class<?>}
     */
    @Nonnull
    public static <T> Class<T> forClassName(@Nonnull String className) {
        Class<T> result = ToolBytecode.forClassNameSilent(className);
        if (result == null) {
            throw new RuntimeException("Can't get class based on [" + className + "]");
        }
        return result;
    }

    /**
     * 获取Class
     *
     * @param className 类
     * @return {@code Class<?>}
     */
    @Nullable
    public static <T> Class<T> forClassNameSilent(@Nonnull String className) {
        return (Class<T>) loadClassSilent(className, null);
    }

    /**
     * 加载类，通过传入类的字符串，返回其对应的类名<br>
     * 此方法支持缓存，第一次被加载的类之后会读取缓存中的类<br>
     * 加载失败的原因可能是此类不存在或其关联引用类不存在<br>
     * 扩展{@link Class#forName(String, boolean, ClassLoader)}方法，支持以下几类类名的加载：
     *
     * <pre>
     * 1、原始类型，例如：int
     * 2、数组类型，例如：int[]、Long[]、String[]
     * 3、内部类，例如：java.lang.Thread.State会被转为java.lang.Thread$State加载
     * </pre>
     *
     * @param className   类名
     * @param classLoader {@link ClassLoader}
     * @return 类名对应的类，如果不存在返回null
     */
    @Nullable
    public static Class<?> loadClassSilent(@Nonnull String className, @Nullable ClassLoader classLoader) {
        // 自动将包名中的"/"替换为"."
        className = className.replace(PoolOfCharacter.SLASH, PoolOfCharacter.DOT).trim();

        // 加载原始类型和缓存中的类
        Class<?> clazz = null;

        if (className.length() <= 8) {
            clazz = PoolOfObject.PRIMITIVE_NAME_TO_PRIMITIVE.get().get(className);
        }

        if (clazz == null) {
            clazz = CLASS_CACHE.computeIfAbsent(new Tuple<>(className, classLoader), (key) -> {
                String cn = (String) key.get(0);
                ClassLoader cl = (ClassLoader) key.get(1);
                int length = cn.length(), i;
                Class<?> c = null;
                if ((i = cn.lastIndexOf("[]")) == length - 2) {
                    // 对象数组"java.lang.String[]"风格
                    final Class<?> elementClass = loadClassSilent(cn.substring(0, i), cl);
                    if (elementClass != null) {
                        c = Array.newInstance(elementClass, 0).getClass();
                    }
                } else if ((i = cn.indexOf("[L")) == 0 && cn.endsWith(";")) {
                    // "[Ljava.lang.String;" 风格
                    final Class<?> elementClass = loadClassSilent(cn.substring(i, length - 1), cl);
                    if (elementClass != null) {
                        c = Array.newInstance(elementClass, 0).getClass();
                    }
                } else if ((i = cn.indexOf("[")) == 0) {
                    // "[[I" 或 "[[Ljava.lang.String;" 风格
                    final Class<?> elementClass = loadClassSilent(cn.substring(i), cl);
                    if (elementClass != null) {
                        c = Array.newInstance(elementClass, 0).getClass();
                    }
                } else {
                    // 加载普通类
                    try {
                        if (null == cl) {
                            c = Class.forName(cn);
                        } else {
                            c = Class.forName(cn, true, cl);
                        }
                    } catch (ClassNotFoundException e) {
                        // 尝试获取内部类，例如java.lang.Thread.State =》java.lang.Thread$State
                        // 尝试获取内部类，例如java.lang.Thread.State =》java.lang.Thread$State
                        final int lastDotIndex = cn.lastIndexOf(".");
                        if (lastDotIndex > 0) { // 类与内部类的分隔符不能在第一位，因此>0
                            final String innerClassName =
                                    cn.substring(0, lastDotIndex)
                                            + "$" + cn.substring(lastDotIndex + 1);
                            try {
                                c = Class.forName(innerClassName, true, cl);
                            } catch (ClassNotFoundException ex) {
                                // 尝试获取内部类失败时，忽略之。
                            }
                        }
                    }
                }
                return c;
            });
        }
        return clazz;
    }

    /**
     * 加载外部类
     *
     * @param jarOrDir jar文件或者包含jar和class文件的目录
     * @param name     类名
     * @return 类
     */
    public static Class<?> loadOuterClass(File jarOrDir, String name) throws ClassNotFoundException {
        return ToolBytecode.getJarClassLoader(jarOrDir).loadClass(name);
    }

    /**
     * 获得Type对应的原始类
     *
     * @param any {@link Type}
     * @return 原始类，如果无法获取原始类，返回{@code null}
     */
    @Nonnull
    public static Class<?> getClass(@Nonnull Object any) {
        return switch (any) {
            case Class c -> c;
            case ParameterizedType p -> (Class<?>) p.getRawType();
            case TypeVariable t -> (Class<?>) t.getBounds()[0];
            case NullWrapperBean n -> n.getWrappedClass();
            case WildcardType w -> ToolBytecode.getClass(w.getUpperBounds()[0]);
            default -> any.getClass();
        };
    }

    /**
     * 获得对象数组的类数组
     *
     * @param objects 对象数组，如果数组中存在{@code null}元素，则此元素被认为是Object类型
     * @return 类数组
     */
    @Nonnull
    public static Class<?>[] getClasses(@Nonnull Object... objects) {
        Class<?>[] classes = new Class<?>[objects.length];
        Object obj;
        for (int i = 0; i < objects.length; i++) {
            obj = objects[i];
            if (null == obj) {
                classes[i] = Object.class;
            } else {
                classes[i] = ToolBytecode.getClass(obj);
            }
        }
        return classes;
    }

    /**
     * 创建一个新对象
     *
     * @param clz 类
     * @param <T> 类的类型
     * @return 返回创建出的对象，返回对象不为null
     * @throws RuntimeException 如果无法创建成功则抛出该异常
     */
    @Nullable
    public static <T> T createInstance(@Nonnull Class<T> clz) {
        return createInstance(clz, PoolOfArray.EMPTY_OBJECT_ARRAY);
    }

    /**
     * 创建一个新对象
     *
     * @param clz  类
     * @param args 构成对象的参数
     * @param <T>  类的类型
     * @return 返回创建出的对象，返回对象不为null
     * @throws RuntimeException 如果无法创建成功则抛出该异常
     */
    @Nullable
    public static <T> T createInstance(@Nonnull Class<T> clz, @Nonnull Object... args) {
        Constructor<T> constructor = ToolBytecode.getConstructor(clz, ToolBytecode.getClasses(args));
        if (constructor != null) {
            try {
                return constructor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 尝试遍历并调用此类的所有构造方法，直到构造成功并返回
     *
     * <p>对于某些特殊的接口，按照其默认实现实例化，例如：
     *
     * <pre>
     *     Map       -》 HashMap
     *     Collection -》 ArrayList
     *     List      -》 ArrayList
     *     Set       -》 HashSet
     * </pre>
     *
     * @param <T> 对象类型
     * @param clz 被构造的类
     * @return 构造后的对象，构造失败返回{@code null}
     */
    public static <T> T createInstanceIfPossible(@Nonnull Class<T> clz) {
        // 原始类型
        if (clz.isPrimitive() && PoolOfObject.PRIMITIVE_DEFAULT_VALUE.get().containsKey(clz)) {
            return (T) PoolOfObject.PRIMITIVE_DEFAULT_VALUE.get().get(clz);
        }
        // 枚举
        if (clz.isEnum()) {
            return null;
        }
        // 数组
        if (clz.isArray()) {
            return (T) Array.newInstance(clz.getComponentType(), 0);
        }

        if (clz.isInterface()) {
            if (clz == Map.class) {
                return (T) new HashMap<>();
            } else if (clz == List.class || clz == Collection.class) {
                return (T) new ArrayList<>();
            } else if (clz == Set.class) {
                return (T) new HashSet<>();
            }
        } else {
            return ToolBytecode.createInstance(clz);
        }
        return null;
    }

    /**
     * 在当前注解元素查找指定类型注解元素
     *
     * @param annotatedElement 注解元素
     * @param annotationType   注解元素Class
     * @param <A>              注解元素类型
     * @return {@code List<A>}
     */
    public static <A extends Annotation> List<A> lookupAnnotation(@Nonnull AnnotatedElement annotatedElement,
                                                                  @Nonnull Class<A> annotationType,
                                                                  boolean isLookupParent) {
        return (List<A>) lookupAllAnnotation(annotatedElement, null, annotation -> annotation.annotationType() == annotationType, isLookupParent);
    }

    /**
     * 在当前注解元素查找所有逐渐
     *
     * @param annotatedElement      注解元素
     * @param forbidRecursionFilter 禁止迭代过滤器
     * @param predicate             注解过滤器
     * @return {@code List<Annotation>}
     */
    public static List<Annotation> lookupAllAnnotation(@Nonnull AnnotatedElement annotatedElement,
                                                       @Nullable Predicate<Annotation> forbidRecursionFilter,
                                                       @Nullable Predicate<Annotation> predicate,
                                                       boolean isLookupParent) {
        if (annotatedElement instanceof Class clz && isLookupParent) {
            List<Annotation> list = new ArrayList<>();
            for (Class<?> c = clz; c != Object.class; c = c.getSuperclass()) {
                list.addAll(lookupAllAnnotationOnAnnotation(c.getDeclaredAnnotations(), forbidRecursionFilter, predicate));
            }
            return list;
        } else {
            Stream<Annotation> stream = lookupAllAnnotationOnAnnotation(annotatedElement.getDeclaredAnnotations(), forbidRecursionFilter, predicate)
                    .stream();
            if (predicate != null) {
                stream = stream.filter(predicate);
            }
            return stream.collect(Collectors.toList());
        }
    }

    /**
     * 在当前注解以及父级查找所有逐渐
     *
     * @param annotations           注解数组
     * @param forbidRecursionFilter 禁止迭代过滤器
     * @return {@code List<Annotation>}
     */
    public static List<Annotation> lookupAllAnnotationOnAnnotation(@Nonnull Annotation[] annotations,
                                                                   @Nullable Predicate<Annotation> forbidRecursionFilter,
                                                                   @Nullable Predicate<Annotation> predicate) {
        List<Annotation> allAnnotation = new ArrayList<>();
        recursionAnnotation(annotations, allAnnotation, new LinkedList<>(), forbidRecursionFilter);
        if (predicate == null) {
            return allAnnotation;
        }
        return allAnnotation.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 迭代注解
     *
     * @param annotations           注解数组
     * @param annotationContainer   注解容器
     * @param stack                 栈容器
     * @param forbidRecursionFilter 禁止迭代过滤器
     */
    private static void recursionAnnotation(@Nonnull Annotation[] annotations,
                                            @Nonnull List<Annotation> annotationContainer,
                                            @Nonnull LinkedList<Annotation> stack,
                                            @Nullable Predicate<Annotation> forbidRecursionFilter) {
        for (Annotation annotation : annotations) {
            if (stack.lastIndexOf(annotation) >= 0) {
                continue;
            }
            stack.push(annotation);
            annotationContainer.add(annotation);
            Stream<Annotation> annotationStream = Arrays.stream(annotation.annotationType().getDeclaredAnnotations())
                    .filter(anno -> !PoolOfObject.META_ANNOTATIONS.get().contains(anno.annotationType()));

            if (forbidRecursionFilter != null) {
                annotationStream = annotationStream.filter(forbidRecursionFilter);
            }
            Annotation[] nextAnnotations = annotationStream.toArray(Annotation[]::new);

            if (nextAnnotations.length != 0) {
                recursionAnnotation(nextAnnotations, annotationContainer, stack, forbidRecursionFilter);
            }
            stack.pop();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //Class判断
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 是否为包装类型
     *
     * @param clazz 类
     * @return 是否为包装类型
     */
    public static boolean isPrimitiveWrapper(@Nullable Class<?> clazz) {
        return null != clazz && PoolOfObject.WRAPPER_TO_PRIMITIVE.get().containsKey(clazz);
    }

    /**
     * 是否为基本类型（包括包装类和原始类）
     *
     * @param clazz 类
     * @return 是否为基本类型
     */
    public static boolean isBasicType(@Nullable Class<?> clazz) {
        return null != clazz && (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    /**
     * 比较判断target是否和inputCLass兼容，target是inputCLass或接口，或target是inputCLass或更大的基本类型
     *
     * @param target     类组
     * @param inputCLass 另一个类组
     * @return 如果兼容返回true，否则返回false
     */
    public static boolean isCompatibilityFrom(@Nullable Class<?>[] target, @Nullable Class<?>[] inputCLass) {
        if (Whether.empty(target) && Whether.empty(inputCLass)) {
            return true;
        }
        if (null == target || null == inputCLass) {
            // 任何一个为null不相等（之前已判断两个都为null的情况）
            return false;
        }
        if (target.length != inputCLass.length) {
            return false;
        }
        for (int i = 0; i < target.length; i++) {
            if (!isCompatibilityFrom(target[i], inputCLass[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较判断target是否和inputCLass兼容，target是inputCLass或接口，或target是inputCLass或更大的基本类型
     *
     * @param target     类
     * @param inputCLass 另一个类
     * @return 如果兼容返回true，否则返回false
     */
    public static boolean isCompatibilityFrom(@Nonnull Class<?> target, @Nonnull Class<?> inputCLass) {
        boolean targetArray = target.isArray();
        boolean inputCLassArray = inputCLass.isArray();
        if (targetArray && inputCLassArray) {
            return target == inputCLass;
        } else if (targetArray || inputCLassArray) {
            return false;
        } else {
            //目标类型是原生类型
            if (PoolOfObject.PRIMITIVE_TO_WRAPPER.get().containsKey(target)) {
                Map<Class<?>, Integer> pc = PoolOfObject.PRIMITIVE_COMPATIBILITY.get();
                int targetCompatibility = pc.getOrDefault(target, -1);
                int otherClazzCompatibility = pc.getOrDefault(PoolOfObject.unwrap(inputCLass), -1);
                //目标类型是原生类型的兼容性
                return targetCompatibility >= otherClazzCompatibility;
            }
            //目标类型是包装类型
            else if (PoolOfObject.WRAPPER_TO_PRIMITIVE.get().containsKey(target)) {
                return target == inputCLass;
            } else {
                return target.isAssignableFrom(inputCLass);
            }
        }
    }


    //------------------------------------------------------------------------------------------------------------------
    //获取Constructor
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 查找类中的指定参数的构造方法，如果找到构造方法，会自动设置可访问为true
     *
     * @param <T>            对象类型
     * @param clazz          类
     * @param parameterTypes 参数类型，只要任何一个参数是指定参数的父类或接口或相等即可，此参数可以不传
     * @return 构造方法，如果未找到返回null
     */
    @Nullable
    public static <T> Constructor<T> getConstructor(@Nonnull Class<T> clazz, @Nonnull Class<?>... parameterTypes) {
        final Constructor<T>[] constructors = ToolBytecode.getConstructors(clazz);
        if (parameterTypes.length == 0) {
            return Arrays.stream(constructors).filter(constructor -> constructor.getParameterCount() == 0).findFirst().orElse(null);
        }
        Optional<Constructor<T>> optionalConstructor = Arrays.stream(constructors)
                .filter(constructor -> constructor.getParameterCount() == parameterTypes.length)
                .filter(constructor -> isCompatibilityFrom(constructor.getParameterTypes(), parameterTypes)).findFirst();

        if (optionalConstructor.isPresent()) {
            Constructor<T> constructor = optionalConstructor.get();
            constructor.setAccessible(true);
            return constructor;
        }
        return null;
    }

    /**
     * 获得一个类中所有构造列表
     *
     * @param <T>   构造的对象类型
     * @param clazz 类，非{@code null}
     * @return 字段列表
     */
    @Nonnull
    public static <T> Constructor<T>[] getConstructors(@Nonnull Class<T> clazz) throws SecurityException {
        return (Constructor<T>[]) CONSTRUCTORS_CACHE.computeIfAbsent(clazz, (clz) -> clazz.getDeclaredConstructors());
    }

    //------------------------------------------------------------------------------------------------------------------
    //Method
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 检查给定方法是否为Getter或者Setter方法，规则为：<br>
     *
     * <ul>
     *   <li>方法参数必须为0个或1个
     *   <li>如果是无参方法，则判断是否以“get”或“is”开头
     *   <li>如果方法参数1个，则判断是否以“set”开头
     * </ul>
     *
     * @param method 方法
     * @return 是否为Getter或者Setter方法
     */
    public static boolean isGetterOrSetterIgnoreCase(@Nullable Method method) {
        return isGetterOrSetter(method, true);
    }

    /**
     * 检查给定方法是否为Getter或者Setter方法，规则为：<br>
     *
     * <ul>
     *   <li>方法参数必须为0个或1个
     *   <li>方法名称不能是getClass
     *   <li>如果是无参方法，则判断是否以“get”或“is”开头
     *   <li>如果方法参数1个，则判断是否以“set”开头
     * </ul>
     *
     * @param method     方法
     * @param ignoreCase 是否忽略方法名的大小写
     * @return 是否为Getter或者Setter方法
     */
    public static boolean isGetterOrSetter(@Nullable Method method, boolean ignoreCase) {
        if (null == method) {
            return false;
        }

        // 参数个数必须为0或1
        final int parameterCount = method.getParameterCount();
        if (parameterCount > 1) {
            return false;
        }

        String name = method.getName();
        // 跳过getClass这个特殊方法
        if ("getClass".equals(name)) {
            return false;
        }
        if (ignoreCase) {
            name = name.toLowerCase();
        }
        return switch (parameterCount) {
            case 0 -> name.startsWith("get") || name.startsWith("is");
            case 1 -> name.startsWith("set");
            default -> false;
        };
    }

    /**
     * 是否为equals方法
     *
     * @param method 方法
     * @return 是否为equals方法
     */
    public static boolean isEqualsMethod(@Nullable Method method) {
        return method != null
                && method.getParameterCount() == 1
                && "equals".equals(method.getName())
                && method.getParameterTypes()[0] == Object.class;
    }

    /**
     * 是否为hashCode方法
     *
     * @param method 方法
     * @return 是否为hashCode方法
     */
    public static boolean isHashCodeMethod(@Nullable Method method) {
        return method != null
                && method.getParameterCount() == 0
                && "hashCode".equals(method.getName());
    }

    /**
     * 是否为toString方法
     *
     * @param method 方法
     * @return 是否为toString方法
     */
    public static boolean isToStringMethod(@Nullable Method method) {
        return method != null
                && method.getParameterCount() == 0
                && "toString".equals(method.getName());
    }

    /**
     * 获取方法的唯一键，结构为:
     *
     * <pre>
     *     返回类型#类全限定名.方法名:参数1类型,参数2类型...
     * </pre>
     *
     * @param method 方法
     * @return 方法唯一键
     */
    @Nonnull
    public static String getMethodUniqueKey(@Nonnull Method method) {
        return ToolBytecode.getMethodUniqueKey(method, false);
    }

    /**
     * 获取方法的唯一键，结构为:
     *
     * <pre>
     *     返回类型#类全限定名.方法名:参数1类型,参数2类型...
     * </pre>
     *
     * @param method 方法
     * @return 方法唯一键
     */
    @Nonnull
    public static String getMethodUniqueKey(@Nonnull Method method, boolean isFullyQualifiedName) {
        TextBuilder sb = TextBuilder.of(isFullyQualifiedName ? 200 : 80);
        sb.append(method.getReturnType().getName()).append('#');
        if (isFullyQualifiedName) {
            sb.append(method.getDeclaringClass().getName()).append(".");
        }
        sb.append(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length > 0) {
            sb.append(':').append(parameters[0].getName());
            for (int i = 1; i < parameters.length; i++) {
                sb.append(',').append(parameters[i].getName());
            }
        }
        return sb.toString();
    }

    /**
     * 查找指定对象中的所有方法（包括非public方法），也包括父对象和Object类的方法
     *
     * <p>此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     *
     * @param object     被查找的对象，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param args       参数
     * @return 方法
     */
    @Nonnull
    public static Method[] getPublicMethods(@Nonnull Object object, @Nonnull String methodName, boolean ignoreCase, @Nullable Object... args) {
        return ToolBytecode.getPublicMethods(object instanceof Class ? (Class<?>) object : object.getClass(), methodName, ignoreCase, ToolBytecode.getClasses(args));
    }

    /**
     * 查找指定方法 如果找不到对应的方法则返回{@code null}<br>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。<br>
     * 如果查找的方法有多个同参数类型重载，查找第一个找到的方法
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param ignoreCase 是否忽略大小写
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return {@code Method[]}
     */
    @Nonnull
    public static Method[] getPublicMethods(
            @Nonnull Class<?> clazz, @Nonnull String methodName, boolean ignoreCase, @Nullable Class<?>[] paramTypes) {
        return Arrays.stream(ToolBytecode.getPublicMethods(clazz, methodName, ignoreCase))
                .filter(method -> ToolBytecode.isCompatibilityFrom(method.getParameterTypes(), paramTypes))
                .toArray(Method[]::new);
    }

    /**
     * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回
     *
     * <p>此方法只检查方法名是否一致，并不检查参数的一致性。
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param ignoreCase 是否忽略大小写
     * @return {@code Method[]}
     */
    @Nonnull
    public static Method[] getPublicMethods(@Nonnull Class<?> clazz, @Nonnull String methodName, boolean ignoreCase) {
        return ToolBytecode.getPublicMethods(clazz, method -> ignoreCase
                ? method.getName().equalsIgnoreCase(methodName)
                : method.getName().equals(methodName));
    }

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz  查找方法的类
     * @param filter 过滤器
     * @return 过滤后的方法列表
     */
    @Nonnull
    public static Method[] getPublicMethods(@Nonnull Class<?> clazz, @Nonnull Predicate<Method> filter) {
        return Arrays.stream(ToolBytecode.getPublicMethods(clazz)).filter(filter).toArray(Method[]::new);
    }

    /**
     * 获得一个类中所有方法列表，包括其父类中的方法
     *
     * @param clazz 类，非{@code null}
     * @return 方法列表
     */
    @Nonnull
    public static Method[] getPublicMethods(@Nonnull Class<?> clazz) {
        return Arrays.stream(ToolBytecode.getMethods(clazz)).filter(Whether::publicModifier).toArray(Method[]::new);
    }

    /**
     * 查找指定对象中的所有方法（包括非public方法），也包括父对象和Object类的方法
     *
     * <p>此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     *
     * @param object     被查找的对象，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param args       参数
     * @return 方法
     */
    @Nonnull
    public static Method[] getMethods(@Nonnull Object object, @Nonnull String methodName, boolean ignoreCase, @Nullable Object... args) {
        return ToolBytecode.getMethods(object instanceof Class ? (Class<?>) object : object.getClass(), methodName, ignoreCase, ToolBytecode.getClasses(args));
    }

    /**
     * 查找指定方法 如果找不到对应的方法则返回{@code null}<br>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。<br>
     * 如果查找的方法有多个同参数类型重载，查找第一个找到的方法
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param ignoreCase 是否忽略大小写
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return {@code Method[]}
     */
    @Nonnull
    public static Method[] getMethods(
            @Nonnull Class<?> clazz, @Nonnull String methodName, boolean ignoreCase, @Nullable Class<?>[] paramTypes) {
        return Arrays.stream(ToolBytecode.getMethods(clazz, methodName, ignoreCase))
                .filter(method -> ToolBytecode.isCompatibilityFrom(method.getParameterTypes(), paramTypes))
                .toArray(Method[]::new);
    }

    /**
     * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回
     *
     * <p>此方法只检查方法名是否一致，并不检查参数的一致性。
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param ignoreCase 是否忽略大小写
     * @return {@code Method[]}
     */
    @Nonnull
    public static Method[] getMethods(@Nonnull Class<?> clazz, @Nonnull String methodName, boolean ignoreCase) {
        return ToolBytecode.getMethods(clazz, method -> ignoreCase
                ? method.getName().equalsIgnoreCase(methodName)
                : method.getName().equals(methodName));
    }

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz  查找方法的类
     * @param filter 过滤器
     * @return 过滤后的方法列表
     */
    @Nonnull
    public static Method[] getMethods(@Nonnull Class<?> clazz, @Nonnull Predicate<Method> filter) {
        return Arrays.stream(ToolBytecode.getMethods(clazz)).filter(filter).toArray(Method[]::new);
    }

    /**
     * 获得一个类中所有方法列表，包括其父类中的方法
     *
     * @param clazz 类，非{@code null}
     * @return 方法列表
     */
    @Nonnull
    public static Method[] getMethods(@Nonnull Class<?> clazz) {
        return METHODS_CACHE.computeIfAbsent(
                clazz, (clz) -> ToolBytecode.getMethodsDirectly(clazz, true, true));
    }

    /**
     * 获得一个类中所有方法列表，直接反射获取，无缓存<br>
     * 接口获取方法和默认方法，获取的方法包括：
     *
     * <ul>
     *   <li>本类中的所有方法（包括static方法）
     *   <li>父类中的所有方法（包括static方法）
     *   <li>Object中（包括static方法）
     * </ul>
     *
     * @param clazz                类或接口
     * @param withSupers           是否包括父类或接口的方法列表
     * @param withMethodFromObject 是否包括Object中的方法
     * @return 方法列表
     */
    @Nonnull
    private static Method[] getMethodsDirectly(
            @Nonnull Class<?> clazz, boolean withSupers, boolean withMethodFromObject) {
        if (clazz.isInterface()) {
            // 对于接口，直接调用Class.getMethods方法获取所有方法，因为接口都是public方法
            return withSupers ? clazz.getMethods() : clazz.getDeclaredMethods();
        }

        final UniqueKeySet<String, Method> result = new UniqueKeySet<>(true, ToolBytecode::getMethodUniqueKey);
        Class<?> searchType = clazz;
        while (searchType != null && (withMethodFromObject || Object.class != searchType)) {

            for (Method declaredMethod : searchType.getDeclaredMethods()) {
                result.addIfAbsent(declaredMethod);
            }

            for (Class<?> ifc : searchType.getInterfaces()) {
                for (Method m : ifc.getMethods()) {
                    if (!Whether.abstractModifier(m)) {
                        result.addIfAbsent(m);
                    }
                }
            }
            searchType = (withSupers && !searchType.isInterface()) ? searchType.getSuperclass() : null;
        }
        return result.toArray(new Method[0]);
    }

    /**
     * 执行对象中指定方法
     *
     * @param <T>        返回对象类型
     * @param object     方法所在对象
     * @param methodName 方法名
     * @param args       参数列表
     * @return 执行结果
     */
    public static <T> T invokeSilent(@Nonnull Object object, @Nonnull String methodName, @Nonnull Object... args) {
        try {
            return ToolBytecode.invoke(object, methodName, args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行方法
     *
     * <p>对于用户传入参数会做必要检查，包括：
     *
     * <pre>
     *     1、忽略多余的参数
     *     2、参数不够补齐默认值
     *     3、传入参数为null，但是目标参数类型为原始类型，做转换
     * </pre>
     *
     * @param <T>    返回对象类型
     * @param object 对象，如果执行静态方法，此值为{@code null}
     * @param method 方法（对象方法或static方法都可）
     * @param args   参数对象
     * @return 结果
     * @throws InvocationTargetRuntimeException 目标方法执行异常
     */
    public static <T> T invokeSilent(@Nullable Object object, @Nonnull Method method, @Nonnull Object... args) {
        try {
            return ToolBytecode.invoke(object, method, args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行对象中指定方法
     *
     * @param <T>        返回对象类型
     * @param object     方法所在对象
     * @param methodName 方法名
     * @param args       参数列表
     * @return 执行结果
     */
    public static <T> T invoke(@Nonnull Object object, @Nonnull String methodName, @Nonnull Object... args)
            throws Throwable {
        final Method[] methods = ToolBytecode.getMethods(object, methodName, false, args);
        if (methods.length == 0) {
            throw new IllegalArgumentException(String.format("No such method: [%s] from [%s]", methodName, object.getClass()));
        }
        return ToolBytecode.invoke(object instanceof Class || Whether.staticModifier(methods[0]) ? null : object, methods[0], args);
    }

    /**
     * 执行方法
     *
     * <p>对于用户传入参数会做必要检查，包括：
     *
     * <pre>
     *     1、忽略多余的参数
     *     2、参数不够补齐默认值
     *     3、传入参数为null，但是目标参数类型为原始类型，做转换
     * </pre>
     *
     * @param <T>    返回对象类型
     * @param object 对象，如果执行静态方法，此值为{@code null}
     * @param method 方法（对象方法或static方法都可）
     * @param args   参数对象
     * @return 结果
     * @throws InvocationTargetException 目标方法执行异常
     * @throws IllegalAccessException    访问异常
     */
    public static <T> T invoke(@Nullable Object object, @Nonnull Method method, @Nonnull Object... args) throws Throwable {

        if (!Whether.staticModifier(method)) {
            if (object == null) {
                throw new IllegalArgumentException("Due to field not static and object must be not null");
            }
            if (!ToolBytecode.isCompatibilityFrom(method.getDeclaringClass(), object.getClass())) {
                throw new IllegalArgumentException(String.format("The [%s] not include [%s] method", object.getClass(), method));
            }
        }

        Class<?>[] realParameterTypes = method.getParameterTypes();

        Class<?>[] parameterTypes = ToolBytecode.getClasses(args);

        if (!ToolBytecode.isCompatibilityFrom(realParameterTypes, parameterTypes)) {
            throw new IllegalArgumentException(String.format("The method parameter [%s] and input parameter [%s] not compatibility",
                    Arrays.toString(realParameterTypes), Arrays.toString(parameterTypes)));
        }

        method.setAccessible(true);

        if (method.isDefault()) {
            // 当方法是default方法时，尤其对象是代理对象，需使用句柄方式执行
            // 代理对象情况下调用method.invoke会导致循环引用执行，最终栈溢出
            final Class<?> declaringClass = method.getDeclaringClass();
            final MethodHandles.Lookup lookup = LookupFactory.lookup(declaringClass);
            MethodHandle handle = lookup.unreflectSpecial(method, declaringClass);
            if (object != null) {
                handle = handle.bindTo(object);
            }
            return (T) handle.invokeWithArguments(args);
        }

        return (T) method.invoke(Whether.staticModifier(method) ? null : object, args);
    }

    //------------------------------------------------------------------------------------------------------------------
    //Field
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 查找指定类中是否包含指定名称对应的字段，包括所有字段（包括非public字段），也包括父类和Object类的字段
     *
     * @param clazz     被查找字段的类,不能为null
     * @param fieldName 字段名
     * @return 是否包含字段
     */
    public static boolean hasField(@Nonnull Class<?> clazz, @Nonnull String fieldName) throws SecurityException {
        return ToolBytecode.getNoStaticFields(clazz, fieldName, false).length > 0;
    }

    /**
     * 获取方法的唯一键，结构为:
     *
     * <pre>
     *     类全限定名.属性名
     * </pre>
     *
     * @param field 方法
     * @return 方法唯一键
     */
    @Nonnull
    public static String getFieldUniqueKey(@Nonnull Field field) {
        return ToolBytecode.getFieldUniqueKey(field, false);
    }

    /**
     * 获取方法的唯一键，结构为:
     *
     * <pre>
     *     类全限定名.属性名
     * </pre>
     *
     * @param field 方法
     * @return 方法唯一键
     */
    @Nonnull
    public static String getFieldUniqueKey(@Nonnull Field field, boolean isFullyQualifiedName) {
        TextBuilder sb = TextBuilder.of(isFullyQualifiedName ? 200 : 80);
        if (isFullyQualifiedName) {
            sb.append(field.getDeclaringClass().getName()).append(".");
        }
        sb.append(field.getName());
        return sb.toString();
    }

    /**
     * 获取指定类中字段名和字段对应的有序Map，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param clazz 类
     * @return 字段名和字段对应的Map，有序
     */
    public static Map<String, Field> getFieldMap(@Nonnull Class<?> clazz) {
        Field[] fields = ToolBytecode.getFields(clazz);
        final Map<String, Field> map = New.map(true, fields.length);
        for (Field field : fields) {
            map.put(getFieldUniqueKey(field, true), field);
        }
        return map;
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param object     对象或者类
     * @param fieldName  域名
     * @param ignoreCase 是否忽略大小写
     * @return {@code Field[]}
     */
    @Nonnull
    public static Field[] getStaticFields(@Nonnull Object object, @Nonnull String fieldName, boolean ignoreCase) {
        return ToolBytecode.getStaticFields(object instanceof Class ? (Class<?>) object : object.getClass(), fieldName, ignoreCase);
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param clazz      类
     * @param fieldName  域名
     * @param ignoreCase 是否忽略大小写
     * @return {@code Field[]}
     */
    @Nonnull
    public static Field[] getStaticFields(@Nonnull Class<?> clazz, @Nonnull String fieldName, boolean ignoreCase) {
        return ToolBytecode.getStaticFields(clazz, method -> ignoreCase
                ? method.getName().equalsIgnoreCase(fieldName)
                : method.getName().equals(fieldName));
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param clazz 类
     * @return {@code Field[]}
     */
    @Nonnull
    public static Field[] getStaticFields(@Nonnull Class<?> clazz, @Nonnull Predicate<Field> filter) {
        return Arrays.stream(ToolBytecode.getStaticFields(clazz)).filter(filter).toArray(Field[]::new);
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param clazz 类
     * @return {@code Field[]}
     */
    @Nonnull
    public static Field[] getStaticFields(@Nonnull Class<?> clazz) {
        return Arrays.stream(ToolBytecode.getFields(clazz)).filter(Whether::staticModifier).toArray(Field[]::new);
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param object     对象或者类
     * @param fieldName  域名
     * @param ignoreCase 是否忽略大小写
     * @return {@code Field[]}
     */
    @Nonnull
    public static Field[] getNoStaticFields(@Nonnull Object object, @Nonnull String fieldName, boolean ignoreCase) {
        return ToolBytecode.getNoStaticFields(object instanceof Class ? (Class<?>) object : object.getClass(), fieldName, ignoreCase);
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param clazz      类
     * @param fieldName  域名
     * @param ignoreCase 是否忽略大小写
     * @return {@code Field[]}
     */
    @Nonnull
    public static Field[] getNoStaticFields(@Nonnull Class<?> clazz, @Nonnull String fieldName, boolean ignoreCase) {
        return ToolBytecode.getNoStaticFields(clazz, method -> ignoreCase
                ? method.getName().equalsIgnoreCase(fieldName)
                : method.getName().equals(fieldName));
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param clazz 类
     * @return {@code Field[]}
     */
    @Nonnull
    public static Field[] getNoStaticFields(@Nonnull Class<?> clazz, @Nonnull Predicate<Field> filter) {
        return Arrays.stream(ToolBytecode.getNoStaticFields(clazz)).filter(filter).toArray(Field[]::new);
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param clazz 类
     * @return {@code Field[]}
     */
    @Nonnull
    public static Field[] getNoStaticFields(@Nonnull Class<?> clazz) {
        return Arrays.stream(ToolBytecode.getFields(clazz)).filter(field -> !Whether.staticModifier(field)).toArray(Field[]::new);
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param object     对象或者类
     * @param fieldName  域名
     * @param ignoreCase 是否忽略大小写
     * @return {@code Field[]}
     */
    @Nonnull
    public static Field[] getFields(@Nonnull Object object, @Nonnull String fieldName, boolean ignoreCase) {
        return ToolBytecode.getFields(object instanceof Class ? (Class<?>) object : object.getClass(), fieldName, ignoreCase);
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param clazz      类
     * @param fieldName  域名
     * @param ignoreCase 是否忽略大小写
     * @return {@code Field[]}
     */
    @Nonnull
    public static Field[] getFields(@Nonnull Class<?> clazz, @Nonnull String fieldName, boolean ignoreCase) {
        return ToolBytecode.getFields(clazz, method -> ignoreCase
                ? method.getName().equalsIgnoreCase(fieldName)
                : method.getName().equals(fieldName));
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param clazz  类
     * @param filter 过滤器
     * @return {@code Field[]}
     */
    @Nonnull
    public static Field[] getFields(@Nonnull Class<?> clazz, @Nonnull Predicate<Field> filter) {
        return Arrays.stream(ToolBytecode.getFields(clazz)).filter(filter).toArray(Field[]::new);
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param clazz 类
     * @return {@code Field[]}
     */
    @Nonnull
    public static Field[] getFields(@Nonnull Class<?> clazz) {
        return FIELDS_CACHE.computeIfAbsent(clazz, (clz) -> ToolBytecode.getFieldsDirectly(clazz, true));
    }

    /**
     * 获得一个类中所有字段列表，直接反射获取，无缓存<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param clazz                类
     * @param withSuperClassFields 是否包括父类的字段列表
     * @return 字段列表
     */
    @Nonnull
    private static Field[] getFieldsDirectly(@Nonnull Class<?> clazz, boolean withSuperClassFields) {
        List<Field> allFields = new ArrayList<>();
        Class<?> searchType = clazz;
        while (searchType != null) {
            Collections.addAll(allFields, searchType.getDeclaredFields());
            searchType = withSuperClassFields ? searchType.getSuperclass() : null;
        }
        return allFields.toArray(Field[]::new);
    }

    /**
     * 获取字段值
     *
     * @param object    对象，如果static字段，此处为类
     * @param fieldName 字段名
     * @return 字段值
     */
    @Nullable
    public static Object getFieldValueAsSafeSilent(@Nonnull Object object, @Nonnull String fieldName) {
        try {
            return ToolBytecode.getFieldValueAsSafe(object, fieldName);
        } catch (Throwable e) {
            throw new RuntimeException("Error to get field value by reflect", e);
        }
    }

    /**
     * 获取字段值
     *
     * @param object 对象，static字段则此字段为null
     * @param field  字段
     * @return 字段值
     */
    @Nullable
    public static Object getFieldValueAsSafeSilent(@Nullable Object object, @Nonnull Field field) {
        try {
            return ToolBytecode.getFieldValueAsSafe(object, field);
        } catch (Throwable e) {
            throw new RuntimeException("Error to get field value by reflect", e);
        }
    }

    /**
     * 获取字段值
     *
     * @param object    对象，如果static字段，此处为类
     * @param fieldName 字段名
     * @return 字段值
     */
    @Nullable
    public static Object getFieldValueAsSafe(@Nonnull Object object, @Nonnull String fieldName) throws Throwable {
        Field[] fields = ToolBytecode.getFields(object, fieldName, false);
        if (fields.length == 0) {
            throw new IllegalArgumentException(String.format("No such fields: [%s] from [%s]", fields[0], object instanceof Class ? object : object.getClass()));
        }
        return ToolBytecode.getFieldValueAsSafe(object, fields[0]);
    }

    /**
     * 获取字段值
     *
     * @param object 对象，static字段则此字段为null
     * @param field  字段
     * @return 字段值
     */
    @Nullable
    public static Object getFieldValueAsSafe(@Nonnull Object object, @Nonnull Field field) throws Throwable {
        if (object != null && !(object instanceof Class) && !Whether.staticModifier(field)) {
            if (!ToolBytecode.isCompatibilityFrom(field.getDeclaringClass(), object.getClass())) {
                throw new IllegalArgumentException(String.format("The [%s] not include [%s] field", object.getClass(), field));
            }
            String name = Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
            Method[] publicMethods = ToolBytecode.getPublicMethods(object.getClass(), "get" + name, false);
            Class<?> type = field.getType();
            if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
                if (publicMethods.length != 0) {
                    publicMethods = ToolBytecode.getPublicMethods(object.getClass(), "is" + name, false);
                }
            }
            if (publicMethods.length != 0) {
                return ToolBytecode.invoke(object, publicMethods[0]);
            }
        }
        return ToolBytecode.getFieldValue(object, field);

    }

    /**
     * 获取字段值
     *
     * @param object    对象，如果static字段，此处为类
     * @param fieldName 字段名
     * @return 字段值
     */
    @Nullable
    public static Object getFieldValue(@Nonnull Object object, @Nonnull String fieldName) throws IllegalAccessException {
        Field[] fields = ToolBytecode.getFields(object, fieldName, false);
        if (fields.length == 0) {
            throw new IllegalArgumentException(String.format("No such fields: [%s] from [%s]", fields[0], object instanceof Class ? object : object.getClass()))
                    ;
        }
        return ToolBytecode.getFieldValue(object, fields[0]);
    }

    /**
     * 获取字段值
     *
     * @param object 对象，static字段则此字段为null
     * @param field  字段
     * @return 字段值
     */
    @Nullable
    public static Object getFieldValue(@Nonnull Object object, @Nonnull Field field) throws IllegalAccessException {
        field.setAccessible(true);
        if (Whether.staticModifier(field)) {
            return field.get(null);
        } else {
            if (object == null) {
                throw new IllegalArgumentException("Due to field not static and object must be not null");
            }
            //这个属性域不在该对象上
            if (!ToolBytecode.isCompatibilityFrom(field.getDeclaringClass(), object.getClass())) {
                throw new IllegalArgumentException(String.format("The [%s] not include [%s] field", object.getClass(), field));
            }
            return field.get(object);
        }
    }

    /**
     * 设置字段值
     *
     * @param object    对象,static字段则此处传Class
     * @param fieldName 字段名
     * @param value     值，值类型必须与字段类型匹配，不会自动转换对象类型
     */
    public static void setFieldValueAsSafeSilent(@Nonnull Object object, @Nonnull String fieldName, @Nullable Object value) {
        try {
            ToolBytecode.setFieldValueAsSafe(object, fieldName, value);
        } catch (Throwable e) {
            throw new RuntimeException("Error to set field value by reflect", e);
        }
    }

    /**
     * 设置字段值
     *
     * @param object 对象，如果是static字段，此参数为null
     * @param field  字段
     * @param value  值，值类型必须与字段类型匹配，不会自动转换对象类型
     */
    public static void setFieldValueAsSafeSilent(@Nullable Object object, @Nonnull Field field, @Nullable Object value) {
        try {
            ToolBytecode.setFieldValueAsSafe(object, field, value);
        } catch (Throwable e) {
            throw new RuntimeException("Error to set field value by reflect", e);
        }
    }

    /**
     * 设置字段值，当存在方法时通过方法获取
     *
     * @param object    对象,static字段则此处传Class
     * @param fieldName 字段名
     * @param value     值，值类型必须与字段类型匹配，不会自动转换对象类型
     */
    public static void setFieldValueAsSafe(@Nonnull Object object, @Nonnull String fieldName, @Nullable Object value) throws Throwable {
        Field[] fields = ToolBytecode.getFields(object, fieldName, false);
        if (fields.length == 0) {
            throw new IllegalArgumentException(String.format("No such fields: [%s] from [%s]", fields[0], object instanceof Class ? object : object.getClass()));
        }
        ToolBytecode.setFieldValueAsSafe(object, fields[0], value);
    }

    /**
     * 设置字段值，当存在方法时通过方法获取
     *
     * @param object 对象，如果是static字段，此参数为null
     * @param field  字段
     * @param value  值，值类型必须与字段类型匹配，不会自动转换对象类型
     */
    public static void setFieldValueAsSafe(@Nullable Object object, @Nonnull Field field, @Nullable Object value) throws Throwable {
        if (value != null && !field.getType().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("The value not match field type");
        }
        if (object != null && !(object instanceof Class) && !Whether.staticModifier(field)) {
            if (!ToolBytecode.isCompatibilityFrom(field.getDeclaringClass(), object.getClass())) {
                throw new IllegalArgumentException(String.format("The [%s] not include [%s] field", object.getClass(), field));
            }
            Method[] publicMethods = ToolBytecode.getPublicMethods(object.getClass(),
                    "set" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1), false, field.getType());
            if (publicMethods.length != 0) {
                ToolBytecode.invoke(object, publicMethods[0], value);
            }
        }
        ToolBytecode.setFieldValue(object, field, value);
    }

    /**
     * 设置字段值
     *
     * @param object    对象,static字段则此处传Class
     * @param fieldName 字段名
     * @param value     值，值类型必须与字段类型匹配，不会自动转换对象类型
     */
    public static void setFieldValue(@Nonnull Object object, @Nonnull String fieldName, @Nullable Object value) throws IllegalAccessException {
        Field[] fields = ToolBytecode.getFields(object, fieldName, false);
        if (fields.length == 0) {
            throw new IllegalArgumentException(String.format("No such fields: [%s] from [%s]", fields[0], object instanceof Class ? object : object.getClass()));
        }
        ToolBytecode.setFieldValue(object, fields[0], value);
    }

    /**
     * 设置字段值
     *
     * @param object 对象，如果是static字段，此参数为null
     * @param field  字段
     * @param value  值，值类型必须与字段类型匹配，不会自动转换对象类型
     */
    public static void setFieldValue(@Nullable Object object, @Nonnull Field field, @Nullable Object value) throws IllegalAccessException {
        if (value != null && !field.getType().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("The value not match field type");
        }
        field.setAccessible(true);
        if (Whether.staticModifier(field)) {
            field.set(null, value);
        } else {
            if (object == null) {
                throw new IllegalArgumentException("Due to field not static and object must be not null");
            }
            //这个属性域不在该对象上
            if (!ToolBytecode.isCompatibilityFrom(field.getDeclaringClass(), object.getClass())) {
                throw new IllegalArgumentException(String.format("The [%s] not include [%s] field", object.getClass(), field));
            }
            field.set(object, value);
        }
    }
}

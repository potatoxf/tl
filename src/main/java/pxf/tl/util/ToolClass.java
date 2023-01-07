package pxf.tl.util;


import pxf.tl.api.*;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.exception.UtilException;
import pxf.tl.function.BiConsumerThrow;
import pxf.tl.function.BiPredicateThrow;
import pxf.tl.function.FunctionThrow;
import pxf.tl.function.SupplierThrow;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.io.FileUtil;
import pxf.tl.io.resource.ResourceUtil;
import pxf.tl.iter.ParentIterableOnClass;
import pxf.tl.lang.ClassScanner;
import pxf.tl.lang.ParameterizedTypeImpl;
import pxf.tl.lang.reflect.ActualTypeMapperPool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 针对 {@link Type} 的工具类封装<br>
 * 最主要功能包括：
 *
 * <pre>
 * 1. 获取方法的参数和返回值类型（包括Type和Class）
 * 2. 获取泛型参数类型（包括对象的泛型参数或集合元素的泛型类型）
 * </pre>
 *
 * @author potatoxf
 */
public final class ToolClass {

    /**
     * 获取字段对应的Type类型<br>
     * 方法优先获取GenericType，获取不到则获取Type
     *
     * @param field 字段
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getType(Field field) {
        if (null == field) {
            return null;
        }
        return field.getGenericType();
    }

    /**
     * 获得Field对应的原始类
     *
     * @param field {@link Field}
     * @return 原始类，如果无法获取原始类，返回{@code null}
     */
    public static Class<?> getClass(Field field) {
        return null == field ? null : field.getType();
    }

    // ----------------------------------------------------------------------------------- Param Type

    /**
     * 获取方法的第一个参数类型<br>
     * 优先获取方法的GenericParameterTypes，如果获取不到，则获取ParameterTypes
     *
     * @param method 方法
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getFirstParamType(Method method) {
        return getParamType(method, 0);
    }

    /**
     * 获取方法的第一个参数类
     *
     * @param method 方法
     * @return 第一个参数类型，可能为{@code null}
     */
    public static Class<?> getFirstParamClass(Method method) {
        return getParamClass(method, 0);
    }

    /**
     * 获取方法的参数类型<br>
     * 优先获取方法的GenericParameterTypes，如果获取不到，则获取ParameterTypes
     *
     * @param method 方法
     * @param index  第几个参数的索引，从0开始计数
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getParamType(Method method, int index) {
        Type[] types = getParamTypes(method);
        if (null != types && types.length > index) {
            return types[index];
        }
        return null;
    }

    /**
     * 获取方法的参数类
     *
     * @param method 方法
     * @param index  第几个参数的索引，从0开始计数
     * @return 参数类，可能为{@code null}
     */
    public static Class<?> getParamClass(Method method, int index) {
        Class<?>[] classes = getParamClasses(method);
        if (null != classes && classes.length > index) {
            return classes[index];
        }
        return null;
    }

    /**
     * 获取方法的参数类型列表<br>
     * 优先获取方法的GenericParameterTypes，如果获取不到，则获取ParameterTypes
     *
     * @param method 方法
     * @return {@link Type}列表，可能为{@code null}
     * @see Method#getGenericParameterTypes()
     * @see Method#getParameterTypes()
     */
    public static Type[] getParamTypes(Method method) {
        return null == method ? null : method.getGenericParameterTypes();
    }

    /**
     * 解析方法的参数类型列表<br>
     * 依赖jre\lib\rt.jar
     *
     * @param method t方法
     * @return 参数类型类列表
     * @see Method#getGenericParameterTypes
     * @see Method#getParameterTypes
     */
    public static Class<?>[] getParamClasses(Method method) {
        return null == method ? null : method.getParameterTypes();
    }

    // ----------------------------------------------------------------------------------- Return Type

    /**
     * 获取方法的返回值类型<br>
     * 获取方法的GenericReturnType
     *
     * @param method 方法
     * @return {@link Type}，可能为{@code null}
     * @see Method#getGenericReturnType()
     * @see Method#getReturnType()
     */
    public static Type getReturnType(Method method) {
        return null == method ? null : method.getGenericReturnType();
    }

    /**
     * 解析方法的返回类型类列表
     *
     * @param method 方法
     * @return 返回值类型的类
     * @see Method#getGenericReturnType
     * @see Method#getReturnType
     */
    public static Class<?> getReturnClass(Method method) {
        return null == method ? null : method.getReturnType();
    }

    // ----------------------------------------------------------------------------------- Type
    // Argument

    /**
     * 获得给定类的第一个泛型参数
     *
     * @param type 被检查的类型，必须是已经确定泛型类型的类型
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getTypeArgument(Type type) {
        return getTypeArgument(type, 0);
    }

    /**
     * 获得给定类的泛型参数
     *
     * @param type  被检查的类型，必须是已经确定泛型类型的类
     * @param index 泛型类型的索引号，即第几个泛型类型
     * @return {@link Type}
     */
    public static Type getTypeArgument(Type type, int index) {
        final Type[] typeArguments = getTypeArguments(type);
        if (null != typeArguments && typeArguments.length > index) {
            return typeArguments[index];
        }
        return null;
    }

    /**
     * 获得指定类型中所有泛型参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     *
     * <p>通过此方法，传入B.class即可得到String
     *
     * @param type 指定类型
     * @return 所有泛型参数类型
     */
    public static Type[] getTypeArguments(Type type) {
        if (null == type) {
            return null;
        }

        final ParameterizedType parameterizedType = toParameterizedType(type);
        return (null == parameterizedType) ? null : parameterizedType.getActualTypeArguments();
    }

    /**
     * 将{@link Type} 转换为{@link ParameterizedType}<br>
     * {@link ParameterizedType}用于获取当前类或父类中泛型参数化后的类型<br>
     * 一般用于获取泛型参数具体的参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     *
     * <p>通过此方法，传入B.class即可得到B{@link ParameterizedType}，从而获取到String
     *
     * @param type {@link Type}
     * @return {@link ParameterizedType}
     */
    public static ParameterizedType toParameterizedType(Type type) {
        ParameterizedType result = null;
        if (type instanceof ParameterizedType) {
            result = (ParameterizedType) type;
        } else if (type instanceof Class) {
            final Class<?> clazz = (Class<?>) type;
            Type genericSuper = clazz.getGenericSuperclass();
            if (null == genericSuper || Object.class.equals(genericSuper)) {
                // 如果类没有父类，而是实现一些定义好的泛型接口，则取接口的Type
                final Type[] genericInterfaces = clazz.getGenericInterfaces();
                if (Whether.noEmpty(genericInterfaces)) {
                    // 默认取第一个实现接口的泛型Type
                    genericSuper = genericInterfaces[0];
                }
            }
            result = toParameterizedType(genericSuper);
        }
        return result;
    }

    /**
     * 是否未知类型<br>
     * type为null或者{@link TypeVariable} 都视为未知类型
     *
     * @param type Type类型
     * @return 是否未知类型
     */
    public static boolean isUnknown(Type type) {
        return null == type || type instanceof TypeVariable;
    }

    /**
     * 指定泛型数组中是否含有泛型变量
     *
     * @param types 泛型数组
     * @return 是否含有泛型变量
     */
    public static boolean hasTypeVariable(Type... types) {
        for (Type type : types) {
            if (type instanceof TypeVariable) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取泛型变量和泛型实际类型的对应关系Map，例如：
     *
     * <pre>
     *     T    cn.hutool.test.User
     *     E    java.lang.Integer
     * </pre>
     *
     * @param clazz 被解析的包含泛型参数的类
     * @return 泛型对应关系Map
     */
    public static Map<Type, Type> getTypeMap(Class<?> clazz) {
        return ActualTypeMapperPool.get(clazz);
    }

    /**
     * 获得泛型字段对应的泛型实际类型，如果此变量没有对应的实际类型，返回null
     *
     * @param type  实际类型明确的类
     * @param field 字段
     * @return 实际类型，可能为Class等
     */
    public static Type getActualType(Type type, Field field) {
        if (null == field) {
            return null;
        }
        return getActualType(Safe.value(type, field.getDeclaringClass()), field.getGenericType());
    }

    /**
     * 获得泛型变量对应的泛型实际类型，如果此变量没有对应的实际类型，返回null 此方法可以处理：
     *
     * <pre>
     *     1. 泛型化对象，类似于Map&lt;User, Key&lt;Long&gt;&gt;
     *     2. 泛型变量，类似于T
     * </pre>
     *
     * @param type         类
     * @param typeVariable 泛型变量，例如T等
     * @return 实际类型，可能为Class等
     */
    public static Type getActualType(Type type, Type typeVariable) {
        if (typeVariable instanceof ParameterizedType) {
            return getActualType(type, (ParameterizedType) typeVariable);
        }

        if (typeVariable instanceof TypeVariable) {
            return ActualTypeMapperPool.getActualType(type, (TypeVariable<?>) typeVariable);
        }

        // 没有需要替换的泛型变量，原样输出
        return typeVariable;
    }

    /**
     * 获得泛型变量对应的泛型实际类型，如果此变量没有对应的实际类型，返回null 此方法可以处理复杂的泛型化对象，类似于Map&lt;User, Key&lt;Long&gt;&gt;
     *
     * @param type              类
     * @param parameterizedType 泛型变量，例如List&lt;T&gt;等
     * @return 实际类型，可能为Class等
     */
    public static Type getActualType(Type type, ParameterizedType parameterizedType) {
        // 字段类型为泛型参数类型，解析对应泛型类型为真实类型，类似于List<T> a
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        // 泛型对象中含有未被转换的泛型变量
        if (ToolClass.hasTypeVariable(actualTypeArguments)) {
            actualTypeArguments = getActualTypes(type, parameterizedType.getActualTypeArguments());
            if (Whether.noEmpty(actualTypeArguments)) {
                // 替换泛型变量为实际类型，例如List<T>变为List<String>
                parameterizedType =
                        new ParameterizedTypeImpl(
                                actualTypeArguments,
                                parameterizedType.getOwnerType(),
                                parameterizedType.getRawType());
            }
        }

        return parameterizedType;
    }

    /**
     * 获得泛型变量对应的泛型实际类型，如果此变量没有对应的实际类型，返回null
     *
     * @param type          类
     * @param typeVariables 泛型变量数组，例如T等
     * @return 实际类型数组，可能为Class等
     */
    public static Type[] getActualTypes(Type type, Type... typeVariables) {
        return ActualTypeMapperPool.getActualTypes(type, typeVariables);
    }


    /**
     * {@code null}安全的获取对象类型
     *
     * @param <T> 对象类型
     * @param obj 对象，如果为{@code null} 返回{@code null}
     * @return 对象类型，提供对象如果为{@code null} 返回{@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(T obj) {
        return ((null == obj) ? null : (Class<T>) obj.getClass());
    }

    /**
     * 获得外围类<br>
     * 返回定义此类或匿名类所在的类，如果类本身是在包中定义的，返回{@code null}
     *
     * @param clazz 类
     * @return 外围类
     */
    public static Class<?> getEnclosingClass(Class<?> clazz) {
        return null == clazz ? null : clazz.getEnclosingClass();
    }

    /**
     * 是否为顶层类，即定义在包中的类，而非定义在类中的内部类
     *
     * @param clazz 类
     * @return 是否为顶层类
     */
    public static boolean isTopLevelClass(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return null == getEnclosingClass(clazz);
    }

    /**
     * 指定类是否与给定的类名相同
     *
     * @param clazz      类
     * @param className  类名，可以是全类名（包含包名），也可以是简单类名（不包含包名）
     * @param ignoreCase 是否忽略大小写
     * @return 指定类是否与给定的类名相同
     */
    public static boolean equals(Class<?> clazz, String className, boolean ignoreCase) {
        if (null == clazz || Whether.blank(className)) {
            return false;
        }
        if (ignoreCase) {
            return className.equalsIgnoreCase(clazz.getName())
                    || className.equalsIgnoreCase(clazz.getSimpleName());
        } else {
            return className.equals(clazz.getName()) || className.equals(clazz.getSimpleName());
        }
    }

    // ----------------------------------------------------------------------------------------- Scan
    // classes

    /**
     * 扫描指定包路径下所有包含指定注解的类
     *
     * @param packageName     包路径
     * @param annotationClass 注解类
     * @return 类集合
     * @see ClassScanner#scanPackageByAnnotation(String, Class)
     */
    public static Set<Class<?>> scanPackageByAnnotation(
            String packageName, final Class<? extends Annotation> annotationClass) {
        return ClassScanner.scanPackageByAnnotation(packageName, annotationClass);
    }

    /**
     * 扫描指定包路径下所有指定类或接口的子类或实现类
     *
     * @param packageName 包路径
     * @param superClass  父类或接口
     * @return 类集合
     * @see ClassScanner#scanPackageBySuper(String, Class)
     */
    public static Set<Class<?>> scanPackageBySuper(String packageName, final Class<?> superClass) {
        return ClassScanner.scanPackageBySuper(packageName, superClass);
    }

    /**
     * 扫描该包路径下所有class文件
     *
     * @return 类集合
     * @see ClassScanner#scanPackage()
     */
    public static Set<Class<?>> scanPackage() {
        return ClassScanner.scanPackage();
    }

    /**
     * 扫描该包路径下所有class文件
     *
     * @param packageName 包路径 com | com. | com.abs | com.abs.
     * @return 类集合
     * @see ClassScanner#scanPackage(String)
     */
    public static Set<Class<?>> scanPackage(String packageName) {
        return ClassScanner.scanPackage(packageName);
    }

    /**
     * 扫描包路径下满足class过滤器条件的所有class文件，<br>
     * 如果包路径为 com.abs + A.class 但是输入 abs会产生classNotFoundException<br>
     * 因为className 应该为 com.abs.A 现在却成为abs.A,此工具类对该异常进行忽略处理,有可能是一个不完善的地方，以后需要进行修改<br>
     *
     * @param packageName 包路径 com | com. | com.abs | com.abs.
     * @param classFilter class过滤器，过滤掉不需要的class
     * @return 类集合
     */
    public static Set<Class<?>> scanPackage(String packageName, Predicate<Class<?>> classFilter) {
        return ClassScanner.scanPackage(packageName, classFilter);
    }

    // -----------------------------------------------------------------------------------------
    // Method


    // ----------------------------------------------------------------------------------------- Field

    /**
     * 查找指定类中的所有字段（包括非public字段）， 字段不存在则返回{@code null}
     *
     * @param clazz     被查找字段的类
     * @param fieldName 字段名
     * @return 字段
     * @throws SecurityException 安全异常
     */
    public static Field getDeclaredField(Class<?> clazz, String fieldName) throws SecurityException {
        if (null == clazz || Whether.blank(fieldName)) {
            return null;
        }
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // e.printStackTrace();
        }
        return null;
    }

    /**
     * 查找指定类中的所有字段（包括非public字段)
     *
     * @param clazz 被查找字段的类
     * @return 字段
     * @throws SecurityException 安全异常
     */
    public static Field[] getDeclaredFields(Class<?> clazz) throws SecurityException {
        if (null == clazz) {
            return null;
        }
        return clazz.getDeclaredFields();
    }

    // -----------------------------------------------------------------------------------------
    // Classpath

    /**
     * 获得ClassPath，不解码路径中的特殊字符（例如空格和中文）
     *
     * @return ClassPath集合
     */
    public static Set<String> getClassPathResources() {
        return getClassPathResources(false);
    }

    /**
     * 获得ClassPath
     *
     * @param isDecode 是否解码路径中的特殊字符（例如空格和中文）
     * @return ClassPath集合
     */
    public static Set<String> getClassPathResources(boolean isDecode) {
        return getClassPaths(ToolString.EMPTY, isDecode);
    }

    /**
     * 获得ClassPath，不解码路径中的特殊字符（例如空格和中文）
     *
     * @param packageName 包名称
     * @return ClassPath路径字符串集合
     */
    public static Set<String> getClassPaths(String packageName) {
        return getClassPaths(packageName, false);
    }

    /**
     * 获得ClassPath
     *
     * @param packageName 包名称
     * @param isDecode    是否解码路径中的特殊字符（例如空格和中文）
     * @return ClassPath路径字符串集合
     */
    public static Set<String> getClassPaths(String packageName, boolean isDecode) {
        String packagePath = packageName.replace(PoolOfString.DOT, PoolOfString.SLASH);
        Enumeration<URL> resources;
        try {
            resources = getClassLoader().getResources(packagePath);
        } catch (IOException e) {
            throw new UtilException(e, "Loading classPath [{}] error!", packagePath);
        }
        final Set<String> paths = new HashSet<>();
        String path;
        while (resources.hasMoreElements()) {
            path = resources.nextElement().getPath();
            paths.add(isDecode ? ToolURL.decode(path, Charsets.defaultCharsets()) : path);
        }
        return paths;
    }

    /**
     * 获得ClassPath，将编码后的中文路径解码为原字符<br>
     * 这个ClassPath路径会文件路径被标准化处理
     *
     * @return ClassPath
     */
    public static String getClassPath() {
        return getClassPath(false);
    }

    /**
     * 获得ClassPath，这个ClassPath路径会文件路径被标准化处理
     *
     * @param isEncoded 是否编码路径中的中文
     * @return ClassPath
     */
    public static String getClassPath(boolean isEncoded) {
        final URL classPathURL = getClassPathURL();
        String url = isEncoded ? classPathURL.getPath() : ToolURL.getDecodedPath(classPathURL);
        return FileUtil.normalize(url);
    }

    /**
     * 获得ClassPath URL
     *
     * @return ClassPath URL
     */
    public static URL getClassPathURL() {
        return getResourceURL(ToolString.EMPTY);
    }

    /**
     * 获得资源的URL<br>
     * 路径用/分隔，例如:
     *
     * <pre>
     * config/a/db.config
     * spring/xml/test.xml
     * </pre>
     *
     * @param resource 资源（相对Classpath的路径）
     * @return 资源URL
     * @see ResourceUtil#getResource(String)
     */
    public static URL getResourceURL(String resource) throws IORuntimeException {
        return ResourceUtil.getResource(resource);
    }

    /**
     * 获取指定路径下的资源列表<br>
     * 路径格式必须为目录格式,用/分隔，例如:
     *
     * <pre>
     * config/a
     * spring/xml
     * </pre>
     *
     * @param resource 资源路径
     * @return 资源列表
     * @see ResourceUtil#getResources(String)
     */
    public static List<URL> getResources(String resource) {
        return ResourceUtil.getResources(resource);
    }

    /**
     * 获得资源相对路径对应的URL
     *
     * @param resource  资源相对路径
     * @param baseClass 基准Class，获得的相对路径相对于此Class所在路径，如果为{@code null}则相对ClassPath
     * @return {@link URL}
     * @see ResourceUtil#getResource(String, Class)
     */
    public static URL getResourceUrl(String resource, Class<?> baseClass) {
        return ResourceUtil.getResource(resource, baseClass);
    }

    /**
     * @return 获得Java ClassPath路径，不包括 jre
     */
    public static String[] getJavaClassPaths() {
        return System.getProperty("java.class.path").split(System.getProperty("path.separator"));
    }

    /**
     * 获取当前线程的{@link ClassLoader}
     *
     * @return 当前线程的class loader
     * @see ToolBytecode#getClassLoader()
     */
    public static ClassLoader getContextClassLoader() {
        return ToolBytecode.getContextClassLoader();
    }

    /**
     * 获取{@link ClassLoader}<br>
     * 获取顺序如下：<br>
     *
     * <pre>
     * 1、获取当前线程的ContextClassLoader
     * 2、获取{@link ToolBytecode}类对应的ClassLoader
     * 3、获取系统ClassLoader（{@link ClassLoader#getSystemClassLoader()}）
     * </pre>
     *
     * @return 类加载器
     */
    public static ClassLoader getClassLoader() {
        return ToolBytecode.getClassLoader();
    }

    /**
     * 是否简单值类型或简单值类型的数组<br>
     * 包括：原始类型,、String、other CharSequence, a Number, a Date, a URI, a URL, a Locale or a Class及其数组
     *
     * @param clazz 属性类
     * @return 是否简单值类型或简单值类型的数组
     */
    public static boolean isSimpleTypeOrArray(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return isSimpleValueType(clazz)
                || (clazz.isArray() && isSimpleValueType(clazz.getComponentType()));
    }

    /**
     * 是否为简单值类型<br>
     * 包括：
     *
     * <pre>
     *     原始类型
     *     String、other CharSequence
     *     Number
     *     Date
     *     URI
     *     URL
     *     Locale
     *     Class
     * </pre>
     *
     * @param clazz 类
     * @return 是否为简单值类型
     */
    public static boolean isSimpleValueType(Class<?> clazz) {
        return ToolBytecode.isBasicType(clazz) //
                || clazz.isEnum() //
                || CharSequence.class.isAssignableFrom(clazz) //
                || Number.class.isAssignableFrom(clazz) //
                || Date.class.isAssignableFrom(clazz) //
                || clazz.equals(URI.class) //
                || clazz.equals(URL.class) //
                || clazz.equals(Locale.class) //
                || clazz.equals(Class.class) //
                // jdk8 date object
                || TemporalAccessor.class.isAssignableFrom(clazz); //
    }

    /**
     * 检查目标类是否可以从原类转化<br>
     * 转化包括：<br>
     * 1、原类是对象，目标类型是原类型实现的接口<br>
     * 2、目标类型是原类型的父类<br>
     * 3、两者是原始类型或者包装类型（相互转换）
     *
     * @param targetType 目标类型
     * @param sourceType 原类型
     * @return 是否可转化
     */
    public static boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
        if (null == targetType || null == sourceType) {
            return false;
        }

        // 对象类型
        if (targetType.isAssignableFrom(sourceType)) {
            return true;
        }

        // 基本类型
        if (targetType.isPrimitive()) {
            // 原始类型
            Class<?> resolvedPrimitive = PoolOfObject.WRAPPER_TO_PRIMITIVE.get().get(sourceType);
            return targetType.equals(resolvedPrimitive);
        } else {
            // 包装类型
            Class<?> resolvedWrapper = PoolOfObject.PRIMITIVE_TO_WRAPPER.get().get(sourceType);
            return resolvedWrapper != null && targetType.isAssignableFrom(resolvedWrapper);
        }
    }

    /**
     * 设置方法为可访问
     *
     * @param method 方法
     * @return 方法
     */
    public static Method setAccessible(Method method) {
        if (null != method && false == method.isAccessible()) {
            method.setAccessible(true);
        }
        return method;
    }

    /**
     * 是否为抽象类
     *
     * @param clazz 类
     * @return 是否为抽象类
     */
    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * 是否为标准的类<br>
     * 这个类必须：
     *
     * <pre>
     * 1、非接口
     * 2、非抽象类
     * 3、非Enum枚举
     * 4、非数组
     * 5、非注解
     * 6、非原始类型（int, long等）
     * </pre>
     *
     * @param clazz 类
     * @return 是否为标准类
     */
    public static boolean isNormalClass(Class<?> clazz) {
        return null != clazz //
                && false == clazz.isInterface() //
                && false == isAbstract(clazz) //
                && false == clazz.isEnum() //
                && false == clazz.isArray() //
                && false == clazz.isAnnotation() //
                && false == clazz.isSynthetic() //
                && false == clazz.isPrimitive(); //
    }

    /**
     * 判断类是否为枚举类型
     *
     * @param clazz 类
     * @return 是否为枚举类型
     */
    public static boolean isEnum(Class<?> clazz) {
        return null != clazz && clazz.isEnum();
    }

    /**
     * 获得给定类的第一个泛型参数
     *
     * @param clazz 被检查的类，必须是已经确定泛型类型的类
     * @return {@link Class}
     */
    public static Class<?> getTypeArgument(Class<?> clazz) {
        return getTypeArgument(clazz, 0);
    }

    /**
     * 获得给定类的泛型参数
     *
     * @param clazz 被检查的类，必须是已经确定泛型类型的类
     * @param index 泛型类型的索引号，即第几个泛型类型
     * @return {@link Class}
     */
    public static Class<?> getTypeArgument(Class<?> clazz, int index) {
        final Type argumentType = ToolClass.getTypeArgument(clazz, index);
        return ToolBytecode.getClass(argumentType);
    }

    /**
     * 获得给定类所在包的名称<br>
     * 例如：<br>
     * com.xiaoleilu.hutool.util.ToolClass =》 com.xiaoleilu.hutool.util
     *
     * @param clazz 类
     * @return 包名
     */
    public static String getPackage(Class<?> clazz) {
        if (clazz == null) {
            return ToolString.EMPTY;
        }
        final String className = clazz.getName();
        int packageEndIndex = className.lastIndexOf(PoolOfString.DOT);
        if (packageEndIndex == -1) {
            return ToolString.EMPTY;
        }
        return className.substring(0, packageEndIndex);
    }

    /**
     * 获得给定类所在包的路径<br>
     * 例如：<br>
     * com.xiaoleilu.hutool.util.ToolClass =》 com/xiaoleilu/hutool/util
     *
     * @param clazz 类
     * @return 包名
     */
    public static String getPackagePath(Class<?> clazz) {
        return getPackage(clazz).replace(PoolOfCharacter.DOT, PoolOfCharacter.SLASH);
    }

    /**
     * 是否为JDK中定义的类或接口，判断依据：
     *
     * <pre>
     * 1、以java.、javax.开头的包名
     * 2、ClassLoader为null
     * </pre>
     *
     * @param clazz 被检查的类
     * @return 是否为JDK中定义的类或接口
     */
    public static boolean isJdkClass(Class<?> clazz) {
        final Package objectPackage = clazz.getPackage();
        if (null == objectPackage) {
            return false;
        }
        final String objectPackageName = objectPackage.getName();
        return objectPackageName.startsWith("java.") //
                || objectPackageName.startsWith("javax.") //
                || clazz.getClassLoader() == null;
    }

    /**
     * 获取class类路径URL, 不管是否在jar包中都会返回文件夹的路径<br>
     * class在jar包中返回jar所在文件夹,class不在jar中返回文件夹目录<br>
     * jdk中的类不能使用此方法
     *
     * @param clazz 类
     * @return URL
     */
    public static URL getLocation(Class<?> clazz) {
        if (null == clazz) {
            return null;
        }
        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }

    /**
     * 获取class类路径, 不管是否在jar包中都会返回文件夹的路径<br>
     * class在jar包中返回jar所在文件夹,class不在jar中返回文件夹目录<br>
     * jdk中的类不能使用此方法
     *
     * @param clazz 类
     * @return class路径
     */
    public static String getLocationPath(Class<?> clazz) {
        final URL location = getLocation(clazz);
        if (null == location) {
            return null;
        }
        return location.getPath();
    }

    /**
     * 是否为抽象类或接口
     *
     * @param clazz 类
     * @return 是否为抽象类或接口
     */
    public static boolean isAbstractOrInterface(Class<?> clazz) {
        return isAbstract(clazz) || isInterface(clazz);
    }

    /**
     * 是否为接口
     *
     * @param clazz 类
     * @return 是否为接口
     */
    public static boolean isInterface(Class<?> clazz) {
        return clazz.isInterface();
    }

    /**
     * 查找域
     *
     * @param target           目标
     * @param fieldName        域名
     * @param includeModifiers 包括修饰符
     * @param excludeModifiers 排除修饰符
     * @return {@code Field}
     */
    public static Field lookupField(
            @Nonnull Object target,
            @Nonnull String fieldName,
            @Nullable Integer includeModifiers,
            @Nullable Integer excludeModifiers) {
        final Class<?> clz = target instanceof Class ? (Class<?>) target : target.getClass();
        Field field = null;
        for (Class<?> c : new ParentIterableOnClass(clz)) {
            try {
                field = c.getDeclaredField(fieldName);
                if (Whether.matchModifiers(field, includeModifiers, excludeModifiers)) {
                    break;
                }
            } catch (NoSuchFieldException e) {
                field = null;
            }
        }
        return field;
    }

    /**
     * 获取当前属性
     *
     * @param clz              类
     * @param includeModifiers 包括修饰符
     * @param excludeModifiers 排除修饰符
     * @return {@code Field[]}
     */
    public static Field[] getContextFields(
            @Nonnull Class<?> clz,
            @Nullable Integer includeModifiers,
            @Nullable Integer excludeModifiers) {
        List<Field[]> list = new ArrayList<>();
        for (Class<?> c : new ParentIterableOnClass(clz)) {
            list.add(getCurrentFields(c, includeModifiers, excludeModifiers));
        }
        return list.stream().flatMap(Arrays::stream).toArray(Field[]::new);
    }

    /**
     * 获取当前属性
     *
     * @param clz              类
     * @param includeModifiers 包括修饰符
     * @param excludeModifiers 排除修饰符
     * @return {@code Field[]}
     */
    public static Field[] getCurrentFields(
            @Nonnull Class<?> clz,
            @Nullable Integer includeModifiers,
            @Nullable Integer excludeModifiers) {
        return Arrays.stream(clz.getDeclaredFields())
                .filter(field -> Whether.matchModifiers(field, includeModifiers, excludeModifiers))
                .toArray(Field[]::new);
    }

    /**
     * 获取可追踪域值
     *
     * @param target           目标，如果获取静态域传入Class，如果是对象域则传入对象
     * @param type             指定域类型，不是该类型则不获取，如果传入null则不管域类型
     * @param fieldName        域名
     * @param includeModifiers 包括修饰符
     * @param excludeModifiers 排除修饰符
     * @return 如果是域是final类型，则返回该域上的值，否则返回SupplierThrow
     */
    public static Object getTrackFieldValue(
            Object target,
            Class<?> type,
            @Nonnull String fieldName,
            @Nullable Integer includeModifiers,
            @Nullable Integer excludeModifiers) {
        Object value = null;
        if (target != null) {
            @Nullable final Object object = target instanceof Class ? null : target;
            @Nullable final Field field = lookupField(target, fieldName, includeModifiers, excludeModifiers);
            if (field != null) {
                try {
                    if (type.equals(field.getType()) || type.isAssignableFrom(field.getType())) {
                        int modifiers = field.getModifiers();
                        if (Modifier.isFinal(modifiers)) {
                            value =
                                    (SupplierThrow<Object, Throwable>)
                                            () -> {
                                                field.setAccessible(true);
                                                return field.get(object);
                                            };
                        } else {
                            field.setAccessible(true);
                            value = field.get(object);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return value;
    }


    /**
     * 默认的类加载器
     */
    private static final ClassLoader[] DEFAULT_CLASS_LOADER = getDefaultClassLoader();

    /**
     * 获取默认的类加载器
     *
     * @return the context class loader, may be null
     */
    public static ClassLoader classLoader() {
        return DEFAULT_CLASS_LOADER[0];
    }

    /**
     * Returns an array of class Loaders initialized from the specified array.
     *
     * <p>If the input is null or empty, it defaults to both {@link #classLoader()} and {@link
     * #classLoader()}
     *
     * @return the array of class loaders, not null
     */
    public static ClassLoader[] classLoaders(ClassLoader... classLoaders) {
        if (classLoaders == null || classLoaders.length == 0) {
            return contentClassLoader();
        } else {
            Set<ClassLoader> result = new LinkedHashSet<>();
            Collections.addAll(result, classLoaders);
            Collections.addAll(result, contentClassLoader());
            return result.toArray(new ClassLoader[0]);
        }
    }

    /**
     * 获取默认的类加载器
     *
     * @return {@code ClassLoader[]}
     */
    public static ClassLoader[] contentClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader == DEFAULT_CLASS_LOADER[0]) {
            return Arrays.copyOf(DEFAULT_CLASS_LOADER, DEFAULT_CLASS_LOADER.length);
        }
        Set<ClassLoader> result = new LinkedHashSet<>();
        ClassLoader classLoader = contextClassLoader;
        while (classLoader != null) {
            result.add(classLoader);
            classLoader = classLoader.getParent();
        }
        result.add(DEFAULT_CLASS_LOADER[0]);
        result.add(DEFAULT_CLASS_LOADER[1]);
        return result.toArray(new ClassLoader[0]);
    }

    /**
     * default class loader
     *
     * @return {@code ClassLoader[]}
     */
    public static ClassLoader[] defaultClassLoader() {
        return DEFAULT_CLASS_LOADER;
    }

    /**
     * 获取默认的类加载器
     *
     * @return {@code ClassLoader[]}
     */
    private static ClassLoader[] getDefaultClassLoader() {
        ClassLoader classLoader = null;
        try {
            classLoader = ClassLoader.getSystemClassLoader();
        } catch (SecurityException ignored) {
            // AccessControlException on Google App Engine
        }
        if (classLoader != null) {
            return new ClassLoader[]{classLoader, classLoader.getParent()};
        }
        throw new RuntimeException("No find default class loader");
    }

    /**
     * 获取所有类
     *
     * @param clz 类
     * @return 返回自身类和继承类
     */
    public static Set<Class<?>> getAllInterface(Class<?> clz) {
        Set<Class<?>> result = new HashSet<Class<?>>();
        List<Class<?>> allClass = getAllClass(clz);
        for (Class<?> aClass : allClass) {
            Collections.addAll(result, aClass.getInterfaces());
        }
        return result;
    }

    /**
     * 获取所有扩展类
     *
     * @param clz 类
     * @return {@code List<Class<?>>}
     */
    public static List<Class<?>> getAllClass(Class<?> clz) {
        List<Class<?>> allExtendClass = getAllExtendClass(clz);
        allExtendClass.add(clz);
        return allExtendClass;
    }

    /**
     * 获取所有扩展父类
     *
     * @param clz 类
     * @return {@code List<Class<?>>}
     */
    public static List<Class<?>> getAllExtendClass(Class<?> clz) {
        List<Class<?>> result = new ArrayList<Class<?>>();
        for (Class<?> c = clz.getSuperclass(); c != Object.class; c = c.getSuperclass()) {
            result.add(c);
        }
        return result;
    }

    /**
     * 将对象转为Class数组
     *
     * @param objects 对象数组
     * @return 返回Class数组
     */
    public static Class<?>[] toClassArray(Object... objects) {
        if (Whether.empty(objects)) {
            return PoolOfArray.EMPTY_CLASS_ARRAY;
        }
        Class<?>[] result = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                result[i] = objects[i].getClass();
            }
        }
        return result;
    }

    /**
     * 在类中查找指定注解
     *
     * @param clz             注解类型
     * @param annotationClass 注解类
     * @param <A>             注解类型
     * @return 返回注解，不存在返回null
     */
    public static <A extends Annotation> A lookupAnnotationOnClass(
            Class<?> clz, Class<A> annotationClass) {
        for (Class<?> c = clz; c != Object.class; c = c.getSuperclass()) {
            Annotation[] annotations = c.getDeclaredAnnotations();
            if (annotations != null && annotations.length > 0) {
                for (Annotation declaredAnnotation : annotations) {
                    if (declaredAnnotation instanceof Documented
                            || declaredAnnotation instanceof Retention
                            || declaredAnnotation instanceof Target
                            || declaredAnnotation instanceof Inherited) {
                        break;
                    }
                    if (declaredAnnotation.annotationType() == annotationClass) {
                        return (A) declaredAnnotation;
                    }
                    A result =
                            lookupAnnotationOnAnnotation(declaredAnnotation.annotationType(), annotationClass);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 在注解中查找指定注解
     *
     * @param clz             注解类型
     * @param annotationClass 注解类
     * @param <A>             注解类型
     * @return 返回注解，不存在返回null
     */
    public static <A extends Annotation> A lookupAnnotationOnAnnotation(
            Class<? extends Annotation> clz, Class<A> annotationClass) {
        Annotation[] annotations = clz.getDeclaredAnnotations();
        if (annotations != null && annotations.length > 0) {
            for (Annotation declaredAnnotation : annotations) {
                if (declaredAnnotation instanceof Documented
                        || declaredAnnotation instanceof Retention
                        || declaredAnnotation instanceof Target
                        || declaredAnnotation instanceof Inherited) {
                    break;
                }
                if (declaredAnnotation.annotationType() == annotationClass) {
                    return (A) declaredAnnotation;
                }
                A result =
                        lookupAnnotationOnAnnotation(declaredAnnotation.annotationType(), annotationClass);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
//
//    /**
//     * 过滤Class
//     *
//     * @param classes     {@code Iterable<Class<?>>}
//     * @param classFilter 字节过滤器
//     * @return {@code List<Class<?>>}
//     */
//    public static List<Class<?>> filterClass(Iterable<Class<?>> classes, ClassFilter classFilter) {
//        List<Class<?>> result = new LinkedList<Class<?>>();
//        for (@Nullable final Class<?> clz : classes) {
//            if (classFilter.specifyClass().length != 0
//                    && !Whether.matchOne(classFilter.specifyClass(), aClass -> aClass == clz)) {
//                continue;
//            }
//            if (classFilter.noSpecifyClass().length != 0
//                    && Whether.matchAll(classFilter.noSpecifyClass(), aClass -> aClass != clz)) {
//                continue;
//            }
//            if (classFilter.extendsClass().length != 0
//                    && !Whether.matchOne(classFilter.extendsClass(), aClass -> aClass.isAssignableFrom(clz))) {
//                continue;
//            }
//            if (classFilter.noExtendsClass().length != 0
//                    && Whether.matchAll(classFilter.extendsClass(), aClass -> !aClass.isAssignableFrom(clz))) {
//                continue;
//            }
//            if (classFilter.implementsInterface().length != 0
//                    && !Whether.matchOne(
//                    classFilter.implementsInterface(),
//                    aClass -> aClass.isInterface() && aClass.isAssignableFrom(clz))) {
//                continue;
//            }
//            if (classFilter.noImplementsInterface().length != 0
//                    && Whether.matchAll(
//                    classFilter.noImplementsInterface(),
//                    aClass -> aClass.isInterface() && !aClass.isAssignableFrom(clz))) {
//                continue;
//            }
//            if (classFilter.hasAnnotation().length != 0
//                    && !Whether.matchOne(
//                    classFilter.hasAnnotation(),
//                    aClass -> ToolClass.lookupAnnotationOnClass(clz, aClass) != null)) {
//                continue;
//            }
//            if (classFilter.noHasAnnotation().length != 0
//                    && Whether.matchAll(
//                    classFilter.noHasAnnotation(),
//                    aClass -> ToolClass.lookupAnnotationOnClass(clz, aClass) == null)) {
//                continue;
//            }
//            if (classFilter.matchPackagePattern().length != 0
//                    && !Whether.matchOne(
//                    classFilter.matchPackagePattern(), s -> clz.getPackage().getName().matches(s))) {
//                continue;
//            }
//            if (classFilter.noMatchPackagePattern().length != 0
//                    && Whether.matchAll(
//                    classFilter.noMatchPackagePattern(), s -> !clz.getPackage().getName().matches(s))) {
//                continue;
//            }
//            result.add(clz);
//        }
//        return result;
//    }

    /**
     * 获取可追踪域值
     *
     * @param target    目标，如果获取静态域传入Class，如果是对象域则传入对象
     * @param type      指定域类型，不是该类型则不获取，如果传入null则不管域类型
     * @param fieldName 域名
     * @return 如果是域是final类型，则返回该域上的值，否则返回SupplierThrow
     */
    public static Object obtainTrackFieldValue(Object target, Class<?> type, String fieldName) {
        Object value = null;
        if (target != null) {
            @Nullable final Object object = target instanceof Class ? null : target;
            @Nullable final Field field = findField(target, fieldName);
            if (field != null) {
                try {
                    if (type.equals(field.getType()) || type.isAssignableFrom(field.getType())) {
                        int modifiers = field.getModifiers();
                        if (Modifier.isFinal(modifiers)) {
                            value =
                                    (SupplierThrow<Object, Throwable>)
                                            () -> {
                                                field.setAccessible(true);
                                                return field.get(object);
                                            };
                        } else {
                            field.setAccessible(true);
                            value = field.get(object);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return value;
    }

    /**
     * 获取域
     *
     * @param enumerate 枚举类型
     * @return {@code Field}
     */
    public static Field getField(Enum<?> enumerate) {
        try {
            return enumerate.getClass().getDeclaredField(enumerate.name());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Impossible", e);
        }
    }

    /**
     * 获取域值
     *
     * @param target    目标对象
     * @param fieldName 域名
     * @param isForce   是否强制
     * @return 返回域上的对象值
     */
    public static Object getFieldValue(
            @Nullable final Object target, @Nullable final String fieldName, final boolean isForce) {
        if (target != null) {
            @Nullable final Object object = target instanceof Class ? null : target;
            Field field = findField(target, fieldName);
            if (field != null) {
                return getFieldValue(object, field, isForce);
            }
        }
        return null;
    }

    /**
     * 获取域值
     *
     * @param target  目标对象
     * @param field   域
     * @param isForce 是否强制
     * @return 返回域上的对象值
     */
    public static Object getFieldValue(
            @Nullable final Object target, @Nullable final Field field, final boolean isForce) {
        if (field == null) {
            throw new IllegalArgumentException("The attribute field must not be null");
        }
        int modifiers = field.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            try {
                if (!Modifier.isPublic(modifiers)) {
                    field.setAccessible(true);
                }
                return field.get(null);
            } catch (IllegalAccessException ignored) {
            }
        } else {
            Class<?> declaringClass = field.getDeclaringClass();
            if (target == null) {
                throw new IllegalArgumentException(
                        "The target object must not be null,because the field ["
                                + field.getName()
                                + "] isn't static field on ["
                                + declaringClass
                                + "]");
            }
            Class<?> clz = target.getClass();
            if (declaringClass.isAssignableFrom(clz)) {
                try {
                    String name = field.getName();
                    return clz.getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1))
                            .invoke(target);
                } catch (IllegalAccessException
                        | NoSuchMethodException
                        | InvocationTargetException ignored) {
                }
                if (isForce) {
                    try {
                        if (!Modifier.isPublic(modifiers)) {
                            field.setAccessible(true);
                        }
                        return field.get(target);
                    } catch (IllegalAccessException ignored) {
                    }
                }
            } else {
                throw new IllegalArgumentException(
                        "The type of the attribute field does not match the target object type");
            }
        }
        return null;
    }

    /**
     * 设置域值
     *
     * @param target    目标对象
     * @param fieldName 域名
     * @param isForce   是否强制
     * @return 是否设值成功
     */
    public static boolean setFieldValue(
            @Nullable final Object target,
            @Nullable final String fieldName,
            @Nullable final Object value,
            final boolean isForce) {
        if (target != null) {
            @Nullable final Object object = target instanceof Class ? null : target;
            Field field = findField(target, fieldName);
            if (field != null) {
                return setFieldValue(target, field, value, isForce);
            }
        }
        return false;
    }

    /**
     * 设置域值
     *
     * @param target  目标对象
     * @param field   域
     * @param isForce 是否强制
     * @return 返回域上的对象值
     */
    public static boolean setFieldValue(
            @Nullable final Object target,
            @Nullable final Field field,
            @Nullable final Object value,
            final boolean isForce) {
        if (field == null) {
            throw new IllegalArgumentException("The attribute field must not be null");
        }
        int modifiers = field.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            throw new IllegalArgumentException("The attribute field cannot be changed");
        }
        Class<?> type = field.getType();
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException(
                    "The value of the attribute field is inconsistent with the input value type");
        }
        if (Modifier.isStatic(modifiers)) {
            try {
                if (!Modifier.isPublic(modifiers)) {
                    field.setAccessible(true);
                }
                field.set(null, value);
                return true;
            } catch (IllegalAccessException ignored) {
            }
        } else {
            if (target == null) {
                throw new IllegalArgumentException("The target object must not be null");
            }
            Class<?> clz = target.getClass();
            Class<?> declaringClass = field.getDeclaringClass();
            if (declaringClass.isAssignableFrom(clz)) {
                try {
                    clz.getMethod(
                                    "set"
                                            + field.getName().substring(0, 1).toUpperCase()
                                            + field.getName().substring(1),
                                    type)
                            .invoke(target, value);
                    return true;
                } catch (IllegalAccessException
                        | InvocationTargetException
                        | NoSuchMethodException ignored) {
                }
                if (isForce) {
                    try {
                        if (!Modifier.isPublic(modifiers)) {
                            field.setAccessible(true);
                        }
                        field.set(target, value);
                        return true;
                    } catch (IllegalAccessException ignored) {
                    }
                }
            } else {
                throw new IllegalArgumentException(
                        "The type of the attribute field does not match the target object type");
            }
        }
        return false;
    }

    /**
     * 查找域
     *
     * @param target    目标
     * @param fieldName 域名
     * @return {@code Field}
     */
    public static Field findField(Object target, String fieldName) {
        final Class<?> clz = target instanceof Class ? (Class<?>) target : target.getClass();
        Field field = null;
        for (Class<?> c = clz; c != Object.class; c = c.getSuperclass()) {
            try {
                field = c.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                field = null;
            }
        }
        return field;
    }

    /**
     * 查找所有域
     *
     * @param target 目标对象或类型
     * @return {@code Field}
     */
    public static List<Field> findAllField(Object target) {
        final Class<?> clz = target instanceof Class ? (Class<?>) target : target.getClass();
        List<Field> fields = new LinkedList<Field>();
        for (Class<?> c = clz; c != Object.class; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    /**
     * invoke the given {@code method} with {@code args}, return either the result or an exception if
     * occurred
     *
     * @param method 方法
     * @param object 对象
     * @param args   参数
     * @return 方法返回值
     */
    public static Object invokeMethod(Method method, Object object, Object... args) {
        try {
            return method.invoke(object, args);
        } catch (Exception e) {
            return e;
        }
    }

    /**
     * 查找方法
     *
     * @param target     目标对象或类型
     * @param methodName 方法名
     * @return {@code Method}
     */
    public static Method findMethod(Object target, String methodName, Class<?>... argType) {
        final Class<?> clz = target instanceof Class ? (Class<?>) target : target.getClass();
        Method method = null;
        for (Class<?> c = clz; c != Object.class; c = c.getSuperclass()) {
            try {
                method = c.getDeclaredMethod(methodName, argType);
                break;
            } catch (NoSuchMethodException e) {
                method = null;
            }
        }
        return method;
    }

    /**
     * 查找方法
     *
     * @param target 目标对象或类型
     * @return {@code Method}
     */
    public static List<Method> findMethod(Object target) {
        final Class<?> clz = target instanceof Class ? (Class<?>) target : target.getClass();
        List<Method> methods = new LinkedList<Method>();
        for (Class<?> c = clz; c != Object.class; c = c.getSuperclass()) {
            methods.addAll(Arrays.asList(c.getDeclaredMethods()));
        }
        return methods;
    }

    /**
     * 复制值到设置器中
     *
     * @param srcBean   来源Bean
     * @param container 设置器
     */
    public static void copy(
            @Nullable final Object srcBean, @Nullable final Map<String, ? super Object> container) {
        copy(srcBean, (field, value) -> container.put(field.getName(), value));
    }

    /**
     * 复制值到设置器中
     *
     * @param srcBean  来源Bean
     * @param distBean 目标Bean
     */
    public static void copy(@Nonnull final Object srcBean, @Nonnull final Object distBean) {
        copy(srcBean, (field, object) -> setFieldValue(distBean, field, object, true));
    }

    /**
     * 复制值到设置器中
     *
     * @param srcBean 来源Bean
     * @param setter  设置器
     */
    public static void copy(
            @Nonnull final Object srcBean,
            @Nonnull final BiConsumerThrow<Field, Object, RuntimeException> setter) {
        List<Field> allField = findAllField(srcBean);
        for (Field field : allField) {
            setter.accept(field, getFieldValue(srcBean, field, true));
        }
    }

    /**
     * 复制值到Bean中
     *
     * @param distBean 目标Bean
     * @param src      获取器，参数：目标Bean的域。返回值：对象
     */
    public static void copyTo(
            @Nonnull final Object distBean, @Nonnull final Map<String, ? super Object> src) {
        copyTo(distBean, p -> src.get(p.getName()), (field, value) -> value != null);
    }

    /**
     * 复制值到Bean中
     *
     * @param distBean  目标Bean
     * @param getter    获取器，参数：目标Bean的域。返回值：对象
     * @param condition 条件器，参数：目标Bean的域，要设置的值。返回值：true则赋值，否则跳过
     */
    public static void copyTo(
            @Nonnull final Object distBean,
            @Nonnull final FunctionThrow<Field, Object, RuntimeException> getter,
            @Nonnull final BiPredicateThrow<Field, Object, Throwable> condition) {
        List<Field> allField = findAllField(distBean);
        for (Field field : allField) {
            try {
                Object value = getter.applyThrow(field);
                if (condition.test(field, value)) {
                    setFieldValue(distBean, field, value, true);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * 提取对象类必须继承指定类型并且等于指定泛型 {@code class C implements Comparable<C>}
     *
     * @param object        被提取对象
     * @param type          指定类型
     * @param genericLength 泛型长度
     * @param indexGeneric  要比较的泛型索引
     * @return 如果符合要求返回 {@code object}的 {@code Class}，否则返回 {@code null}
     */
    public static Class<?> classForImplementsGenericInterface(
            Object object, Class<?> type, int genericLength, int indexGeneric) {
        if (indexGeneric >= genericLength) {
            throw new IllegalArgumentException(
                    "Specify the generic position must be less than the generic length");
        }
        Class<?> objectClass = object.getClass();
        if (!type.isAssignableFrom(objectClass)) {
            return null;
        }
        Type[] genericInterfaces = objectClass.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (!(genericInterface instanceof ParameterizedType)) {
                continue;
            }
            ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
            Type rawType = parameterizedType.getRawType();
            if (rawType == type) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length != genericLength) {
                    throw new IllegalArgumentException(
                            "The specified generic length must be equal to the target type generic length");
                }
                if (actualTypeArguments[indexGeneric] == objectClass) {
                    return objectClass;
                }
            }
        }
        return null;
    }

    /**
     * @param type
     * @param isPublic
     * @param isFinal
     * @param <T>
     * @return
     */
    public static <T> List<T> getAllDeclaredStaticFieldsOfType(
            Class<T> type, boolean isPublic, boolean isFinal) {
        return filterAllStaticFields(type::getDeclaredFields, isPublic, isFinal).stream()
                .filter(type::isInstance)
                .map(o -> (T) o)
                .collect(Collectors.toList());
    }

    /**
     * @param type
     * @param isPublic
     * @param isFinal
     * @param <T>
     * @return
     */
    public static <T> List<T> getAllStaticFieldsOfType(
            Class<T> type, boolean isPublic, boolean isFinal) {
        return filterAllStaticFields(type::getFields, isPublic, isFinal).stream()
                .filter(type::isInstance)
                .map(o -> (T) o)
                .collect(Collectors.toList());
    }

    /**
     * @param filterSupplier
     * @param isPublic
     * @param isFinal
     * @param <T>
     * @return
     */
    public static <T> List<Object> filterAllStaticFields(
            Supplier<Field[]> filterSupplier, boolean isPublic, boolean isFinal) {
        return Arrays.stream(filterSupplier.get())
                .filter(
                        field -> {
                            int modifiers = field.getModifiers();
                            return (!isPublic || Modifier.isPublic(modifiers))
                                    && Modifier.isStatic(modifiers)
                                    && (!isFinal || Modifier.isFinal(modifiers));
                        })
                .map(
                        field -> {
                            try {
                                return field.get(null);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                .filter(Whether::noNvl)
                .collect(Collectors.toList());
    }

    /**
     * Returns a distinct collection of URLs based on URLs derived from class loaders.
     *
     * <p>This finds the URLs using {@link URLClassLoader#getURLs()} using the specified class loader,
     * searching up the parent hierarchy.
     *
     * <p>If the optional {@link ClassLoader}s are not specified, then both {@link #classLoader()} and
     * {@link #classLoader()} are used for {@link ClassLoader#getResources(String)}.
     *
     * <p>The returned URLs retains the order of the given {@code classLoaders}.
     *
     * @return the collection of URLs, not null
     */
    public static Collection<URL> forClassLoader(ClassLoader... classLoaders) {
        final Collection<URL> result = new ArrayList<>();
        final ClassLoader[] finalClassLoaders = classLoaders(classLoaders);
        for (ClassLoader classLoader : finalClassLoaders) {
            while (classLoader != null) {
                if (classLoader instanceof URLClassLoader) {
                    URL[] urls = ((URLClassLoader) classLoader).getURLs();
                    if (urls != null) {
                        result.addAll(Arrays.asList(urls));
                    }
                }
                classLoader = classLoader.getParent();
            }
        }
        return ToolCollection.distinctByString(result);
    }

    /**
     * Returns the URL that contains a {@code Class}.
     *
     * <p>This searches for the class using {@link ClassLoader#getResource(String)}.
     *
     * <p>If the optional {@link ClassLoader}s are not specified, then both {@link #classLoader()} and
     * {@link #classLoader()} are used for {@link ClassLoader#getResources(String)}.
     *
     * @return the URL containing the class, null if not found
     */
    public static URL forClass(Class<?> aClass, ClassLoader... classLoaders) {
        final ClassLoader[] loaders = classLoaders(classLoaders);
        final String resourceName = aClass.getName().replace(".", "/") + ".class";
        for (ClassLoader classLoader : loaders) {
            try {
                @Nullable final URL url = classLoader.getResource(resourceName);
                if (url != null) {
                    @Nullable final String normalizedUrl =
                            url.toExternalForm()
                                    .substring(
                                            0,
                                            url.toExternalForm()
                                                    .lastIndexOf(aClass.getPackage().getName().replace(".", "/")));
                    return new URL(normalizedUrl);
                }
            } catch (MalformedURLException e) {
                ToolLog.warn(e, () -> "Could not get URL");
            }
        }
        return null;
    }

    /**
     * Returns a distinct collection of URLs based on a package name.
     *
     * <p>This searches for the package name as a resource, using {@link
     * ClassLoader#getResources(String)}. For example, {@code forPackage(potatoxf.helper.reflections)}
     * effectively returns URLs from the classpath containing packages starting with {@code
     * potatoxf.helper.reflections}.
     *
     * <p>If the optional {@link ClassLoader}s are not specified, then both {@link #classLoader()} and
     * {@link #classLoader()} are used for {@link ClassLoader#getResources(String)}.
     *
     * <p>The returned URLs retainsthe order of the given {@code classLoaders}.
     *
     * @return the collection of URLs, not null
     */
    public static Collection<URL> forPackage(String name, ClassLoader... classLoaders) {
        return forResource(formatResourcePath(name), classLoaders);
    }

    /**
     * Returns a distinct collection of URLs based on a resource.
     *
     * <p>This searches for the resource name, using {@link ClassLoader#getResources(String)}. For
     * example, {@code forResource(test.properties)} effectively returns URLs from the classpath
     * containing files of that name.
     *
     * <p>If the optional {@link ClassLoader}s are not specified, then both {@link #classLoader()} and
     * {@link #classLoader()} are used for {@link ClassLoader#getResources(String)}.
     *
     * <p>The returned URLs retains the order of the given {@code classLoaders}.
     *
     * @return the collection of URLs, not null
     */
    public static Collection<URL> forResource(String resourceName, ClassLoader... classLoaders) {
        final List<URL> result = new ArrayList<>();
        final ClassLoader[] loaders = classLoaders(classLoaders);
        for (ClassLoader classLoader : loaders) {
            try {
                @Nullable final Enumeration<URL> urls = classLoader.getResources(resourceName);
                while (urls.hasMoreElements()) {
                    @Nullable final URL url = urls.nextElement();
                    int index = url.toExternalForm().lastIndexOf(resourceName);
                    if (index != -1) {
                        // Add old url as contextUrl to support exotic url handlers
                        result.add(new URL(url, url.toExternalForm().substring(0, index)));
                    } else {
                        result.add(url);
                    }
                }
            } catch (IOException e) {
                ToolLog.warn(e, () -> "Could not get URL");
            }
        }
        return ToolCollection.distinctByString(result);
    }

    /**
     * Returns a distinct collection of URLs based on the {@code java.class.path} system property.
     *
     * <p>This finds the URLs using the {@code java.class.path} system property.
     *
     * <p>The returned collection of URLs retains the classpath order.
     *
     * @return the collection of URLs, not null
     */
    public static Collection<URL> forJavaClassPath() {
        Collection<URL> urls = new ArrayList<>();
        if (JavaEnvironment.JAVA_CLASS_PATH != null) {
            for (String path : JavaEnvironment.JAVA_CLASS_PATH.split(File.pathSeparator)) {
                try {
                    urls.add(new File(path).toURI().toURL());
                } catch (Exception e) {
                    ToolLog.warn(e, () -> "Could not get URL");
                }
            }
        }
        return ToolCollection.distinctByString(urls);
    }

    /**
     * 格式化资源路径
     *
     * @param resource 资源
     * @return 返回符合要求的资源路径
     */
    public static String formatResourcePath(String resource) {
        if (resource != null) {
            return ToolString.clearPath(false, false, resource.replace(".", "/"));
        }
        return "";
    }

    // ---------------------------------------------------------------------------

    @Nonnull
    public static Class<?>[] getParameterTypes(@Nullable final Executable executable) {
        return executable != null ? executable.getParameterTypes() : PoolOfArray.EMPTY_CLASS_ARRAY;
    }

    @Nonnull
    public static Set<Annotation> getParameterAnnotations(@Nullable final Executable executable) {
        return Arrays.stream(
                        executable != null ? executable.getParameterAnnotations() : new Annotation[0][0])
                .flatMap(Arrays::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Nonnull
    public static Set<Annotation> getAnnotations(@Nullable final AnnotatedElement annotatedElement) {
        return Arrays.stream(
                        annotatedElement != null ? annotatedElement.getAnnotations() : new Annotation[0])
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Nonnull
    public static Set<Annotation> getDeclaredAnnotations(
            @Nullable final AnnotatedElement annotatedElement) {
        return Arrays.stream(
                        annotatedElement != null
                                ? annotatedElement.getDeclaredAnnotations()
                                : new Annotation[0])
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * where member name equals given {@code name}
     */
    public static boolean isMatchMemberName(
            @Nullable final Member member, @Nullable final String name) {
        return member != null && member.getName().equals(name);
    }

    /**
     * where member name startsWith given {@code prefix}
     */
    public static boolean isMatchMemberNamePrefix(
            @Nullable final Member member, @Nullable final String prefix) {
        return member != null && member.getName().startsWith(prefix);
    }

    /**
     * when field type equal given {@code type}
     */
    public static boolean isMatchFieldType(
            @Nullable final Field field, @Nullable final Class<?> type) {
        return field != null && field.getType().equals(type);
    }

    /**
     * when field type assignable to given {@code type}
     */
    public static boolean isAssignableFromFieldType(
            @Nullable final Field field, @Nullable final Class<?> type) {
        return field != null && type.isAssignableFrom(field.getType());
    }

    /**
     * when method return type equal given {@code type}
     */
    public static boolean isMatchMethodReturnType(
            @Nullable final Method method, @Nullable final Class<?> type) {
        return method != null && method.getReturnType().equals(type);
    }

    /**
     * when method return type assignable from given {@code type}
     */
    public static boolean isAssignableFromMethodReturnType(
            @Nullable final Method method, @Nullable final Class<?> type) {
        return method != null && type.isAssignableFrom(method.getReturnType());
    }

    /**
     * when method/constructor parameters count equal given {@code count}
     */
    public static boolean isMatchMethodParametersCount(
            @Nullable final Executable executable, final int count) {
        return ToolClass.getParameterTypes(executable).length == count;
    }

    /**
     * when method/constructor parameter types equals given {@code types}
     */
    public static boolean isMatchMethodParameters(
            @Nullable final Executable executable, @Nullable final Class<?>... types) {
        return Arrays.equals(getParameterTypes(executable), types);
    }

    /**
     * when member parameter types assignable to given {@code types}
     */
    public static boolean isAssignableToMethodParameters(
            @Nullable final Executable executable, @Nullable final Class<?>... types) {
        return isAssignable(types, getParameterTypes(executable));
    }

    /**
     * when method/constructor parameter types assignable from given {@code types}
     */
    public static boolean isAssignableFromMethodParameters(
            @Nullable final Executable executable, @Nullable final Class<?>... types) {
        return isAssignable(getParameterTypes(executable), types);
    }

    /**
     * when method/constructor has any parameter with an annotation matches given {@code annotations}
     */
    public static boolean isExistAnnotationOnParameter(
            @Nullable final Executable executable,
            @Nullable final Class<? extends Annotation> annotationClass) {
        return getParameterAnnotationTypes(executable).stream()
                .anyMatch(c -> c.equals(annotationClass));
    }

    /**
     * when method/constructor has any parameter with an annotation matches given {@code annotations},
     * including member matching
     */
    public static boolean isExistAnnotationOnParameter(
            @Nullable final Executable executable, @Nullable final Annotation annotation) {
        return getParameterAnnotations(executable).stream()
                .anyMatch(a -> isEqualAnnotationWithAllMember(a, annotation));
    }

    @Nonnull
    public static Set<Class<? extends Annotation>> getParameterAnnotationTypes(
            @Nullable final Executable executable) {
        return ToolCollection.toSet(getParameterAnnotations(executable), Annotation::annotationType);
    }

    public static boolean isEqualAnnotation(
            @Nullable final Annotation annotation, @Nullable final Annotation otherAnnotation) {
        return annotation != null
                && otherAnnotation != null
                && annotation.annotationType() == otherAnnotation.annotationType();
    }

    public static boolean isEqualAnnotations(
            @Nullable final Annotation[] annotation, @Nullable final Annotation[] otherAnnotation) {
        return ToolArray.equals(annotation, otherAnnotation, ToolClass::isEqualAnnotation);
    }

    public static boolean isEqualAnnotationWithAllMember(
            @Nullable final Annotation annotation, @Nullable final Annotation otherAnnotation) {
        if (isEqualAnnotation(annotation, otherAnnotation)) {
            for (Method method : annotation.annotationType().getDeclaredMethods()) {
                if (!ToolClass.invokeMethod(method, annotation)
                        .equals(ToolClass.invokeMethod(method, otherAnnotation))) return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isEqualAnnotationsWithAllMember(
            @Nullable final Annotation[] annotation, @Nullable final Annotation[] otherAnnotation) {
        return ToolArray.equals(
                annotation, otherAnnotation, ToolClass::isEqualAnnotationWithAllMember);
    }

    /**
     * where element is annotated with given {@code annotationType}
     */
    public static boolean isExistAnnotationType(
            @Nullable final AnnotatedElement annotatedElement,
            @Nullable final Class<? extends Annotation> annotationType) {
        return annotatedElement != null && annotatedElement.isAnnotationPresent(annotationType);
    }

    /**
     * where element is annotated with given {@code annotationTypes}
     */
    public static boolean isExistAnnotationTypes(
            @Nullable final AnnotatedElement annotatedElement,
            @Nonnull final Class<? extends Annotation>... annotationTypes) {
        return annotatedElement != null
                && Arrays.stream(annotationTypes)
                .filter(Whether::noNvl)
                .allMatch(annotatedElement::isAnnotationPresent);
    }

    /**
     * where element is annotated with given {@code annotation}
     */
    public static boolean isExistAnnotation(
            @Nullable final AnnotatedElement annotatedElement, @Nullable final Annotation annotation) {
        return annotatedElement != null
                && annotation != null
                && annotatedElement.isAnnotationPresent(annotation.annotationType());
    }

    /**
     * where element is annotated with given {@code annotations}
     */
    public static boolean isExistAnnotations(
            @Nullable final AnnotatedElement annotatedElement, @Nonnull final Annotation... annotations) {
        if (annotatedElement != null) {
            return Arrays.stream(annotations)
                    .filter(Whether::noNvl)
                    .allMatch(a -> annotatedElement.isAnnotationPresent(a.annotationType()));
        }
        return false;
    }

    /**
     * where element is annotated with given {@code annotation}, including member matching
     */
    public static boolean isExistAnnotationWithAllMember(
            @Nullable final AnnotatedElement annotatedElement, @Nullable final Annotation annotation) {
        return annotatedElement != null
                && annotation != null
                && isEqualAnnotationWithAllMember(
                annotatedElement.getAnnotation(annotation.annotationType()), annotation);
    }

    /**
     * where element is annotated with given {@code annotations}, including member matching
     */
    public static boolean isExistAnnotationsWithAllMember(
            @Nullable final AnnotatedElement annotatedElement, @Nonnull final Annotation... annotations) {
        if (annotatedElement != null) {
            return isEqualAnnotationsWithAllMember(annotatedElement.getAnnotations(), annotations);
        }
        return false;
    }

    /**
     * 是否废弃枚举类型
     *
     * @param enumerate 枚举类型
     * @return 如果废弃返回 {@code true}，否则 {@code false}
     */
    public static boolean isDeprecated(Enum<?> enumerate) {
        if (enumerate == null) {
            return true;
        }
        return isDeprecated(ToolClass.getField(enumerate));
    }

    /**
     * 是否废弃类型
     *
     * @param accessibleObject 可访问对象
     * @return 如果废弃返回 {@code true}，否则 {@code false}
     */
    public static boolean isDeprecated(AccessibleObject accessibleObject) {
        if (accessibleObject == null) {
            return true;
        }
        return isContainAnnotation(accessibleObject, Deprecated.class);
    }

    /**
     * 是否包含注解
     *
     * @param accessibleObject 可访问对象
     * @param annotationClass  注解类
     * @return 如果包含返回 {@code true}，否则 {@code false}
     */
    public static boolean isContainAnnotation(
            AccessibleObject accessibleObject, Class<? extends Annotation> annotationClass) {
        return accessibleObject.getAnnotation(annotationClass) != null;
    }

    public static boolean isAssignable(Class<?>[] src, Class<?>[] dist) {
        if (src == null || src.length == 0) {
            return dist == src;
        }
        if (src.length != dist.length) {
            return false;
        }
        for (int i = 0; i < src.length; i++) {
            if (!dist[i].isAssignableFrom(src[i]) || dist[i] == Object.class && src[i] != Object.class) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check whether the given exception is compatible with the specified exception types, as declared
     * in a throws clause.
     *
     * @param ex                 the exception to check
     * @param declaredExceptions the exception types declared in the throws clause
     * @return whether the given exception is compatible
     */
    public static boolean compatibleWithThrowsClause(Throwable ex, Class<?>... declaredExceptions) {
        if (!Whether.checkedException(ex)) {
            return true;
        }
        if (declaredExceptions != null) {
            for (Class<?> declaredException : declaredExceptions) {
                if (declaredException.isInstance(ex)) {
                    return true;
                }
            }
        }
        return false;
    }
}

package pxf.tl.annotation;

import java.lang.annotation.Annotation;

/**
 * 字节码过滤器
 *
 * @author potatoxf
 * @see potatoxf.helper.util.ToolClass#filterClass(Iterable, ClassFilter)
 */
public @interface ClassFilter {

    /**
     * 筛选指定类
     *
     * <p>条件：只要有一个类符合{@code clz==specifyClass}
     *
     * @return {@code Class<?>[]}
     */
    Class<?>[] specifyClass() default {};

    /**
     * 筛选指定类
     *
     * <p>条件：所有类都符合{@code clz!=specifyClass}
     *
     * @return {@code Class<?>[]}
     */
    Class<?>[] noSpecifyClass() default {};

    /**
     * 筛选继承类
     *
     * <p>条件：只要有一个类符合{@code extendsClass.isAssignableFrom(clz)}
     *
     * @return {@code Class<?>[]}
     */
    Class<?>[] extendsClass() default {};

    /**
     * 筛选继承类
     *
     * <p>条件：所有类都符合{@code !extendsClass.isAssignableFrom(clz)}
     *
     * @return {@code Class<?>[]}
     */
    Class<?>[] noExtendsClass() default {};

    /**
     * 筛选接口
     *
     * <p>条件：只要有一个类符合{@code clz.isInterface() && extendsClass.isAssignableFrom(clz)}
     *
     * @return {@code Class<?>[]}
     */
    Class<?>[] implementsInterface() default {};

    /**
     * 筛选接口
     *
     * <p>条件：所有类都符合{@code clz.isInterface() && !extendsClass.isAssignableFrom(clz)}
     *
     * @return {@code Class<?>[]}
     */
    Class<?>[] noImplementsInterface() default {};

    /**
     * 筛选注解
     *
     * <p>条件：只要有一个类符合{@code ToolClass.lookupAnnotationOnClass(clz, aClass) != null}
     *
     * @return {@code Class<?>[]}
     * @see potatoxf.helper.util.ToolClass#lookupAnnotationOnClass(Class, Class)
     */
    Class<? extends Annotation>[] hasAnnotation() default {};

    /**
     * 筛选注解
     *
     * <p>条件：所有类都符合{@code ToolClass.lookupAnnotationOnClass(clz, aClass) == null}
     *
     * @return {@code Class<?>[]}
     * @see potatoxf.helper.util.ToolClass#lookupAnnotationOnClass(Class, Class)
     */
    Class<? extends Annotation>[] noHasAnnotation() default {};

    /**
     * 筛选包
     *
     * <p>条件：只要有一个类符合{@code clz.getPackage().getName().matches(s)}
     *
     * @return {@code Class<?>[]}
     */
    String[] matchPackagePattern() default {};

    /**
     * 筛选包
     *
     * <p>条件：所有类都符合{@code !clz.getPackage().getName().matches(s)}
     *
     * @return {@code Class<?>[]}
     */
    String[] noMatchPackagePattern() default {};
}

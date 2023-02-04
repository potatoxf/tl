package pxf.tl.api;

import pxf.tl.util.ToolLog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * 国际化语言常量，用于常量类和常量枚举
 *
 * @author potatoxf
 */
public interface Literal<T extends Literal<T>> extends This<T>, Comparable<T> {
    /**
     * 使用当前本地话
     */
    ThreadLocal<Locale> LOCALE_THREAD_LOCAL = new ThreadLocal<>();
    /**
     * 包名缓存
     */
    Map<Class<?>, String> BASE_BUNDLE_NAME_CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * 资源包名称
     *
     * @return 返回当前包所在位置
     */
    @Nonnull
    default String resourceBundleName() {
        String name = BASE_BUNDLE_NAME_CACHE.get(getClass());
        if (name == null) {
            name = this$().getClass().getName().replace('.', '/');
            BASE_BUNDLE_NAME_CACHE.put(getClass(), name);
        }
        return "META-INF/i18n/" + name;
    }

    /**
     * 通过身份牌查找
     *
     * @return 返回唯一身份牌
     */
    default int getCode() {
        if (this$() instanceof Enum e) {
            return e.ordinal();
        }
        throw new UnsupportedOperationException("No Enum class please override");
    }

    /**
     * 通过唯一名称查找
     *
     * @return 返回名称
     */
    @Nonnull
    default String getName() {
        if (this$() instanceof Enum e) {
            return e.name();
        }
        throw new UnsupportedOperationException("No Enum class please override");
    }

    /**
     * 该可查找信息注释信息
     * <p>
     * {@link #LOCALE_THREAD_LOCAL}可通过这里设置
     *
     * @return 可查找信息注释信息
     */
    @Nonnull
    default String getMessage() {
        try {
            return getMessage(LOCALE_THREAD_LOCAL.get());
        } finally {
            LOCALE_THREAD_LOCAL.remove();
        }
    }

    /**
     * 该可查找信息注释信息
     *
     * @param locale 本地化语言
     * @return 可查找信息注释信息
     */
    @Nonnull
    default String getMessage(@Nullable Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String key = localeKey();
        try {
            return ResourceBundle.getBundle(resourceBundleName(), locale).getString(key);
        } catch (MissingResourceException e) {
            ToolLog.warn(e, () -> "Error to get message from [%s][%s]", resourceBundleName(), key);
            return "";
        }
    }

    /**
     * 获取本地化语言键，返回枚举的名称并将 {@code _}换成 {@code .}
     *
     * @return 返回本地化语言键
     */
    @Nonnull
    default String localeKey() {
        String name = getName();
        Boolean lowerCase = isLowerName();
        if (lowerCase != null) {
            if (lowerCase) {
                name = name.toLowerCase();
            } else {
                name = name.toUpperCase();
            }
        }
        Object[][] objects = replaceForName();
        if (objects != null && objects.length != 0) {
            for (Object[] array : objects) {
                if (array.length == 1 && array[0] != null) {
                    name = name.replace(String.valueOf(array[0]), "");
                } else if (array.length == 2 && array[0] != null) {
                    if (array[1] != null) {
                        name = name.replace(String.valueOf(array[0]), String.valueOf(array[1]));
                    } else {
                        name = name.replace(String.valueOf(array[0]), "");
                    }
                } else if (array.length == 3 && array[0] != null) {
                    if (array[1] != null) {
                        name = name.replaceAll(String.valueOf(array[0]), String.valueOf(array[1]));
                    } else {
                        name = name.replaceAll(String.valueOf(array[0]), "");
                    }
                } else {
                    int len = array.length / 2;
                    if (array.length % 2 == 0) {
                        for (int i = 0; i < len; i++) {
                            name = name.replace(String.valueOf(array[i++]), String.valueOf(array[i]));
                        }
                    } else {
                        for (int i = 0; i < len; i++) {
                            name = name.replaceAll(String.valueOf(array[i++]), String.valueOf(array[i]));
                        }
                    }
                }
            }
        }
        return name;
    }

    /**
     * 是否是小写名称
     *
     * @return 如果返回null则按照原样输出，如果是true则是小写否则是大写
     */
    default Boolean isLowerName() {
        return null;
    }

    /**
     * 替换数组，
     * <p>
     * 返回要替换的数组
     * <p>
     * {@link String#replace(CharSequence, CharSequence)}
     * {@link String#replaceAll(String, String)}
     *
     * @return {@code Object[][]}
     */
    default Object[][] replaceForName() {
        return null;
    }

    /**
     * 优先级
     *
     * @param other 另一个元素
     * @return 返回比较值
     */
    @Override
    default int compareTo(@Nonnull T other) {
        return Integer.compare(getCode(), other.getCode());
    }

}

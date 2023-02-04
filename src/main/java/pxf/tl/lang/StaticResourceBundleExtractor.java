package pxf.tl.lang;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 静态资源包提取器
 *
 * @author potatoxf
 */
public final class StaticResourceBundleExtractor extends StaticFieldExtractor {
    /**
     * 默认静态域名
     */
    private static final String DEFAULT_RESOURCE_NAME = "RESOURCE_NAME";

    public StaticResourceBundleExtractor() {
        super(DEFAULT_RESOURCE_NAME);
    }

    /**
     * 获取本地化值
     *
     * @param clz       类
     * @param locale    本地化语言
     * @param localeKey 本地化语言键
     * @return 本地化语言值
     */
    public String getLocaleValue(Class<?> clz, Locale locale, String localeKey) {
        ResourceBundle resourceBundle = getResourceBundle(clz, locale);
        if (resourceBundle != null && localeKey != null) {
            return resourceBundle.getString(localeKey);
        }
        return null;
    }

    /**
     * 获取资源包
     *
     * @param clz    类
     * @param locale 本地化语言
     * @return ResourceBundle
     */
    public ResourceBundle getResourceBundle(Class<?> clz, Locale locale) {
        String value = getResourceBundleName(clz);
        if (value != null) {
            try {
                return locale == null
                        ? ResourceBundle.getBundle(value, Locale.getDefault())
                        : ResourceBundle.getBundle(value, locale);
            } catch (MissingResourceException ignored) {
            }
        }
        return null;
    }

    /**
     * 获取绑定资源名
     *
     * @param clz 类
     * @return 绑定资源名
     */
    public String getResourceBundleName(Class<?> clz) {
        return (String) getValue(clz);
    }
}

package pxf.tl.setting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.api.GetterWithKeyedForValue;
import pxf.tl.api.PoolOfString;
import pxf.tl.bean.BeanUtil;
import pxf.tl.bean.copier.CopyOptions;
import pxf.tl.bean.copier.ValueProvider;
import pxf.tl.convert.Convert;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolBytecode;
import pxf.tl.util.ToolString;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Setting抽象类
 *
 * @author potatoxf
 */
public abstract class AbsSetting implements GetterWithKeyedForValue<String>, Serializable {
    /**
     * 数组类型值默认分隔符
     */
    public static final String DEFAULT_DELIMITER = ",";
    /**
     * 默认分组
     */
    public static final String DEFAULT_GROUP = PoolOfString.EMPTY;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbsSetting.class);
    private static final long serialVersionUID = 6200156302595905863L;

    public String getStr(String key, String defaultValue) {
        return getStr(key, DEFAULT_GROUP, defaultValue);
    }

    /**
     * 获得字符串类型值
     *
     * @param key          KEY
     * @param group        分组
     * @param defaultValue 默认值
     * @return 值，如果字符串为{@code null}返回默认值
     */
    public String getStr(String key, String group, String defaultValue) {
        final String value = getByGroup(key, group);
        return Safe.value(value, defaultValue);
    }

    /**
     * 获得字符串类型值，如果字符串为{@code null}或者""返回默认值
     *
     * @param key          KEY
     * @param group        分组
     * @param defaultValue 默认值
     * @return 值，如果字符串为{@code null}或者""返回默认值
     */
    public String getStrNotEmpty(String key, String group, String defaultValue) {
        final String value = getByGroup(key, group);
        return Safe.value(value, defaultValue);
    }

    /**
     * 获得指定分组的键对应值
     *
     * @param key   键
     * @param group 分组
     * @return 值
     */
    public abstract String getByGroup(String key, String group);

    // --------------------------------------------------------------- Get

    /**
     * 带有日志提示的get，如果没有定义指定的KEY，则打印debug日志
     *
     * @param key 键
     * @return 值
     */
    public String getWithLog(String key) {
        final String value = getStringValue(key);
        if (value == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No key define for [{}]!", key);
            }
        }
        return value;
    }

    /**
     * 带有日志提示的get，如果没有定义指定的KEY，则打印debug日志
     *
     * @param key   键
     * @param group 分组
     * @return 值
     */
    public String getByGroupWithLog(String key, String group) {
        final String value = getByGroup(key, group);
        if (value == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No key define for [{}] of group [{}] !", key, group);
            }
        }
        return value;
    }

    // --------------------------------------------------------------- Get string array

    /**
     * 获得数组型
     *
     * @param key 属性名
     * @return 属性值
     */
    public String[] getStrings(String key) {
        return getStrings(key, null);
    }

    /**
     * 获得数组型
     *
     * @param key          属性名
     * @param defaultValue 默认的值
     * @return 属性值
     */
    public String[] getStringsWithDefault(String key, String[] defaultValue) {
        String[] value = getStrings(key, null);
        if (null == value) {
            value = defaultValue;
        }

        return value;
    }

    /**
     * 获得数组型
     *
     * @param key   属性名
     * @param group 分组名
     * @return 属性值
     */
    public String[] getStrings(String key, String group) {
        return getStrings(key, group, DEFAULT_DELIMITER);
    }

    /**
     * 获得数组型
     *
     * @param key       属性名
     * @param group     分组名
     * @param delimiter 分隔符
     * @return 属性值
     */
    public String[] getStrings(String key, String group, String delimiter) {
        final String value = getByGroup(key, group);
        if (Whether.blank(value)) {
            return null;
        }
        return ToolString.split(value, delimiter);
    }

    // --------------------------------------------------------------- Get int

    /**
     * 获取数字型型属性值
     *
     * @param key   属性名
     * @param group 分组名
     * @return 属性值
     */
    public Integer getInt(String key, String group) {
        return getInt(key, group, null);
    }

    /**
     * 获取数字型型属性值
     *
     * @param key          属性名
     * @param group        分组名
     * @param defaultValue 默认值
     * @return 属性值
     */
    public Integer getInt(String key, String group, Integer defaultValue) {
        return Convert.toInt(getByGroup(key, group), defaultValue);
    }

    // --------------------------------------------------------------- Get bool

    /**
     * 获取布尔型属性值
     *
     * @param key   属性名
     * @param group 分组名
     * @return 属性值
     */
    public Boolean getBool(String key, String group) {
        return getBool(key, group, null);
    }

    /**
     * 获取布尔型属性值
     *
     * @param key          属性名
     * @param group        分组名
     * @param defaultValue 默认值
     * @return 属性值
     */
    public Boolean getBool(String key, String group, Boolean defaultValue) {
        return Convert.toBool(getByGroup(key, group), defaultValue);
    }

    // --------------------------------------------------------------- Get long

    /**
     * 获取long类型属性值
     *
     * @param key   属性名
     * @param group 分组名
     * @return 属性值
     */
    public Long getLong(String key, String group) {
        return getLong(key, group, null);
    }

    /**
     * 获取long类型属性值
     *
     * @param key          属性名
     * @param group        分组名
     * @param defaultValue 默认值
     * @return 属性值
     */
    public Long getLong(String key, String group, Long defaultValue) {
        return Convert.toLong(getByGroup(key, group), defaultValue);
    }

    // --------------------------------------------------------------- Get char

    /**
     * 获取char类型属性值
     *
     * @param key   属性名
     * @param group 分组名
     * @return 属性值
     */
    public Character getChar(String key, String group) {
        final String value = getByGroup(key, group);
        if (Whether.blank(value)) {
            return null;
        }
        return value.charAt(0);
    }

    // --------------------------------------------------------------- Get double

    /**
     * 获取double类型属性值
     *
     * @param key   属性名
     * @param group 分组名
     * @return 属性值
     */
    public Double getDouble(String key, String group) {
        return getDouble(key, group, null);
    }

    /**
     * 获取double类型属性值
     *
     * @param key          属性名
     * @param group        分组名
     * @param defaultValue 默认值
     * @return 属性值
     */
    public Double getDouble(String key, String group, Double defaultValue) {
        return Convert.toDouble(getByGroup(key, group), defaultValue);
    }

    /**
     * 将setting中的键值关系映射到对象中，原理是调用对象对应的set方法<br>
     * 只支持基本类型的转换
     *
     * @param <T>   Bean类型
     * @param group 分组
     * @param bean  Bean对象
     * @return Bean
     */
    public <T> T toBean(final String group, T bean) {
        return BeanUtil.fillBean(
                bean,
                new ValueProvider<String>() {

                    @Override
                    public Object value(String key, Type valueType) {
                        return getByGroup(key, group);
                    }

                    @Override
                    public boolean containsKey(String key) {
                        return null != getByGroup(key, group);
                    }
                },
                CopyOptions.create());
    }

    /**
     * 将setting中的键值关系映射到对象中，原理是调用对象对应的set方法<br>
     * 只支持基本类型的转换
     *
     * @param <T>       Bean类型
     * @param group     分组
     * @param beanClass Bean类型
     * @return Bean
     */
    public <T> T toBean(String group, Class<T> beanClass) {
        return toBean(group, ToolBytecode.createInstanceIfPossible(beanClass));
    }

    /**
     * 将setting中的键值关系映射到对象中，原理是调用对象对应的set方法<br>
     * 只支持基本类型的转换
     *
     * @param <T>  bean类型
     * @param bean Bean
     * @return Bean
     */
    public <T> T toBean(T bean) {
        return toBean(null, bean);
    }

    /**
     * 将setting中的键值关系映射到对象中，原理是调用对象对应的set方法<br>
     * 只支持基本类型的转换
     *
     * @param <T>       bean类型
     * @param beanClass Bean类型
     * @return Bean
     */
    public <T> T toBean(Class<T> beanClass) {
        return toBean(null, beanClass);
    }
}

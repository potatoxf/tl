package pxf.tl.setting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.api.Charsets;
import pxf.tl.api.GetterWithKeyedForValue;
import pxf.tl.api.PoolOfString;
import pxf.tl.bean.BeanUtil;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.help.Assert;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.io.FileUtil;
import pxf.tl.io.resource.*;
import pxf.tl.io.watch.SimpleWatcher;
import pxf.tl.io.watch.WatchMonitor;
import pxf.tl.io.watch.WatchUtil;
import pxf.tl.util.ToolBytecode;
import pxf.tl.util.ToolIO;
import pxf.tl.util.ToolString;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Properties文件读取封装类
 *
 * @author potatoxf
 */
public final class Props extends Properties implements GetterWithKeyedForValue<String> {
    /**
     * 默认配置文件扩展名
     */
    public static final String EXT_NAME = "properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(Props.class);
    /**
     * 配置文件缓存
     */
    private static final Map<String, Props> propsMap = new ConcurrentHashMap<>();

    private static final long serialVersionUID = 1935981579709590740L;
    /**
     * 属性文件的Resource
     */
    private Resource resource;
    // ----------------------------------------------------------------------- 私有属性 start
    private WatchMonitor watchMonitor;
    /**
     * properties文件编码<br>
     * issue#1701，此属性不能被序列化，故忽略序列化
     */
    private transient Charsets charsets = Charsets.UTF_8;

    /**
     * 构造
     */
    public Props() {
    }

    /**
     * 构造，使用相对于Class文件根目录的相对路径
     *
     * @param path 配置文件路径，相对于ClassPath，或者使用绝对路径
     */
    public Props(String path) {
        this(path, Charsets.ISO_8859_1);
    }


    /**
     * 构造，使用相对于Class文件根目录的相对路径
     *
     * @param path     相对或绝对路径
     * @param charsets 字符集
     */
    public Props(String path, Charsets charsets) {
        Assert.notBlank(path, "Blank properties file path !");
        if (null != charsets) {
            this.charsets = charsets;
        }
        this.load(ResourceUtil.getResourceObj(path));
    }
    // ----------------------------------------------------------------------- 私有属性 end

    /**
     * 构造
     *
     * @param propertiesFile 配置文件对象
     */
    public Props(File propertiesFile) {
        this(propertiesFile, Charsets.ISO_8859_1);
    }

    /**
     * 构造
     *
     * @param propertiesFile 配置文件对象
     * @param charsets       字符集
     */
    public Props(File propertiesFile, Charsets charsets) {
        Assert.notNull(propertiesFile, "Null properties file!");
        this.charsets = charsets;
        this.load(new FileResource(propertiesFile));
    }

    // ----------------------------------------------------------------------- 构造方法 start

    /**
     * 构造，相对于classes读取文件
     *
     * @param path  相对路径
     * @param clazz 基准类
     */
    public Props(String path, Class<?> clazz) {
        this(path, clazz, Charsets.ISO_8859_1);
    }


    /**
     * 构造，相对于classes读取文件
     *
     * @param path     相对路径
     * @param clazz    基准类
     * @param charsets 字符集
     */
    public Props(String path, Class<?> clazz, Charsets charsets) {
        Assert.notBlank(path, "Blank properties file path !");
        if (null != charsets) {
            this.charsets = charsets;
        }
        this.load(new ClassPathResource(path, clazz));
    }

    /**
     * 构造，使用URL读取
     *
     * @param propertiesUrl 属性文件路径
     */
    public Props(URL propertiesUrl) {
        this(propertiesUrl, Charsets.ISO_8859_1);
    }

    /**
     * 构造，使用URL读取
     *
     * @param propertiesUrl 属性文件路径
     * @param charsets      字符集
     */
    public Props(URL propertiesUrl, Charsets charsets) {
        Assert.notNull(propertiesUrl, "Null properties URL !");
        if (null != charsets) {
            this.charsets = charsets;
        }
        this.load(propertiesUrl);
    }

    /**
     * 构造，使用URL读取
     *
     * @param properties 属性文件路径
     */
    public Props(Properties properties) {
        if (Whether.noEmpty(properties)) {
            this.putAll(properties);
        }
    }

    /**
     * 获取当前环境下的配置文件<br>
     * name可以为不包括扩展名的文件名（默认.properties），也可以是文件名全称
     *
     * @param name 文件名，如果没有扩展名，默认为.properties
     * @return 当前环境下配置文件
     */
    public static Props get(String name) {
        return propsMap.computeIfAbsent(
                name,
                (filePath) -> {
                    final String extName = FileUtil.extName(filePath);
                    if (Whether.empty(extName)) {
                        filePath = filePath + "." + Props.EXT_NAME;
                    }
                    return new Props(filePath);
                });
    }

    /**
     * 获取给定路径找到的第一个配置文件<br>
     * * name可以为不包括扩展名的文件名（默认.properties为结尾），也可以是文件名全称
     *
     * @param names 文件名，如果没有扩展名，默认为.properties
     * @return 当前环境下配置文件
     */
    public static Props getFirstFound(String... names) {
        for (String name : names) {
            try {
                return get(name);
            } catch (NoResourceException e) {
                // ignore
            }
        }
        return null;
    }

    /**
     * 获取系统参数，例如用户在执行java命令时定义的 -Duse=hutool
     *
     * @return 系统参数Props
     */
    public static Props getSystemProps() {
        return new Props(System.getProperties());
    }

    /**
     * 构建一个空的Props，用于手动加入参数
     *
     * @return Setting
     */
    public static Props create() {
        return new Props();
    }

    /**
     * 获得Classpath下的Properties文件
     *
     * @param resource 资源（相对Classpath的路径）
     * @return Props
     */
    public static Props getProp(String resource) {
        return new Props(resource);
    }

    /**
     * 获得Classpath下的Properties文件
     *
     * @param resource 资源（相对Classpath的路径）
     * @param charsets 字符集
     * @return Properties
     */
    public static Props getProp(String resource, Charsets charsets) {
        return new Props(resource, charsets);
    }

    // ----------------------------------------------------------------------- 构造方法 end

    /**
     * 初始化配置文件
     *
     * @param url {@link URL}
     */
    public void load(URL url) {
        load(new UrlResource(url));
    }

    /**
     * 初始化配置文件
     *
     * @param resource {@link Resource}
     */
    public void load(Resource resource) {
        Assert.notNull(resource, "Props resource must be not null!");
        this.resource = resource;

        try (final BufferedReader reader = resource.getReader(charsets)) {
            super.load(reader);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 重新加载配置文件
     */
    public void load() {
        this.load(this.resource);
    }

    /**
     * 在配置文件变更时自动加载
     *
     * @param autoReload 是否自动加载
     */
    public void autoLoad(boolean autoReload) {
        if (autoReload) {
            Assert.notNull(this.resource, "Properties resource must be not null!");
            if (null != this.watchMonitor) {
                // 先关闭之前的监听
                this.watchMonitor.close();
            }
            this.watchMonitor =
                    WatchUtil.createModify(
                            this.resource.getUrl(),
                            new SimpleWatcher() {
                                @Override
                                public void onModify(WatchEvent<?> event, Path currentPath) {
                                    load();
                                }
                            });
            this.watchMonitor.start();
        } else {
            ToolIO.closes(this.watchMonitor);
            this.watchMonitor = null;
        }
    }

    // ----------------------------------------------------------------------- Get start

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    public Object getObjectValue(String key) {
        return super.getProperty(key);
    }

    /**
     * 获取并删除键值对，当指定键对应值非空时，返回并删除这个值，后边的键对应的值不再查找
     *
     * @param keys 键列表，常用于别名
     * @return 字符串值
     */
    public String getAndRemoveStr(String... keys) {
        Object value = null;
        for (String key : keys) {
            value = remove(key);
            if (null != value) {
                break;
            }
        }
        return (String) value;
    }

    /**
     * 转换为标准的{@link Properties}对象
     *
     * @return {@link Properties}对象
     */
    public Properties toProperties() {
        final Properties properties = new Properties();
        properties.putAll(this);
        return properties;
    }

    /**
     * 将配置文件转换为Bean，支持嵌套Bean<br>
     * 支持的表达式：
     *
     * <pre>
     * persion
     * persion.name
     * persons[3]
     * person.friends[5].name
     * ['person']['friends'][5]['name']
     * </pre>
     *
     * @param <T>       Bean类型
     * @param beanClass Bean类
     * @return Bean对象
     */
    public <T> T toBean(Class<T> beanClass) {
        return toBean(beanClass, null);
    }

    /**
     * 将配置文件转换为Bean，支持嵌套Bean<br>
     * 支持的表达式：
     *
     * <pre>
     * persion
     * persion.name
     * persons[3]
     * person.friends[5].name
     * ['person']['friends'][5]['name']
     * </pre>
     *
     * @param <T>       Bean类型
     * @param beanClass Bean类
     * @param prefix    公共前缀，不指定前缀传null，当指定前缀后非此前缀的属性被忽略
     * @return Bean对象
     */
    public <T> T toBean(Class<T> beanClass, String prefix) {
        final T bean = ToolBytecode.createInstanceIfPossible(beanClass);
        return fillBean(bean, prefix);
    }

    /**
     * 将配置文件转换为Bean，支持嵌套Bean<br>
     * 支持的表达式：
     *
     * <pre>
     * persion
     * persion.name
     * persons[3]
     * person.friends[5].name
     * ['person']['friends'][5]['name']
     * </pre>
     *
     * @param <T>    Bean类型
     * @param bean   Bean对象
     * @param prefix 公共前缀，不指定前缀传null，当指定前缀后非此前缀的属性被忽略
     * @return Bean对象
     */
    public <T> T fillBean(T bean, String prefix) {
        prefix = Safe.value(ToolString.addSuffixIfNot(prefix, PoolOfString.DOT));

        String key;
        for (Map.Entry<Object, Object> entry : this.entrySet()) {
            key = (String) entry.getKey();
            if (!ToolString.startWith(key, prefix)) {
                // 非指定开头的属性忽略掉
                continue;
            }
            try {
                BeanUtil.setProperty(bean, ToolString.subSuf(key, prefix.length()), entry.getValue());
            } catch (Exception e) {
                // 忽略注入失败的字段（这些字段可能用于其它配置）
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("Ignore property: [{}]", key, e);
                }
            }
        }

        return bean;
    }

    // ----------------------------------------------------------------------- Get end

    // ----------------------------------------------------------------------- Set start

    /**
     * 设置值，无给定键创建之。设置后未持久化
     *
     * @param key   属性键
     * @param value 属性值
     */
    public void setProperty(String key, Object value) {
        super.setProperty(key, value.toString());
    }

    /**
     * 持久化当前设置，会覆盖掉之前的设置
     *
     * @param absolutePath 设置文件的绝对路径
     * @throws IORuntimeException IO异常，可能为文件未找到
     */
    public void store(String absolutePath) throws IORuntimeException {
        Writer writer = null;
        try {
            writer = FileUtil.getWriter(absolutePath, charsets, false);
            super.store(writer, null);
        } catch (IOException e) {
            throw new IORuntimeException(e, "Store properties to [{}] error!", absolutePath);
        } finally {
            ToolIO.closes(writer);
        }
    }

    /**
     * 存储当前设置，会覆盖掉以前的设置
     *
     * @param path  相对路径
     * @param clazz 相对的类
     */
    public void store(String path, Class<?> clazz) {
        this.store(FileUtil.getAbsolutePath(path, clazz));
    }
    // ----------------------------------------------------------------------- Set end
}

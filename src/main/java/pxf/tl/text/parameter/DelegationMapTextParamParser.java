package pxf.tl.text.parameter;


import pxf.tl.collection.map.CaseInsensitiveMap;
import pxf.tl.help.New;
import pxf.tl.help.Valid;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolCollection;
import pxf.tl.util.ToolString;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 综合委托文本Map解析器
 *
 * <p>子类必须继承{@code AbstractMapTextParamParser}并且子类必须有默认构造函数
 *
 * <p>已实现类型： STRING{@link StringMapTextParser}
 *
 * @author potatoxf
 */
public final class DelegationMapTextParamParser extends AbstractMapTextParamParser {
    /**
     * 默认键
     */
    public static final String DEFAULT_KEY = "DEFAULT";
    /**
     * 解析类型
     */
    public static final String DEFAULT_TYPE = "STRING";
    /**
     * 默认解析器
     */
    private static final StringMapTextParser DEFAULT = new StringMapTextParser();
    /**
     * 配置
     */
    private final CaseInsensitiveMap<String, Class<? extends AbstractMapTextParamParser>> config;
    /**
     * 默认键
     */
    private final String defaultKey;

    private DelegationMapTextParamParser(
            Map<String, Class<? extends AbstractMapTextParamParser>> config, String defaultKey) {
        this.config =
                config == null
                        ? new CaseInsensitiveMap<String, Class<? extends AbstractMapTextParamParser>>()
                        : ToolCollection.toStringObjectCaseInsensitiveMap(config);
        this.defaultKey = defaultKey;
        if (!this.config.containsKey(DEFAULT_TYPE)) {
            this.config.put(DEFAULT_TYPE, StringMapTextParser.class);
        }
    }

    public static DelegationMapTextParamParser of(String defaultKey) {
        return new DelegationMapTextParamParser(null, defaultKey);
    }

    public static DelegationMapTextParamParser of(
            Map<String, Class<? extends AbstractMapTextParamParser>> config, String defaultKey) {
        return new DelegationMapTextParamParser(config, defaultKey);
    }

    @SuppressWarnings("unchecked")
    public static DelegationMapTextParamParser of(String propertiesClasspath, String defaultKey) {
        InputStream resourceAsStream = Class.class.getResourceAsStream(propertiesClasspath);
        Properties properties = new Properties();
        if (resourceAsStream != null) {
            try {
                properties.load(resourceAsStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, Class<? extends AbstractMapTextParamParser>> config =
                new HashMap<String, Class<? extends AbstractMapTextParamParser>>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            Object key = entry.getKey();
            if (key != null) {
                Object value = entry.getValue();
                if (value != null) {
                    try {
                        Class<?> clz = Class.forName(value.toString().trim());
                        if (!DelegationMapTextParamParser.class.equals(clz)
                                && AbstractMapTextParamParser.class.isAssignableFrom(clz)) {
                            config.put(key.toString(), (Class<? extends AbstractMapTextParamParser>) clz);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new DelegationMapTextParamParser(config, defaultKey);
    }

    /**
     * 解析类型
     *
     * @return 类型
     */
    @Override
    protected String type() {
        return null;
    }

    /**
     * 解析单个值为Map{@code Map<String, Object>}
     *
     * @param input 输入字符串
     * @return 返回解析后的结果{@code Map<String, Object>}
     */
    @Override
    protected Map<String, Object> doParseValue(String input) throws Throwable {
        if (Whether.empty(input)) {
            return Collections.emptyMap();
        }
        String[] startSignAndData = ToolString.parseStartSignAndData(input, '#');
        return Valid.val(doParseMapValue(startSignAndData[0], startSignAndData[1]));
    }

    private Map<String, Object> doParseMapValue(String type, String data) throws Throwable {
        Class<? extends AbstractMapTextParamParser> clz = config.get(type);
        if (defaultKey != null) {
            clz = config.get(defaultKey);
        }
        if (clz == null) {
            return DEFAULT.parseValue(data, null);
        }
        return clz.newInstance().parseValue(data, null);
    }

    private static final class StringMapTextParser extends AbstractMapTextParamParser {
        /**
         * 解析类型
         *
         * @return 类型
         */
        @Override
        protected String type() {
            return "STRING";
        }

        /**
         * 解析单个值
         *
         * @param input 输入字符串
         * @return 返回解析后的结果
         */
        @Override
        protected Map<String, Object> doParseValue(String input) throws Throwable {
            return New.map(true, DEFAULT_KEY, input);
        }
    }
}

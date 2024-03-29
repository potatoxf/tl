package pxf.tl.bean.copier.provider;


import pxf.tl.bean.BeanUtil;
import pxf.tl.bean.PropDesc;
import pxf.tl.bean.copier.ValueProvider;
import pxf.tl.collection.map.TransMap;
import pxf.tl.function.Editor;
import pxf.tl.util.ToolString;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Bean的值提供者
 *
 * @author potatoxf
 */
public class BeanValueProvider implements ValueProvider<String> {

    final Map<String, PropDesc> sourcePdMap;
    private final Object source;
    private final boolean ignoreError;

    /**
     * 构造
     *
     * @param bean        Bean
     * @param ignoreCase  是否忽略字段大小写
     * @param ignoreError 是否忽略字段值读取错误
     */
    public BeanValueProvider(Object bean, boolean ignoreCase, boolean ignoreError) {
        this(bean, ignoreCase, ignoreError, null);
    }

    /**
     * 构造
     *
     * @param bean        Bean
     * @param ignoreCase  是否忽略字段大小写
     * @param ignoreError 是否忽略字段值读取错误
     * @param keyEditor   键编辑器
     */
    public BeanValueProvider(
            Object bean, boolean ignoreCase, boolean ignoreError, Editor<String> keyEditor) {
        this.source = bean;
        this.ignoreError = ignoreError;
        final Map<String, PropDesc> sourcePdMap =
                BeanUtil.getBeanDesc(source.getClass()).getPropMap(ignoreCase);
        // issue#2202@Github
        // 如果用户定义了键编辑器，则提供的map中的数据必须全部转换key
        this.sourcePdMap =
                new TransMap<>(new HashMap<>(sourcePdMap.size(), 1)) {
                    @Override
                    protected String customKey(Object key) {
                        if (ignoreCase && key instanceof CharSequence) {
                            key = key.toString().toLowerCase();
                        }
                        if (null != keyEditor) {
                            key = keyEditor.edit(key.toString());
                        }
                        return key.toString();
                    }
                };
        this.sourcePdMap.putAll(sourcePdMap);
    }

    @Override
    public Object value(String key, Type valueType) {
        final PropDesc sourcePd = getPropDesc(key, valueType);

        Object result = null;
        if (null != sourcePd) {
            result = sourcePd.getValue(this.source, valueType, this.ignoreError);
        }
        return result;
    }

    @Override
    public boolean containsKey(String key) {
        final PropDesc sourcePd = getPropDesc(key, null);

        // 字段描述不存在或忽略读的情况下，表示不存在
        return null != sourcePd && sourcePd.isReadable(false);
    }

    /**
     * 获得属性描述
     *
     * @param key       字段名
     * @param valueType 值类型，用于判断是否为Boolean，可以为null
     * @return 属性描述
     */
    private PropDesc getPropDesc(String key, Type valueType) {
        PropDesc sourcePd = sourcePdMap.get(key);
        if (null == sourcePd
                && (null == valueType || Boolean.class == valueType || boolean.class == valueType)) {
            // boolean类型字段字段名支持两种方式
            sourcePd = sourcePdMap.get(ToolString.upperFirstAndAddPre(key, "is"));
        }

        return sourcePd;
    }
}

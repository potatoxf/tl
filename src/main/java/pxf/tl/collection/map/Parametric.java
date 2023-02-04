package pxf.tl.collection.map;


import pxf.tl.api.GetterWithKeyedForValue;
import pxf.tl.util.ToolCollection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * @author potatoxf
 */
public class Parametric extends CaseInsensitiveMap<String, Object> implements GetterWithKeyedForValue<String> {

    public Parametric(Map<? extends String, ?> map) {
        super(map);
    }

    public Parametric() {
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    public Number getNumberValue(String key) {
        return ToolCollection.getBigDecimalValue(this, key);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    public Boolean getBooleanValue(String key) {
        return ToolCollection.getBooleanValue(this, key);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    public Character getCharacterValue(String key) {
        return ToolCollection.getCharacterValue(this, key);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    public BigInteger getBigIntegerValue(String key) {
        return ToolCollection.getBigIntegerValue(this, key);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    public BigDecimal getBigDecimalValue(String key) {
        return ToolCollection.getBigDecimalValue(this, key);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    public String getStringValue(String key) {
        return ToolCollection.getStringValue(this, key);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    public Object getObjectValue(String key) {
        return ToolCollection.getObjectValue(this, key);
    }
}

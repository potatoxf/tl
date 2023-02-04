package pxf.tl.convert.impl;


import pxf.tl.bean.BeanUtil;
import pxf.tl.collection.map.MapUtil;
import pxf.tl.convert.AbstractConverter;
import pxf.tl.convert.ConverterRegistry;
import pxf.tl.util.ToolBytecode;
import pxf.tl.util.ToolString;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * {@link Map} 转换器
 *
 * @author potatoxf
 */
public class MapConverter extends AbstractConverter<Map<?, ?>> {
    private static final long serialVersionUID = 1L;

    /**
     * Map类型
     */
    private final Type mapType;
    /**
     * 键类型
     */
    private final Type keyType;
    /**
     * 值类型
     */
    private final Type valueType;

    /**
     * 构造，Map的key和value泛型类型自动获取
     *
     * @param mapType Map类型
     */
    public MapConverter(Type mapType) {
        this(mapType, ToolBytecode.getTypeArgument(mapType, 0), ToolBytecode.getTypeArgument(mapType, 1));
    }

    /**
     * 构造
     *
     * @param mapType   Map类型
     * @param keyType   键类型
     * @param valueType 值类型
     */
    public MapConverter(Type mapType, Type keyType, Type valueType) {
        this.mapType = mapType;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Map<?, ?> convertInternal(Object value) {
        Map map;
        if (value instanceof Map) {
            final Class<?> valueClass = value.getClass();
            if (valueClass.equals(this.mapType)) {
                final Type[] typeArguments = ToolBytecode.getTypeArguments(valueClass);
                if (null != typeArguments //
                        && 2 == typeArguments.length //
                        && Objects.equals(this.keyType, typeArguments[0]) //
                        && Objects.equals(this.valueType, typeArguments[1])) {
                    // 对于键值对类型一致的Map对象，不再做转换，直接返回原对象
                    return (Map) value;
                }
            }
            map = MapUtil.createMap(ToolBytecode.getClass(this.mapType));
            convertMapToMap((Map) value, map);
        } else if (BeanUtil.isBean(value.getClass())) {
            map = BeanUtil.beanToMap(value);
            // 二次转换，转换键值类型
            map = convertInternal(map);
        } else {
            throw new UnsupportedOperationException(
                    ToolString.format("Unsupport toMap value type: {}", value.getClass().getName()));
        }
        return map;
    }

    /**
     * Map转Map
     *
     * @param srcMap    源Map
     * @param targetMap 目标Map
     */
    private void convertMapToMap(Map<?, ?> srcMap, Map<Object, Object> targetMap) {
        final ConverterRegistry convert = ConverterRegistry.getInstance();
        srcMap.forEach(
                (key, value) -> {
                    key = ToolBytecode.isUnknown(this.keyType) ? key : convert.convert(this.keyType, key);
                    value =
                            ToolBytecode.isUnknown(this.valueType) ? value : convert.convert(this.valueType, value);
                    targetMap.put(key, value);
                });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Map<?, ?>> getTargetType() {
        return (Class<Map<?, ?>>) ToolBytecode.getClass(this.mapType);
    }
}

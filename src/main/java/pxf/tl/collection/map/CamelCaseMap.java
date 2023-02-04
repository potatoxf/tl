package pxf.tl.collection.map;


import pxf.tl.help.Safe;

import java.io.Serial;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 驼峰Key风格的Map<br>
 * 对KEY转换为驼峰，get("int_value")和get("intValue")获得的值相同，put进入的值也会被覆盖
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author potatoxf
 */
public class CamelCaseMap<K, V> extends TransMap<K, V> {
    @Serial
    private static final long serialVersionUID = 4043263744224569870L;

    public CamelCaseMap(Supplier<Map<K, V>> mapFactory) {
        super(mapFactory);
    }

    public CamelCaseMap(Map<K, V> emptyMap) {
        super(emptyMap);
    }

    @Override
    protected K customKey(Object key) {
        if (key instanceof CharSequence) {
            key = Safe.toCamelCase(key.toString(), false);
        }
        return (K) key;
    }
}

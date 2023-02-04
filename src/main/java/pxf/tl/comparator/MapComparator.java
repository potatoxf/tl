package pxf.tl.comparator;

import java.util.Map;

/**
 * Map 比较器
 *
 * @author potatoxf
 */
public class MapComparator<K, V> extends InternalComparator<Map<K, V>, V> {

    public MapComparator(K key) {
        this(key, true);
    }

    public MapComparator(final K key, boolean isNullElementLast) {
        super(isNullElementLast, kvMap -> kvMap.get(key));
    }
}

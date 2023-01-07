package pxf.tl.collection.map;


import pxf.tl.comparator.AbstractComparator;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * A case-insensitive <code>Map</code>.
 *
 * <p>Before keys are added to the proxyMap or compared to other existing keys, they are converted
 * to all lowercase in a locale-independent fashion by using information from the Unicode data file.
 *
 * <p>Null keys are supported.
 *
 * <p>The <code>keySet()</code> method returns all lowercase keys, or nulls.
 *
 * <p>Example:
 *
 * <pre><code>
 *  Map&lt;String, String&gt; proxyMap = new CaseInsensitiveMap&lt;String, String&gt;();
 *  proxyMap.put("One", "One");
 *  proxyMap.put("Two", "Two");
 *  proxyMap.put(null, "Three");
 *  proxyMap.put("one", "Four");
 * </code></pre>
 *
 * <p>The example above creates a <code>CaseInsensitiveMap</code> with three entries.
 *
 * <p><code>proxyMap.get(null)</code> returns <code>"Three"</code> and <code>proxyMap.get("ONE")
 * </code> returns <code>"Four".</code> The <code>Set</code> returned by <code>keySet()</code>
 * equals <code>{"one", "two", null}.</code>
 *
 * @author potatoxf
 */
@SuppressWarnings("unchecked")
public class InsensitiveMap<K, V> extends TreeMap<K, V> {

    private static final ComparatorImpl<?> COMPARATOR = new ComparatorImpl<>();

    public InsensitiveMap() {
        super((Comparator<? super K>) COMPARATOR);
    }

    private static class ComparatorImpl<T> extends AbstractComparator<T> {

        @Override
        protected Integer doCompareBefore(T o1, T o2) {
            if (o1 instanceof String && o2 instanceof String) {
                return String.CASE_INSENSITIVE_ORDER.compare((String) o1, (String) o2);
            }
            if (o1 instanceof String) {
                return -1;
            }
            if (o2 instanceof String) {
                return 1;
            }
            return super.doCompareBefore(o1, o2);
        }

        @Override
        protected int doCompareAfter(T o1, T o2) {
            Integer v1 = o1.hashCode();
            Integer v2 = o2.hashCode();
            return v1.compareTo(v2);
        }
    }
}

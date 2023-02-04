package pxf.tl.collection.map;


import pxf.tl.comparator.ObjectInsensitiveComparator;

import java.io.Serial;
import java.util.Map;
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
public class CaseInsensitiveMap<K, V> extends TransMap<K, V> {
    @Serial
    private static final long serialVersionUID = 1L;

    public CaseInsensitiveMap(Map<? extends K, ? extends V> map) {
        this();
        this.putAll(map);
    }

    public CaseInsensitiveMap() {
        super(new TreeMap<>(new ObjectInsensitiveComparator<>()));
    }
}

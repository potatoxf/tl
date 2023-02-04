package pxf.tl.api;

import java.util.Objects;

/**
 * @author potatoxf
 */
public class Triple<C, K, V> extends Pair<K, V> {

    protected volatile C catalog;

    public Triple(C catalog, K key) {
        this(catalog, key, null);
    }

    public Triple(C catalog, K key, V value) {
        super(key, value);
        this.catalog = catalog;
    }

    public C getCatalog() {
        return catalog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple<?, ?, ?> triple)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(catalog, triple.catalog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), catalog);
    }
}

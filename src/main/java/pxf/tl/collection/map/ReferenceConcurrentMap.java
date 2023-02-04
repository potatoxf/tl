package pxf.tl.collection.map;


import pxf.tl.api.ReferenceType;
import pxf.tl.collection.ExtendConcurrentMap;
import pxf.tl.util.ToolCollection;
import pxf.tl.util.ToolObject;

import javax.annotation.Nonnull;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 线程安全的ReferenceMap实现<br>
 * 参考：jdk.management.resource.internal.WeakKeyConcurrentHashMap
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author potatoxf
 */
public class ReferenceConcurrentMap<K, V> implements ExtendConcurrentMap<K, V> {

    final ConcurrentMap<Reference<K>, V> raw;
    private final ReferenceQueue<K> lastQueue;
    private final ReferenceType keyType;
    /**
     * 回收监听
     */
    private BiConsumer<Reference<? extends K>, V> purgeListener;

    // region 构造

    /**
     * 构造
     *
     * @param raw           {@link ConcurrentMap}实现
     * @param referenceType Reference类型
     */
    public ReferenceConcurrentMap(
            ConcurrentMap<Reference<K>, V> raw, ReferenceType referenceType) {
        this.raw = raw;
        this.keyType = referenceType;
        lastQueue = new ReferenceQueue<>();
    }
    // endregion

    /**
     * 设置对象回收清除监听
     *
     * @param purgeListener 监听函数
     */
    public void setPurgeListener(BiConsumer<Reference<? extends K>, V> purgeListener) {
        this.purgeListener = purgeListener;
    }

    @Override
    public int size() {
        this.purgeStaleKeys();
        return this.raw.size();
    }

    @Override
    public V get(Object key) {
        this.purgeStaleKeys();
        return this.raw.get(ofKey(key, null));
    }

    @Override
    public boolean containsKey(Object key) {
        this.purgeStaleKeys();
        return this.raw.containsKey(ofKey(key, null));
    }

    @Override
    public boolean containsValue(Object value) {
        this.purgeStaleKeys();
        return this.raw.containsValue(value);
    }

    @Override
    public V put(K key, V value) {
        this.purgeStaleKeys();
        return this.raw.put(ofKey(key, this.lastQueue), value);
    }

    @Override
    public V putIfAbsent(@Nonnull K key, V value) {
        this.purgeStaleKeys();
        return this.raw.putIfAbsent(ofKey(key, this.lastQueue), value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public V replace(@Nonnull K key, @Nonnull V value) {
        this.purgeStaleKeys();
        return this.raw.replace(ofKey(key, this.lastQueue), value);
    }

    @Override
    public boolean replace(@Nonnull K key, @Nonnull V oldValue, @Nonnull V newValue) {
        this.purgeStaleKeys();
        return this.raw.replace(ofKey(key, this.lastQueue), oldValue, newValue);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.purgeStaleKeys();
        this.raw.replaceAll((kWeakKey, value) -> function.apply(kWeakKey.get(), value));
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        this.purgeStaleKeys();
        return this.raw.computeIfAbsent(
                ofKey(key, this.lastQueue), kWeakKey -> mappingFunction.apply(key));
    }

    @Override
    public V computeIfPresent(
            K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        this.purgeStaleKeys();
        return this.raw.computeIfPresent(
                ofKey(key, this.lastQueue), (kWeakKey, value) -> remappingFunction.apply(key, value));
    }

    @Override
    public V remove(Object key) {
        this.purgeStaleKeys();
        return this.raw.remove(ofKey(key, null));
    }

    @Override
    public boolean remove(@Nonnull Object key, Object value) {
        this.purgeStaleKeys();
        return this.raw.remove(ofKey(key, null), value);
    }

    @Override
    public void clear() {
        this.raw.clear();
        //noinspection StatementWithEmptyBody
        while (lastQueue.poll() != null)
            ;
    }

    @Override
    public Set<K> keySet() {
        // TODO 非高效方式的set转换，应该返回一个view
        final Collection<K> trans =
                ToolCollection.trans(
                        this.raw.keySet(), (reference) -> null == reference ? null : reference.get());
        return new HashSet<>(trans);
    }

    @Override
    public Collection<V> values() {
        this.purgeStaleKeys();
        return this.raw.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        this.purgeStaleKeys();
        return this.raw.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey().get(), entry.getValue()))
                .collect(Collectors.toSet());
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        this.purgeStaleKeys();
        this.raw.forEach((key, value) -> action.accept(key.get(), value));
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        this.purgeStaleKeys();
        return this.raw.compute(
                ofKey(key, this.lastQueue), (kWeakKey, value) -> remappingFunction.apply(key, value));
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        this.purgeStaleKeys();
        return this.raw.merge(ofKey(key, this.lastQueue), value, remappingFunction);
    }

    /**
     * 清除被回收的键
     */
    private void purgeStaleKeys() {
        Reference<? extends K> reference;
        V value;
        while ((reference = this.lastQueue.poll()) != null) {
            value = this.raw.remove(reference);
            if (null != purgeListener) {
                purgeListener.accept(reference, value);
            }
        }
    }

    /**
     * 根据Reference类型构建key对应的{@link Reference}
     *
     * @param key   键
     * @param queue {@link ReferenceQueue}
     * @return {@link Reference}
     */
    @SuppressWarnings("unchecked")
    private Reference<K> ofKey(Object key, ReferenceQueue<? super K> queue) {
        switch (keyType) {
            case WEAK:
                return new WeakKey<>((K) key, queue);
            case SOFT:
                return new SoftKey<>((K) key, queue);
        }
        throw new IllegalArgumentException("Unsupported key type: " + keyType);
    }

    /**
     * 弱键
     *
     * @param <K> 键类型
     */
    private static class WeakKey<K> extends WeakReference<K> {
        private final int hashCode;

        /**
         * 构造
         *
         * @param key   原始Key，不能为{@code null}
         * @param queue {@link ReferenceQueue}
         */
        WeakKey(K key, ReferenceQueue<? super K> queue) {
            super(key, queue);
            hashCode = key.hashCode();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            } else if (other instanceof WeakKey) {
                return ToolObject.equals(((WeakKey<?>) other).get(), get());
            }
            return false;
        }
    }

    /**
     * 弱键
     *
     * @param <K> 键类型
     */
    private static class SoftKey<K> extends SoftReference<K> {
        private final int hashCode;

        /**
         * 构造
         *
         * @param key   原始Key，不能为{@code null}
         * @param queue {@link ReferenceQueue}
         */
        SoftKey(K key, ReferenceQueue<? super K> queue) {
            super(key, queue);
            hashCode = key.hashCode();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            } else if (other instanceof SoftKey) {
                return ToolObject.equals(((SoftKey<?>) other).get(), get());
            }
            return false;
        }
    }
}

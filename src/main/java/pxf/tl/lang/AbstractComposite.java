package pxf.tl.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolCollection;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * 抽象综合类，用于综合多种实现类的调用
 *
 * @author potatoxf
 */
public abstract class AbstractComposite<K, T> {

    /**
     * 日志
     */
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    /**
     * 锁
     */
    private final Object lock = new Object();
    /**
     * 实例容器
     */
    private final Map<K, T> instanceContainer = new LinkedHashMap<>();

    /**
     * 实例的顺序
     */
    private final Map<K, Integer> orderContainer = new LinkedHashMap<>();

    /**
     * 实例顺序缓存
     */
    private volatile List<T> instances;

    /**
     * 注冊实例，以实例{@code Class}作为分类
     *
     * @param instance 实例
     */
    public final void registerInstance(K key, T instance) {
        if (key == null) {
            throw new IllegalArgumentException("The key does not allow null");
        }
        if (instance == null) {
            throw new IllegalArgumentException("Add instance does not allow null");
        }
        synchronized (lock) {
            instances = null;
            instanceContainer.put(key, instance);
        }
    }

    /**
     * 指定实例顺序，如果不包括则跳过
     *
     * @param keys 顺序类{@code K}
     */
    @SafeVarargs
    public final void specifiedInstanceWithOrder(K... keys) {
        if (Whether.empty(keys)) {
            return;
        }
        synchronized (lock) {
            instances = null;
            int i = 0;
            for (K key : keys) {
                if (instanceContainer.containsKey(key)) {
                    orderContainer.put(key, i++);
                } else {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("does not include object instances of type [" + key + "]");
                    }
                    orderContainer.put(key, -1);
                }
            }
            for (Map.Entry<K, Integer> entry : orderContainer.entrySet()) {
                if (entry.getValue() == -1) {
                    orderContainer.put(entry.getKey(), i++);
                }
            }
        }
    }

    /**
     * 指定实例顺序，如果不包括则跳过
     *
     * @param comparator 排序比较器
     */
    public final void specifiedAllInstanceWithSort(Comparator<K> comparator) {
        synchronized (lock) {
            instances = null;
            orderContainer.clear();
            ToolCollection.sortMap(instanceContainer, comparator, orderContainer);
        }
    }

    /**
     * 获取实例列表
     *
     * @return {@code List<T>}
     */
    @Nonnull
    public final List<T> getInstanceList() {
        if (instances != null) {
            return instances;
        }
        synchronized (lock) {
            Map<K, T> map =
                    ToolCollection.filterMap(
                            instanceContainer,
                            (k, v) -> filterKey(k) && filterInstance(v),
                            new LinkedHashMap<>());

            if (Whether.empty(orderContainer)) {
                instances = new ArrayList<>(map.values());
            } else {
                instances = new ArrayList<>(map.size());
                orderContainer.forEach(
                        (k, v) -> {
                            if (map.containsKey(k)) {
                                instances.add(map.get(k));
                            }
                        });
            }
            instances = Collections.unmodifiableList(instances);
        }
        return instances;
    }

    /**
     * 过滤符合要求的键
     *
     * @param key {code K}
     * @return 返回true则符合要求，否则false
     */
    protected boolean filterKey(K key) {
        return true;
    }

    /**
     * 过滤符合要求的实例
     *
     * @param instance {code T}
     * @return 返回true则符合要求，否则false
     */
    protected boolean filterInstance(T instance) {
        return true;
    }
}

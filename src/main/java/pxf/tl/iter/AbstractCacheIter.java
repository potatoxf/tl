package pxf.tl.iter;

import pxf.tl.help.New;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author potatoxf
 */
public abstract class AbstractCacheIter<T> extends AbstractIter<T> {
    /**
     * 缓存
     */
    private final LinkedList<T> cached;
    /**
     * 自动延续
     */
    private final boolean autoContinue;
    /**
     * 过率器
     */
    private final Predicate<T> conditionFilter;
    /**
     * 是否完成缓存
     */
    private boolean finishCache = false;
    /**
     * 当前迭代器
     */
    private Iterator<T> currentIterator;

    protected AbstractCacheIter(LinkedList<T> cached, boolean autoContinue, Predicate<T> conditionFilter) {
        this.cached = cached;
        this.autoContinue = autoContinue;
        this.conditionFilter = conditionFilter;
    }

    /**
     * 是否缓存
     *
     * @return 是否缓存
     */
    public final boolean isCache() {
        return cached != null;
    }


    /**
     * 初始化迭代器
     *
     * @param iterator 迭代器
     */
    public final void initIterator(Iterator<T> iterator) {
        if (isRunning()) {
            throw new ConcurrentModificationException("The initIterator is not allowed on the running");
        }
        if (iterator == this) {
            throw new IllegalArgumentException("The same is not allowed iterator");
        }
        reset();
        currentIterator = iterator;
    }

    /**
     * 获取第一个元素
     *
     * @return {@code Optional<T>}
     */
    @Nonnull
    public Optional<T> getFirst() {
        List<T> newList = getNewList();
        if (newList.isEmpty()) {
            return Optional.of(null);
        }
        return Optional.of(newList.get(0));
    }

    /**
     * 转为数组
     *
     * @param componentType 数组元素类型
     * @return 转为数组
     */
    public final T[] toArray(@Nonnull Class<?> componentType) {
        return toArray(value -> New.array(componentType, value));
    }

    /**
     * 转为数组
     *
     * @param generator 生成数组
     * @return 转为数组
     */
    public final T[] toArray(@Nonnull IntFunction<T[]> generator) {
        return getNewList().toArray(generator);
    }

    /**
     * 转为链表
     *
     * @return {@code List<T>}
     */
    public final List<T> toList() {
        return toList(null);
    }

    /**
     * 转为链表
     *
     * @return {@code List<T>}
     */
    public final List<T> toList(Predicate<T> filter) {
        List<T> result = getNewList();
        if (filter != null) {
            result = result.stream().filter(filter).collect(Collectors.toList());
        }
        return result;
    }

    /**
     * 将元素添加到集合中
     *
     * @param collection 集合
     */
    public final void addTo(@Nonnull Collection<? super T> collection) {
        if (finishCache && cached != null) {
            collection.addAll(cached);
        } else {
            for (T t : this) {
                collection.add(t);
            }
        }
    }

    /**
     * 获取迭代器
     *
     * @return {@code Iterator<T>}
     */
    @Nonnull
    public final Iterator<T> copyIterator() {
        return getNewList().iterator();
    }

    /**
     * 重置迭代器
     */
    @Override
    public void reset() {
        super.reset();
        finishCache = false;
        currentIterator = null;
        if (cached != null) {
            cached.clear();
        }
    }

    /**
     * 是否支持重复迭代
     *
     * @return 如果支持返回true，否则返回false
     */
    @Override
    protected final boolean isSupportAgain() {
        return isCache();
    }

    /**
     * 获取下一个值，当无下一个节点时，须返回{@code null}表示遍历结束
     *
     * @return 节点值
     */
    @Override
    protected final T doNext() {
        T currentValue = null;
        if (finishCache) {
            boolean hasNext = false;
            if (currentIterator == null && cached != null) {
                currentIterator = cached.iterator();
            }
            if (currentIterator != null) {
                hasNext = currentIterator.hasNext();
            }
            //存在下一个元素
            if (hasNext) {
                currentValue = currentIterator.next();
            } else {
                currentIterator = null;
                if (autoContinue) {
                    again();
                }
            }
        } else {
            while (true) {
                currentValue = computeNext();
                // 没有下一个，则已完成
                if (currentValue == null) {
                    currentIterator = null;
                    finishCache = true;
                    if (autoContinue) {
                        again();
                    }
                    break;
                } else {
                    // 未指定过滤器
                    if (conditionFilter == null) {
                        if (cached != null) {
                            cached.add(currentValue);
                        }
                        break;
                    } else {
                        if (conditionFilter.test(currentValue)) {
                            if (cached != null) {
                                cached.add(currentValue);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return currentValue;
    }

    /**
     * 获取下一元素
     *
     * @return 返回下一元素，如果元素为null则没有下一元素
     */
    protected final T getNextElement() {
        if (currentIterator != null) {
            T t;
            while (currentIterator.hasNext()) {
                t = currentIterator.next();
                if (t != null) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * 计算新的节点，通过实现此方法，当调用{@link #hasNext()}时将此方法产生的节点缓存，直到调用{@link #next()}取出<br>
     * 当无下一个节点时，须返回{@code null}表示遍历结束
     *
     * @return 节点值
     */
    protected abstract T computeNext();


    protected final List<T> getNewList() {
        List<T> list = new ArrayList<>();
        if (finishCache && cached != null) {
            list.addAll(cached);
        } else {
            forEach((value, index) -> {
                if (conditionFilter == null || conditionFilter.test(value)) {
                    list.add(value);
                }
                return true;
            });
        }
        currentIterator = list.iterator();
        again();
        return new ArrayList<>(list);
    }
}

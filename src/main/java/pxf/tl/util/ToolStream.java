package pxf.tl.util;


import pxf.tl.api.Charsets;
import pxf.tl.collection.SimpleCollector;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.help.Assert;
import pxf.tl.help.New;
import pxf.tl.help.Whether;
import pxf.tl.iter.AnyIter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author potatoxf
 */
public final class ToolStream {
    /**
     * 说明已包含IDENTITY_FINISH特征 为 Characteristics.IDENTITY_FINISH 的缩写
     */
    public static final Set<Collector.Characteristics> CH_ID =
            Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
    /**
     * 说明不包含IDENTITY_FINISH特征
     */
    public static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

    private ToolStream() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    /**
     * 将collection转化为类型不变的map<br>
     * <B>{@code Collection<V> ----> Map<K,V>}</B>
     *
     * @param collection 需要转化的集合
     * @param key        V类型转化为K类型的lambda方法
     * @param <V>        collection中的泛型
     * @param <K>        map中的key类型
     * @return 转化后的map
     */
    public static <V, K> Map<K, V> toIdentityMap(Collection<V> collection, Function<V, K> key) {
        return toIdentityMap(collection, key, false);
    }

    /**
     * 将collection转化为类型不变的map<br>
     * <B>{@code Collection<V> ----> Map<K,V>}</B>
     *
     * @param collection 需要转化的集合
     * @param key        V类型转化为K类型的lambda方法
     * @param isParallel 是否并行流
     * @param <V>        collection中的泛型
     * @param <K>        map中的key类型
     * @return 转化后的map
     */
    public static <V, K> Map<K, V> toIdentityMap(
            Collection<V> collection, Function<V, K> key, boolean isParallel) {
        if (Whether.empty(collection)) {
            return New.map(0);
        }
        return toMap(
                collection, key, Function.identity(), isParallel);
    }

    /**
     * 将Collection转化为map(value类型与collection的泛型不同)<br>
     * <B>{@code Collection<E> -----> Map<K,V> }</B>
     *
     * @param collection 需要转化的集合
     * @param key        E类型转化为K类型的lambda方法
     * @param value      E类型转化为V类型的lambda方法
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @param <V>        map中的value类型
     * @return 转化后的map
     */
    public static <E, K, V> Map<K, V> toMap(
            Collection<E> collection, Function<E, K> key, Function<E, V> value) {
        return toMap(collection, key, value, false);
    }

    /**
     * @param collection 需要转化的集合
     * @param key        E类型转化为K类型的lambda方法
     * @param value      E类型转化为V类型的lambda方法
     * @param isParallel 是否并行流
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @param <V>        map中的value类型
     * @return 转化后的map
     */
    public static <E, K, V> Map<K, V> toMap(
            Collection<E> collection, Function<E, K> key, Function<E, V> value, boolean isParallel) {
        if (Whether.empty(collection)) {
            return New.map(0);
        }
        return ToolStream.of(collection, isParallel)
                .collect(HashMap::new, (m, v) -> m.put(key.apply(v), value.apply(v)), HashMap::putAll);
    }

    /**
     * 将collection按照规则(比如有相同的班级id)分组成map<br>
     * <B>{@code Collection<E> -------> Map<K,List<E>> } </B>
     *
     * @param collection 需要分组的集合
     * @param key        分组的规则
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @return 分组后的map
     */
    public static <E, K> Map<K, List<E>> groupByKey(Collection<E> collection, Function<E, K> key) {
        return groupByKey(collection, key, false);
    }

    /**
     * 将collection按照规则(比如有相同的班级id)分组成map<br>
     * <B>{@code Collection<E> -------> Map<K,List<E>> } </B>
     *
     * @param collection 需要分组的集合
     * @param key        键分组的规则
     * @param isParallel 是否并行流
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @return 分组后的map
     */
    public static <E, K> Map<K, List<E>> groupByKey(
            Collection<E> collection, Function<E, K> key, boolean isParallel) {
        if (Whether.empty(collection)) {
            return New.map(0);
        }
        return groupBy(collection, key, Collectors.toList(), isParallel);
    }

    /**
     * 将collection按照两个规则(比如有相同的年级id,班级id)分组成双层map<br>
     * <B>{@code Collection<E> ---> Map<T,Map<U,List<E>>> } </B>
     *
     * @param collection 需要分组的集合
     * @param key1       第一个分组的规则
     * @param key2       第二个分组的规则
     * @param <E>        集合元素类型
     * @param <K>        第一个map中的key类型
     * @param <U>        第二个map中的key类型
     * @return 分组后的map
     */
    public static <E, K, U> Map<K, Map<U, List<E>>> groupBy2Key(
            Collection<E> collection, Function<E, K> key1, Function<E, U> key2) {
        return groupBy2Key(collection, key1, key2, false);
    }

    /**
     * 将collection按照两个规则(比如有相同的年级id,班级id)分组成双层map<br>
     * <B>{@code Collection<E> ---> Map<T,Map<U,List<E>>> } </B>
     *
     * @param collection 需要分组的集合
     * @param key1       第一个分组的规则
     * @param key2       第二个分组的规则
     * @param isParallel 是否并行流
     * @param <E>        集合元素类型
     * @param <K>        第一个map中的key类型
     * @param <U>        第二个map中的key类型
     * @return 分组后的map
     */
    public static <E, K, U> Map<K, Map<U, List<E>>> groupBy2Key(
            Collection<E> collection, Function<E, K> key1, Function<E, U> key2, boolean isParallel) {
        if (Whether.empty(collection)) {
            return New.map(0);
        }
        return groupBy(
                collection, key1, ToolStream.groupingBy(key2, Collectors.toList()), isParallel);
    }

    /**
     * 将collection按照两个规则(比如有相同的年级id,班级id)分组成双层map<br>
     * <B>{@code Collection<E> ---> Map<T,Map<U,E>> } </B>
     *
     * @param collection 需要分组的集合
     * @param key1       第一个分组的规则
     * @param key2       第二个分组的规则
     * @param <T>        第一个map中的key类型
     * @param <U>        第二个map中的key类型
     * @param <E>        collection中的泛型
     * @return 分组后的map
     */
    public static <E, T, U> Map<T, Map<U, E>> group2Map(
            Collection<E> collection, Function<E, T> key1, Function<E, U> key2) {
        return group2Map(collection, key1, key2, false);
    }

    /**
     * 将collection按照两个规则(比如有相同的年级id,班级id)分组成双层map<br>
     * <B>{@code Collection<E> ---> Map<T,Map<U,E>> } </B>
     *
     * @param collection 需要分组的集合
     * @param key1       第一个分组的规则
     * @param key2       第二个分组的规则
     * @param isParallel 是否并行流
     * @param <T>        第一个map中的key类型
     * @param <U>        第二个map中的key类型
     * @param <E>        collection中的泛型
     * @return 分组后的map
     */
    public static <E, T, U> Map<T, Map<U, E>> group2Map(
            Collection<E> collection, Function<E, T> key1, Function<E, U> key2, boolean isParallel) {
        if (Whether.empty(collection) || key1 == null || key2 == null) {
            return New.map(0);
        }
        return groupBy(
                collection, key1, ToolStream.toMap(key2, Function.identity(), (l, r) -> l), isParallel);
    }

    /**
     * 将collection按照规则(比如有相同的班级id)分组成map，map中的key为班级id，value为班级名<br>
     * <B>{@code Collection<E> -------> Map<K,List<V>> } </B>
     *
     * @param collection 需要分组的集合
     * @param key        键分组的规则
     * @param value      值分组的规则
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @param <V>        List中的value类型
     * @return 分组后的map
     */
    public static <E, K, V> Map<K, List<V>> groupKeyValue(
            Collection<E> collection, Function<E, K> key, Function<E, V> value) {
        return groupKeyValue(collection, key, value, false);
    }

    /**
     * 将collection按照规则(比如有相同的班级id)分组成map，map中的key为班级id，value为班级名<br>
     * <B>{@code Collection<E> -------> Map<K,List<V>> } </B>
     *
     * @param collection 需要分组的集合
     * @param key        键分组的规则
     * @param value      值分组的规则
     * @param isParallel 是否并行流
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @param <V>        List中的value类型
     * @return 分组后的map
     */
    public static <E, K, V> Map<K, List<V>> groupKeyValue(
            Collection<E> collection, Function<E, K> key, Function<E, V> value, boolean isParallel) {
        if (Whether.empty(collection)) {
            return New.map(0);
        }
        return groupBy(
                collection,
                key,
                Collectors.mapping(v -> Optional.ofNullable(v).map(value).orElse(null), Collectors.toList()),
                isParallel);
    }

    /**
     * 作为所有groupingBy的公共方法，更接近于原生，灵活性更强
     *
     * @param collection 需要分组的集合
     * @param key        第一次分组时需要的key
     * @param downstream 分组后需要进行的操作
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @param <D>        后续操作的返回值
     * @return 分组后的map
     */
    public static <E, K, D> Map<K, D> groupBy(
            Collection<E> collection, Function<E, K> key, Collector<E, ?, D> downstream) {
        if (Whether.empty(collection)) {
            return New.map(0);
        }
        return groupBy(collection, key, downstream, false);
    }

    /**
     * 作为所有groupingBy的公共方法，更接近于原生，灵活性更强
     *
     * @param collection 需要分组的集合
     * @param key        第一次分组时需要的key
     * @param downstream 分组后需要进行的操作
     * @param isParallel 是否并行流
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @param <D>        后续操作的返回值
     * @return 分组后的map
     * @see Collectors#groupingBy(Function, Collector)
     */
    public static <E, K, D> Map<K, D> groupBy(
            Collection<E> collection,
            Function<E, K> key,
            Collector<E, ?, D> downstream,
            boolean isParallel) {
        if (Whether.empty(collection)) {
            return New.map(0);
        }
        return ToolStream.of(collection, isParallel).collect(ToolStream.groupingBy(key, downstream));
    }

    /**
     * 将collection转化为List集合，但是两者的泛型不同<br>
     * <B>{@code Collection<E> ------> List<T> } </B>
     *
     * @param collection 需要转化的集合
     * @param function   collection中的泛型转化为list泛型的lambda表达式
     * @param <E>        collection中的泛型
     * @param <T>        List中的泛型
     * @return 转化后的list
     */
    public static <E, T> List<T> toList(Collection<E> collection, Function<E, T> function) {
        return toList(collection, function, false);
    }

    /**
     * 将collection转化为List集合，但是两者的泛型不同<br>
     * <B>{@code Collection<E> ------> List<T> } </B>
     *
     * @param collection 需要转化的集合
     * @param function   collection中的泛型转化为list泛型的lambda表达式
     * @param isParallel 是否并行流
     * @param <E>        collection中的泛型
     * @param <T>        List中的泛型
     * @return 转化后的list
     */
    public static <E, T> List<T> toList(
            Collection<E> collection, Function<E, T> function, boolean isParallel) {
        if (Whether.empty(collection)) {
            return New.list(false);
        }
        return ToolStream.of(collection, isParallel)
                .map(function)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 将collection转化为Set集合，但是两者的泛型不同<br>
     * <B>{@code Collection<E> ------> Set<T> } </B>
     *
     * @param collection 需要转化的集合
     * @param function   collection中的泛型转化为set泛型的lambda表达式
     * @param <E>        collection中的泛型
     * @param <T>        Set中的泛型
     * @return 转化后的Set
     */
    public static <E, T> Set<T> toSet(Collection<E> collection, Function<E, T> function) {
        return toSet(collection, function, false);
    }

    /**
     * 将collection转化为Set集合，但是两者的泛型不同<br>
     * <B>{@code Collection<E> ------> Set<T> } </B>
     *
     * @param collection 需要转化的集合
     * @param function   collection中的泛型转化为set泛型的lambda表达式
     * @param isParallel 是否并行流
     * @param <E>        collection中的泛型
     * @param <T>        Set中的泛型
     * @return 转化后的Set
     */
    public static <E, T> Set<T> toSet(
            Collection<E> collection, Function<E, T> function, boolean isParallel) {
        if (Whether.empty(collection)) {
            return New.set(true);
        }
        return ToolStream.of(collection, isParallel)
                .map(function)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 合并两个相同key类型的map
     *
     * @param map1  第一个需要合并的 map
     * @param map2  第二个需要合并的 map
     * @param merge 合并的lambda，将key value1 value2合并成最终的类型,注意value可能为空的情况
     * @param <K>   map中的key类型
     * @param <X>   第一个 map的value类型
     * @param <Y>   第二个 map的value类型
     * @param <V>   最终map的value类型
     * @return 合并后的map
     */
    public static <K, X, Y, V> Map<K, V> merge(
            Map<K, X> map1, Map<K, Y> map2, BiFunction<X, Y, V> merge) {
        if (Whether.empty(map1) && Whether.empty(map2)) {
            return New.map(0);
        } else if (Whether.empty(map1)) {
            map1 = New.map(0);
        } else if (Whether.empty(map2)) {
            map2 = New.map(0);
        }
        Set<K> key = new HashSet<>();
        key.addAll(map1.keySet());
        key.addAll(map2.keySet());
        Map<K, V> map = New.map(key.size());
        for (K t : key) {
            X x = map1.get(t);
            Y y = map2.get(t);
            V z = merge.apply(x, y);
            if (z != null) {
                map.put(t, z);
            }
        }
        return map;
    }

    /**
     * @param <T>
     * @return
     */
    public static <T> Collector<T, ?, List<T>> toSyncList() {
        return Collectors.toCollection(CopyOnWriteArrayList::new);
    }

    /**
     * @param <T>
     * @return
     */
    public static <T> Collector<T, ?, Set<T>> toSyncSet() {
        return Collectors.toCollection(ConcurrentSkipListSet::new);
    }

    /**
     * @param <T>
     * @return
     */
    public static <T> Collector<T, ?, Set<T>> toLinkSet() {
        return Collectors.toCollection(LinkedHashSet::new);
    }

    @SafeVarargs
    public static <T> Stream<T> of(T... array) {
        Assert.notNull(array, "Array must be not null!");
        return Stream.of(array);
    }

    /**
     * {@link Iterable}转换为{@link Stream}，默认非并行
     *
     * @param iterable 集合
     * @param <T>      集合元素类型
     * @return {@link Stream}
     */
    public static <T> Stream<T> of(Iterable<T> iterable) {
        return of(iterable, false);
    }

    /**
     * {@link Iterable}转换为{@link Stream}
     *
     * @param iterable 集合
     * @param parallel 是否并行
     * @param <T>      集合元素类型
     * @return {@link Stream}
     */
    public static <T> Stream<T> of(Iterable<T> iterable, boolean parallel) {
        Assert.notNull(iterable, "Iterable must be not null!");
        return StreamSupport.stream(
                Spliterators.spliterator(AnyIter.ofIterable(true, iterable).toList(), 0), parallel);
    }

    /**
     * 按行读取文件为{@link Stream}
     *
     * @param file 文件
     * @return {@link Stream}
     */
    public static Stream<String> of(File file) {
        return of(file, Charsets.UTF_8);
    }

    /**
     * 按行读取文件为{@link Stream}
     *
     * @param path 路径
     * @return {@link Stream}
     */
    public static Stream<String> of(Path path) {
        return of(path, Charsets.UTF_8);
    }

    /**
     * 按行读取文件为{@link Stream}
     *
     * @param file     文件
     * @param charsets 编码
     * @return {@link Stream}
     */
    public static Stream<String> of(File file, Charsets charsets) {
        Assert.notNull(file, "File must be not null!");
        return of(file.toPath(), charsets);
    }

    /**
     * 按行读取文件为{@link Stream}
     *
     * @param path     路径
     * @param charsets 编码
     * @return {@link Stream}
     */
    public static Stream<String> of(Path path, Charsets charsets) {
        try {
            return Files.lines(path, charsets.get());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 通过函数创建Stream
     *
     * @param seed           初始值
     * @param elementCreator 递进函数，每次调用此函数获取下一个值
     * @param limit          限制个数
     * @param <T>            创建元素类型
     * @return {@link Stream}
     */
    public static <T> Stream<T> of(T seed, UnaryOperator<T> elementCreator, int limit) {
        return Stream.iterate(seed, elementCreator).limit(limit);
    }

    /**
     * 将Stream中所有元素以指定分隔符，合并为一个字符串，对象默认调用toString方法
     *
     * @param stream    {@link Stream}
     * @param delimiter 分隔符
     * @param <T>       元素类型
     * @return 字符串
     */
    public static <T> String join(Stream<T> stream, CharSequence delimiter) {
        return stream.collect(joining(delimiter));
    }

    /**
     * 将Stream中所有元素以指定分隔符，合并为一个字符串
     *
     * @param stream       {@link Stream}
     * @param delimiter    分隔符
     * @param toStringFunc 元素转换为字符串的函数
     * @param <T>          元素类型
     * @return 字符串
     */
    public static <T> String join(
            Stream<T> stream, CharSequence delimiter, Function<T, ? extends CharSequence> toStringFunc) {
        return stream.collect(joining(delimiter, toStringFunc));
    }

    /**
     * 提供任意对象的Join操作的{@link Collector}实现，对象默认调用toString方法
     *
     * @param delimiter 分隔符
     * @param <T>       对象类型
     * @return {@link Collector}
     */
    public static <T> Collector<T, ?, String> joining(CharSequence delimiter) {
        return joining(delimiter, Object::toString);
    }

    /**
     * 提供任意对象的Join操作的{@link Collector}实现
     *
     * @param delimiter    分隔符
     * @param toStringFunc 自定义指定对象转换为字符串的方法
     * @param <T>          对象类型
     * @return {@link Collector}
     */
    public static <T> Collector<T, ?, String> joining(
            CharSequence delimiter, Function<T, ? extends CharSequence> toStringFunc) {
        return joining(delimiter, ToolString.EMPTY, ToolString.EMPTY, toStringFunc);
    }

    /**
     * 提供任意对象的Join操作的{@link Collector}实现
     *
     * @param delimiter    分隔符
     * @param prefix       前缀
     * @param suffix       后缀
     * @param toStringFunc 自定义指定对象转换为字符串的方法
     * @param <T>          对象类型
     * @return {@link Collector}
     */
    public static <T> Collector<T, ?, String> joining(
            CharSequence delimiter,
            CharSequence prefix,
            CharSequence suffix,
            Function<T, ? extends CharSequence> toStringFunc) {
        return new SimpleCollector<>(
                () -> new StringJoiner(delimiter, prefix, suffix),
                (joiner, ele) -> joiner.add(toStringFunc.apply(ele)),
                StringJoiner::merge,
                StringJoiner::toString,
                Collections.emptySet());
    }

    /**
     * 提供对null值友好的groupingBy操作的{@link Collector}实现，可指定map类型
     *
     * @param classifier 分组依据
     * @param mapFactory 提供的map
     * @param downstream 下游操作
     * @param <T>        实体类型
     * @param <K>        实体中的分组依据对应类型，也是Map中key的类型
     * @param <D>        下游操作对应返回类型，也是Map中value的类型
     * @param <A>        下游操作在进行中间操作时对应类型
     * @param <M>        最后返回结果Map类型
     * @return {@link Collector}
     */
    public static <T, K, D, A, M extends Map<K, D>> Collector<T, ?, M> groupingBy(
            Function<? super T, ? extends K> classifier,
            Supplier<M> mapFactory,
            Collector<? super T, A, D> downstream) {
        final Supplier<A> downstreamSupplier = downstream.supplier();
        final BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
        final BiConsumer<Map<K, A>, T> accumulator =
                (m, t) -> {
                    final K key = Optional.ofNullable(t).map(classifier).orElse(null);
                    final A container = m.computeIfAbsent(key, k -> downstreamSupplier.get());
                    downstreamAccumulator.accept(container, t);
                };
        final BinaryOperator<Map<K, A>> merger = mapMerger(downstream.combiner());
        @SuppressWarnings("unchecked") final Supplier<Map<K, A>> mangledFactory = (Supplier<Map<K, A>>) mapFactory;

        if (downstream.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new SimpleCollector<>(mangledFactory, accumulator, merger, CH_ID);
        } else {
            @SuppressWarnings("unchecked") final Function<A, A> downstreamFinisher = (Function<A, A>) downstream.finisher();
            final Function<Map<K, A>, M> finisher =
                    intermediate -> {
                        intermediate.replaceAll((k, v) -> downstreamFinisher.apply(v));
                        @SuppressWarnings("unchecked") final M castResult = (M) intermediate;
                        return castResult;
                    };
            return new SimpleCollector<>(mangledFactory, accumulator, merger, finisher, CH_NOID);
        }
    }

    /**
     * 提供对null值友好的groupingBy操作的{@link Collector}实现
     *
     * @param classifier 分组依据
     * @param downstream 下游操作
     * @param <T>        实体类型
     * @param <K>        实体中的分组依据对应类型，也是Map中key的类型
     * @param <D>        下游操作对应返回类型，也是Map中value的类型
     * @param <A>        下游操作在进行中间操作时对应类型
     * @return {@link Collector}
     */
    public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingBy(
            Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream) {
        return groupingBy(classifier, HashMap::new, downstream);
    }

    /**
     * 提供对null值友好的groupingBy操作的{@link Collector}实现
     *
     * @param classifier 分组依据
     * @param <T>        实体类型
     * @param <K>        实体中的分组依据对应类型，也是Map中key的类型
     * @return {@link Collector}
     */
    public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingBy(
            Function<? super T, ? extends K> classifier) {
        return groupingBy(classifier, Collectors.toList());
    }

    /**
     * 对null友好的 toMap 操作的 {@link Collector}实现，默认使用HashMap
     *
     * @param keyMapper     指定map中的key
     * @param valueMapper   指定map中的value
     * @param mergeFunction 合并前对value进行的操作
     * @param <T>           实体类型
     * @param <K>           map中key的类型
     * @param <U>           map中value的类型
     * @return 对null友好的 toMap 操作的 {@link Collector}实现
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper,
            BinaryOperator<U> mergeFunction) {
        return toMap(keyMapper, valueMapper, mergeFunction, HashMap::new);
    }

    /**
     * 对null友好的 toMap 操作的 {@link Collector}实现
     *
     * @param keyMapper     指定map中的key
     * @param valueMapper   指定map中的value
     * @param mergeFunction 合并前对value进行的操作
     * @param mapSupplier   最终需要的map类型
     * @param <T>           实体类型
     * @param <K>           map中key的类型
     * @param <U>           map中value的类型
     * @param <M>           map的类型
     * @return 对null友好的 toMap 操作的 {@link Collector}实现
     */
    public static <T, K, U, M extends Map<K, U>> Collector<T, ?, M> toMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper,
            BinaryOperator<U> mergeFunction,
            Supplier<M> mapSupplier) {
        BiConsumer<M, T> accumulator =
                (map, element) ->
                        map.put(
                                Optional.ofNullable(element).map(keyMapper).get(),
                                Optional.ofNullable(element).map(valueMapper).get());
        return new SimpleCollector<>(mapSupplier, accumulator, mapMerger(mergeFunction), CH_ID);
    }

    /**
     * 用户合并map的BinaryOperator，传入合并前需要对value进行的操作
     *
     * @param mergeFunction 合并前需要对value进行的操作
     * @param <K>           key的类型
     * @param <V>           value的类型
     * @param <M>           map
     * @return 用户合并map的BinaryOperator
     */
    public static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger(
            BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Map.Entry<K, V> e : m2.entrySet()) {
                m1.merge(e.getKey(), e.getValue(), mergeFunction);
            }
            return m1;
        };
    }

    /**
     * 聚合这种数据类型:{@code Collection<Map<K,V>> => Map<K,List<V>>} 其中key相同的value，会累加到List中
     *
     * @param <K> key的类型
     * @param <V> value的类型
     * @return 聚合后的map
     */
    public static <K, V> Collector<Map<K, V>, ?, Map<K, List<V>>> reduceListMap() {
        return reduceListMap(HashMap::new);
    }

    /**
     * 聚合这种数据类型:{@code Collection<Map<K,V>> => Map<K,List<V>>} 其中key相同的value，会累加到List中
     *
     * @param mapSupplier 可自定义map的类型如concurrentHashMap等
     * @param <K>         key的类型
     * @param <V>         value的类型
     * @param <R>         返回值的类型
     * @return 聚合后的map
     */
    public static <K, V, R extends Map<K, List<V>>> Collector<Map<K, V>, ?, R> reduceListMap(
            final Supplier<R> mapSupplier) {
        return Collectors.reducing(
                mapSupplier.get(),
                value -> {
                    final R result = mapSupplier.get();
                    value.forEach((k, v) -> result.computeIfAbsent(k, i -> new ArrayList<>()).add(v));
                    return result;
                },
                (l, r) -> {
                    r.forEach((k, v) -> l.computeIfAbsent(k, i -> new ArrayList<>()).addAll(v));
                    return l;
                });
    }
}

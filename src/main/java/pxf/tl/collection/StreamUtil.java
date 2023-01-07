package pxf.tl.collection;


import pxf.tl.api.Charsets;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.help.Assert;
import pxf.tl.iter.AnyIter;
import pxf.tl.util.ToolString;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * {@link Stream} 工具类
 *
 * @author potatoxf
 */
public class StreamUtil {

    /**
     * 说明已包含IDENTITY_FINISH特征 为 Characteristics.IDENTITY_FINISH 的缩写
     */
    public static final Set<Collector.Characteristics> CH_ID =
            Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
    /**
     * 说明不包含IDENTITY_FINISH特征
     */
    public static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

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

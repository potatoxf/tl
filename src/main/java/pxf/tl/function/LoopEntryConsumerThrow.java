package pxf.tl.function;


/**
 * 针对两个参数做相应的操作，例如Map中的KEY和VALUE
 *
 * @param <K> KEY类型
 * @param <V> VALUE类型
 * @author potatoxf
 */
@FunctionalInterface
public interface LoopEntryConsumerThrow<K, V, E extends Throwable> extends LoopEntryConsumer<K, V> {

    /**
     * 接受并处理一个参数
     *
     * @param value 参数值
     * @param index 参数在集合中的索引
     */
    boolean acceptThrow(K key, V value, int index) throws E;

    /**
     * 接受并处理一对参数
     *
     * @param key   键
     * @param value 值
     * @param index 参数在集合中的索引
     */
    @Override
    default boolean accept(K key, V value, int index) {
        try {
            return acceptThrow(key, value, index);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}

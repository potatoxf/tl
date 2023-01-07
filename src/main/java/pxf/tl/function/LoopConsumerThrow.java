package pxf.tl.function;

/**
 * 针对一个参数做相应的操作<br>
 * 此函数接口与JDK8中Consumer不同是多提供了index参数，用于标记遍历对象是第几个。
 *
 * @param <T> 处理参数类型
 * @author potatoxf
 */
@FunctionalInterface
public interface LoopConsumerThrow<T, E extends Throwable> extends LoopConsumer<T> {

    /**
     * 接受并处理一个参数
     *
     * @param value 参数值
     * @param index 参数在集合中的索引
     */
    Boolean acceptThrow(T value, int index) throws E;

    /**
     * 接受并处理一个参数
     *
     * @param value 参数值
     * @param index 参数在集合中的索引
     */
    @Override
    default Boolean accept(T value, int index) {
        try {
            return acceptThrow(value, index);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}

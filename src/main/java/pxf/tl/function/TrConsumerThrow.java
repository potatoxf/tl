package pxf.tl.function;

/**
 * 3参数Consumer
 *
 * @param <P1> 参数一类型
 * @param <P2> 参数二类型
 * @param <P3> 参数三类型
 * @author potatoxf
 */
@FunctionalInterface
public interface TrConsumerThrow<P1, P2, P3, E extends Throwable> extends TrConsumer<P1, P2, P3> {

    /**
     * 接收参数方法
     *
     * @param p1 参数一
     * @param p2 参数二
     * @param p3 参数三
     */
    void acceptThrow(P1 p1, P2 p2, P3 p3) throws E;

    /**
     * 接收参数方法
     *
     * @param p1 参数一
     * @param p2 参数二
     * @param p3 参数三
     */
    @Override
    default void accept(P1 p1, P2 p2, P3 p3) {
        try {
            acceptThrow(p1, p2, p3);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}

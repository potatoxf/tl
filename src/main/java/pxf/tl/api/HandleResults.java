package pxf.tl.api;


import pxf.tl.function.ConsumerThrow;
import pxf.tl.function.FunctionThrow;
import pxf.tl.function.VaryConsumerThrow;
import pxf.tl.function.VaryFunctionThrow;
import pxf.tl.help.Valid;

import java.util.*;

/**
 * @author potatoxf
 */
@SuppressWarnings("unchecked")
public class HandleResults<R> extends HandleResult<List<HandleResult<R>>>
        implements Iterable<HandleResult<R>> {
    /**
     * 是否全部正常完成
     */
    private boolean isAllNormalFinish = true;
    /**
     * 索引
     */
    private int index = 0;

    public HandleResults() {
        super.set(new LinkedList<>());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final T[] inputs, final VaryConsumerThrow<E> loopBody) {
        return new HandleResults<R>().loopExecute(inputs, loopBody);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final T[] inputs, final VaryConsumerThrow<E> loopBody, final int startIndex) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, startIndex);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param initBody 初始化执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final T[] inputs,
            final VaryConsumerThrow<E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, initBody);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final T[] inputs,
            final VaryConsumerThrow<E> loopBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param initBody   初始化执行体
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final T[] inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, startIndex, initBody);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final T[] inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, startIndex, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final T[] inputs,
            final VaryConsumerThrow<E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, initBody, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final T[] inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return new HandleResults<R>()
                .loopExecute(inputs, loopBody, startIndex, initBody, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterable<T> inputs, final VaryConsumerThrow<E> loopBody) {
        return new HandleResults<R>().loopExecute(inputs, loopBody);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterable<T> inputs, final VaryConsumerThrow<E> loopBody, final int startIndex) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, startIndex);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param initBody 初始化执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterable<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, initBody);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterable<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param initBody   初始化执行体
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterable<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, startIndex, initBody);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterable<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, startIndex, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterable<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, initBody, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterable<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return new HandleResults<R>()
                .loopExecute(inputs, loopBody, startIndex, initBody, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterator<T> inputs, final VaryConsumerThrow<E> loopBody) {
        return new HandleResults<R>().loopExecute(inputs, loopBody);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterator<T> inputs, final VaryConsumerThrow<E> loopBody, final int startIndex) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, startIndex);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param initBody 初始化执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterator<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, initBody);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterator<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param initBody   初始化执行体
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterator<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, startIndex, initBody);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterator<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, startIndex, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterator<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return new HandleResults<R>().loopExecute(inputs, loopBody, initBody, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public static <T, R, E extends Throwable> HandleResults<R> forLoop(
            final Iterator<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return new HandleResults<R>()
                .loopExecute(inputs, loopBody, startIndex, initBody, exceptionHandler);
    }

    @Override
    public Iterator<HandleResult<R>> iterator() {
        return get().iterator();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // 数组类型
    // ------------------------------------------------------------------------------------------------------------------

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs, final VaryConsumerThrow<E> loopBody) {
        return loopExecute(inputs, loopBody, 0, null, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs, final VaryConsumerThrow<E> loopBody, final int startIndex) {
        return loopExecute(inputs, loopBody, startIndex, null, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param initBody 初始化执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs,
            final VaryConsumerThrow<E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return loopExecute(inputs, loopBody, 0, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs,
            final VaryConsumerThrow<E> loopBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, 0, null, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param initBody   初始化执行体
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return loopExecute(inputs, loopBody, startIndex, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, startIndex, null, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs,
            final VaryConsumerThrow<E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, 0, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(
                inputs, loopBody.toVaryFunctionThrow(), startIndex, initBody, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs, final VaryFunctionThrow<R, E> loopBody) {
        return loopExecute(inputs, loopBody, 0, null, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs, final VaryFunctionThrow<R, E> loopBody, final int startIndex) {
        return loopExecute(inputs, loopBody, startIndex, null, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param initBody 初始化执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return loopExecute(inputs, loopBody, 0, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, 0, null, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param initBody   初始化执行体
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return loopExecute(inputs, loopBody, startIndex, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final int startIndex,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, startIndex, null, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, 0, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final T[] inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(new Tuple<>(inputs), loopBody, startIndex, initBody, exceptionHandler);
    }

    // ------------------------------------------------------------------------------------------------------------------
    // 可迭代类型
    // ------------------------------------------------------------------------------------------------------------------

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs, final VaryConsumerThrow<E> loopBody) {
        return loopExecute(inputs, loopBody, 0, null, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs, final VaryConsumerThrow<E> loopBody, final int startIndex) {
        return loopExecute(inputs, loopBody, startIndex, null, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param initBody 初始化执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return loopExecute(inputs, loopBody, 0, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, 0, null, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param initBody   初始化执行体
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return loopExecute(inputs, loopBody, startIndex, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, startIndex, null, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, 0, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(
                inputs, loopBody.toVaryFunctionThrow(), startIndex, initBody, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs, final VaryFunctionThrow<R, E> loopBody) {
        return loopExecute(inputs, loopBody, 0, null, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs, final VaryFunctionThrow<R, E> loopBody, final int startIndex) {
        return loopExecute(inputs, loopBody, startIndex, null, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param initBody 初始化执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return loopExecute(inputs, loopBody, 0, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, 0, null, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param initBody   初始化执行体
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return loopExecute(inputs, loopBody, startIndex, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final int startIndex,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, startIndex, null, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, 0, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterable<T> inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(
                Valid.val(inputs).iterator(), loopBody, startIndex, initBody, exceptionHandler);
    }

    // ------------------------------------------------------------------------------------------------------------------
    // 迭代器类型
    // ------------------------------------------------------------------------------------------------------------------

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs, final VaryConsumerThrow<E> loopBody) {
        return loopExecute(inputs, loopBody, 0, null, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs, final VaryConsumerThrow<E> loopBody, final int startIndex) {
        return loopExecute(inputs, loopBody, startIndex, null, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param initBody 初始化执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return loopExecute(inputs, loopBody, 0, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, 0, null, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param initBody   初始化执行体
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return loopExecute(inputs, loopBody, startIndex, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, startIndex, null, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, 0, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs,
            final VaryConsumerThrow<E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(
                inputs, loopBody.toVaryFunctionThrow(), startIndex, initBody, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs, final VaryFunctionThrow<R, E> loopBody) {
        return loopExecute(inputs, loopBody, 0, null, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs, final VaryFunctionThrow<R, E> loopBody, final int startIndex) {
        return loopExecute(inputs, loopBody, startIndex, null, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs   循环迭代器
     * @param loopBody 循环执行体
     * @param initBody 初始化执行体
     * @param <T>      数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return loopExecute(inputs, loopBody, 0, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, 0, null, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs     循环迭代器
     * @param loopBody   循环执行体
     * @param startIndex 起始索引
     * @param initBody   初始化执行体
     * @param <T>        数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody) {
        return loopExecute(inputs, loopBody, startIndex, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final int startIndex,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, startIndex, null, exceptionHandler);
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        return loopExecute(inputs, loopBody, 0, initBody, this.getDefaultExceptionHandler());
    }

    /**
     * 循环执行逻辑
     *
     * @param inputs           循环迭代器
     * @param loopBody         循环执行体
     * @param startIndex       起始索引
     * @param initBody         初始化执行体
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param <T>              数据类型
     * @return {@code BatchHandleResult<R>}
     */
    public <T, E extends Throwable> HandleResults<R> loopExecute(
            final Iterator<T> inputs,
            final VaryFunctionThrow<R, E> loopBody,
            final int startIndex,
            final ConsumerThrow<Map<Object, Object>, Throwable> initBody,
            final FunctionThrow<E, Throwable, RuntimeException> exceptionHandler) {
        if (inputs == null) {
            throw new IllegalArgumentException("The iterator must be not null");
        } else if (inputs.hasNext()) {
            reset();
            Map<Object, Object> container = new HashMap<>();
            try {
                if (initBody != null) {
                    initBody.accept(container);
                }
                T data;
                LoopEvent loopEvent;
                index = startIndex;
                for (; inputs.hasNext(); index++) {
                    data = inputs.next();
                    HandleResult<R> handleResult = HandleResult.of();
                    if (index == 0) {
                        if (!inputs.hasNext()) {
                            loopEvent = LoopEvent.ONLY;
                        } else {
                            loopEvent = LoopEvent.FIRST;
                        }
                    } else {
                        if (!inputs.hasNext()) {
                            loopEvent = LoopEvent.LAST;
                        } else {
                            loopEvent = LoopEvent.ELEMENT;
                        }
                    }
                    handleResult.onceExecute(loopBody, exceptionHandler, data, index, loopEvent, container);
                    get().add(handleResult);
                }
            } catch (Throwable e) {
                failure(e);
            }
        }
        return this;
    }

    /**
     * 返回索引
     *
     * @return 返回索引
     */
    public int getIndex() {
        return index;
    }

    /**
     * 是否所有执行都正常完成
     *
     * @return 如果是返回true，否则返回false
     */
    public boolean isAllNormalFinish() {
        return isAllNormalFinish;
    }

    /**
     * Set a value
     *
     * @param value the non-null value
     */
    @Override
    public void set(List<HandleResult<R>> value) {
        throw new UnsupportedOperationException();
    }

    /**
     * 重置
     */
    @Override
    public void reset() {
        setException(null);
        get().clear();
        isAllNormalFinish = true;
        index = 0;
    }

    /**
     * 设置执行失败返回的异常
     *
     * @param exception 异常
     */
    @Override
    protected void failure(Throwable exception) {
        super.failure(exception);
        isAllNormalFinish = false;
    }

    private <E extends Throwable>
    FunctionThrow<E, Throwable, RuntimeException> getDefaultExceptionHandler() {
        return (FunctionThrow<E, Throwable, RuntimeException>) HandleResult.DEFAULT_EXCEPTION_HANDLER;
    }
}

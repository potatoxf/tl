package pxf.tl.api;


import pxf.tl.function.FunctionThrow;
import pxf.tl.function.VaryConsumerThrow;
import pxf.tl.function.VaryFunctionThrow;

/**
 * 处理结果返回类
 *
 * @author potatoxf
 */
@SuppressWarnings("unchecked")
public class HandleResult<R> extends MutableObject<R> {
    public static final FunctionThrow<Throwable, Throwable, RuntimeException>
            DEFAULT_EXCEPTION_HANDLER = throwable -> throwable;
    /**
     * 是否有异常
     */
    private Throwable exception;

    protected HandleResult() {
    }

    /**
     * @param <T>
     * @return
     */
    public static <T> HandleResult<T> of() {
        return new HandleResult<>();
    }

    /**
     * 执行一次逻辑
     *
     * @param logic 执行逻辑
     * @param args  执行参数
     * @return {@code HandleResult<R>}
     */
    public static <R> HandleResult<R> forOne(VaryFunctionThrow<R, Throwable> logic, Object... args) {
        return new HandleResult<R>().onceExecute(logic, args);
    }

    /**
     * 执行一次逻辑
     *
     * @param logic 执行逻辑
     * @param args  执行参数
     * @return {@code HandleResult<R>}
     */
    public static <R> HandleResult<R> forOne(VaryConsumerThrow<Throwable> logic, Object... args) {
        return new HandleResult<R>().onceExecute(logic, args);
    }

    /**
     * 执行一次逻辑
     *
     * @param logic            执行逻辑
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param args             执行参数
     * @return {@code HandleResult<R>}
     */
    public static <R, E extends Throwable> HandleResult<R> forOne(
            VaryFunctionThrow<R, E> logic,
            FunctionThrow<E, Throwable, RuntimeException> exceptionHandler,
            Object... args) {
        return new HandleResult<R>().onceExecute(logic, exceptionHandler, args);
    }

    /**
     * 执行一次逻辑
     *
     * @param logic            执行逻辑
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param args             执行参数
     * @return {@code HandleResult<R>}
     */
    public static <R, E extends Throwable> HandleResult<R> forOne(
            VaryConsumerThrow<E> logic,
            FunctionThrow<E, Throwable, RuntimeException> exceptionHandler,
            Object... args) {
        return new HandleResult<R>().onceExecute(logic, exceptionHandler, args);
    }

    /**
     * 是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return exception == null;
    }

    /**
     * has exception
     *
     * @return {@code true} if there is exception, otherwise {@code false}
     */
    public boolean hasException() {
        return exception != null;
    }

    /**
     * get exception
     *
     * @return the exception
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Set exception
     *
     * @param exception the non-null exception
     */
    protected HandleResult<R> setException(Throwable exception) {
        this.exception = exception;
        return this;
    }

    /**
     * 执行一次逻辑
     *
     * @param logic 执行逻辑
     * @param args  执行参数
     * @return {@code HandleResult<R>}
     */
    public HandleResult<R> onceExecute(VaryFunctionThrow<R, Throwable> logic, Object... args) {
        return onceExecute(logic, DEFAULT_EXCEPTION_HANDLER, args);
    }

    /**
     * 执行一次逻辑
     *
     * @param logic 执行逻辑
     * @param args  执行参数
     * @return {@code HandleResult<R>}
     */
    public HandleResult<R> onceExecute(VaryConsumerThrow<Throwable> logic, Object... args) {
        return onceExecute(logic, DEFAULT_EXCEPTION_HANDLER, args);
    }

    /**
     * 执行一次逻辑
     *
     * @param logic            执行逻辑
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param args             执行参数
     * @return {@code HandleResult<R>}
     */
    public <E extends Throwable> HandleResult<R> onceExecute(
            VaryFunctionThrow<R, E> logic,
            FunctionThrow<E, Throwable, RuntimeException> exceptionHandler,
            Object... args) {
        try {
            R r = logic.apply(args);
            if (r != null) {
                success(r);
            }
        } catch (Throwable e) {
            if (exceptionHandler != null) {
                Throwable throwable = exceptionHandler.apply((E) e);
                if (throwable != null) {
                    failure(throwable);
                }
            } else {
                failure(e);
            }
        }
        return this;
    }

    /**
     * 执行一次逻辑
     *
     * @param logic            执行逻辑
     * @param exceptionHandler 异常处理器，返回对执行逻辑异常的处理，如果返回null，则不记录异常信息
     * @param args             执行参数
     * @return {@code HandleResult<R>}
     */
    public <E extends Throwable> HandleResult<R> onceExecute(
            VaryConsumerThrow<E> logic,
            FunctionThrow<E, Throwable, RuntimeException> exceptionHandler,
            Object... args) {
        try {
            logic.accept(args);
        } catch (Throwable e) {
            if (exceptionHandler != null) {
                Throwable throwable = exceptionHandler.apply((E) e);
                if (throwable != null) {
                    failure(throwable);
                }
            } else {
                failure(e);
            }
        }
        return this;
    }

    /**
     * 设置成功执行返回数据，没有则为null
     *
     * @param data 数据
     */
    protected void success(R data) {
        set(data);
    }

    /**
     * 设置执行失败返回的异常
     *
     * @param exception 异常
     */
    protected void failure(Throwable exception) {
        setException(exception);
    }

    /**
     * 重置
     */
    public void reset() {
        setException(null);
        set(null);
    }
}

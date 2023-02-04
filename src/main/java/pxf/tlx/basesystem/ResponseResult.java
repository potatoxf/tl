package pxf.tlx.basesystem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.HttpStatus;
import pxf.tl.api.Literal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static pxf.tlx.basesystem.ResponseResultStatus.CODE_FAIL;
import static pxf.tlx.basesystem.ResponseResultStatus.CODE_OK;

/**
 * Api调用返回结果
 *
 * @author potatoxf
 */
@ApiModel(value = "REST返回结构数据", description = "REST返回结构数据")
public class ResponseResult<T> {
    /**
     * 是否成功
     */
    @ApiModelProperty("调用是否成功")
    private final boolean success;
    /**
     * 编码
     */
    @ApiModelProperty("返回状态代码")
    private final int code;
    /**
     * 返回处理信息
     */
    @Nonnull
    @ApiModelProperty("返回消息")
    private final String msg;
    /**
     * 数据
     */
    @Nullable
    @ApiModelProperty("返回数据")
    private final T data;

    private ResponseResult(boolean success, int code, @Nonnull String msg, @Nullable T data) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造失败API结果
     *
     * @param message 消息信息
     * @return {@code ApiResult<?>}
     */
    @Nonnull
    public static ResponseResult<?> ofFail(@Nullable String message) {
        return ofFail(CODE_FAIL, message, null);
    }

    /**
     * 构造失败API结果
     *
     * @param literal 常量
     * @return {@code ApiResult<?>}
     */
    @Nonnull
    public static ResponseResult<?> ofFail(@Nonnull Literal<?> literal) {
        return ofFail(literal.getCode(), literal.getMessage());
    }

    /**
     * 构造失败API结果
     *
     * @param code    API结果码
     * @param message 消息信息
     * @return {@code ApiResult<?>}
     */
    @Nonnull
    public static ResponseResult<?> ofFail(int code, @Nullable String message) {
        return ofFail(code, message, null);
    }

    /**
     * 构造失败API结果
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return {@code ApiResult<T>}
     */
    @Nonnull
    public static <T> ResponseResult<T> ofFail(int code, @Nullable String message, @Nullable T data) {
        return of(false, code, message, data);
    }

    /**
     * 构造成功API结果
     *
     * @return {@code ApiResult<?>}
     */
    @Nonnull
    public static ResponseResult<?> ofSuccess() {
        return ofSuccess(CODE_OK, null, null);
    }

    /**
     * 构造成功API结果
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return {@code ApiResult<T>}
     */
    @Nonnull
    public static <T> ResponseResult<T> ofSuccess(@Nullable T data) {
        return ofSuccess(CODE_OK, null, data);
    }

    /**
     * 构造成功API结果
     *
     * @param literal 常量
     * @param data    数据
     * @param <T>     数据类型
     * @return {@code ApiResult<T>}
     */
    @Nonnull
    public static <T> ResponseResult<T> ofSuccess(@Nonnull Literal<?> literal, @Nullable T data) {
        return ofSuccess(literal.getCode(), literal.getMessage(), data);
    }

    /**
     * 构造成功API结果
     *
     * @param code    API结果码
     * @param message 消息信息
     * @param data    数据
     * @param <T>     数据类型
     * @return {@code ApiResult<T>}
     */
    @Nonnull
    public static <T> ResponseResult<T> ofSuccess(int code, @Nullable String message, @Nullable T data) {
        return of(true, code, message, data);
    }

    /**
     * 构造API结果
     *
     * @param httpStatus HTTP状态码
     * @return {@code ApiResult<T>}
     */
    @Nonnull
    public static ResponseResult<?> of(@Nonnull HttpStatus httpStatus) {
        return of(httpStatus.series() == HttpStatus.Series.SUCCESSFUL, httpStatus.value(), null, null);
    }

    /**
     * 构造API结果
     *
     * @param httpStatus HTTP状态码
     * @param message    消息信息
     * @param data       数据
     * @param <T>        数据类型
     * @return {@code ApiResult<T>}
     */
    @Nonnull
    public static <T> ResponseResult<?> of(@Nonnull HttpStatus httpStatus, @Nullable String message, @Nullable T data) {
        return of(httpStatus.series() == HttpStatus.Series.SUCCESSFUL, httpStatus.value(), message, data);
    }

    /**
     * 构造API结果
     *
     * @param success 是否成功
     * @return {@code ApiResult<T>}
     */
    @Nonnull
    public static ResponseResult<?> of(boolean success) {
        return of(success, success ? CODE_OK : CODE_FAIL, null, null);
    }

    /**
     * 构造API结果
     *
     * @param responseResult 是否成功
     * @param message        消息信息
     * @param <T>            数据类型
     * @return {@code ApiResult<T>}
     */
    @Nonnull
    public static <T> ResponseResult<T> copyExceptMessage(
            @Nonnull ResponseResult<T> responseResult, @Nullable String message) {
        return of(responseResult.success, responseResult.code, message, responseResult.data);
    }

    /**
     * 构造API结果
     *
     * @param responseResult 是否成功
     * @param data           数据
     * @param <T>            数据类型
     * @return {@code ApiResult<T>}
     */
    @Nonnull
    public static <T> ResponseResult<T> copyExceptData(@Nonnull ResponseResult<?> responseResult, @Nullable T data) {
        return of(responseResult.success, responseResult.code, responseResult.msg, data);
    }

    /**
     * 构造API结果
     *
     * @param success 是否成功
     * @param literal 常量
     * @param data    数据
     * @param <T>     数据类型
     * @return {@code ApiResult<T>}
     */
    @Nonnull
    public static <T> ResponseResult<T> of(
            boolean success, @Nonnull Literal<?> literal, @Nullable T data) {
        return of(success, literal.getCode(), literal.getMessage(), data);
    }

    /**
     * 构造API结果
     *
     * @param success 是否成功
     * @param code    API结果码
     * @param message 消息信息
     * @param data    数据
     * @param <T>     数据类型
     * @return {@code ApiResult<T>}
     */
    @Nonnull
    public static <T> ResponseResult<T> of(
            boolean success, int code, @Nullable String message, @Nullable T data) {
        if (success) {
            return new ResponseResult<>(true, code, Objects.requireNonNullElseGet(message, ResponseResultStatus.OK::getMessage), data);
        } else {
            return new ResponseResult<>(false, code, Objects.requireNonNullElseGet(message, ResponseResultStatus.OK::getMessage), data);
        }
    }

    /**
     * 判断返回结果是否成功
     *
     * @return {@code boolean}
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 获取API结果码
     *
     * @return {@code int}
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取API结果信息
     *
     * @return {@code int}
     */
    @Nonnull
    public String getMsg() {
        return msg;
    }

    /**
     * 获取API结果数据
     *
     * @return {@code T}
     */
    @Nullable
    public T getData() {
        return data;
    }
}

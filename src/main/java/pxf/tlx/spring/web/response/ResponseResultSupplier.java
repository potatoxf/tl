package pxf.tlx.spring.web.response;

import pxf.tlx.basesystem.ResponseResult;

/**
 * API结果异常接口
 *
 * <p>用于处理带有{@code ApiResultException}的异常来返回前端相应的{@code ApiResult}
 *
 * @author potatoxf
 */
public interface ResponseResultSupplier {

    /**
     * 获取异常
     *
     * @return {@code ApiResult<?>}
     */
    ResponseResult<?> getResponseResult();
}

package pxf.tlx.mybatis;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 自动填充值处理器
 *
 * @author potatoxf
 */
public interface AutoFillValueHandler {

    /**
     * 处理值
     *
     * @param object    对象
     * @param value     值，可能为null
     * @param valueType 值类型，可能为null，当object是Map时，并且不存在该值
     * @param isInsert  是否是插入还是更新
     * @return 处理后的值
     * @throws Throwable 处理值发生异常
     */
    Object handle(@Nonnull Object object, @Nullable Object value, @Nullable Class<?> valueType, boolean isInsert) throws Throwable;
}

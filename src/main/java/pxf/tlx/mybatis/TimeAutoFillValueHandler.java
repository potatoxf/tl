package pxf.tlx.mybatis;

import pxf.tl.convert.Convert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author potatoxf
 */
public class TimeAutoFillValueHandler implements AutoFillValueHandler {
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
    @Override
    public Object handle(@Nonnull Object object, @Nullable Object value, @Nullable Class<?> valueType, boolean isInsert) throws Throwable {
        if (valueType != null) {
            return Convert.convert(valueType, new Date());
        } else if (value != null) {
            return Convert.convert(value.getClass(), new Date());
        }
        return value;
    }
}

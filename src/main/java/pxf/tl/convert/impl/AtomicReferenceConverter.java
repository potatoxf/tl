package pxf.tl.convert.impl;


import pxf.tl.convert.AbstractConverter;
import pxf.tl.convert.ConverterRegistry;
import pxf.tl.util.ToolBytecode;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link AtomicReference}转换器
 *
 * @author potatoxf
 */
@SuppressWarnings("rawtypes")
public class AtomicReferenceConverter extends AbstractConverter<AtomicReference> {
    private static final long serialVersionUID = 1L;

    @Override
    protected AtomicReference<?> convertInternal(Object value) {

        // 尝试将值转换为Reference泛型的类型
        Object targetValue = null;
        final Type paramType = ToolBytecode.getTypeArgument(AtomicReference.class);
        if (false == ToolBytecode.isUnknown(paramType)) {
            targetValue = ConverterRegistry.getInstance().convert(paramType, value);
        }
        if (null == targetValue) {
            targetValue = value;
        }

        return new AtomicReference<>(targetValue);
    }
}

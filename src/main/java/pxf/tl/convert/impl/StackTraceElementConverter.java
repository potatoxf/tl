package pxf.tl.convert.impl;


import pxf.tl.collection.map.MapUtil;
import pxf.tl.convert.AbstractConverter;
import pxf.tl.help.Safe;

import java.util.Map;

/**
 * {@link StackTraceElement} 转换器<br>
 * 只支持Map方式转换
 *
 * @author potatoxf
 */
public class StackTraceElementConverter extends AbstractConverter<StackTraceElement> {
    private static final long serialVersionUID = 1L;

    @Override
    protected StackTraceElement convertInternal(Object value) {
        if (value instanceof Map) {
            final Map<?, ?> map = (Map<?, ?>) value;

            final String declaringClass = MapUtil.getStr(map, "className");
            final String methodName = MapUtil.getStr(map, "methodName");
            final String fileName = MapUtil.getStr(map, "fileName");
            final Integer lineNumber = MapUtil.getInt(map, "lineNumber");

            return new StackTraceElement(
                    declaringClass, methodName, fileName, Safe.value(lineNumber, 0));
        }
        return null;
    }
}

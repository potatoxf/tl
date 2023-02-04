package pxf.tl.convert.impl;


import pxf.tl.api.PoolOfCharacter;
import pxf.tlx.codec.codec.Base64;
import pxf.tl.convert.AbstractConverter;
import pxf.tl.convert.Convert;
import pxf.tl.help.New;
import pxf.tl.iter.AnyIter;
import pxf.tl.util.ToolArray;
import pxf.tl.util.ToolByte;
import pxf.tl.util.ToolObject;
import pxf.tl.util.ToolString;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

/**
 * 数组转换器，包括原始类型数组
 *
 * @author potatoxf
 */
public class ArrayConverter extends AbstractConverter<Object> {
    private static final long serialVersionUID = 1L;

    private final Class<?> targetType;
    /**
     * 目标元素类型
     */
    private final Class<?> targetComponentType;

    /**
     * 是否忽略元素转换错误
     */
    private boolean ignoreElementError;

    /**
     * 构造
     *
     * @param targetType 目标数组类型
     */
    public ArrayConverter(Class<?> targetType) {
        this(targetType, false);
    }

    /**
     * 构造
     *
     * @param targetType         目标数组类型
     * @param ignoreElementError 是否忽略元素转换错误
     */
    public ArrayConverter(Class<?> targetType, boolean ignoreElementError) {
        if (null == targetType) {
            // 默认Object数组
            targetType = Object[].class;
        }

        if (targetType.isArray()) {
            this.targetType = targetType;
            this.targetComponentType = targetType.getComponentType();
        } else {
            // 用户传入类为非数组时，按照数组元素类型对待
            this.targetComponentType = targetType;
            this.targetType = ToolArray.getArrayType(targetType);
        }

        this.ignoreElementError = ignoreElementError;
    }

    @Override
    protected Object convertInternal(Object value) {
        return value.getClass().isArray() ? convertArrayToArray(value) : convertObjectToArray(value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class getTargetType() {
        return this.targetType;
    }

    /**
     * 设置是否忽略元素转换错误
     *
     * @param ignoreElementError 是否忽略元素转换错误
     */
    public void setIgnoreElementError(boolean ignoreElementError) {
        this.ignoreElementError = ignoreElementError;
    }

    // -------------------------------------------------------------------------------------- Private
    // method start

    /**
     * 数组对数组转换
     *
     * @param array 被转换的数组值
     * @return 转换后的数组
     */
    private Object convertArrayToArray(Object array) {
        final Class<?> valueComponentType = ToolArray.getComponentType(array);

        if (valueComponentType == targetComponentType) {
            return array;
        }

        final int len = ToolArray.length(array);
        final Object result = Array.newInstance(targetComponentType, len);

        for (int i = 0; i < len; i++) {
            Array.set(result, i, convertComponentType(Array.get(array, i)));
        }
        return result;
    }

    /**
     * 非数组对数组转换
     *
     * @param value 被转换值
     * @return 转换后的数组
     */
    private Object convertObjectToArray(Object value) {
        if (value instanceof CharSequence) {
            if (targetComponentType == char.class || targetComponentType == Character.class) {
                return convertArrayToArray(value.toString().toCharArray());
            }

            // issue#2365
            // 字符串转bytes，首先判断是否为Base64，是则转换，否则按照默认getBytes方法。
            if (targetComponentType == byte.class) {
                final String str = value.toString();
                if (Base64.isBase64(str)) {
                    return Base64.decode(value.toString());
                }
                return str.getBytes();
            }

            // 单纯字符串情况下按照逗号分隔后劈开
            final String[] strings = ToolString.split(value.toString(), PoolOfCharacter.COMMA);
            return convertArrayToArray(strings);
        }

        Object result;
        switch (value) {
            case Collection c -> {
                result = AnyIter.ofObject(false, c, targetComponentType).toArray(targetComponentType);
            }
            case Iterable iterable -> {
                result = AnyIter.ofObject(false, iterable, targetComponentType).toArray(targetComponentType);
            }
            case Iterator iterator -> {
                result = AnyIter.ofObject(false, iterator, targetComponentType).toArray(targetComponentType);
            }
            case Number n && byte.class == targetComponentType ->
                    // 用户可能想序列化指定对象
                    result = ToolByte.numberToBytes((Number) value);
            case Serializable s && byte.class == targetComponentType ->
                    // 用户可能想序列化指定对象
                    result = ToolObject.serialize(value);
            case null, default ->
                    // everything else:
                    result = convertToSingleElementArray(value);
        }

        return result;
    }

    /**
     * 单元素数组
     *
     * @param value 被转换的值
     * @return 数组，只包含一个元素
     */
    private Object[] convertToSingleElementArray(Object value) {
        final Object[] singleElementArray = New.array(targetComponentType, 1);
        singleElementArray[0] = convertComponentType(value);
        return singleElementArray;
    }

    /**
     * 转换元素类型
     *
     * @param value 值
     * @return 转换后的值，转换失败若{@link #ignoreElementError}为true，返回null，否则抛出异常
     */
    private Object convertComponentType(Object value) {
        return Convert.convertWithCheck(this.targetComponentType, value, null, this.ignoreElementError);
    }
    // -------------------------------------------------------------------------------------- Private
    // method end
}

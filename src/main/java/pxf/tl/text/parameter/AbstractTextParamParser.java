package pxf.tl.text.parameter;


import pxf.tl.help.Whether;
import pxf.tl.util.ToolBytecode;

/**
 * 抽象文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
abstract class AbstractTextParamParser<T> implements TextParamParser<T> {

    /**
     * 获取支持结果类型
     *
     * @return 返回 {@code Class<?>}
     */
    @Override
    public Class<?> supportResultType() {
        return ToolBytecode.extractGenericClass(getClass(), AbstractTextParamParser.class, 0);
    }

    /**
     * 是否是支持的结果类型
     *
     * @param value 测试值
     * @return 如果是返回 {@code true}，否则 {@code false}
     */
    @Override
    public boolean isSupportResultType(Object value) {
        if (value instanceof Class) {
            return supportResultType().equals(value);
        } else {
            return supportResultType().isInstance(value);
        }
    }

    /**
     * 解析单个值，如果不成功则使用默认值
     *
     * @param input        输入字符串
     * @param defaultValue 默认值
     * @return 返回解析后的结果
     */
    @Override
    public T parseValue(String input, T defaultValue) {
        if (Whether.empty(input)) {
            return defaultValue;
        }
        input = input.trim();
        try {
            return doParseValue(input);
        } catch (Throwable e) {
            return defaultValue;
        }
    }

    /**
     * 解析单个值
     *
     * @param input 输入字符串
     * @return 返回解析后的结果
     * @throws Throwable 解析出现异常
     */
    protected abstract T doParseValue(String input) throws Throwable;
}

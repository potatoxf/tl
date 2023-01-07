package pxf.tl.text.parameter;

/**
 * 文本参数解析器
 *
 * @author potatoxf
 */
public interface TextParamParser<T> {

    /**
     * 获取支持结果类型
     *
     * @return 返回 {@code Class<?>}
     */
    Class<?> supportResultType();

    /**
     * 是否是支持的结果类型
     *
     * @param value 测试值
     * @return 如果是返回 {@code true}，否则 {@code false}
     */
    boolean isSupportResultType(Object value);

    /**
     * 解析单个值，如果不成功则使用默认值
     *
     * @param input        输入字符串
     * @param defaultValue 默认值
     * @return 返回解析后的结果
     */
    T parseValue(String input, T defaultValue);
}

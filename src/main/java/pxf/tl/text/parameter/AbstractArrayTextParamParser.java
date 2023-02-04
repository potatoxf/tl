package pxf.tl.text.parameter;


import pxf.tl.help.Whether;
import pxf.tl.util.ToolString;

/**
 * 抽象文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
abstract class AbstractArrayTextParamParser<T> extends AbstractTextParamParser<T[]> {

    private final AbstractTextParamParser<T> textParamParser;

    public AbstractArrayTextParamParser(AbstractTextParamParser<T> textParamParser) {
        this.textParamParser = textParamParser;
    }

    /**
     * 解析单个值
     *
     * @param input 输入字符串
     * @return 返回解析后的结果
     */
    @Override
    protected T[] doParseValue(String input) {
        String[] strings = ToolString.resolveArrayExpression(input);
        if (Whether.empty(strings)) {
            return null;
        }
        T[] array = createArray(strings.length);
        for (int i = 0; i < strings.length; i++) {
            array[i] = textParamParser.parseValue(strings[i], null);
        }
        return array;
    }

    /**
     * 创建数组
     *
     * @param length 长度
     * @return 返回数组
     */
    protected abstract T[] createArray(int length);
}

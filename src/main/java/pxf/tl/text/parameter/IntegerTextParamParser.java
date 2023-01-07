package pxf.tl.text.parameter;


import pxf.tl.help.Whether;

/**
 * {@code Integer}文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
class IntegerTextParamParser extends AbstractTextParamParser<Integer> {
    public IntegerTextParamParser() {
    }

    @Override
    public boolean isSupportResultType(Object value) {
        return Whether.intObj(value);
    }

    @Override
    public Integer doParseValue(String input) {
        return Integer.parseInt(input);
    }
}

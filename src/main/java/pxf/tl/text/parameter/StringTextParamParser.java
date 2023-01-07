package pxf.tl.text.parameter;

/**
 * {@code String}文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
class StringTextParamParser extends AbstractTextParamParser<String> {
    public StringTextParamParser() {
    }

    @Override
    public String doParseValue(String input) {
        return input;
    }
}

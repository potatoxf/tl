package pxf.tl.text.parameter;

/**
 * {@code Long}文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
class LongTextParamParser extends AbstractTextParamParser<Long> {
    public LongTextParamParser() {
    }

    @Override
    public Long doParseValue(String input) {
        return Long.parseLong(input);
    }
}

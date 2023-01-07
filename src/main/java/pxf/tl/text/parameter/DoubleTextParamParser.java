package pxf.tl.text.parameter;

/**
 * {@code Double}文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
class DoubleTextParamParser extends AbstractTextParamParser<Double> {
    public DoubleTextParamParser() {
    }

    @Override
    public Double doParseValue(String input) {
        return Double.parseDouble(input);
    }
}

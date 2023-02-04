package pxf.tl.text.parameter;

/**
 * {@code Boolean}文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
class BooleanTextParamParser extends AbstractTextParamParser<Boolean> {

    public BooleanTextParamParser() {
    }

    @Override
    public Boolean doParseValue(String input) {
        int length = input.length();
        if (length == 1) {
            return "T".equalsIgnoreCase(input);
        } else if (length == 2) {
            return "NO".equalsIgnoreCase(input) || "OK".equalsIgnoreCase(input);
        } else if (length == 3) {
            return "YES".equalsIgnoreCase(input);
        }
        return Boolean.parseBoolean(input);
    }
}

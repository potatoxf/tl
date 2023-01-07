package pxf.tl.text.parameter;

/**
 * {@code Boolean}文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
class StringArrayTextParamParser extends AbstractArrayTextParamParser<String> {

    static final StringTextParamParser TEXT_PARAM_PARSER = new StringTextParamParser();

    public StringArrayTextParamParser() {
        super(TEXT_PARAM_PARSER);
    }

    /**
     * 创建数组
     *
     * @param length 长度
     * @return 返回数组
     */
    @Override
    protected String[] createArray(int length) {
        return new String[length];
    }
}

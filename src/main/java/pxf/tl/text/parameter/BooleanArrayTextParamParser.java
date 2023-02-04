package pxf.tl.text.parameter;

/**
 * {@code Boolean}文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
class BooleanArrayTextParamParser extends AbstractArrayTextParamParser<Boolean> {

    static final BooleanTextParamParser TEXT_PARAM_PARSER = new BooleanTextParamParser();

    public BooleanArrayTextParamParser() {
        super(TEXT_PARAM_PARSER);
    }

    /**
     * 创建数组
     *
     * @param length 长度
     * @return 返回数组
     */
    @Override
    protected Boolean[] createArray(int length) {
        return new Boolean[length];
    }
}

package pxf.tl.text.parameter;

/**
 * {@code Boolean}文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
class LongArrayTextParamParser extends AbstractArrayTextParamParser<Long> {

    static final LongTextParamParser TEXT_PARAM_PARSER = new LongTextParamParser();

    public LongArrayTextParamParser() {
        super(TEXT_PARAM_PARSER);
    }

    /**
     * 创建数组
     *
     * @param length 长度
     * @return 返回数组
     */
    @Override
    protected Long[] createArray(int length) {
        return new Long[length];
    }
}

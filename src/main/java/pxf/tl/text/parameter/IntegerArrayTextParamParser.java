package pxf.tl.text.parameter;

/**
 * {@code Boolean}文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
class IntegerArrayTextParamParser extends AbstractArrayTextParamParser<Integer> {

    static final IntegerTextParamParser TEXT_PARAM_PARSER = new IntegerTextParamParser();

    public IntegerArrayTextParamParser() {
        super(TEXT_PARAM_PARSER);
    }

    /**
     * 创建数组
     *
     * @param length 长度
     * @return 返回数组
     */
    @Override
    protected Integer[] createArray(int length) {
        return new Integer[length];
    }
}

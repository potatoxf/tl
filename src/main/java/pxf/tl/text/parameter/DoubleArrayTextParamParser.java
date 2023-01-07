package pxf.tl.text.parameter;

/**
 * {@code Boolean}文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
class DoubleArrayTextParamParser extends AbstractArrayTextParamParser<Double> {

    static final DoubleTextParamParser TEXT_PARAM_PARSER = new DoubleTextParamParser();

    public DoubleArrayTextParamParser() {
        super(TEXT_PARAM_PARSER);
    }

    /**
     * 创建数组
     *
     * @param length 长度
     * @return 返回数组
     */
    @Override
    protected Double[] createArray(int length) {
        return new Double[length];
    }
}

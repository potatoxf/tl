package pxf.tl.text.parameter;

/**
 * {@code Boolean}文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
class ClassArrayTextParamParser extends AbstractArrayTextParamParser<Class<?>> {

    static final ClassTextParamParser TEXT_PARAM_PARSER = new ClassTextParamParser();

    public ClassArrayTextParamParser() {
        super(TEXT_PARAM_PARSER);
    }

    /**
     * 创建数组
     *
     * @param length 长度
     * @return 返回数组
     */
    @Override
    protected Class<?>[] createArray(int length) {
        return new Class[length];
    }
}

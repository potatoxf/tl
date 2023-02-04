package pxf.tl.text.parameter;

/**
 * {@code String}文本参数解析器
 *
 * <p>该类线程安全
 *
 * @author potatoxf
 */
class ClassTextParamParser extends AbstractTextParamParser<Class<?>> {

    @Override
    protected Class<?> doParseValue(String input) {
        try {
            return Class.forName(input, false, ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

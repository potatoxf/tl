package pxf.tl.text.parameter;

/**
 * @author potatoxf
 */
public enum TextType {
    /**
     * 文本类型
     */
    STRING(0, StringArrayTextParamParser.TEXT_PARAM_PARSER),
    BOOLEAN(1, BooleanArrayTextParamParser.TEXT_PARAM_PARSER),
    CLASS(2, ClassArrayTextParamParser.TEXT_PARAM_PARSER),
    INTEGER(3, IntegerArrayTextParamParser.TEXT_PARAM_PARSER),
    LONG(4, LongArrayTextParamParser.TEXT_PARAM_PARSER),
    DOUBLE(5, DoubleArrayTextParamParser.TEXT_PARAM_PARSER),
    STRINGS(100, new StringArrayTextParamParser()),
    BOOLEANS(101, new BooleanArrayTextParamParser()),
    CLASSES(102, new ClassArrayTextParamParser()),
    INTEGERS(103, new IntegerArrayTextParamParser()),
    LONGS(104, new LongArrayTextParamParser()),
    DOUBLES(105, new DoubleArrayTextParamParser());

    private final int type;

    private final TextParamParser<?> textParamParser;

    TextType(int type, TextParamParser<?> textParamParser) {
        this.type = type;
        this.textParamParser = textParamParser;
    }

    public int getType() {
        return type;
    }

    public TextParamParser<?> getTextParamParser() {
        return textParamParser;
    }
}

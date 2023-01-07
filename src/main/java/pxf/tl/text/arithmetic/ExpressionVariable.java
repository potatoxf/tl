package pxf.tl.text.arithmetic;

/**
 * Represents a reference to a variable.
 *
 * @author potatoxf
 */
class ExpressionVariable extends Expression {

    /**
     * 变量
     */
    private final String name;

    public ExpressionVariable(String name) {
        this.name = name;
    }

    @Override
    public double evaluate(ArithmeticData arithmeticData) {
        return arithmeticData.getVariable(name).getValue();
    }

    /**
     * 获取表达式字符串
     *
     * @return 字符串
     */
    @Override
    protected String toExpressionString() {
        return name;
    }
}

package pxf.tl.text.arithmetic;

/**
 * Represents a constant numeric expression.
 *
 * @author potatoxf
 */
class ExpressionConstant extends Expression {
    /**
     * Used as dummy expression by the parser if an error occurs while parsing.
     */
    public static final ExpressionConstant EMPTY = new ExpressionConstant(Double.NaN);
    /**
     * Value
     */
    private final double value;

    public ExpressionConstant(double value) {
        this.value = value;
    }

    public ExpressionConstant(Expression expression) {
        this.value = expression.evaluate();
    }

    @Override
    public double evaluate(ArithmeticData arithmeticData) {
        return value;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    protected String toExpressionString() {
        return String.valueOf(value);
    }
}

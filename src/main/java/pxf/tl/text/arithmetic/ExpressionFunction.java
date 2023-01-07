package pxf.tl.text.arithmetic;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the invocation of a arithmeticFunction.
 *
 * @author potatoxf
 */
class ExpressionFunction extends Expression {

    private final List<Expression> parameters = new ArrayList<Expression>();
    /**
     * the arithmeticFunction to evaluate.
     */
    private final ArithmeticFunction arithmeticFunction;

    public ExpressionFunction(ArithmeticFunction arithmeticFunction) {
        this.arithmeticFunction = arithmeticFunction;
    }

    @Override
    public double evaluate(ArithmeticData arithmeticData) {
        return arithmeticFunction.eval(arithmeticData, parameters);
    }

    @Override
    public Expression simplify() {
        if (!arithmeticFunction.isNaturalFunction()) {
            return this;
        }
        for (Expression expr : parameters) {
            if (!expr.isConstant()) {
                return this;
            }
        }
        return new ExpressionConstant(evaluate());
    }

    /**
     * 获取表达式字符串
     *
     * @return 字符串
     */
    @Override
    protected String toExpressionString() {
        return arithmeticFunction.toString();
    }

    /**
     * Adds an expression as parameter.
     *
     * @param expression the parameter to add
     */
    public void addParameter(Expression expression) {
        parameters.add(expression);
    }

    /**
     * Returns all parameters added so far.
     *
     * @return a list of parameters added to this call
     */
    public List<Expression> getParameters() {
        return parameters;
    }
}

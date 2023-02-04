package pxf.tl.text.arithmetic;

/**
 * Represents the result of a parsed expression.
 *
 * <p>Can be evaluated to return a double value. If an error occurs {@code Double.NaN} will be
 * returned.
 *
 * @author potatoxf
 */
abstract class Expression {

    /**
     * Evaluates the expression to a double number.
     *
     * @param arithmeticData expression parameter
     * @return the double value as a result of evaluating this expression. Returns NaN if an error
     * occurs
     */
    public abstract double evaluate(ArithmeticData arithmeticData);

    /**
     * Evaluates the expression to a double number.
     *
     * @return the double value as a result of evaluating this expression. Returns NaN if an error
     * occurs
     */
    public final double evaluate() {
        return evaluate(ArithmeticData.ROOT);
    }

    /**
     * Evaluates the expression to a double number.
     *
     * @param arithmeticData expression parameter
     * @return the double value as a result of evaluating this expression. Returns NaN if an error
     * occurs
     */
    public double strictEvaluate(ArithmeticData arithmeticData) {
        return evaluate(arithmeticData);
    }

    /**
     * Evaluates the expression to a double number.
     *
     * @return the double value as a result of evaluating this expression. Returns NaN if an error
     * occurs
     */
    public final double strictEvaluate() {
        return evaluate(ArithmeticData.ROOT);
    }

    /**
     * Returns a simplified version of this expression.
     *
     * @return a simplified version of this expression or <tt>this</tt> if the expression cannot be
     * simplified
     */
    public Expression simplify() {
        return this;
    }

    /**
     * Determines the this expression is constant
     *
     * @return <tt>true</tt> if the result of evaluate will never change and does not depend on
     * external state like variables
     */
    public boolean isConstant() {
        return false;
    }

    /**
     * Returns a string representation of the object. In general, the <code>toString</code> method
     * returns a string that "textually represents" this object. The result should be a concise but
     * informative representation that is easy for a person to read. It is recommended that all
     * subclasses override this method.
     *
     * <p>The <code>toString</code> method for class <code>Object</code> returns a string consisting
     * of the name of the class of which the object is an instance, the at-sign character `<code>@
     * </code>', and the unsigned hexadecimal representation of the hash code of the object. In other
     * words, this method returns a string equal to the value of:
     *
     * <blockquote>
     *
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre>
     *
     * </blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public final String toString() {
        return getClass().getSimpleName() + ":[" + toExpressionString() + "]";
    }

    /**
     * 获取表达式字符串
     *
     * @return 字符串
     */
    protected abstract String toExpressionString();
}

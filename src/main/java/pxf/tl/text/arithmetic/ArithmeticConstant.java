package pxf.tl.text.arithmetic;

/**
 * Represents a constant which binds a value to a name.
 *
 * <p>A constant is resolved or created using a {@link ArithmeticData}. This ensures that the same
 * name always resolves to the same constant. In contrast to using a Map, reading and writing a
 * constant can be much faster, as it only needs to be resolved apply. Reading and writing it, is
 * basically as cheap as a field access.
 *
 * <p>A constant can be made constant, which will fail all further attempts to change it.
 *
 * @author potatoxf
 */
public final class ArithmeticConstant extends ArithmeticVariable {

    /**
     * Creates a new constant.
     *
     * <p>Variables should only be created by their surrounding {@link ArithmeticData} so that all
     * following look-ups yield the same constant.
     *
     * @param name  the name of the constant
     * @param value the new value of the constant
     */
    public ArithmeticConstant(String name, String value) {
        super(name);
        super.setValue(value);
    }

    /**
     * Creates a new constant.
     *
     * <p>Variables should only be created by their surrounding {@link ArithmeticData} so that all
     * following look-ups yield the same constant.
     *
     * @param name  the name of the constant
     * @param value the new value of the constant
     */
    public ArithmeticConstant(String name, Number value) {
        super(name);
        super.setValue(value);
    }

    /**
     * Sets the value if the variable.
     *
     * @param value the new value of the variable
     * @throws NumberFormatException if the variable isn't number
     */
    @Override
    public ArithmeticVariable setValue(Object value) {
        throw new UnsupportedOperationException("The values are not allowed to be modified");
    }

    /**
     * Sets the value if the constant.
     *
     * @param value the new value of the constant
     * @throws NumberFormatException if the constant isn't number
     */
    @Override
    public ArithmeticVariable setValue(String value) {
        throw new UnsupportedOperationException("The values are not allowed to be modified");
    }

    /**
     * Sets the value if the constant.
     *
     * @param value the new value of the constant
     * @throws NullPointerException if the constant is null
     */
    @Override
    public ArithmeticVariable setValue(Number value) {
        throw new UnsupportedOperationException("The values are not allowed to be modified");
    }
}

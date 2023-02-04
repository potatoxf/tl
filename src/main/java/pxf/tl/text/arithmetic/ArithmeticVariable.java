package pxf.tl.text.arithmetic;

/**
 * Represents a variable which binds a value to a name.
 *
 * <p>A variable is resolved or created using a {@link ArithmeticData}. This ensures that the same
 * name always resolves to the same variable. In contrast to using a Map, reading and writing a
 * variable can be much faster, as it only needs to be resolved apply. Reading and writing it, is
 * basically as cheap as a field access.
 *
 * <p>A variable can be made constant, which will fail all further attempts to change it.
 *
 * @author potatoxf
 */
public class ArithmeticVariable {

    /**
     * Variables Name
     */
    private final String name;

    /**
     * Variables Value
     */
    private double value;

    /**
     * Creates a new variable.
     *
     * <p>Variables should only be created by their surrounding {@link ArithmeticData} so that all
     * following look-ups yield the same variable.
     *
     * @param name the name of the variable
     */
    protected ArithmeticVariable(String name) {
        this.name = name;
    }

    /**
     * Returns the value previously set.
     *
     * @return the value previously set or 0 if the variable is not written yet
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the value if the variable.
     *
     * @param value the new value of the variable
     * @throws NumberFormatException if the variable isn't number
     */
    public ArithmeticVariable setValue(Object value) {
        if (value instanceof String) {
            setValue((String) value);
        } else if (value instanceof Number) {
            setValue((Number) value);
        } else {
            throw new NumberFormatException();
        }
        return this;
    }

    /**
     * Sets the value if the variable.
     *
     * @param value the new value of the variable
     * @throws NumberFormatException if the variable isn't number
     */
    public ArithmeticVariable setValue(String value) {
        this.value = Double.parseDouble(value);
        return this;
    }

    /**
     * Sets the value if the variable.
     *
     * @param value the new value of the variable
     * @throws NullPointerException if the variable is null
     */
    public ArithmeticVariable setValue(Number value) {
        this.value = value.doubleValue();
        return this;
    }

    /**
     * Returns the name of the variable.
     *
     * @return the name of this variable
     */
    public String getName() {
        return name;
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
        return "<" + toStringSign() + ">:" + name + "=" + value;
    }

    /**
     * string sign for toString
     *
     * @return return string sign
     */
    protected String toStringSign() {
        return getClass().getSimpleName();
    }
}

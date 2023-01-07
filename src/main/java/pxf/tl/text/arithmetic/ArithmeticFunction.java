package pxf.tl.text.arithmetic;


import pxf.tl.help.Whether;
import pxf.tl.lang.TextBuilder;
import pxf.tl.util.ToolObject;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Defines a function which can be referenced and evaluated from within expressions.
 *
 * @author potatoxf
 */
public abstract class ArithmeticFunction {

    /**
     * Provides access to {@link Math#sin(double)}
     */
    public static final ArithmeticFunction SIN =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "sin";
                }

                @Override
                protected double eval(double a) {
                    return Math.sin(a);
                }
            };
    /**
     * Provides access to {@link Math#sinh(double)}
     */
    public static final ArithmeticFunction SINH =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "sinh";
                }

                @Override
                protected double eval(double a) {
                    return Math.sinh(a);
                }
            };
    /**
     * Provides access to {@link Math#cos(double)}
     */
    public static final ArithmeticFunction COS =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "cos";
                }

                @Override
                protected double eval(double a) {
                    return Math.cos(a);
                }
            };
    /**
     * Provides access to {@link Math#cosh(double)}
     */
    public static final ArithmeticFunction COSH =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "cosh";
                }

                @Override
                protected double eval(double a) {
                    return Math.cosh(a);
                }
            };
    /**
     * Provides access to {@link Math#tan(double)}
     */
    public static final ArithmeticFunction TAN =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "tan";
                }

                @Override
                protected double eval(double a) {
                    return Math.tan(a);
                }
            };
    /**
     * Provides access to {@link Math#tanh(double)}
     */
    public static final ArithmeticFunction TANH =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "tanh";
                }

                @Override
                protected double eval(double a) {
                    return Math.tanh(a);
                }
            };
    /**
     * Provides access to {@link Math#abs(double)}
     */
    public static final ArithmeticFunction ABS =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "abs";
                }

                @Override
                protected double eval(double a) {
                    return Math.abs(a);
                }
            };
    /**
     * Provides access to {@link Math#asin(double)}
     */
    public static final ArithmeticFunction ASIN =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "asin";
                }

                @Override
                protected double eval(double a) {
                    return Math.asin(a);
                }
            };
    /**
     * Provides access to {@link Math#acos(double)}
     */
    public static final ArithmeticFunction ACOS =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "acos";
                }

                @Override
                protected double eval(double a) {
                    return Math.acos(a);
                }
            };
    /**
     * Provides access to {@link Math#atan(double)}
     */
    public static final ArithmeticFunction ATAN =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "atan";
                }

                @Override
                protected double eval(double a) {
                    return Math.atan(a);
                }
            };
    /**
     * Provides access to {@link Math#atan2(double, double)}
     */
    public static final ArithmeticFunction ATAN2 =
            new BinaryArithmeticFunction() {
                @Override
                public String getName() {
                    return "atan2";
                }

                @Override
                protected double eval(double a, double b) {
                    return Math.atan2(a, b);
                }
            };
    /**
     * Provides access to {@link Math#round(double)}
     */
    public static final ArithmeticFunction ROUND =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "round";
                }

                @Override
                protected double eval(double a) {
                    return Math.round(a);
                }
            };
    /**
     * Provides access to {@link Math#floor(double)}
     */
    public static final ArithmeticFunction FLOOR =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "floor";
                }

                @Override
                protected double eval(double a) {
                    return Math.floor(a);
                }
            };
    /**
     * Provides access to {@link Math#ceil(double)}
     */
    public static final ArithmeticFunction CEIL =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "ceil";
                }

                @Override
                protected double eval(double a) {
                    return Math.ceil(a);
                }
            };
    /**
     * Provides access to {@link Math#pow(double, double)}
     */
    public static final ArithmeticFunction POW =
            new BinaryArithmeticFunction() {
                @Override
                public String getName() {
                    return "pow";
                }

                @Override
                protected double eval(double a, double b) {
                    return Math.pow(a, b);
                }
            };
    /**
     * Provides access to {@link Math#sqrt(double)}
     */
    public static final ArithmeticFunction SQRT =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "sqrt";
                }

                @Override
                protected double eval(double a) {
                    return Math.sqrt(a);
                }
            };
    /**
     * Provides access to {@link Math#exp(double)}
     */
    public static final ArithmeticFunction EXP =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "exp";
                }

                @Override
                protected double eval(double a) {
                    return Math.exp(a);
                }
            };
    /**
     * Provides access to {@link Math#log(double)}
     */
    public static final ArithmeticFunction LN =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "ln";
                }

                @Override
                protected double eval(double a) {
                    return Math.log(a);
                }
            };
    /**
     * Provides access to {@link Math#log10(double)}
     */
    public static final ArithmeticFunction LOG =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "log";
                }

                @Override
                protected double eval(double a) {
                    return Math.log10(a);
                }
            };
    /**
     * Provides access to {@link Math#min(double, double)}
     */
    public static final ArithmeticFunction MIN =
            new BinaryArithmeticFunction() {
                @Override
                public String getName() {
                    return "min";
                }

                @Override
                protected double eval(double a, double b) {
                    return Math.min(a, b);
                }
            };
    /**
     * Provides access to {@link Math#max(double, double)}
     */
    public static final ArithmeticFunction MAX =
            new BinaryArithmeticFunction() {
                @Override
                public String getName() {
                    return "max";
                }

                @Override
                protected double eval(double a, double b) {
                    return Math.max(a, b);
                }
            };
    /**
     * Provides access to {@link Math#random()} which will be multiplied by the given argument.
     */
    public static final ArithmeticFunction RND =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "rnd";
                }

                @Override
                protected double eval(double a) {
                    return Math.random() * a;
                }
            };
    /**
     * Provides access to {@link Math#signum(double)}
     */
    public static final ArithmeticFunction SIGN =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "sign";
                }

                @Override
                protected double eval(double a) {
                    return Math.signum(a);
                }
            };
    /**
     * Provides access to {@link Math#toDegrees(double)}
     */
    public static final ArithmeticFunction DEG =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "deg";
                }

                @Override
                protected double eval(double a) {
                    return Math.toDegrees(a);
                }
            };
    /**
     * Provides access to {@link Math#toRadians(double)}
     */
    public static final ArithmeticFunction RAD =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "rad";
                }

                @Override
                protected double eval(double a) {
                    return Math.toRadians(a);
                }
            };
    /**
     * Provides an if-like function
     *
     * <p>It expects three arguments: A condition, an expression being evaluated if the condition is
     * non zero and an expression which is being evaluated if the condition is zero.
     */
    public static final ArithmeticFunction IF =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "if";
                }

                @Override
                public int getNumberOfArguments() {
                    return 3;
                }

                @Override
                public double eval(ArithmeticData arithmeticData, List<Expression> args) {
                    double check = args.get(0).evaluate(arithmeticData);
                    if (Double.isNaN(check)) {
                        return check;
                    }
                    if (Math.abs(check) > 0) {
                        return args.get(1).evaluate(arithmeticData);
                    } else {
                        return args.get(2).evaluate(arithmeticData);
                    }
                }

                @Override
                public boolean isNaturalFunction() {
                    return false;
                }
            };
    /**
     * var arg
     */
    public static final ArithmeticFunction AVG =
            new ArithmeticFunction() {
                @Override
                public String getName() {
                    return "avg";
                }

                @Override
                public int getNumberOfArguments() {
                    return -1;
                }

                @Override
                public double eval(ArithmeticData arithmeticData, List<Expression> args) {
                    double avg = 0;
                    if (Whether.empty(args)) {
                        return avg;
                    }
                    for (Expression e : args) {
                        avg += e.evaluate(arithmeticData);
                    }
                    return avg / args.size();
                }
            };
    /**
     * function table
     */
    public static final Map<String, ArithmeticFunction> DEFAULT_FUNCTION_TABLE =
            new TreeMap<String, ArithmeticFunction>();

    /*
     * Setup well known ArithmeticFunction
     */
    static {
        registerFunction("sin", SIN);
        registerFunction("cos", COS);
        registerFunction("tan", TAN);
        registerFunction("sinh", SINH);
        registerFunction("cosh", COSH);
        registerFunction("tanh", TANH);
        registerFunction("asin", ASIN);
        registerFunction("acos", ACOS);
        registerFunction("atan", ATAN);
        registerFunction("atan2", ATAN2);
        registerFunction("deg", DEG);
        registerFunction("rad", RAD);
        registerFunction("abs", ABS);
        registerFunction("round", ROUND);
        registerFunction("ceil", CEIL);
        registerFunction("floor", FLOOR);
        registerFunction("exp", EXP);
        registerFunction("ln", LN);
        registerFunction("log", LOG);
        registerFunction("sqrt", SQRT);
        registerFunction("pow", POW);
        registerFunction("min", MIN);
        registerFunction("max", MAX);
        registerFunction("rnd", RND);
        registerFunction("sign", SIGN);
        registerFunction("if", IF);
        registerFunction("avg", AVG);
    }

    /**
     * Registers a new arithmeticFunction which can be referenced from within an expression.
     *
     * <p>A arithmeticFunction must be registered before an expression is parsed in order to be
     * visible.
     *
     * @param name               the name of the arithmeticFunction. If a arithmeticFunction with the same name is
     *                           already available, it will be overridden
     * @param arithmeticFunction the arithmeticFunction which is invoked as an expression is evaluated
     */
    public static void registerFunction(String name, ArithmeticFunction arithmeticFunction) {
        DEFAULT_FUNCTION_TABLE.put(name, arithmeticFunction);
    }

    /**
     * Returns the name of function
     *
     * @return the function name
     */
    public abstract String getName();

    /**
     * Returns the number of expected arguments.
     *
     * <p>If the function is called with a different number of arguments, an error will be created
     *
     * <p>In order to support functions with a variable number of arguments, a negative number can be
     * returned. This will essentially disable the check.
     *
     * @return the number of arguments expected by this function or a negative number to indicate that
     * this function accepts a variable number of arguments
     */
    public int getNumberOfArguments() {
        return 1;
    }

    /**
     * Executes the function with the given arguments.
     *
     * <p>The arguments need to be evaluated first. This is not done externally to permit functions to
     * perform lazy evaluations.
     *
     * @param args the arguments for this function. The length of the given list will exactly match
     *             <tt>getNumberOfArguments</tt>
     * @return the result of the function evaluated with the given arguments
     */
    public double eval(ArithmeticData arithmeticData, List<Expression> args) {
        double a = args.get(0).evaluate(arithmeticData);
        if (Double.isNaN(a)) {
            return a;
        }
        return eval(a);
    }

    /**
     * A natural function returns the same output for the same input.
     *
     * <p>All classical mathematical functions are "natural". A function which reads user input is not
     * natural, as the function might return different results depending on the users input
     *
     * @return <tt>true</tt> if the function returns the same output for the same input,
     * <tt>false</tt> otherwise
     */
    public boolean isNaturalFunction() {
        return true;
    }

    /**
     * Performs the computation of the unary function
     *
     * @param arg the argument of the function
     * @return the result of calling the function with a as argument
     */
    protected double eval(double arg) {
        return arg;
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
        return ToolObject.toFunctionString(
                        TextBuilder.of(), getName(), "x", getNumberOfArguments())
                .toString();
    }

    /**
     * Defines a binary function which can be referenced and evaluated from within expressions.
     *
     * @author potatoxf
     */
    public abstract static class BinaryArithmeticFunction extends ArithmeticFunction {

        @Override
        public int getNumberOfArguments() {
            return 2;
        }

        @Override
        public double eval(ArithmeticData arithmeticData, List<Expression> args) {
            double a = args.get(0).evaluate(arithmeticData);
            if (Double.isNaN(a)) {
                return a;
            }
            double b = args.get(1).evaluate(arithmeticData);
            if (Double.isNaN(b)) {
                return b;
            }
            return eval(a, b);
        }

        /**
         * Performs the computation of the binary function
         *
         * @param a the first argument of the function
         * @param b the second argument of the function
         * @return the result of calling the function with a and b
         */
        protected abstract double eval(double a, double b);
    }
}

package pxf.tl.text.arithmetic;


import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.text.TextParseError;
import pxf.tl.text.TextParseException;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

/**
 * 四则运算表达式
 *
 * @author potatoxf
 */
public final class ArithmeticExpression {
    /**
     * 表达式
     */
    private final Expression rootExpression;
    /**
     * 变量名列表
     */
    private final List<String> variableNameList;
    /**
     * 变量是否需要有值，
     */
    private final Map<String, Boolean> isOptionalValueMap;
    /**
     * 变量值
     */
    private final Map<String, Double> valueMap;

    private ArithmeticExpression(
            Expression rootExpression,
            List<String> variableNameList,
            Map<String, Boolean> isOptionalValueMap,
            Map<String, Double> valueMap) {
        this.rootExpression = rootExpression;
        this.variableNameList = variableNameList;
        this.isOptionalValueMap = isOptionalValueMap;
        this.valueMap = valueMap;
    }

    /**
     * 四则运算
     *
     * @param input       输入值
     * @param inputParams 输入参数
     * @return {@code ArithmeticExpression}
     */
    public static double calculate(String input, Map<String, Object> inputParams) {
        ArithmeticExpression arithmeticExpression = ArithmeticExpression.of(input);
        pxf.tl.text.arithmetic.ArithmeticData arithmeticData = new pxf.tl.text.arithmetic.ArithmeticData();
        arithmeticData.create(inputParams);
        return arithmeticExpression.evaluate(arithmeticData);
    }

    /**
     * 构建默认四则运算表达式
     *
     * @param input 输入值
     * @return {@code ArithmeticExpression}
     */
    public static ArithmeticExpression of(String input) {
        return create(input).setGlobalOptionalValue(false).parse();
    }

    public static Parser create(String input) {
        return new Parser(new StringReader(input), pxf.tl.text.arithmetic.ArithmeticFunction.DEFAULT_FUNCTION_TABLE);
    }

    public static Parser create(String input, Map<String, pxf.tl.text.arithmetic.ArithmeticFunction> functionTable) {
        return new Parser(new StringReader(input), functionTable);
    }

    public static Parser create(Reader input) {
        return new Parser(input, pxf.tl.text.arithmetic.ArithmeticFunction.DEFAULT_FUNCTION_TABLE);
    }

    public static Parser create(Reader input, Map<String, pxf.tl.text.arithmetic.ArithmeticFunction> functionTable) {
        return new Parser(input, functionTable);
    }

    /**
     * 表达式含有的变量名称
     *
     * @return {@code Collection<String> }
     */
    public Collection<String> variableNames() {
        return variableNameList;
    }

    /**
     * 精确计算
     *
     * @param arithmeticData 数据
     * @return 返回计算结果值
     */
    public double evaluate(pxf.tl.text.arithmetic.ArithmeticData arithmeticData) {
        return rootExpression.evaluate(checkArg(arithmeticData));
    }

    /**
     * 精确计算
     *
     * @param arithmeticData 数据
     * @return 返回计算结果值
     */
    public double strictEvaluate(pxf.tl.text.arithmetic.ArithmeticData arithmeticData) {
        return rootExpression.strictEvaluate(checkArg(arithmeticData));
    }

    private pxf.tl.text.arithmetic.ArithmeticData checkArg(pxf.tl.text.arithmetic.ArithmeticData arithmeticData) {
        pxf.tl.text.arithmetic.ArithmeticData wrapper =
                arithmeticData != null ? new pxf.tl.text.arithmetic.ArithmeticData(arithmeticData) : new pxf.tl.text.arithmetic.ArithmeticData();
        List<String> exception = new ArrayList<>();
        for (String variableName : variableNames()) {
            if (wrapper.containsVariable(variableName)) {
                continue;
            }
            // 是可选值则设置默认值
            if (isOptionalValueMap.get(variableName)) {
                wrapper.create(variableName).setValue(valueMap.get(variableName));
            } else {
                exception.add(variableName);
            }
        }
        if (Whether.noEmpty(exception)) {
            throw new IllegalArgumentException("Missing parameter: " + exception);
        }
        return wrapper;
    }

    /**
     * Parses a given mathematical expression into an abstract syntax tree which can be evaluated.
     *
     * <p>Takes a string input as String or Reader which will be translated into an {@link
     * Expression}. If one or more errors occur, a {@link TextParseException} will be thrown. The
     * parser tries to continue as long a possible to provide good insight into the errors within the
     * expression.
     *
     * <p>This is a recursive descending parser which has a method per non-terminal.
     *
     * <p>Using this parser is as easy as: {@code ArithmeticData expArg = ArithmeticData.make();
     * ArithmeticVariable a = expArg.getVariable("a"); Expression expr = Parser.createCache("3 + a *
     * 4"); a.setValue(4); System.out.println(expr.evaluate()); a.setValue(5);
     * System.out.println(expr.evaluate()); }
     */
    public static final class Parser {
        private final List<TextParseError> errors = new ArrayList<>();
        /**
         * 函数表
         */
        private final Map<String, pxf.tl.text.arithmetic.ArithmeticFunction> functionTable;
        /**
         * 变量名列表
         */
        private final List<String> variableNameList = new LinkedList<>();
        /**
         * 变量是否需要有值，
         */
        private final Map<String, Boolean> optionalValueMap = new TreeMap<>();
        /**
         * 变量值
         */
        private final Map<String, Double> defaultValueMap = new TreeMap<>();
        /**
         * Token提取器
         */
        private final Tokenizer tokenizer;
        /**
         * 默认是否需要值
         */
        private boolean defaultOptionalValue = true;
        /**
         * 默认值
         */
        private double defaultValue = 0;

        private Parser(Reader input, Map<String, pxf.tl.text.arithmetic.ArithmeticFunction> functionTable) {
            this.tokenizer = new Tokenizer(input);
            this.tokenizer.setProblemCollector(errors);
            this.functionTable = Safe.value(functionTable);
        }

        public Parser setIsOptionalValue(String variableName, boolean isOptionalValue) {
            optionalValueMap.put(variableName, isOptionalValue);
            return this;
        }

        public Parser setDefaultValue(String variableName, double value) {
            defaultValueMap.put(variableName, value);
            return this;
        }

        public Parser setGlobalOptionalValue(boolean defaultOptionalValue) {
            this.defaultOptionalValue = defaultOptionalValue;
            return this;
        }

        public Parser setGlobalDefaultValue(double defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /**
         * Parses the expression in <tt>input</tt>
         *
         * @return the parsed expression
         * @throws TextParseException if the expression contains one or more errors
         */
        public ArithmeticExpression parse() throws TextParseException {
            Expression result = expression().simplify();
            if (tokenizer.current().isNotEnd()) {
                Token token = tokenizer.consume();
                errors.add(
                        TextParseError.error(
                                token,
                                String.format(
                                        "Unexpected token: '%s'. Expected an expression.", token.getSource())));
            }
            if (!Whether.empty(errors)) {
                throw TextParseException.create(errors);
            }
            for (String variableName : variableNameList) {
                if (!optionalValueMap.containsKey(variableName)) {
                    optionalValueMap.put(variableName, defaultOptionalValue);
                }
                if (optionalValueMap.get(variableName) && !defaultValueMap.containsKey(variableName)) {
                    defaultValueMap.put(variableName, defaultValue);
                }
            }
            return new ArithmeticExpression(
                    result,
                    Collections.unmodifiableList(variableNameList),
                    Collections.unmodifiableMap(optionalValueMap),
                    Collections.unmodifiableMap(defaultValueMap));
        }

        /**
         * Parser rule for parsing an expression.
         *
         * <p>This is the root rule. An expression is a <tt>relationalExpression</tt> which might be
         * followed by a logical operator (&amp;&amp; or ||) and another <tt>expression</tt>.
         *
         * @return an expression parsed from the given input
         */
        private Expression expression() {
            Expression left = relationalExpression();
            if (tokenizer.current().isSymbol("&&")) {
                tokenizer.consume();
                Expression right = expression();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.AND);
            }
            if (tokenizer.current().isSymbol("||")) {
                tokenizer.consume();
                Expression right = expression();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.OR);
            }
            return left;
        }

        /**
         * Parser rule for parsing a relational expression.
         *
         * <p>A relational expression is a <tt>term</tt> which might be followed by a relational
         * operator (&lt;,&lt;=,...,&gt;) and another <tt>relationalExpression</tt>.
         *
         * @return a relational expression parsed from the given input
         */
        private Expression relationalExpression() {
            Expression left = term();
            if (tokenizer.current().isSymbol("<")) {
                tokenizer.consume();
                Expression right = relationalExpression();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.LT);
            }
            if (tokenizer.current().isSymbol("<=")) {
                tokenizer.consume();
                Expression right = relationalExpression();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.LT_EQ);
            }
            if (tokenizer.current().isSymbol("=")) {
                tokenizer.consume();
                Expression right = relationalExpression();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.EQ);
            }
            if (tokenizer.current().isSymbol(">=")) {
                tokenizer.consume();
                Expression right = relationalExpression();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.GT_EQ);
            }
            if (tokenizer.current().isSymbol(">")) {
                tokenizer.consume();
                Expression right = relationalExpression();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.GT);
            }
            if (tokenizer.current().isSymbol("!=")) {
                tokenizer.consume();
                Expression right = relationalExpression();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.NEQ);
            }
            return left;
        }

        /**
         * Parser rule for parsing a term.
         *
         * <p>A term is a <tt>product</tt> which might be followed by + or - as operator and another
         * <tt>term</tt>.
         *
         * @return a term parsed from the given input
         */
        private Expression term() {
            Expression left = product();
            if (tokenizer.current().isSymbol("+")) {
                tokenizer.consume();
                Expression right = term();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.ADD);
            }
            if (tokenizer.current().isSymbol("-")) {
                tokenizer.consume();
                Expression right = term();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.SUBTRACT);
            }
            if (tokenizer.current().isNumber()) {
                if (tokenizer.current().getContents().startsWith("-")) {
                    Expression right = term();
                    return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.ADD);
                }
            }

            return left;
        }

        /**
         * Parser rule for parsing a product.
         *
         * <p>A product is a <tt>power</tt> which might be followed by *, / or % as operator and another
         * <tt>product</tt>.
         *
         * @return a product parsed from the given input
         */
        private Expression product() {
            Expression left = power();
            if (tokenizer.current().isSymbol("*")) {
                tokenizer.consume();
                Expression right = product();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.MULTIPLY);
            }
            if (tokenizer.current().isSymbol("/")) {
                tokenizer.consume();
                Expression right = product();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.DIVIDE);
            }
            if (tokenizer.current().isSymbol("%")) {
                tokenizer.consume();
                if (tokenizer.current().isEnd() || !tokenizer.current().isNumber()) {
                    Expression expression =
                            reOrder(left, new pxf.tl.text.arithmetic.ExpressionConstant(0.01), pxf.tl.text.arithmetic.ExpressionBinary.Op.MULTIPLY);
                    if (tokenizer.current().isNotEnd()) {
                        tokenizer.consume();
                        Expression right = product();
                        return reOrder(expression, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.MULTIPLY);
                    }
                    return expression;
                }
                Expression right = product();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.MODULO);
            }
            return left;
        }

        /*
         * Reorders the operands of the given operation in order to generate a "left handed" AST which performs evaluations
         * in natural order (from left to right).
         */
        private Expression reOrder(Expression left, Expression right, pxf.tl.text.arithmetic.ExpressionBinary.Op op) {
            if (right instanceof pxf.tl.text.arithmetic.ExpressionBinary) {
                pxf.tl.text.arithmetic.ExpressionBinary rightOp = (pxf.tl.text.arithmetic.ExpressionBinary) right;
                if (!rightOp.isSealed() && rightOp.getOp().getPriority() == op.getPriority()) {
                    replaceLeft(rightOp, left, op);
                    return right;
                }
            }
            return new pxf.tl.text.arithmetic.ExpressionBinary(op, left, right);
        }

        private void replaceLeft(pxf.tl.text.arithmetic.ExpressionBinary target, Expression newLeft, pxf.tl.text.arithmetic.ExpressionBinary.Op op) {
            if (target.getLeft() instanceof pxf.tl.text.arithmetic.ExpressionBinary) {
                pxf.tl.text.arithmetic.ExpressionBinary leftOp = (pxf.tl.text.arithmetic.ExpressionBinary) target.getLeft();
                if (!leftOp.isSealed() && leftOp.getOp().getPriority() == op.getPriority()) {
                    replaceLeft(leftOp, newLeft, op);
                    return;
                }
            }
            target.setLeft(new pxf.tl.text.arithmetic.ExpressionBinary(op, newLeft, target.getLeft()));
        }

        /**
         * Parser rule for parsing a power.
         *
         * <p>A power is an <tt>atom</tt> which might be followed by ^ or ** as operator and another
         * <tt>power</tt>.
         *
         * @return a power parsed from the given input
         */
        private Expression power() {
            Expression left = atom();
            if (tokenizer.current().isSymbol("^") || tokenizer.current().isSymbol("**")) {
                tokenizer.consume();
                Expression right = power();
                return reOrder(left, right, pxf.tl.text.arithmetic.ExpressionBinary.Op.POWER);
            }
            return left;
        }

        /**
         * Parser rule for parsing an atom.
         *
         * <p>An atom is either a numeric constant, an <tt>expression</tt> in brackets, an
         * <tt>expression</tt> surrounded by | to signal the absolute function, an identifier to signal
         * a variable reference or an identifier followed by a bracket to signal a function call.
         *
         * @return an atom parsed from the given input
         */
        private Expression atom() {
            if (tokenizer.current().isSymbol("-")) {
                tokenizer.consume();
                pxf.tl.text.arithmetic.ExpressionBinary result =
                        new pxf.tl.text.arithmetic.ExpressionBinary(pxf.tl.text.arithmetic.ExpressionBinary.Op.SUBTRACT, new pxf.tl.text.arithmetic.ExpressionConstant(0d), atom());
                result.seal();
                return result;
            }
            if (tokenizer.current().isSymbol("+") && tokenizer.next().isSymbol("(")) {
                // Support for brackets with a leading + like "+(2.2)" in this case we simply ignore the
                // + sign
                tokenizer.consume();
            }
            if (tokenizer.current().isSymbol("(")) {
                tokenizer.consume();
                Expression result = expression();
                if (result instanceof pxf.tl.text.arithmetic.ExpressionBinary) {
                    ((pxf.tl.text.arithmetic.ExpressionBinary) result).seal();
                }
                expect(")");
                return result;
            }
            if (tokenizer.current().isSymbol("|")) {
                tokenizer.consume();
                pxf.tl.text.arithmetic.ExpressionFunction call = new pxf.tl.text.arithmetic.ExpressionFunction(pxf.tl.text.arithmetic.ArithmeticFunction.ABS);
                call.addParameter(expression());
                expect("|");
                return call;
            }
            if (tokenizer.current().isIdentifier()) {
                if (tokenizer.next().isSymbol("(")) {
                    return functionCall();
                }
                Token variableNameToken = tokenizer.consume();
                String variableName = variableNameToken.getContents();
                try {
                    variableNameList.add(variableName);
                    return new pxf.tl.text.arithmetic.ExpressionVariable(variableName);
                } catch (
                        @SuppressWarnings("UnusedCatchParameter")
                                IllegalArgumentException e) {
                    errors.add(
                            TextParseError.error(
                                    variableNameToken, String.format("Unknown variable: '%s'", variableName)));
                    return new pxf.tl.text.arithmetic.ExpressionConstant(0);
                }
            }
            return literalAtom();
        }

        /**
         * Parser rule for parsing a Literal atom.
         *
         * <p>An Literal atom is a numeric constant.
         *
         * @return an atom parsed from the given input
         */
        @SuppressWarnings("squid:S1698")
        private Expression literalAtom() {
            if (tokenizer.current().isSymbol("+") && tokenizer.next().isNumber()) {
                // Parse numbers with a leading + sign like +2.02 by simply ignoring the +
                tokenizer.consume();
            }
            if (tokenizer.current().isNumber()) {
                double value = Double.parseDouble(tokenizer.consume().getContents());
                if (tokenizer.current().is(Token.TokenType.ID)) {
                    String quantifier = tokenizer.current().getContents().intern();
                    switch (quantifier) {
                        case "n":
                            value /= 1000000000d;
                            tokenizer.consume();
                            break;
                        case "u":
                            value /= 1000000d;
                            tokenizer.consume();
                            break;
                        case "m":
                            value /= 1000d;
                            tokenizer.consume();
                            break;
                        case "K":
                        case "k":
                            value *= 1000d;
                            tokenizer.consume();
                            break;
                        case "M":
                            value *= 1000000d;
                            tokenizer.consume();
                            break;
                        case "G":
                            value *= 1000000000d;
                            tokenizer.consume();
                            break;
                        default:
                            Token token = tokenizer.consume();
                            errors.add(
                                    TextParseError.error(
                                            token,
                                            String.format(
                                                    "Unexpected token: '%s'. Expected a valid quantifier.",
                                                    token.getSource())));
                            break;
                    }
                }
                return new pxf.tl.text.arithmetic.ExpressionConstant(value);
            }
            Token token = tokenizer.consume();
            errors.add(
                    TextParseError.error(
                            token,
                            String.format("Unexpected token: '%s'. Expected an expression.", token.getSource())));
            return pxf.tl.text.arithmetic.ExpressionConstant.EMPTY;
        }

        /**
         * Parses a function call.
         *
         * @return the function call as Expression
         */
        private Expression functionCall() {
            Token funToken = tokenizer.consume();
            pxf.tl.text.arithmetic.ArithmeticFunction fun = functionTable.get(funToken.getContents());
            if (fun == null) {
                errors.add(
                        TextParseError.error(
                                funToken, String.format("Unknown function: '%s'", funToken.getContents())));
                return pxf.tl.text.arithmetic.ExpressionConstant.EMPTY;
            }
            pxf.tl.text.arithmetic.ExpressionFunction call = new pxf.tl.text.arithmetic.ExpressionFunction(fun);
            tokenizer.consume();
            while (!tokenizer.current().isSymbol(")") && tokenizer.current().isNotEnd()) {
                if (!Whether.empty(call.getParameters())) {
                    expect(",");
                }
                call.addParameter(expression());
            }
            expect(")");
            if (call.getParameters().size() != fun.getNumberOfArguments()
                    && fun.getNumberOfArguments() >= 0) {
                errors.add(
                        TextParseError.error(
                                funToken,
                                String.format(
                                        "Number of arguments for function '%s' do not match. Expected: %d, Found: %d",
                                        funToken.getContents(),
                                        fun.getNumberOfArguments(),
                                        call.getParameters().size())));
                return pxf.tl.text.arithmetic.ExpressionConstant.EMPTY;
            }
            return call;
        }

        /**
         * Signals that the given token is expected.
         *
         * <p>If the current input is pointing at the specified token, it will be consumed. If not, an
         * error will be added to the error list and the input remains unchanged.
         *
         * @param trigger the trigger of the expected token
         */
        private void expect(String trigger) {
            if (tokenizer.current().matches(Token.TokenType.SYMBOL, trigger)) {
                tokenizer.consume();
            } else {
                errors.add(
                        TextParseError.error(
                                tokenizer.current(),
                                String.format(
                                        "Unexpected token '%s'. Expected: '%s'",
                                        tokenizer.current().getSource(), trigger)));
            }
        }
    }
}

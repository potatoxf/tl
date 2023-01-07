package pxf.tl.image.captcha.generator;


import pxf.tl.api.PoolOfCharacter;
import pxf.tl.math.Calculator;
import pxf.tl.util.ToolRandom;
import pxf.tl.util.ToolString;

/**
 * 数字计算验证码生成器
 *
 * @author potatoxf
 */
public class MathGenerator implements CodeGenerator {
    private static final long serialVersionUID = -5514819971774091076L;

    private static final String operators = "+-*";

    /**
     * 参与计算数字最大长度
     */
    private final int numberLength;

    /**
     * 构造
     */
    public MathGenerator() {
        this(2);
    }

    /**
     * 构造
     *
     * @param numberLength 参与计算最大数字位数
     */
    public MathGenerator(int numberLength) {
        this.numberLength = numberLength;
    }

    @Override
    public String generate() {
        final int limit = getLimit();
        String number1 = Integer.toString(ToolRandom.randomInt(limit));
        String number2 = Integer.toString(ToolRandom.randomInt(limit));
        number1 = ToolString.padAfter(number1, this.numberLength, PoolOfCharacter.SPACE);
        number2 = ToolString.padAfter(number2, this.numberLength, PoolOfCharacter.SPACE);

        return ToolString.builder() //
                .append(number1) //
                .append(ToolRandom.randomChar(operators)) //
                .append(number2) //
                .append('=')
                .toString();
    }

    @Override
    public boolean verify(String code, String userInputCode) {
        int result;
        try {
            result = Integer.parseInt(userInputCode);
        } catch (NumberFormatException e) {
            // 用户输入非数字
            return false;
        }

        final int calculateResult = (int) Calculator.conversion(code);
        return result == calculateResult;
    }

    /**
     * 获取验证码长度
     *
     * @return 验证码长度
     */
    public int getLength() {
        return this.numberLength * 2 + 2;
    }

    /**
     * 根据长度获取参与计算数字最大值
     *
     * @return 最大值
     */
    private int getLimit() {
        return Integer.parseInt("1" + ToolString.repeat('0', this.numberLength));
    }
}

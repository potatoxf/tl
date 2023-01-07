package pxf.tl.text.capitulation;


import pxf.tl.exception.UnsupportedException;
import pxf.tl.help.Whether;

/**
 * 中午大写数字
 *
 * @author potatoxf
 */
public class ChineseCapitalizedTextParser extends CapitalizedTextParser {

    private static final String[] DIGIT = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

    private static final String[] SMALL_UNIT = new String[]{"", "拾", "佰", "仟"};

    private static final String[] BIG_UNIT = new String[]{"", "万", "亿", "万亿", "兆"};

    private final ChineseIntegerHelper chineseIntegerHelper =
            new ChineseIntegerHelper(DIGIT, SMALL_UNIT, BIG_UNIT, "壹拾", "拾");

    @Override
    protected int configMaxDecimalLength() {
        return 3;
    }

    @Override
    protected int configMaxIntegerLength() {
        return chineseIntegerHelper.getMaxSupportLength();
    }

    /**
     * 合并正规整数和小数部分
     *
     * @param integer 整数部分
     * @param decimal 小数部分，没有返回空字符串
     * @return 合并成正规的数字
     */
    @Override
    protected String marge(boolean isNegative, String integer, String decimal) {
        String prefix = isNegative ? "负" : "";
        if (Whether.empty(decimal)) {
            return prefix + integer + "元整";
        }
        return integer + "元" + decimal;
    }

    /**
     * 解析小数部分
     *
     * @param decimal 小数
     * @return 返回正规小数
     */
    @Override
    protected String parseDecimal(String decimal) {

        int len = decimal.length();
        String f = digitDecimal(decimal.charAt(0)) + "角";
        if (len == 1) {
            return f;
        } else if (len == 2) {
            return f + digitDecimal(decimal.charAt(1)) + "分";
        } else if (len == 3) {
            return f + digitDecimal(decimal.charAt(1)) + "分" + digitDecimal(decimal.charAt(2)) + "厘";
        } else {
            throw new UnsupportedException("The decimal length must be lesser equal 3");
        }
    }

    /**
     * 解析整数部分
     *
     * @param amount 整数
     * @return 返回正规整数
     */
    @Override
    protected String parseInteger(String amount) {
        return chineseIntegerHelper.resolveNumber(amount);
    }

    private String digitDecimal(char c) {
        return DIGIT[Character.digit(c, 10)];
    }
}

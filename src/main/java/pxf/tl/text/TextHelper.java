package pxf.tl.text;


import pxf.tl.help.Whether;
import pxf.tl.lang.TextBuilder;
import pxf.tl.util.ToolString;

/**
 * @author potatoxf
 */
public class TextHelper {
    private TextHelper() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    /**
     * 文字脱敏
     *
     * @param rule 数据脱敏规则，规则：{@code &C}: 影藏中间字符串，规则{@code &E}: 影藏结尾字符串，规则{@code &S}: 影藏起始字符串
     * @param data 字符串数据
     * @return 返回数据脱敏后的文字
     */
    public static String desensitization(final String data, final String rule) {
        return desensitization(rule, data, null, null);
    }

    /**
     * 文字脱敏
     *
     * @param data         字符串数据
     * @param rule         数据脱敏规则，规则：{@code &C:*}: 影藏中间字符串，规则{@code &E:*}: 影藏结尾字符串，规则{@code &S:*}: 影藏起始字符串
     * @param targetLength 目标文字长度
     * @return 返回数据脱敏后的文字
     */
    public static String desensitization(
            final String data, final String rule, final Integer targetLength) {
        return desensitization(rule, data, targetLength, null);
    }

    /**
     * 文字脱敏
     *
     * @param data     字符串数据
     * @param rule     数据脱敏规则，规则：{@code &C:*}: 影藏中间字符串，规则{@code &E:*}: 影藏结尾字符串，规则{@code &S:*}: 影藏起始字符串
     * @param markRate 掩码占用比率，默认0.65
     * @return 返回数据脱敏后的文字
     */
    public static String desensitization(
            final String data, final String rule, final Double markRate) {
        return desensitization(data, rule, null, markRate);
    }

    /**
     * 文字脱敏
     *
     * @param data         字符串数据
     * @param rule         数据脱敏规则，规则：{@code &C:*}: 影藏中间字符串，规则{@code &E:*}: 影藏结尾字符串，规则{@code &S:*}: 影藏起始字符串
     * @param targetLength 目标文字长度
     * @param markRate     掩码占用比率，默认0.65
     * @return 返回数据脱敏后的文字
     */
    public static String desensitization(
            final String data, final String rule, final Integer targetLength, final Double markRate) {
        if (rule.length() == 4 && rule.charAt(2) == ':') {
            return desensitization(data, rule.substring(0, 2), rule.charAt(3), targetLength, markRate);
        } else if (rule.length() == 2) {
            return desensitization(data, rule, '*', targetLength, markRate);
        } else {
            return data;
        }
    }

    /**
     * 文字脱敏
     *
     * @param data 字符串数据
     * @param rule 数据脱敏规则，规则：{@code &C}: 影藏中间字符串，规则{@code &E}: 影藏结尾字符串，规则{@code &S}: 影藏起始字符串
     * @param mark 掩码字符
     * @return 返回数据脱敏后的文字
     */
    public static String desensitization(final String data, final String rule, final char mark) {
        return desensitization(data, rule, mark, null, null);
    }

    /**
     * 文字脱敏
     *
     * @param data         字符串数据
     * @param rule         数据脱敏规则，规则：{@code &C}: 影藏中间字符串，规则{@code &E}: 影藏结尾字符串，规则{@code &S}: 影藏起始字符串
     * @param mark         掩码字符
     * @param targetLength 目标文字长度
     * @return 返回数据脱敏后的文字
     */
    public static String desensitization(
            final String data, final String rule, final char mark, final Integer targetLength) {
        return desensitization(data, rule, mark, targetLength, null);
    }

    /**
     * 文字脱敏
     *
     * @param data     字符串数据
     * @param rule     数据脱敏规则，规则：{@code &C}: 影藏中间字符串，规则{@code &E}: 影藏结尾字符串，规则{@code &S}: 影藏起始字符串
     * @param mark     掩码字符
     * @param markRate 掩码占用比率，默认0.65
     * @return 返回数据脱敏后的文字
     */
    public static String desensitization(
            final String data, final String rule, final char mark, final Double markRate) {
        return desensitization(data, rule, mark, null, null);
    }

    /**
     * 文字脱敏
     *
     * @param data         字符串数据
     * @param rule         数据脱敏规则，规则：{@code &C}: 影藏中间字符串，规则{@code &E}: 影藏结尾字符串，规则{@code &S}: 影藏起始字符串
     * @param mark         掩码字符
     * @param targetLength 目标文字长度
     * @param markRate     掩码占用比率，默认0.65
     * @return 返回数据脱敏后的文字
     */
    public static String desensitization(
            final String data,
            final String rule,
            final char mark,
            final Integer targetLength,
            final Double markRate) {
        final int finishLength =
                targetLength != null ? Math.min(data.length(), targetLength) : data.length();
        final double markRatio = markRate == null || markRate <= 0 || markRate >= 1 ? 0.65 : markRate;
        final int markLength = (int) (finishLength * markRatio) + 1;
        final int charLength = finishLength - markLength;
        TextBuilder cas = TextBuilder.of();
        if ("&C".equalsIgnoreCase(rule)) {
            final int scl = charLength / 2;
            final int ecl = charLength - scl;
            cas.append(data, 0, scl);
            ToolString.repeat(cas, mark, markLength);
            cas.append(data, data.length() - ecl, data.length());
            return cas.toString();
        } else if ("&S".equalsIgnoreCase(rule)) {
            ToolString.repeat(cas, mark, markLength);
            cas.append(data, data.length() - charLength, data.length());
            return cas.toString();
        } else if ("&E".equalsIgnoreCase(rule)) {
            cas.append(data, 0, charLength);
            ToolString.repeat(cas, mark, markLength);
            return cas.toString();
        } else if (Whether.empty(rule)) {
            return data;
        } else {
            throw new IllegalArgumentException("Error rule '" + rule + "',Rules must be in (&C,&S,&E)");
        }
    }
}

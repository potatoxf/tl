package pxf.tl.text.password;


import pxf.tl.collection.map.Parametric;

/**
 * @author potatoxf
 */
public abstract class PasswordSecurityChecker {

    public static final String[] DEFAULT_SIMILAR_CHAR_SEQUENCE_TEMPLATE = {
            "qwertyuiopasdfghjklzxcvbnm",
            "QWERTYUIOPASDFGHJKLZXCVBNM",
            "1234567890",
            "0987654321",
            "qazwsxedcrfvtgbyhnujmikolp",
            "QAZWSXEDCRFVTGBYHNUJMIKOLP",
    };
    /**
     * 至少字符长度
     */
    private final int atLeastLength = 8;
    /**
     * 至少大写字符
     */
    private final int atLeastUpperLetterCount = 2;
    /**
     * 至少小写字符
     */
    private final int atLeastLowerLetterCount = 2;
    /**
     * 至少数字字符
     */
    private final int atLeastNumberCount = 2;
    /**
     * 至少符号字符
     */
    private final int atLeastSymbolCount = 2;
    /**
     * 允许最大相似长度
     */
    private final int notOverSimilarLength = 6;
    /**
     * 允许最大重复长度
     */
    private final int notOverRepeatLength = 3;
    /**
     * 最大重复段倍数
     */
    private final int notOverRepeatSegmentMultiple = 4;
    /**
     * 检查相似序列最小长度
     */
    private final int atLeastSimilarCharSequenceLength = 3;
    /**
     * 检查相似序列模板
     */
    private final String[] similarCharSequenceTemplate = DEFAULT_SIMILAR_CHAR_SEQUENCE_TEMPLATE;

    /**
     * @param password
     * @return
     */
    public final double check(String password) {
        return check(password, null);
    }

    /**
     * @param password
     * @param readOnlyCaseInsensitiveMap
     * @return
     */
    public final double check(
            String password, Parametric readOnlyCaseInsensitiveMap) {
        double value = calculatePasswordStrength(password, readOnlyCaseInsensitiveMap);
        if (value < 0 || value > 1) {
            throw new ArithmeticException("");
        }
        return value;
    }

    /**
     * @param password
     * @param readOnlyCaseInsensitiveMap
     * @return
     */
    protected abstract double calculatePasswordStrength(
            String password, Parametric readOnlyCaseInsensitiveMap);
}

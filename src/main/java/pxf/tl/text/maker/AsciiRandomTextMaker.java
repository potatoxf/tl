package pxf.tl.text.maker;

import java.util.Random;

/**
 * 生成随机文本为数字和大小写字母混合
 *
 * @author potatoxf
 */
public class AsciiRandomTextMaker extends AbstractRandomTextMaker {
    /**
     * 下界
     */
    private final int downLimit;
    /**
     * 上界
     */
    private final int upperLimit;

    public AsciiRandomTextMaker(int downLimit, int upperLimit) {
        if (downLimit >= upperLimit) {
            throw new IllegalArgumentException("Lower bound must be smaller than upper bound");
        }
        if (upperLimit > 127 || downLimit < 0) {
            throw new IllegalArgumentException("Must be between 0-127");
        }
        this.upperLimit = upperLimit;
        this.downLimit = downLimit;
    }

    /**
     * 生成随机Unicode字符
     *
     * @param random 随机器
     */
    @Override
    protected int makeRandomUnicodeChar(Random random) {
        int r;
        do {
            r = random.nextInt(upperLimit + 1);
        } while (r < downLimit && !isMatch(r));
        return r;
    }

    protected boolean isMatch(int codepoint) {
        return true;
    }
}

package pxf.tl.text.maker;

import java.util.Random;

/**
 * creates random text from an array of characters
 *
 * @author potatoxf
 */
public class CustomizeTextMaker extends AbstractRandomTextMaker implements TextMaker {

    private final char[] reselectedChars;

    public CustomizeTextMaker(char[] reselectedChars) {
        if (reselectedChars == null || reselectedChars.length == 0) {
            throw new IllegalArgumentException("Preselected characters are not allowed to be empty");
        }
        this.reselectedChars = reselectedChars;
    }

    /**
     * 生成随机Unicode字符
     *
     * @param random 随机器
     */
    @Override
    protected int makeRandomUnicodeChar(Random random) {
        return reselectedChars[random.nextInt(reselectedChars.length)];
    }
}

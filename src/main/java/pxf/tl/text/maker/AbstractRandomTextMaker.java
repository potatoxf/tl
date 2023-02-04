package pxf.tl.text.maker;


import pxf.tl.help.Whether;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author potatoxf
 */
public abstract class AbstractRandomTextMaker implements TextMaker {

    /**
     * 默认长度
     */
    private static final int DEFAULT_LENGTH = 4;
    /**
     * 随机
     */
    private SecureRandom secureRandom = new SecureRandom();
    /**
     * 长度
     */
    private int length = DEFAULT_LENGTH;
    /**
     * 排除字符串
     */
    private String excludeString;
    /**
     * 最大冲突次数
     */
    private int maxConflictCount = Byte.MAX_VALUE;

    /**
     * 制造文本
     *
     * @return {@code String}
     */
    @Override
    public String make() {
        return make(DEFAULT_LENGTH);
    }

    /**
     * 制造文本
     *
     * @param length 指定长度
     * @return {@code String}
     */
    @Override
    public String make(int length) {
        SecureRandom random = getSecureRandom();
        if (random == null) {
            random = new SecureRandom();
        }
        if (length < DEFAULT_LENGTH) {
            length = getLength();
            if (length < DEFAULT_LENGTH) {
                length = DEFAULT_LENGTH;
            }
        }
        String excludeString = getExcludeString();
        if (excludeString == null) {
            excludeString = "";
        }
        StringBuilder container = new StringBuilder(length);
        makeRandomText(container, random, length, excludeString);
        return container.toString();
    }

    /**
     * 生成随机文本
     *
     * @param container     文本容器
     * @param random        随机器
     * @param length        总长度
     * @param excludeString 排除文本
     */
    protected void makeRandomText(
            StringBuilder container, Random random, int length, String excludeString) {
        int i = 0;
        while (i < length) {
            char c;
            int count = 0;
            while (true) {
                int codepoint = makeRandomUnicodeChar(random);
                char[] chars = Character.toChars(codepoint);
                if (chars.length != 1) {
                    throw new RuntimeException(
                            "Characters are generated incorrectly, unicode ["
                                    + codepoint
                                    + "] is not in the range of char characters");
                }
                c = chars[0];
                if (Whether.letterChar(codepoint) || Whether.numberChar(codepoint)) {
                    if (excludeString.indexOf(c) < 0) {
                        break;
                    } else {
                        count++;
                    }
                }
                if (count > maxConflictCount) {
                    throw new RuntimeException(
                            "Consecutive "
                                    + count
                                    + " generates characters in the excluded string and throws an exception");
                }
            }
            container.append(c);
            i++;
        }
    }

    /**
     * 生成随机Unicode字符
     *
     * @param random 随机器
     */
    protected abstract int makeRandomUnicodeChar(Random random);

    public SecureRandom getSecureRandom() {
        return secureRandom;
    }

    public void setSecureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getExcludeString() {
        return excludeString;
    }

    public void setExcludeString(String excludeString) {
        this.excludeString = excludeString;
    }

    public int getMaxConflictCount() {
        return maxConflictCount;
    }

    public void setMaxConflictCount(int maxConflictCount) {
        this.maxConflictCount = maxConflictCount;
    }
}

package pxf.tl.text.finder;


import pxf.tl.help.Assert;

import java.io.Serial;
import java.util.function.Predicate;

/**
 * 字符匹配查找器<br>
 * 查找满足指定{@link Predicate} 匹配的字符所在位置，此类长用于查找某一类字符，如数字等
 *
 * @author potatoxf
 */
public class CharMatcherFinder extends TextFinder {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Predicate<Character> predicate;

    /**
     * 构造
     *
     * @param predicate 被查找的字符匹配器
     */
    public CharMatcherFinder(Predicate<Character> predicate) {
        this.predicate = predicate;
    }

    @Override
    public int start(int from) {
        Assert.notNull(this.text, "Text to find must be not null!");
        final int limit = getValidEndIndex();
        if (negative) {
            for (int i = from; i > limit; i--) {
                if (predicate.test(text.charAt(i))) {
                    return i;
                }
            }
        } else {
            for (int i = from; i < limit; i++) {
                if (predicate.test(text.charAt(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int end(int start) {
        if (start < 0) {
            return -1;
        }
        return start + 1;
    }
}

package pxf.tl.text.cron.pattern.matcher;


import java.util.function.Predicate;

/**
 * 表达式中的某个位置部分匹配器<br>
 * 用于匹配日期位中对应数字是否匹配
 *
 * @author potatoxf
 */
public interface PartMatcher extends Predicate<Integer> {

    /**
     * 获取指定值之后的匹配值，也可以是指定值本身
     *
     * @param value 指定的值
     * @return 匹配到的值或之后的值
     */
    int nextAfter(int value);
}

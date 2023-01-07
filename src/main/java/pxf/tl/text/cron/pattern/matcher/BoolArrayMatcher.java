package pxf.tl.text.cron.pattern.matcher;


import pxf.tl.help.Assert;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolString;

import java.util.Collections;
import java.util.List;

/**
 * 将表达式中的数字值列表转换为Boolean数组，匹配时匹配相应数组位
 *
 * @author potatoxf
 */
public class BoolArrayMatcher implements PartMatcher {

    /**
     * 用户定义此字段的最小值
     */
    private final int minValue;

    private final boolean[] bValues;

    /**
     * 构造
     *
     * @param intValueList 匹配值列表
     */
    public BoolArrayMatcher(List<Integer> intValueList) {
        Assert.isTrue(Whether.noEmpty(intValueList), "Values must be not empty!");
        bValues = new boolean[Collections.max(intValueList) + 1];
        int min = Integer.MAX_VALUE;
        for (Integer value : intValueList) {
            min = Math.min(min, value);
            bValues[value] = true;
        }
        this.minValue = min;
    }

    @Override
    public boolean test(Integer value) {
        if (null == value || value >= bValues.length) {
            return false;
        }
        return bValues[value];
    }

    @Override
    public int nextAfter(int value) {
        if (value > minValue) {
            while (value < bValues.length) {
                if (bValues[value]) {
                    return value;
                }
                value++;
            }
        }

        // 两种情况返回最小值
        // 一是给定值小于最小值，那下一个匹配值就是最小值
        // 二是给定值大于最大值，那下一个匹配值也是下一轮的最小值
        return minValue;
    }

    /**
     * 获取表达式定义的最小值
     *
     * @return 最小值
     */
    public int getMinValue() {
        return this.minValue;
    }

    @Override
    public String toString() {
        return ToolString.format("Matcher:{}", new Object[]{this.bValues});
    }
}

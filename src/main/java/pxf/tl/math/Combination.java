package pxf.tl.math;


import pxf.tl.util.ToolNumber;
import pxf.tl.util.ToolString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 组合，即C(n, m)<br>
 * 排列组合相关类 参考：http://cgs1999.iteye.com/blog/2327664
 *
 * @author potatoxf
 */
public class Combination implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String[] dataArray;

    /**
     * 组合，即C(n, m)<br>
     * 排列组合相关类 参考：http://cgs1999.iteye.com/blog/2327664
     *
     * @param dataArray 用于组合的数据
     */
    public Combination(String[] dataArray) {
        this.dataArray = dataArray;
    }

    /**
     * 计算组合数，即C(n, m) = n!/((n-m)! * m!)
     *
     * @param n 总数
     * @param m 选择的个数
     * @return 组合数
     */
    public static long count(int n, int m) {
        if (0 == m || n == m) {
            return 1;
        }
        return (n > m) ? ToolNumber.factorial(n, n - m) / ToolNumber.factorial(m) : 0;
    }

    /**
     * 计算组合总数，即C(n, 1) + C(n, 2) + C(n, 3)...
     *
     * @param n 总数
     * @return 组合数
     */
    public static long countAll(int n) {
        if (n < 0 || n > 63) {
            throw new IllegalArgumentException(
                    ToolString.format("countAll must have n >= 0 and n <= 63, but got n={}", n));
        }
        return n == 63 ? Long.MAX_VALUE : (1L << n) - 1;
    }

    /**
     * 组合选择（从列表中选择m个组合）
     *
     * @param m 选择个数
     * @return 组合结果
     */
    public List<String[]> select(int m) {
        final List<String[]> result = new ArrayList<>((int) count(this.dataArray.length, m));
        select(0, new String[m], 0, result);
        return result;
    }

    /**
     * 全组合
     *
     * @return 全排列结果
     */
    public List<String[]> selectAll() {
        final List<String[]> result = new ArrayList<>((int) countAll(this.dataArray.length));
        for (int i = 1; i <= this.dataArray.length; i++) {
            result.addAll(select(i));
        }
        return result;
    }

    /**
     * 组合选择
     *
     * @param dataIndex   待选开始索引
     * @param resultList  前面（resultIndex-1）个的组合结果
     * @param resultIndex 选择索引，从0开始
     * @param result      结果集
     */
    private void select(int dataIndex, String[] resultList, int resultIndex, List<String[]> result) {
        int resultLen = resultList.length;
        int resultCount = resultIndex + 1;
        if (resultCount > resultLen) { // 全部选择完时，输出组合结果
            result.add(Arrays.copyOf(resultList, resultList.length));
            return;
        }

        // 递归选择下一个
        for (int i = dataIndex; i < dataArray.length + resultCount - resultLen; i++) {
            resultList[resultIndex] = dataArray[i];
            select(i + 1, resultList, resultIndex + 1, result);
        }
    }
}

package pxf.tl.text.capitulation;


import pxf.tl.help.Whether;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 中文整数转大写助手
 *
 * @author potatoxf
 */
public final class ChineseIntegerHelper {

    /**
     * 0-9对应数字
     */
    private final String[] digit;

    /**
     * 小单位序列
     */
    private final String[] smallUnit;

    /**
     * 大单位序列
     */
    private final String[] bigUnit;

    /**
     * 间隔，没多少个小单位添加大单位
     */
    private final int interval;

    /**
     * 简化书写
     */
    private final Map<String, String> simplification;

    /**
     * 构造函数
     *
     * @param digit                  0-9对应数字
     * @param smallUnit              小单位
     * @param bigUnit                大单位序列
     * @param simplificationKeyValue 简化书写的元素
     */
    public ChineseIntegerHelper(
            String[] digit, String[] smallUnit, String[] bigUnit, String... simplificationKeyValue) {

        this.digit = digit;
        this.smallUnit = smallUnit;
        this.bigUnit = bigUnit;
        this.interval = smallUnit.length;
        if (Whether.empty(simplificationKeyValue)) {
            this.simplification = null;
        } else {
            int len = simplificationKeyValue.length / 2;
            Map<String, String> map = new HashMap<String, String>(len, 1);
            for (int i = 0; i < len; i++) {
                map.put(simplificationKeyValue[i++], simplificationKeyValue[i]);
            }
            this.simplification = map;
        }
    }

    /**
     * 构造函数
     *
     * @param digit          0-9对应数字
     * @param smallUnit      小单位序列
     * @param bigUnit        大单位序列
     * @param simplification 简化书写
     */
    public ChineseIntegerHelper(
            final String[] digit,
            final String[] smallUnit,
            final String[] bigUnit,
            final Map<String, String> simplification) {

        this.digit = digit;
        this.smallUnit = smallUnit;
        this.bigUnit = bigUnit;
        this.interval = smallUnit.length;
        this.simplification = simplification;
    }

    /**
     * 解析整数部分
     *
     * @param integerNumber 整数
     * @return 返回正规整数
     */
    public String resolve(String integerNumber) {
        if (!Whether.integerNumber(integerNumber)) {
            throw new IllegalArgumentException("Input parameter is not an integerNumber");
        }
        return resolveNumber(integerNumber);
    }

    /**
     * 解析整数部分
     *
     * @param integerNumber 整数
     * @return 返回正规整数
     */
    String resolveNumber(String integerNumber) {
        LinkedList<String> stack = new LinkedList<String>();
        int length = integerNumber.length();
        for (int i = 0; i < length; i++) {
            char c = integerNumber.charAt(length - i - 1);
            String digitString;
            if (c == '0') {
                digitString = "0";
            } else {
                digitString = digit[Character.digit(c, 10)] + smallUnit[i % interval];
            }
            if (i % interval == 0) {
                stack.push(digitString + bigUnit[i / interval]);
            } else {
                stack.push(digitString);
            }
        }
        StringBuilder sb = new StringBuilder(integerNumber.length() * 2);
        boolean lastZero = false;
        for (String s : stack) {
            if ("0".equals(s)) {
                if (!lastZero) {
                    sb.append(digit[0]);
                    lastZero = true;
                }
            } else {
                lastZero = false;
                if (s.startsWith("0")) {
                    sb.append(s.substring(1));
                } else {
                    if (simplification == null) {
                        sb.append(s);
                    } else {
                        String value = simplification.get(s);
                        sb.append(value == null ? s : value);
                    }
                }
            }
        }
        return sb.toString();
    }

    public int getMaxSupportLength() {
        return bigUnit.length * interval;
    }
}

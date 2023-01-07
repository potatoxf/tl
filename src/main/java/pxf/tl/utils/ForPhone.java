package pxf.tl.utils;

import pxf.tl.api.PoolOfPattern;
import pxf.tl.util.ToolRegex;
import pxf.tl.util.ToolString;

/**
 * 电话号码工具类，包括：
 *
 * <ul>
 *   <li>手机号码
 *   <li>400、800号码
 *   <li>座机号码
 * </ul>
 *
 * @author potatoxf
 */
public class ForPhone {

    /**
     * 验证是否为手机号码（中国）
     *
     * @param value 值
     * @return 是否为手机号码（中国）
     */
    public static boolean isMobile(CharSequence value) {
        return ToolRegex.isMatchRegex(PoolOfPattern.MOBILE, value);
    }

    /**
     * 验证是否为手机号码（香港）
     *
     * @param value 手机号码
     * @return 是否为香港手机号码
     * @author potatoxf
     */
    public static boolean isMobileHk(CharSequence value) {
        return ToolRegex.isMatchRegex(PoolOfPattern.MOBILE_HK, value);
    }

    /**
     * 验证是否为手机号码（台湾）
     *
     * @param value 手机号码
     * @return 是否为台湾手机号码
     * @author potatoxf
     */
    public static boolean isMobileTw(CharSequence value) {
        return ToolRegex.isMatchRegex(PoolOfPattern.MOBILE_TW, value);
    }

    /**
     * 验证是否为手机号码（澳门）
     *
     * @param value 手机号码
     * @return 是否为澳门手机号码
     * @author potatoxf
     */
    public static boolean isMobileMo(CharSequence value) {
        return ToolRegex.isMatchRegex(PoolOfPattern.MOBILE_MO, value);
    }

    /**
     * 验证是否为座机号码（中国）
     *
     * @param value 值
     * @return 是否为座机号码（中国）
     */
    public static boolean isTel(CharSequence value) {
        return ToolRegex.isMatchRegex(PoolOfPattern.TEL, value);
    }

    /**
     * 验证是否为座机号码（中国）+ 400 + 800
     *
     * @param value 值
     * @return 是否为座机号码（中国）
     * @author potatoxf
     */
    public static boolean isTel400800(CharSequence value) {
        return ToolRegex.isMatchRegex(PoolOfPattern.TEL_400_800, value);
    }

    /**
     * 验证是否为座机号码+手机号码（CharUtil中国）+ 400 + 800电话 + 手机号号码（香港）
     *
     * @param value 值
     * @return 是否为座机号码+手机号码（中国）+手机号码（香港）+手机号码（台湾）+手机号码（澳门）
     */
    public static boolean isPhone(CharSequence value) {
        return isMobile(value)
                || isTel400800(value)
                || isMobileHk(value)
                || isMobileTw(value)
                || isMobileMo(value);
    }

    /**
     * 隐藏手机号前7位 替换字符为"*" 栗子
     *
     * @param phone 手机号码
     * @return 替换后的字符串
     */
    public static CharSequence hideBefore(CharSequence phone) {
        return ToolString.hide(phone, 0, 7);
    }

    /**
     * 隐藏手机号中间4位 替换字符为"*"
     *
     * @param phone 手机号码
     * @return 替换后的字符串
     */
    public static CharSequence hideBetween(CharSequence phone) {
        return ToolString.hide(phone, 3, 7);
    }

    /**
     * 隐藏手机号最后4位 替换字符为"*"
     *
     * @param phone 手机号码
     * @return 替换后的字符串
     */
    public static CharSequence hideAfter(CharSequence phone) {
        return ToolString.hide(phone, 7, 11);
    }

    /**
     * 获取手机号前3位
     *
     * @param phone 手机号码
     * @return 手机号前3位
     */
    public static CharSequence subBefore(CharSequence phone) {
        return ToolString.sub(phone, 0, 3);
    }

    /**
     * 获取手机号中间4位
     *
     * @param phone 手机号码
     * @return 手机号中间4位
     */
    public static CharSequence subBetween(CharSequence phone) {
        return ToolString.sub(phone, 3, 7);
    }

    /**
     * 获取手机号后4位
     *
     * @param phone 手机号码
     * @return 手机号后4位
     */
    public static CharSequence subAfter(CharSequence phone) {
        return ToolString.sub(phone, 7, 11);
    }

    /**
     * 获取固话号码中的区号
     *
     * @param value 完整的固话号码
     * @return 固话号码的区号部分
     */
    public static CharSequence subTelBefore(CharSequence value) {
        return ToolRegex.getGroup1(PoolOfPattern.TEL, value);
    }

    /**
     * 获取固话号码中的号码
     *
     * @param value 完整的固话号码
     * @return 固话号码的号码部分
     */
    public static CharSequence subTelAfter(CharSequence value) {
        return ToolRegex.get(PoolOfPattern.TEL, value, 2);
    }
}

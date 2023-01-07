package pxf.tl.api;

import java.util.regex.Pattern;

/**
 * 常用正则表达式集合，更多正则见:<br>
 * <a href="https://any86.github.io/any-rule/">https://any86.github.io/any-rule/</a>
 *
 * @author potatoxf
 */
public interface PoolOfPattern {
    /**
     * TOKEN标识
     */
    Pattern TOKEN = Pattern.compile(PoolOfRegex.TOKEN);
    /**
     *
     */
    Pattern PLACEHOLDER = Pattern.compile(PoolOfRegex.PLACEHOLDER);
    /**
     * 文件目录名正则表达式
     */
    Pattern FILE_DIR_NAME = Pattern.compile(PoolOfRegex.FILE_DIR_NAME);
    /**
     * 正则表达式特殊字符
     */
    Pattern ESCAPE_REGEX = Pattern.compile(PoolOfRegex.ESCAPE_REGEX);
    /**
     * 英文字母 、数字和下划线
     */
    Pattern GENERAL = Pattern.compile(PoolOfRegex.GENERAL);
    /**
     * 数字
     */
    Pattern NUMBERS = Pattern.compile(PoolOfRegex.NUMBERS);
    /**
     * 小数
     */
    Pattern DECIMAL = Pattern.compile(PoolOfRegex.DECIMAL);
    /**
     * 整数
     */
    Pattern INTEGER = Pattern.compile(PoolOfRegex.INTEGER);
    /**
     * 八进制
     */
    Pattern OCT = Pattern.compile(PoolOfRegex.OCT);
    /**
     * 16进制字符串
     */
    Pattern HEX = Pattern.compile(PoolOfRegex.HEX);
    /**
     * 字母
     */
    Pattern WORD = Pattern.compile(PoolOfRegex.WORD);
    /**
     * 单个中文汉字
     */
    Pattern CHINESE = Pattern.compile(PoolOfRegex.CHINESE);
    /**
     * 中文汉字
     */
    Pattern CHINESES = Pattern.compile(PoolOfRegex.CHINESES);
    /**
     * 分组
     */
    Pattern GROUP_VAR = Pattern.compile(PoolOfRegex.GROUP_VAR);
    /**
     * UUID
     */
    Pattern UUID = Pattern.compile(PoolOfRegex.UUID, Pattern.CASE_INSENSITIVE);
    /**
     * 不带横线的UUID
     */
    Pattern UUID_SIMPLE = Pattern.compile(PoolOfRegex.UUID_SIMPLE);
    /**
     * IP v4
     */
    Pattern IPV4 = Pattern.compile(PoolOfRegex.IPV4);
    /**
     * IP v6
     */
    Pattern IPV6 = Pattern.compile(PoolOfRegex.IPV6);
    /**
     * 端口号正则表达式
     */
    Pattern PORT = Pattern.compile(PoolOfRegex.PORT);
    /**
     * 十六进制端口号正则表达式
     */
    Pattern PORT_HEX = Pattern.compile(PoolOfRegex.PORT_HEX);
    /**
     * URI
     */
    Pattern URI = Pattern.compile(PoolOfRegex.URI);
    /**
     * URL
     */
    Pattern URL = Pattern.compile(PoolOfRegex.URL);
    /**
     * Http URL
     */
    Pattern URL_HTTP =
            Pattern.compile(PoolOfRegex.URL_HTTP, Pattern.CASE_INSENSITIVE);
    /**
     * 12小时制和24小时制
     */
    Pattern TIME = Pattern.compile(PoolOfRegex.TIME);
    /**
     * 时间范围
     */
    Pattern TIME_RANGE = Pattern.compile(PoolOfRegex.TIME_RANGE);
    /**
     * 货币
     */
    Pattern MONEY = Pattern.compile(PoolOfRegex.MONEY);
    /**
     * 邮件，符合RFC 5322规范，正则来自：<a href="http://emailregex.com/">http://emailregex.com/</a><br>
     * <a
     * href="https://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address/44317754">https://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address/44317754</a>
     * 注意email 要宽松一点。比如 jetz.chong@hutool.cn、jetz-chong@
     * hutool.cn、jetz_chong@hutool.cn、dazhi.duan@hutool.cn 宽松一点把，都算是正常的邮箱
     */
    Pattern EMAIL = Pattern.compile(PoolOfRegex.EMAIL, Pattern.CASE_INSENSITIVE);
    /**
     * 移动电话
     */
    Pattern MOBILE = Pattern.compile(PoolOfRegex.MOBILE);
    /**
     * 中国香港移动电话 eg: 中国香港： +852 5100 4810， 三位区域码+10位数字, 中国香港手机号码8位数 eg: 中国大陆： +86 180 4953
     * 1399，2位区域码标示+13位数字 中国大陆 +86 Mainland China 中国香港 +852 Hong Kong 中国澳门 +853 Macao 中国台湾 +886 Taiwan
     */
    Pattern MOBILE_HK = Pattern.compile(PoolOfRegex.MOBILE_HK);
    /**
     * 中国台湾移动电话 eg: 中国台湾： +886 09 60 000000， 三位区域码+号码以数字09开头 + 8位数字, 中国台湾手机号码10位数 中国台湾 +886 Taiwan
     * 国际域名缩写：TW
     */
    Pattern MOBILE_TW = Pattern.compile(PoolOfRegex.MOBILE_TW);
    /**
     * 中国澳门移动电话 eg: 中国台湾： +853 68 00000， 三位区域码 +号码以数字6开头 + 7位数字, 中国台湾手机号码8位数 中国澳门 +853 Macao 国际域名缩写：MO
     */
    Pattern MOBILE_MO = Pattern.compile(PoolOfRegex.MOBILE_MO);
    /**
     * 座机号码
     */
    Pattern TEL = Pattern.compile(PoolOfRegex.TEL);
    /**
     * 座机号码+400+800电话
     *
     * @see <a href="https://baike.baidu.com/item/800">800</a>
     */
    Pattern TEL_400_800 = Pattern.compile(PoolOfRegex.TEL_400_800);
    /**
     * 18位身份证号码
     */
    Pattern CITIZEN_ID = Pattern.compile(PoolOfRegex.CITIZEN_ID);
    /**
     * 邮编，兼容港澳台
     */
    Pattern ZIP_CODE = Pattern.compile(PoolOfRegex.ZIP_CODE);
    /**
     * 生日
     */
    Pattern BIRTHDAY = Pattern.compile(PoolOfRegex.BIRTHDAY);
    /**
     * 中文字、英文字母、数字和下划线
     */
    Pattern GENERAL_WITH_CHINESE =
            Pattern.compile(PoolOfRegex.GENERAL_WITH_CHINESE);
    /**
     * MAC地址正则
     */
    Pattern MAC_ADDRESS =
            Pattern.compile(PoolOfRegex.MAC_ADDRESS, Pattern.CASE_INSENSITIVE);

    /**
     * 中国车牌号码（兼容新能源车牌）
     */
    Pattern PLATE_NUMBER = Pattern.compile(PoolOfRegex.PLATE_NUMBER);
    /**
     * 统一社会信用代码
     *
     * <pre>
     * 第一部分：登记管理部门代码1位 (数字或大写英文字母)
     * 第二部分：机构类别代码1位 (数字或大写英文字母)
     * 第三部分：登记管理机关行政区划码6位 (数字)
     * 第四部分：主体标识码（组织机构代码）9位 (数字或大写英文字母)
     * 第五部分：校验码1位 (数字或大写英文字母)
     * </pre>
     */
    Pattern CREDIT_CODE = Pattern.compile(PoolOfRegex.CREDIT_CODE);
    /**
     * 车架号 别名：车辆识别代号 车辆识别码 eg:LDC613P23A1305189 eg:LSJA24U62JG269225 十七位码、车架号 车辆的唯一标示
     */
    Pattern CAR_VIN = Pattern.compile(PoolOfRegex.CAR_VIN);
    /**
     * 驾驶证 别名：驾驶证档案编号、行驶证编号 eg:430101758218 12位数字字符串 仅限：中国驾驶证档案编号
     */
    Pattern CAR_DRIVING_LICENCE = Pattern.compile(PoolOfRegex.CAR_DRIVING_LICENCE);
    /**
     * 中文姓名 总结中国人姓名：2-60位，只能是中文和 ·
     */
    Pattern CHINESE_NAME = Pattern.compile(PoolOfRegex.CHINESE_NAME);
    /**
     * 手机号码
     */
    Pattern PHONE = Pattern.compile(PoolOfRegex.PHONE);
}

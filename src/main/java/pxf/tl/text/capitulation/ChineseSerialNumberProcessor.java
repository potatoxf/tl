package pxf.tl.text.capitulation;


import pxf.tl.exception.UnsupportedException;
import pxf.tl.util.ToolNumber;

/**
 * 中文序号处理器
 *
 * @author potatoxf
 */
public class ChineseSerialNumberProcessor implements SerialNumberProcessor {

    private static final String[] DIGIT = {"〇", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

    private static final String[] SMALL_UNIT = {"", "十", "百", "千"};

    private static final String[] BIG_UNIT = {"", "万", "亿", "兆"};

    private final ChineseIntegerHelper chineseIntegerHelper =
            new ChineseIntegerHelper(DIGIT, SMALL_UNIT, BIG_UNIT, "一十", "十");

    @Override
    public String handle(int serialNumber) {
        if (serialNumber < 0) {
            throw new IllegalArgumentException("The serial number must be greater 0");
        }
        int len = ToolNumber.numberOfDigits(serialNumber);
        int maxSupportLength = this.chineseIntegerHelper.getMaxSupportLength();
        if (len > maxSupportLength) {
            throw new UnsupportedException("The serial number must be lesser " + maxSupportLength);
        }
        return this.chineseIntegerHelper.resolve(String.valueOf(serialNumber));
    }
}

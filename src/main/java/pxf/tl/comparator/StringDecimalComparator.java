package pxf.tl.comparator;


import java.math.BigDecimal;
import java.util.Comparator;

/**
 * 字符串数字比较器
 *
 * @author potatoxf
 */
public class StringDecimalComparator implements Comparator<String> {

    /**
     *
     */
    private final BigDecimal defaultValue;
    /**
     *
     */
    private final boolean isDecimal;

    /**
     * @param defaultValue
     * @param isDecimal
     */
    public StringDecimalComparator(BigDecimal defaultValue, boolean isDecimal) {
        this.defaultValue = defaultValue;
        this.isDecimal = isDecimal;
    }

    /**
     * @param num1
     * @param num2
     * @return
     */
    @Override
    public int compare(String num1, String num2) {
        BigDecimal b1 = new BigDecimal(num1);
        BigDecimal b2 = new BigDecimal(num2);
        if (isDecimal) {
            return b1.compareTo(b2);
        }
        long x = b1.longValueExact();
        long y = b2.longValueExact();
        return Long.compare(x, y);
    }
}

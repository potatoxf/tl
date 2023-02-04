package pxf.tl.date.format;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期格式化输出接口<br>
 * Thanks to Apache Commons Lang 3.5
 *
 * @author potatoxf
 */
public interface DatePrinter extends DateBasic {

    /**
     * 格式化日期表示的毫秒数
     *
     * @param millis 日期毫秒数
     * @return the formatted string
     */
    String format(long millis);

    /**
     * 使用 {@code GregorianCalendar} 格式化 {@code Date}
     *
     * @param date 日期 {@link Date}
     * @return 格式化后的字符串
     */
    String format(Date date);

    /**
     * Formats a {@code Calendar} object. 格式化 {@link Calendar}
     *
     * @param calendar {@link Calendar}
     * @return 格式化后的字符串
     */
    String format(Calendar calendar);

    /**
     * Formats a millisecond {@code long} value into the supplied {@code Appendable}.
     *
     * @param millis the millisecond value to format
     * @param buf    the buffer to format into
     * @param <B>    the Appendable class type, usually StringBuilder or StringBuffer.
     * @return the specified string buffer
     */
    <B extends Appendable> B format(long millis, B buf);

    /**
     * Formats a {@code Date} object into the supplied {@code Appendable} using a {@code
     * GregorianCalendar}.
     *
     * @param date the date to format
     * @param buf  the buffer to format into
     * @param <B>  the Appendable class type, usually StringBuilder or StringBuffer.
     * @return the specified string buffer
     */
    <B extends Appendable> B format(Date date, B buf);

    /**
     * Formats a {@code Calendar} object into the supplied {@code Appendable}. The TimeZone set on the
     * Calendar is only used to adjust the time offset. The TimeZone specified during the construction
     * of the Parser will determine the TimeZone used in the formatted string.
     *
     * @param calendar the calendar to format
     * @param buf      the buffer to format into
     * @param <B>      the Appendable class type, usually StringBuilder or StringBuffer.
     * @return the specified string buffer
     */
    <B extends Appendable> B format(Calendar calendar, B buf);
}

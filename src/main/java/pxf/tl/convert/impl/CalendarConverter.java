package pxf.tl.convert.impl;


import pxf.tl.convert.AbstractConverter;
import pxf.tl.date.DateUtil;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolTime;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期转换器
 *
 * @author potatoxf
 */
public class CalendarConverter extends AbstractConverter<Calendar> {
    private static final long serialVersionUID = 1L;

    /**
     * 日期格式化
     */
    private String format;

    /**
     * 获取日期格式
     *
     * @return 设置日期格式
     */
    public String getFormat() {
        return format;
    }

    /**
     * 设置日期格式
     *
     * @param format 日期格式
     */
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    protected Calendar convertInternal(Object value) {
        // Handle Date
        if (value instanceof Date) {
            return ToolTime.calendar((Date) value);
        }

        // Handle Long
        if (value instanceof Long) {
            // 此处使用自动拆装箱
            return ToolTime.calendar((Long) value);
        }

        final String valueStr = convertToStr(value);
        return ToolTime.calendar(
                Whether.blank(format) ? DateUtil.parse(valueStr) : DateUtil.parse(valueStr, format));
    }
}

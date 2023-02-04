package pxf.tl.convert.impl;


import pxf.tl.convert.AbstractConverter;

import java.time.Period;
import java.time.temporal.TemporalAmount;

/**
 * {@link Period}对象转换器
 *
 * @author potatoxf
 */
public class PeriodConverter extends AbstractConverter<Period> {
    private static final long serialVersionUID = 1L;

    @Override
    protected Period convertInternal(Object value) {
        if (value instanceof TemporalAmount) {
            return Period.from((TemporalAmount) value);
        } else if (value instanceof Integer) {
            return Period.ofDays((Integer) value);
        } else {
            return Period.parse(convertToStr(value));
        }
    }
}

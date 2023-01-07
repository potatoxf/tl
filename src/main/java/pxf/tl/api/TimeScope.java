package pxf.tl.api;


import pxf.tl.iter.AnyIter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

/**
 * 日期范围
 *
 * @author potatoxf
 */
public final class TimeScope implements Serializable, Iterable<Date> {

    @Serial
    private static final long serialVersionUID = 1L;
    private Date start;
    private Date end;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        if (end != null && start != null) {
            if (end.compareTo(start) < 0) {
                throw new IllegalArgumentException("The end time must be more than the start time");
            }
        }
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        if (start != null && end != null) {
            if (start.compareTo(end) > 0) {
                throw new IllegalArgumentException("The start time must be less than the end time");
            }
        }
        this.end = end;
    }

    @Override
    public Iterator<Date> iterator() {
        if (start == null && end == null) {
            return AnyIter.ofEmpty();
        }
        return AnyIter.ofArray(start, end);
    }

    @Override
    public String toString() {
        return super.toString();
        //        if (start == null && end == null) {
        //            return "NO TIME RANGE";
        //        }
        //        if (start == null) {
        //            return "To[" + TimeHelper.formatDefaultDatetime(end) + "]";
        //        }
        //        if (end == null) {
        //            return "From [" + TimeHelper.formatDefaultDatetime(start) + "]";
        //        }
        //        return "From ["
        //                + TimeHelper.formatDefaultDatetime(start)
        //                + "]To["
        //                + TimeHelper.formatDefaultDatetime(end)
        //                + "]";
    }
}

package pxf.tl.database.type.handler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author potatoxf
 */
public class TypeParameter {
    private Charset charset = StandardCharsets.UTF_8;

    private Calendar calendar = Calendar.getInstance();

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}

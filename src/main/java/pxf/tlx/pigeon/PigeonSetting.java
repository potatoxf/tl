package pxf.tlx.pigeon;


import pxf.tl.api.Charsets;

import java.util.Locale;

/**
 * @author potatoxf
 */
public class PigeonSetting {

    /**
     * 字节码
     */
    private Charsets charsets = Charsets.UTF_8;

    private Locale[] locales = null;

    public Charsets getCharsetToken() {
        return charsets;
    }

    public void setCharsetToken(Charsets charsets) {
        this.charsets = charsets;
    }

    public Locale[] getLocales() {
        return locales;
    }

    public void setLocales(Locale[] locales) {
        this.locales = locales;
    }
}

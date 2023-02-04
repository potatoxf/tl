package pxf.tlx.pigeon;


import pxf.tl.api.Charsets;

/**
 * @author potatoxf
 */
public class MessageContent {
    private PigeonSetting pigeonSetting;

    public void setPigeonSetting(PigeonSetting pigeonSetting) {
        this.pigeonSetting = pigeonSetting;
    }

    public Charsets getCharsetToken() {
        if (pigeonSetting != null) {
            Charsets charsets = pigeonSetting.getCharsetToken();
            if (charsets != null) {
                return charsets;
            }
        }
        return Charsets.UTF_8;
    }
}

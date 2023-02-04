package pxf.tl.image;

/**
 * 文字图像实体
 *
 * @author potatoxf
 */
public class TextImage extends CacheImageSupplier {

    /**
     *
     */
    private final String text;

    /**
     * @param text
     * @param format
     * @param image
     */
    public TextImage(String text, String format, byte[] image) {
        super(image, format);
        this.text = text;
    }

    /**
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return text;
    }
}

package pxf.tl.image.captcha;


import pxf.tl.image.TextImage;
import pxf.tl.image.decorator.ImageDecoratorChain;
import pxf.tl.text.maker.FiveLetterNameTextMaker;
import pxf.tl.text.maker.TextMaker;
import pxf.tl.text.renderer.SimpleMessyTextRenderer;
import pxf.tl.text.renderer.TextRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 抽象验证码产生器
 *
 * @author potatoxf
 */
public abstract class AbstractCaptchaProducer implements CaptchaProducer {

    /**
     * 图像装饰器链
     */
    private ImageDecoratorChain imageDecoratorChain;
    /**
     * 文本生成器
     */
    private TextMaker textMaker;
    /**
     * 文本渲染器
     */
    private TextRenderer textRenderer;
    /**
     * 图片格式
     */
    private String format = "jpg";
    /**
     * 宽
     */
    private int width = 200;
    /**
     * 高
     */
    private int height = 50;

    /**
     * 创建一个验证码
     *
     * @return {@code TextImage}
     */
    @Override
    public TextImage createCaptcha() throws IOException {
        String text = getTextMaker().make();
        BufferedImage image = getTextRenderer().render(text, width, height);
        image = decorate(image);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, byteArrayOutputStream);
        return new TextImage(text, format, byteArrayOutputStream.toByteArray());
    }

    protected BufferedImage decorate(BufferedImage image) throws IOException {
        if (imageDecoratorChain != null) {
            image = imageDecoratorChain.decorate(image);
        }
        return image;
    }

    public TextMaker getTextMaker() {
        if (textMaker == null) {
            textMaker = new FiveLetterNameTextMaker();
        }
        return textMaker;
    }

    public void setTextMaker(TextMaker textMaker) {
        this.textMaker = textMaker;
    }

    public TextRenderer getTextRenderer() {
        if (textRenderer == null) {
            textRenderer = new SimpleMessyTextRenderer();
        }
        return textRenderer;
    }

    public void setTextRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

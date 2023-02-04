package pxf.tl.image.captcha;


import pxf.tl.image.TextImage;
import pxf.tl.image.gif.GifEncoder;
import pxf.tl.text.renderer.MessyTextRenderer;
import pxf.tl.text.renderer.TextRenderer;
import pxf.tl.util.ToolFile;
import pxf.tl.util.ToolIO;

import java.io.File;
import java.io.IOException;

/**
 * Gif验证码生成器
 *
 * @author potatoxf
 */
public final class GifCaptchaProducer extends AbstractCaptchaProducer {

    /**
     * 图像数量
     */
    private int imageAmount = 10;
    /**
     * 图像重复次数，0一直重复
     */
    private int repeatCount = 0;
    /**
     * 延迟
     */
    private int delay = 200;

    public GifCaptchaProducer() {
        super.setTextRenderer(new MessyTextRenderer());
        super.setFormat("gif");
    }

    public int getImageAmount() {
        return imageAmount;
    }

    public void setImageAmount(int imageAmount) {
        this.imageAmount = imageAmount;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * 创建一个验证码
     *
     * @return {@code TextImage}
     */
    @Override
    public TextImage createCaptcha() throws IOException {
        String text = getTextMaker().make();
        File file = ToolFile.createTemporaryFile("gif");
        GifEncoder e = new GifEncoder();
        // 设置合成位置
        e.start(file.getAbsolutePath());
        e.setRepeat(repeatCount);
        e.setDelay(delay);
        TextRenderer textRenderer = getTextRenderer();
        for (int i = 0; i < imageAmount; i++) {
            e.addFrame(textRenderer.render(text, getWidth(), getHeight()));
        }
        if (!e.finish()) {
            throw new IOException("Failed to generate gif image");
        }
        return new TextImage(text, getFormat(), ToolIO.readAllBytes(file));
    }

    @Override
    public void setFormat(String format) {
        throw new UnsupportedOperationException();
    }
}

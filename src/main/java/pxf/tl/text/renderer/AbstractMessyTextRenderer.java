package pxf.tl.text.renderer;

import java.awt.*;
import java.security.SecureRandom;
import java.util.Random;

/**
 * 抽象模糊文本渲染器
 *
 * @author potatoxf
 */
public abstract class AbstractMessyTextRenderer extends AbstractTextRenderer {

    /**
     * 随机
     */
    protected final Random random;
    /**
     * 随机字体
     */
    private Font[] randomFont;
    /**
     * 随机颜色
     */
    private Color[] randomColor;

    protected AbstractMessyTextRenderer() {
        this(new SecureRandom());
    }

    protected AbstractMessyTextRenderer(Random random) {
        this.random = random;
    }

    @Override
    protected Color getCurrentColor(char[] chars, int i, Color defaultColor) {
        if (randomColor != null) {
            return randomColor[random.nextInt(randomColor.length)];
        }
        return super.getCurrentColor(chars, i, defaultColor);
    }

    @Override
    protected Font getCurrentFont(char[] chars, int i, Font defaultFont) {
        if (randomFont != null) {
            return randomFont[random.nextInt(randomFont.length)];
        }
        return super.getCurrentFont(chars, i, defaultFont);
    }

    public Font[] getRandomFont() {
        return randomFont;
    }

    public void setRandomFont(Font[] randomFont) {
        this.randomFont = randomFont;
    }

    public Color[] getRandomColor() {
        return randomColor;
    }

    public void setRandomColor(Color[] randomColor) {
        this.randomColor = randomColor;
    }
}

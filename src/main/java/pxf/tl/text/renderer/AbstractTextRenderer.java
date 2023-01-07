package pxf.tl.text.renderer;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

/**
 * 文本渲染器
 *
 * @author potatoxf
 */
public abstract class AbstractTextRenderer implements TextRenderer {
    /**
     * 字体
     */
    private Font font;
    /**
     * 字体颜色
     */
    private Color color;

    /**
     * 渲染文本成图画
     *
     * @param text   文本
     * @param width  宽度
     * @param height 高度
     * @return {@code BufferedImage}
     */
    @Override
    public BufferedImage render(String text, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = image.createGraphics();

        initGraphics(g2D);

        Font font = this.getFont();
        if (font == null) {
            font = g2D.getFont();
        }
        Color color = this.getColor();
        if (color == null) {
            color = g2D.getColor();
        }

        char[] chars = text.toCharArray();
        float[] charWidths = new float[chars.length];
        float[] charHeights = new float[chars.length];
        // get char width and height
        for (int i = 0; i < chars.length; i++) {
            Font currentFont = getCurrentFont(chars, i, font);
            GlyphVector gv =
                    currentFont.createGlyphVector(g2D.getFontRenderContext(), new char[]{chars[i]});
            charWidths[i] = (float) gv.getVisualBounds().getWidth();
            charHeights[i] = (float) gv.getVisualBounds().getHeight();
        }

        drawBefore(g2D);
        for (int i = 0; i < chars.length; i++) {
            g2D.setFont(getCurrentFont(chars, i, font));
            g2D.setColor(getCurrentColor(chars, i, color));
            float x = calculateX(chars, charWidths, charHeights, i, width, height);
            float y = calculateY(chars, charWidths, charHeights, i, width, height);
            g2D.drawString(String.valueOf(chars[i]), x, y);
        }
        drawBefore(g2D);
        return image;
    }

    protected void drawBefore(Graphics2D graphics2D) {
    }

    protected void drawAfter(Graphics2D graphics2D) {
    }

    /**
     * 初始化{@code Graphics2D}
     *
     * @param g2D {@code Graphics2D}
     */
    protected void initGraphics(Graphics2D g2D) {
        RenderingHints hints = new RenderingHints(null);
        initRenderingHints(hints);
        g2D.setRenderingHints(hints);
    }

    /**
     * 计算每一个字符的Y轴坐标
     *
     * @param chars       字符
     * @param charWidths  所有字符宽度
     * @param charHeights 所有字符高度
     * @param i           当前字符位置
     * @param width       总宽度
     * @param height      总高度
     * @return 返回当前字符的Y轴坐标
     */
    protected abstract float calculateY(
            char[] chars, float[] charWidths, float[] charHeights, int i, int width, int height);

    /**
     * 计算每一个字符的X轴坐标
     *
     * @param chars       字符
     * @param charWidths  所有字符宽度
     * @param charHeights 所有字符高度
     * @param i           当前字符位置
     * @param width       总宽度
     * @param height      总高度
     * @return 返回当前字符的X轴坐标
     */
    protected abstract float calculateX(
            char[] chars, float[] charWidths, float[] charHeights, int i, int width, int height);

    /**
     * 获取当前字符颜色
     *
     * @param chars        字符
     * @param i            当前字符位置
     * @param defaultColor 默认颜色
     * @return 返回颜色
     */
    protected Color getCurrentColor(char[] chars, int i, Color defaultColor) {
        return defaultColor;
    }

    /**
     * 获取当前字符字体
     *
     * @param chars       字符
     * @param i           当前字符位置
     * @param defaultFont 默认字体
     * @return 返回字体
     */
    protected Font getCurrentFont(char[] chars, int i, Font defaultFont) {
        return defaultFont;
    }

    protected int calculateTotalWidth(char[] chars, int i, float charWidth, int widthNeeded) {
        if (i > 0) {
            widthNeeded += 2;
        }
        widthNeeded += charWidth;
        return widthNeeded;
    }

    protected void initRenderingHints(RenderingHints hints) {
        hints.add(
                new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        hints.add(
                new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

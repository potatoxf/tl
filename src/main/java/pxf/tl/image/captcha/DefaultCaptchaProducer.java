package pxf.tl.image.captcha;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 默认验证码生成器
 *
 * @author potatoxf
 */
public class DefaultCaptchaProducer extends AbstractCaptchaProducer {
    /**
     * 是否画边框
     */
    private boolean isBorderDrawn = true;
    /**
     * 边框粗细
     */
    private int borderThickness;
    /**
     * 边框颜色
     */
    private Color borderColor;

    @Override
    protected BufferedImage decorate(BufferedImage image) throws IOException {
        image = super.decorate(image);
        Graphics2D graphics = image.createGraphics();
        if (isBorderDrawn) {
            drawBox(graphics);
        }
        return image;
    }

    private void drawBox(Graphics2D graphics) {
        int width = getWidth();
        int height = getHeight();
        graphics.setColor(borderColor);

        if (borderThickness != 1) {
            BasicStroke stroke = new BasicStroke((float) borderThickness);
            graphics.setStroke(stroke);
        }
        Line2D line1 = new Line2D.Double(0, 0, 0, width);
        graphics.draw(line1);
        Line2D line2 = new Line2D.Double(0, 0, width, 0);
        graphics.draw(line2);
        line2 = new Line2D.Double(0, height - 1, width, height - 1);
        graphics.draw(line2);
        line2 = new Line2D.Double(width - 1, height - 1, width - 1, 0);
        graphics.draw(line2);
    }

    public boolean isBorderDrawn() {
        return isBorderDrawn;
    }

    public void setBorderDrawn(boolean borderDrawn) {
        isBorderDrawn = borderDrawn;
    }

    public int getBorderThickness() {
        return borderThickness;
    }

    public void setBorderThickness(int borderThickness) {
        this.borderThickness = borderThickness;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }
}

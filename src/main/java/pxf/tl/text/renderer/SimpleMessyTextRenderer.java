package pxf.tl.text.renderer;


import pxf.tl.util.ToolArray;

import java.awt.*;

/**
 * @author potatoxf
 */
public class SimpleMessyTextRenderer extends AbstractMessyTextRenderer {
    private int fontSize = 16;
    private int spacing = 2;

    @Override
    protected float calculateY(
            char[] chars, float[] charWidths, float[] charHeights, int i, int width, int height) {
        return (height - fontSize) / 5f + fontSize;
    }

    @Override
    protected float calculateX(
            char[] chars, float[] charWidths, float[] charHeights, int i, int width, int height) {
        float r = (width - (ToolArray.sum(charWidths) + (chars.length - 1) * 2)) / 2;
        if (i != 0) {
            r += charWidths[i];
        }
        return r;
    }

    @Override
    protected void initGraphics(Graphics2D g2D) {
        super.initGraphics(g2D);
        g2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getSpacing() {
        return spacing;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }
}

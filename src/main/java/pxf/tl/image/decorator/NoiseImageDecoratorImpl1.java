package pxf.tl.image.decorator;


import pxf.tl.collection.map.Parametric;
import pxf.tl.util.ToolColor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author potatoxf
 */
public class NoiseImageDecoratorImpl1 extends AbstractNoiseImageDecorator {
    /**
     * 图片装饰
     *
     * @param bufferedImage 图片
     * @param parametric    参数
     * @return {@code BufferedImage}，返回装饰后的图像
     */
    @Override
    public BufferedImage decorate(BufferedImage bufferedImage, Parametric parametric) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Graphics2D graph = (Graphics2D) bufferedImage.getGraphics();
        graph.setRenderingHints(
                new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        graph.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        Random random = new Random();
        int noiseLineNum = random.nextInt(3);
        if (noiseLineNum == 0) {
            noiseLineNum = 1;
        }
        for (int i = 0; i < noiseLineNum; i++) {
            graph.setColor(ToolColor.randomBuiltColor());
            graph.drawLine(
                    random.nextInt(width),
                    random.nextInt(height),
                    10 + random.nextInt(20),
                    10 + random.nextInt(20));
        }

        graph.dispose();
        return bufferedImage;
    }
}

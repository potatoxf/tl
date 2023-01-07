package pxf.tl.image.decorator;


import pxf.tl.collection.map.Parametric;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Default implementation of , adds a gradient background to an image. The gradient color is
 * diagonal and made of Color From (top left) and Color To (bottom right).
 *
 * @author potatoxf
 */
public class ImageDecoratorForGradient implements ImageDecorator {
    private Color colorFrom;
    private Color colorTo;

    /**
     * an image with a gradient background added to the base image.
     *
     * @param bufferedImage 图片
     * @return {@code BufferedImage}，返回装饰后的图像
     */
    @Override
    public BufferedImage decorate(BufferedImage bufferedImage) {
        return decorate(bufferedImage, null);
    }

    /**
     * an image with a gradient background added to the base image.
     *
     * @param bufferedImage 图片
     * @param parametric    参数
     * @return {@code BufferedImage}，返回装饰后的图像
     */
    @Override
    public BufferedImage decorate(BufferedImage bufferedImage, Parametric parametric) {

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        // make an opaque image
        BufferedImage imageWithBackground =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graph = (Graphics2D) imageWithBackground.getGraphics();
        RenderingHints hints =
                new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        hints.add(
                new RenderingHints(
                        RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY));
        hints.add(
                new RenderingHints(
                        RenderingHints.KEY_ALPHA_INTERPOLATION,
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));

        hints.add(
                new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));

        graph.setRenderingHints(hints);

        GradientPaint paint = new GradientPaint(0, 0, colorFrom, width, height, colorTo);
        graph.setPaint(paint);
        graph.fill(new Rectangle2D.Double(0, 0, width, height));

        // draw the transparent image over the background
        graph.drawImage(bufferedImage, 0, 0, null);

        return imageWithBackground;
    }
}

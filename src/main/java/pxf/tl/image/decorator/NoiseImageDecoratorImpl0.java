package pxf.tl.image.decorator;


import pxf.tl.collection.map.Parametric;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author potatoxf
 */
public class NoiseImageDecoratorImpl0 extends AbstractNoiseImageDecorator {

    private Color color = Color.BLACK;

    /**
     * 图片装饰
     *
     * @param bufferedImage 图片
     * @param parametric    参数
     * @return {@code BufferedImage}，返回装饰后的图像
     */
    @Override
    public BufferedImage decorate(BufferedImage bufferedImage, Parametric parametric) {
        float factorOne = getFactorOne(parametric);
        float factorTwo = getFactorTwo(parametric);
        float factorThree = getFactorThree(parametric);
        float factorFour = getFactorFour(parametric);
        Color color = getColor(parametric);
        // image size
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        // the points where the line changes the stroke and direction
        Point2D[] pts = null;
        Random rand = new SecureRandom();

        // the curve from where the points are taken
        CubicCurve2D cc =
                new CubicCurve2D.Float(
                        width * factorOne,
                        height * rand.nextFloat(),
                        width * factorTwo,
                        height * rand.nextFloat(),
                        width * factorThree,
                        height * rand.nextFloat(),
                        width * factorFour,
                        height * rand.nextFloat());

        // creates an iterator to define the boundary of the flattened curve
        PathIterator pi = cc.getPathIterator(null, 2);
        Point2D[] tmp = new Point2D[200];
        int i = 0;

        // while pi is iterating the curve, adds points to tmp array
        while (!pi.isDone()) {
            float[] coords = new float[6];
            int v = pi.currentSegment(coords);
            if (v == PathIterator.SEG_MOVETO || v == PathIterator.SEG_LINETO) {
                tmp[i] = new Point2D.Float(coords[0], coords[1]);
            }
            i++;
            pi.next();
        }

        pts = new Point2D[i];
        System.arraycopy(tmp, 0, pts, 0, i);

        Graphics2D graph = (Graphics2D) bufferedImage.getGraphics();
        graph.setRenderingHints(
                new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        graph.setColor(color);

        // for the maximum 3 point change the stroke and direction
        for (i = 0; i < pts.length - 1; i++) {
            if (i < 3) {
                graph.setStroke(new BasicStroke(0.9f * (4 - i)));
            }
            graph.drawLine(
                    (int) pts[i].getX(),
                    (int) pts[i].getY(),
                    (int) pts[i + 1].getX(),
                    (int) pts[i + 1].getY());
        }

        graph.dispose();
        return bufferedImage;
    }

    public Color getColor(Parametric parametric) {
        if (parametric != null) {
            Object value = parametric.getObjectValue("color");
            if (value instanceof Color) {
                return (Color) value;
            }
        }
        return color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

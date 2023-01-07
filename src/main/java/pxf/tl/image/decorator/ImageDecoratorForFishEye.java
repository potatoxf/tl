package pxf.tl.image.decorator;


import pxf.tl.collection.map.Parametric;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * {@link ImageDecoratorForFishEye} adds fish eye effect with vertical and horizontal lines.
 *
 * @author potatoxf
 */
public class ImageDecoratorForFishEye implements ImageDecorator {
    /**
     * Applies distortion by adding fish eye effect and horizontal vertical lines.
     *
     * @param bufferedImage 图片
     * @return {@code BufferedImage}，返回装饰后的图像
     */
    @Override
    public BufferedImage decorate(BufferedImage bufferedImage) {
        return decorate(bufferedImage, null);
    }

    /**
     * Applies distortion by adding fish eye effect and horizontal vertical lines.
     *
     * @param bufferedImage 图片
     * @param parametric    参数
     * @return {@code BufferedImage}，返回装饰后的图像
     */
    @Override
    public BufferedImage decorate(BufferedImage bufferedImage, Parametric parametric) {

        Graphics2D graph = (Graphics2D) bufferedImage.getGraphics();
        int imageHeight = bufferedImage.getHeight();
        int imageWidth = bufferedImage.getWidth();

        // want lines put them in a variable so we might configure these later
        int horizontalLines = imageHeight / 7;
        int verticalLines = imageWidth / 7;

        // calculate space between lines
        int horizontalGaps = imageHeight / (horizontalLines + 1);
        int verticalGaps = imageWidth / (verticalLines + 1);

        // draw the horizontal stripes
        for (int i = horizontalGaps; i < imageHeight; i = i + horizontalGaps) {
            graph.setColor(Color.blue);
            graph.drawLine(0, i, imageWidth, i);
        }

        // draw the vertical stripes
        for (int i = verticalGaps; i < imageWidth; i = i + verticalGaps) {
            graph.setColor(Color.red);
            graph.drawLine(i, 0, i, imageHeight);
        }

        // make a pixel array of the original image.
        // we need this later to do the operations on..
        int[] pix = new int[imageHeight * imageWidth];
        int j = 0;

        for (int j1 = 0; j1 < imageWidth; j1++) {
            for (int k1 = 0; k1 < imageHeight; k1++) {
                pix[j] = bufferedImage.getRGB(j1, k1);
                j++;
            }
        }

        double distance = ranInt(imageWidth / 4, imageWidth / 3);

        // put the distortion in the (dead) middle
        int widthMiddle = bufferedImage.getWidth() / 2;
        int heightMiddle = bufferedImage.getHeight() / 2;

        // again iterate over all pixels..
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {

                int relX = x - widthMiddle;
                int relY = y - heightMiddle;

                double d1 = Math.sqrt(relX * relX + relY * relY);
                if (d1 < distance) {

                    int j2 =
                            widthMiddle
                                    + (int)
                                    (((fishEyeFormula(d1 / distance) * distance) / d1)
                                            * (double) (x - widthMiddle));
                    int k2 =
                            heightMiddle
                                    + (int)
                                    (((fishEyeFormula(d1 / distance) * distance) / d1)
                                            * (double) (y - heightMiddle));
                    bufferedImage.setRGB(x, y, pix[j2 * imageHeight + k2]);
                }
            }
        }

        return bufferedImage;
    }

    /**
     * @param i
     * @param j
     * @return
     */
    private int ranInt(int i, int j) {
        double d = Math.random();
        return (int) ((double) i + (double) ((j - i) + 1) * d);
    }

    /**
     * implementation of: g(s) = - (3/4)s3 + (3/2)s2 + (1/4)s, with s from 0 to 1
     *
     * @param s
     * @return
     */
    private double fishEyeFormula(double s) {
        if (s < 0.0D) {
            return 0.0D;
        }
        if (s > 1.0D) {
            return s;
        } else {
            return -0.75D * s * s * s + 1.5D * s * s + 0.25D * s;
        }
    }
}

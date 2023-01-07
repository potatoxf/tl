package pxf.tl.image.decorator;


import pxf.tl.lang.AbstractChain;

import java.awt.image.BufferedImage;

/**
 * 图像装饰器链
 *
 * @author potatoxf
 */
public class ImageDecoratorChain extends AbstractChain<ImageDecorator> {

    /**
     * 将下一个装饰器修饰到图像上
     *
     * @param image BufferedImage
     * @return BufferedImage
     */
    public BufferedImage doDecorate(BufferedImage image) {

        if (hasNext()) {
            ImageDecorator imageDecorator = next();
            if (imageDecorator != null) {
                image = imageDecorator.decorate(image);
            }
        }
        return image;
    }

    /**
     * 将装全部饰器修饰到图像上
     *
     * @param image BufferedImage
     * @return BufferedImage
     */
    public BufferedImage decorate(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("The image must be no null");
        }
        while (hasNext()) {
            ImageDecorator imageDecorator = next();
            if (imageDecorator == null) {
                continue;
            }
            image = imageDecorator.decorate(image);
        }
        return image;
    }
}

package pxf.tl.image.decorator;


import pxf.tl.collection.map.Parametric;

import java.awt.image.BufferedImage;

/**
 * 功能性接口，图像装饰器
 *
 * <p>修饰图像并将装饰后的图像返回
 *
 * @author potatoxf
 */
public interface ImageDecorator {

    /**
     * 图片装饰
     *
     * @param bufferedImage 图片
     * @return {@code BufferedImage}，返回装饰后的图像
     */
    BufferedImage decorate(BufferedImage bufferedImage);

    /**
     * 图片装饰
     *
     * @param bufferedImage 图片
     * @param parametric    参数
     * @return {@code BufferedImage}，返回装饰后的图像
     */
    BufferedImage decorate(BufferedImage bufferedImage, Parametric parametric);
}

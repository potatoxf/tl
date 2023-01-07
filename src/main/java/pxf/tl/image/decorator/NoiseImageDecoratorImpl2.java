package pxf.tl.image.decorator;


import pxf.tl.collection.map.Parametric;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author potatoxf
 */
public class NoiseImageDecoratorImpl2 extends AbstractNoiseImageDecorator {

    /**
     * 颜色
     */
    private Color color = Color.BLACK;
    /**
     * 线条数量
     */
    private int lineAmount = 50;

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
        int lineAmount = getLineAmount(parametric);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Graphics graphics = bufferedImage.createGraphics();
        // 随机产生16条灰色干扰线，使图像中的认证码不易识别
        graphics.setColor(color);
        // 创建一个随机数生成器类 用于随机产生干扰线
        Random random = new Random();
        int x1, y1, x2, y2;
        for (int i = 0; i < lineAmount; i++) {
            x1 = random.nextInt(width);
            y1 = random.nextInt(height);
            x2 = random.nextInt(12);
            y2 = random.nextInt(12);
            // 第一个参数：第一个点的x坐标；第二个参数：第一个点的y坐标；第三个参数：第二个点的x坐标；第四个参数：第二个点的y坐标；
            graphics.drawLine(x1, y1, x1 + x2, y1 + y2);
        }
        return bufferedImage;
    }

    protected Color getColor(Parametric parametric) {
        if (parametric != null) {
            Object value = parametric.getObjectValue("color");
            if (value instanceof Color) {
                return (Color) value;
            }
        }
        return color;
    }

    protected int getLineAmount(Parametric parametric) {
        if (parametric != null) {
            Integer value = parametric.getIntegerValue("lineAmount");
            if (value != null) {
                return value;
            }
        }
        return lineAmount;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getLineAmount() {
        return lineAmount;
    }

    public void setLineAmount(int lineAmount) {
        this.lineAmount = lineAmount;
    }
}

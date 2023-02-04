package pxf.tl.image.decorator;


import pxf.tl.collection.map.Parametric;
import pxf.tl.util.ToolColor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author potatoxf
 */
public class NoiseImageDecoratorImpl3 extends NoiseImageDecoratorImpl2 {

    /**
     * 干扰线颜色
     */
    private Color lineColor;

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
        int lineAmount = getLineAmount();
        Color lineColor = getLineColor();
        // image size
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Graphics graphics = bufferedImage.createGraphics();
        // 随机产生16条灰色干扰线，使图像中的认证码不易识别
        graphics.setColor(color);
        // 创建一个随机数生成器类 用于随机产生干扰线
        Random random = new Random();
        // 定义坐标
        int y1, y2;
        for (int i = 0; i < lineAmount; i++) {
            graphics.setColor(lineColor == null ? ToolColor.randomColor() : lineColor);
            // 重直方向随机起点
            y1 = random.nextInt(height);
            // 重直方向随机终点
            y2 = random.nextInt(height);
            // 第一个参数：第一个点的x坐标；第二个参数：第一个点的y坐标；第三个参数：第二个点的x坐标；第四个参数：第二个点的y坐标；
            graphics.drawLine(0, y1, width, y2);
        }
        return bufferedImage;
    }

    public Color getLineColor(Parametric parametric) {
        if (parametric != null) {
            Object value = parametric.getObjectValue("color");
            if (value instanceof Color) {
                return (Color) value;
            }
        }
        return lineColor;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }
}

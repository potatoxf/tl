package pxf.tl.text.renderer;


import pxf.tl.util.ToolColor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * @author potatoxf
 */
public class MessyTextRenderer implements TextRenderer {
    /**
     * 图片中干扰线的条数
     */
    private int interLineAmount = 4;
    /**
     * 每个字符的高低位置是否随机
     */
    private boolean randomPosition = true;
    /**
     * 是否画边框
     */
    private boolean hasBorder = false;
    /**
     * 边框颜色，若为null则表示采用随机颜色
     */
    private Color borderColor = null;
    /**
     * 图片颜色,若为null则表示采用随机颜色
     */
    private Color backgroundColor = Color.LIGHT_GRAY;
    /**
     * 字体颜色,若为null则表示采用随机颜色
     */
    private Color fontColor = null;
    /**
     * 干扰线颜色,若为null则表示采用随机颜色
     */
    private Color lineColor = null;

    /**
     * 渲染文本成图画
     *
     * @param text   文本
     * @param width  宽度
     * @param height 高度
     * @return {@code BufferedImage}
     */
    @Override
    public BufferedImage render(String text, int width, int height) throws IOException {
        // 创建 图片缓存对象
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取图形上下文
        Graphics graphics = bufferedImage.getGraphics();
        // 绘制背景图
        graphics.setColor(backgroundColor == null ? ToolColor.randomBuiltColor() : backgroundColor);
        // 填充一个矩形，第一个参数：要填充的矩形的起始x坐标；第二个参数：要填充的矩形的起始y坐标；第三个参数：要填充的矩形的宽度；第四个参数：要填充的矩形的高度；
        graphics.fillRect(0, 0, width, height);
        if (hasBorder) {
            // 画边框
            graphics.setColor(borderColor == null ? ToolColor.randomBuiltColor() : borderColor);
            graphics.drawRect(0, 0, width - 1, height - 1);
        }
        // 画干扰线
        SecureRandom random = new SecureRandom();
        if (interLineAmount > 0) {
            // 定义坐标
            int x1 = 0, y1, y2;
            for (int i = 0; i < interLineAmount; i++) {
                graphics.setColor(lineColor == null ? ToolColor.randomBuiltColor() : lineColor);
                // 重直方向随机起点
                y1 = random.nextInt(height);
                // 重直方向随机终点
                y2 = random.nextInt(height);
                // 第一个参数：第一个点的x坐标；第二个参数：第一个点的y坐标；第三个参数：第二个点的x坐标；第四个参数：第二个点的y坐标；
                graphics.drawLine(x1, y1, width, y2);
            }
        }
        int length = text.length();
        // 字体大小为图片高度的80%
        int fontSize = (int) (height * 0.8);
        int gup = (width - fontSize * length) / (length + 1);
        // 设置第一个字符x坐标
        int fontX = 2 * gup;
        // 设置第一个字符y坐标
        int fontY = fontSize;
        // 设定字体
        graphics.setFont(new Font("Default", Font.PLAIN, fontSize));
        double avgWith = width * 1d / text.length();
        // 写验证码字符
        for (int i = 0; i < length; i++) {
            graphics.setColor(fontColor == null ? ToolColor.randomBuiltColor() : fontColor);
            // 将验证码字符显示到图象中，画字符串，x坐标即字符串左边位置，y坐标是指baseline的y坐标，即字体所在矩形的左上角y坐标+ascent
            double pn = Math.random();
            if (randomPosition) {
                fontY = (int) ((Math.random() * 0.3 + 0.6) * height);
                if (pn > 0.5) {
                    fontX += (int) ((Math.random() * 0.8) * gup);
                } else {
                    fontX -= (int) ((Math.random() * 0.8) * gup);
                }
            }
            graphics.drawString(String.valueOf(text.charAt(i)), fontX, fontY);
            // 移动下一个字符的x坐标
            fontX += fontSize;
        }
        graphics.dispose();
        return bufferedImage;
    }

    public int getInterLineAmount() {
        return interLineAmount;
    }

    public void setInterLineAmount(int interLineAmount) {
        this.interLineAmount = interLineAmount;
    }

    public boolean isRandomPosition() {
        return randomPosition;
    }

    public void setRandomPosition(boolean randomPosition) {
        this.randomPosition = randomPosition;
    }

    public boolean isHasBorder() {
        return hasBorder;
    }

    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }
}

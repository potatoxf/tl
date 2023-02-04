package pxf.tl.image.decorator;


import pxf.tl.collection.map.Parametric;

import java.awt.image.BufferedImage;

/**
 * @author potatoxf
 */
public abstract class AbstractNoiseImageDecorator implements ImageDecorator {
    private float factorOne;
    private float factorTwo;
    private float factorThree;
    private float factorFour;

    /**
     * 图片装饰
     *
     * @param bufferedImage 图片
     * @return {@code BufferedImage}，返回装饰后的图像
     */
    @Override
    public BufferedImage decorate(BufferedImage bufferedImage) {
        return decorate(bufferedImage, null);
    }

    /**
     * 图片装饰
     *
     * @param bufferedImage 图片
     * @param parametric    参数
     * @return {@code BufferedImage}，返回装饰后的图像
     */
    @Override
    public BufferedImage decorate(BufferedImage bufferedImage, Parametric parametric) {
        return null;
    }

    protected float getFactorOne(Parametric parametric) {
        if (parametric != null) {
            return parametric.gainFloatValue("factorOne", factorOne);
        }
        return factorOne;
    }

    protected float getFactorTwo(Parametric parametric) {
        if (parametric != null) {
            return parametric.gainFloatValue("factorTwo", factorTwo);
        }
        return factorTwo;
    }

    protected float getFactorThree(Parametric parametric) {
        if (parametric != null) {
            return parametric.gainFloatValue("factorThree", factorThree);
        }
        return factorThree;
    }

    protected float getFactorFour(Parametric parametric) {
        if (parametric != null) {
            return parametric.gainFloatValue("factorFour", factorFour);
        }
        return factorFour;
    }

    public float getFactorOne() {
        return factorOne;
    }

    public void setFactorOne(float factorOne) {
        this.factorOne = factorOne;
    }

    public float getFactorTwo() {
        return factorTwo;
    }

    public void setFactorTwo(float factorTwo) {
        this.factorTwo = factorTwo;
    }

    public float getFactorThree() {
        return factorThree;
    }

    public void setFactorThree(float factorThree) {
        this.factorThree = factorThree;
    }

    public float getFactorFour() {
        return factorFour;
    }

    public void setFactorFour(float factorFour) {
        this.factorFour = factorFour;
    }
}

package pxf.tl.image;

import java.io.InputStream;

/**
 * 图片提供器
 *
 * @author potatoxf
 */
public interface ImageSupplier {
    /**
     * 获取图片数据
     *
     * @return 图片数据流
     */
    byte[] image();

    /**
     * 获取图片数据
     *
     * @return 图片数据流
     */
    InputStream imageInputStream();

    /**
     * 图片格式
     *
     * @return 图片格式
     */
    String format();
}

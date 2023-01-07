package pxf.tl.image;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author potatoxf
 */
public class CacheImageSupplier implements ImageSupplier {

    /**
     *
     */
    private byte[] data;
    /**
     *
     */
    private String format;

    /**
     *
     */
    public CacheImageSupplier() {
    }

    /**
     * @param data
     * @param format
     */
    public CacheImageSupplier(byte[] data, String format) {
        this.data = data;
        this.format = format;
    }

    /**
     * 获取图片数据
     *
     * @return 图片数据流
     */
    @Override
    public final synchronized byte[] image() {
        return data;
    }

    /**
     * 获取图片数据
     *
     * @return 图片数据流
     */
    @Override
    public final synchronized InputStream imageInputStream() {
        return new ByteArrayInputStream(data);
    }

    /**
     * 获取图片格式
     *
     * @return 图片格式
     */
    @Override
    public final synchronized String format() {
        return format;
    }

    /**
     * 设置图片
     *
     * @param data   图片数据
     * @param format 图片格式
     */
    public synchronized void setImage(byte[] data, String format) {
        if (data == null) {
            throw new IllegalArgumentException();
        }
        if (format == null) {
            throw new IllegalArgumentException();
        }
        this.data = data;
        this.format = format;
    }
}

package pxf.tl.image;


import pxf.tl.util.ToolIO;
import pxf.tl.util.ToolString;

import java.io.File;
import java.io.IOException;

/**
 * @author potatoxf
 */
public class FileImageSupplier extends CacheImageSupplier {

    /**
     *
     */
    private File imageFile;

    /**
     * @param imageFile
     * @throws IOException
     */
    public FileImageSupplier(File imageFile) throws IOException {
        setImageFile(imageFile);
    }

    /**
     * 设置图片文件
     *
     * @param imageFile 图片文件
     * @throws IOException 如果读取文件出现异常
     */
    public synchronized void setImageFile(File imageFile) throws IOException {
        setImage(
                ToolIO.readAllBytes(imageFile), ToolString.extractFileExtension(imageFile.getName()));
    }
}

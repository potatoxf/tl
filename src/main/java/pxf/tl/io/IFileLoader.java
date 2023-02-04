package pxf.tl.io;


import pxf.tl.util.ToolIO;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author potatoxf
 */
public interface IFileLoader {

    /**
     * 获取文件
     *
     * @param address 地址
     * @return {@code byte[]}
     * @throws IOException 当读取文件出现异常
     */
    default byte[] getFile(String address) throws IOException {
        try (InputStream inputStream = getFileInputStream(address)) {
            return ToolIO.readAllBytes(inputStream);
        }
    }

    /**
     * 获取文件流
     *
     * @param address 地址
     * @return {@code InputStream}
     * @throws IOException 当读取文件出现异常
     */
    InputStream getFileInputStream(String address) throws IOException;
}

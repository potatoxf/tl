package pxf.tl.io;


import pxf.tl.net.protocol.ProtocolHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 文件加载类,根据路径加载指定文件
 *
 * @author potatoxf
 */
public class DefaultFileLoader implements IFileLoader {

    static {
        ProtocolHelper.load();
    }

    /**
     * 获取文件流
     *
     * @param address 地址
     * @return {@code InputStream}
     * @throws IOException 当读取文件出现异常
     */
    @Override
    public InputStream getFileInputStream(String address) throws IOException {
        address = address.trim();

        InputStream inputStream = null;

        // 判断是否是网络地址
        if (address.indexOf(':') <= 5) {
            try {
                URL url = new URL(address);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(30 * 1000);
                urlConnection.setReadTimeout(60 * 1000);
                urlConnection.setDoInput(true);
                inputStream = urlConnection.getInputStream();
            } catch (IOException ignored) {
            }
        }

        if (inputStream == null) {
            // 先用绝对路径查询,再查询相对路径
            try {
                inputStream = new FileInputStream(address);
            } catch (FileNotFoundException e) {
                // 获取项目文件
                inputStream = ClassLoader.getSystemResourceAsStream(address);
            }
        }
        if (inputStream == null) {
            throw new IOException("Could not get stream from address [" + address + "]");
        }
        return inputStream;
    }
}

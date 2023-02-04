package pxf.tl.swing;


import pxf.tl.exception.IORuntimeException;
import pxf.tl.util.ToolURL;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * 桌面相关工具（平台相关）<br>
 * Desktop 类允许 Java 应用程序启动已在本机桌面上注册的关联应用程序，以处理 URI 或文件。
 *
 * @author potatoxf
 */
public class DesktopUtil {

    /**
     * 获得{@link Desktop}
     *
     * @return {@link Desktop}
     */
    public static Desktop getDesktop() {
        return Desktop.getDesktop();
    }

    /**
     * 使用平台默认浏览器打开指定URL地址
     *
     * @param url URL地址
     */
    public static void browse(String url) {
        browse(ToolURL.toURI(url));
    }

    /**
     * 使用平台默认浏览器打开指定URI地址
     *
     * @param uri URI地址
     */
    public static void browse(URI uri) {
        final Desktop desktop = getDesktop();
        try {
            desktop.browse(uri);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 启动关联应用程序来打开文件
     *
     * @param file URL地址
     */
    public static void open(File file) {
        final Desktop desktop = getDesktop();
        try {
            desktop.open(file);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 启动关联编辑器应用程序并打开用于编辑的文件
     *
     * @param file 文件
     */
    public static void edit(File file) {
        final Desktop desktop = getDesktop();
        try {
            desktop.edit(file);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 使用关联应用程序的打印命令, 用本机桌面打印设备来打印文件
     *
     * @param file 文件
     */
    public static void print(File file) {
        final Desktop desktop = getDesktop();
        try {
            desktop.print(file);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 使用平台默认浏览器打开指定URL地址
     *
     * @param mailAddress 邮件地址
     */
    public static void mail(String mailAddress) {
        final Desktop desktop = getDesktop();
        try {
            desktop.mail(ToolURL.toURI(mailAddress));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
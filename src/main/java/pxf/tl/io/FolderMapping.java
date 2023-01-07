package pxf.tl.io;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolCollection;
import pxf.tl.util.ToolIO;
import pxf.tl.util.ToolLog;
import pxf.tl.util.ToolString;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * 文件夹映射
 *
 * <p>映射类路径下的文件和外部路径下的文件 当外部路径不存在文件时，则从类路径下文件复制到外部路径下的文件夹
 *
 * @author potatoxf
 */
public class FolderMapping {
    private static final Logger LOGGER = LoggerFactory.getLogger(FolderMapping.class);
    /**
     * 根类路径
     */
    private final String rootClassPath;
    /**
     * 根文件路径
     */
    private final File rootFilePath;

    /**
     * @param rootClassPath 根类路径
     * @param rootFilePath  根文件路径
     */
    public FolderMapping(String rootClassPath, String rootFilePath) {
        this.rootClassPath = ToolString.clearPath(false, true, rootClassPath);
        this.rootFilePath = new File(rootFilePath);
    }

    public File getRootFilePath() {
        return rootFilePath;
    }

    public String getRootClassPath() {
        return rootClassPath;
    }

    /**
     * 是否存在资源
     *
     * @param resourcePath 资源路径
     * @return 存在返回true，否则返回false
     */
    public final boolean exists(String resourcePath) {
        String builtPath = getBuiltPath(resourcePath);
        try {
            return getResourceUrl(builtPath) != null;
        } catch (IOException e) {
            ToolLog.error(LOGGER, e, () -> "Unable to get resource from [%s]", builtPath);
        }
        return false;
    }

    /**
     * 初始化文件
     *
     * @param resourcePath 资源路径
     * @throws IOException 如果读写文件发生异常
     */
    public final File init(String resourcePath) throws IOException {
        File rootFilePath = getRootFilePath();
        if (!rootFilePath.exists()) {
            rootFilePath.mkdir();
        }
        File file = new File(rootFilePath, resourcePath);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            parentFile.mkdirs();
            if (!parentFile.isDirectory()) {
                throw new FileNotFoundException(
                        "The directory [" + parentFile + "] not found on [" + rootFilePath + "]");
            }
            ToolIO.write(file, getBuiltInputStream(resourcePath));
            if (!file.exists()) {
                throw new FileNotFoundException(
                        "The file [" + file + "] not found on [" + rootFilePath + "]");
            }
        }
        return file;
    }

    /**
     * @param resourcePath 资源路径
     * @return 返回外部资源
     * @throws IOException 如果读写文件发生异常
     */
    public final File getOuterFile(String resourcePath) throws IOException {
        return init(resourcePath);
    }

    /**
     * 获取内置路径
     *
     * @param resourcePath 资源路径
     * @return 内置路径
     */
    public final String getBuiltPath(String resourcePath) {
        return getRootClassPath() + resourcePath;
    }

    /**
     * 读取内置文件流
     *
     * @param resourcePath 资源路径
     * @throws IOException 如果读写文件发生异常
     */
    public final InputStream getBuiltInputStream(String resourcePath) throws IOException {
        String builtPath = getBuiltPath(resourcePath);
        URL resourceUrl = getResourceUrl(builtPath);
        InputStream inputStream = resourceUrl.openStream();
        if (inputStream == null) {
            throw new FileNotFoundException("The file [" + builtPath + "]not found on classpath");
        } else {
            return inputStream;
        }
    }

    /**
     * 读取文件输入流，如果外部流不存在则从内置类复制过去
     *
     * @param resourcePath 资源路径
     * @return {@code InputStream}
     * @throws IOException 如果读写文件发生异常
     */
    public InputStream getInputStream(String resourcePath) throws IOException {
        return new FileInputStream(getOuterFile(resourcePath));
    }

    /**
     * 获取资源URL，如果获取到多个则取第一个
     *
     * @param builtPath 内置路径
     * @return {@code URL}
     * @throws IOException 如果获取URL发生异常
     */
    private URL getResourceUrl(String builtPath) throws IOException {
        List<URL> list = new LinkedList<URL>();
        try {
            ToolCollection.addAll(list, ClassLoader.getSystemResources(builtPath));
        } catch (IOException e) {
            throw new IOException("Unable to get resource from [" + builtPath + "]", e);
        }
        if (Whether.empty(list)) {
            throw new FileNotFoundException("The file [" + builtPath + "]not found on classpath");
        } else {
            if (list.size() != 1) {
                ToolLog.warn(LOGGER, () -> "Get multiple resources from [%s] for %s", builtPath, list);
                ToolLog.warn(LOGGER, () -> "Only get the first resource[%s]", list.get(0));
            }
            return list.get(0);
        }
    }
}

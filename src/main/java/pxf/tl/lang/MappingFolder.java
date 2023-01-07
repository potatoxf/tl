package pxf.tl.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolCollection;
import pxf.tl.util.ToolIO;

import javax.annotation.Nonnull;
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
public class MappingFolder {
    private static final Logger LOG = LoggerFactory.getLogger(MappingFolder.class);
    /**
     * 根类路径
     */
    private final String builtRootDirPath;
    /**
     * 根文件路径
     */
    private final String outerRootDirPath;

    /**
     * @param builtRootDirPath 根类路径
     * @param outerRootDirPath 根文件路径
     */
    public MappingFolder(String builtRootDirPath, String outerRootDirPath) {
        this.builtRootDirPath = Safe.formatPath(false, true, builtRootDirPath);
        this.outerRootDirPath = Safe.formatPath(false, true, outerRootDirPath);
    }

    /**
     * 是否存在资源
     *
     * @param resourcePath 资源路径
     * @return 存在返回true，否则返回false
     */
    public final boolean exists(String resourcePath) {
        try {
            getResourceUrl(resourcePath);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 获取外部路径
     *
     * @param resourcePath 资源路径
     * @return 返回外部资源
     * @throws IOException 如果读写文件发生异常
     */
    @Nonnull
    public final String getOuterPath(String resourcePath) {
        return getOuterRootDirPath() + resourcePath;
    }

    /**
     * 获取内置路径
     *
     * @param resourcePath 资源路径
     * @return 内置路径
     */
    @Nonnull
    public final String getBuiltPath(String resourcePath) {
        return getBuiltRootDirPath() + resourcePath;
    }

    /**
     * @return
     */
    public final String getOuterRootDirPath() {
        return outerRootDirPath;
    }

    /**
     * @return
     */
    public final String getBuiltRootDirPath() {
        return builtRootDirPath;
    }

    /**
     * @param resourcePath 资源路径
     * @return 返回外部资源
     * @throws IOException 如果读写文件发生异常
     */
    @Nonnull
    public final File getOuterFile(String resourcePath) throws IOException {
        File rootFilePath = new File(getOuterRootDirPath());
        File file = new File(rootFilePath, resourcePath);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            boolean createDir = parentFile.mkdirs();
            if (!parentFile.isDirectory()) {
                if (!createDir) {
                    throw new IOException("Error to create the directory [" + parentFile + "]");
                }
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
     * 读取内置文件流
     *
     * @param resourcePath 资源路径
     * @throws IOException 如果读写文件发生异常
     */
    @Nonnull
    public final InputStream getBuiltInputStream(String resourcePath) throws IOException {
        URL resourceUrl = getResourceUrl(resourcePath);
        InputStream inputStream = resourceUrl.openStream();
        if (inputStream == null) {
            throw new FileNotFoundException(
                    "The file [" + resourcePath + "] not found on classpath [" + builtRootDirPath + "]");
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
    @Nonnull
    public InputStream getInputStream(String resourcePath) throws IOException {
        return new FileInputStream(getOuterFile(resourcePath));
    }

    /**
     * 获取资源URL，如果获取到多个则取第一个
     *
     * @param resourcePath 资源路径
     * @return {@code URL}
     * @throws IOException 如果获取URL发生异常
     */
    @Nonnull
    private URL getResourceUrl(String resourcePath) throws IOException {
        String builtPath = getBuiltPath(resourcePath);
        List<URL> list = new LinkedList<>();
        try {
            ToolCollection.addAll(list, ClassLoader.getSystemResources(builtPath));
        } catch (IOException e) {
            throw new IOException("Unable to get resource from [" + builtPath + "]", e);
        }
        if (Whether.empty(list)) {
            throw new FileNotFoundException("The file [" + builtPath + "]not found on classpath");
        } else {
            if (list.size() != 1 && LOG.isWarnEnabled()) {
                LOG.warn("Get multiple resources from [" + builtPath + "] for " + list);
                LOG.warn("Only get the first resource[" + list.get(0) + "]");
            }
            return list.get(0);
        }
    }

    /**
     * @param resourceName
     * @return
     */
    public boolean isChange(String resourceName) throws IOException {
        return true;
    }
}

package pxf.tl.io;


import pxf.tl.util.ToolIO;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 带有缓存文件夹映射
 *
 * <p>映射类路径下的文件和外部路径下的文件 当外部路径不存在文件时，则从类路径下文件复制到外部路径下的文件夹
 *
 * @author potatoxf
 */
public class CachedFolderMapping extends FolderMapping {

    private static final CacheBlock EMPTY = new CacheBlock(null, 0);
    private final Map<String, CacheBlock> cache = new ConcurrentHashMap<String, CacheBlock>();
    private final int maxCacheSize;

    /**
     * @param rootClassPath 根类路径
     * @param rootFilePath  根文件路径
     */
    public CachedFolderMapping(String rootClassPath, String rootFilePath) {
        this(rootClassPath, rootFilePath, 1024 * 4);
    }

    /**
     * @param rootClassPath 根类路径
     * @param rootFilePath  根文件路径
     * @param maxCacheSize  最大缓存大小
     */
    public CachedFolderMapping(String rootClassPath, String rootFilePath, int maxCacheSize) {
        super(rootClassPath, rootFilePath);
        this.maxCacheSize = maxCacheSize;
    }

    /**
     * 读取文件输入流，如果外部流不存在则从内置类复制过去
     *
     * @param resourcePath 资源路径
     * @return {@code InputStream}
     * @throws IOException 如果读写文件发生异常
     */
    @Override
    public InputStream getInputStream(String resourcePath) throws IOException {
        byte[] data;
        File outerFile = getOuterFile(resourcePath);
        CacheBlock cacheBlock = cache.get(resourcePath);
        if (cacheBlock == null) {
            cache.put(resourcePath, EMPTY);
            data = cached(resourcePath, outerFile);
        } else {
            if (outerFile.lastModified() == cacheBlock.time) {
                data = cacheBlock.data;
            } else {
                data = cached(resourcePath, outerFile);
            }
        }
        return new ByteArrayInputStream(data);
    }

    private byte[] cached(String resourcePath, File outerFile) throws IOException {
        byte[] data = ToolIO.readAllBytes(outerFile);
        if (outerFile.length() < maxCacheSize) {
            cache.put(resourcePath, new CacheBlock(data, outerFile.lastModified()));
        } else {
            cache.remove(resourcePath);
        }
        return data;
    }

    private static class CacheBlock {
        final byte[] data;
        final long time;

        CacheBlock(byte[] data, long time) {
            this.data = data;
            this.time = time;
        }
    }
}

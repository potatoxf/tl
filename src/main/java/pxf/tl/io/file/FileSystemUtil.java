package pxf.tl.io.file;


import pxf.tl.api.Charsets;
import pxf.tl.api.PoolOfString;
import pxf.tl.collection.map.MapUtil;
import pxf.tl.exception.IORuntimeException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * {@link FileSystem}相关工具类封装<br>
 * 参考：https://blog.csdn.net/j16421881/article/details/78858690
 *
 * @author potatoxf
 */
public class FileSystemUtil {

    /**
     * 创建 {@link FileSystem}
     *
     * @param path 文件路径，可以是目录或Zip文件等
     * @return {@link FileSystem}
     */
    public static FileSystem create(String path) {
        try {
            return FileSystems.newFileSystem(Paths.get(path).toUri(), MapUtil.of("create", "true"));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 创建 Zip的{@link FileSystem}，默认UTF-8编码
     *
     * @param path 文件路径，可以是目录或Zip文件等
     * @return {@link FileSystem}
     */
    public static FileSystem createZip(String path) {
        return createZip(path, null);
    }

    /**
     * 创建 Zip的{@link FileSystem}
     *
     * @param path     文件路径，可以是目录或Zip文件等
     * @param charsets 编码
     * @return {@link FileSystem}
     */
    public static FileSystem createZip(String path, Charsets charsets) {
        if (null == charsets) {
            charsets = Charsets.UTF_8;
        }
        final HashMap<String, String> env = new HashMap<>();
        env.put("create", "true");
        env.put("encoding", charsets.name());

        try {
            return FileSystems.newFileSystem(URI.create("jar:" + Paths.get(path).toUri()), env);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 获取目录的根路径，或Zip文件中的根路径
     *
     * @param fileSystem {@link FileSystem}
     * @return 根 {@link Path}
     */
    public static Path getRoot(FileSystem fileSystem) {
        return fileSystem.getPath(PoolOfString.SLASH);
    }
}

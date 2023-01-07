package pxf.tl.compress;


import pxf.tl.api.Charsets;
import pxf.tl.api.PoolOfString;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.help.New;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.io.FileUtil;
import pxf.tl.io.resource.Resource;
import pxf.tl.util.ToolIO;
import pxf.tl.util.ToolString;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zip生成封装
 *
 * @author potatoxf
 */
public class ZipWriter implements Closeable {

    private final ZipOutputStream out;

    /**
     * 构造
     *
     * @param zipFile  生成的Zip文件
     * @param charsets 编码
     */
    public ZipWriter(File zipFile, Charsets charsets) {
        this.out = getZipOutputStream(zipFile, charsets);
    }

    /**
     * 构造
     *
     * @param out      {@link ZipOutputStream}
     * @param charsets 编码
     */
    public ZipWriter(OutputStream out, Charsets charsets) {
        this.out = New.zipOutputStream(out, charsets);
    }

    /**
     * 构造
     *
     * @param out {@link ZipOutputStream}
     */
    public ZipWriter(ZipOutputStream out) {
        this.out = out;
    }

    /**
     * 创建ZipWriter
     *
     * @param zipFile  生成的Zip文件
     * @param charsets 编码
     * @return ZipWriter
     */
    public static ZipWriter of(File zipFile, Charsets charsets) {
        return new ZipWriter(zipFile, charsets);
    }

    /**
     * 创建ZipWriter
     *
     * @param out      Zip输出的流，一般为输出文件流
     * @param charsets 编码
     * @return ZipWriter
     */
    public static ZipWriter of(OutputStream out, Charsets charsets) {
        return new ZipWriter(out, charsets);
    }

    /**
     * 获得 {@link ZipOutputStream}
     *
     * @param zipFile  压缩文件
     * @param charsets 编码
     * @return {@link ZipOutputStream}
     */
    private static ZipOutputStream getZipOutputStream(File zipFile, Charsets charsets) {
        return New.zipOutputStream(New.fileOutputStream(zipFile), charsets);
    }

    /**
     * 设置压缩级别，可选1~9，-1表示默认
     *
     * @param level 压缩级别
     * @return this
     */
    public ZipWriter setLevel(int level) {
        this.out.setLevel(level);
        return this;
    }

    /**
     * 设置注释
     *
     * @param comment 注释
     * @return this
     */
    public ZipWriter setComment(String comment) {
        this.out.setComment(comment);
        return this;
    }

    /**
     * 获取原始的{@link ZipOutputStream}
     *
     * @return {@link ZipOutputStream}
     */
    public ZipOutputStream getOut() {
        return this.out;
    }

    /**
     * 对文件或文件目录进行压缩
     *
     * @param withSrcDir 是否包含被打包目录，只针对压缩目录有效。若为false，则只压缩目录下的文件或目录，为true则将本目录也压缩
     * @param filter     文件过滤器，通过实现此接口，自定义要过滤的文件（过滤掉哪些文件或文件夹不加入压缩），{@code null}表示不过滤
     * @param files      要压缩的源文件或目录。如果压缩一个文件，则为该文件的全路径；如果压缩一个目录，则为该目录的顶层目录路径
     * @return this
     * @throws IORuntimeException IO异常
     */
    public ZipWriter add(boolean withSrcDir, FileFilter filter, File... files)
            throws IORuntimeException {
        for (File file : files) {
            // 如果只是压缩一个文件，则需要截取该文件的父目录
            String srcRootDir;
            try {
                srcRootDir = file.getCanonicalPath();
                if ((false == file.isDirectory()) || withSrcDir) {
                    // 若是文件，则将父目录完整路径都截取掉；若设置包含目录，则将上级目录全部截取掉，保留本目录名
                    srcRootDir = file.getCanonicalFile().getParentFile().getCanonicalPath();
                }
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }

            _add(file, srcRootDir, filter);
        }
        return this;
    }

    /**
     * 添加资源到压缩包，添加后关闭资源流
     *
     * @param resources 需要压缩的资源，资源的路径为{@link Resource#getName()}
     * @return this
     * @throws IORuntimeException IO异常
     */
    public ZipWriter add(Resource... resources) throws IORuntimeException {
        for (Resource resource : resources) {
            if (null != resource) {
                add(resource.getName(), resource.getStream());
            }
        }
        return this;
    }

    /**
     * 添加文件流到压缩包，添加后关闭输入文件流<br>
     * 如果输入流为{@code null}，则只创建空目录
     *
     * @param path 压缩的路径, {@code null}和""表示根目录下
     * @param in   需要压缩的输入流，使用完后自动关闭，{@code null}表示加入空目录
     * @return this
     * @throws IORuntimeException IO异常
     */
    public ZipWriter add(String path, InputStream in) throws IORuntimeException {
        path = Safe.value(path);
        if (null == in) {
            // 空目录需要检查路径规范性，目录以"/"结尾
            path = ToolString.addSuffixIfNot(path, PoolOfString.SLASH);
            if (Whether.blank(path)) {
                return this;
            }
        }

        return putEntry(path, in);
    }

    /**
     * 对流中的数据加入到压缩文件<br>
     * 路径列表和流列表长度必须一致
     *
     * @param paths 流数据在压缩文件中的路径或文件名
     * @param ins   要压缩的源，添加完成后自动关闭流
     * @return 压缩文件
     * @throws IORuntimeException IO异常
     */
    public ZipWriter add(String[] paths, InputStream[] ins) throws IORuntimeException {
        if (Whether.empty(paths) || Whether.empty(ins)) {
            throw new IllegalArgumentException("Paths or ins is empty !");
        }
        if (paths.length != ins.length) {
            throw new IllegalArgumentException("Paths length is not equals to ins length !");
        }

        for (int i = 0; i < paths.length; i++) {
            add(paths[i], ins[i]);
        }

        return this;
    }

    @Override
    public void close() throws IORuntimeException {
        try {
            out.finish();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            ToolIO.closes(this.out);
        }
    }

    /**
     * 递归压缩文件夹或压缩文件<br>
     * srcRootDir决定了路径截取的位置，例如：<br>
     * file的路径为d:/a/b/c/d.txt，srcRootDir为d:/a/b，则压缩后的文件与目录为结构为c/d.txt
     *
     * @param srcRootDir 被压缩的文件夹根目录
     * @param file       当前递归压缩的文件或目录对象
     * @param filter     文件过滤器，通过实现此接口，自定义要过滤的文件（过滤掉哪些文件或文件夹不加入压缩），{@code null}表示不过滤
     * @throws IORuntimeException IO异常
     */
    private ZipWriter _add(File file, String srcRootDir, FileFilter filter)
            throws IORuntimeException {
        if (null == file || (null != filter && false == filter.accept(file))) {
            return this;
        }

        // 获取文件相对于压缩文件夹根目录的子路径
        final String subPath = FileUtil.subPath(srcRootDir, file);
        if (file.isDirectory()) {
            // 如果是目录，则压缩压缩目录中的文件或子目录
            final File[] files = file.listFiles();
            if (Whether.empty(files)) {
                // 加入目录，只有空目录时才加入目录，非空时会在创建文件时自动添加父级目录
                add(subPath, null);
            } else {
                // 压缩目录下的子文件或目录
                for (File childFile : files) {
                    _add(childFile, srcRootDir, filter);
                }
            }
        } else {
            // 如果是文件或其它符号，则直接压缩该文件
            putEntry(subPath, FileUtil.getInputStream(file));
        }
        return this;
    }

    /**
     * 添加文件流到压缩包，添加后关闭输入文件流<br>
     * 如果输入流为{@code null}，则只创建空目录
     *
     * @param path 压缩的路径, {@code null}和""表示根目录下
     * @param in   需要压缩的输入流，使用完后自动关闭，{@code null}表示加入空目录
     * @throws IORuntimeException IO异常
     */
    private ZipWriter putEntry(String path, InputStream in) throws IORuntimeException {
        try {
            out.putNextEntry(new ZipEntry(path));
            if (null != in) {
                ToolIO.copy(in, out);
            }
            out.closeEntry();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            ToolIO.closes(in);
        }

        ToolIO.flush(this.out);
        return this;
    }
}

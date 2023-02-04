package pxf.tl.util;


import pxf.tl.api.HandleResults;
import pxf.tl.api.JavaEnvironment;
import pxf.tl.exception.IODetailException;
import pxf.tl.function.BiFunctionThrow;
import pxf.tl.function.FunctionThrow;
import pxf.tl.function.VaryConsumerThrow;
import pxf.tl.function.VaryFunctionThrow;
import pxf.tl.help.New;
import pxf.tl.help.Whether;
import pxf.tl.iter.LineIter;
import pxf.tl.lang.SnowflakeWeakly;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author potatoxf
 */
public final class ToolFile {
    public static final Set<String> COMMON_IMAGE_FORMATS =
            ToolCollection.ofSet("png", "jpg", "jpeg", "gif", "bmp");

    /**
     * @param dir1
     * @param dir2
     * @param fileFilter
     * @param targetExtractor
     * @param targetComparator
     * @param targetCondition
     * @param <T>
     * @return
     */
    public static <T> List<T>[] compareListByCondition(
            final String dir1,
            final String dir2,
            final Predicate<File> fileFilter,
            final Function<File, String> mapper,
            final Function<String, T> targetExtractor,
            final Comparator<T> targetComparator,
            final BiPredicate<T, T> targetCondition) {
        Stream<File> stream1 = Arrays.stream(new File(dir1).listFiles());
        if (fileFilter != null) {
            stream1 = stream1.filter(fileFilter);
        }
        List<String> files1 = stream1.map(mapper).collect(Collectors.toList());

        Stream<File> stream2 = Arrays.stream(new File(dir2).listFiles());
        if (fileFilter != null) {
            stream2 = stream2.filter(fileFilter);
        }
        List<String> files2 = stream2.map(mapper).collect(Collectors.toList());

        return ToolCollection.compareListByCondition(
                files1, files2, targetExtractor, targetComparator, targetCondition);
    }

    /**
     * 列出目录下文件后缀
     *
     * @param dirFile  目录
     * @param prefixes 前缀
     * @return 返回列出的文件实例列表List<File>
     */
    public static List<File> listFilesByPrefix(final File dirFile, final String... prefixes) {
        return listFiles(
                dirFile,
                pathname -> {
                    for (String suffix : prefixes) {
                        if (suffix == null || pathname.getName().startsWith(suffix)) {
                            return true;
                        }
                    }
                    return false;
                });
    }

    /**
     * 列出目录下文件后缀
     *
     * @param dirFile  目录
     * @param suffixes 后缀
     * @return 返回列出的文件实例列表List<File>
     */
    public static List<File> listFilesBySuffix(final File dirFile, final String... suffixes) {
        return listFiles(
                dirFile,
                pathname -> {
                    for (String suffix : suffixes) {
                        if (suffix == null || pathname.getName().endsWith(suffix)) {
                            return true;
                        }
                    }
                    return false;
                });
    }

    /**
     * 列出目录下文件后缀
     *
     * @param dirFile 目录
     * @param regexp  正则表达式
     * @return 返回列出的文件实例列表List<File>
     */
    public static List<File> listFilesByRegexp(final File dirFile, final String regexp) {
        return listFilesByRegexp(dirFile, Pattern.compile(regexp));
    }

    /**
     * 列出目录下文件后缀
     *
     * @param dirFile 目录
     * @param regexp  正则表达式
     * @return 返回列出的文件实例列表List<File>
     */
    public static List<File> listFilesByRegexp(final File dirFile, final Pattern regexp) {
        return listFiles(
                dirFile,
                pathname -> {
                    if (regexp != null) {
                        return regexp.matcher(pathname.getPath()).matches();
                    }
                    return false;
                });
    }

    /**
     * 列出目录下文件
     *
     * @param dirFile 目录
     * @return 返回列出的文件实例列表List<File>
     */
    public static List<File> listFiles(final File dirFile) {
        return listFiles(dirFile, pathname -> true);
    }

    /**
     * 列出目录下文件
     *
     * @param dirFile    目录
     * @param fileFilter 文件过滤器
     * @return 返回列出的文件实例列表List<File>
     */
    public static List<File> listFiles(final File dirFile, final FileFilter fileFilter) {
        if (!Whether.directory(dirFile)) {
            return Collections.emptyList();
        }
        List<File> container = new ArrayList<File>();
        Queue<File> fileQueue = new LinkedList<File>();
        fileQueue.add(dirFile);
        File action;
        while (!Whether.empty(fileQueue)) {
            action = fileQueue.poll();
            if (action.isDirectory()) {
                File[] files = action.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            fileQueue.add(file);
                        } else {
                            if (fileFilter.accept(file)) {
                                container.add(file);
                            }
                        }
                    }
                }
            } else {
                if (fileFilter.accept(action)) {
                    container.add(action);
                }
            }
        }
        return container;
    }

    /**
     * 检查目录，没有目录则创建目录
     *
     * @param directory 目录
     */
    public static void checkDirectory(File directory) {
        if (!directory.isDirectory()) {
            createDirectory(directory);
        }
    }

    /**
     * 检查文件，没有目录则创建文件
     *
     * @param file 文件
     */
    public static void checkParentDirectory(File file) {
        File parentFile = file.getParentFile();
        if (!parentFile.isDirectory()) {
            createDirectory(parentFile);
        }
    }

    /**
     * 创建文件
     *
     * @param filePath 文件路径
     * @return 返回 {@code File}
     */
    public static File createFile(final String filePath) throws IODetailException {
        if (filePath == null) {
            throw new IllegalArgumentException("The file path must be no null");
        }
        return createFile(new File(filePath));
    }

    /**
     * 创建文件
     *
     * @param file 文件路径
     * @return 返回 {@code File}
     */
    public static File createFile(final File file) throws IODetailException {
        if (file == null) {
            throw new IllegalArgumentException("The file must be no null");
        }
        if (file.exists() && file.isFile()) {
            return file;
        }
        createDirectory(file.getParentFile());
        boolean newFile;
        try {
            newFile = file.createNewFile();
        } catch (IOException e) {
            throw new IODetailException("Failure to make file", e, file.getPath());
        }
        if (!newFile) {
            throw new IODetailException("Failure to make file", file.getPath());
        }
        return file;
    }

    /**
     * 创建文件
     *
     * @param filePaths 文件路径
     * @return 返回 {@code BatchHandleResult<File>}
     */
    public static HandleResults<File> createFiles(final String... filePaths) {
        return new HandleResults<File>()
                .loopExecute(
                        Arrays.asList(filePaths),
                        (VaryFunctionThrow<File, Throwable>) args -> createFile((String) args[0]));
    }

    /**
     * 创建目录
     *
     * @param filePath 文件路径
     * @return 返回 {@code File}
     */
    public static File createDirectory(final String filePath) throws IODetailException {
        if (filePath == null) {
            throw new IllegalArgumentException("The file path must be no null");
        }
        return createDirectory(new File(filePath));
    }

    /**
     * 创建目录
     *
     * @param file 文件路径
     * @return 返回 {@code File}
     */
    public static File createDirectory(final File file) throws IODetailException {
        if (file == null) {
            throw new IllegalArgumentException("The file must be no null");
        }
        if (file.exists() && file.isDirectory()) {
            return file;
        }
        if (!file.mkdirs()) {
            throw new IODetailException("Failure to make directory", file.getPath());
        }
        return file;
    }

    /**
     * 创建目录
     *
     * @param filePaths 文件路径
     * @return 返回 {@code BatchHandleResult<File>}
     */
    public static HandleResults<File> createDirectories(final String... filePaths) {
        return new HandleResults<File>()
                .loopExecute(
                        Arrays.asList(filePaths),
                        (VaryFunctionThrow<File, Throwable>) args -> createDirectory((String) args[0]));
    }

    /**
     * 创建临时目录
     *
     * @return 返回 {@code File}
     */
    public static File createTemporaryDirectory() throws IOException {
        String tempDir;
        if (Whether.linuxSystem()) {
            tempDir = "/tmp";
        } else {
            Map<String, String> map = System.getenv();
            tempDir = map.get("TEMP");
            if (tempDir != null) {
                tempDir = tempDir.trim();
            }
        }
        if (tempDir == null) {
            tempDir = JavaEnvironment.JAVA_IO_TMPDIR;
        }
        return createDirectory(new File(tempDir));
    }

    /**
     * 创建临时文件
     *
     * @param suffix 文件扩展
     * @return 返回 {@code File}
     */
    public static File createTemporaryFile(String suffix) throws IOException {
        if (suffix == null || suffix.length() == 0) {
            suffix = "dat";
        } else {
            String extension = ToolString.extractFileExtension(suffix);
            if (extension.length() != 0) {
                suffix = extension;
            }
        }
        return File.createTempFile(
                createTemporaryDirectory().getAbsolutePath(),
                "TMP" + SnowflakeWeakly.INSTANCE.nextId() + "." + suffix);
    }

    /**
     * 删除所有文件
     *
     * @param filePath 文件路径
     */
    public static void removeFileOrDirectory(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("The file path must be no null");
        }
        removeFileOrDirectory(new File(filePath));
    }

    /**
     * 删除所有文件
     *
     * @param file 文件
     */
    public static void removeFileOrDirectory(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("The file must be no null");
        }
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            if (!file.delete()) {
                throw new IOException("Failed to delete file" + file);
            }
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File subFile : files) {
                if (subFile.isFile()) {
                    subFile.delete();
                } else if (subFile.isDirectory()) {
                    removeFileOrDirectory(subFile);
                }
            }
        }
    }

    /**
     * 删除所有文件
     *
     * @param files 文件
     */
    public static void removeFileOrDirectory(Collection<File> files) throws IOException {
        for (File file : files) {
            removeFileOrDirectory(file);
        }
    }

    /**
     * 移动文件
     *
     * @param srcPath  源文件
     * @param destPath 目标文件
     */
    public static void move(String srcPath, String destPath) throws IODetailException {
        move(new File(srcPath), new File(destPath));
    }

    /**
     * 移动文件
     *
     * @param src  源文件
     * @param dest 目标文件
     */
    public static void move(File src, File dest) throws IODetailException {
        if (!src.exists()) {
            throw new IODetailException("The src file don't exist", src.getPath());
        }
        if (src.equals(dest)) {
            return;
        }
        if (dest.exists()) {
            throw new IODetailException("The dest file already exist", dest.getPath());
        }
        if (src.getParentFile().equals(dest.getParentFile())) {
            if (!src.renameTo(dest)) {
                throw new IODetailException("Failure to rename", src.getPath(), dest.getPath());
            }
        } else {
            throw new IODetailException(
                    "The src is renamed to dest that no in the same directory",
                    src.getPath(),
                    dest.getPath());
        }
    }

    /**
     * 批量移动文件
     *
     * @param destDirectory 目标目录
     * @param filePaths     文件路径
     * @return {@code BatchHandleResult<Boolean>}
     */
    public static HandleResults<Boolean> move(String destDirectory, String... filePaths) {
        File directory = createDirectory(destDirectory);
        return new HandleResults<Boolean>()
                .loopExecute(
                        filePaths,
                        (VaryConsumerThrow<Throwable>)
                                args -> {
                                    File src = new File(args[0].toString());
                                    File dest = new File(directory, src.getName());
                                    if (dest.exists()) {
                                        throw new IODetailException("", dest.getPath());
                                    }
                                    move(src, dest);
                                });
    }

    /**
     * 重命名文件
     *
     * @param srcPath 源文件
     * @param newName 目标文件
     */
    public static void rename(String srcPath, String newName) throws IODetailException {
        move(srcPath, Paths.get(srcPath).resolve(newName).toString());
    }

    /**
     * 重命名文件
     *
     * @param src     源文件
     * @param newName 新名称
     */
    public static void rename(File src, String newName) throws IODetailException {
        move(src, new File(src.getParentFile(), newName));
    }

    /**
     * 批量重命名
     *
     * @param nameHandler 名字处理器
     * @param filePaths   文件路径
     */
    public static HandleResults<Boolean> rename(
            final FunctionThrow<String, String, RuntimeException> nameHandler, String... filePaths) {
        return new HandleResults<Boolean>()
                .loopExecute(
                        filePaths,
                        (VaryConsumerThrow<Throwable>)
                                args -> {
                                    final File srcFile = (File) args[0];
                                    if (srcFile != null) {
                                        if (srcFile.exists() && srcFile.isFile()) {
                                            String[] strings = extractFileNameInfo(srcFile.getName());
                                            String name = null;
                                            if (nameHandler != null) {
                                                name = nameHandler.apply(strings[0]);
                                            }
                                            if (name == null) {
                                                name = strings[0];
                                            }
                                            rename(srcFile, name + "." + strings[1]);
                                        }
                                    }
                                });
    }

    /**
     * 提前文件名信息
     *
     * @param filePath 文件路径
     * @return 返回文件基本名和扩展名
     */
    public static String[] extractFileNameInfo(String filePath) {
        int si = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        int di = filePath.lastIndexOf('.');
        if ((si < 0 && di < 0)) {
            return new String[]{filePath, ""};
        } else if (si < 0) {
            return new String[]{filePath.substring(0, di), filePath.substring(di + 1)};
        } else if (di < 0 || di < si) {
            return new String[]{filePath.substring(si + 1), ""};
        } else {
            return new String[]{filePath.substring(si + 1, di), filePath.substring(di + 1)};
        }
    }

    /**
     * 复制文件
     *
     * @param srcPath  源文件
     * @param destPath 目标文件
     * @throws IODetailException 如果发生异常
     */
    public static void copy(String srcPath, String destPath) throws IODetailException {
        copy(new File(srcPath), new File(destPath));
    }

    /**
     * 复制文件
     *
     * @param src  源文件
     * @param dest 目标文件
     * @throws IODetailException 如果发生异常
     */
    public static void copy(File src, File dest) throws IODetailException {
        byte[] buffer = new byte[4096];
        FileInputStream fileInputStream = New.fileInputStream(src);
        FileOutputStream fileOutputStream = New.fileOutputStream(dest);
        try {
            int read;
            while (true) {
                try {
                    read = fileInputStream.read(buffer);
                } catch (IOException e) {
                    throw new IODetailException(e.getMessage(), e, src.getPath());
                }
                if (read == -1) {
                    break;
                }
                try {
                    fileOutputStream.write(buffer, 0, read);
                } catch (IOException e) {
                    throw new IODetailException(e.getMessage(), e, dest.getPath());
                }
            }
        } finally {
            ToolIO.closes(fileInputStream, fileOutputStream);
        }
    }

    /**
     * 复制单个文件
     *
     * @param srcPath  待复制的文件名
     * @param destPath 目标文件名
     * @param overlay  如果目标文件存在，是否覆盖
     * @param cut      是否剪切
     */
    public static void copy(String srcPath, String destPath, boolean overlay, boolean cut) {
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            return;
        }
        File destFile = new File(destPath);
        if (srcFile.isDirectory()) {
            createDirectory(destFile);
            doCopy(srcFile.listFiles(), destFile, overlay, cut);
        } else {
            doCopy(new File[]{srcFile}, destFile, overlay, cut);
        }
        if (cut) {
            srcFile.delete();
        }
    }

    /**
     * 复制单个文件
     *
     * @param files         待复制的文件名
     * @param destDirectory 目标目录
     * @param overlay       如果目标文件存在，是否覆盖
     * @param cut           是否剪切
     */
    private static void doCopy(File[] files, File destDirectory, boolean overlay, boolean cut) {
        for (File srcFile : files) {
            File destFile = new File(destDirectory, srcFile.getName());
            if (srcFile.isDirectory()) {
                if (destFile.mkdirs()) {
                    doCopy(srcFile.listFiles(), destFile, overlay, cut);
                }
            } else {
                copy(srcFile, destFile);
            }
            if (cut) {
                srcFile.delete();
            }
        }
    }

    /**
     * 复制文件清单到指定路径
     *
     * @param fileManifest    文件清单
     * @param srcDirPath      原目录路径
     * @param destDirPath     目标目录路径
     * @param srcPathHandler  原目录路径处理器，{@code (srcDirPath,fileMenuItem)->path}
     * @param destPathHandler 目标目录路径处理器，{@code (destDirPath,fileMenuItem)->path}
     * @return BatchHandleResult<Boolean> 返回结果
     */
    public static HandleResults<Boolean> copy(
            final Iterator<String> fileManifest,
            final String srcDirPath,
            final String destDirPath,
            final BiFunctionThrow<String, String, String, RuntimeException> srcPathHandler,
            final BiFunctionThrow<String, String, String, RuntimeException> destPathHandler) {
        if (fileManifest == null) {
            throw new IllegalArgumentException("The file manifest list does not exist");
        }
        if (srcDirPath == null) {
            throw new IllegalArgumentException("不存在");
        }
        if (destDirPath == null) {
            throw new IllegalArgumentException("不存在");
        }
        final File srcFile = new File(srcDirPath);
        final File destFile = new File(destDirPath);
        createDirectory(srcFile);
        createDirectory(destFile);
        return new HandleResults<Boolean>()
                .loopExecute(
                        fileManifest,
                        (VaryConsumerThrow<? extends Throwable>)
                                args -> {
                                    String element = (String) args[0];
                                    if (element != null) {
                                        File aSrcFile =
                                                new File(srcPathHandler.apply(srcFile.getCanonicalPath(), element));
                                        if (aSrcFile.exists() && aSrcFile.isFile()) {
                                            File aDestFile =
                                                    new File(destPathHandler.apply(destFile.getCanonicalPath(), element));
                                            createDirectory(aDestFile.getParentFile());
                                            copy(aSrcFile, aDestFile);
                                        } else {
                                            throw new IODetailException("The src file must be file", aSrcFile.getPath());
                                        }
                                    }
                                });
    }

    /**
     * 在根目录下获取指定路径列表在
     *
     * @param rootDirectory     根目录
     * @param relationFilePaths 相对文件路径
     * @return {@code Iterator<Path>}
     */
    public static List<Path> getSpecifiedPathsInRootDirectory(
            String rootDirectory, String... relationFilePaths) {
        List<Path> files = new ArrayList<>(relationFilePaths.length);
        for (String relationFilePath : relationFilePaths) {
            files.add(Paths.get(rootDirectory, relationFilePath));
        }
        return files;
    }

    /**
     * 合并文本文件
     *
     * @param fileManifest 文件清单
     * @param newFilePath  新文件路径
     * @return BatchHandleResult<Boolean> 返回结果
     */
    public static HandleResults<Boolean> mergeTextFileByLine(
            final Iterator<String> fileManifest, String newFilePath) {
        File newFile = createFile(newFilePath);
        final Set<String> set = new LinkedHashSet<>();
        HandleResults<Boolean> handleResults =
                new HandleResults<Boolean>()
                        .loopExecute(
                                fileManifest,
                                (VaryConsumerThrow<? extends Throwable>)
                                        args -> {
                                            File file = new File(args[0].toString());
                                            if (!file.exists()) {
                                                return;
                                            }
                                            if (!file.isFile()) {
                                                throw new IODetailException("This is not a file", file.getPath());
                                            }
                                            LineIter lineIterable = new LineIter(new FileReader(file));
                                            for (String line : lineIterable) {
                                                if (Whether.noEmpty(line)) {
                                                    set.add(line.trim());
                                                }
                                            }
                                        });
        try {
            ToolIO.writeTextData(newFile, set, null);
        } catch (IOException e) {
            throw new IODetailException("Failure to write file", e, newFilePath);
        }
        return handleResults;
    }

    /**
     * 检查并预留文件，检查格式，是否为文件如果存在则删除，如果是目录
     *
     * @param filePath 文件
     * @param formats  格式，如果为空则不检查
     * @return 返回文件
     * @throws IllegalArgumentException 如果文件为null或者格式不正确或者是目录则抛出该异常
     * @throws IllegalStateException    如果删除失败
     */
    public static File checkReservedFile(String filePath, String... formats) {
        if (filePath == null) {
            throw new IllegalArgumentException("The input file path must not be null");
        }
        return checkReservedFile(new File(filePath), formats);
    }

    /**
     * 检查并预留文件，检查格式，是否为文件如果存在则删除，如果是目录
     *
     * @param file    文件
     * @param formats 格式，如果为空则不检查
     * @return 返回文件
     * @throws IllegalArgumentException 如果文件为null或者格式不正确或者是目录则抛出该异常
     * @throws IllegalStateException    如果删除失败
     */
    public static File checkReservedFile(File file, String... formats) {
        if (file == null) {
            throw new IllegalArgumentException("The input file must not be null");
        }
        if (Whether.noEmpty(formats)) {
            boolean b = true;
            for (String format : formats) {
                if (Whether.endsWithAndCase(file.getName(), format, true)) {
                    b = false;
                    break;
                }
            }
            if (b) {
                throw new IllegalArgumentException(
                        "The format of input file [" + file + "] must be " + Arrays.toString(formats));
            }
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IllegalArgumentException("The input file [" + file + "] must be file");
            }
            if (!file.delete()) {
                throw new IllegalStateException("The input file [" + file + "] be exists and not deleted");
            }
        }
        return file;
    }

    /**
     * 合法化文件绝对路径
     *
     * @param driver 驱动
     * @param paths  路径
     * @return 返回合法化文件路径
     */
    public static String legalizationFileAbsolutePath(String driver, String... paths) {
        return legalizationFilePath(false, true, driver, paths);
    }

    /**
     * 合法化文件相对路径
     *
     * @param paths 路径
     * @return 返回合法化文件路径
     */
    public static String legalizationFilRelativePath(String... paths) {
        return legalizationFilePath(false, false, null, paths);
    }

    /**
     * 合法化文件路径URL
     *
     * @param driver 驱动
     * @param paths  路径
     * @return 返回合法化文件路径
     */
    public static String legalizationFileAbsolutePathUrl(String driver, String... paths) {
        return legalizationFilePath(true, true, driver, paths);
    }

    /**
     * 合法化文件路径
     *
     * @param driver 驱动
     * @param paths  路径
     * @return 返回合法化文件路径
     */
    private static String legalizationFilePath(
            boolean isUrl, boolean isAbsolutePath, String driver, String... paths) {
        if (paths.length == 0) {
            throw new IllegalArgumentException("The paths must be not empty");
        }
        if (isAbsolutePath && Whether.windowsSystem()) {
            if (Whether.empty(driver)) {
                throw new IllegalArgumentException("The windows system must have a driver symbol");
            }
            if (driver.length() > 2 || !Whether.letterChar(driver.charAt(0)) || ':' == driver.charAt(0)) {
                throw new IllegalArgumentException("The driver error for [" + driver + "]");
            }
        }
        StringBuilder sb = new StringBuilder(256);
        if (isAbsolutePath && isUrl) {
            sb.append("file://");
        }
        if (isAbsolutePath) {
            if (Whether.windowsSystem()) {
                if (isUrl) {
                    sb.append("/");
                }
                sb.append(driver.charAt(0)).append(":");
            }
            sb.append(JavaEnvironment.PATH_SEPARATOR);
        }
        char c;
        boolean isPathSplit = false;
        for (String path : paths) {
            if (!isPathSplit) {
                sb.append(JavaEnvironment.PATH_SEPARATOR);
            }
            for (int i = 0; i < path.length(); i++) {
                c = path.charAt(i);
                if (c == '\\' || c == '/') {
                    if (!isPathSplit) {
                        sb.append(JavaEnvironment.PATH_SEPARATOR);
                        isPathSplit = true;
                    }
                } else {
                    if (c == '"' || c == ':' || c == '*' || c == '?' || c == '|' || c == '<' || c == '>') {
                        throw new IllegalArgumentException("The path is not allowed to include \" : * ? | < >");
                    }
                    isPathSplit = false;
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 价值类路径上的属性文件
     *
     * @param path 路径
     * @return Properties
     */
    public static Properties loadClasspathProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(JavaEnvironment.class.getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * 获取文件大小 返回 KB 保留3位小数 没有文件时返回0
     *
     * @param filePath
     * @return
     */
    public static long getFileByteSize(String filePath) {
        return getFileByteSize(new File(filePath));
    }

    /**
     * 获取文件大小 返回 KB 保留3位小数 没有文件时返回0
     *
     * @param file
     * @return
     */
    public static long getFileByteSize(File file) {
        if (file.exists()) {
            return file.length() / 1000;
        } else {
            return 0;
        }
    }

    /**
     * 是否是常用图片格式
     *
     * @param path 路径
     * @return 如果是常用图片格式返回true，否则返回false
     */
    public static boolean isCommonImageFormat(String path) {
        String extension = ToolString.extractFileExtension(path);
        if (extension != null) {
            return COMMON_IMAGE_FORMATS.contains(extension.toLowerCase());
        }
        return false;
    }

    /**
     * 压缩文件或目录
     *
     * @param srcFileOrDirectory 压缩文件或目录
     * @param destZipFile        目标文件
     * @throws IOException 如果发生异常
     */
    public static void zipFileOrDirectory(String srcFileOrDirectory, String destZipFile)
            throws IOException {
        if (srcFileOrDirectory == null) {
            throw new IllegalArgumentException("The directory must be no null");
        }
        if (destZipFile == null) {
            throw new IllegalArgumentException("The zip file must be no null");
        }
        zipFileOrDirectory(new File(srcFileOrDirectory), new File(destZipFile));
    }

    /**
     * 压缩文件或目录
     *
     * @param srcFileOrDirectory 压缩文件或目录
     * @param destZipFile        目标文件
     * @throws IOException 如果发生异常
     */
    public static void zipFileOrDirectory(File srcFileOrDirectory, String destZipFile)
            throws IOException {
        if (destZipFile == null) {
            throw new IllegalArgumentException("The dest zip file must be no null");
        }
        zipFileOrDirectory(srcFileOrDirectory, new File(destZipFile));
    }

    /**
     * 压缩文件或目录
     *
     * @param srcFileOrDirectory 压缩文件或目录
     * @param destZipFile        目标文件
     * @throws IOException 如果发生异常
     */
    public static void zipFileOrDirectory(String srcFileOrDirectory, File destZipFile)
            throws IOException {
        if (srcFileOrDirectory == null) {
            throw new IllegalArgumentException("The src directory must be no null");
        }
        zipFileOrDirectory(new File(srcFileOrDirectory), destZipFile);
    }

    /**
     * 压缩文件或目录
     *
     * @param srcFileOrDirectory 压缩文件或目录
     * @param destZipFile        目标文件
     * @throws IOException 如果发生异常
     */
    public static void zipFileOrDirectory(File srcFileOrDirectory, File destZipFile)
            throws IOException {
        checkParentDirectory(destZipFile);
        try (FileOutputStream fileOutputStream = new FileOutputStream(destZipFile)) {
            zipFileOrDirectory(srcFileOrDirectory, fileOutputStream);
        }
    }

    /**
     * 压缩文件或目录
     *
     * @param srcFileOrDirectory 压缩文件或目录
     * @param outputStream       输出流
     * @throws IOException 如果发生异常
     */
    public static void zipFileOrDirectory(File srcFileOrDirectory, OutputStream outputStream)
            throws IOException {
        if (srcFileOrDirectory == null) {
            throw new IllegalArgumentException("The srcFileOrDirectory must be no null");
        }
        if (!srcFileOrDirectory.exists()) {
            throw new IllegalArgumentException(
                    "The srcFileOrDirectory does not exist for [" + srcFileOrDirectory + "]");
        }
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            if (srcFileOrDirectory.isFile()) {
                zipCompress(zipOutputStream, srcFileOrDirectory, srcFileOrDirectory.getName());
            } else {
                zipCompress(zipOutputStream, srcFileOrDirectory, "");
            }
        }
    }

    /**
     * 压缩文件或目录
     *
     * @param srcFileOrDirectory 压缩文件或目录
     * @param destZipFile        目标文件
     * @throws IOException 如果发生异常
     */
    public static void zipBatchFileOrDirectory(Collection<File> srcFileOrDirectory, File destZipFile)
            throws IOException {
        checkParentDirectory(destZipFile);
        try (FileOutputStream fileOutputStream = new FileOutputStream(destZipFile)) {
            zipBatchFileOrDirectory(srcFileOrDirectory, fileOutputStream);
        }
    }

    /**
     * 压缩文件或目录
     *
     * @param srcFileOrDirectory 压缩文件或目录
     * @param outputStream       输出流
     * @throws IOException 如果发生异常
     */
    public static void zipBatchFileOrDirectory(
            Collection<File> srcFileOrDirectory, OutputStream outputStream) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (File srcFile : srcFileOrDirectory) {
                if (srcFile == null || !srcFile.exists()) {
                    continue;
                }
                zipCompress(zipOutputStream, srcFile, srcFile.getName());
            }
        }
    }

    /**
     * 压缩文件或目录
     *
     * @param zipOut    {@code ZipOutputStream}压缩输出流
     * @param itemFile  {@code File}压缩文件
     * @param entryName {@code String}压缩文件名
     * @throws IOException 如果发生异常
     */
    private static void zipCompress(ZipOutputStream zipOut, File itemFile, String entryName)
            throws IOException {
        if (itemFile.isHidden()) {
            return;
        }
        if (itemFile.isDirectory()) {
            File[] children = itemFile.listFiles();
            for (File childFile : children) {
                zipCompress(
                        zipOut,
                        childFile,
                        (entryName.length() == 0 ? "" : entryName + "/") + childFile.getName());
            }
        } else {
            zipOut.putNextEntry(new ZipEntry(entryName));
            ToolIO.write(zipOut, itemFile);
            zipOut.closeEntry();
        }
    }

    /**
     * 读取到字节数组0
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        fi.close();
        return buffer;
    }

    /**
     * 读取到字节数组1
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(String filePath) throws IOException {

        File f = new File(filePath);
        if (!f.exists()) {
            throw new FileNotFoundException(filePath);
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length())) {
            BufferedInputStream in = null;
            in = new BufferedInputStream(new FileInputStream(f));
            byte[] buffer = new byte[JavaEnvironment.DEFAULT_BUFFER_SIZE];
            int len = 0;
            while (-1 != (len = in.read(buffer))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 读取到字节数组2
     *
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray2(String filePath) throws IOException {
        File f = new File(filePath);
        if (!f.exists()) {
            throw new FileNotFoundException(filePath);
        }
        FileChannel channel = null;
        FileInputStream fs = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length())) {
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(JavaEnvironment.DEFAULT_BUFFER_SIZE);
            while ((channel.read(byteBuffer)) > 0) {
                bos.write(byteBuffer.array(), 0, byteBuffer.limit());
            }
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Mapped File way MappedByteBuffer 可以在处理大文件时，提升性能
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray3(String filePath) throws IOException {
        FileChannel fc = null;
        RandomAccessFile rf = null;
        try {
            rf = new RandomAccessFile(filePath, "r");
            fc = rf.getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    fc.size()).load();
            //System.out.println(byteBuffer.isLoaded());
            byte[] result = new byte[(int) fc.size()];
            if (byteBuffer.remaining() > 0) {
                // System.out.println("remain");
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                rf.close();
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

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
     * ???????????????????????????
     *
     * @param dirFile  ??????
     * @param prefixes ??????
     * @return ?????????????????????????????????List<File>
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
     * ???????????????????????????
     *
     * @param dirFile  ??????
     * @param suffixes ??????
     * @return ?????????????????????????????????List<File>
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
     * ???????????????????????????
     *
     * @param dirFile ??????
     * @param regexp  ???????????????
     * @return ?????????????????????????????????List<File>
     */
    public static List<File> listFilesByRegexp(final File dirFile, final String regexp) {
        return listFilesByRegexp(dirFile, Pattern.compile(regexp));
    }

    /**
     * ???????????????????????????
     *
     * @param dirFile ??????
     * @param regexp  ???????????????
     * @return ?????????????????????????????????List<File>
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
     * ?????????????????????
     *
     * @param dirFile ??????
     * @return ?????????????????????????????????List<File>
     */
    public static List<File> listFiles(final File dirFile) {
        return listFiles(dirFile, pathname -> true);
    }

    /**
     * ?????????????????????
     *
     * @param dirFile    ??????
     * @param fileFilter ???????????????
     * @return ?????????????????????????????????List<File>
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
     * ??????????????????????????????????????????
     *
     * @param directory ??????
     */
    public static void checkDirectory(File directory) {
        if (!directory.isDirectory()) {
            createDirectory(directory);
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param file ??????
     */
    public static void checkParentDirectory(File file) {
        File parentFile = file.getParentFile();
        if (!parentFile.isDirectory()) {
            createDirectory(parentFile);
        }
    }

    /**
     * ????????????
     *
     * @param filePath ????????????
     * @return ?????? {@code File}
     */
    public static File createFile(final String filePath) throws IODetailException {
        if (filePath == null) {
            throw new IllegalArgumentException("The file path must be no null");
        }
        return createFile(new File(filePath));
    }

    /**
     * ????????????
     *
     * @param file ????????????
     * @return ?????? {@code File}
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
     * ????????????
     *
     * @param filePaths ????????????
     * @return ?????? {@code BatchHandleResult<File>}
     */
    public static HandleResults<File> createFiles(final String... filePaths) {
        return new HandleResults<File>()
                .loopExecute(
                        Arrays.asList(filePaths),
                        (VaryFunctionThrow<File, Throwable>) args -> createFile((String) args[0]));
    }

    /**
     * ????????????
     *
     * @param filePath ????????????
     * @return ?????? {@code File}
     */
    public static File createDirectory(final String filePath) throws IODetailException {
        if (filePath == null) {
            throw new IllegalArgumentException("The file path must be no null");
        }
        return createDirectory(new File(filePath));
    }

    /**
     * ????????????
     *
     * @param file ????????????
     * @return ?????? {@code File}
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
     * ????????????
     *
     * @param filePaths ????????????
     * @return ?????? {@code BatchHandleResult<File>}
     */
    public static HandleResults<File> createDirectories(final String... filePaths) {
        return new HandleResults<File>()
                .loopExecute(
                        Arrays.asList(filePaths),
                        (VaryFunctionThrow<File, Throwable>) args -> createDirectory((String) args[0]));
    }

    /**
     * ??????????????????
     *
     * @return ?????? {@code File}
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
     * ??????????????????
     *
     * @param suffix ????????????
     * @return ?????? {@code File}
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
     * ??????????????????
     *
     * @param filePath ????????????
     */
    public static void removeFileOrDirectory(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("The file path must be no null");
        }
        removeFileOrDirectory(new File(filePath));
    }

    /**
     * ??????????????????
     *
     * @param file ??????
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
     * ??????????????????
     *
     * @param files ??????
     */
    public static void removeFileOrDirectory(Collection<File> files) throws IOException {
        for (File file : files) {
            removeFileOrDirectory(file);
        }
    }

    /**
     * ????????????
     *
     * @param srcPath  ?????????
     * @param destPath ????????????
     */
    public static void move(String srcPath, String destPath) throws IODetailException {
        move(new File(srcPath), new File(destPath));
    }

    /**
     * ????????????
     *
     * @param src  ?????????
     * @param dest ????????????
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
     * ??????????????????
     *
     * @param destDirectory ????????????
     * @param filePaths     ????????????
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
     * ???????????????
     *
     * @param srcPath ?????????
     * @param newName ????????????
     */
    public static void rename(String srcPath, String newName) throws IODetailException {
        move(srcPath, Paths.get(srcPath).resolve(newName).toString());
    }

    /**
     * ???????????????
     *
     * @param src     ?????????
     * @param newName ?????????
     */
    public static void rename(File src, String newName) throws IODetailException {
        move(src, new File(src.getParentFile(), newName));
    }

    /**
     * ???????????????
     *
     * @param nameHandler ???????????????
     * @param filePaths   ????????????
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
     * ?????????????????????
     *
     * @param filePath ????????????
     * @return ?????????????????????????????????
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
     * ????????????
     *
     * @param srcPath  ?????????
     * @param destPath ????????????
     * @throws IODetailException ??????????????????
     */
    public static void copy(String srcPath, String destPath) throws IODetailException {
        copy(new File(srcPath), new File(destPath));
    }

    /**
     * ????????????
     *
     * @param src  ?????????
     * @param dest ????????????
     * @throws IODetailException ??????????????????
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
     * ??????????????????
     *
     * @param srcPath  ?????????????????????
     * @param destPath ???????????????
     * @param overlay  ???????????????????????????????????????
     * @param cut      ????????????
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
     * ??????????????????
     *
     * @param files         ?????????????????????
     * @param destDirectory ????????????
     * @param overlay       ???????????????????????????????????????
     * @param cut           ????????????
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
     * ?????????????????????????????????
     *
     * @param fileManifest    ????????????
     * @param srcDirPath      ???????????????
     * @param destDirPath     ??????????????????
     * @param srcPathHandler  ???????????????????????????{@code (srcDirPath,fileMenuItem)->path}
     * @param destPathHandler ??????????????????????????????{@code (destDirPath,fileMenuItem)->path}
     * @return BatchHandleResult<Boolean> ????????????
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
            throw new IllegalArgumentException("?????????");
        }
        if (destDirPath == null) {
            throw new IllegalArgumentException("?????????");
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
     * ??????????????????????????????????????????
     *
     * @param rootDirectory     ?????????
     * @param relationFilePaths ??????????????????
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
     * ??????????????????
     *
     * @param fileManifest ????????????
     * @param newFilePath  ???????????????
     * @return BatchHandleResult<Boolean> ????????????
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
     * ?????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param filePath ??????
     * @param formats  ?????????????????????????????????
     * @return ????????????
     * @throws IllegalArgumentException ???????????????null??????????????????????????????????????????????????????
     * @throws IllegalStateException    ??????????????????
     */
    public static File checkReservedFile(String filePath, String... formats) {
        if (filePath == null) {
            throw new IllegalArgumentException("The input file path must not be null");
        }
        return checkReservedFile(new File(filePath), formats);
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param file    ??????
     * @param formats ?????????????????????????????????
     * @return ????????????
     * @throws IllegalArgumentException ???????????????null??????????????????????????????????????????????????????
     * @throws IllegalStateException    ??????????????????
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
     * ???????????????????????????
     *
     * @param driver ??????
     * @param paths  ??????
     * @return ???????????????????????????
     */
    public static String legalizationFileAbsolutePath(String driver, String... paths) {
        return legalizationFilePath(false, true, driver, paths);
    }

    /**
     * ???????????????????????????
     *
     * @param paths ??????
     * @return ???????????????????????????
     */
    public static String legalizationFilRelativePath(String... paths) {
        return legalizationFilePath(false, false, null, paths);
    }

    /**
     * ?????????????????????URL
     *
     * @param driver ??????
     * @param paths  ??????
     * @return ???????????????????????????
     */
    public static String legalizationFileAbsolutePathUrl(String driver, String... paths) {
        return legalizationFilePath(true, true, driver, paths);
    }

    /**
     * ?????????????????????
     *
     * @param driver ??????
     * @param paths  ??????
     * @return ???????????????????????????
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
     * ?????????????????????????????????
     *
     * @param path ??????
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
     * ?????????????????? ?????? KB ??????3????????? ?????????????????????0
     *
     * @param filePath
     * @return
     */
    public static long getFileByteSize(String filePath) {
        return getFileByteSize(new File(filePath));
    }

    /**
     * ?????????????????? ?????? KB ??????3????????? ?????????????????????0
     *
     * @param file
     * @return
     */
    public static long getFileByteSize(File file) {
        if (file.exists()) {
            return file.length();
        } else {
            return 0;
        }
    }

    /**
     * ???????????????????????????
     *
     * @param path ??????
     * @return ?????????????????????????????????true???????????????false
     */
    public static boolean isCommonImageFormat(String path) {
        String extension = ToolString.extractFileExtension(path);
        if (extension != null) {
            return COMMON_IMAGE_FORMATS.contains(extension.toLowerCase());
        }
        return false;
    }

    /**
     * ?????????????????????
     *
     * @param srcFileOrDirectory ?????????????????????
     * @param destZipFile        ????????????
     * @throws IOException ??????????????????
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
     * ?????????????????????
     *
     * @param srcFileOrDirectory ?????????????????????
     * @param destZipFile        ????????????
     * @throws IOException ??????????????????
     */
    public static void zipFileOrDirectory(File srcFileOrDirectory, String destZipFile)
            throws IOException {
        if (destZipFile == null) {
            throw new IllegalArgumentException("The dest zip file must be no null");
        }
        zipFileOrDirectory(srcFileOrDirectory, new File(destZipFile));
    }

    /**
     * ?????????????????????
     *
     * @param srcFileOrDirectory ?????????????????????
     * @param destZipFile        ????????????
     * @throws IOException ??????????????????
     */
    public static void zipFileOrDirectory(String srcFileOrDirectory, File destZipFile)
            throws IOException {
        if (srcFileOrDirectory == null) {
            throw new IllegalArgumentException("The src directory must be no null");
        }
        zipFileOrDirectory(new File(srcFileOrDirectory), destZipFile);
    }

    /**
     * ?????????????????????
     *
     * @param srcFileOrDirectory ?????????????????????
     * @param destZipFile        ????????????
     * @throws IOException ??????????????????
     */
    public static void zipFileOrDirectory(File srcFileOrDirectory, File destZipFile)
            throws IOException {
        checkParentDirectory(destZipFile);
        try (FileOutputStream fileOutputStream = new FileOutputStream(destZipFile)) {
            zipFileOrDirectory(srcFileOrDirectory, fileOutputStream);
        }
    }

    /**
     * ?????????????????????
     *
     * @param srcFileOrDirectory ?????????????????????
     * @param outputStream       ?????????
     * @throws IOException ??????????????????
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
     * ?????????????????????
     *
     * @param srcFileOrDirectory ?????????????????????
     * @param destZipFile        ????????????
     * @throws IOException ??????????????????
     */
    public static void zipBatchFileOrDirectory(Collection<File> srcFileOrDirectory, File destZipFile)
            throws IOException {
        checkParentDirectory(destZipFile);
        try (FileOutputStream fileOutputStream = new FileOutputStream(destZipFile)) {
            zipBatchFileOrDirectory(srcFileOrDirectory, fileOutputStream);
        }
    }

    /**
     * ?????????????????????
     *
     * @param srcFileOrDirectory ?????????????????????
     * @param outputStream       ?????????
     * @throws IOException ??????????????????
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
     * ?????????????????????
     *
     * @param zipOut    {@code ZipOutputStream}???????????????
     * @param itemFile  {@code File}????????????
     * @param entryName {@code String}???????????????
     * @throws IOException ??????????????????
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
}

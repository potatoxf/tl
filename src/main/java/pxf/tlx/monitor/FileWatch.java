package pxf.tlx.monitor;


import pxf.tl.lang.CommonKey;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * 文件监听参数
 *
 * @author potatoxf
 */
public final class FileWatch {
    private static final int CREATED = 0b0_00_01;
    private static final int DELETED = 0b0_00_10;
    private static final int UPDATED = 0b0_01_00;
    private static final int FILE = 0b0_10_00;
    private static final int DIR = 0b1_00_00;
    private final CommonKey commonKey;
    private final Path root;
    private boolean isWatchSubPath;
    private int targetEvent;
    private Pattern regexpPath;
    private Predicate<Path> pathFilter;

    public FileWatch(CommonKey commonKey, Path root) {
        this.commonKey = commonKey;
        this.root = root;
    }

    public static int buildEventMark(
            boolean isFileOtherwiseDir, boolean created, boolean deleted, boolean updated) {
        int mark = 0;
        if (isFileOtherwiseDir) {
            mark |= FILE;
        } else {
            mark |= DIR;
        }
        if (created) {
            mark |= CREATED;
        }
        if (deleted) {
            mark |= DELETED;
        }
        if (updated) {
            mark |= UPDATED;
        }
        return mark;
    }

    public static int buildEventMark(File file, boolean created, boolean deleted, boolean updated) {
        if (!file.exists()) {
            throw new IllegalArgumentException("The file of [" + file + "] must existed");
        }
        return buildEventMark(file.isFile(), created, deleted, updated);
    }

    public static int buildEventMark(Path path, boolean created, boolean deleted, boolean updated) {
        return buildEventMark(path.toFile(), created, deleted, updated);
    }

    public static boolean isFile(int target) {
        return (FILE & target) != 0;
    }

    public static boolean isDir(int target) {
        return (DIR & target) != 0;
    }

    public static boolean isCreated(int target) {
        return (CREATED & target) != 0;
    }

    public static boolean isDeleted(int target) {
        return (DELETED & target) != 0;
    }

    public static boolean isUpdated(int target) {
        return (UPDATED & target) != 0;
    }

    public boolean isValidEvent(int event) {
        return (targetEvent & event) - FileWatch.FILE > 0;
    }

    public FileWatch setFilterMark(
            boolean isFileOtherwiseDir, boolean created, boolean deleted, boolean updated) {
        this.targetEvent = buildEventMark(isFileOtherwiseDir, created, deleted, updated);
        return this;
    }

    public CommonKey getReferenceKey() {
        return commonKey;
    }

    public Path getRoot() {
        return root;
    }

    public Pattern getRegexpPath() {
        return regexpPath;
    }

    public FileWatch setRegexpPath(Pattern regexpPath) {
        this.regexpPath = regexpPath;
        return this;
    }

    public boolean isWatchSubPath() {
        return isWatchSubPath;
    }

    public FileWatch setWatchSubPath(boolean watchSubPath) {
        isWatchSubPath = watchSubPath;
        return this;
    }

    public Predicate<Path> getPathFilter() {
        return pathFilter;
    }

    public FileWatch setPathFilter(Predicate<Path> pathFilter) {
        this.pathFilter = pathFilter;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileWatch)) return false;
        FileWatch fileWatch = (FileWatch) o;
        return getReferenceKey().equals(fileWatch.getReferenceKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReferenceKey());
    }
}

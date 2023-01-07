package pxf.tl.io.virtual;


import java.io.*;
import java.nio.file.Files;
import java.util.Collections;

/*
 * An implementation of {@link potatoxf.helper.io.virtual.Vfs.Dir} for directory {@link java.io.File}.
 */
public class SystemVirtualDirectory implements VirtualDirectory {
    private final File file;

    public SystemVirtualDirectory(File file) {
        if (file != null && (!file.isDirectory() || !file.canRead())) {
            throw new RuntimeException("cannot use dir " + file);
        }
        this.file = file;
    }

    public String getPath() {
        return file != null ? file.getPath().replace("\\", "/") : "/NO-SUCH-DIRECTORY/";
    }

    public Iterable<VirtualFile> getFiles() {
        if (file == null || !file.exists()) return Collections.emptyList();
        return () -> {
            try {
                return Files.walk(file.toPath())
                        .filter(Files::isRegularFile)
                        .map(
                                path ->
                                        (VirtualFile) new SystemVirtualFile(SystemVirtualDirectory.this, path.toFile()))
                        .iterator();
            } catch (IOException e) {
                throw new RuntimeException("could not get files for " + file, e);
            }
        };
    }

    /**
     * an implementation of {@link VirtualFile} for a directory {@link File}
     */
    public static class SystemVirtualFile implements VirtualFile {
        private final SystemVirtualDirectory root;
        private final File file;

        public SystemVirtualFile(final SystemVirtualDirectory root, File file) {
            this.root = root;
            this.file = file;
        }

        public String getName() {
            return file.getName();
        }

        public String getRelativePath() {
            String filepath = file.getPath().replace("\\", "/");
            if (filepath.startsWith(root.getPath())) {
                return filepath.substring(root.getPath().length() + 1);
            }

            return null; // should not get here
        }

        public InputStream openInputStream() {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String toString() {
            return file.toString();
        }
    }
}

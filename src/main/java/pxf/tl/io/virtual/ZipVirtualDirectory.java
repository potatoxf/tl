package pxf.tl.io.virtual;


import pxf.tl.util.ToolIO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * an implementation of {@link VirtualDirectory} for {@link ZipFile}
 *
 * @author potatoxf
 */
public class ZipVirtualDirectory implements VirtualDirectory {
    final ZipFile zipFile;

    public ZipVirtualDirectory(ZipFile zipFile) {
        this.zipFile = Objects.requireNonNull(zipFile, "The zip file must be no null");
    }

    public String getPath() {
        return zipFile.getName().replace("\\", "/");
    }

    public Iterable<VirtualFile> getFiles() {
        return () ->
                zipFile.stream()
                        .filter(entry -> !entry.isDirectory())
                        .map(entry -> (VirtualFile) new ZipVirtualFile(ZipVirtualDirectory.this, entry))
                        .iterator();
    }

    public void close() {
        ToolIO.closes(zipFile);
    }

    @Override
    public String toString() {
        return zipFile.getName();
    }

    /**
     * an implementation of {@link VirtualFile} for {@link ZipEntry}
     */
    private static class ZipVirtualFile implements VirtualFile {
        private final ZipVirtualDirectory root;
        private final ZipEntry entry;

        public ZipVirtualFile(final ZipVirtualDirectory root, ZipEntry entry) {
            this.root = root;
            this.entry = entry;
        }

        public String getName() {
            String name = entry.getName();
            return name.substring(name.lastIndexOf("/") + 1);
        }

        public String getRelativePath() {
            return entry.getName();
        }

        public InputStream openInputStream() throws IOException {
            return root.zipFile.getInputStream(entry);
        }

        @Override
        public String toString() {
            return root.getPath() + "!" + java.io.File.separatorChar + entry.toString();
        }
    }
}

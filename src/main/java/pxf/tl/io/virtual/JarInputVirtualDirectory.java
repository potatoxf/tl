package pxf.tl.io.virtual;


import pxf.tl.util.ToolLog;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class JarInputVirtualDirectory implements VirtualDirectory {
    private final URL url;
    JarInputStream jarInputStream;
    long cursor = 0;
    long nextCursor = 0;

    public JarInputVirtualDirectory(URL url) {
        this.url = url;
    }

    public String getPath() {
        return url.getPath();
    }

    public Iterable<VirtualFile> getFiles() {
        return () ->
                new Iterator<VirtualFile>() {
                    VirtualFile entry = null;

                    {
                        try {
                            jarInputStream = new JarInputStream(url.openConnection().getInputStream());
                        } catch (Exception e) {
                            throw new RuntimeException("Could not open url connection", e);
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        return entry != null || (entry = computeNext()) != null;
                    }

                    @Override
                    public VirtualFile next() {
                        VirtualFile next = entry;
                        entry = null;
                        return next;
                    }

                    private VirtualFile computeNext() {
                        while (true) {
                            try {
                                ZipEntry entry = jarInputStream.getNextJarEntry();
                                if (entry == null) {
                                    return null;
                                }

                                long size = entry.getSize();
                                if (size < 0) size = 0xffffffffl + size; // JDK-6916399
                                nextCursor += size;
                                if (!entry.isDirectory()) {
                                    return new JarInputVirtualFile(
                                            JarInputVirtualDirectory.this, entry, cursor, nextCursor);
                                }
                            } catch (IOException e) {
                                throw new RuntimeException("could not get next zip entry", e);
                            }
                        }
                    }
                };
    }

    public void close() {
        try {
            if (jarInputStream != null) ((InputStream) jarInputStream).close();
        } catch (IOException e) {
            ToolLog.warn(e, () -> "Could not get URL");
        }
    }

    /**
     *
     */
    public static class JarInputVirtualFile implements VirtualFile {
        private final ZipEntry entry;
        private final JarInputVirtualDirectory jarInputDir;
        private final long fromIndex;
        private final long endIndex;

        public JarInputVirtualFile(
                JarInputVirtualDirectory jarInputDir, ZipEntry entry, long cursor, long nextCursor) {
            this.entry = entry;
            this.jarInputDir = jarInputDir;
            this.fromIndex = cursor;
            this.endIndex = nextCursor;
        }

        public String getName() {
            String name = entry.getName();
            return name.substring(name.lastIndexOf("/") + 1);
        }

        public String getRelativePath() {
            return entry.getName();
        }

        public InputStream openInputStream() {
            return new InputStream() {
                @Override
                public int read() throws IOException {
                    if (jarInputDir.cursor >= fromIndex && jarInputDir.cursor <= endIndex) {
                        int read = jarInputDir.jarInputStream.read();
                        jarInputDir.cursor++;
                        return read;
                    } else {
                        return -1;
                    }
                }
            };
        }
    }
}

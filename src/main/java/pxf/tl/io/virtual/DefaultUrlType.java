package pxf.tl.io.virtual;


import pxf.tl.help.New;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolLog;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author potatoxf
 */
public class DefaultUrlType {

    public static class Directory implements UrlType {
        /**
         * @param url
         * @return
         * @throws Exception
         */
        @Override
        public boolean matches(URL url) throws Exception {
            if (url.getProtocol().equals("file") && !Whether.hasJarFileInPath(url)) {
                File file = New.file(url);
                return file != null && file.isDirectory();
            }
            return false;
        }

        /**
         * @param url
         * @return
         * @throws Exception
         */
        @Override
        public VirtualDirectory createDir(URL url) throws Exception {
            return new SystemVirtualDirectory(New.file(url));
        }
    }

    /**
     * @author potatoxf
     */
    public static class file implements UrlType {
        @Override
        public boolean matches(URL url) throws Exception {
            return url.getProtocol().equals("file") && Whether.hasJarFileInPath(url);
        }

        @Override
        public VirtualDirectory createDir(URL url) throws Exception {
            return new ZipVirtualDirectory(new JarFile(New.file(url)));
        }
    }

    /**
     * @author potatoxf
     */
    public static class JarUrl implements UrlType {
        @Override
        public boolean matches(URL url) throws Exception {
            return ("jar".equals(url.getProtocol())
                    || "zip".equals(url.getProtocol())
                    || "wsjar".equals(url.getProtocol()))
                    && !Whether.hasInnerJarFileInPath(url);
        }

        @Override
        public VirtualDirectory createDir(URL url) throws Exception {
            try {
                URLConnection urlConnection = url.openConnection();
                if (urlConnection instanceof JarURLConnection) {
                    urlConnection.setUseCaches(false);
                    return new ZipVirtualDirectory(((JarURLConnection) urlConnection).getJarFile());
                }
            } catch (Throwable e) {
                /*fallback*/
            }
            File file = New.file(url);
            if (file != null) {
                return new ZipVirtualDirectory(new JarFile(file));
            }
            return null;
        }
    }

    /**
     * @author potatoxf
     */
    public static class JarInputStreamUrlType implements UrlType {
        public boolean matches(URL url) throws Exception {
            return url.toExternalForm().contains(".jar");
        }

        public VirtualDirectory createDir(final URL url) throws Exception {
            return new JarInputVirtualDirectory(url);
        }
    }

    /**
     * UrlType to be used by Reflections library. This class handles the vfszip and vfsfile protocol
     * of JBOSS files.
     *
     * <p>
     *
     * <p>to use it, register it in Vfs via {@link Vfs#addDefaultURLTypes(UrlType)} or {@link
     * Vfs#setDefaultURLTypes(java.util.List)}.
     *
     * @author potatoxf
     */
    public static class vfs implements UrlType {
        public static final String[] REPLACE_EXTENSION =
                new String[]{".ear/", ".jar/", ".war/", ".sar/", ".har/", ".par/"};

        private static final String VFSZIP = "vfszip";
        private static final String VFSFILE = "vfsfile";

        public boolean matches(URL url) {
            return VFSZIP.equals(url.getProtocol()) || VFSFILE.equals(url.getProtocol());
        }

        public VirtualDirectory createDir(final URL url) {
            try {
                URL adaptedUrl = adaptURL(url);
                return new ZipVirtualDirectory(new JarFile(adaptedUrl.getFile()));
            } catch (Exception e) {
                try {
                    return new ZipVirtualDirectory(new JarFile(url.getFile()));
                } catch (IOException ex) {
                    ToolLog.warn(ex, () -> "Could not get URL");
                }
            }
            return null;
        }

        public URL adaptURL(URL url) throws MalformedURLException {
            if (VFSZIP.equals(url.getProtocol())) {
                return replaceZipSeparators(url.getPath(), file -> file.exists() && file.isFile());
            } else if (VFSFILE.equals(url.getProtocol())) {
                return new URL(url.toString().replace(VFSFILE, "file"));
            } else {
                return url;
            }
        }

        URL replaceZipSeparators(String path, Predicate<File> acceptFile) throws MalformedURLException {
            int pos = 0;
            while (pos != -1) {
                pos = findFirstMatchOfDeployableExtention(path, pos);

                if (pos > 0) {
                    File file = new File(path.substring(0, pos - 1));
                    if (acceptFile.test(file)) {
                        return replaceZipSeparatorStartingFrom(path, pos);
                    }
                }
            }

            throw new RuntimeException(
                    "Unable to identify the real zip file in path '" + path + "'.");
        }

        int findFirstMatchOfDeployableExtention(String path, int pos) {
            Pattern p = Pattern.compile("\\.[ejprw]ar/");
            Matcher m = p.matcher(path);
            if (m.find(pos)) {
                return m.end();
            } else {
                return -1;
            }
        }

        URL replaceZipSeparatorStartingFrom(String path, int pos) throws MalformedURLException {
            String zipFile = path.substring(0, pos - 1);
            String zipPath = path.substring(pos);

            int numSubs = 1;
            for (String ext : REPLACE_EXTENSION) {
                while (zipPath.contains(ext)) {
                    zipPath = zipPath.replace(ext, ext.substring(0, 4) + "!");
                    numSubs++;
                }
            }

            String prefix = "";
            for (int i = 0; i < numSubs; i++) {
                prefix += "zip:";
            }

            if (zipPath.trim().length() == 0) {
                return new URL(prefix + "/" + zipFile);
            } else {
                return new URL(prefix + "/" + zipFile + "!" + zipPath);
            }
        }
    }
}

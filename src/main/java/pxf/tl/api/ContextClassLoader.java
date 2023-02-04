package pxf.tl.api;

import pxf.tl.util.ToolLog;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * A class to wrap access to multiple class loaders making them work as one
 *
 * @author potatoxf
 */
public class ContextClassLoader {
    private static final ClassLoader[] DEFAULT_CLASS_LOADER = getDefaultClassLoader();
    static final ContextClassLoader DEFAULT_INSTANCE = new ContextClassLoader(DEFAULT_CLASS_LOADER);
    private final ClassLoader[] classLoaders;

    /**
     * @param classLoaders {@code ClassLoader[]}
     */
    private ContextClassLoader(ClassLoader[] classLoaders) {
        this.classLoaders = classLoaders;
    }

    /**
     * 获取默认的类加载器
     *
     * @return {@code ClassLoader[]}
     */
    private static ClassLoader[] getDefaultClassLoader() {
        ClassLoader classLoader = null;
        try {
            classLoader = ClassLoader.getSystemClassLoader();
        } catch (SecurityException ignored) {
            // AccessControlException on Google App Engine
        }
        if (classLoader != null) {
            return new ClassLoader[]{classLoader, classLoader.getParent()};
        }
        throw new RuntimeException("No find default class loader");
    }

    /**
     * @param classLoaders {@code ClassLoader[]}
     * @return {@code ContextClassLoader}
     */
    public static ContextClassLoader of(ClassLoader... classLoaders) {
        if (classLoaders == null
                || classLoaders.length == 0
                || Arrays.equals(classLoaders, DEFAULT_CLASS_LOADER)) {
            return ContextClassLoader.DEFAULT_INSTANCE;
        }
        return new ContextClassLoader(classLoaders);
    }

    /**
     * Get a resource as a URL using the current class path
     *
     * @param resource - the resource to locate
     * @return the resource url
     */
    public URL getResourceAsUrl(String resource) throws IOException {
        return getResourceAsUrl(resource, getClassLoaders(null));
    }

    /**
     * Get a resource from the classpath, starting with a specific class loader
     *
     * @param resource    - the resource to find
     * @param classLoader - the first classloader to try
     * @return the resource url
     */
    public URL getResourceAsUrl(String resource, ClassLoader classLoader) throws IOException {
        return getResourceAsUrl(resource, getClassLoaders(classLoader));
    }

    /**
     * Returns the URL that contains a {@code Class}.
     *
     * <p>This searches for the class using {@link ClassLoader#getResource(String)}.
     *
     * <p>If the optional {@link ClassLoader}s are not specified
     *
     * @param clz - the class to locate
     * @return the URL containing the class, null if not found
     */
    public URL getClassAsUrl(Class<?> clz) {
        return getClassAsUrl(clz, getClassLoaders(null));
    }

    /**
     * Returns the URL that contains a {@code Class}.
     *
     * <p>This searches for the class using {@link ClassLoader#getResource(String)}.
     *
     * <p>If the optional {@link ClassLoader}s are not specified
     *
     * @param clz         - the class to locate
     * @param classLoader - the first classloader to try
     * @return the URL containing the class, null if not found
     */
    public URL getClassAsUrl(Class<?> clz, ClassLoader classLoader) {
        return getClassAsUrl(clz, getClassLoaders(classLoader));
    }

    /**
     * Get a resource as a URL using the current class path
     *
     * @param resource - the resource to locate
     * @return the resources
     */
    public Collection<URL> getResourceAsUrls(String resource) throws IOException {
        return getResourceAsUrls(resource, getClassLoaders(null));
    }

    /**
     * Get a resource from the classpath, starting with a specific class loader
     *
     * @param resource    - the resource to find
     * @param classLoader - the first classloader to try
     * @return the resources
     */
    public Collection<URL> getResourceAsUrls(String resource, ClassLoader classLoader)
            throws IOException {
        return getResourceAsUrls(resource, getClassLoaders(classLoader));
    }

    /**
     * Get a resource from the classpath
     *
     * @param resource - the resource to find
     * @return the stream or null
     */
    public InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(resource, getClassLoaders(null));
    }

    /**
     * Get a resource from the classpath, starting with a specific class loader
     *
     * @param resource    - the resource to find
     * @param classLoader - the first class loader to try
     * @return the stream or null
     */
    public InputStream getResourceAsStream(String resource, ClassLoader classLoader)
            throws IOException {
        return getResourceAsStream(resource, getClassLoaders(classLoader));
    }

    /**
     * Load a class on the classpath (or die trying)
     *
     * @param name - the class to look for
     * @return - the class or null
     */
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, getClassLoaders(null));
    }

    /**
     * Load a class on the classpath (or die trying)
     *
     * @param name - the class to look for
     * @return - the class or null
     */
    public Class<?> loadClass(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return loadClass(name, getClassLoaders(classLoader));
    }

    /**
     * Find a class on the classpath (or die trying)
     *
     * @param name - the class to look for
     * @return - the class or null
     */
    public Class<?> classForName(String name) throws ClassNotFoundException {
        return classForName(name, true, getClassLoaders(null));
    }

    /**
     * Find a class on the classpath (or die trying)
     *
     * @param name       - the class to look for
     * @param initialize - the class initialize
     * @return - the class or null
     */
    public Class<?> classForName(String name, boolean initialize) throws ClassNotFoundException {
        return classForName(name, initialize, getClassLoaders(null));
    }

    /**
     * Find a class on the classpath, starting with a specific classloader (or die trying)
     *
     * @param name        - the class to look for
     * @param classLoader - the first classloader to try
     * @return - the class or null
     */
    public Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return classForName(name, true, getClassLoaders(classLoader));
    }

    /**
     * Find a class on the classpath, starting with a specific classloader (or die trying)
     *
     * @param name        - the class to look for
     * @param initialize  - the class initialize
     * @param classLoader - the first classloader to try
     * @return - the class or null
     */
    public Class<?> classForName(String name, boolean initialize, ClassLoader classLoader)
            throws ClassNotFoundException {
        return classForName(name, initialize, getClassLoaders(classLoader));
    }

    /**
     * Returns the URL that contains a {@code Class}.
     *
     * <p>This searches for the class using {@link ClassLoader#getResource(String)}.
     *
     * <p>If the optional {@link ClassLoader}s are not specified
     *
     * @param clz          - the class to locate
     * @param classLoaders - the class loaders to examine
     * @return the URL containing the class, null if not found
     */
    private static URL getClassAsUrl(Class<?> clz, ClassLoader[] classLoaders) {
        final String resourceName = clz.getName().replace(".", "/") + ".class";
        for (ClassLoader classLoader : classLoaders) {
            try {
                @Nullable final URL url = classLoader.getResource(resourceName);
                if (url != null) {
                    @Nullable final String normalizedUrl =
                            url.toExternalForm()
                                    .substring(
                                            0,
                                            url.toExternalForm()
                                                    .lastIndexOf(clz.getPackage().getName().replace(".", "/")));
                    return new URL(normalizedUrl);
                }
            } catch (MalformedURLException e) {
                ToolLog.warn(e, () -> "Could not get URL");
            }
        }
        return null;
    }

    /**
     * Get a resource as a URL using the current class path
     *
     * @param resource     - the resource to locate
     * @param classLoaders - the class loaders to examine
     * @return the resource url
     */
    private URL getResourceAsUrl(String resource, ClassLoader[] classLoaders) throws IOException {
        if (resource != null && classLoaders != null) {
            URL url;
            for (ClassLoader classLoader : classLoaders) {
                if (null != classLoader) {
                    // look for the resource as passed in...
                    url = classLoader.getResource(resource);
                    // ...but some class loaders want this leading "/", so we'll add it
                    // and try aget if we didn't find the resource
                    if (null == url) {
                        url = classLoader.getResource("/" + resource);
                    }
                    // "It's always in the last place I look for it!"
                    // ... because only an idiot would keep looking for it after finding it, so stop looking
                    // already.
                    if (null != url) {
                        return url;
                    }
                }
            }
        }
        // didn't find it anywhere.
        throw new IOException("No find resource url for [" + resource + "]");
    }

    /**
     * Try to get a resource from a group of classLoaders
     *
     * @param resource    - the resource to get
     * @param classLoader - the classLoaders to examine
     * @return the resources
     */
    private Collection<URL> getResourceAsUrls(String resource, ClassLoader[] classLoader)
            throws IOException {
        Set<URL> resources = new LinkedHashSet<>();
        if (classLoader != null) {
            for (ClassLoader cl : classLoader) {
                if (null != cl) {
                    try {
                        Enumeration<URL> urlEnumeration = cl.getResources(resource);
                        while (urlEnumeration.hasMoreElements()) {
                            URL url = urlEnumeration.nextElement();
                            int index = url.toExternalForm().lastIndexOf(resource);
                            if (index != -1) {
                                // Add old url as contextUrl to support exotic url handlers
                                resources.add(new URL(url, url.toExternalForm().substring(0, index)));
                            } else {
                                resources.add(url);
                            }
                        }
                    } catch (IOException e) {
                        ToolLog.warn(e, () -> "Could not get URL");
                    }
                    if (resources.isEmpty()) {
                        try {
                            Enumeration<URL> urlEnumeration = cl.getResources("/" + resource);
                            while (urlEnumeration.hasMoreElements()) {
                                URL url = urlEnumeration.nextElement();
                                int index = url.toExternalForm().lastIndexOf(resource);
                                if (index != -1) {
                                    // Add old url as contextUrl to support exotic url handlers
                                    resources.add(new URL(url, url.toExternalForm().substring(0, index)));
                                } else {
                                    resources.add(url);
                                }
                            }
                        } catch (IOException e) {
                            ToolLog.warn(e, () -> "Could not get URL");
                        }
                    }
                }
            }
        }
        if (resources.isEmpty()) {
            throw new IOException("No find resource url for [" + resource + "]");
        }
        return resources;
    }

    /**
     * Try to get a resource from a group of classLoaders
     *
     * @param resource    - the resource to get
     * @param classLoader - the classLoaders to examine
     * @return the resource url
     */
    private InputStream getResourceAsStream(String resource, ClassLoader[] classLoader)
            throws IOException {
        if (resource != null && classLoader != null) {
            for (ClassLoader cl : classLoader) {
                if (null != cl) {
                    // try to find the resource as passed
                    InputStream returnValue = cl.getResourceAsStream(resource);
                    // now, some class loaders want this leading "/", so we'll add it and try aget if we
                    // didn't
                    // find the resource
                    if (null == returnValue) {
                        returnValue = cl.getResourceAsStream("/" + resource);
                    }
                    if (null != returnValue) {
                        return returnValue;
                    }
                }
            }
        }
        throw new IOException("No find resource for [" + resource + "]");
    }

    /**
     * Load a class on the classpath (or die trying)
     *
     * @param name - the class to look for
     * @return - the class or null
     */
    private Class<?> loadClass(String name, ClassLoader[] classLoader) throws ClassNotFoundException {
        for (ClassLoader cl : classLoader) {
            if (null != cl) {
                try {
                    return cl.loadClass(name);
                } catch (ClassNotFoundException e) {
                    // we'll ignore this until all classLoaders fail to locate the class
                }
            }
        }
        throw new ClassNotFoundException("No find class for [" + name + "]");
    }

    /**
     * Attempt to load a class from a group of classLoaders
     *
     * @param name        - the class to load
     * @param initialize  - the class initialize
     * @param classLoader - the group of classLoaders to examine
     * @return the class or null
     */
    private Class<?> classForName(String name, boolean initialize, ClassLoader[] classLoader)
            throws ClassNotFoundException {
        for (ClassLoader cl : classLoader) {
            if (null != cl) {
                try {
                    return Class.forName(name, true, cl);
                } catch (ClassNotFoundException e) {
                    // we'll ignore this until all classLoaders fail to locate the class
                }
            }
        }
        throw new ClassNotFoundException("No find class for [" + name + "]");
    }

    private ClassLoader[] getClassLoaders(ClassLoader classLoader) {
        if (classLoaders == null) {
            if (classLoader == null) {
                return new ClassLoader[]{
                        Thread.currentThread().getContextClassLoader(), getClass().getClassLoader()
                };
            } else {
                return new ClassLoader[]{
                        classLoader, Thread.currentThread().getContextClassLoader(), getClass().getClassLoader()
                };
            }
        }
        ClassLoader[] result;
        if (classLoader != null) {
            result = new ClassLoader[classLoaders.length + 3];
            result[0] = classLoader;
            System.arraycopy(classLoaders, 0, result, 1, classLoaders.length);
            return result;
        } else {
            result = new ClassLoader[classLoaders.length + 2];
            System.arraycopy(classLoaders, 0, result, 0, classLoaders.length);
        }
        result[result.length - 2] = Thread.currentThread().getContextClassLoader();
        result[result.length - 1] = getClass().getClassLoader();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContextClassLoader that = (ContextClassLoader) o;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(classLoaders, that.classLoaders);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(classLoaders);
    }
}

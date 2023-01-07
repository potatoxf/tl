package pxf.tl.api;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * A class to simplify access to resources through the classLoader.
 *
 * @author potatoxf
 */
public class ContextResource {
    static final ContextResource DEFAULT_INSTANCE =
            new ContextResource(ContextClassLoader.DEFAULT_INSTANCE, Charset.defaultCharset());
    private final ContextClassLoader contextClassLoader;
    private final Charset charset;

    /**
     * @param contextClassLoader THe class to wrap access to multiple class loaders
     * @param charset            The charset
     */
    private ContextResource(ContextClassLoader contextClassLoader, Charset charset) {
        this.contextClassLoader = contextClassLoader;
        this.charset = charset;
    }

    /**
     * @param contextClassLoader THe class to wrap access to multiple class loaders
     * @param charset            The charset
     */
    public static ContextResource of(ContextClassLoader contextClassLoader, Charset charset) {
        if ((contextClassLoader == null
                || DEFAULT_INSTANCE.contextClassLoader.equals(contextClassLoader))
                && (charset == null || DEFAULT_INSTANCE.charset.equals(charset))) {
            return DEFAULT_INSTANCE;
        }
        return new ContextResource(contextClassLoader, charset);
    }

    /**
     * Returns a resource on the classpath as a Properties object
     *
     * @param resource The resource to find
     * @return The resource
     */
    public Properties getResourceAsProperties(String resource) throws IOException {
        return getResourceAsProperties(resource, null);
    }

    /**
     * Returns a resource on the classpath as a Properties object
     *
     * @param loader   The classLoader used to fetch the resource
     * @param resource The resource to find
     * @return The resource
     */
    public Properties getResourceAsProperties(String resource, ClassLoader loader)
            throws IOException {
        Properties props = new Properties();
        props.load(getResourceAsStream(resource, loader));
        return props;
    }

    /**
     * Returns a resource on the classpath as a Reader object
     *
     * @param resource The resource to find
     * @return The resource or null
     */
    public Reader getResourceAsReader(String resource) throws IOException {
        InputStream in = getResourceAsStream(resource);
        if (charset == null) {
            return new InputStreamReader(in);
        } else {
            return new InputStreamReader(in, charset);
        }
    }

    /**
     * Returns a resource on the classpath as a Reader object
     *
     * @param resource The resource to find
     * @param loader   The classLoader used to fetch the resource
     * @return The resource or null
     */
    public Reader getResourceAsReader(String resource, ClassLoader loader) throws IOException {
        InputStream in = getResourceAsStream(resource, loader);
        if (charset == null) {
            return new InputStreamReader(in);
        } else {
            return new InputStreamReader(in, charset);
        }
    }

    /**
     * Returns a resource on the classpath as a Stream object
     *
     * @param resource The resource to find
     * @return The resource or null
     */
    public InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(resource, null);
    }

    /**
     * Returns a resource on the classpath as a Stream object
     *
     * @param resource The resource to find
     * @param loader   The classLoader used to fetch the resource
     * @return The resource or null
     */
    public InputStream getResourceAsStream(String resource, ClassLoader loader) throws IOException {
        return contextClassLoader.getResourceAsStream(resource, loader);
    }

    /**
     * Returns a resource on the classpath as a File object
     *
     * @param resource The resource to find
     * @return The resource or null
     */
    public File getResourceAsFile(String resource) throws IOException {
        return new File(getResourceUrl(resource).getFile());
    }

    /**
     * Returns a resource on the classpath as a File object
     *
     * @param resource - the resource to find
     * @param loader   - the classLoader used to fetch the resource
     * @return The resource or null
     */
    public File getResourceAsFile(String resource, ClassLoader loader) throws IOException {
        return new File(getResourceUrl(resource, loader).getFile());
    }

    /**
     * Returns the URL of the resource on the classpath
     *
     * @param resource The resource to find
     * @return The resource or null
     */
    public URL getResourceUrl(String resource) throws IOException {
        return getResourceUrl(resource, null);
    }

    /**
     * Returns the URL of the resource on the classpath
     *
     * @param resource The resource to find
     * @param loader   The classLoader used to fetch the resource
     * @return The resource or null
     */
    public URL getResourceUrl(String resource, ClassLoader loader) throws IOException {
        return contextClassLoader.getResourceAsUrl(resource, loader);
    }

    /**
     * Gets a URL as a Reader
     *
     * @param urlString - the URL to get
     * @return A Reader with the data from the URL or null
     */
    public Reader getUrlAsReader(String urlString) throws IOException {
        InputStream in = getUrlAsStream(urlString);
        if (charset == null) {
            return new InputStreamReader(in);
        } else {
            return new InputStreamReader(in, charset);
        }
    }

    /**
     * Gets a URL as an input stream
     *
     * @param urlString - the URL to get
     * @return An input stream with the data from the URL or null
     */
    public InputStream getUrlAsStream(String urlString) throws IOException {
        return new URL(urlString).openConnection().getInputStream();
    }

    /**
     * Gets a URL as a Properties object
     *
     * @param urlString - the URL to get
     * @return A Properties object with the data from the URL
     */
    public Properties getUrlAsProperties(String urlString) throws IOException {
        Properties props = new Properties();
        if (urlString != null) {
            InputStream in = getUrlAsStream(urlString);
            if (in != null) {
                props.load(in);
            }
        }
        return props;
    }

    public Properties loadProperties(URL url) throws IOException {
        if (url != null) {
            try (InputStream in = url.openStream();
                 BufferedInputStream reader = new BufferedInputStream(in)) {
                Properties properties = new Properties();
                properties.load(reader);
                return properties;
            }
        }
        return null;
    }
}

package pxf.tl.lang;


import pxf.tl.api.ContextClassLoader;
import pxf.tl.function.FunctionThrow;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolCollection;
import pxf.tl.util.ToolArray;
import pxf.tl.util.ToolURL;
import pxf.tl.util.ToolZip;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarFile;

/**
 * @author potatoxf
 * @version $Rev$ $Date$
 */
public class ContextResourceFinder {
    private static final ContextResourceFinder DEFAULT =
            new ContextResourceFinder(null, null, (URL[]) null);
    private final URL[] urls;
    private final String path;
    private final ContextClassLoader classLoader;

    private ContextResourceFinder(String path, ContextClassLoader classLoader, URL... urls) {
        if (path == null) {
            path = "";
        } else if (path.length() > 0 && !path.endsWith("/")) {
            path += "/";
        }
        this.path = path;
        this.classLoader = classLoader;
        this.urls = ToolArray.filter(
                urls, (FunctionThrow<URL, URL, Throwable>) url -> {
                    if (url == null || isDirectory(url) || "jar".equals(url.getProtocol())) {
                        return null;
                    }
                    try {
                        return new URL("jar", "", -1, url + "!/");
                    } catch (MalformedURLException ignored) {
                    }
                    return null;
                });
    }

    public static ContextResourceFinder of(URL... urls) {
        return of(null, null, urls);
    }

    public static ContextResourceFinder of(String path) {
        return of(path, null, (URL[]) null);
    }

    public static ContextResourceFinder of(String path, URL... urls) {
        return of(path, null, urls);
    }

    public static ContextResourceFinder of(String path, ContextClassLoader classLoader) {
        return of(path, classLoader, (URL[]) null);
    }

    public static ContextResourceFinder of(String path, ContextClassLoader classLoader, URL... urls) {
        if (Whether.empty(path) && Whether.empty(urls) && classLoader == null) {
            return DEFAULT;
        }
        return new ContextResourceFinder(path, classLoader, (URL[]) null);
    }

    private static boolean isDirectory(URL url) {
        String file = url.getFile();
        return (file.length() > 0 && file.charAt(file.length() - 1) == '/');
    }

    private static void readDirectoryEntries(URL location, Map<String, URL> resources)
            throws MalformedURLException, UnsupportedEncodingException {
        File dir = new File(URLDecoder.decode(location.getPath(), StandardCharsets.UTF_8));
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (!file.isDirectory()) {
                    String name = file.getName();
                    URL url = file.toURI().toURL();
                    resources.put(name, url);
                }
            }
        }
    }

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Find String
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Find
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public URL find(String uri) throws IOException {
        String fullUrl = path + uri;
        URL resource = getResource(fullUrl);
        if (resource == null) {
            throw new IOException("Could not find resource '" + fullUrl + "'");
        }
        return resource;
    }

    public List<URL> findAll(String uri) throws IOException {
        return getResources(path + uri);
    }

    /**
     * Reads the contents of the URL as a {@link String}'s and returns it.
     *
     * @param uri URI
     * @return a stringified content of a resource
     * @throws IOException An exception occurs when reading dataif a resource pointed out by the uri
     *                     param could not be find
     * @see ClassLoader#getResource(String)
     */
    public String findString(String uri) throws IOException {
        String fullUrl = path + uri;
        URL resource = getResource(fullUrl);
        if (resource == null) {
            throw new IOException("Could not find a resource in : " + fullUrl);
        }
        return ToolURL.loadContents(resource);
    }

    /**
     * Reads the contents of the found URLs as a list of {@link String}'s and returns them.
     *
     * @param uri URI
     * @return a list of the content of each resource URL found
     * @throws IOException An exception occurs when reading dataif any of the found URLs are unable to
     *                     be read.
     */
    public List<String> findAllStrings(String uri) throws IOException {
        String fullUrl = path + uri;
        List<String> strings = new ArrayList<String>();
        Collection<URL> resources = getResources(fullUrl);
        for (URL url : resources) {
            String string = ToolURL.loadContents(url);
            strings.add(string);
        }
        return strings;
    }

    /**
     * Reads the contents of the found URLs as a Strings and returns them. Individual URLs that cannot
     * be read are skipped and added to the list of 'resourcesNotLoaded'
     *
     * @param uri URI
     * @return {@code Resource<List<String>>} a list of the content of each resource URL found
     * @throws IOException An exception occurs when reading data
     */
    public Resource<List<String>> findAvailableStrings(String uri) throws IOException {
        final List<String> resourcesNotLoaded = new ArrayList<String>();
        List<String> strings = new ArrayList<String>();
        Collection<URL> resources = getResources(path + uri);
        for (URL url : resources) {
            try {
                String string = ToolURL.loadContents(url);
                strings.add(string);
            } catch (IOException notAvailable) {
                resourcesNotLoaded.add(url.toExternalForm());
            }
        }
        return new Resource<List<String>>(strings, resourcesNotLoaded);
    }
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Find Class<?>
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * Reads the contents of all non-directory URLs immediately under the specified location and
     * returns them in a proxyMap keyed by the file name.
     *
     * <p>Any URLs that cannot be read will cause an exception to be thrown.
     *
     * <p>Example classpath:
     *
     * <p>META-INF/serializables/one META-INF/serializables/two META-INF/serializables/three
     * META-INF/serializables/four/foo.txt
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); Map proxyMap =
     * finder.mapAvailableStrings("serializables"); proxyMap.contains("one"); // true
     * proxyMap.contains("two"); // true proxyMap.contains("three"); // true
     * proxyMap.contains("four"); // false
     *
     * @param uri URI
     * @return {@code Map<String, String>} a list of the content of each resource URL found
     * @throws IOException An exception occurs when reading dataif any of the urls cannot be read
     */
    public Map<String, String> mapAllStrings(String uri) throws IOException {
        return ToolCollection.processMapValueThrow(getResourcesMap(uri), ToolURL::loadContents);
    }

    /**
     * Reads the contents of all non-directory URLs immediately under the specified location and
     * returns them in a proxyMap keyed by the file name.
     *
     * <p>Individual URLs that cannot be read are skipped and added to the list of
     * 'resourcesNotLoaded'
     *
     * <p>Example classpath:
     *
     * <p>META-INF/serializables/one META-INF/serializables/two # not readable
     * META-INF/serializables/three META-INF/serializables/four/foo.txt
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); Map proxyMap =
     * finder.mapAvailableStrings("serializables"); proxyMap.contains("one"); // true
     * proxyMap.contains("two"); // false proxyMap.contains("three"); // true
     * proxyMap.contains("four"); // false
     *
     * @param uri URI
     * @return {@code Resource<Map<String, String>>} a list of the content of each resource URL found
     * @throws IOException An exception occurs when reading data
     */
    public Resource<Map<String, String>> mapAvailableStrings(String uri) throws IOException {
        final List<String> resourcesNotLoaded = new ArrayList<String>();
        Map<String, String> map =
                ToolCollection.processMapValue(
                        getResourcesMap(uri),
                        (FunctionThrow<URL, String, IOException>)
                                p -> {
                                    try {
                                        return ToolURL.loadContents(p);
                                    } catch (IOException notAvailable) {
                                        resourcesNotLoaded.add(p.toExternalForm());
                                        return null;
                                    }
                                });
        return new Resource<Map<String, String>>(map, resourcesNotLoaded);
    }

    /**
     * Executes {@link #findString(String)} assuming the contents URL found is the name of a class
     * that should be loaded and returned.
     *
     * @param uri URI
     * @return Class<?>
     * @throws IOException            当读取数据发生异常
     * @throws ClassNotFoundException 未找到类
     */
    public Class<?> findClass(String uri) throws IOException, ClassNotFoundException {
        String className = findString(uri);
        return classLoader.loadClass(className);
    }

    /**
     * Executes findAllStrings assuming the strings are the names of a classes that should be loaded
     * and returned.
     *
     * <p>Any URL or class that cannot be loaded will cause an exception to be thrown.
     *
     * @param uri URI
     * @return List<Class < ?>>
     * @throws IOException            An exception occurs when reading data
     * @throws ClassNotFoundException 未找到类
     */
    public List<Class<?>> findAllClasses(String uri) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        List<String> strings = findAllStrings(uri);
        for (String className : strings) {
            Class<?> clazz = classLoader.loadClass(className);
            classes.add(clazz);
        }
        return classes;
    }

    /**
     * Executes findAvailableStrings assuming the strings are the names of a classes that should be
     * loaded and returned.
     *
     * <p>Any class that cannot be loaded will be skipped and placed in the 'resourcesNotLoaded'
     * collection.
     *
     * @param uri URI
     * @return {@code Resource<List<Class<?>>>}
     * @throws IOException An exception occurs when reading data
     */
    public Resource<List<Class<?>>> findAvailableClasses(String uri) throws IOException {
        Resource<List<String>> resource = findAvailableStrings(uri);
        final List<String> resourcesNotLoaded = resource.getFailurePath();
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (String className : resource.getData()) {
            try {
                Class<?> clazz = classLoader.loadClass(className);
                classes.add(clazz);
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(className);
            }
        }
        return new Resource<List<Class<?>>>(classes, resource.getFailurePath());
    }
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Find Implementation
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * Executes mapAllStrings assuming the value of each entry in the proxyMap is the name of a class
     * that should be loaded.
     *
     * <p>Any class that cannot be loaded will be cause an exception to be thrown.
     *
     * <p>Example classpath:
     *
     * <p>META-INF/xmlparsers/xerces META-INF/xmlparsers/crimson
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); Map proxyMap =
     * finder.mapAvailableStrings("xmlparsers"); proxyMap.contains("xerces"); // true
     * proxyMap.contains("crimson"); // true Class<?> xercesClass = proxyMap.get("xerces"); Class<?>
     * crimsonClass = proxyMap.get("crimson");
     *
     * @param uri URI
     * @return Map<String, Class < ?>>
     * @throws IOException            当读取数据发生异常
     * @throws ClassNotFoundException 未找到类
     */
    public Map<String, Class<?>> mapAllClasses(String uri)
            throws IOException, ClassNotFoundException {
        return ToolCollection.processMapValueThrow(mapAllStrings(uri), classLoader::loadClass);
    }

    /**
     * Executes mapAvailableStrings assuming the value of each entry in the proxyMap is the name of a
     * class that should be loaded.
     *
     * <p>Any class that cannot be loaded will be skipped and placed in the 'resourcesNotLoaded'
     * collection.
     *
     * <p>Example classpath:
     *
     * <p>META-INF/xmlparsers/xerces META-INF/xmlparsers/crimson
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); Map proxyMap =
     * finder.mapAvailableStrings("xmlparsers"); proxyMap.contains("xerces"); // true
     * proxyMap.contains("crimson"); // true Class<?> xercesClass = proxyMap.get("xerces"); Class<?>
     * crimsonClass = proxyMap.get("crimson");
     *
     * @param uri URI
     * @return {@code Resource<Map<String, Class<?>>>}
     * @throws IOException An exception occurs when reading data
     */
    public Resource<Map<String, Class<?>>> mapAvailableClasses(String uri) throws IOException {
        Resource<Map<String, String>> resource = mapAvailableStrings(uri);
        final List<String> resourcesNotLoaded = resource.getFailurePath();
        Map<String, Class<?>> map =
                ToolCollection.processMapValue(
                        resource.getData(),
                        (FunctionThrow<String, Class<?>, IOException>)
                                p -> {
                                    try {
                                        return classLoader.loadClass(p);
                                    } catch (Exception notAvailable) {
                                        resourcesNotLoaded.add(p);
                                        return null;
                                    }
                                });
        return new Resource<Map<String, Class<?>>>(map, resourcesNotLoaded);
    }

    /**
     * Assumes the class specified points to a file in the classpath that contains the name of a class
     * that implements or is a subclass of the specfied class.
     *
     * <p>Any class that cannot be loaded will be cause an exception to be thrown.
     *
     * <p>Example classpath:
     *
     * <p>META-INF/java.io.InputStream # contains the classname org.acme.AcmeInputStream
     * META-INF/java.io.OutputStream
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); Class<?> clazz =
     * finder.findImplementation(java.io.InputStream.class); clazz.getName(); // returns
     * "org.acme.AcmeInputStream"
     *
     * @param interfase a superclass or interface
     * @return Class<?>
     * @throws IOException            An exception occurs when reading data if the URL cannot be read
     * @throws ClassNotFoundException 未找到类 if the class found is not loadable
     * @throws ClassCastException     if the class found is not assignable to the specified superclass or
     *                                interface
     */
    public Class<?> findImplementation(Class<?> interfase)
            throws IOException, ClassNotFoundException {
        String className = findString(interfase.getName());
        Class<?> impl = classLoader.loadClass(className);
        if (!interfase.isAssignableFrom(impl)) {
            throw new ClassCastException("Class<?> not of type: " + interfase.getName());
        }
        return impl;
    }

    /**
     * Assumes the class specified points to a file in the classpath that contains the name of a class
     * that implements or is a subclass of the specfied class.
     *
     * <p>Any class that cannot be loaded or assigned to the specified interface will be cause an
     * exception to be thrown.
     *
     * <p>Example classpath:
     *
     * <p>META-INF/java.io.InputStream # contains the classname org.acme.AcmeInputStream
     * META-INF/java.io.InputStream # contains the classname org.widget.NeatoInputStream
     * META-INF/java.io.InputStream # contains the classname com.foo.BarInputStream
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); List classes =
     * finder.findAllImplementations(java.io.InputStream.class);
     * classes.contains("org.acme.AcmeInputStream"); // true
     * classes.contains("org.widget.NeatoInputStream"); // true
     * classes.contains("com.foo.BarInputStream"); // true
     *
     * @param interfase a superclass or interface
     * @return List<Class < ?>>
     * @throws IOException            An exception occurs when reading data if the URL cannot be read
     * @throws ClassNotFoundException 未找到类 if the class found is not loadable
     * @throws ClassCastException     if the class found is not assignable to the specified superclass or
     *                                interface
     */
    public List<Class<?>> findAllImplementations(Class<?> interfase)
            throws IOException, ClassNotFoundException {
        List<Class<?>> implementations = new ArrayList<Class<?>>();
        List<String> strings = findAllStrings(interfase.getName());
        for (String className : strings) {
            Class<?> impl = classLoader.loadClass(className);
            if (!interfase.isAssignableFrom(impl)) {
                throw new ClassCastException("Class<?> not of type: " + interfase.getName());
            }
            implementations.add(impl);
        }
        return implementations;
    }

    /**
     * Assumes the class specified points to a file in the classpath that contains the name of a class
     * that implements or is a subclass of the specfied class.
     *
     * <p>Any class that cannot be loaded or are not assignable to the specified class will be skipped
     * and placed in the 'resourcesNotLoaded' collection.
     *
     * <p>Example classpath:
     *
     * <p>META-INF/java.io.InputStream # contains the classname org.acme.AcmeInputStream
     * META-INF/java.io.InputStream # contains the classname org.widget.NeatoInputStream
     * META-INF/java.io.InputStream # contains the classname com.foo.BarInputStream
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); List classes =
     * finder.findAllImplementations(java.io.InputStream.class);
     * classes.contains("org.acme.AcmeInputStream"); // true
     * classes.contains("org.widget.NeatoInputStream"); // true
     * classes.contains("com.foo.BarInputStream"); // true
     *
     * @param interfase a superclass or interface
     * @return {@code Resource<List<Class<?>>>}
     * @throws IOException An exception occurs when reading data
     */
    public Resource<List<Class<?>>> findAvailableImplementations(Class<?> interfase)
            throws IOException {
        Resource<List<String>> resource = findAvailableStrings(interfase.getName());
        final List<String> resourcesNotLoaded = resource.getFailurePath();
        List<Class<?>> implementations = new ArrayList<Class<?>>();
        for (String className : resource.getData()) {
            try {
                Class<?> impl = classLoader.loadClass(className);
                if (interfase.isAssignableFrom(impl)) {
                    implementations.add(impl);
                } else {
                    resourcesNotLoaded.add(className);
                }
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(className);
            }
        }
        return new Resource<List<Class<?>>>(implementations, resourcesNotLoaded);
    }
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Find Properties
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * Assumes the class specified points to a directory in the classpath that holds files containing
     * the name of a class that implements or is a subclass of the specfied class.
     *
     * <p>Any class that cannot be loaded or assigned to the specified interface will be cause an
     * exception to be thrown.
     *
     * <p>Example classpath:
     *
     * <p>META-INF/java.net.URLStreamHandler/jar META-INF/java.net.URLStreamHandler/file
     * META-INF/java.net.URLStreamHandler/http
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); Map proxyMap =
     * finder.mapAllImplementations(java.net.URLStreamHandler.class); Class<?> jarUrlHandler =
     * proxyMap.get("jar"); Class<?> fileUrlHandler = proxyMap.get("file"); Class<?> httpUrlHandler =
     * proxyMap.get("http");
     *
     * @param interfase a superclass or interface
     * @return Map<String, Class < ?>>
     * @throws IOException            An exception occurs when reading data if the URL cannot be read
     * @throws ClassNotFoundException 未找到类 if the class found is not loadable
     * @throws ClassCastException     if the class found is not assignable to the specified superclass or
     *                                interface
     */
    public Map<String, Class<?>> mapAllImplementations(final Class<?> interfase)
            throws IOException, ClassNotFoundException {
        return ToolCollection.processMapValueThrow(
                mapAllStrings(interfase.getName()),
                p -> {
                    Class<?> impl = classLoader.loadClass(p);
                    if (!interfase.isAssignableFrom(impl)) {
                        throw new ClassCastException("Class<?> not of type: " + interfase.getName());
                    }
                    return impl;
                });
    }

    /**
     * Assumes the class specified points to a directory in the classpath that holds files containing
     * the name of a class that implements or is a subclass of the specfied class.
     *
     * <p>Any class that cannot be loaded or are not assignable to the specified class will be skipped
     * and placed in the 'resourcesNotLoaded' collection.
     *
     * <p>Example classpath:
     *
     * <p>META-INF/java.net.URLStreamHandler/jar META-INF/java.net.URLStreamHandler/file
     * META-INF/java.net.URLStreamHandler/http
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); Map proxyMap =
     * finder.mapAllImplementations(java.net.URLStreamHandler.class); Class<?> jarUrlHandler =
     * proxyMap.get("jar"); Class<?> fileUrlHandler = proxyMap.get("file"); Class<?> httpUrlHandler =
     * proxyMap.get("http");
     *
     * @param interfase a superclass or interface
     * @return {@code Resource<Map<String, Class<?>>>}
     * @throws IOException An exception occurs when reading data
     */
    public Resource<Map<String, Class<?>>> mapAvailableImplementations(final Class<?> interfase)
            throws IOException {
        Resource<Map<String, String>> resource = mapAvailableStrings(interfase.getName());
        final List<String> resourcesNotLoaded = resource.getFailurePath();
        Map<String, Class<?>> map =
                ToolCollection.processMapValue(
                        resource.getData(),
                        (FunctionThrow<String, Class<?>, ClassNotFoundException>)
                                p -> {
                                    try {
                                        Class<?> impl = classLoader.loadClass(p);
                                        if (interfase.isAssignableFrom(impl)) {
                                            return impl;
                                        } else {
                                            resourcesNotLoaded.add(p);
                                        }
                                    } catch (Exception notAvailable) {
                                        resourcesNotLoaded.add(p);
                                    }
                                    return null;
                                });
        return new Resource<Map<String, Class<?>>>(map, resourcesNotLoaded);
    }

    /**
     * Finds the corresponding resource and reads it in as a properties file
     *
     * <p>Example classpath:
     *
     * <p>META-INF/widget.properties
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); Properties
     * widgetProps = finder.findProperties("widget.properties");
     *
     * @param uri URI
     * @return Properties
     * @throws IOException An exception occurs when reading dataif the URL cannot be read or is not in
     *                     properties file format
     */
    public Properties findProperties(String uri) throws IOException {
        String fullUrl = path + uri;
        URL resource = getResource(fullUrl);
        if (resource == null) {
            throw new IOException("Could not find resource: " + fullUrl);
        }
        return ToolURL.loadProperties(resource);
    }

    /**
     * Finds the corresponding resources and reads them in as a properties files
     *
     * <p>Any URL that cannot be read in as a properties file will cause an exception to be thrown.
     *
     * <p>Example classpath:
     *
     * <p>META-INF/app.properties META-INF/app.properties META-INF/app.properties
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); List<Properties>
     * appProps = finder.findAllProperties("app.properties");
     *
     * @param uri URI
     * @return List<Properties>
     * @throws IOException An exception occurs when reading dataif the URL cannot be read or is not in
     *                     properties file format
     */
    public List<Properties> findAllProperties(String uri) throws IOException {
        String fullUrl = path + uri;
        List<Properties> properties = new ArrayList<Properties>();
        Collection<URL> resources = getResources(fullUrl);
        for (URL url : resources) {
            Properties props = ToolURL.loadProperties(url);
            properties.add(props);
        }
        return properties;
    }

    /**
     * Finds the corresponding resources and reads them in as a properties files
     *
     * <p>Any URL that cannot be read in as a properties file will be added to the
     * 'resourcesNotLoaded' collection.
     *
     * <p>Example classpath:
     *
     * <p>META-INF/app.properties META-INF/app.properties META-INF/app.properties
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); List<Properties>
     * appProps = finder.findAvailableProperties("app.properties");
     *
     * @param uri URI
     * @return List<Properties>
     * @throws IOException An exception occurs when reading data
     */
    public Resource<List<Properties>> findAvailableProperties(String uri) throws IOException {
        final List<String> resourcesNotLoaded = new ArrayList<String>();
        String fullUrl = path + uri;
        List<Properties> properties = new ArrayList<Properties>();
        Collection<URL> resources = getResources(fullUrl);
        for (URL url : resources) {
            try {
                Properties props = ToolURL.loadProperties(url);
                properties.add(props);
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(url.toExternalForm());
            }
        }
        return new Resource<List<Properties>>(properties, resourcesNotLoaded);
    }

    /**
     * Finds the corresponding resources and reads them in as a properties files
     *
     * <p>Any URL that cannot be read in as a properties file will cause an exception to be thrown.
     *
     * <p>Example classpath:
     *
     * <p>META-INF/jdbcDrivers/oracle.sql.properties META-INF/jdbcDrivers/mysql.props
     * META-INF/jdbcDrivers/derby
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); List<Properties>
     * driversList = finder.findAvailableProperties("jdbcDrivers"); Properties oracleProps =
     * driversList.get("oracle.sql.properties"); Properties mysqlProps =
     * driversList.get("mysql.props"); Properties derbyProps = driversList.get("derby");
     *
     * @param uri URI
     * @return Map<String, Properties>
     * @throws IOException An exception occurs when reading dataif the URL cannot be read or is not in
     *                     properties file format
     */
    public Map<String, Properties> mapAllProperties(String uri) throws IOException {
        return ToolCollection.processMapValueThrow(getResourcesMap(uri), ToolURL::loadProperties);
    }

    /**
     * Finds the corresponding resources and reads them in as a properties files
     *
     * <p>Any URL that cannot be read in as a properties file will be added to the
     * 'resourcesNotLoaded' collection.
     *
     * <p>Example classpath:
     *
     * <p>META-INF/jdbcDrivers/oracle.sql.properties META-INF/jdbcDrivers/mysql.props
     * META-INF/jdbcDrivers/derby
     *
     * <p>ContextResourceFinder finder = new ContextResourceFinder("META-INF/"); List<Properties>
     * driversList = finder.findAvailableProperties("jdbcDrivers"); Properties oracleProps =
     * driversList.get("oracle.sql.properties"); Properties mysqlProps =
     * driversList.get("mysql.props"); Properties derbyProps = driversList.get("derby");
     *
     * @param uri URI
     * @return {@code Resource<Map<String, Properties>>}
     * @throws IOException An exception occurs when reading data
     */
    public Resource<Map<String, Properties>> mapAvailableProperties(String uri) throws IOException {
        final List<String> resourcesNotLoaded = new ArrayList<String>();
        Map<String, Properties> map =
                ToolCollection.processMapValueThrow(
                        getResourcesMap(uri),
                        (FunctionThrow<URL, Properties, IOException>)
                                p -> {
                                    try {
                                        return ToolURL.loadProperties(p);
                                    } catch (Exception notAvailable) {
                                        resourcesNotLoaded.add(p.toExternalForm());
                                    }
                                    return null;
                                });
        return new Resource<Map<String, Properties>>(map, resourcesNotLoaded);
    }

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Map Resources
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public Map<String, URL> getResourcesMap(String uri) throws IOException {
        String basePath = path + uri;
        Map<String, URL> resources = new HashMap<String, URL>();
        if (!basePath.endsWith("/")) {
            basePath += "/";
        }
        Collection<URL> urls = getResources(basePath);
        for (URL location : urls) {
            try {
                if ("jar".equals(location.getProtocol())) {
                    resources.putAll(ToolZip.loadJarEntryUrlInPath(location, basePath));
                } else if ("file".equals(location.getProtocol())) {
                    readDirectoryEntries(location, resources);
                }
            } catch (Exception ignored) {
            }
        }
        return resources;
    }

    private URL getResource(String fullUrl) throws IOException {
        if (urls == null) {
            return classLoader.getResourceAsUrl(fullUrl);
        }
        return findResource(fullUrl, urls);
    }

    private List<URL> getResources(String fullUrl) throws IOException {
        if (urls == null) {
            return new ArrayList<URL>(classLoader.getResourceAsUrls(fullUrl));
        }
        List<URL> resources = new ArrayList<URL>();
        for (URL url : urls) {
            URL resource = findResource(fullUrl, url);
            if (resource != null) {
                resources.add(resource);
            }
        }
        return resources;
    }

    private URL findResource(String resourceName, URL... search) {
        for (int i = 0; i < search.length; i++) {
            URL currentUrl = search[i];
            if (currentUrl == null) {
                continue;
            }
            try {
                String protocol = currentUrl.getProtocol();
                if ("jar".equals(protocol)) {
                    /*
                     * If the connection for currentUrl or resURL is
                     * used, getJarFile() will throw an exception if the
                     * entry doesn't exist.
                     */
                    URL jarURL = ((JarURLConnection) currentUrl.openConnection()).getJarFileURL();
                    JarFile jarFile;
                    JarURLConnection juc;
                    try {
                        juc =
                                (JarURLConnection)
                                        new URL("jar", "", jarURL.toExternalForm() + "!/").openConnection();
                        jarFile = juc.getJarFile();
                    } catch (IOException e) {
                        // Don't look for this jar file again
                        search[i] = null;
                        throw e;
                    }
                    try {
                        juc =
                                (JarURLConnection)
                                        new URL("jar", "", jarURL.toExternalForm() + "!/").openConnection();
                        jarFile = juc.getJarFile();
                        String entryName;
                        if (currentUrl.getFile().endsWith("!/")) {
                            entryName = resourceName;
                        } else {
                            String file = currentUrl.getFile();
                            int sepIdx = file.lastIndexOf("!/");
                            if (sepIdx == -1) {
                                // Invalid URL, don't look here again
                                search[i] = null;
                                continue;
                            }
                            sepIdx += 2;
                            entryName = file.substring(sepIdx) + resourceName;
                        }
                        if ("META-INF/".equals(entryName) && jarFile.getEntry("META-INF/MANIFEST.MF") != null) {
                            return ToolURL.merge(currentUrl, "META-INF/MANIFEST.MF");
                        }
                        if (jarFile.getEntry(entryName) != null) {
                            return ToolURL.merge(currentUrl, resourceName);
                        }
                    } finally {
                        if (!juc.getUseCaches()) {
                            try {
                                jarFile.close();
                            } catch (Exception ignored) {
                            }
                        }
                    }
                } else if ("file".equals(protocol)) {
                    String baseFile = currentUrl.getFile();
                    String host = currentUrl.getHost();
                    int hostLength = 0;
                    if (host != null) {
                        hostLength = host.length();
                    }
                    StringBuilder buf =
                            new StringBuilder(2 + hostLength + baseFile.length() + resourceName.length());
                    if (hostLength > 0) {
                        buf.append("//").append(host);
                    }
                    // baseFile always ends with '/'
                    buf.append(baseFile);
                    String fixedResName = resourceName;
                    // Do not make a UNC path, i.e. \\host
                    while (fixedResName.startsWith("/") || fixedResName.startsWith("\\")) {
                        fixedResName = fixedResName.substring(1);
                    }
                    buf.append(fixedResName);
                    String filename = buf.toString();
                    File file = new File(filename);
                    File file2 = new File(URLDecoder.decode(filename, StandardCharsets.UTF_8));
                    if (file.exists() || file2.exists()) {
                        return ToolURL.merge(currentUrl, fixedResName);
                    }
                } else {
                    URL resourceURL = ToolURL.merge(currentUrl, resourceName);
                    URLConnection urlConnection = resourceURL.openConnection();
                    try {
                        urlConnection.getInputStream().close();
                    } catch (SecurityException e) {
                        return null;
                    }
                    // HTTP can return a stream on a non-existent file
                    // So check for the return code;
                    if (!"http".equals(resourceURL.getProtocol())) {
                        return resourceURL;
                    }
                    int code = ((HttpURLConnection) urlConnection).getResponseCode();
                    if (code >= 200 && code < 300) {
                        return resourceURL;
                    }
                }
            } catch (SecurityException | IOException ignored) {
            }
        }
        return null;
    }

    public static class Resource<T> {
        private final T data;
        private final List<String> failurePath;

        Resource(T data, List<String> failurePath) {
            this.data = data;
            this.failurePath = failurePath;
        }

        public T getData() {
            return data;
        }

        public List<String> getFailurePath() {
            return failurePath;
        }
    }
}

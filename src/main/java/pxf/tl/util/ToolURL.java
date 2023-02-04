package pxf.tl.util;


import pxf.tl.api.Charsets;
import pxf.tl.exception.IORuntimeException;
import pxf.tl.exception.UtilException;
import pxf.tl.help.Assert;
import pxf.tl.help.New;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.io.resource.ResourceUtil;
import pxf.tl.net.url.UrlQuery;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;

/**
 * URL（Uniform Resource Locator）统一资源定位符相关工具类
 *
 * <p>统一资源定位符，描述了一台特定服务器上某资源的特定位置。 URL组成：
 *
 * <pre>
 *   协议://主机名[:端口]/ 路径/[:参数] [?查询]#Fragment
 *   protocol :// hostname[:port] / path / [:parameters][?query]#fragment
 * </pre>
 *
 * @author potatoxf
 */
public final class ToolURL {

    /**
     * 针对ClassPath路径的伪协议前缀（兼容Spring）: "classpath:"
     */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    /**
     * URL 前缀表示文件: "file:"
     */
    public static final String FILE_URL_PREFIX = "file:";
    /**
     * URL 前缀表示jar: "jar:"
     */
    public static final String JAR_URL_PREFIX = "jar:";
    /**
     * URL 前缀表示war: "war:"
     */
    public static final String WAR_URL_PREFIX = "war:";
    /**
     * URL 协议表示文件: "file"
     */
    public static final String URL_PROTOCOL_FILE = "file";
    /**
     * URL 协议表示Jar文件: "jar"
     */
    public static final String URL_PROTOCOL_JAR = "jar";
    /**
     * URL 协议表示zip文件: "zip"
     */
    public static final String URL_PROTOCOL_ZIP = "zip";
    /**
     * URL 协议表示WebSphere文件: "wsjar"
     */
    public static final String URL_PROTOCOL_WSJAR = "wsjar";
    /**
     * URL 协议表示JBoss zip文件: "vfszip"
     */
    public static final String URL_PROTOCOL_VFSZIP = "vfszip";
    /**
     * URL 协议表示JBoss文件: "vfsfile"
     */
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
    /**
     * URL 协议表示JBoss VFS资源: "vfs"
     */
    public static final String URL_PROTOCOL_VFS = "vfs";
    /**
     * Jar路径以及内部文件路径的分界符: "!/"
     */
    public static final String JAR_URL_SEPARATOR = "!/";
    /**
     * WAR路径及内部文件路径分界符
     */
    public static final String WAR_URL_SEPARATOR = "*/";

    public static String down(String href) throws InterruptedException {
        long begin_time = new Date().getTime();
        URL url = null;
        try {
            url = new URL(href);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        URLConnection conn = null;
        try {
            conn = url.openConnection();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String fileName = url.getFile();

        fileName = fileName.substring(fileName.lastIndexOf("/"));
        System.out.println("开始下载>>>");
        int fileSize = conn.getContentLength();
        System.out.println("文件总共大小：" + fileSize + "字节");

        // 设置分块大小
        int blockSize = 1024 * 1024;
        // 文件分块的数量
        int blockNum = fileSize / blockSize;

        if ((fileSize % blockSize) != 0) {
            blockNum += 1;
        }

        System.out.println("分块数->线程数：" + blockNum);

        Thread[] threads = new Thread[blockNum];
        for (int i = 0; i < blockNum; i++) {

            // 匿名函数对象需要用到的变量
            final int index = i;
            final int finalBlockNum = blockNum;
            final String finalFileName = fileName;

            // 创建一个线程
            threads[i] =
                    new Thread() {

                        public void run() {
                            URL url = null;
                            try {
                                url = new URL(href);
                            } catch (MalformedURLException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            try {

                                // 重新获取连接
                                URLConnection conn = url.openConnection();
                                // 重新获取流
                                InputStream in = conn.getInputStream();
                                // 定义起始和结束点
                                int beginPoint = 0, endPoint = 0;

                                System.out.print("第" + (index + 1) + "块文件：");
                                beginPoint = index * blockSize;

                                // 判断结束点
                                if (index < finalBlockNum - 1) {
                                    endPoint = beginPoint + blockSize;
                                } else {
                                    endPoint = fileSize;
                                }

                                System.out.println("起始字节数：" + beginPoint + ",结束字节数：" + endPoint);

                                // 将下载的文件存储到一个文件夹中
                                // 当该文件夹不存在时，则新建
                                File filePath = new File("C:\\dir\\");
                                if (!filePath.exists()) {
                                    filePath.mkdirs();
                                }
                                FileOutputStream fos =
                                        new FileOutputStream(new File("C:\\dir\\", finalFileName + "_" + (index + 1)));

                                // 跳过 beginPoint个字节进行读取
                                in.skip(beginPoint);
                                byte[] buffer = new byte[1024];
                                int count;
                                // 定义当前下载进度
                                int process = beginPoint;
                                // 当前进度必须小于结束字节数
                                while (process < endPoint) {

                                    count = in.read(buffer);
                                    // 判断是否读到最后一块
                                    if (process + count >= endPoint) {
                                        count = endPoint - process;
                                        process = endPoint;
                                    } else {
                                        // 计算当前进度
                                        process += count;
                                    }
                                    // 保存文件流
                                    fos.write(buffer, 0, count);
                                }
                                fos.close();
                                in.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
            threads[i].start();
        }

        // 当所有线程都结束时才开始文件的合并
        for (Thread t : threads) {
            t.join();
        }

        // 若该文件夹不存在，则创建一个文件夹
        File filePath = new File("C:\\dir\\");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        // 定义文件输出流
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("C:\\dir\\" + fileName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = 0; i < blockNum; i++) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream("C:\\dir\\" + fileName + "_" + (i + 1));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            byte[] buffer = new byte[1024];
            int count;
            try {
                while ((count = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long end_time = new Date().getTime();
        long seconds = (end_time - begin_time) / 1000;
        long minutes = seconds / 60;
        long second = seconds % 60;
        System.out.println("下载完成,用时：" + minutes + "分" + second + "秒");
        return "temp-rainy/" + fileName;
    }

    /**
     * 将{@link URI}转换为{@link URL}
     *
     * @param uri {@link URI}
     * @return URL对象
     * @throws UtilException {@link MalformedURLException}包装，URI格式有问题时抛出
     * @see URI#toURL()
     */
    public static URL url(URI uri) throws UtilException {
        if (null == uri) {
            return null;
        }
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new UtilException(e);
        }
    }

    /**
     * 通过一个字符串形式的URL地址创建URL对象
     *
     * @param url URL
     * @return URL对象
     */
    public static URL url(String url) {
        return url(url, null);
    }

    /**
     * 通过一个字符串形式的URL地址创建URL对象
     *
     * @param url     URL
     * @param handler {@link URLStreamHandler}
     * @return URL对象
     */
    public static URL url(String url, URLStreamHandler handler) {
        if (null == url) {
            return null;
        }

        // 兼容Spring的ClassPath路径
        if (url.startsWith(CLASSPATH_URL_PREFIX)) {
            url = url.substring(CLASSPATH_URL_PREFIX.length());
            return ToolBytecode.getClassLoader().getResource(url);
        }

        try {
            return new URL(null, url, handler);
        } catch (MalformedURLException e) {
            // 尝试文件路径
            try {
                return new File(url).toURI().toURL();
            } catch (MalformedURLException ex2) {
                throw new UtilException(e);
            }
        }
    }

    /**
     * 获取string协议的URL，类似于string:///xxxxx
     *
     * @param content 正文
     * @return URL
     */
    public static URI getStringURI(CharSequence content) {
        if (null == content) {
            return null;
        }
        final String contentStr = ToolString.addPrefixIfNot(content, "string:///");
        return URI.create(contentStr);
    }

    /**
     * 将URL字符串转换为URL对象，并做必要验证
     *
     * @param urlStr URL字符串
     * @return URL
     */
    public static URL toUrlForHttp(String urlStr) {
        return toUrlForHttp(urlStr, null);
    }

    /**
     * 将URL字符串转换为URL对象，并做必要验证
     *
     * @param urlStr  URL字符串
     * @param handler {@link URLStreamHandler}
     * @return URL
     */
    public static URL toUrlForHttp(String urlStr, URLStreamHandler handler) {
        Assert.notBlank(urlStr, "Url is blank !");
        // 编码空白符，防止空格引起的请求异常
        urlStr = encodeBlank(urlStr);
        try {
            return new URL(null, urlStr, handler);
        } catch (MalformedURLException e) {
            throw new UtilException(e);
        }
    }

    /**
     * 单独编码URL中的空白符，空白符编码为%20
     *
     * @param urlStr URL字符串
     * @return 编码后的字符串
     */
    public static String encodeBlank(CharSequence urlStr) {
        if (urlStr == null) {
            return null;
        }

        int len = urlStr.length();
        final StringBuilder sb = new StringBuilder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = urlStr.charAt(i);
            if (ToolChar.isBlankChar(c)) {
                sb.append("%20");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 获得URL
     *
     * @param pathBaseClassLoader 相对路径（相对于classes）
     * @return URL
     * @see ResourceUtil#getResource(String)
     */
    public static URL getURL(String pathBaseClassLoader) {
        return ResourceUtil.getResource(pathBaseClassLoader);
    }

    /**
     * 获得URL
     *
     * @param path  相对给定 class所在的路径
     * @param clazz 指定class
     * @return URL
     * @see ResourceUtil#getResource(String, Class)
     */
    public static URL getURL(String path, Class<?> clazz) {
        return ResourceUtil.getResource(path, clazz);
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param file URL对应的文件对象
     * @return URL
     * @throws UtilException MalformedURLException
     */
    public static URL getURL(File file) {
        Assert.notNull(file, "File is null !");
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new UtilException(e, "Error occured when get URL!");
        }
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param files URL对应的文件对象
     * @return URL
     * @throws UtilException MalformedURLException
     */
    public static URL[] getURLs(File... files) {
        final URL[] urls = new URL[files.length];
        try {
            for (int i = 0; i < files.length; i++) {
                urls[i] = files[i].toURI().toURL();
            }
        } catch (MalformedURLException e) {
            throw new UtilException(e, "Error occured when get URL!");
        }

        return urls;
    }

    /**
     * 获取URL中域名部分，只保留URL中的协议（Protocol）、Host，其它为null。
     *
     * @param url URL
     * @return 域名的URI
     */
    public static URI getHost(URL url) {
        if (null == url) {
            return null;
        }

        try {
            return new URI(url.getProtocol(), url.getHost(), null, null);
        } catch (URISyntaxException e) {
            throw new UtilException(e);
        }
    }

    /**
     * 补全相对路径
     *
     * @param baseUrl      基准URL
     * @param relativePath 相对URL
     * @return 相对路径
     * @throws UtilException MalformedURLException
     */
    public static String completeUrl(String baseUrl, String relativePath) {
        baseUrl = normalize(baseUrl, false);
        if (Whether.blank(baseUrl)) {
            return null;
        }

        try {
            final URL absoluteUrl = new URL(baseUrl);
            final URL parseUrl = new URL(absoluteUrl, relativePath);
            return parseUrl.toString();
        } catch (MalformedURLException e) {
            throw new UtilException(e);
        }
    }
    // -------------------------------------------------------------------------- decode

    /**
     * 解码URL<br>
     * 将%开头的16进制表示的内容解码。
     *
     * @param url URL
     * @return 解码后的URL
     * @throws UtilException UnsupportedEncodingException
     */
    public static String decode(String url) throws UtilException {
        return decode(url, Charsets.UTF_8);
    }

    /**
     * 解码application/x-www-form-urlencoded字符<br>
     * 将%开头的16进制表示的内容解码。
     *
     * @param content  被解码内容
     * @param charsets 编码，null表示不解码
     * @return 编码后的字符
     */
    public static String decode(String content, Charsets charsets) {
        Charset charset = null;
        if (charsets != null) {
            charset = charsets.get();
        }
        if (charset == null) {
            charset = Charsets.UTF_8.get();
        }
        return URLDecoder.decode(content, charset);
    }

    /**
     * 获得path部分<br>
     *
     * @param uriStr URI路径
     * @return path
     * @throws UtilException 包装URISyntaxException
     */
    public static String getPath(String uriStr) {
        return toURI(uriStr).getPath();
    }

    /**
     * 从URL对象中获取不被编码的路径Path<br>
     * 对于本地路径，URL对象的getPath方法对于包含中文或空格时会被编码，导致本读路径读取错误。<br>
     * 此方法将URL转为URI后获取路径用于解决路径被编码的问题
     *
     * @param url {@link URL}
     * @return 路径
     */
    public static String getDecodedPath(URL url) {
        if (null == url) {
            return null;
        }

        String path = null;
        try {
            // URL对象的getPath方法对于包含中文或空格的问题
            path = New.uri(url).getPath();
        } catch (UtilException e) {
            // ignore
        }
        return (null != path) ? path : url.getPath();
    }

    /**
     * 转字符串为URI
     *
     * @param location 字符串路径
     * @return URI
     * @throws UtilException 包装URISyntaxException
     */
    public static URI toURI(String location) throws UtilException {
        return toURI(location, false);
    }

    /**
     * 转字符串为URI
     *
     * @param location 字符串路径
     * @param isEncode 是否编码参数中的特殊字符（默认UTF-8编码）
     * @return URI
     * @throws UtilException 包装URISyntaxException
     */
    public static URI toURI(String location, boolean isEncode) throws UtilException {
        if (isEncode) {
//            location = encode(location);
        }
        try {
            return new URI(ToolString.trim(location));
        } catch (URISyntaxException e) {
            throw new UtilException(e);
        }
    }

    /**
     * 从URL中获取JarFile
     *
     * @param url URL
     * @return JarFile
     */
    public static JarFile getJarFile(URL url) {
        try {
            JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
            return urlConnection.getJarFile();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 标准化URL字符串，包括：
     *
     * <ol>
     *   <li>自动补齐“http://”头
     *   <li>去除开头的\或者/
     *   <li>替换\为/
     * </ol>
     *
     * @param url URL字符串
     * @return 标准化后的URL字符串
     */
    public static String normalize(String url) {
        return normalize(url, false);
    }

    /**
     * 标准化URL字符串，包括：
     *
     * <ol>
     *   <li>自动补齐“http://”头
     *   <li>去除开头的\或者/
     *   <li>替换\为/
     * </ol>
     *
     * @param url          URL字符串
     * @param isEncodePath 是否对URL中path部分的中文和特殊字符做转义（不包括 http:, /和域名部分）
     * @return 标准化后的URL字符串
     */
    public static String normalize(String url, boolean isEncodePath) {
        return normalize(url, isEncodePath, false);
    }

    /**
     * 标准化URL字符串，包括：
     *
     * <ol>
     *   <li>自动补齐“http://”头
     *   <li>去除开头的\或者/
     *   <li>替换\为/
     *   <li>如果replaceSlash为true，则替换多个/为一个
     * </ol>
     *
     * @param url          URL字符串
     * @param isEncodePath 是否对URL中path部分的中文和特殊字符做转义（不包括 http:, /和域名部分）
     * @param replaceSlash 是否替换url body中的 //
     * @return 标准化后的URL字符串
     */
    public static String normalize(String url, boolean isEncodePath, boolean replaceSlash) {
        if (Whether.blank(url)) {
            return url;
        }
        final int sepIndex = url.indexOf("://");
        String protocol;
        String body;
        if (sepIndex > 0) {
            protocol = ToolString.subPre(url, sepIndex + 3);
            body = ToolString.subSuf(url, sepIndex + 3);
        } else {
            protocol = "http://";
            body = url;
        }

        final int paramsSepIndex = ToolString.indexOf(body, '?');
        String params = null;
        if (paramsSepIndex > 0) {
            params = ToolString.subSuf(body, paramsSepIndex);
            body = ToolString.subPre(body, paramsSepIndex);
        }

        if (Whether.noEmpty(body)) {
            // 去除开头的\或者/
            //noinspection ConstantConditions
            body = body.replaceAll("^[\\\\/]+", ToolString.EMPTY);
            // 替换\为/
            body = body.replace("\\", "/");
            if (replaceSlash) {
                // issue#I25MZL@Gitee，双斜杠在URL中是允许存在的，默认不做替换
                body = body.replaceAll("//+", "/");
            }
        }

        final int pathSepIndex = ToolString.indexOf(body, '/');
        String domain = body;
        String path = null;
        if (pathSepIndex > 0) {
            domain = ToolString.subPre(body, pathSepIndex);
            path = ToolString.subSuf(body, pathSepIndex);
        }
        if (isEncodePath) {
//            path = encode(path);
        }
        return protocol + domain + Safe.value(path) + Safe.value(params);
    }

    /**
     * 将Map形式的Form表单数据转换为Url参数形式<br>
     * paramMap中如果key为空（null和""）会被忽略，如果value为null，会被做为空白符（""）<br>
     * 会自动url编码键和值
     *
     * <pre>
     * key1=v1&amp;key2=&amp;key3=v3
     * </pre>
     *
     * @param paramMap 表单数据
     * @param charset  编码，编码为null表示不编码
     * @return url参数
     */
    public static String buildQuery(Map<String, ?> paramMap, Charsets charset) {
        return UrlQuery.of(paramMap).build(charset);
    }

    /**
     * 获取指定URL对应资源的内容长度，对于Http，其长度使用Content-Length头决定。
     *
     * @param url URL
     * @return 内容长度，未知返回-1
     * @throws IORuntimeException IO异常
     */
    public static long getContentLength(URL url) throws IORuntimeException {
        if (null == url) {
            return -1;
        }

        URLConnection conn = null;
        try {
            conn = url.openConnection();
            return conn.getContentLengthLong();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (conn instanceof HttpURLConnection) {
                ((HttpURLConnection) conn).disconnect();
            }
        }
    }

    /**
     * Data URI Scheme封装，数据格式为Base64。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br>
     * 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>Data URI的格式规范：
     *
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param data     编码后的数据
     * @return Data URI字符串
     */
    public static String getDataUriBase64(String mimeType, String data) {
        return getDataUri(mimeType, null, "base64", data);
    }

    /**
     * Data URI Scheme封装。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br>
     * 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>Data URI的格式规范：
     *
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param encoding 数据编码方式（US-ASCII，BASE64等）
     * @param data     编码后的数据
     * @return Data URI字符串
     */
    public static String getDataUri(String mimeType, String encoding, String data) {
        return getDataUri(mimeType, null, encoding, data);
    }

    /**
     * Data URI Scheme封装。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br>
     * 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>Data URI的格式规范：
     *
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param charset  可选项（null表示无），源文本的字符集编码方式
     * @param encoding 数据编码方式（US-ASCII，BASE64等）
     * @param data     编码后的数据
     * @return Data URI字符串
     */
    public static String getDataUri(String mimeType, Charsets charset, String encoding, String data) {
        final StringBuilder builder = ToolString.builder("data:");
        if (Whether.noBlank(mimeType)) {
            builder.append(mimeType);
        }
        if (null != charset) {
            builder.append(";charset=").append(charset.name());
        }
        if (Whether.noBlank(encoding)) {
            builder.append(';').append(encoding);
        }
        builder.append(',').append(data);

        return builder.toString();
    }


    public static URL merge(URL base, String name) throws MalformedURLException {
        return new URL(base.getProtocol(), base.getHost(), base.getPort(), base.getFile() + name, null);
    }

    public static Properties loadProperties(URL resource) throws IOException {
        InputStream in = resource.openStream();
        try {
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } finally {
            ToolIO.closes(in);
        }
    }

    public static String loadContents(URL resource) throws IOException {
        InputStream in = resource.openStream();
        try {
            return ToolIO.readAllString(in).trim();
        } finally {
            ToolIO.closes(in);
        }
    }
}

package pxf.tl.api;

import lombok.Data;
import lombok.Getter;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.io.FileUtil;
import pxf.tl.net.NetUtil;
import pxf.tl.util.ToolArray;
import pxf.tl.util.ToolLog;
import pxf.tl.util.ToolRegex;
import pxf.tl.util.ToolString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntSupplier;

/**
 * @author potatoxf
 */
public interface JavaEnvironment {
    //------------------------------------------------------------------------------------------------------------------
    //基本判断
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 默认缓存大小 8192
     */
    int DEFAULT_BUFFER_SIZE =
            getSystemProperty("TOOL.DEFAULT_BUFFER_SIZE", 2 << 12, Integer::parseInt, (v, d) -> v >= d);
    /**
     * 默认中等缓存大小 16384
     */
    int DEFAULT_MIDDLE_BUFFER_SIZE =
            getSystemProperty("TOOL.DEFAULT_MIDDLE_BUFFER_SIZE", 2 << 13, Integer::parseInt, (v, d) -> v >= d);
    /**
     * 默认大缓存大小 32768
     */
    int DEFAULT_LARGE_BUFFER_SIZE =
            getSystemProperty("TOOL.DEFAULT_LARGE_BUFFER_SIZE", 2 << 14, Integer::parseInt, (v, d) -> v >= d);

    //------------------------------------------------------------------------------------------------------------------
    //基本判断
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The {@code java.util.prefs.PreferencesFactory} System Property. A class name.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_UTIL_PREFS_PREFERENCES_FACTORY =
            JavaEnvironment.getSystemProperty("java.util.prefs.PreferencesFactory");
    /**
     * The {@code java.awt.fonts} System Property.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_AWT_FONTS = JavaEnvironment.getSystemProperty("java.awt.fonts");
    /**
     * The {@code java.awt.graphicsenv} System Property.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_AWT_GRAPHICSENV = JavaEnvironment.getSystemProperty("java.awt.graphicsenv");
    /**
     * The {@code java.awt.headless} System Property. The value of this property is the String {@code
     * "true"} or {@code "false"}.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_AWT_HEADLESS = JavaEnvironment.getSystemProperty("java.awt.headless");
    /**
     * The {@code java.awt.printerjob} System Property.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_AWT_PRINTERJOB = JavaEnvironment.getSystemProperty("java.awt.printerjob");
    /**
     * The {@code java.class.path} System Property. Java class path.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_CLASS_PATH = JavaEnvironment.getSystemProperty("java.class.path");
    /**
     * The {@code java.class.version} System Property. Java class format version number.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_CLASS_VERSION = JavaEnvironment.getSystemProperty("java.class.version");
    /**
     * The {@code java.compiler} System Property. Name of JIT compiler to use. First in JDK version
     * 1.2. Not used in Sun JDKs after 1.2.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_COMPILER = JavaEnvironment.getSystemProperty("java.compiler");
    /**
     * The {@code java.endorsed.dirs} System Property. Path of endorsed directory or directories.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_ENDORSED_DIRS = JavaEnvironment.getSystemProperty("java.endorsed.dirs");
    /**
     * The {@code java.ext.dirs} System Property. Path of extension directory or directories.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_EXT_DIRS = JavaEnvironment.getSystemProperty("java.ext.dirs");
    /**
     * The {@code java.library.path} System Property. List of paths to search when loading libraries.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_LIBRARY_PATH = JavaEnvironment.getSystemProperty("java.library.path");
    /**
     * The {@code java.runtime.name} System Property. Java Runtime Environment name.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_RUNTIME_NAME = JavaEnvironment.getSystemProperty("java.runtime.name");
    /**
     * The {@code java.runtime.version} System Property. Java Runtime Environment version.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_RUNTIME_VERSION = JavaEnvironment.getSystemProperty("java.runtime.version");
    /**
     * The {@code java.specification.name} System Property. Java Runtime Environment specification
     * name.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_SPECIFICATION_NAME = JavaEnvironment.getSystemProperty("java.specification.name");
    /**
     * The {@code java.specification.vendor} System Property. Java Runtime Environment specification
     * vendor.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_SPECIFICATION_VENDOR =
            JavaEnvironment.getSystemProperty("java.specification.vendor");
    /**
     * The {@code java.specification.version} System Property. Java Runtime Environment specification
     * version.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_SPECIFICATION_VERSION =
            JavaEnvironment.getSystemProperty("java.specification.version");
    /**
     * The {@code java.vendor} System Property. Java vendor-specific string.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_VENDOR = JavaEnvironment.getSystemProperty("java.vendor");
    /**
     * The {@code java.vendor.url} System Property. Java vendor URL.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_VENDOR_URL = JavaEnvironment.getSystemProperty("java.vendor.url");
    /**
     * The {@code java.version} System Property. Java version number.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_VERSION = JavaEnvironment.getSystemProperty("java.version");
    /**
     * The {@code java.vm.info} System Property. Java Virtual Machine implementation info.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_VM_INFO = JavaEnvironment.getSystemProperty("java.vm.info");
    /**
     * The {@code java.vm.name} System Property. Java Virtual Machine implementation name.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_VM_NAME = JavaEnvironment.getSystemProperty("java.vm.name");
    /**
     * The {@code java.vm.specification.name} System Property. Java Virtual Machine specification
     * name.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_VM_SPECIFICATION_NAME =
            JavaEnvironment.getSystemProperty("java.vm.specification.name");
    /**
     * The {@code java.vm.specification.vendor} System Property. Java Virtual Machine specification
     * vendor.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_VM_SPECIFICATION_VENDOR =
            JavaEnvironment.getSystemProperty("java.vm.specification.vendor");
    /**
     * The {@code java.vm.specification.version} System Property. Java Virtual Machine specification
     * version.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_VM_SPECIFICATION_VERSION =
            JavaEnvironment.getSystemProperty("java.vm.specification.version");
    /**
     * The {@code java.vm.vendor} System Property. Java Virtual Machine implementation vendor.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_VM_VENDOR = JavaEnvironment.getSystemProperty("java.vm.vendor");
    /**
     * The {@code java.vm.version} System Property. Java Virtual Machine implementation version.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_VM_VERSION = JavaEnvironment.getSystemProperty("java.vm.version");
    /**
     * The {@code java.io.tmpdir} System Property. Default temp file path.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_IO_TMPDIR = JavaEnvironment.getSystemProperty("java.io.tmpdir");
    /**
     * The {@code java.home} System Property. Java installation directory.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String JAVA_HOME = JavaEnvironment.getSystemProperty("java.home");
    /**
     * The {@code awt.toolkit} System Property.
     *
     * <p>Holds a class name, on Windows XP this is {@code sun.awt.windows.WToolkit}.
     *
     * <p><b>On platforms without a GUI, this value is {@code null}.</b>
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String AWT_TOOLKIT = JavaEnvironment.getSystemProperty("awt.toolkit");

    //------------------------------------------------------------------------------------------------------------------
    //
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The {@code file.encoding} System Property.
     *
     * <p>File encoding, such as {@code Cp1252}.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String FILE_ENCODING = JavaEnvironment.getSystemProperty("file.encoding");
    /**
     * The {@code file.separator} System Property. The file separator is:
     *
     * <ul>
     *   <li>{@code "/"}</code> on UNIX
     *   <li>{@code "\"}</code> on Windows.
     * </ul>
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String FILE_SEPARATOR = JavaEnvironment.getSystemProperty("file.separator");
    /**
     * The {@code line.separator} System Property. Line separator (<code>&quot;\n&quot;</code> on
     * UNIX).
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String LINE_SEPARATOR = JavaEnvironment.getSystemProperty("line.separator");
    /**
     * The {@code path.separator} System Property. Path separator (<code>&quot;:&quot;</code> on
     * UNIX).
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String PATH_SEPARATOR = JavaEnvironment.getSystemProperty("path.separator");

    //------------------------------------------------------------------------------------------------------------------
    //
    //------------------------------------------------------------------------------------------------------------------

    /**
     * The {@code os.version} System Property. Operating system version.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String OS_VERSION = JavaEnvironment.getSystemProperty("os.version");
    /**
     * The {@code os.arch} System Property. Operating system architecture.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String OS_ARCH = JavaEnvironment.getSystemProperty("os.arch");
    /**
     * The {@code os.name} System Property. Operating system name.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String OS_NAME = JavaEnvironment.getSystemProperty("os.name");


    //------------------------------------------------------------------------------------------------------------------
    //
    //------------------------------------------------------------------------------------------------------------------


    /**
     * The {@code user.language} System Property. User's language code, such as {@code "en"}.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String USER_LANGUAGE = JavaEnvironment.getSystemProperty("user.language");
    /**
     * The {@code user.name} System Property. User's account name.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String USER_NAME = JavaEnvironment.getSystemProperty("user.name");
    /**
     * The {@code user.timezone} System Property. For example: {@code "America/Los_Angeles"}.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String USER_TIMEZONE = JavaEnvironment.getSystemProperty("user.timezone");
    /**
     * The {@code user.country} or {@code user.region} System Property. User's country code, such as
     * {@code GB}. First in Java version 1.2 as {@code user.region}. Renamed to {@code user.country}
     * in 1.4
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String USER_COUNTRY = gainSystemProperty("user.country", "user.region", null);
    /**
     * The {@code user.home} System Property. User's home directory.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String USER_HOME = JavaEnvironment.getSystemProperty("user.home");
    /**
     * The {@code user.dir} System Property. User's current working directory.
     *
     * <p>Defaults to {@code null} if the runtime does not have security access to read this property
     * or the property does not exist.
     *
     * <p>This value is initialized when the class is loaded. If {@link System#setProperty(String,
     * String)} or {@link System#setProperties(Properties)} is called after this class is
     * loaded, the value will be out of sync with that System property.
     */
    String USER_DIR = JavaEnvironment.getSystemProperty("user.dir");

    /**
     * 获取当前进程ID，首先获取进程名称，读取@前的ID值，如果不存在，则读取进程名的hash值
     */
    int PID = ((IntSupplier) () -> {
        final String processName = ManagementFactory.getRuntimeMXBean().getName();
        if (processName == null || processName.isBlank()) {
            ToolLog.warn(() -> "Process name is blank!");
        } else {
            final int atIndex = processName.indexOf('@');
            if (atIndex > 0) {
                return Integer.parseInt(processName.substring(0, atIndex));
            } else {
                return processName.hashCode();
            }
        }
        return PoolOfCommon.EOF;
    }).getAsInt();
    /**
     * 机器码
     */
    int MACHINE_PIECE = ((IntSupplier) () -> {
        // 机器码
        int machinePiece;
        try {
            StringBuilder netSb = new StringBuilder();
            // 返回机器所有的网络接口
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            // 遍历网络接口
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                // 网络接口信息
                netSb.append(ni.toString());
            }
            // 保留后两位
            machinePiece = netSb.toString().hashCode() << 16;
        } catch (Exception e) {
            // 出问题随机生成,保留后两位
            machinePiece = ThreadLocalRandom.current().nextInt() << 16;
        }
        return machinePiece;
    }).getAsInt();
    /**
     * 进程码
     */
    int PROCESS_PIECE = ((IntSupplier) () -> {
        // 进程码
        // 因为静态变量类加载可能相同,所以要获取进程ID + 加载对象的ID值
        int processPiece;
        // 进程ID初始化
        int processId = PID;
        if (processId < 0) {
            processId = ThreadLocalRandom.current().nextInt();
        }

        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        // 返回对象哈希码,无论是否重写hashCode方法
        int loaderId = (loader != null) ? System.identityHashCode(loader) : 0;

        // 进程ID + 对象加载ID
        // 保留前2位
        final String processSb = Integer.toHexString(processId) + Integer.toHexString(loaderId);
        processPiece = processSb.hashCode() & 0xFFFF;

        return processPiece;
    }).getAsInt();
    /**
     * 机器信息
     */
    int MACHINE = MACHINE_PIECE | PROCESS_PIECE;

    /**
     * 获取Java环境信息
     *
     * @return {@link JavaEnvironment.Info }
     */
    static Info currentInfo() {
        return new JavaEnvironment.Info();
    }

    /**
     * Gets the Java home directory as a {@code File}.
     *
     * @return a directory
     * @throws SecurityException if a security manager exists and its {@code checkPropertyAccess}
     *                           method doesn't allow access to the specified system property.
     * @see System#getProperty(String)
     */
    @Nonnull
    static File javaHomeFile() {
        return new File(JAVA_HOME);
    }

    /**
     * Gets the Java IO temporary directory as a {@code File}.
     *
     * @return a directory
     * @throws SecurityException if a security manager exists and its {@code checkPropertyAccess}
     *                           method doesn't allow access to the specified system property.
     * @see System#getProperty(String)
     */
    @Nonnull
    static File javaIoTmpDirFile() {
        return new File(JAVA_IO_TMPDIR);
    }

    /**
     * Gets the user directory as a {@code File}.
     *
     * @return a directory
     * @throws SecurityException if a security manager exists and its {@code checkPropertyAccess}
     *                           method doesn't allow access to the specified system property.
     * @see System#getProperty(String)
     */
    @Nonnull
    static File userDirFile() {
        return new File(USER_DIR);
    }

    /**
     * Gets the user home directory as a {@code File}.
     *
     * @return a directory
     * @throws SecurityException if a security manager exists and its {@code checkPropertyAccess}
     *                           method doesn't allow access to the specified system property.
     * @see System#getProperty(String)
     */
    @Nonnull
    static File userHomeFile() {
        return new File(USER_HOME);
    }

    //------------------------------------------------------------------------------------------------------------------
    //进程控制
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 销毁进程
     *
     * @param process 进程
     */
    static void destroy(Process process) {
        if (null != process) {
            process.destroy();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //JVM
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 增加一个JVM关闭后的钩子，用于在JVM关闭时执行某些操作
     *
     * @param hook 钩子
     */
    static void jvmAddShutdownHook(Runnable hook) {
        Runtime.getRuntime()
                .addShutdownHook((hook instanceof Thread) ? (Thread) hook : new Thread(hook));
    }

    /**
     * 获取JDK名称
     *
     * @return {@code String}
     */
    static String jvmName() {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    /**
     * 获得JVM可用的处理器数量（一般为CPU核心数）
     *
     * @return 可用的处理器数量
     */
    static int jvmAvailableProcessorCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 获取jvm总计内存
     *
     * @return jvm总计内存
     */
    static long jvmTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    /**
     * 获取jvm最大内存
     *
     * @return jvm最大内存
     */
    static long jvmMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    /**
     * 获取jvm空闲内存
     *
     * @return jvm空闲内存
     */
    static long jvmFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    /**
     * 获得JVM最大可用内存，计算方法为：<br>
     * 最大内存-总内存+剩余内存
     *
     * @return 最大可用内存
     */
    static long jvmUsableMemory() {
        return jvmMaxMemory() - jvmTotalMemory() + jvmFreeMemory();
    }

    //------------------------------------------------------------------------------------------------------------------
    //系统属性获取
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Gets a System property, defaulting to {@code null} if the property cannot be read.
     *
     * <p>If a {@code SecurityException} is caught, the return value is {@code null} and a message is
     * written to {@code System.err}.
     *
     * @param property the system property name
     * @return the system property value or {@code null} if a security problem occurs
     */
    static String get(String property) {
        return get(property, true);
    }

    /**
     * Gets a System property, defaulting to {@code null} if the property cannot be read.
     *
     * <p>If a {@code SecurityException} is caught, the return value is {@code null} and a message is
     * written to {@code System.err}.
     *
     * @param property the system property name
     * @return the system property value or {@code null} if a security problem occurs
     */
    static String get(String property, boolean quiet) {
        try {
            return getSystemProperty(property, null);
        } catch (Throwable e) {
            if (!quiet) {
                ToolLog.error(e,
                        () -> "Caught a SecurityException reading the system property '{}'; the SystemUtil property value will default to null.",
                        property);
            }
        }
        return null;
    }

    /**
     * Gets a System property, defaulting to {@code null} if the property cannot be read.
     *
     * <p>If a {@code SecurityException} is caught, the return value is {@code null} and a message is
     * written to {@code System.err}.
     *
     * @param property         the system property name
     * @param elsePropertyName the else system propertyName name
     * @return the system property value or {@code null} if a security problem occurs
     */
    @Nonnull
    static String gainSystemProperty(@Nonnull String property, @Nonnull String elsePropertyName) {
        return gainSystemProperty(property, elsePropertyName, PoolOfString.EMPTY);
    }

    /**
     * Gets a System property, defaulting to {@code null} if the property cannot be read.
     *
     * <p>If a {@code SecurityException} is caught, the return value is {@code null} and a message is
     * written to {@code System.err}.
     *
     * @param property         the system property name
     * @param elsePropertyName the else system propertyName name
     * @return the system property value or {@code null} if a security problem occurs
     */
    static String gainSystemProperty(@Nonnull String property, @Nonnull String elsePropertyName,
                                     String defaultValue) {
        return gainSystemProperty(property, elsePropertyName, defaultValue, s -> s);
    }

    /**
     * Gets a System propertyName, defaulting to {@code null} if the propertyName cannot be read.
     *
     * <p>If a {@code SecurityException} is caught, the return value is {@code null} and a message is
     * written to {@code System.err}.
     *
     * @param propertyName     the system propertyName name
     * @param elsePropertyName the else system propertyName name
     * @return the system propertyName value or {@code null} if a security problem occurs
     */
    static <T> T gainSystemProperty(
            @Nonnull String propertyName, @Nonnull String elsePropertyName, T defaultValue,
            @Nullable Function<String, T> handler) {
        return gainSystemProperty(propertyName, elsePropertyName, defaultValue, handler, null);
    }

    /**
     * Gets a System propertyName, defaulting to {@code null} if the propertyName cannot be read.
     *
     * <p>If a {@code SecurityException} is caught, the return value is {@code null} and a message is
     * written to {@code System.err}.
     *
     * @param propertyName     the system propertyName name
     * @param elsePropertyName the else system propertyName name
     * @return the system propertyName value or {@code null} if a security problem occurs
     */
    static <T> T gainSystemProperty(
            @Nonnull String propertyName, @Nonnull String elsePropertyName, T defaultValue,
            @Nullable Function<String, T> handler, @Nullable BiPredicate<T, T> predicate) {
        T systemProperty = getSystemProperty(propertyName, null, handler, predicate);
        if (systemProperty != null) {
            return systemProperty;
        }
        systemProperty = getSystemProperty(elsePropertyName, null, handler, predicate);
        if (systemProperty != null) {
            return systemProperty;
        }
        return defaultValue;
    }

    /**
     * Gets a System property, defaulting to {@code null} if the property cannot be read.
     *
     * <p>If a {@code SecurityException} is caught, the return value is {@code null} and a message is
     * written to {@code System.err}.
     *
     * @param property the system property name
     * @return the system property value or {@code null} if a security problem occurs
     */
    @Nonnull
    static String getSystemProperty(@Nonnull String property) {
        return getSystemProperty(property, PoolOfString.EMPTY);
    }

    /**
     * Gets a System property, defaulting to {@code null} if the property cannot be read.
     *
     * <p>If a {@code SecurityException} is caught, the return value is {@code null} and a message is
     * written to {@code System.err}.
     *
     * @param property the system property name
     * @return the system property value or {@code null} if a security problem occurs
     */
    static String getSystemProperty(@Nonnull String property, String defaultValue) {
        return getSystemProperty(property, defaultValue, s -> s);
    }

    /**
     * Gets a System propertyName, defaulting to {@code null} if the propertyName cannot be read.
     *
     * <p>If a {@code SecurityException} is caught, the return value is {@code null} and a message is
     * written to {@code System.err}.
     *
     * @param propertyName the system propertyName name
     * @return the system propertyName value or {@code null} if a security problem occurs
     */
    static <T> T getSystemProperty(
            @Nonnull String propertyName, T defaultValue,
            @Nullable Function<String, T> handler) {
        return getSystemProperty(propertyName, defaultValue, handler, null);
    }

    /**
     * Gets a System propertyName, defaulting to {@code null} if the propertyName cannot be read.
     *
     * <p>If a {@code SecurityException} is caught, the return value is {@code null} and a message is
     * written to {@code System.err}.
     *
     * @param propertyName the system propertyName name
     * @return the system propertyName value or {@code null} if a security problem occurs
     */
    static <T> T getSystemProperty(
            @Nonnull String propertyName, T defaultValue,
            @Nullable Function<String, T> handler, @Nullable BiPredicate<T, T> predicate) {
        try {
            String propertyValue = System.getProperty(propertyName);
            if (propertyValue != null && handler != null) {
                try {
                    T result = handler.apply(propertyValue);
                    if (result != null) {
                        if (predicate == null
                                || predicate.test(result, defaultValue)) {
                            return result;
                        }
                    }
                } catch (Throwable e) {
                    if (defaultValue == null) {
                        throw e;
                    } else {
                        ToolLog.error(e,
                                () -> "Caught a Exception reading the system propertyValue name [%s] and the propertyValue value [%s]",
                                propertyName, propertyValue);

                    }
                }
            }
        } catch (
                final SecurityException ex) {
            if (defaultValue == null) {
                throw ex;
            } else {
                ToolLog.error(ex,
                        () -> "Caught a SecurityException reading the system property name [%s]; the SystemUtils propertyName value will default to null.",
                        propertyName);
            }
        }
        return defaultValue;
    }

    /**
     * 执行命令<br>
     * 命令带参数时参数可作为其中一个参数，也可以将命令和参数组合为一个字符串传入
     *
     * @param commands 命令
     * @return {@link Process}
     */
    static Process executeCommand(String... commands) throws IOException {
        return new ProcessBuilder(handleCommands(commands)).redirectErrorStream(true).start();
    }

    /**
     * 执行命令<br>
     * 命令带参数时参数可作为其中一个参数，也可以将命令和参数组合为一个字符串传入
     *
     * @param envp     环境变量参数，传入形式为key=value，null表示继承系统环境变量
     * @param dir      执行命令所在目录（用于相对路径命令执行），null表示使用当前进程执行的目录
     * @param commands 命令
     * @return {@link Process}
     */
    static Process executeCommand(String[] envp, File dir, String... commands) throws IOException {
        return Runtime.getRuntime().exec(handleCommands(commands), envp, dir);
    }

    /**
     * 处理命令，多行命令原样返回，单行命令拆分处理
     *
     * @param commands 命令
     * @return 处理后的命令
     */
    static String[] handleCommands(String... commands) {
        if (commands == null || commands.length == 0) {
            throw new NullPointerException("Command is empty !");
        }

        // 单条命令的情况
        if (1 == commands.length) {
            final String cmd = commands[0];
            if (Whether.blank(cmd)) {
                throw new NullPointerException("Command is blank !");
            }
            final List<String> cmdList = new ArrayList<>();
            final Stack<Character> stack = new Stack<>();
            final StringBuilder cache = new StringBuilder();

            final int length = cmd.length();
            boolean inWrap = false;

            char c;
            for (int i = 0; i < length; i++) {
                c = cmd.charAt(i);
                switch (c) {
                    case PoolOfCharacter.SINGLE_QUOTE:
                    case PoolOfCharacter.DOUBLE_QUOTES:
                        if (inWrap) {
                            if (c == stack.peek()) {
                                // 结束包装
                                stack.pop();
                                inWrap = false;
                            }
                            cache.append(c);
                        } else {
                            stack.push(c);
                            cache.append(c);
                            inWrap = true;
                        }
                        break;
                    case PoolOfCharacter.SPACE:
                        if (inWrap) {
                            // 处于包装内
                            cache.append(c);
                        } else {
                            cmdList.add(cache.toString());
                            cache.setLength(0);
                        }
                        break;
                    default:
                        cache.append(c);
                        break;
                }
            }

            if (cache.length() != 0) {
                cmdList.add(cache.toString());
            }

            commands = cmdList.toArray(new String[0]);
        }
        return commands;
    }

    /**
     * Java环境信息
     */
    @Data
    final class Info {
        private final Os os = new Os();
        private final Memory memory = new Memory();
        private final Host host = new Host();
        private final User user = new User();
        private final Java java = new Java();
        private final JavaSpec javaSpec = new JavaSpec();
        private final JavaRuntime javaRuntime = new JavaRuntime();
        private final Jvm jvm = new Jvm();
        private final JvmSpec jvmSpec = new JvmSpec();

        private static String safeValue(Object value) {
            return Safe.value(value, "[n/a]");
        }

        private Info() {
        }

        /**
         * 代表当前OS的信息。
         */
        @Getter
        public static final class Os implements Serializable {
            /**
             * 取得当前OS的版本（取自系统属性：{@code os.version}）。
             *
             * <p>例如：{@code "5.1"}
             */
            private final String version = OS_VERSION;
            /**
             * 取得当前OS的架构（取自系统属性：{@code os.arch}）。
             *
             * <p>例如：{@code "x86"}
             */
            private final String arch = OS_ARCH;
            /**
             * 取得当前OS的名称（取自系统属性：{@code os.name}）。
             *
             * <p>例如：{@code "Windows XP"}
             */
            private final String name = OS_NAME;
            // 由于改变file.encoding属性并不会改变系统字符编码，为了保持一致，通过LocaleUtil取系统默认编码。
            /**
             * 取得OS的文件路径的分隔符（取自系统属性：{@code file.separator}）。
             *
             * <p>例如：Unix为{@code "/"}，Windows为{@code "\\"}。
             */
            private final String fileSeparator = FILE_SEPARATOR;
            /**
             * 取得OS的文本文件换行符（取自系统属性：{@code line.separator}）。
             *
             * <p>例如：Unix为{@code "\n"}，Windows为{@code "\r\n"}。
             */
            private final String lineSeparator = LINE_SEPARATOR;
            /**
             * 取得OS的搜索路径分隔符（取自系统属性：{@code path.separator}）。
             *
             * <p>例如：Unix为{@code ":"}，Windows为{@code ";"}。
             */
            private final String pathSeparator = PATH_SEPARATOR;

            private Os() {
            }

            /**
             * 将OS的信息转换成字符串。
             *
             * @return OS的字符串表示
             */
            @Override
            public String toString() {
                return "OS Arch:        " + safeValue(getArch()) +
                        "OS Name:        " + safeValue(getName()) +
                        "OS Version:     " + safeValue(getVersion()) +
                        "File Separator: " + safeValue(getFileSeparator()) +
                        "Line Separator: " + safeValue(getLineSeparator()) +
                        "Path Separator: " + safeValue(getPathSeparator());
            }
        }

        /**
         * 运行时信息，包括内存总大小、已用大小、可用大小等
         *
         * @author potatoxf
         */
        @Getter
        public static final class Memory implements Serializable {

            private Memory() {
            }

            /**
             * 获得JVM最大内存
             *
             * @return 最大内存
             */
            public long getMaxMemory() {
                return Runtime.getRuntime().maxMemory();
            }

            /**
             * 获得JVM已分配内存
             *
             * @return 已分配内存
             */
            public long getTotalMemory() {
                return Runtime.getRuntime().totalMemory();
            }

            /**
             * 获得JVM已分配内存中的剩余空间
             *
             * @return 已分配内存中的剩余空间
             */
            public long getFreeMemory() {
                return Runtime.getRuntime().freeMemory();
            }

            /**
             * 获得JVM最大可用内存
             *
             * @return 最大可用内存
             */
            public long getUsableMemory() {
                return getMaxMemory() - getTotalMemory() + getFreeMemory();
            }

            @Override
            public String toString() {
                return "Max Memory:      " + FileUtil.readableFileSize(getMaxMemory()) +
                        "Total Memory:    " + FileUtil.readableFileSize(getTotalMemory()) +
                        "Free Memory:     " + FileUtil.readableFileSize(getFreeMemory()) +
                        "Usable Memory:   " + FileUtil.readableFileSize(getUsableMemory());
            }
        }

        /**
         * 代表当前主机的信息。
         */
        @Getter
        public static final class Host implements Serializable {
            /**
             * 取得当前主机的名称。
             *
             * <p>例如：<code>"webserver1"</code>
             */
            private final String hostName;
            /**
             * 取得当前主机的地址。
             *
             * <p>例如：<code>"192.168.0.1"</code>
             */
            private final String hostAddress;

            private Host() {
                final InetAddress localhost = NetUtil.getLocalhost();
                if (null != localhost) {
                    hostName = localhost.getHostName();
                    hostAddress = localhost.getHostAddress();
                } else {
                    hostName = null;
                    hostAddress = null;
                }
            }

            /**
             * 将当前主机的信息转换成字符串。
             *
             * @return 主机信息的字符串表示
             */
            @Override
            public String toString() {
                return "Host Name:    " + safeValue(getHostName()) +
                        "Host Address: " + safeValue(getHostAddress());
            }
        }

        /**
         * 代表当前用户的信息。
         */
        @Getter
        public static final class User implements Serializable {
            /**
             * 取得当前登录用户的名字（取自系统属性：{@code user.name}）。
             *
             * <p>例如：{@code "admin"}
             */
            private final String name = USER_NAME;
            /**
             * 取得当前登录用户的home目录（取自系统属性：{@code user.home}）。
             *
             * <p>例如：{@code "/home/admin/"}
             */
            private final String home = USER_HOME;
            /**
             * 取得当前目录（取自系统属性：{@code user.dir}）。
             *
             * <p>例如：{@code "/home/admin/working/"}
             */
            private final String dir = USER_DIR;
            /**
             * 取得临时目录（取自系统属性：{@code java.io.tmpdir}）。
             *
             * <p>例如：{@code "/tmp/"}
             */
            private final String tmpdir = JAVA_IO_TMPDIR;
            /**
             * 取得当前登录用户的语言设置（取自系统属性：{@code user.language}）。
             *
             * <p>例如：{@code "zh"}、{@code "en"}等
             */
            private final String language = USER_LANGUAGE;
            /**
             * 取得当前登录用户的国家或区域设置（取自系统属性：JDK1.4 {@code user.country}或JDK1.2 {@code user.region}）。
             *
             * <p>例如：{@code "CN"}、{@code "US"}等
             */
            private final String country = USER_COUNTRY;

            private User() {
            }

            /**
             * 将当前用户的信息转换成字符串。
             *
             * @return 用户信息的字符串表示
             */
            @Override
            public String toString() {
                return "User Name:        " + safeValue(getName()) +
                        "User Home Dir:    " + safeValue(getHome()) +
                        "User Current Dir: " + safeValue(getDir()) +
                        "User Temp Dir:    " + safeValue(getTmpdir()) +
                        "User Language:    " + safeValue(getLanguage()) +
                        "User Country:     " + safeValue(getCountry());
            }
        }

        /**
         * 代表Java Implementation的信息。
         */
        @Getter
        public static final class Java implements Serializable {
            /**
             * 取得当前Java impl.的版本（取自系统属性：{@code java.version}）。
             *
             * <p>例如Sun JDK 1.4.2：{@code "1.4.2"}
             */
            private final String version = JAVA_VERSION;
            /**
             * 取得当前Java impl.的版本的{@code float}值。
             */
            private final float floatVersion = getJavaVersionAsFloat();
            /**
             * 取得当前Java impl.的版本的{@code int}值。
             */
            private final int intVersion = getJavaVersionAsInt();
            /**
             * 取得当前Java impl.的厂商（取自系统属性：{@code java.vendor}）。
             *
             * <p>例如Sun JDK 1.4.2：{@code "Sun Microsystems Inc."}
             */
            private final String vendor = JAVA_VENDOR;
            /**
             * 取得当前Java impl.的厂商网站的URL（取自系统属性：{@code java.vendor.url}）。
             *
             * <p>例如Sun JDK 1.4.2：{@code "<a href="http://java.sun.com/">http://java.sun.com/</a>"}
             */
            private final String vendorUrl = JAVA_VENDOR_URL;

            private Java() {
            }

            private float getJavaVersionAsFloat() {
                if (version == null) {
                    return 0f;
                }

                String str = version;

                str = ToolRegex.get("^[0-9]{1,2}(\\.[0-9]{1,2})?", str, 0);

                return Float.parseFloat(str);
            }

            /**
             * 取得当前Java impl.的版本的{@code int}值。
             *
             * @return Java版本的<code>int</code>值或{@code 0}
             */
            private int getJavaVersionAsInt() {
                if (version == null) {
                    return 0;
                }

                final String javaVersion = ToolRegex.get("^[0-9]{1,2}(\\.[0-9]{1,2}){0,2}", this.version, 0);

                final String[] split = javaVersion.split("\\.");
                String result = ToolArray.join(split, "");

                // 保证java10及其之后的版本返回的值为4位
                if (split[0].length() > 1) {
                    result = (result + "0000").substring(0, 4);
                }

                return Integer.parseInt(result);
            }

            /**
             * 判定当前Java的版本是否大于等于指定的版本号。
             *
             * <p>例如：
             *
             * <ul>
             *   <li>测试JDK 1.2：{@code isJavaVersionAtLeast(1.2f)}
             *   <li>测试JDK 1.2.1：{@code isJavaVersionAtLeast(1.31f)}
             * </ul>
             *
             * @param requiredVersion 需要的版本
             * @return 如果当前Java版本大于或等于指定的版本，则返回{@code true}
             */
            public boolean isJavaVersionAtLeast(final float requiredVersion) {
                return floatVersion >= requiredVersion;
            }

            /**
             * 判定当前Java的版本是否大于等于指定的版本号。
             *
             * <p>例如：
             *
             * <ul>
             *   <li>测试JDK 1.2：{@code isJavaVersionAtLeast(120)}
             *   <li>测试JDK 1.2.1：{@code isJavaVersionAtLeast(131)}
             * </ul>
             *
             * @param requiredVersion 需要的版本
             * @return 如果当前Java版本大于或等于指定的版本，则返回{@code true}
             */
            public boolean isJavaVersionAtLeast(final int requiredVersion) {
                return intVersion >= requiredVersion;
            }

            /**
             * 将Java Implementation的信息转换成字符串。
             *
             * @return JVM impl.的字符串表示
             */
            @Override
            public String toString() {
                return "Java Version:    " + safeValue(getVersion()) +
                        "Java Vendor:     " + safeValue(getVendor()) +
                        "Java Vendor URL: " + safeValue(getVendorUrl());
            }
        }

        /**
         * 代表当前运行的JRE的信息。
         */
        @Getter
        public static final class JavaRuntime implements Serializable {
            /**
             * 取得当前JRE的名称（取自系统属性：<code>java.runtime.name</code>）。
             *
             * <p>例如Sun JDK 1.4.2： <code>"Java(TM) 2 Runtime Environment, Standard Edition"</code>
             */
            private final String name = JAVA_RUNTIME_NAME;
            /**
             * 取得当前JRE的版本（取自系统属性：<code>java.runtime.version</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"1.4.2-b28"</code>
             */
            private final String version = JAVA_RUNTIME_VERSION;
            /**
             * 取得当前JRE的安装目录（取自系统属性：<code>java.home</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"/opt/jdk1.4.2/jre"</code>
             */
            private final String homeDir = JAVA_HOME;
            /**
             * 取得当前JRE的扩展目录列表（取自系统属性：<code>java.ext.dirs</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"/opt/jdk1.4.2/jre/lib/ext:..."</code>
             */
            private final String extDirs = JAVA_EXT_DIRS;
            /**
             * 取得当前JRE的endorsed目录列表（取自系统属性：<code>java.endorsed.dirs</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"/opt/jdk1.4.2/jre/lib/endorsed:..."</code>
             */
            private final String endorsedDirs = JAVA_ENDORSED_DIRS;
            /**
             * 取得当前JRE的系统classpath（取自系统属性：<code>java.class.path</code>）。
             *
             * <p>例如：<code>"/home/admin/myclasses:/home/admin/..."</code>
             */
            private final String classPath = JAVA_CLASS_PATH;
            /**
             * 取得当前JRE的class文件格式的版本（取自系统属性：<code>java.class.version</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"48.0"</code>
             */
            private final String classVersion = JAVA_CLASS_VERSION;
            /**
             * 取得当前JRE的library搜索路径（取自系统属性：<code>java.library.path</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"/opt/jdk1.4.2/bin:..."</code>
             */
            private final String libraryPath = JAVA_LIBRARY_PATH;
            private final String sunBootClassPath = get("sun.boot.class.path", false);
            /**
             * JVM is 32M <code>or</code> 64M
             */
            private final String sunArchDataModel = get("sun.arch.data.model", false);

            private JavaRuntime() {
            }

            /**
             * 取得当前JRE的library搜索路径（取自系统属性：<code>java.library.path</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"/opt/jdk1.4.2/bin:..."</code>
             *
             * @return 属性值，如果不能取得（因为Java安全限制）或值不存在，则返回<code>null</code>。
             */
            public String[] getLibraryPathArray() {
                return ToolString.splitToArray(getLibraryPath(), get("path.separator", false));
            }

            /**
             * 取得当前JRE的URL协议packages列表（取自系统属性：<code>java.library.path</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"sun.net.www.protocol|..."</code>
             *
             * @return 属性值，如果不能取得（因为Java安全限制）或值不存在，则返回<code>null</code>。
             */
            public String getProtocolPackages() {
                return get("java.protocol.handler.pkgs", true);
            }

            /**
             * 将当前运行的JRE信息转换成字符串。
             *
             * @return JRE信息的字符串表示
             */
            @Override
            public String toString() {
                return "Java Runtime Name:      " + safeValue(getName()) +
                        "Java Runtime Version:   " + safeValue(getVersion()) +
                        "Java Home Dir:          " + safeValue(getHomeDir()) +
                        "Java Extension Dirs:    " + safeValue(getExtDirs()) +
                        "Java Endorsed Dirs:     " + safeValue(getEndorsedDirs()) +
                        "Java Class Path:        " + safeValue(getClassPath()) +
                        "Java Class Version:     " + safeValue(getClassVersion()) +
                        "Java Library Path:      " + safeValue(getLibraryPath()) +
                        "Java Protocol Packages: " + safeValue(getProtocolPackages());
            }
        }

        /**
         * 代表Java Specification的信息。
         */
        @Getter
        public static class JavaSpec implements Serializable {
            /**
             * 取得当前Java Spec.的名称（取自系统属性：<code>java.specification.name</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"Java Platform API Specification"</code>
             */
            private final String name = JAVA_SPECIFICATION_NAME;
            /**
             * 取得当前Java Spec.的版本（取自系统属性：<code>java.specification.version</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"1.4"</code>
             */
            private final String version = JAVA_SPECIFICATION_VERSION;
            /**
             * 取得当前Java Spec.的厂商（取自系统属性：<code>java.specification.vendor</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"Sun Microsystems Inc."</code>
             */
            private final String vendor = JAVA_SPECIFICATION_VENDOR;

            private JavaSpec() {
            }

            /**
             * 将Java Specification的信息转换成字符串。
             *
             * @return JVM spec.的字符串表示
             */
            @Override
            public final String toString() {
                return "Java Spec. Name:    " + safeValue(getName()) +
                        "Java Spec. Version: " + safeValue(getVersion()) +
                        "Java Spec. Vendor:  " + safeValue(getVendor());
            }
        }

        /**
         * 代表Java Virtual Machine Implementation的信息。
         */
        @Getter
        public static final class Jvm implements Serializable {
            /**
             * 取得当前JVM impl.的名称（取自系统属性：<code>java.vm.name</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"Java HotSpot(TM) Client VM"</code>
             */
            private final String name = JAVA_VM_NAME;
            /**
             * 取得当前JVM impl.的版本（取自系统属性：<code>java.vm.version</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"1.4.2-b28"</code>
             */
            private final String version = JAVA_VM_VERSION;
            /**
             * 取得当前JVM impl.的厂商（取自系统属性：<code>java.vm.vendor</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"Sun Microsystems Inc."</code>
             */
            private final String vendor = JAVA_VM_VENDOR;
            /**
             * 取得当前JVM impl.的信息（取自系统属性：<code>java.vm.info</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"mixed mode"</code>
             */
            private final String info = JAVA_VM_INFO;
            /**
             * 获得JVM可用的处理器数量（一般为CPU核心数）
             */
            private final int availableProcessorCount = Runtime.getRuntime().availableProcessors();

            private Jvm() {
            }

            /**
             * 将Java Virutal Machine Implementation的信息转换成字符串。
             *
             * @return JVM impl.的字符串表示
             */
            @Override
            public String toString() {
                return "JavaVM Name:               " + safeValue(getName()) +
                        "JavaVM Version:            " + safeValue(getVersion()) +
                        "JavaVM Vendor:             " + safeValue(getVendor()) +
                        "JavaVM Info:               " + safeValue(getInfo()) +
                        "JavaVM Processor Count:    " + safeValue(getAvailableProcessorCount());
            }
        }

        /**
         * 代表Java Virutal Machine Specification的信息。
         */
        @Getter
        public static final class JvmSpec implements Serializable {
            /**
             * 取得当前JVM spec.的名称（取自系统属性：<code>java.vm.specification.name</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"Java Virtual Machine Specification"</code>
             */
            private final String name =
                    JAVA_VM_SPECIFICATION_NAME;
            /**
             * 取得当前JVM spec.的版本（取自系统属性：<code>java.vm.specification.version</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"1.0"</code>
             */
            private final String version =
                    JAVA_VM_SPECIFICATION_VERSION;
            /**
             * 取得当前JVM spec.的厂商（取自系统属性：<code>java.vm.specification.vendor</code>）。
             *
             * <p>例如Sun JDK 1.4.2：<code>"Sun Microsystems Inc."</code>
             */
            private final String vendor =
                    JAVA_VM_SPECIFICATION_VENDOR;

            private JvmSpec() {
            }

            /**
             * 将Java Virutal Machine Specification的信息转换成字符串。
             *
             * @return JVM spec.的字符串表示
             */
            @Override
            public String toString() {
                return "JavaVM Spec. Name:    " + safeValue(getName()) +
                        "JavaVM Spec. Version: " + safeValue(getVersion()) +
                        "JavaVM Spec. Vendor:  " + safeValue(getVendor());
            }
        }
    }
}

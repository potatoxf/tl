package pxf.tl.api.spi.instance;


import pxf.tl.api.JavaEnvironment;
import pxf.tl.api.spi.SpiApi;
import pxf.tl.util.ToolString;

/**
 * 项目
 *
 * @author potatoxf
 */
public class SystemProject implements SpiApi {

    protected SystemProject() {
    }

    /**
     * 获取Project路径
     *
     * @return 返回当前本地路径
     */
    public String getProjectPath() {
        return JavaEnvironment.USER_DIR;
    }

    /**
     * 获取Classpath的路径
     *
     * <p>当项目以jar、war包运行时，路径改成实际硬盘位置
     *
     * @return 返回当前本地路径
     */
    public String getClassPath() {
        return legalizedAbsoluteDirectory(
                Thread.currentThread().getContextClassLoader().getResource("").toString());
    }

    /**
     * 获取Project路径
     *
     * @return 返回当前本地路径
     */
    public String getProjectSubPath(String relativePath) {
        return getProjectPath() + legalizedRelativeDirectory(relativePath);
    }

    /**
     * 获取Classpath的路径
     *
     * <p>当项目以jar、war包运行时，路径改成实际硬盘位置
     *
     * @return 返回当前本地路径
     */
    public String getClassSubPath(String relativePath) {
        return getClassPath() + legalizedRelativeDirectory(relativePath);
    }

    protected String legalizedAbsoluteDirectory(String path) {
        return ToolString.clearPath(true, true, path);
    }

    protected String legalizedRelativeDirectory(String path) {
        return ToolString.clearPath(false, true, path);
    }
}

package pxf.tlx.monitor;

import java.nio.file.Path;

/**
 * 文件监听器
 *
 * @author potatoxf
 */
public interface FileListener {

    /**
     * 当文件触发指定事件时执行该函数
     *
     * @param fileWatch 文件监视参数
     * @param path      当前监控文件路径
     * @param event     事件ID
     */
    void emit(FileWatch fileWatch, Path path, int event);
}

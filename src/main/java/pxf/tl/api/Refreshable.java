package pxf.tl.api;

/**
 * @author potatoxf
 */
public interface Refreshable {

    /**
     * 刷新
     *
     * @throws Throwable 抛出异常
     */
    void flush() throws Throwable;
}

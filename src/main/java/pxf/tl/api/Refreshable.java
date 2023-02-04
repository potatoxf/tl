package pxf.tl.api;

/**
 * @author potatoxf
 */
public interface Refreshable {
    /**
     * 资源键，通过该键来查找该可刷新资源刷新
     *
     * @return 返回资源键
     */
    String key();

    /**
     * 刷新
     *
     * @throws Throwable 抛出异常
     */
    void flush() throws Throwable;
}

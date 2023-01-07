package pxf.tl.api;


/**
 * 资源刷新
 *
 * @author potatoxf
 */
public interface ResourceRefreshable extends Refreshable {
    /**
     * 资源键，通过该键来查找该可刷新资源刷新
     *
     * @return 返回资源键
     */
    String key();
}

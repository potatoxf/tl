package pxf.tl.iter;

import java.util.List;

/**
 * 分页数据加载器
 *
 * @author potatoxf
 */
@FunctionalInterface
public interface PaginationDataLoader<T> {

    /**
     * 加载数据
     *
     * @param pageSize 页面大小
     * @param page     第几个页面
     * @return 返回改分页数据
     * @throws Throwable 如果加载数据发生了异常
     */
    List<T> load(int pageSize, int page) throws Throwable;
}

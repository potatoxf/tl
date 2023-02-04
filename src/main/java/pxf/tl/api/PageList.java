package pxf.tl.api;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * 分页列表
 *
 * @author potatoxf
 */
@Getter
@Setter
public class PageList<E> extends ArrayList<E> implements Page<E> {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 页码，从1开始
     */
    private int pageNum;
    /**
     * 页面大小
     */
    private int pageSize;
    /**
     * 起始行
     */
    private int startRow;
    /**
     * 末行
     */
    private int endRow;
    /**
     * 总数
     */
    private int total;
    /**
     * 总页数
     */
    private int pages;
    /**
     * 包含count查询
     */
    private boolean count = true;
    /**
     * 分页合理化
     */
    private Boolean reasonable;
    /**
     * 当设置为true的时候，如果pageSize设置为0（或RowBounds的limit=0），就不执行分页，返回全部结果
     */
    private Boolean pageSizeZero;
    /**
     * 进行count查询的列名
     */
    private String countColumn;
    /**
     * 排序
     */
    private String orderBy;
    /**
     * 只增加排序
     */
    private boolean orderByOnly;

    public PageList() {
        super();
    }

    public PageList(int pageNum, int pageSize) {
        this(pageNum, pageSize, true, null);
    }

    public PageList(int pageNum, int pageSize, boolean count) {
        this(pageNum, pageSize, count, null);
    }

    private PageList(int pageNum, int pageSize, boolean count, Boolean reasonable) {
        super(0);
        if (pageNum == 1 && pageSize == Integer.MAX_VALUE) {
            pageSizeZero = true;
            pageSize = 0;
        }
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.count = count;
        calculateStartAndEndRow();
        setReasonable(reasonable);
    }

    /**
     * int[] rowBounds
     * 0 : offset
     * 1 : limit
     */
    public PageList(int[] rowBounds, boolean count) {
        super(0);
        if (rowBounds[0] == 0 && rowBounds[1] == Integer.MAX_VALUE) {
            pageSizeZero = true;
            this.pageSize = 0;
        } else {
            this.pageSize = rowBounds[1];
            this.pageNum = rowBounds[1] != 0 ? (int) (Math.ceil(((double) rowBounds[0] + rowBounds[1]) / rowBounds[1])) : 0;
        }
        this.startRow = rowBounds[0];
        this.count = count;
        this.endRow = this.startRow + rowBounds[1];
    }

    /**
     * 设置页码
     */
    public void setPageNum(int pageNum) {
        //分页合理化，针对不合理的页码自动处理
        this.pageNum = ((reasonable != null && reasonable) && pageNum <= 0) ? 1 : pageNum;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        calculateStartAndEndRow();
    }

    public void setTotal(int total) {
        this.total = total;
        if (total == -1) {
            pages = 1;
            return;
        }
        if (pageSize > 0) {
            pages = total / pageSize + ((total % pageSize == 0) ? 0 : 1);
        } else {
            pages = 0;
        }
        //分页合理化，针对不合理的页码自动处理
        if ((reasonable != null && reasonable) && pageNum > pages) {
            if (pages != 0) {
                pageNum = pages;
            }
            calculateStartAndEndRow();
        }
    }

    /**
     * 当设置为true的时候，如果pageSize设置为0（或RowBounds的limit=0），就不执行分页，返回全部结果
     */
    public void setPageSizeZero(Boolean pageSizeZero) {
        if (this.pageSizeZero == null && pageSizeZero != null) {
            this.pageSizeZero = pageSizeZero;
        }
    }

    /**
     * 设置合理化
     */
    public void setReasonable(Boolean reasonable) {
        if (reasonable == null) {
            return;
        }
        this.reasonable = reasonable;
        //分页合理化，针对不合理的页码自动处理
        if (this.reasonable && this.pageNum <= 0) {
            this.pageNum = 1;
            calculateStartAndEndRow();
        }
    }

    public PageInfo<E> toPageInfo() {
        return new PageInfo<>(this);
    }

    public <T> PageInfo<T> toPageInfo(Function<E, T> function) {
        List<T> list = new ArrayList<>(this.size());
        for (E e : this) {
            list.add(function.apply(e));
        }
        PageInfo<T> pageInfo = new PageInfo<>(list);
        pageInfo.setPageNum(this.getPageNum());
        pageInfo.setPageSize(this.getPageSize());
        pageInfo.setPages(this.getPages());
        pageInfo.setStartRow(this.getStartRow());
        pageInfo.setEndRow(this.getEndRow());
        pageInfo.calcByNavigatePages(PageInfo.DEFAULT_NAVIGATE_PAGES);
        return pageInfo;
    }

    public PageSerializable<E> toPageSerializable() {
        return new PageSerializable<>(this);
    }

    public <T> PageSerializable<T> toPageSerializable(Function<E, T> function) {
        List<T> list = new ArrayList<>(this.size());
        for (E e : this) {
            list.add(function.apply(e));
        }
        return new PageSerializable<>(list);
    }

    /**
     * 计算起止行号
     */
    private void calculateStartAndEndRow() {
        this.startRow = this.pageNum > 0 ? (this.pageNum - 1) * this.pageSize : 0;
        this.endRow = this.startRow + this.pageSize * (this.pageNum > 0 ? 1 : 0);
    }

    @Override
    public String toString() {
        return "PageList{" +
                "count=" + count +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", startRow=" + startRow +
                ", endRow=" + endRow +
                ", total=" + total +
                ", pages=" + pages +
                ", reasonable=" + reasonable +
                ", pageSizeZero=" + pageSizeZero +
                '}' + super.toString();
    }


    @Override
    public Integer getPageNum() {
        return this.pageNum;
    }


    @Override
    public Integer getPageSize() {
        return this.pageSize;
    }

    @Override
    public String getOrderBy() {
        return this.orderBy;
    }

    @Override
    public List<E> result() {
        return this;
    }
}

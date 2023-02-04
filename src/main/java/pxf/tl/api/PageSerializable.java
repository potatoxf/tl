package pxf.tl.api;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author potatoxf
 */
@Getter
@Setter
public class PageSerializable<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //总记录数
    protected int total;
    //结果集
    protected List<T> list;

    public PageSerializable() {
    }

    public PageSerializable(List<T> list) {
        this.list = list;
        if (list instanceof PageList pageList) {
            this.total = pageList.getTotal();
        } else {
            this.total = list.size();
        }
    }

    @Override
    public String toString() {
        return "PageSerializable{" +
                "total=" + total +
                ", list=" + list +
                '}';
    }
}

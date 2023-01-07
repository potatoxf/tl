package pxf.tl.database;


import pxf.tl.api.This;

import java.io.Serializable;

/**
 * 表格支持额外数据
 *
 * @author potatoxf
 */
public interface TableSupportOtherData<
        PrimaryKey extends Serializable & Comparable<PrimaryKey>,
        Table extends
                TableSupportCommon<PrimaryKey, Table> & TableSupportOtherData<PrimaryKey, Table>>
        extends This<Table> {

    /**
     * 是否包含其它数据值
     *
     * @param key
     * @return 如果存在返回true，否则返回false
     */
    boolean containsOtherData(String key);

    /**
     * 获取其它数据值
     *
     * @param key 键
     * @return 值
     */
    Object getOtherData(String key);

    /**
     * 放入其它数据值
     *
     * @param key   键
     * @param value 值
     * @return 返回老的值
     */
    Object putOtherData(String key, Object value);
}

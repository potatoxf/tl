package pxf.tl.database;


import pxf.tl.api.This;

import java.io.Serializable;

/**
 * 表格支持主键
 *
 * @author potatoxf
 */
public interface TableSupportPrimaryKey<
        PrimaryKey extends Serializable & Comparable<PrimaryKey>,
        Table extends TableSupportPrimaryKey<PrimaryKey, Table>>
        extends This<Table> {

    String DEFAULT_FIELD_NAME_GID = "GID";

    /**
     * 获取注解
     *
     * @return {@code PrimaryKey}
     */
    PrimaryKey getPrimaryKey();

    /**
     * 设置主键
     *
     * @param primaryKey 主键
     * @return {@link #this$()}
     */
    Table setPrimaryKey(PrimaryKey primaryKey);

    /**
     * 获取主键属性名称
     *
     * @return {@link String}
     */
    default String getPrimaryKeyFieldName() {
        return DEFAULT_FIELD_NAME_GID;
    }
}

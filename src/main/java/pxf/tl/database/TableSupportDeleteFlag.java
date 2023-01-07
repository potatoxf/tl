package pxf.tl.database;


import pxf.tl.api.This;

import java.io.Serializable;

/**
 * 表格支持逻辑删除
 *
 * @author potatoxf
 */
public interface TableSupportDeleteFlag<
        PrimaryKey extends Serializable & Comparable<PrimaryKey>,
        Table extends
                TableSupportCommon<PrimaryKey, Table> & TableSupportDeleteFlag<PrimaryKey, Table>>
        extends This<Table> {

    /**
     * 未删除状态值
     */
    int DELETE_STATUS__FAIL = 0;

    /**
     * 已删除状态值
     */
    int DELETE_STATUS__PASS = 1;
    /**
     * 属性名称
     */
    String DEFAULT_FIELD_NAME_DELETE_FLAG = "DELETE_FLAG";

    /**
     * 获取删除状态
     *
     * @return 返回删除状态
     */
    int getDeleteFlag();

    /**
     * 设置删除状态
     *
     * @param deleteFlag
     * @return {@link #this$()}
     */
    Table setDeleteFlag(int deleteFlag);

    /**
     * 获取删除状态属性名称
     *
     * @return {@link String}
     */
    default String getDeleteFlagFieldName() {
        return DEFAULT_FIELD_NAME_DELETE_FLAG;
    }

    /**
     * 是否删除
     *
     * @return 如果删除返回true，否则返回false
     */
    default boolean isDeleteFlag() {
        return DELETE_STATUS__PASS == getDeleteFlag();
    }

    /**
     * 确认删除状态
     *
     * @return {@link #this$()}
     */
    default Table enterDeleteFlag() {
        return setDeleteFlag(DELETE_STATUS__PASS);
    }

    /**
     * 取消删除状态
     *
     * @return {@link #this$()}
     */
    default Table cancelDeleteFlag() {
        return setDeleteFlag(DELETE_STATUS__FAIL);
    }
}

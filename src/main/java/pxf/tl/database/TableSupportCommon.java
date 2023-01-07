package pxf.tl.database;


import pxf.tl.api.This;
import pxf.tl.comparator.InternalComparator;

import java.io.Serializable;
import java.util.Date;

/**
 * 表格支持常用数据字段
 *
 * @author potatoxf
 */
public interface TableSupportCommon<
        PrimaryKey extends Serializable & Comparable<PrimaryKey>,
        Table extends TableSupportCommon<PrimaryKey, Table>>
        extends Comparable<Table>, This<Table> {

    String DEFAULT_FIELD_NAME_UPDATED_TIME = "UPDATED_TIME";
    String DEFAULT_FIELD_NAME_UPDATED_BY = "UPDATED_BY";
    String DEFAULT_FIELD_NAME_CREATED_TIME = "CREATED_TIME";
    String DEFAULT_FIELD_NAME_CREATED_BY = "CREATED_BY";
    String DEFAULT_FIELD_NAME_REVISION = "REVISION";

    String getCreatedBy();

    Table setCreatedBy(String createdBy);

    default String getCreatedByFieldName() {
        return DEFAULT_FIELD_NAME_CREATED_BY;
    }

    Date getCreatedTime();

    Table setCreatedTime(Date createdTime);

    default String getCreatedTimeFieldName() {
        return DEFAULT_FIELD_NAME_CREATED_TIME;
    }

    String getUpdatedBy();

    Table setUpdatedBy(String updatedBy);

    default String getUpdatedByFieldName() {
        return DEFAULT_FIELD_NAME_UPDATED_BY;
    }

    Date getUpdatedTime();

    Table setUpdatedTime(Date updatedTime);

    default String getUpdatedTimeFieldName() {
        return DEFAULT_FIELD_NAME_UPDATED_TIME;
    }

    int getRevision();

    Table setRevision(int revision);

    default String getRevisionFieldName() {
        return DEFAULT_FIELD_NAME_REVISION;
    }

    default Long getUpdateTimestamp() {
        Date date = getUpdatedTime();
        return date != null ? date.getTime() : null;
    }

    default Table setUpdateTimestamp(Long updateTimestamp) {
        if (updateTimestamp != null) {
            setUpdatedTime(new Date(updateTimestamp));
        }
        return this$();
    }

    default Long getCreateTimestamp() {
        Date date = getCreatedTime();
        return date != null ? date.getTime() : null;
    }

    default Table setCreateTimestamp(Long createTimestamp) {
        if (createTimestamp != null) {
            setCreatedTime(new Date(createTimestamp));
        }
        return this$();
    }

    @Override
    default int compareTo(Table other) {
        return internalComparator().compare(this$(), other);
    }

    default InternalComparator<Table, Long> internalComparator() {
        return new InternalComparator<>(TableSupportCommon::getCreateTimestamp);
    }
}

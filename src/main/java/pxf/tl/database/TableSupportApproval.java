package pxf.tl.database;


import pxf.tl.api.This;

import java.io.Serializable;

/**
 * 表格支持审批状态值
 *
 * @author potatoxf
 */
public interface TableSupportApproval<
        PrimaryKey extends Serializable & Comparable<PrimaryKey>,
        Table extends
                TableSupportCommon<PrimaryKey, Table> & TableSupportApproval<PrimaryKey, Table>>
        extends This<Table> {

    /**
     * 审批失败状态
     */
    int APPROVAL_STATUS__FAIL = 0;

    /**
     * 审批成功状态
     */
    int APPROVAL_STATUS__PASS = 1;

    /**
     * 获取审批状态
     *
     * @return {@link #APPROVAL_STATUS__FAIL}{@link #APPROVAL_STATUS__PASS}
     */
    int getApprovalStatus();

    /**
     * 设置审批状态
     *
     * @param approvalStatus 审批状态
     * @return {@link #this$()}
     */
    Table setApprovalStatus(int approvalStatus);

    /**
     * 设置审批成功状态
     *
     * @return {@link #this$()}
     */
    default Table passApprove() {
        return setApprovalStatus(APPROVAL_STATUS__PASS);
    }

    /**
     * 设置审批失败状态
     *
     * @return {@link #this$()}
     */
    default Table failApprove() {
        return setApprovalStatus(APPROVAL_STATUS__FAIL);
    }
}

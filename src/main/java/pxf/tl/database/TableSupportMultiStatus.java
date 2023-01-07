package pxf.tl.database;


import pxf.tl.api.This;
import pxf.tl.function.BiConsumerThrow;
import pxf.tl.function.FunctionThrow;
import pxf.tl.help.Valid;
import pxf.tl.util.ToolBit;

import java.io.Serializable;

/**
 * 表格支持多位状态值
 *
 * @author potatoxf
 */
public interface TableSupportMultiStatus<
        PrimaryKey extends Serializable & Comparable<PrimaryKey>,
        Table extends
                TableSupportCommon<PrimaryKey, Table> & TableSupportMultiStatus<PrimaryKey, Table>>
        extends This<Table> {

    String DEFAULT_FIELD_NAME_MULTI_STATUS = "MULTI_STATUS";

    /**
     * 返回多位状态值
     *
     * @return 多位状态值
     */
    int getMultiStatus();

    /**
     * 设置多位状态值
     *
     * @param multiStatus 多位状态值
     * @return {@link #this$()}
     */
    Table setMultiStatus(int multiStatus);

    /**
     * 获取多位状态值属性名称
     *
     * @return {@link String}
     */
    default String getMultiStatusFieldName() {
        return DEFAULT_FIELD_NAME_MULTI_STATUS;
    }

    default Table setBitStatusValue(int... target) {
        return setBit(getMultiStatus(), TableSupportMultiStatus::setMultiStatus, target);
    }

    default Table clearBitStatusValue(int... target) {
        return clearBit(getMultiStatus(), TableSupportMultiStatus::setMultiStatus, target);
    }

    default Table flipBitStatusValue(int... target) {
        return flipBit(getMultiStatus(), TableSupportMultiStatus::setMultiStatus, target);
    }

    default boolean isBit(FunctionThrow<Table, String, RuntimeException> getter, int... target) {
        return isBit(getter.apply(this$()), target);
    }

    default boolean isBit(String value, int... target) {
        return ToolBit.testAllBit(Valid.string(value, "0"), target);
    }

    default boolean isBit(long value, int... target) {
        return ToolBit.testAllBit(String.valueOf(value), target);
    }

    default Table setBit(
            FunctionThrow<Table, String, RuntimeException> getter,
            BiConsumerThrow<Table, String, RuntimeException> setter,
            int... target) {
        return setBit(getter.apply(this$()), setter, target);
    }

    default Table setBit(
            String value, BiConsumerThrow<Table, String, RuntimeException> setter, int... target) {
        setter.accept(this$(), ToolBit.setBit(value, target));
        return this$();
    }

    default Table setBit(
            long value, BiConsumerThrow<Table, Integer, RuntimeException> setter, int... target) {
        setter.accept(this$(), Integer.parseInt(ToolBit.setBit(value, target)));
        return this$();
    }

    default Table clearBit(
            FunctionThrow<Table, String, RuntimeException> getter,
            BiConsumerThrow<Table, String, RuntimeException> setter,
            int... target) {
        return clearBit(getter.apply(this$()), setter, target);
    }

    default Table clearBit(
            String value, BiConsumerThrow<Table, String, RuntimeException> setter, int... target) {
        setter.accept(this$(), ToolBit.clearBit(value, target));
        return this$();
    }

    default Table clearBit(
            long value, BiConsumerThrow<Table, Integer, RuntimeException> setter, int... target) {
        setter.accept(this$(), Integer.parseInt(ToolBit.clearBit(value, target)));
        return this$();
    }

    default Table flipBit(
            FunctionThrow<Table, String, RuntimeException> getter,
            BiConsumerThrow<Table, String, RuntimeException> setter,
            int... target) {
        return flipBit(getter.apply(this$()), setter, target);
    }

    default Table flipBit(
            String value, BiConsumerThrow<Table, String, RuntimeException> setter, int... target) {
        setter.accept(this$(), ToolBit.flipBit(value, target));
        return this$();
    }

    default Table flipBit(
            long value, BiConsumerThrow<Table, Integer, RuntimeException> setter, int... target) {
        setter.accept(this$(), Integer.parseInt(ToolBit.flipBit(value, target)));
        return this$();
    }
}

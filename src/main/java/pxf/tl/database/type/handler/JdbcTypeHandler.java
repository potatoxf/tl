package pxf.tl.database.type.handler;

import pxf.tl.database.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Jdbc类型处理接口
 *
 * @author potatoxf
 */
public interface JdbcTypeHandler<T> {

    /**
     * 设置{@code PreparedStatement}参数
     *
     * @param h              {@code PreparedStatement}
     * @param parameterIndex 参数索引
     * @param parameter      参数
     * @param jdbcType       {@code JdbcType}Jdbc类型
     * @param typeParameter  类型参数
     * @throws SQLException 如果发生异常
     */
    void setParameter(PreparedStatement h, int parameterIndex, T parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException;

    /**
     * 获取数据结果
     *
     * @param h             {@code PreparedStatement}
     * @param columnIndex   列索引
     * @param typeParameter 类型参数
     * @throws SQLException 如果发生异常
     */
    T getResult(CallableStatement h, int columnIndex, TypeParameter typeParameter) throws SQLException;

    /**
     * 获取数据结果
     *
     * @param h             {@code PreparedStatement}
     * @param columnIndex   列索引
     * @param typeParameter 类型参数
     * @throws SQLException 如果发生异常
     */
    T getResult(ResultSet h, int columnIndex, TypeParameter typeParameter) throws SQLException;
}

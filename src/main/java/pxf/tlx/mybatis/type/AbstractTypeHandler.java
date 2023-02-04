package pxf.tlx.mybatis.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import pxf.tl.function.BiFunctionThrow;
import pxf.tl.function.TrConsumerThrow;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author potatoxf
 */
public abstract class AbstractTypeHandler<T, V> extends BaseTypeHandler<T> {
    private final V defaultEmptyValue;
    private final TrConsumerThrow<PreparedStatement, Integer, V, SQLException> preparedStatementSetter;
    private final BiFunctionThrow<ResultSet, Integer, V, SQLException> resultSetGetter;
    private final BiFunctionThrow<CallableStatement, Integer, V, SQLException> callableStatementGetter;

    protected AbstractTypeHandler(V defaultEmptyValue,
                                  TrConsumerThrow<PreparedStatement, Integer, V, SQLException> preparedStatementSetter,
                                  BiFunctionThrow<ResultSet, Integer, V, SQLException> resultSetGetter,
                                  BiFunctionThrow<CallableStatement, Integer, V, SQLException> callableStatementGetter) {
        this.defaultEmptyValue = Objects.requireNonNull(defaultEmptyValue);
        this.preparedStatementSetter = Objects.requireNonNull(preparedStatementSetter);
        this.resultSetGetter = Objects.requireNonNull(resultSetGetter);
        this.callableStatementGetter = Objects.requireNonNull(callableStatementGetter);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getNullableResult(rs, rs.findColumn(columnName));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int paramIndex, T parameter, JdbcType jdbcType) throws SQLException {
        if (!isEmptyTarget(parameter)) {
            V value = fromTarget(parameter);
            if (value != null) {
                preparedStatementSetter.accept(ps, paramIndex, value);
            } else {
                if (defaultEmptyValue != null) {
                    preparedStatementSetter.accept(ps, paramIndex, defaultEmptyValue);
                } else {
                    ps.setObject(paramIndex, null);
                }
            }
        } else {
            if (defaultEmptyValue != null) {
                preparedStatementSetter.accept(ps, paramIndex, defaultEmptyValue);
            } else {
                ps.setObject(paramIndex, null);
            }
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        V value = resultSetGetter.apply(rs, columnIndex);
        if (value == null) {
            return null;
        }
        return toTarget(value);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        V value = callableStatementGetter.apply(cs, columnIndex);
        if (value == null) {
            return null;
        }
        return toTarget(value);
    }

    /**
     * 判断目标对象是否为空
     *
     * @param target {@code T}
     * @return 如果是返回true，否则返回false
     */
    protected boolean isEmptyTarget(T target) {
        return target == null;
    }

    /**
     * 从字符串转成目标对象
     *
     * @param value {@code Number}
     * @return {@code T}
     */
    protected abstract T toTarget(V value);

    /**
     * 从目标对象转成字符串
     *
     * @param target {@code T}
     * @return {@code Number}
     */
    protected abstract V fromTarget(T target);
}

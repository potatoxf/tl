package pxf.tl.database.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author potatoxf
 */
public interface JdbcTypeHandler<T> {

    void setParameter(PreparedStatement h, int parameterIndex, T parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException;

    T getResult(CallableStatement h, int columnIndex, TypeParameter typeParameter) throws SQLException;

    T getResult(ResultSet h, int columnIndex, TypeParameter typeParameter) throws SQLException;
}

package pxf.tlx.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import pxf.tl.api.Literal;
import pxf.tl.util.ToolBytecode;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

/**
 * Literal数据与字符串类型转换处理器
 *
 * @author potatoxf
 */
@MappedJdbcTypes(value = {JdbcType.CHAR, JdbcType.VARCHAR, JdbcType.LONGNVARCHAR})
public class IntegerLiteralTypeHandler<T extends Enum<T> & Literal<T>> extends AbstractTypeHandlerForNumber<T, Integer> {

    private final Class<T> type;

    public IntegerLiteralTypeHandler(Class<T> type) {
        super(null, PreparedStatement::setInt, ResultSet::getInt, CallableStatement::getInt);
        if (!Literal.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException();
        }
        this.type = Objects.requireNonNull(type);
    }

    @Override
    protected T toTarget(Integer value) {
        if (type.isEnum()) {
            for (T enumConstant : type.getEnumConstants()) {
                if (enumConstant.getCode() == value) {
                    return enumConstant;
                }
            }
        } else if (!ToolBytecode.isAbstract(type)) {

        }
        return null;
    }

    @Override
    protected Integer fromTarget(T target) {
        return target.getCode();
    }
}

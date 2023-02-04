package pxf.tlx.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import pxf.tl.api.Literal;

import java.util.Objects;

/**
 * Literal数据与字符串类型转换处理器
 *
 * @author potatoxf
 */
@MappedJdbcTypes(value = {JdbcType.CHAR, JdbcType.VARCHAR, JdbcType.LONGNVARCHAR})
public class StringLiteralTypeHandler<T extends Enum<T> & Literal<T>> extends AbstractTypeHandlerForString<T> {

    private final Class<T> type;

    public StringLiteralTypeHandler(Class<T> type) {
        if (!Literal.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException();
        }
        this.type = Objects.requireNonNull(type);
    }

    @Override
    protected T toTarget(String string) {
        return Enum.valueOf(type, string);
    }

    @Override
    protected String fromTarget(T target) {
        return target.name();
    }
}

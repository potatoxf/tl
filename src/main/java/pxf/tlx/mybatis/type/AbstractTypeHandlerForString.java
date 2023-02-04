package pxf.tlx.mybatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 针对文本数据库类型进行类型转换处理器
 *
 * @author potatoxf
 */
public abstract class AbstractTypeHandlerForString<T> extends AbstractTypeHandler<T, String> {

    public AbstractTypeHandlerForString() {
        super("", PreparedStatement::setString, ResultSet::getString, CallableStatement::getString);
    }
}

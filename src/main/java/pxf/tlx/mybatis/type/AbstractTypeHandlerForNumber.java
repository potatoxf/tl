package pxf.tlx.mybatis.type;

import pxf.tl.function.BiFunctionThrow;
import pxf.tl.function.TrConsumerThrow;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 针对文本数据库类型进行类型转换处理器
 *
 * @author potatoxf
 */
public abstract class AbstractTypeHandlerForNumber<T, N extends Number> extends AbstractTypeHandler<T, N> {

    public AbstractTypeHandlerForNumber(N defaultEmptyValue,
                                        TrConsumerThrow<PreparedStatement, Integer, N, SQLException> preparedStatementSetter,
                                        BiFunctionThrow<ResultSet, Integer, N, SQLException> resultSetGetter,
                                        BiFunctionThrow<CallableStatement, Integer, N, SQLException> callableStatementGetter) {
        super(defaultEmptyValue, preparedStatementSetter, resultSetGetter, callableStatementGetter);
    }
}

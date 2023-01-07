package pxf.tl.database.type;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Mysql数据库类型
 *
 * @author potatoxf
 */
public final class OracleDataType extends DatabaseDataType {

    public static final OracleDataType VARCHAR = new OracleDataType("VARCHAR", String.class);
    public static final OracleDataType NVARCHAR = new OracleDataType("NVARCHAR", String.class);
    public static final OracleDataType VARCHAR2 = new OracleDataType("VARCHAR2", String.class);
    public static final OracleDataType NVARCHAR2 = new OracleDataType("NVARCHAR2", String.class);
    public static final OracleDataType CHAR = new OracleDataType("CHAR", String.class);
    public static final OracleDataType NCHAR = new OracleDataType("CHAR", String.class);
    public static final OracleDataType LONG = new OracleDataType("LONG", String.class);
    public static final OracleDataType NUMBER =
            new OracleDataType("NUMBER", BigDecimal.class, 1, 38, 8);
    public static final OracleDataType FLOAT = new OracleDataType("FLOAT", Float.class);
    public static final OracleDataType DATE = new OracleDataType("DATE", Date.class);
    public static final OracleDataType TIMESTAMP = new OracleDataType("TIMESTAMP", Date.class);
    public static final OracleDataType BLOB = new OracleDataType("BLOB", InputStream.class);
    public static final OracleDataType CLOB = new OracleDataType("CLOB", Reader.class);

    OracleDataType(String typeName, Class<?> javaType) {
        super(typeName, javaType);
    }

    OracleDataType(String typeName, Class<?> javaType, int maxLimitLength) {
        super(typeName, javaType, maxLimitLength);
    }

    OracleDataType(String typeName, Class<?> javaType, int minLimitLength, int maxLimitLength) {
        super(typeName, javaType, minLimitLength, maxLimitLength);
    }

    OracleDataType(
            String typeName,
            Class<?> javaType,
            int minLimitLength,
            int maxLimitLength,
            int maxLimitAccuracy) {
        super(typeName, javaType, minLimitLength, maxLimitLength, maxLimitAccuracy);
    }
}

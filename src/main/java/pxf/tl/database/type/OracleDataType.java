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

    public static final OracleDataType VARCHAR = of("VARCHAR", String.class);
    public static final OracleDataType NVARCHAR = of("NVARCHAR", String.class);
    public static final OracleDataType VARCHAR2 = of("VARCHAR2", String.class);
    public static final OracleDataType NVARCHAR2 = of("NVARCHAR2", String.class);
    public static final OracleDataType CHAR = of("CHAR", String.class);
    public static final OracleDataType NCHAR = of("CHAR", String.class);
    public static final OracleDataType LONG = of("LONG", String.class);
    public static final OracleDataType NUMBER = of("NUMBER", BigDecimal.class, 1, 38, 8);
    public static final OracleDataType FLOAT = of("FLOAT", Float.class);
    public static final OracleDataType DATE = of("DATE", Date.class);
    public static final OracleDataType TIMESTAMP = of("TIMESTAMP", Date.class);
    public static final OracleDataType BLOB = of("BLOB", InputStream.class);
    public static final OracleDataType CLOB = of("CLOB", Reader.class);

    OracleDataType(String typeName, Class<?> javaType) {
        super(typeName, javaType);
    }

    OracleDataType(String typeName, Class<?> javaType, int maxLimitLength) {
        super(typeName, javaType, maxLimitLength);
    }

    OracleDataType(String typeName, Class<?> javaType, int minLimitLength, int maxLimitLength) {
        super(typeName, javaType, minLimitLength, maxLimitLength);
    }

    OracleDataType(String typeName, Class<?> javaType, int minLimitLength, int maxLimitLength, int maxLimitAccuracy) {
        super(typeName, javaType, minLimitLength, maxLimitLength, maxLimitAccuracy);
    }

    private static OracleDataType of(String typeName, Class<?> javaType) {
        return new OracleDataType(typeName, javaType);
    }

    private static OracleDataType of(String typeName, Class<?> javaType, int maxLimitLength) {
        return new OracleDataType(typeName, javaType, maxLimitLength);
    }

    private static OracleDataType of(String typeName, Class<?> javaType, int minLimitLength, int maxLimitLength) {
        return new OracleDataType(typeName, javaType, minLimitLength, maxLimitLength);
    }

    private static OracleDataType of(String typeName, Class<?> javaType, int minLimitLength, int maxLimitLength, int maxLimitAccuracy) {
        return new OracleDataType(typeName, javaType, minLimitLength, maxLimitLength, maxLimitAccuracy);
    }

}

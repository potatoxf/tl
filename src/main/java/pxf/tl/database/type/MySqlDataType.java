package pxf.tl.database.type;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Mysql数据库类型
 *
 * @author potatoxf
 */
public final class MySqlDataType extends DatabaseDataType {

    public static final MySqlDataType BIGINT = of("BIGINT", Long.class, 12);
    public static final MySqlDataType CHAR = of("CHAR", String.class);
    public static final MySqlDataType DATE = of("DATE", Date.class);
    public static final MySqlDataType DATETIME = of("DATETIME", Date.class);
    public static final MySqlDataType DECIMAL = of("DECIMAL", BigDecimal.class, 16, 4);
    public static final MySqlDataType DOUBLE = of("DOUBLE", BigDecimal.class);
    public static final MySqlDataType ENUM = of("ENUM", String.class);
    public static final MySqlDataType FLOAT = of("FLOAT", BigDecimal.class);
    public static final MySqlDataType INTEGER = of("INTEGER", Integer.class, 8);
    public static final MySqlDataType LONGTEXT = of("LONGTEXT", String.class);
    public static final MySqlDataType MEDIUMINT = of("MEDIUMINT", String.class, 4);
    public static final MySqlDataType MEDIUMTEXT = of("MEDIUMTEXT", String.class);
    public static final MySqlDataType SET = of("SET", Long.class);
    public static final MySqlDataType TEXT = of("TEXT", String.class);
    public static final MySqlDataType TIME = of("TIME", Date.class);
    public static final MySqlDataType TIMESTAMP = of("TIMESTAMP", Date.class);
    public static final MySqlDataType TINYINT = of("SMALLINT", Integer.class, 4);
    public static final MySqlDataType TINYTEXT = of("TINYTEXT", String.class);
    public static final MySqlDataType VARCHAR = of("VARCHAR", String.class);
    public static final MySqlDataType YEAR = of("YEAR", Integer.class);

    MySqlDataType(String typeName, Class<?> javaType) {
        super(typeName, javaType);
    }

    MySqlDataType(String typeName, Class<?> javaType, int maxLimitLength) {
        super(typeName, javaType, maxLimitLength);
    }

    MySqlDataType(String typeName, Class<?> javaType, int minLimitLength, int maxLimitLength) {
        super(typeName, javaType, minLimitLength, maxLimitLength);
    }

    MySqlDataType(String typeName, Class<?> javaType, int minLimitLength, int maxLimitLength, int maxLimitAccuracy) {
        super(typeName, javaType, minLimitLength, maxLimitLength, maxLimitAccuracy);
    }

    private static MySqlDataType of(String typeName, Class<?> javaType) {
        return new MySqlDataType(typeName, javaType);
    }

    private static MySqlDataType of(String typeName, Class<?> javaType, int maxLimitLength) {
        return new MySqlDataType(typeName, javaType, maxLimitLength);
    }

    private static MySqlDataType of(String typeName, Class<?> javaType, int minLimitLength, int maxLimitLength) {
        return new MySqlDataType(typeName, javaType, minLimitLength, maxLimitLength);
    }

    private static MySqlDataType of(String typeName, Class<?> javaType, int minLimitLength, int maxLimitLength, int maxLimitAccuracy) {
        return new MySqlDataType(typeName, javaType, minLimitLength, maxLimitLength, maxLimitAccuracy);
    }
}

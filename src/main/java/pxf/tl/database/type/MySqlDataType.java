package pxf.tl.database.type;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Mysql数据库类型
 *
 * @author potatoxf
 */
public final class MySqlDataType extends DatabaseDataType {

    public static final MySqlDataType BIGINT = new MySqlDataType("BIGINT", Long.class, 12);
    public static final MySqlDataType CHAR = new MySqlDataType("CHAR", String.class);
    public static final MySqlDataType DATE = new MySqlDataType("DATE", Date.class);
    public static final MySqlDataType DATETIME = new MySqlDataType("DATETIME", Date.class);
    public static final MySqlDataType DECIMAL = new MySqlDataType("DECIMAL", BigDecimal.class, 16, 4);
    public static final MySqlDataType DOUBLE = new MySqlDataType("DOUBLE", BigDecimal.class);
    public static final MySqlDataType ENUM = new MySqlDataType("ENUM", String.class);
    public static final MySqlDataType FLOAT = new MySqlDataType("FLOAT", BigDecimal.class);
    public static final MySqlDataType INTEGER = new MySqlDataType("INTEGER", Integer.class, 8);
    public static final MySqlDataType LONGTEXT = new MySqlDataType("LONGTEXT", String.class);
    public static final MySqlDataType MEDIUMINT = new MySqlDataType("MEDIUMINT", String.class, 4);
    public static final MySqlDataType MEDIUMTEXT = new MySqlDataType("MEDIUMTEXT", String.class);
    public static final MySqlDataType SET = new MySqlDataType("SET", Long.class);
    public static final MySqlDataType TEXT = new MySqlDataType("TEXT", String.class);
    public static final MySqlDataType TIME = new MySqlDataType("TIME", Date.class);
    public static final MySqlDataType TIMESTAMP = new MySqlDataType("TIMESTAMP", Date.class);
    public static final MySqlDataType TINYINT = new MySqlDataType("SMALLINT", Integer.class, 4);
    public static final MySqlDataType TINYTEXT = new MySqlDataType("TINYTEXT", String.class);
    public static final MySqlDataType VARCHAR = new MySqlDataType("VARCHAR", String.class);
    public static final MySqlDataType YEAR = new MySqlDataType("YEAR", Integer.class);

    MySqlDataType(String typeName, Class<?> javaType) {
        super(typeName, javaType);
    }

    MySqlDataType(String typeName, Class<?> javaType, int maxLimitLength) {
        super(typeName, javaType, maxLimitLength);
    }

    MySqlDataType(String typeName, Class<?> javaType, int minLimitLength, int maxLimitLength) {
        super(typeName, javaType, minLimitLength, maxLimitLength);
    }

    MySqlDataType(
            String typeName,
            Class<?> javaType,
            int minLimitLength,
            int maxLimitLength,
            int maxLimitAccuracy) {
        super(typeName, javaType, minLimitLength, maxLimitLength, maxLimitAccuracy);
    }
}

package pxf.tl.database.type;


import pxf.tl.help.New;
import pxf.tl.util.ToolBytecode;
import pxf.tl.util.ToolLog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 数据库类型
 *
 * @author potatoxf
 */
public enum DatabaseType {
    H2(MySqlDataType.class),
    DB2(MySqlDataType.class),
    HSQL(MySqlDataType.class),
    POSTGRES(MySqlDataType.class),
    MSSQL(MySqlDataType.class),
    MYSQL(MySqlDataType.class),
    ORACLE(OracleDataType.class);
    private static final Map<String, DatabaseType> DATABASE_TYPE_MAPPINGS =
            Map.ofEntries(
                    Map.entry("H2", H2),
                    Map.entry("HSQL Database Engine", HSQL),
                    Map.entry("MySQL", MYSQL),
                    Map.entry("Oracle", ORACLE),
                    Map.entry("PostgreSQL", POSTGRES),
                    Map.entry("Microsoft SQL Server", MSSQL),
                    Map.entry("DB2", DB2),
                    Map.entry("DB2/NT", DB2),
                    Map.entry("DB2/NT64", DB2),
                    Map.entry("DB2 UDP", DB2),
                    Map.entry("DB2/LINUX", DB2),
                    Map.entry("DB2/LINUX390", DB2),
                    Map.entry("DB2/LINUXX8664", DB2),
                    Map.entry("DB2/LINUXZ64", DB2),
                    Map.entry("DB2/LINUXPPC64", DB2),
                    Map.entry("DB2/400 SQL", DB2),
                    Map.entry("DB2/6000", DB2),
                    Map.entry("DB2 UDB iSeries", DB2),
                    Map.entry("DB2/AIX64", DB2),
                    Map.entry("DB2/HPUX", DB2),
                    Map.entry("DB2/HP64", DB2),
                    Map.entry("DB2/SUN", DB2),
                    Map.entry("DB2/SUN64", DB2),
                    Map.entry("DB2/PTX", DB2),
                    Map.entry("DB2/2", DB2),
                    Map.entry("DB2 UDB AS400", DB2)
            );
    private final Map<String, DatabaseDataType> databaseDataTypes;
    private final Class<? extends DatabaseDataType> dataType;
    private final Set<String> alias;

    DatabaseType(@Nullable Class<? extends DatabaseDataType> dataType, @Nonnull String... alias) {
        this.dataType = dataType;
        this.alias = alias.length == 0 ? Collections.emptySet() : Set.of(alias);
        if (dataType != null) {
            Map<String, DatabaseDataType> map = New.map(true);
            ToolBytecode.getAllStaticFieldsOfType(dataType, true, true)
                    .forEach(databaseDataType -> map.put(databaseDataType.toString(), databaseDataType));
            this.databaseDataTypes = Collections.unmodifiableMap(map);
        } else {
            this.databaseDataTypes = Collections.emptyMap();
        }
    }

    /**
     * 获取数据库类型
     *
     * @param connection 数据库连接
     * @return 返回数据类型字符串
     */
    @Nonnull
    public static DatabaseType parse(@Nonnull Connection connection) {
        Map<String, DatabaseType> databaseTypeMappings = DATABASE_TYPE_MAPPINGS;
        DatabaseType databaseType;
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String databaseProductName = databaseMetaData.getDatabaseProductName();
            ToolLog.info(() -> "Database product name: %s", databaseProductName);
            databaseType = databaseTypeMappings.get(databaseProductName);
            if (databaseType == null) {
                throw new RuntimeException(
                        "Couldn't deduct database type from database product name '"
                                + databaseProductName
                                + "' in "
                                + databaseTypeMappings.keySet());
            }
            ToolLog.info(() -> "Using database type: %s", databaseType);
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't deduct database type", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                ToolLog.error(e, () -> "Exception while closing the Database connection");
            }
        }
        return databaseType;
    }

    /**
     * 获取数据库类型
     *
     * @param dataSource 数据源
     * @return 返回数据类型字符串
     */
    @Nonnull
    public static DatabaseType parse(@Nonnull DataSource dataSource) {
        try {
            return parse(dataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't deduct database type", e);
        }
    }

    /**
     * 获取数据库类型
     *
     * @return {@code Class<? extends DatabaseDataType>}
     */
    @Nullable
    public Class<? extends DatabaseDataType> getDataType() {
        return dataType;
    }

    /**
     * @return huo
     */
    public Set<String> getAlias() {
        return alias;
    }

    /**
     * 获取数据数据类型列表
     *
     * @return {@code Collection<String>}
     */
    @Nonnull
    public Collection<String> getDatabaseDataTypes() {
        return databaseDataTypes.keySet();
    }

    /**
     * 获取数据库数据类型
     *
     * @param dataType 数据类型
     * @return {@code DatabaseDataType}
     */
    @Nonnull
    public DatabaseDataType getDatabaseDataType(String dataType) {
        DatabaseDataType databaseDataType = databaseDataTypes.get(dataType);
        if (databaseDataType == null) {
            throw new RuntimeException(
                    String.format("Database [%s] hasn't registered dataType [%s]", dataType, dataType));
        }
        return databaseDataType;
    }

    /**
     * 获取Java数据类型
     *
     * @param dataType 数据类型
     * @return {@code Class<?>}
     */
    @Nonnull
    public Class<?> getJavaType(String dataType) {
        return getDatabaseDataType(dataType).getJavaType();
    }
}

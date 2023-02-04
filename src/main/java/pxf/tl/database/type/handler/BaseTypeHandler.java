package pxf.tl.database.type.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.api.Tuple;
import pxf.tl.database.type.JdbcType;
import pxf.tl.util.ToolLog;

import javax.annotation.Nonnull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

/**
 * @author potatoxf
 */
public abstract class BaseTypeHandler<T> implements JdbcTypeHandler<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTypeHandler.class);
    private static final Map<Class<?>, Tuple<Set<?>>> TYPE_CACHE = new ConcurrentHashMap<>();
    /**
     * 原生类型
     */
    private final Type rawType = getSuperclassTypeParameter(getClass());

    protected BaseTypeHandler() {
    }

    public static boolean isSupportJavaType(Class<BaseTypeHandler<?>> type, Class<?> javaType) {
        return false;
    }

    public static boolean isSupportJdbcType(Class<BaseTypeHandler<?>> type, JdbcType jdbcType) {
        return false;
    }

    private static Type getSuperclassTypeParameter(Class<?> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof Class) {
            // try to climb up the hierarchy until meet something useful
            if (BaseTypeHandler.class != genericSuperclass) {
                return getSuperclassTypeParameter(clazz.getSuperclass());
            }

            throw new TypeException("""
                    '%s' extends TypeReference but misses the type parameter.Remove the extension or add a type parameter to it.
                    """.formatted(clazz));
        }

        Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        // TODO remove this when Reflector is fixed to return Types
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType) rawType).getRawType();
        }

        return rawType;
    }

    public final Type getRawType() {
        return rawType;
    }

    @Override
    public final void setParameter(PreparedStatement h, int parameterIndex, T parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
        if (parameter == null) {
            if (jdbcType == null) {
                throw new TypeException("JDBC requires that the JdbcType must be specified for all nullable parameters.");
            }
            try {
                int sqlType = jdbcType.getCode();
                if (sqlType == Types.OTHER) {
                    boolean useSetObject = false;
                    Integer sqlTypeToUse = null;
                    if (true) {
                        try {
                            sqlTypeToUse = h.getParameterMetaData().getParameterType(parameterIndex);
                        } catch (SQLException ex) {
                            ToolLog.error(LOGGER, () -> "JDBC getParameterType call failed - using fallback method instead: %s", ex);
                        }
                    }
                    if (sqlTypeToUse == null) {
                        // Proceed with database-specific checks
                        sqlTypeToUse = Types.NULL;
                        DatabaseMetaData dbmd = h.getConnection().getMetaData();
                        String jdbcDriverName = dbmd.getDriverName();
                        String databaseProductName = dbmd.getDatabaseProductName();
                        if (databaseProductName.startsWith("Informix") ||
                                (jdbcDriverName.startsWith("Microsoft") && jdbcDriverName.contains("SQL Server"))) {
                            // "Microsoft SQL Server JDBC Driver 3.0" versus "Microsoft JDBC Driver 4.0 for SQL Server"
                            useSetObject = true;
                        } else if (databaseProductName.startsWith("DB2") ||
                                jdbcDriverName.startsWith("jConnect") ||
                                jdbcDriverName.startsWith("SQLServer") ||
                                jdbcDriverName.startsWith("Apache Derby")) {
                            sqlTypeToUse = Types.VARCHAR;
                        }
                    }
                    if (useSetObject) {
                        h.setObject(parameterIndex, null);
                    } else {
                        h.setNull(parameterIndex, sqlTypeToUse);
                    }
                } else {
                    h.setNull(parameterIndex, sqlType);
                }
            } catch (Throwable e) {
                throw new SQLException("Error setting null for parameter #" + parameterIndex + " with JdbcType " + jdbcType + " . " +
                        "Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. " +
                        "Cause: " + e, e);
            }
        } else {
            try {
                checkExceptJavaType(parameterIndex, parameter.getClass(), getSet(0), this::isSupportParameterJavaTypes);
                checkExceptJdbcType(parameterIndex, jdbcType, getSet(1), this::isSupportParameterJdbcTypes);
                setNonNullParameter(h, parameterIndex, parameter, jdbcType, typeParameter);
            } catch (Throwable e) {
                throw new SQLException("Error setting non null for parameter #" + parameterIndex + "[" + parameter + "]with JdbcType" + jdbcType + "." +
                        "Try setting a different JdbcType for this parameter or a different configuration property. " +
                        "Cause: " + e, e);
            }
        }
    }

    @Override
    public final T getResult(CallableStatement h, int columnIndex, TypeParameter typeParameter) throws SQLException {
        JdbcType jdbcType = JdbcType.forCode(h.getMetaData().getColumnType(columnIndex));
        T result;
        try {
            checkExceptJdbcType(columnIndex, jdbcType, getSet(2), this::isSupportResultSetJdbcTypes);
            result = getNullableResult(h, columnIndex, jdbcType, typeParameter);
        } catch (Throwable e) {
            throw new SQLException("Error attempting to get column #" + columnIndex + " from callable statement.  Cause: " + e, e);
        }
        if (h.wasNull()) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public final T getResult(ResultSet h, int columnIndex, TypeParameter typeParameter) throws SQLException {
        JdbcType jdbcType = JdbcType.forCode(h.getMetaData().getColumnType(columnIndex));
        T result;
        try {
            checkExceptJdbcType(columnIndex, jdbcType, getSet(2), this::isSupportResultSetJdbcTypes);
            result = getNullableResult(h, columnIndex, jdbcType, typeParameter);
        } catch (Throwable e) {
            throw new SQLException("Error attempting to get column #" + columnIndex + " from result set.  Cause: " + e, e);
        }
        if (h.wasNull()) {
            return null;
        } else {
            return result;
        }
    }

    private void checkExceptJavaType(int columnIndex, Class<?> columnJavaType, Set<Type> javaTypesSet, BiPredicate<Set<Type>, Class<?>> predicate)
            throws SQLException {
        if (predicate.test(javaTypesSet, columnJavaType)) {
            return;
        }
        Class<?> clz = (Class<?>) getRawType();
        String message = "The column(" +
                columnIndex +
                ") type is [" +
                columnJavaType +
                "],but require class " +
                javaTypesSet;
        throw new SQLException(message);
    }

    private void checkExceptJdbcType(int columnIndex, JdbcType columnJdbcType, Set<JdbcType> jdbcTypesSet, BiPredicate<Set<JdbcType>, JdbcType> predicate)
            throws SQLException {
        if (predicate.test(jdbcTypesSet, columnJdbcType)) {
            return;
        }
        Class<?> clz = (Class<?>) getRawType();
        String message = "The column(" +
                columnIndex +
                ") type is [" +
                columnJdbcType +
                "],but require class [" +
                clz +
                "] and sql jdbcTypes is " +
                jdbcTypesSet;
        throw new SQLException(message);
    }

    @SuppressWarnings("unchecked")
    private <E> Set<E> getSet(int index) {
        Class<?> key = getClass();
        Tuple<Set<?>> tuple = TYPE_CACHE.get(key);
        if (tuple == null) {
            tuple = new Tuple<>(getSupportParameterJavaTypes(), getSupportParameterJdbcTypes(), getSupportResultSetJdbcTypes());
            TYPE_CACHE.putIfAbsent(key, tuple);
        }
        return (Set<E>) tuple.get(index);
    }

    protected SQLException throwSQLException(JdbcType jdbcType) {
        return new SQLException("Not Support [" + getRawType() + "] to database type [" + jdbcType + "]");
    }

    //------------------------------------------------------------------------------------------------------------------
    //实现自定义判断
    //------------------------------------------------------------------------------------------------------------------

    protected boolean isSupportParameterJavaTypes(@Nonnull Set<Type> javaTypesSet, @Nonnull Class<?> columnJavaType) {
        return javaTypesSet.contains(columnJavaType);
    }

    protected boolean isSupportParameterJdbcTypes(@Nonnull Set<JdbcType> jdbcTypesSet, @Nonnull JdbcType columnJdbcType) {
        return jdbcTypesSet.contains(columnJdbcType);
    }

    protected boolean isSupportResultSetJdbcTypes(@Nonnull Set<JdbcType> jdbcTypesSet, @Nonnull JdbcType columnJdbcType) {
        return jdbcTypesSet.contains(columnJdbcType);
    }

    //------------------------------------------------------------------------------------------------------------------
    //子类实现支持类型
    //------------------------------------------------------------------------------------------------------------------

    protected Set<Type> getSupportParameterJavaTypes() {
        return Set.of(getRawType());
    }

    protected abstract Set<JdbcType> getSupportParameterJdbcTypes();

    protected Set<JdbcType> getSupportResultSetJdbcTypes() {
        return getSupportParameterJdbcTypes();
    }

    //------------------------------------------------------------------------------------------------------------------
    //子类实现功能
    //------------------------------------------------------------------------------------------------------------------

    public abstract void setNonNullParameter(PreparedStatement h, int parameterIndex, T parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException;

    public abstract T getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException;

    public abstract T getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException;

}

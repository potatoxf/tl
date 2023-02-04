package pxf.tl.database.type.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.database.type.JdbcType;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.SQLXML;
import java.sql.*;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static pxf.tl.database.type.JdbcType.*;

/**
 * @author potatoxf
 */
public sealed abstract class BuiltinTypeHandler<T> extends BaseTypeHandler<T>
        permits BuiltinTypeHandler.ArrayImpl,
        BuiltinTypeHandler.ArrayTypeHandler,
        BuiltinTypeHandler.BlobImpl,
        BuiltinTypeHandler.BooleanImpl,
        BuiltinTypeHandler.BytesImpl,
        BuiltinTypeHandler.ClassifyTypeHandler,
        BuiltinTypeHandler.ClobImpl,
        BuiltinTypeHandler.DateImpl,
        BuiltinTypeHandler.InputStreamImpl,
        BuiltinTypeHandler.NClobImpl,
        BuiltinTypeHandler.ReaderImpl,
        BuiltinTypeHandler.RefImpl,
        BuiltinTypeHandler.RowIdImpl,
        BuiltinTypeHandler.SQLXMLImpl,
        BuiltinTypeHandler.CharSequenceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BuiltinTypeHandler.class);
    protected final TypeConfiguration typeConfiguration;

    public BuiltinTypeHandler(TypeConfiguration typeConfiguration) {
        this.typeConfiguration = Objects.requireNonNull(typeConfiguration, "The type configuration must be not null");
    }

    public static BuiltinTypeHandler<?> getTypeHandler(Class<?> type, Map<Class<?>, BuiltinTypeHandlerFactory> builtinTypeHandlerFactories) throws SQLException {
        if (type == null) {
            throw new SQLException();
        }
        BuiltinTypeHandlerFactory builtinTypeHandleFactory;
        if (Number.class.isAssignableFrom(type)) {
            builtinTypeHandleFactory = builtinTypeHandlerFactories.get(Number.class);
        } else if (Date.class.isAssignableFrom(type)) {
            builtinTypeHandleFactory = builtinTypeHandlerFactories.get(Date.class);
        } else if (Enum.class.isAssignableFrom(type)) {
            builtinTypeHandleFactory = builtinTypeHandlerFactories.get(Enum.class);
        } else {
            builtinTypeHandleFactory = builtinTypeHandlerFactories.get(type);
        }
        if (builtinTypeHandleFactory == null) {
            throw new SQLException("");
        }
        return builtinTypeHandleFactory.apply(type);
    }

    static Map<Class<?>, BuiltinTypeHandlerFactory> createBuiltinTypeHandlerFactories(TypeConfiguration typeConfiguration) {
        return Map.ofEntries(
                dynamicNew(Enum.class, c -> new EnumImpl(typeConfiguration, c)),
                staticNew(Boolean.class, new BooleanImpl(typeConfiguration)),
                dynamicNew(Number.class, c -> new NumberImpl(typeConfiguration, c)),
                staticNew(CharSequence.class, new CharSequenceImpl(typeConfiguration)),
                staticNew(Date.class, new DateImpl(typeConfiguration)),
                staticNew(byte[].class, new BytesImpl(typeConfiguration)),
                staticNew(Reader.class, new ReaderImpl(typeConfiguration)),
                staticNew(InputStream.class, new InputStreamImpl(typeConfiguration)),
                staticNew(Clob.class, new ClobImpl(typeConfiguration)),
                staticNew(NClob.class, new NClobImpl(typeConfiguration)),
                staticNew(Blob.class, new BlobImpl(typeConfiguration)),
                staticNew(Array.class, new ArrayImpl(typeConfiguration)),
                staticNew(SQLXML.class, new SQLXMLImpl(typeConfiguration)),
                staticNew(Ref.class, new RefImpl(typeConfiguration)),
                staticNew(RowId.class, new RowIdImpl(typeConfiguration))
        );
    }

    private static Map.Entry<Class<?>, BuiltinTypeHandlerFactory> staticNew(Class<?> type, BuiltinTypeHandler<?> typeHandler) {
        return Map.entry(type, tClass -> typeHandler);
    }

    private static Map.Entry<Class<?>, BuiltinTypeHandlerFactory> dynamicNew(Class<?> type, BuiltinTypeHandlerFactory typeHandlerFactory) {
        return Map.entry(type, typeHandlerFactory);
    }

    protected String getString(Clob clob, TypeParameter typeParameter) throws SQLException {
        try {
            StringBuilder sb = new StringBuilder();
            Reader characterStream = clob.getCharacterStream();
            CharBuffer buffer = CharBuffer.allocate(8096);
            int len;
            while ((len = characterStream.read(buffer)) != -1) {
                sb.append(buffer, 0, len);
            }
            characterStream.close();
            return sb.toString();
        } catch (IOException e) {
            throw new SQLException("", e);
        }
    }

    protected String getString(InputStream inputStream, TypeParameter typeParameter) throws SQLException {
        try {
            Charset charset = typeParameter.getCharset();
            if (charset == null) {
                return new String(inputStream.readAllBytes());
            } else {
                return new String(inputStream.readAllBytes(), charset);
            }
        } catch (IOException e) {
            throw new SQLException("", e);
        }
    }

    private interface BuiltinTypeHandlerFactory extends Function<Class<?>, BuiltinTypeHandler<?>> {
    }

    static final class BooleanImpl extends BuiltinTypeHandler<Boolean> {
        public BooleanImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(BIT, TINYINT, SMALLINT, INTEGER, BIGINT, CHAR, VARCHAR);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, Boolean parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            h.setBoolean(parameterIndex, parameter);
        }

        @Override
        public Boolean getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return h.getBoolean(columnIndex);
        }

        @Override
        public Boolean getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return h.getBoolean(columnIndex);
        }
    }

    static final class CharSequenceImpl extends BuiltinTypeHandler<CharSequence> {
        public CharSequenceImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR, CLOB, NCLOB, BINARY, VARBINARY, BLOB, SQLXML);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, CharSequence parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            String value = parameter.toString();
            switch (jdbcType) {
                case CHAR, VARCHAR, LONGVARCHAR -> {
                    h.setString(parameterIndex, value);
                }
                case NCHAR, NVARCHAR, LONGNVARCHAR -> {
                    h.setNString(parameterIndex, value);
                }
                case CLOB -> {
                    if (value.length() > 4000) {
                        // Necessary for older Oracle drivers, in particular when running against an Oracle 10 database.
                        // Should also work fine against other drivers/databases since it uses standard JDBC 4.0 API.
                        h.setClob(parameterIndex, new StringReader(value), value.length());
                    } else {
                        // Fallback: setString or setNString binding
                        h.setString(parameterIndex, value);
                    }
                }
                case NCLOB -> {
                    if (value.length() > 4000) {
                        // Necessary for older Oracle drivers, in particular when running against an Oracle 10 database.
                        // Should also work fine against other drivers/databases since it uses standard JDBC 4.0 API.
                        h.setNClob(parameterIndex, new StringReader(value), value.length());
                    } else {
                        // Fallback: setString or setNString binding
                        h.setNString(parameterIndex, value);
                    }
                }
                case BINARY, VARBINARY, BLOB -> {
                    h.setBinaryStream(parameterIndex, new ByteArrayInputStream(value.getBytes(typeParameter.getCharset())));
                }
                case SQLXML -> {
                    SQLXML sqlxml = h.getConnection().createSQLXML();
                    try {
                        sqlxml.setString(value);
                        h.setSQLXML(parameterIndex, sqlxml);
                    } finally {
                        sqlxml.free();
                    }
                }
                default -> {

                }
            }
        }

        @Override
        public CharSequence getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            switch (jdbcType) {
                case CHAR, VARCHAR, LONGVARCHAR -> {
                    return h.getString(columnIndex);
                }
                case NCHAR, NVARCHAR, LONGNVARCHAR -> {
                    return h.getNString(columnIndex);
                }
                case CLOB -> {
                    return getString(h.getClob(columnIndex), typeParameter);
                }
                case NCLOB -> {
                    return getString(h.getNClob(columnIndex), typeParameter);
                }
                case BINARY, VARBINARY, BLOB -> {
                    return getString(h.getBlob(columnIndex).getBinaryStream(), typeParameter);
                }
                case SQLXML -> {
                    return convertToString(h.getSQLXML(columnIndex));
                }
                default -> {

                }
            }
            return null;
        }

        @Override
        public CharSequence getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            switch (jdbcType) {
                case CHAR, VARCHAR, LONGVARCHAR -> {
                    return h.getString(columnIndex);
                }
                case NCHAR, NVARCHAR, LONGNVARCHAR -> {
                    return h.getNString(columnIndex);
                }
                case CLOB -> {
                    return getString(h.getClob(columnIndex), typeParameter);
                }
                case NCLOB -> {
                    return getString(h.getNClob(columnIndex), typeParameter);
                }
                case BINARY, VARBINARY -> {
                    return getString(h.getBinaryStream(columnIndex), typeParameter);
                }
                case BLOB -> {
                    return getString(h.getBlob(columnIndex).getBinaryStream(), typeParameter);
                }
                case SQLXML -> {
                    return convertToString(h.getSQLXML(columnIndex));
                }
                default -> {

                }
            }
            return null;
        }


        private String convertToString(SQLXML sqlxml) throws SQLException {
            if (sqlxml == null) {
                return null;
            }
            try {
                return sqlxml.getString();
            } finally {
                sqlxml.free();
            }
        }
    }

    static final class DateImpl extends BuiltinTypeHandler<Date> {
        public DateImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        private Date parseDate(String datetime, TypeParameter typeParameter) {
            List<DateTimeFormatter> dateTimeFormatters = typeConfiguration.getDateTimeFormatters();
            Calendar calendar = typeParameter.getCalendar();
            if (calendar == null) {
                for (DateTimeFormatter dateTimeFormatter : dateTimeFormatters) {
                    try {
                        return Date.from(Instant.from(dateTimeFormatter.parse(datetime)));
                    } catch (DateTimeParseException ignored) {
                    }
                }
            } else {
                for (DateTimeFormatter dateTimeFormatter : dateTimeFormatters) {
                    try {
                        calendar.setTimeInMillis(Instant.from(dateTimeFormatter.parse(datetime)).toEpochMilli());
                        return calendar.getTime();
                    } catch (DateTimeParseException ignored) {

                    }
                }
            }
            return null;
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(DATE, TIMESTAMP, TIMESTAMP_WITH_TIMEZONE, TIME, TIME_WITH_TIMEZONE, CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, Date parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            switch (jdbcType) {
                case TIME -> {
                    h.setTime(parameterIndex, new Time(parameter.getTime()));
                }
                case TIMESTAMP -> {
                    h.setTimestamp(parameterIndex, new Timestamp(parameter.getTime()));
                }
                case TIME_WITH_TIMEZONE -> {
                    Calendar calendar = typeParameter.getCalendar();
                    Date date;
                    if (calendar == null) {
                        h.setTime(parameterIndex, new Time(parameter.getTime()));
                    } else {
                        h.setTime(parameterIndex, new Time(parameter.getTime()), calendar);
                    }
                }
                case TIMESTAMP_WITH_TIMEZONE -> {
                    Calendar calendar = typeParameter.getCalendar();
                    Date date;
                    if (calendar == null) {
                        h.setTimestamp(parameterIndex, new Timestamp(parameter.getTime()));
                    } else {
                        h.setTimestamp(parameterIndex, new Timestamp(parameter.getTime()), calendar);
                    }
                }
                case DATE -> {
                    Calendar calendar = typeParameter.getCalendar();
                    Date date;
                    if (calendar == null) {
                        h.setDate(parameterIndex, new java.sql.Date(parameter.getTime()));
                    } else {
                        h.setDate(parameterIndex, new java.sql.Date(parameter.getTime()), calendar);
                    }
                }
                case default -> throw throwSQLException(jdbcType);
            }
        }

        @Override
        public Date getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            switch (jdbcType) {
                case TIME -> {
                    return h.getTime(columnIndex);
                }
                case TIMESTAMP -> {
                    return h.getTimestamp(columnIndex);
                }
                case TIME_WITH_TIMEZONE -> {
                    Calendar calendar = typeParameter.getCalendar();
                    Date date;
                    if (calendar == null) {
                        date = h.getTime(columnIndex);
                    } else {
                        date = h.getTime(columnIndex, calendar);
                    }
                    return date;
                }
                case TIMESTAMP_WITH_TIMEZONE -> {
                    Calendar calendar = typeParameter.getCalendar();
                    Date date;
                    if (calendar == null) {
                        date = h.getTimestamp(columnIndex);
                    } else {
                        date = h.getTimestamp(columnIndex, calendar);
                    }
                    return date;
                }
                case DATE -> {
                    Calendar calendar = typeParameter.getCalendar();
                    Date date;
                    if (calendar == null) {
                        date = h.getDate(columnIndex);
                    } else {
                        date = h.getDate(columnIndex, calendar);
                    }
                    return date;
                }
                case CHAR,
                        VARCHAR,
                        LONGVARCHAR -> {
                    return parseDate(h.getString(columnIndex), typeParameter);
                }
                case NCHAR,
                        NVARCHAR,
                        LONGNVARCHAR -> {
                    return parseDate(h.getNString(columnIndex), typeParameter);
                }
            }
            return null;
        }

        @Override
        public Date getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            switch (jdbcType) {
                case TIME -> {
                    return h.getTime(columnIndex);
                }
                case TIMESTAMP -> {
                    return h.getTimestamp(columnIndex);
                }
                case TIME_WITH_TIMEZONE -> {
                    Calendar calendar = typeParameter.getCalendar();
                    Date date;
                    if (calendar == null) {
                        date = h.getTime(columnIndex);
                    } else {
                        date = h.getTime(columnIndex, calendar);
                    }
                    return date;
                }
                case TIMESTAMP_WITH_TIMEZONE -> {
                    Calendar calendar = typeParameter.getCalendar();
                    Date date;
                    if (calendar == null) {
                        date = h.getTimestamp(columnIndex);
                    } else {
                        date = h.getTimestamp(columnIndex, calendar);
                    }
                    return date;
                }
                case DATE -> {
                    Calendar calendar = typeParameter.getCalendar();
                    Date date;
                    if (calendar == null) {
                        date = h.getDate(columnIndex);
                    } else {
                        date = h.getDate(columnIndex, calendar);
                    }
                    return date;
                }
                case CHAR,
                        VARCHAR,
                        LONGVARCHAR -> {
                    return parseDate(h.getString(columnIndex), typeParameter);
                }
                case NCHAR,
                        NVARCHAR,
                        LONGNVARCHAR -> {
                    return parseDate(h.getNString(columnIndex), typeParameter);
                }
            }
            return null;
        }
    }

    static final class BytesImpl extends BuiltinTypeHandler<byte[]> {
        public BytesImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(BINARY, VARBINARY, LONGVARBINARY, BLOB);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, byte[] parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            h.setBytes(parameterIndex, parameter);
        }

        @Override
        public byte[] getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            if (jdbcType == BLOB) {
                Blob blob = h.getBlob(columnIndex);
                if (blob != null) {
                    return blob.getBytes(1, (int) blob.length());
                }
            } else {
                return h.getBytes(columnIndex);
            }
            return null;
        }

        @Override
        public byte[] getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            if (jdbcType == BLOB) {
                Blob blob = h.getBlob(columnIndex);
                if (blob != null) {
                    return blob.getBytes(1, (int) blob.length());
                }
            } else {
                return h.getBytes(columnIndex);
            }
            return null;
        }
    }

    static final class ReaderImpl extends BuiltinTypeHandler<Reader> {
        public ReaderImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR, CLOB, NCLOB);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, Reader parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {

        }

        @Override
        public Reader getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return null;
        }

        @Override
        public Reader getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            switch (jdbcType) {
                case CHAR,
                        VARCHAR,
                        LONGVARCHAR -> {
                    return h.getCharacterStream(columnIndex);
                }
                case NCHAR,
                        NVARCHAR,
                        LONGNVARCHAR -> {
                    return h.getNCharacterStream(columnIndex);
                }
                case CLOB -> {
                    return h.getClob(columnIndex).getCharacterStream();
                }
                case NCLOB -> {
                    return h.getNClob(columnIndex).getCharacterStream();
                }
            }
            return null;
        }
    }

    static final class InputStreamImpl extends BuiltinTypeHandler<InputStream> {
        public InputStreamImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(BINARY, VARBINARY, LONGVARBINARY);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, InputStream parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {

        }

        @Override
        public InputStream getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return null;
        }

        @Override
        public InputStream getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return h.getBinaryStream(columnIndex);
        }
    }

    static final class ClobImpl extends BuiltinTypeHandler<Clob> {
        public ClobImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(CLOB);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, Clob parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {

        }

        @Override
        public Clob getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return null;
        }

        @Override
        public Clob getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return h.getClob(columnIndex);
        }
    }

    static final class NClobImpl extends BuiltinTypeHandler<NClob> {
        public NClobImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(NCLOB);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, NClob parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {

        }

        @Override
        public NClob getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return null;
        }

        @Override
        public NClob getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return h.getNClob(columnIndex);
        }
    }

    static final class BlobImpl extends BuiltinTypeHandler<Blob> {
        public BlobImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(BLOB);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, Blob parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {

        }

        @Override
        public Blob getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return null;
        }

        @Override
        public Blob getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return h.getBlob(columnIndex);
        }
    }

    static final class ArrayImpl extends BuiltinTypeHandler<Array> {
        public ArrayImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(ARRAY);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, Array parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {

        }

        @Override
        public Array getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return null;
        }

        @Override
        public Array getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return h.getArray(columnIndex);
        }
    }

    static final class SQLXMLImpl extends BuiltinTypeHandler<SQLXML> {
        public SQLXMLImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(SQLXML);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, SQLXML parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {

        }

        @Override
        public SQLXML getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return null;
        }

        @Override
        public SQLXML getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return h.getSQLXML(columnIndex);
        }
    }

    static final class RefImpl extends BuiltinTypeHandler<Ref> {
        public RefImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(REF);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, Ref parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {

        }

        @Override
        public Ref getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return null;
        }

        @Override
        public Ref getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return h.getRef(columnIndex);
        }
    }

    static final class RowIdImpl extends BuiltinTypeHandler<RowId> {
        public RowIdImpl(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(ROWID);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, RowId parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {

        }

        @Override
        public RowId getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return null;
        }

        @Override
        public RowId getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return h.getRowId(columnIndex);
        }
    }

    static final class ArrayTypeHandler extends BuiltinTypeHandler<Object> {

        private static final ConcurrentHashMap<Class<?>, JdbcType> STANDARD_MAPPING;

        static {
            STANDARD_MAPPING = new ConcurrentHashMap<>();
            STANDARD_MAPPING.put(BigDecimal.class, NUMERIC);
            STANDARD_MAPPING.put(BigInteger.class, BIGINT);
            STANDARD_MAPPING.put(boolean.class, BOOLEAN);
            STANDARD_MAPPING.put(Boolean.class, BOOLEAN);
            STANDARD_MAPPING.put(byte[].class, VARBINARY);
            STANDARD_MAPPING.put(byte.class, TINYINT);
            STANDARD_MAPPING.put(Byte.class, TINYINT);
            STANDARD_MAPPING.put(Calendar.class, TIMESTAMP);
            STANDARD_MAPPING.put(java.sql.Date.class, DATE);
            STANDARD_MAPPING.put(Date.class, TIMESTAMP);
            STANDARD_MAPPING.put(double.class, DOUBLE);
            STANDARD_MAPPING.put(Double.class, DOUBLE);
            STANDARD_MAPPING.put(float.class, REAL);
            STANDARD_MAPPING.put(Float.class, REAL);
            STANDARD_MAPPING.put(int.class, INTEGER);
            STANDARD_MAPPING.put(Integer.class, INTEGER);
            STANDARD_MAPPING.put(LocalDate.class, DATE);
            STANDARD_MAPPING.put(LocalDateTime.class, TIMESTAMP);
            STANDARD_MAPPING.put(LocalTime.class, TIME);
            STANDARD_MAPPING.put(long.class, BIGINT);
            STANDARD_MAPPING.put(Long.class, BIGINT);
            STANDARD_MAPPING.put(OffsetDateTime.class, TIMESTAMP_WITH_TIMEZONE);
            STANDARD_MAPPING.put(OffsetTime.class, TIME_WITH_TIMEZONE);
            STANDARD_MAPPING.put(Short.class, SMALLINT);
            STANDARD_MAPPING.put(String.class, VARCHAR);
            STANDARD_MAPPING.put(Time.class, TIME);
            STANDARD_MAPPING.put(Timestamp.class, TIMESTAMP);
            STANDARD_MAPPING.put(URL.class, DATALINK);
        }

        public ArrayTypeHandler(TypeConfiguration typeConfiguration) {
            super(typeConfiguration);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return null;
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, Object parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            if (parameter instanceof Array) {
                // it's the user's responsibility to properly free() the Array instance
                h.setArray(parameterIndex, (Array) parameter);
            } else {
                if (!parameter.getClass().isArray()) {
                    throw new TypeException(
                            "ArrayType Handler requires SQL array or java array parameter and does not support type "
                                    + parameter.getClass());
                }
                Class<?> componentType = parameter.getClass().getComponentType();
                String arrayTypeName = resolveTypeName(componentType);
                Array array = h.getConnection().createArrayOf(arrayTypeName, (Object[]) parameter);
                h.setArray(parameterIndex, array);
                array.free();
            }
        }

        @Override
        public Object getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return extractArray(h.getArray(columnIndex));
        }

        @Override
        public Object getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return extractArray(h.getArray(columnIndex));
        }

        private String resolveTypeName(Class<?> type) {
            return STANDARD_MAPPING.getOrDefault(type, JAVA_OBJECT).name();
        }

        private Object extractArray(Array array) throws SQLException {
            if (array == null) {
                return null;
            }
            Object result = array.getArray();
            array.free();
            return result;
        }

    }

    static sealed abstract class ClassifyTypeHandler<T> extends BuiltinTypeHandler<T> {
        protected final Class<T> targetType;

        public ClassifyTypeHandler(TypeConfiguration typeConfiguration, Class<T> targetType) {
            super(typeConfiguration);
            if (isSupportParameterJavaTypes(getSupportParameterJavaTypes(), targetType)) {
                this.targetType = targetType;
            } else {
                throw new TypeException("No support java type[" + targetType + "]");
            }
        }
    }

    @SuppressWarnings("unchecked")
    static final class EnumImpl<E extends Enum<E>> extends ClassifyTypeHandler<E> {
        public EnumImpl(TypeConfiguration typeConfiguration, Class<E> targetType) {
            super(typeConfiguration, targetType);
        }

        @Override
        protected boolean isSupportParameterJavaTypes(@Nonnull Set<Type> javaTypesSet, @Nonnull Class<?> columnJavaType) {
            for (Type supportJavaType : javaTypesSet) {
                if (supportJavaType instanceof Class<?>) {
                    if (((Class<?>) supportJavaType).isAssignableFrom(columnJavaType)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(BIT, TINYINT, SMALLINT, INTEGER, BIGINT, CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, E parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            switch (jdbcType) {
                case BIT, TINYINT, SMALLINT, INTEGER, BIGINT -> {
                    h.setInt(parameterIndex, parameter.ordinal());
                }
                case NCHAR, NVARCHAR, LONGNVARCHAR -> {
                    h.setNString(parameterIndex, parameter.name());
                }
                default -> {
                    h.setString(parameterIndex, parameter.name());
                }
            }
        }

        @Override
        public E getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            switch (jdbcType) {
                case BIT, TINYINT, SMALLINT, INTEGER, BIGINT -> {
                    int i = h.getInt(columnIndex);
                    Object[] enumConstants = targetType.getEnumConstants();
                    if (i >= 0 && i < enumConstants.length) {
                        return (E) enumConstants[i];
                    } else {
                        throw new TypeException();
                    }
                }
                case NCHAR, NVARCHAR, LONGNVARCHAR -> {
                    return Enum.valueOf(targetType, h.getNString(columnIndex));
                }
                default -> {
                    return Enum.valueOf(targetType, h.getString(columnIndex));
                }
            }
        }

        @Override
        public E getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            switch (jdbcType) {
                case BIT, TINYINT, SMALLINT, INTEGER, BIGINT -> {
                    int i = h.getInt(columnIndex);
                    Object[] enumConstants = targetType.getEnumConstants();
                    if (i >= 0 && i < enumConstants.length) {
                        return (E) enumConstants[i];
                    } else {
                        throw new TypeException();
                    }
                }
                case NCHAR, NVARCHAR, LONGNVARCHAR -> {
                    return Enum.valueOf(targetType, h.getNString(columnIndex));
                }
                default -> {
                    return Enum.valueOf(targetType, h.getString(columnIndex));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    static final class NumberImpl<E extends Number> extends ClassifyTypeHandler<E> {

        public NumberImpl(TypeConfiguration typeConfiguration, Class<E> targetType) {
            super(typeConfiguration, targetType);
        }

        @Override
        protected Set<Type> getSupportParameterJavaTypes() {
            return Set.of(Integer.class, Long.class, Double.class, BigDecimal.class, BigInteger.class, Float.class, Short.class, Byte.class);
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(BIT, BOOLEAN, NUMERIC, TINYINT, SMALLINT, INTEGER, BIGINT, FLOAT, DOUBLE, DECIMAL,
                    DATE, TIME, TIMESTAMP, CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, Number parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            switch (jdbcType) {
                case BIT, TINYINT -> {
                    h.setByte(parameterIndex, parameter.byteValue());
                }
                case BOOLEAN -> {
                    h.setBoolean(parameterIndex, parameter.intValue() != 0);
                }
                case SMALLINT -> {
                    h.setShort(parameterIndex, parameter.shortValue());
                }
                case INTEGER -> {
                    h.setInt(parameterIndex, parameter.intValue());
                }
                case BIGINT -> {
                    h.setLong(parameterIndex, parameter.longValue());
                }
                case NUMERIC, FLOAT, DOUBLE, DECIMAL -> {
                    h.setBigDecimal(parameterIndex, BigDecimal.valueOf(parameter.doubleValue()));
                }
                case CHAR, VARCHAR, LONGVARCHAR -> {
                    h.setString(parameterIndex, parameter.toString());
                }
                case NCHAR, NVARCHAR, LONGNVARCHAR -> {
                    h.setNString(parameterIndex, parameter.toString());
                }
                case DATE -> {
                    h.setDate(parameterIndex, new java.sql.Date(parameter.longValue()));
                }
                case TIME -> {
                    h.setTime(parameterIndex, new Time(parameter.longValue()));
                }
                case TIMESTAMP -> {
                    h.setTimestamp(parameterIndex, new Timestamp(parameter.longValue()));
                }
                default -> throw throwSQLException(jdbcType);
            }
        }

        @Override
        public E getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return (E) convert(getNullableNumberResult(h, columnIndex, jdbcType, typeParameter));
        }

        @Override
        public E getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return (E) convert(getNullableNumberResult(h, columnIndex, jdbcType, typeParameter));
        }

        private Number convert(Number number) {
            if (targetType == number.getClass()) {
                return number;
            }
            if (Integer.class == targetType) {
                return number.intValue();
            } else if (Long.class == targetType) {
                return number.longValue();
            } else if (Double.class == targetType) {
                return number.doubleValue();
            } else if (BigDecimal.class == targetType) {
                return BigDecimal.valueOf(number.doubleValue());
            } else if (BigInteger.class == targetType) {
                return BigInteger.valueOf(number.longValue());
            } else if (Float.class == targetType) {
                return number.floatValue();
            } else if (Short.class == targetType) {
                return number.shortValue();
            } else if (Byte.class == targetType) {
                return number.byteValue();
            } else {
                throw new IllegalStateException("Unexpected value: " + number);
            }
        }

        private Number getNullableNumberResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            switch (jdbcType) {
                case BIT -> {
                    return ((Number) h.getByte(columnIndex)).intValue();
                }
                case BOOLEAN -> {
                    return h.getBoolean(columnIndex) ? 1 : 0;
                }
                case NUMERIC, TINYINT, SMALLINT, INTEGER, BIGINT, FLOAT, DOUBLE, DECIMAL -> {
                    return h.getBigDecimal(columnIndex);
                }
                case CHAR, VARCHAR, LONGVARCHAR -> {
                    return new BigDecimal(h.getString(columnIndex));
                }
                case NCHAR, NVARCHAR, LONGNVARCHAR -> {
                    return new BigDecimal(h.getNString(columnIndex));
                }
                case DATE -> {
                    return h.getDate(columnIndex).getTime();
                }
                case TIME -> {
                    return h.getTime(columnIndex).getTime();
                }
                case TIMESTAMP -> {
                    return h.getTimestamp(columnIndex).getTime();
                }
                default -> throw throwSQLException(jdbcType);
            }
        }

        private Number getNullableNumberResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            switch (jdbcType) {
                case BIT -> {
                    return ((Number) h.getByte(columnIndex)).intValue();
                }
                case BOOLEAN -> {
                    return h.getBoolean(columnIndex) ? 1 : 0;
                }
                case NUMERIC, TINYINT, SMALLINT, INTEGER, BIGINT, FLOAT, DOUBLE, DECIMAL -> {
                    return h.getBigDecimal(columnIndex);
                }
                case CHAR, VARCHAR, LONGVARCHAR -> {
                    return new BigDecimal(h.getString(columnIndex));
                }
                case NCHAR, NVARCHAR, LONGNVARCHAR -> {
                    return new BigDecimal(h.getNString(columnIndex));
                }
                case DATE -> {
                    return h.getDate(columnIndex).getTime();
                }
                case TIME -> {
                    return h.getTime(columnIndex).getTime();
                }
                case TIMESTAMP -> {
                    return h.getTimestamp(columnIndex).getTime();
                }
                default -> throw throwSQLException(jdbcType);
            }
        }
    }

    static final class TemporalImpl<E extends Temporal> extends ClassifyTypeHandler<E> {
        public TemporalImpl(TypeConfiguration typeConfiguration, Class<E> targetType) {
            super(typeConfiguration, targetType);
        }

        @Override
        protected Set<Type> getSupportParameterJavaTypes() {
            return Set.of(LocalDate.class, LocalTime.class, LocalDateTime.class,
                    ZonedDateTime.class, OffsetDateTime.class, OffsetTime.class,
                    YearMonth.class, Year.class, Month.class, Instant.class,
                    JapaneseDate.class
            );
        }

        @Override
        protected Set<JdbcType> getSupportParameterJdbcTypes() {
            return Set.of(DATE, TIMESTAMP, TIMESTAMP_WITH_TIMEZONE, TIME, TIME_WITH_TIMEZONE, CHAR, VARCHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR);
        }

        @Override
        public void setNonNullParameter(PreparedStatement h, int parameterIndex, E parameter, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {

        }

        @Override
        public E getNullableResult(CallableStatement h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return null;
        }

        @Override
        public E getNullableResult(ResultSet h, int columnIndex, JdbcType jdbcType, TypeParameter typeParameter) throws SQLException {
            return null;
        }
    }


}

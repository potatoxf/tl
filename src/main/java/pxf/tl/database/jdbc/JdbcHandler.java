package pxf.tl.database.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.function.FunctionThrow;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;

/**
 * @author potatoxf
 */
public class JdbcHandler {

    private static final Logger logger = LoggerFactory.getLogger(JdbcHandler.class);
    @Nonnull
    private final JdbcConfig jdbcConfig;

    public JdbcHandler(@Nonnull JdbcConfig jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
    }


    @Nullable
    private <T> T execute(FunctionThrow<Connection, PreparedStatement, SQLException> psc, FunctionThrow<PreparedStatement, T, SQLException> action, boolean closeResources) {

//        Connection con = DataSourceUtils.getConnection(obtainDataSource());
        PreparedStatement ps = null;
        try {
            Connection con = jdbcConfig.getDataSource().getConnection();
            ps = psc.applyThrow(con);
            applyStatementSettings(ps);
            T result = action.applyThrow(ps);
            handleWarnings(ps);
            return result;
        } catch (SQLException ex) {
        } finally {
            if (closeResources) {
            }
        }
        return null;
    }

    protected void applyStatementSettings(Statement stmt) throws SQLException {
        int fetchSize = jdbcConfig.getFetchSize();
        if (fetchSize != -1) {
            stmt.setFetchSize(fetchSize);
        }
        int maxRows = jdbcConfig.getMaxRows();
        if (maxRows != -1) {
            stmt.setMaxRows(maxRows);
        }
        if (jdbcConfig.getQueryTimeout() >= 0) {
            // No current transaction timeout -> apply specified value.
            stmt.setQueryTimeout(jdbcConfig.getQueryTimeout());
        }
    }

    protected void handleWarnings(Statement stmt) throws SQLException {
        if (jdbcConfig.isIgnoreWarnings()) {
            if (logger.isDebugEnabled()) {
                SQLWarning warningToLog = stmt.getWarnings();
                while (warningToLog != null) {
                    logger.debug("SQLWarning ignored: SQL state '" + warningToLog.getSQLState() + "', error code '" +
                            warningToLog.getErrorCode() + "', message [" + warningToLog.getMessage() + "]");
                    warningToLog = warningToLog.getNextWarning();
                }
            }
        } else {
            handleWarnings(stmt.getWarnings());
        }
    }

    protected void handleWarnings(@Nullable SQLWarning warning) throws SQLException {
        if (warning != null) {
            throw new SQLException("Warning not ignored", warning);
        }
    }

}

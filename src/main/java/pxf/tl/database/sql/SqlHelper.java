package pxf.tl.database.sql;


import pxf.tl.function.ConsumerThrow;
import pxf.tl.util.ToolIO;
import pxf.tl.util.ToolNumber;
import pxf.tl.util.ToolString;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL助手类
 *
 * @author potatoxf
 */
public class SqlHelper {
    private static final Pattern DELIMITER_PATTERN =
            Pattern.compile(
                    "^\\s*((--)|(//))?\\s*(//)?\\s*@DELIMITER\\s+([^\\s]+)", Pattern.CASE_INSENSITIVE);

    /**
     * This is an internal testing utility.<br>
     * You are welcome to use this class for your own purposes,<br>
     * but if there is some feature/enhancement you need for your own usage,<br>
     * please make and modify your own copy instead of sending us an enhancement request.<br>
     *
     * @param connection
     * @param reader
     * @param defaultDelimiter
     * @param lineSeparator
     * @param throwWarning
     * @param escapeProcessing
     * @param stopOnError
     * @param sendFullScript
     * @param fullLineDelimiter
     * @param autoCommit
     * @param logger
     * @param errorLogger
     * @return
     */
    public static boolean runScript(
            final Connection connection,
            final Reader reader,
            final String defaultDelimiter,
            final String lineSeparator,
            final boolean throwWarning,
            final boolean escapeProcessing,
            final boolean stopOnError,
            final boolean sendFullScript,
            final boolean fullLineDelimiter,
            final boolean autoCommit,
            final Consumer<String> logger,
            final Consumer<String> errorLogger) {
        boolean hasError = false;
        try {
            if (autoCommit != connection.getAutoCommit()) {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException t) {
            hasError = true;
            errorLogger.accept("Could not set AutoCommit to " + autoCommit + ". Cause: " + t);
        }

        StringBuilder script = new StringBuilder();
        BufferedReader lineReader = new BufferedReader(reader);
        ConsumerThrow<String, SQLException> executeStatement =
                (c) -> {
                    try (Statement statement = connection.createStatement()) {
                        statement.setEscapeProcessing(escapeProcessing);
                        String sql = c.replace("\r\n", "\n");
                        try {
                            boolean hasResults = statement.execute(sql);
                            if (throwWarning) {
                                while (hasResults || statement.getUpdateCount() != -1) {
                                    // In Oracle, CREATE PROCEDURE, FUNCTION, etc. returns warning
                                    // instead of throwing exception if there is compilation error.
                                    SQLWarning warning = statement.getWarnings();
                                    if (warning != null) {
                                        throw warning;
                                    }
                                    if (hasResults) {
                                        try (ResultSet rs = statement.getResultSet()) {
                                            ResultSetMetaData md = rs.getMetaData();
                                            int cols = md.getColumnCount();
                                            for (int i = 0; i < cols; i++) {
                                                String name = md.getColumnLabel(i + 1);
                                                logger.accept(name + "\t");
                                            }
                                            logger.accept("\n");
                                            while (rs.next()) {
                                                for (int i = 0; i < cols; i++) {
                                                    String value = rs.getString(i + 1);
                                                    logger.accept(value + "\t");
                                                }
                                                logger.accept("\n");
                                            }
                                        } catch (SQLException e) {
                                            errorLogger.accept("Error printing results: " + e.getMessage());
                                        }
                                    }
                                    hasResults = statement.getMoreResults();
                                }
                            }
                        } catch (SQLWarning e) {
                            throw e;
                        } catch (SQLException e) {
                            if (stopOnError) {
                                throw e;
                            } else {
                                errorLogger.accept("Error executing: " + c + ".  Cause: " + e);
                            }
                        }
                    }
                };

        try {
            if (sendFullScript) {
                String command = ToolIO.readAllString(lineReader);
                logger.accept(command);
                executeStatement.accept(command);
            } else {
                String line;
                String delimiter = defaultDelimiter;
                while ((line = lineReader.readLine()) != null) {
                    String trimmedLine = line.trim();
                    if (trimmedLine.startsWith("//") || trimmedLine.startsWith("--")) {
                        Matcher matcher = DELIMITER_PATTERN.matcher(trimmedLine);
                        if (matcher.find()) {
                            delimiter = matcher.group(5);
                        }
                        logger.accept(trimmedLine);
                    } else if (!fullLineDelimiter && trimmedLine.contains(delimiter)
                            || fullLineDelimiter && trimmedLine.equals(delimiter)) {
                        script.append(line, 0, line.lastIndexOf(delimiter));
                        script.append(lineSeparator);
                        String command = script.toString();
                        logger.accept(command);
                        executeStatement.accept(command);
                        script.setLength(0);
                    } else if (trimmedLine.length() > 0) {
                        script.append(line);
                        script.append(lineSeparator);
                    }
                }
                if (script != null && script.toString().trim().length() > 0) {
                    throw new RuntimeException(
                            "Line missing end-of-line terminator (" + delimiter + ") => " + script);
                }
            }
        } catch (Exception e) {
            hasError = true;
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
            } catch (SQLException t) {
                errorLogger.accept("Could not rollback transaction. Cause: " + t);
            }
            errorLogger.accept("Error executing: " + script + ".  Cause: " + e);
        } finally {
            try {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
            } catch (SQLException t) {
                hasError = true;
                errorLogger.accept("Could not commit transaction. Cause: " + t);
            }
        }

        return hasError;
    }

    /**
     * 打印sql日志
     *
     * @param sql    sql语句
     * @param args   参数
     * @param logger 日志打印器
     */
    public static void log(String sql, List<Object> args, Consumer<String> logger) {
        if (args == null) {
            args = Collections.emptyList();
        }
        logger.accept("-----------------------------------------");
        logger.accept("SQL ----------> [" + sql + "]");
        logger.accept("--------------> PARAMETER <--------------");
        int size = args.size();
        if (size == 0) {
            logger.accept("                                         ");
            logger.accept("              NO PARAMETERS              ");
        } else {
            int max =
                    args.stream()
                            .map(o -> o.getClass().getSimpleName().length())
                            .max(Integer::compareTo)
                            .get();
            for (int i = 0; i < size; i++) {
                Object arg = args.get(i);
                String index = ToolNumber.showNumber(i + 1, 2, '0');
                String argType = ToolString.fixed(arg.getClass().getSimpleName(), max, ' ', true);
                logger.accept(index + "  ----------> [" + argType + "] = " + arg);
            }
        }
        logger.accept("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }
}

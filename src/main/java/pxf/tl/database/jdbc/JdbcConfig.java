package pxf.tl.database.jdbc;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.Objects;

/**
 * @author potatoxf
 */
public class JdbcConfig {

    private static final String RETURN_RESULT_SET_PREFIX = "#result-set-";

    private static final String RETURN_UPDATE_COUNT_PREFIX = "#update-count-";


    /**
     * If this variable is false, we will throw exceptions on SQL warnings.
     */
    private final boolean ignoreWarnings;

    /**
     * If this variable is set to a non-negative value, it will be used for setting the
     * fetchSize property on statements used for query processing.
     */
    private final int fetchSize;

    /**
     * If this variable is set to a non-negative value, it will be used for setting the
     * maxRows property on statements used for query processing.
     */
    private final int maxRows;

    /**
     * If this variable is set to a non-negative value, it will be used for setting the
     * queryTimeout property on statements used for query processing.
     */
    private final int queryTimeout;

    /**
     * If this variable is set to true, then all results checking will be bypassed for any
     * callable statement processing. This can be used to avoid a bug in some older Oracle
     * JDBC drivers like 10.1.0.2.
     */
    private final boolean skipResultsProcessing;

    /**
     * If this variable is set to true then all results from a stored procedure call
     * that don't have a corresponding SqlOutParameter declaration will be bypassed.
     * All other results processing will be take place unless the variable
     * {@code skipResultsProcessing} is set to {@code true}.
     */
    private final boolean skipUndeclaredResults;

    /**
     * If this variable is set to true then execution of a CallableStatement will return
     * the results in a Map that uses case insensitive names for the parameters.
     */
    private final boolean resultsMapCaseInsensitive;

    /**
     * Boolean flag controlled by a {@code spring.xml.ignore} system property that instructs Spring to
     * ignore XML, i.e. to not initialize the XML-related infrastructure.
     * <p>The default is "false".
     */
    private final boolean shouldIgnoreXml;

    private final boolean lazyInit;

    @Nonnull
    private final DataSource dataSource;

    public static JdbcConfig.Factory create() {
        return new Factory();
    }

    private JdbcConfig(boolean ignoreWarnings, int fetchSize, int maxRows, int queryTimeout,
                       boolean skipResultsProcessing, boolean skipUndeclaredResults,
                       boolean resultsMapCaseInsensitive, boolean shouldIgnoreXml,
                       boolean lazyInit, @Nonnull DataSource dataSource) {
        this.ignoreWarnings = ignoreWarnings;
        this.fetchSize = fetchSize;
        this.maxRows = maxRows;
        this.queryTimeout = queryTimeout;
        this.skipResultsProcessing = skipResultsProcessing;
        this.skipUndeclaredResults = skipUndeclaredResults;
        this.resultsMapCaseInsensitive = resultsMapCaseInsensitive;
        this.shouldIgnoreXml = shouldIgnoreXml;
        this.lazyInit = lazyInit;
        this.dataSource = dataSource;
    }

    public boolean isIgnoreWarnings() {
        return ignoreWarnings;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public boolean isSkipResultsProcessing() {
        return skipResultsProcessing;
    }

    public boolean isSkipUndeclaredResults() {
        return skipUndeclaredResults;
    }

    public boolean isResultsMapCaseInsensitive() {
        return resultsMapCaseInsensitive;
    }

    public boolean isShouldIgnoreXml() {
        return shouldIgnoreXml;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public static class Factory {

        /**
         * If this variable is false, we will throw exceptions on SQL warnings.
         */
        private boolean ignoreWarnings = true;

        /**
         * If this variable is set to a non-negative value, it will be used for setting the
         * fetchSize property on statements used for query processing.
         */
        private int fetchSize = -1;

        /**
         * If this variable is set to a non-negative value, it will be used for setting the
         * maxRows property on statements used for query processing.
         */
        private int maxRows = -1;

        /**
         * If this variable is set to a non-negative value, it will be used for setting the
         * queryTimeout property on statements used for query processing.
         */
        private int queryTimeout = -1;

        /**
         * If this variable is set to true, then all results checking will be bypassed for any
         * callable statement processing. This can be used to avoid a bug in some older Oracle
         * JDBC drivers like 10.1.0.2.
         */
        private boolean skipResultsProcessing = false;

        /**
         * If this variable is set to true then all results from a stored procedure call
         * that don't have a corresponding SqlOutParameter declaration will be bypassed.
         * All other results processing will be take place unless the variable
         * {@code skipResultsProcessing} is set to {@code true}.
         */
        private boolean skipUndeclaredResults = false;

        /**
         * If this variable is set to true then execution of a CallableStatement will return
         * the results in a Map that uses case insensitive names for the parameters.
         */
        private boolean resultsMapCaseInsensitive = false;

        /**
         * Boolean flag controlled by a {@code spring.xml.ignore} system property that instructs Spring to
         * ignore XML, i.e. to not initialize the XML-related infrastructure.
         * <p>The default is "false".
         */
        private boolean shouldIgnoreXml = false;

        private DataSource dataSource;


        private boolean lazyInit = true;

        private Factory() {
        }

        public void setIgnoreWarnings(boolean ignoreWarnings) {
            this.ignoreWarnings = ignoreWarnings;
        }

        public void setFetchSize(int fetchSize) {
            this.fetchSize = fetchSize;
        }

        public void setMaxRows(int maxRows) {
            this.maxRows = maxRows;
        }

        public void setQueryTimeout(int queryTimeout) {
            this.queryTimeout = queryTimeout;
        }

        public void setSkipResultsProcessing(boolean skipResultsProcessing) {
            this.skipResultsProcessing = skipResultsProcessing;
        }

        public void setSkipUndeclaredResults(boolean skipUndeclaredResults) {
            this.skipUndeclaredResults = skipUndeclaredResults;
        }

        public void setResultsMapCaseInsensitive(boolean resultsMapCaseInsensitive) {
            this.resultsMapCaseInsensitive = resultsMapCaseInsensitive;
        }

        public void setShouldIgnoreXml(boolean shouldIgnoreXml) {
            this.shouldIgnoreXml = shouldIgnoreXml;
        }

        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public void setLazyInit(boolean lazyInit) {
            this.lazyInit = lazyInit;
        }

        public JdbcConfig build() {
            Objects.requireNonNull(dataSource, "The datasource must be no null");
            return new JdbcConfig(ignoreWarnings, fetchSize, maxRows, queryTimeout, skipResultsProcessing,
                    skipUndeclaredResults, resultsMapCaseInsensitive, shouldIgnoreXml, lazyInit, dataSource);
        }
    }

}

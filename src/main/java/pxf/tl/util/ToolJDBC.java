package pxf.tl.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author potatoxf
 */
public final class ToolJDBC {


    /**
     * 获取JDBC的URL
     *
     * @param dataSource 数据源
     * @param close      是否关闭
     * @return 返回JDBC的URL
     */
    public static String getURL(DataSource dataSource, boolean close) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return conn.getMetaData().getURL();
        } catch (SQLException e) {
            throw new RuntimeException("Error to url", e);
        } finally {
            ToolIO.close(conn);
        }
    }

    public static String getDialect(String jdbcUrl, Collection<String> dialects) {
        String url = jdbcUrl.toLowerCase();
        for (String dialect : dialects) {
            if (url.contains(":" + dialect.toLowerCase() + ":")) {
                return dialect;
            }
        }
        return null;
    }
}

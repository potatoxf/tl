package pxf.tl.database.sql;


import pxf.tl.database.type.DatabaseType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Sql创建器
 *
 * @author potatoxf
 */
public interface SqlCreator {

    /**
     * 创建SQL
     *
     * @param databaseType  数据库类型
     * @param stringBuilder 字符串构建器
     * @return 返回字符串参数列表
     */
    @Nonnull
    List<Object> createSql(
            @Nullable final DatabaseType databaseType, @Nonnull final StringBuilder stringBuilder);

    /**
     * 创建SQL
     *
     * @param stringBuilder 字符串构建器
     * @return 返回字符串参数列表
     */
    @Nonnull
    default List<Object> createSql(@Nonnull final StringBuilder stringBuilder) {
        return createSql(null, stringBuilder);
    }
}

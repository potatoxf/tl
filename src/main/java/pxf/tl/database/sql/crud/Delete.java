package pxf.tl.database.sql.crud;


import pxf.tl.database.sql.CommonKeyWord;
import pxf.tl.database.sql.SqlCreator;
import pxf.tl.database.type.DatabaseType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Delete 语句
 *
 * @author potatoxf
 */
public final class Delete extends Where<Delete> implements SqlCreator {

    private Delete(String tableName) {
        super(tableName);
    }

    public static Delete from(String tableName) {
        return new Delete(tableName);
    }

    /**
     * 创建SQL
     *
     * @param databaseType  数据库类型
     * @param stringBuilder 字符串构建器
     * @return 返回字符串参数列表
     */
    @Nonnull
    @Override
    public List<Object> createSql(
            @Nullable final DatabaseType databaseType, @Nonnull final StringBuilder stringBuilder) {
        stringBuilder
                .append(CommonKeyWord.DELETE_PREFIX.getName(databaseType))
                .append(getKeywordDelimiter())
                .append(CommonKeyWord.FROM.getName(databaseType))
                .append(getKeywordDelimiter())
                .append(getTableName());
        return super.createSql(databaseType, stringBuilder);
    }
}

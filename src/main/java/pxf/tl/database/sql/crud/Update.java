package pxf.tl.database.sql.crud;


import pxf.tl.api.Pair;
import pxf.tl.database.sql.CommonKeyWord;
import pxf.tl.database.sql.SqlCreator;
import pxf.tl.database.type.DatabaseType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author potatoxf
 */
public final class Update extends Where<Update> implements SqlCreator {
    private final List<Pair<String, Object>> values = new LinkedList<>();

    private Update(String tableName) {
        super(tableName);
    }

    public static Update from(String tableName) {
        return new Update(tableName);
    }

    /**
     * 设置值
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public Update set(@Nonnull final String columnName, @Nullable final Object value) {
        values.add(new Pair<>(columnName, value));
        return this;
    }

    /**
     * 如果不为null则设置值
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public Update setIfNoNull(@Nonnull final String columnName, @Nullable final Object value) {
        if (value != null) {
            values.add(new Pair<>(columnName, value));
        }
        return this;
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
        List<Object> parameters = new ArrayList<>();
        stringBuilder
                .append(CommonKeyWord.UPDATE_PREFIX.getName(databaseType))
                .append(getKeywordDelimiter())
                .append(getTableName())
                .append(getKeywordDelimiter())
                .append(CommonKeyWord.UPDATE_SET.getName(databaseType))
                .append(getKeywordDelimiter());
        Iterator<Pair<String, Object>> iterator = values.iterator();
        Pair<String, Object> next = iterator.next();
        Object value = next.getValue();
        if (value == null) {
            stringBuilder
                    .append(next.getKey())
                    .append(CommonKeyWord.UPDATE_ASSIGN.getName(databaseType))
                    .append(CommonKeyWord.NULL.getName(databaseType));
        } else {
            stringBuilder
                    .append(next.getKey())
                    .append(CommonKeyWord.UPDATE_ASSIGN.getName(databaseType))
                    .append(getParameterPlaceholder());
            parameters.add(value);
        }
        while (iterator.hasNext()) {
            next = iterator.next();
            value = next.getValue();
            stringBuilder.append(getFieldDelimiter()).append(" ");
            if (value == null) {
                stringBuilder
                        .append(next.getKey())
                        .append(CommonKeyWord.UPDATE_ASSIGN.getName(databaseType))
                        .append(CommonKeyWord.NULL.getName(databaseType));
            } else {
                stringBuilder
                        .append(next.getKey())
                        .append(CommonKeyWord.UPDATE_ASSIGN.getName(databaseType))
                        .append(getParameterPlaceholder());
                parameters.add(value);
            }
        }
        parameters.addAll(super.createSql(databaseType, stringBuilder));
        return parameters;
    }
}

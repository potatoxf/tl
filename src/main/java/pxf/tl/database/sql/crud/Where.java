package pxf.tl.database.sql.crud;


import pxf.tl.api.This;
import pxf.tl.api.Triple;
import pxf.tl.database.sql.CommonKeyWord;
import pxf.tl.database.sql.CompareKeyWord;
import pxf.tl.database.sql.SqlCreator;
import pxf.tl.database.type.DatabaseType;
import pxf.tl.help.Whether;
import pxf.tl.iter.AnyIter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author potatoxf
 */
public abstract class Where<T extends Where<T>> extends SqlSetting<T>
        implements This<T>, SqlCreator {
    private final List<Object> wheres = new LinkedList<>();
    private boolean isOr = false;
    private List<Triple<String, CompareKeyWord, Object>> conditions = new LinkedList<>();

    protected Where(String tableName) {
        super(tableName);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T eq(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.EQ, value, null);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T eqIfNoEmpty(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.EQ, value, Whether::noEmpty);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T eqIfCondition(
            @Nonnull final String columnName,
            @Nullable final Object value,
            @Nullable Predicate<Object> condition) {
        return buildConditionIfMatch(columnName, CompareKeyWord.EQ, value, condition);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T noEq(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.NO_EQ, value, null);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T noEqIfNoEmpty(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.NO_EQ, value, Whether::noEmpty);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T noEqIfCondition(
            @Nonnull final String columnName,
            @Nullable final Object value,
            @Nullable Predicate<Object> condition) {
        return buildConditionIfMatch(columnName, CompareKeyWord.NO_EQ, value, condition);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T gt(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.GT, value, null);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T gtIfNoEmpty(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.GT, value, Whether::noEmpty);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T gtIfCondition(
            @Nonnull final String columnName,
            @Nullable final Object value,
            @Nullable Predicate<Object> condition) {
        return buildConditionIfMatch(columnName, CompareKeyWord.GT, value, condition);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T gtEq(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.GT_EQ, value, null);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T gtEqIfNoEmpty(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.GT_EQ, value, Whether::noEmpty);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T gtEqIfCondition(
            @Nonnull final String columnName,
            @Nullable final Object value,
            @Nullable Predicate<Object> condition) {
        return buildConditionIfMatch(columnName, CompareKeyWord.GT_EQ, value, condition);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T lt(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.LT, value, null);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T ltIfNoEmpty(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.LT, value, Whether::noEmpty);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T ltIfCondition(
            @Nonnull final String columnName,
            @Nullable final Object value,
            @Nullable Predicate<Object> condition) {
        return buildConditionIfMatch(columnName, CompareKeyWord.LT, value, condition);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T ltEq(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.LT_EQ, value, null);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T ltEqIfNoEmpty(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.LT_EQ, value, Whether::noEmpty);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T ltEqIfCondition(
            @Nonnull final String columnName,
            @Nullable final Object value,
            @Nullable Predicate<Object> condition) {
        return buildConditionIfMatch(columnName, CompareKeyWord.LT_EQ, value, condition);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T like(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.LIKE, value, null, v -> "%" + v + "%");
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T likeIfNoEmpty(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.LIKE, value, Whether::noEmpty, v -> "%" + v + "%");
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T likeIfCondition(
            @Nonnull final String columnName,
            @Nullable final Object value,
            @Nullable Predicate<Object> condition) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.LIKE, value, condition, v -> "%" + v + "%");
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T leftLike(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.LIKE, value, null, v -> "%" + v);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T leftLikeIfNoEmpty(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.LIKE, value, Whether::noEmpty, v -> "%" + v);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T leftLikeIfCondition(
            @Nonnull final String columnName,
            @Nullable final Object value,
            @Nullable Predicate<Object> condition) {
        return buildConditionIfMatch(columnName, CompareKeyWord.LIKE, value, condition, v -> "%" + v);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T rightLike(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.LIKE, value, null, v -> v + "%");
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T rightLikeIfNoEmpty(
            @Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.LIKE, value, Whether::noEmpty, v -> v + "%");
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T rightLikeIfCondition(
            @Nonnull final String columnName,
            @Nullable final Object value,
            @Nullable Predicate<Object> condition) {
        return buildConditionIfMatch(columnName, CompareKeyWord.LIKE, value, condition, v -> v + "%");
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T notLike(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.NOT_LIKE, value, null, v -> "%" + v + "%");
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T notLikeIfNoEmpty(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(
                columnName,
                CompareKeyWord.NOT_LIKE,
                value,
                Whether::noEmpty,
                v -> "%" + v + "%");
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T noLikeIfCondition(
            @Nonnull final String columnName,
            @Nullable final Object value,
            @Nullable Predicate<Object> condition) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.NOT_LIKE, value, condition, v -> "%" + v + "%");
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T notLeftLike(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.NOT_LIKE, value, null, v -> "%" + v);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T notLeftLikeIfNoEmpty(
            @Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.NOT_LIKE, value, Whether::noEmpty, v -> "%" + v);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T noLeftLikeIfCondition(
            @Nonnull final String columnName,
            @Nullable final Object value,
            @Nullable Predicate<Object> condition) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.NOT_LIKE, value, condition, v -> "%" + v);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T notRightLike(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.NOT_LIKE, value, null, v -> v + "%");
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T notRightLikeIfNoEmpty(
            @Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.NOT_LIKE, value, Whether::noEmpty, v -> v + "%");
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T noRightLikeIfCondition(
            @Nonnull final String columnName,
            @Nullable final Object value,
            @Nullable Predicate<Object> condition) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.NOT_LIKE, value, condition, v -> v + "%");
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T in(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.IN, value, null);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T inIfNoEmpty(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.IN, value, Whether::noEmpty);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T notIn(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(columnName, CompareKeyWord.NOT_IN, value, null);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param value      值
     * @return {@code this}
     */
    @Nonnull
    public final T notInIfNoEmpty(@Nonnull final String columnName, @Nullable final Object value) {
        return buildConditionIfMatch(
                columnName, CompareKeyWord.NOT_IN, value, Whether::noEmpty);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @return {@code this}
     */
    @Nonnull
    public final T isNull(@Nonnull final String columnName) {
        return buildConditionIfMatch(columnName, CompareKeyWord.IS_NULL, null, null);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @return {@code this}
     */
    @Nonnull
    public final T isNotNull(@Nonnull final String columnName) {
        return buildConditionIfMatch(columnName, CompareKeyWord.IS_NOT_NULL, null, null);
    }

    @Nonnull
    public final T toAnd() {
        if (Whether.noEmpty(conditions)) {
            if (isOr) {
                wheres.add(conditions);
                conditions = new LinkedList<>();
            }
        }
        isOr = false;
        return this$();
    }

    @Nonnull
    public final T toOr() {
        if (Whether.noEmpty(conditions)) {
            if (!isOr) {
                wheres.addAll(conditions);
                conditions.clear();
            }
        }
        isOr = true;
        return this$();
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
                .append(getKeywordDelimiter())
                .append(CommonKeyWord.WHERE.getName(databaseType))
                .append(getKeywordDelimiter());
        if (Whether.empty(wheres)) {
            appendConditionSegmentList(
                    databaseType,
                    stringBuilder,
                    null,
                    null,
                    CommonKeyWord.AND.getName(databaseType),
                    conditions,
                    parameters);
        } else {
            appendObject(
                    databaseType, stringBuilder, CommonKeyWord.AND.getName(databaseType), wheres, parameters);
            if (Whether.noEmpty(conditions)) {
                stringBuilder
                        .append(getKeywordDelimiter())
                        .append(CommonKeyWord.AND.getName(databaseType))
                        .append(getKeywordDelimiter());
                appendConditionSegmentList(
                        databaseType,
                        stringBuilder,
                        null,
                        null,
                        CommonKeyWord.AND.getName(databaseType),
                        conditions,
                        parameters);
            }
        }
        return parameters;
    }

    @SuppressWarnings("unchecked")
    private void appendObject(
            @Nullable final DatabaseType databaseType,
            @Nonnull final StringBuilder stringBuilder,
            @Nonnull final String join,
            @Nonnull final List<Object> objectList,
            @Nonnull final List<Object> parameters) {
        Object object = objectList.get(0);
        if (object instanceof List) {
            appendConditionSegmentList(
                    databaseType,
                    stringBuilder,
                    "(",
                    ")",
                    CommonKeyWord.OR.getName(databaseType),
                    (List<Triple<String, CompareKeyWord, Object>>) object,
                    parameters);
        } else {
            appendConditionSegment(
                    databaseType,
                    stringBuilder,
                    (Triple<String, CompareKeyWord, Object>) object,
                    parameters);
        }
        int size = objectList.size();
        for (int i = 1; i < size; i++) {
            object = objectList.get(i);
            stringBuilder.append(getKeywordDelimiter()).append(join).append(getKeywordDelimiter());
            if (object instanceof List) {
                appendConditionSegmentList(
                        databaseType,
                        stringBuilder,
                        "(",
                        ")",
                        CommonKeyWord.OR.getName(databaseType),
                        (List<Triple<String, CompareKeyWord, Object>>) object,
                        parameters);
            } else {
                appendConditionSegment(
                        databaseType,
                        stringBuilder,
                        (Triple<String, CompareKeyWord, Object>) object,
                        parameters);
            }
        }
    }

    private void appendConditionSegmentList(
            @Nullable final DatabaseType databaseType,
            @Nonnull final StringBuilder stringBuilder,
            @Nullable final String prefix,
            @Nullable final String suffix,
            @Nonnull final String join,
            @Nonnull final List<Triple<String, CompareKeyWord, Object>> immutableTripleList,
            @Nonnull final List<Object> parameters) {
        int size = immutableTripleList.size();
        if (size == 1) {
            appendConditionSegment(databaseType, stringBuilder, immutableTripleList.get(0), parameters);
        } else {
            if (prefix != null) {
                stringBuilder.append(prefix);
            }
            appendConditionSegment(databaseType, stringBuilder, immutableTripleList.get(0), parameters);
            for (int i = 1; i < size; i++) {
                stringBuilder.append(getKeywordDelimiter()).append(join).append(getKeywordDelimiter());
                appendConditionSegment(databaseType, stringBuilder, immutableTripleList.get(i), parameters);
            }
            if (suffix != null) {
                stringBuilder.append(suffix);
            }
        }
    }

    private void appendConditionSegment(
            @Nullable final DatabaseType databaseType,
            @Nonnull final StringBuilder stringBuilder,
            @Nonnull final Triple<String, CompareKeyWord, Object> immutableTriple,
            @Nonnull final List<Object> parameters) {
        Object value = immutableTriple.getValue();
        CompareKeyWord compareKeyWord = immutableTriple.getKey();
        String compareKeyWordName = compareKeyWord.getName(databaseType);
        if (value == null) {
            if (compareKeyWord.isNoValue()) {
                stringBuilder
                        .append(immutableTriple.getCatalog())
                        .append(getKeywordDelimiter())
                        .append(compareKeyWordName);
            } else {
                throw new IllegalArgumentException(
                        "The value of ["
                                + immutableTriple.getCatalog()
                                + " "
                                + compareKeyWordName
                                + " ?] is null");
            }
        } else {
            stringBuilder
                    .append(immutableTriple.getCatalog())
                    .append(getKeywordDelimiter())
                    .append(compareKeyWordName)
                    .append(getKeywordDelimiter());
            if (compareKeyWord == CompareKeyWord.IN || compareKeyWord == CompareKeyWord.NOT_IN) {
                stringBuilder.append("(");
                Iterator<Object> iterator = AnyIter.ofObject(value, null);
                Object object = iterator.next();
                stringBuilder.append(getParameterPlaceholder());
                parameters.add(object);
                while (iterator.hasNext()) {
                    object = iterator.next();
                    stringBuilder.append(getFieldDelimiter()).append(" ").append(getParameterPlaceholder());
                    parameters.add(object);
                }
                stringBuilder.append(")");
            } else {
                stringBuilder.append(getParameterPlaceholder());
                parameters.add(value);
            }
        }
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param keyWord    关键字
     * @param value      值
     * @param predicate
     * @return {@code this}
     */
    @Nonnull
    private T buildConditionIfMatch(
            @Nonnull final String columnName,
            @Nonnull final CompareKeyWord keyWord,
            @Nullable final Object value,
            @Nullable final Predicate<Object> predicate) {
        return buildConditionIfMatch(columnName, keyWord, value, predicate, null);
    }

    /**
     * 设置条件
     *
     * @param columnName 列名
     * @param keyWord    关键字
     * @param value      值
     * @param predicate
     * @return {@code this}
     */
    @Nonnull
    private T buildConditionIfMatch(
            @Nonnull final String columnName,
            @Nonnull final CompareKeyWord keyWord,
            @Nullable final Object value,
            @Nullable final Predicate<Object> predicate,
            @Nullable final Function<Object, Object> valueHandler) {
        Object v = value;
        if (predicate == null) {
            if (value == null) {
                if (keyWord.isNoValue()) {
                    conditions.add(new Triple<>(columnName, keyWord, null));
                } else {
                    conditions.add(new Triple<>(columnName, CompareKeyWord.IS_NULL, null));
                }
            } else {
                if (valueHandler != null) {
                    v = valueHandler.apply(value);
                }
                conditions.add(new Triple<>(columnName, keyWord, v));
            }
        } else {
            if (predicate.test(value)) {
                if (valueHandler != null) {
                    v = valueHandler.apply(value);
                }
                conditions.add(new Triple<>(columnName, keyWord, v));
            }
        }
        return this$();
    }
}

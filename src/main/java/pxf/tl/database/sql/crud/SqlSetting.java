package pxf.tl.database.sql.crud;


import pxf.tl.api.PoolOfPattern;
import pxf.tl.api.This;
import pxf.tl.util.ToolRegex;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author potatoxf
 */
public abstract class SqlSetting<T extends SqlSetting<T>> implements This<T> {
    private final String tableName;

    private String parameterPlaceholder = "?";

    private String keywordDelimiter = " ";

    private String fieldDelimiter = ",";

    protected SqlSetting(@Nonnull final String tableName) {
        this.tableName = ToolRegex.requireMatchPattern(PoolOfPattern.TOKEN, tableName, false);
    }

    @Nonnull
    public String getTableName() {
        return tableName;
    }

    @Nonnull
    public String getParameterPlaceholder() {
        return parameterPlaceholder;
    }

    @Nonnull
    public T setParameterPlaceholder(@Nonnull final String parameterPlaceholder) {
        this.parameterPlaceholder = Objects.requireNonNull(parameterPlaceholder);
        return this$();
    }

    @Nonnull
    public String getKeywordDelimiter() {
        return keywordDelimiter;
    }

    @Nonnull
    public T setKeywordDelimiter(@Nonnull final String keywordDelimiter) {
        this.keywordDelimiter = Objects.requireNonNull(keywordDelimiter);
        return this$();
    }

    @Nonnull
    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    @Nonnull
    public T setFieldDelimiter(@Nonnull final String fieldDelimiter) {
        this.fieldDelimiter = Objects.requireNonNull(fieldDelimiter);
        return this$();
    }
}

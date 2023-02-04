package pxf.tl.database.sql;


import pxf.tl.database.type.DatabaseType;

import java.util.Map;

/**
 * @author potatoxf
 */
public final class CompareKeyWord extends KeyWord {

    public static final CompareKeyWord EQ = of("=");
    public static final CompareKeyWord NO_EQ = of("!=", Map.of(DatabaseType.ORACLE, "<>"));
    public static final CompareKeyWord GT = of(">");
    public static final CompareKeyWord GT_EQ = of(">=");
    public static final CompareKeyWord LT = of("<");
    public static final CompareKeyWord LT_EQ = of("<=");
    public static final CompareKeyWord LIKE = of("LIKE");
    public static final CompareKeyWord NOT_LIKE = of("NOT LIKE");
    public static final CompareKeyWord IN = of("IN");
    public static final CompareKeyWord NOT_IN = of("NOT IN");
    public static final CompareKeyWord IS_NULL = of("IS NULL", true);
    public static final CompareKeyWord IS_NOT_NULL = of("IS NOT NULL", true);

    private final boolean noValue;

    private CompareKeyWord(
            String name, boolean noValue, Map<DatabaseType, String> databaseTypeStringMap) {
        super(name, databaseTypeStringMap);
        this.noValue = noValue;
    }

    private static CompareKeyWord of(String name) {
        return new CompareKeyWord(name, false, null);
    }

    private static CompareKeyWord of(String name, boolean isNoValue) {
        return new CompareKeyWord(name, isNoValue, null);
    }

    private static CompareKeyWord of(String name, Map<DatabaseType, String> databaseTypeStringMap) {
        return new CompareKeyWord(name, false, databaseTypeStringMap);
    }

    private static CompareKeyWord of(
            String name, boolean isNoValue, Map<DatabaseType, String> databaseTypeStringMap) {
        return new CompareKeyWord(name, isNoValue, databaseTypeStringMap);
    }

    public boolean isNoValue() {
        return noValue;
    }
}

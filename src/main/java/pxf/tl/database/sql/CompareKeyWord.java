package pxf.tl.database.sql;


import pxf.tl.database.type.DatabaseType;
import pxf.tl.help.New;

import java.util.Map;

/**
 * @author potatoxf
 */
public final class CompareKeyWord extends KeyWord {

    public static final CompareKeyWord EQ = new CompareKeyWord("=");
    public static final CompareKeyWord NO_EQ =
            new CompareKeyWord("!=", New.unmodifiableMap(DatabaseType.ORACLE, "<>"));
    public static final CompareKeyWord GT = new CompareKeyWord(">");
    public static final CompareKeyWord GT_EQ = new CompareKeyWord(">=");
    public static final CompareKeyWord LT = new CompareKeyWord("<");
    public static final CompareKeyWord LT_EQ = new CompareKeyWord("<=");
    public static final CompareKeyWord LIKE = new CompareKeyWord("LIKE");
    public static final CompareKeyWord NOT_LIKE = new CompareKeyWord("NOT LIKE");
    public static final CompareKeyWord IN = new CompareKeyWord("IN");
    public static final CompareKeyWord NOT_IN = new CompareKeyWord("NOT IN");
    public static final CompareKeyWord IS_NULL = new CompareKeyWord("IS NULL", true);
    public static final CompareKeyWord IS_NOT_NULL = new CompareKeyWord("IS NOT NULL", true);

    private final boolean isNoValue;

    private CompareKeyWord(String name) {
        this(name, false, null);
    }

    private CompareKeyWord(String name, boolean isNoValue) {
        this(name, isNoValue, null);
    }

    private CompareKeyWord(String name, Map<DatabaseType, String> databaseTypeStringMap) {
        this(name, false, databaseTypeStringMap);
    }

    private CompareKeyWord(
            String name, boolean isNoValue, Map<DatabaseType, String> databaseTypeStringMap) {
        super(name, databaseTypeStringMap);
        this.isNoValue = isNoValue;
    }

    public boolean isNoValue() {
        return isNoValue;
    }
}

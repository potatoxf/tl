package pxf.tl.database.sql;


import pxf.tl.database.type.DatabaseType;

import java.util.Map;

/**
 * @author potatoxf
 */
public final class CommonKeyWord extends KeyWord {
    public static final CommonKeyWord NULL = new CommonKeyWord("NULL");
    public static final CommonKeyWord AND = new CommonKeyWord("AND");
    public static final CommonKeyWord OR = new CommonKeyWord("OR");
    public static final CommonKeyWord WHERE = new CommonKeyWord("WHERE");
    public static final CommonKeyWord FROM = new CommonKeyWord("FROM");
    public static final CommonKeyWord UPDATE_PREFIX = new CommonKeyWord("UPDATE");
    public static final CommonKeyWord UPDATE_SET = new CommonKeyWord("SET");
    public static final CommonKeyWord UPDATE_ASSIGN = new CommonKeyWord("=");
    public static final CommonKeyWord DELETE_PREFIX = new CommonKeyWord("DELETE");

    private CommonKeyWord(String name) {
        this(name, null);
    }

    private CommonKeyWord(String name, Map<DatabaseType, String> databaseTypeStringMap) {
        super(name, databaseTypeStringMap);
    }
}

package pxf.tl.database.sql;


import pxf.tl.database.type.DatabaseType;
import pxf.tl.util.ToolCollection;

import java.sql.Connection;
import java.util.Map;
import java.util.Objects;

/**
 * @author potatoxf
 */
public abstract class KeyWord {

    private final String name;
    private final Map<DatabaseType, String> databaseTypeStringMap;

    protected KeyWord(String name, Map<DatabaseType, String> databaseTypeStringMap) {
        this.name =
                Objects.requireNonNull(name, "The keyword name must not be null").trim().toUpperCase();
        this.databaseTypeStringMap = ToolCollection.unmodifiableMap(databaseTypeStringMap, true);
    }

    public final String getName() {
        return getName((DatabaseType) null);
    }

    public String getName(Connection connection) {
        return getName(DatabaseType.parse(connection));
    }

    public final String getName(DatabaseType databaseType) {
        if (databaseTypeStringMap != null && databaseTypeStringMap.containsKey(databaseType)) {
            return databaseTypeStringMap.get(databaseType);
        }
        return name;
    }

    @Override
    public final String toString() {
        return name;
    }
}

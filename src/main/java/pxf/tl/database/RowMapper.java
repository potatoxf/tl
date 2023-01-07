package pxf.tl.database;


import pxf.tl.lang.BeanReflector;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author potatoxf
 */
public abstract class RowMapper<T> {

    Function<String, String> columnToFieldName;

    public void rowMap(T entity, ResultSet resultSet, int row) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException();
        }
        BeanReflector beanReflector = BeanReflector.of(entity.getClass());

        ResultSetMetaData metaData = resultSet.getMetaData();

        int columnCount = metaData.getColumnCount();

        List<String> fieldNameList = new ArrayList<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            String fieldName = columnToFieldName.apply(columnName);
            if (beanReflector.isExistField(fieldName)) {
                fieldNameList.add(fieldName);
            } else {
                fieldNameList.add(null);
            }
        }

        while (resultSet.next()) {
            for (int i = 0; i < columnCount; i++) {
                String fieldName = fieldNameList.get(0);
                if (fieldName == null) {
                    continue;
                }
                Class<?> fieldType = beanReflector.getFieldType(fieldName);
                Object value = null;//todo JdbcHelper.getResultSetValue(resultSet, i + 1, fieldType);
                beanReflector.setValue(entity, fieldName, value);
            }
        }
    }
}

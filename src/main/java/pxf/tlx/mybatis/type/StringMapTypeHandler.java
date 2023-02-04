package pxf.tlx.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import pxf.tl.util.ToolString;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Map<String, String>数据与字符串类型转换处理器
 *
 * @author potatoxf
 */
@MappedJdbcTypes(value = {JdbcType.CHAR, JdbcType.VARCHAR, JdbcType.LONGNVARCHAR})
public class StringMapTypeHandler extends AbstractTypeHandlerForString<Map<String, List<String>>> {

    @Override
    protected boolean isEmptyTarget(Map<String, List<String>> target) {
        return target == null || target.isEmpty();
    }

    @Override
    protected Map<String, List<String>> toTarget(String string) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        if (string != null) {
            String[] entries = ToolString.resolveArrayExpression(string, ';');
            for (String entry : entries) {
                String[] pair = ToolString.resolveArrayExpression(entry, '=');
                if (pair != null && pair.length != 0) {
                    if (pair.length == 1) {
                        Arrays.stream(ToolString.resolveArrayExpression(pair[0], ','))
                                .map(String::trim).filter(s -> !s.isEmpty())
                                .forEach(key -> map.put(key, new ArrayList<>(1)));
                    } else {
                        List<String> value = new ArrayList<>();
                        for (int i = 1; i < pair.length; i++) {
                            value.addAll(Arrays.stream(ToolString.resolveArrayExpression(pair[1], ','))
                                    .map(String::trim).filter(s -> !s.isEmpty())
                                    .collect(Collectors.toList()));
                        }
                        Arrays.stream(ToolString.resolveArrayExpression(pair[0], ','))
                                .map(String::trim).filter(s -> !s.isEmpty())
                                .forEach(key -> map.put(key, new ArrayList<>(value)));
                    }
                }
            }
        }
        return map;
    }

    @Override
    protected String fromTarget(Map<String, List<String>> target) {
        Map<String, String> map = new LinkedHashMap<>();
        target.forEach((k, v) -> map.put(k, v.stream().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.joining(","))));

        Map<String, String> reserveMap = new LinkedHashMap<>();
        map.forEach((k, v) -> {
            if (reserveMap.containsKey(v)) {
                reserveMap.put(v, reserveMap.get(v) + "," + k);
            } else {
                reserveMap.put(v, k);
            }
        });
        return reserveMap.entrySet().stream().map(e -> e.getValue() + "=" + e.getKey()).collect(Collectors.joining(";"));
    }
}

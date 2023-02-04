package pxf.tlx.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import pxf.tl.util.ToolString;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Set<String>数据与字符串类型转换处理器
 *
 * @author potatoxf
 */
@MappedJdbcTypes(value = {JdbcType.CHAR, JdbcType.VARCHAR, JdbcType.LONGNVARCHAR})
public class StringSetTypeHandler extends AbstractTypeHandlerForString<Set<String>> {

    @Override
    protected boolean isEmptyTarget(Set<String> target) {
        return target == null || target.isEmpty();
    }

    @Override
    protected Set<String> toTarget(String string) {
        Set<String> set = new LinkedHashSet<>();
        for (String element : ToolString.resolveArrayExpression(string, ',')) {
            if ((element = element.trim()).length() == 0) {
                continue;
            }
            set.add(element);
        }
        return set;
    }

    @Override
    protected String fromTarget(Set<String> target) {
        return target.stream().map(String::trim)
                .filter(s -> !s.isBlank()).collect(Collectors.joining(","));
    }
}

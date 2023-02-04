package pxf.tlx.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import pxf.tl.util.ToolString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * List<String>数据与字符串类型转换处理器
 *
 * @author potatoxf
 */
@MappedJdbcTypes(value = {JdbcType.CHAR, JdbcType.VARCHAR, JdbcType.LONGNVARCHAR})
public class StringListTypeHandler extends AbstractTypeHandlerForString<List<String>> {

    @Override
    protected boolean isEmptyTarget(List<String> target) {
        return target == null || target.isEmpty();
    }

    @Override
    protected List<String> toTarget(String string) {
        List<String> List = new ArrayList<>();
        for (String element : ToolString.resolveArrayExpression(string, ',')) {
            if ((element = element.trim()).length() == 0) {
                continue;
            }
            List.add(element);
        }
        return List;
    }

    @Override
    protected String fromTarget(List<String> target) {
        return target.stream().map(String::trim)
                .filter(s -> !s.isBlank()).collect(Collectors.joining(","));
    }
}

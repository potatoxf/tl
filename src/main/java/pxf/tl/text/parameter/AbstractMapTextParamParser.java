package pxf.tl.text.parameter;

import java.util.Map;

/**
 * 文本Map解析器
 *
 * <p>将字符串通过一定形式转换成{@code Map}
 *
 * @author potatoxf
 */
public abstract class AbstractMapTextParamParser
        extends AbstractTextParamParser<Map<String, Object>> {

    /**
     * 解析类型
     *
     * @return 类型
     */
    protected abstract String type();
}

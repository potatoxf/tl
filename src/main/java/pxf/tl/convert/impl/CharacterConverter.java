package pxf.tl.convert.impl;


import pxf.tl.convert.AbstractConverter;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolBoolean;

/**
 * 字符转换器
 *
 * @author potatoxf
 */
public class CharacterConverter extends AbstractConverter<Character> {
    private static final long serialVersionUID = 1L;

    @Override
    protected Character convertInternal(Object value) {
        if (value instanceof Boolean) {
            return ToolBoolean.toCharacter((Boolean) value);
        } else {
            final String valueStr = convertToStr(value);
            if (Whether.noBlank(valueStr)) {
                return valueStr.charAt(0);
            }
        }
        return null;
    }
}

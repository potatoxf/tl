package pxf.tl.convert.impl;


import pxf.tl.api.Charsets;
import pxf.tl.convert.AbstractConverter;
import pxf.tl.convert.ConvertException;
import pxf.tl.util.ToolIO;
import pxf.tl.util.ToolXML;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.TimeZone;

/**
 * 字符串转换器，提供各种对象转换为字符串的逻辑封装
 *
 * @author potatoxf
 */
public class StringConverter extends AbstractConverter<String> {
    private static final long serialVersionUID = 1L;

    /**
     * Clob字段值转字符串
     *
     * @param clob {@link Clob}
     * @return 字符串
     */
    private static String clobToStr(Clob clob) {
        Reader reader = null;
        try {
            reader = clob.getCharacterStream();
            return ToolIO.read(reader);
        } catch (SQLException e) {
            throw new ConvertException(e);
        } finally {
            ToolIO.closes(reader);
        }
    }

    /**
     * Blob字段值转字符串
     *
     * @param blob {@link Blob}
     * @return 字符串
     */
    private static String blobToStr(Blob blob) {
        InputStream in = null;
        try {
            in = blob.getBinaryStream();
            return ToolIO.read(in, Charsets.UTF_8);
        } catch (SQLException e) {
            throw new ConvertException(e);
        } finally {
            ToolIO.closes(in);
        }
    }

    @Override
    protected String convertInternal(Object value) {
        if (value instanceof TimeZone) {
            return ((TimeZone) value).getID();
        } else if (value instanceof org.w3c.dom.Node) {
            return ToolXML.toStr((org.w3c.dom.Node) value);
        } else if (value instanceof Clob) {
            return clobToStr((Clob) value);
        } else if (value instanceof Blob) {
            return blobToStr((Blob) value);
        } else if (value instanceof Type) {
            return ((Type) value).getTypeName();
        }

        // 其它情况
        return convertToStr(value);
    }
}

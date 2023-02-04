package pxf.tlx.mybatis;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import pxf.tl.help.Safe;

import java.util.Map;

/**
 * @author potatoxf
 */
public class MapCamelCaseKeyWrapperFactory implements ObjectWrapperFactory {

    @Override
    public boolean hasWrapperFor(Object object) {
        return object instanceof Map;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        return new MapWrapper(metaObject, (Map<String, Object>) object) {
            @Override
            public String findProperty(String name, boolean useCamelCaseMapping) {
                return name == null ? "" : Safe.toCamelCase(name, false);
            }
        };
    }
}
package pxf.tl.util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author potatoxf
 */
public final class ToolMap {
    private ToolMap() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    @Nonnull
    public static List<Object> getValueList(@Nonnull Map<?, ?> map, @Nonnull Iterator<?> keys) {
        List<Object> list = new ArrayList<>();
        while (keys.hasNext()) {
            Object key = keys.next();
            Object object = map.get(key);
            if (object != null) {
                list.add(object);
            }
        }
        return list;
    }

    @Nonnull
    public static List<Object> getValueList(@Nonnull Map<?, ?> map, @Nonnull Object... keys) {
        List<Object> list = new ArrayList<>(keys.length);
        for (Object key : keys) {
            Object object = map.get(key);
            if(object!=null){
                list.add(object);
            }
        }
        return list;
    }

}

package pxf.tl.api;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.Map;

/**
 * @author potatoxf
 */
public interface PoolOfArray {
    boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
    char[] EMPTY_CHAR_ARRAY = new char[0];
    Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
    byte[] EMPTY_BYTE_ARRAY = new byte[0];
    Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
    short[] EMPTY_SHORT_ARRAY = new short[0];
    Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];
    int[] EMPTY_INT_ARRAY = new int[0];
    Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
    long[] EMPTY_LONG_ARRAY = new long[0];
    Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];
    float[] EMPTY_FLOAT_ARRAY = new float[0];
    Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
    double[] EMPTY_DOUBLE_ARRAY = new double[0];
    Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
    Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
    String[] EMPTY_STRING_ARRAY = new String[0];
    /**
     * 空数组
     */
    InstanceSupplier<Map<Class<?>, Object>> EMPTY_ARRAYS_MAP = InstanceSupplier.of(() -> Map.ofEntries(
            Map.entry(boolean.class, EMPTY_BOOLEAN_ARRAY),
            Map.entry(Boolean.class, EMPTY_BOOLEAN_OBJECT_ARRAY),
            Map.entry(char.class, EMPTY_CHAR_ARRAY),
            Map.entry(Character.class, EMPTY_CHARACTER_OBJECT_ARRAY),
            Map.entry(byte.class, EMPTY_BYTE_ARRAY),
            Map.entry(Byte.class, EMPTY_BYTE_OBJECT_ARRAY),
            Map.entry(short.class, EMPTY_SHORT_ARRAY),
            Map.entry(Short.class, EMPTY_SHORT_OBJECT_ARRAY),
            Map.entry(int.class, EMPTY_INT_ARRAY),
            Map.entry(Integer.class, EMPTY_INTEGER_OBJECT_ARRAY),
            Map.entry(long.class, EMPTY_LONG_ARRAY),
            Map.entry(Long.class, EMPTY_LONG_OBJECT_ARRAY),
            Map.entry(float.class, EMPTY_FLOAT_ARRAY),
            Map.entry(Float.class, EMPTY_DOUBLE_ARRAY),
            Map.entry(Double.class, EMPTY_DOUBLE_OBJECT_ARRAY),
            Map.entry(Class.class, EMPTY_CLASS_ARRAY),
            Map.entry(String.class, EMPTY_STRING_ARRAY)
    ));

    static Object empty(@Nonnull Class<?> type) {
        Map<Class<?>, Object> eam = EMPTY_ARRAYS_MAP.get();
        if (eam.containsKey(type)) {
            return eam.get(type);
        }
        return Array.newInstance(type, 0);
    }

}

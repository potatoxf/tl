package pxf.tl.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.annotation.StaticField;
import pxf.tl.function.SupplierThrow;
import pxf.tl.util.ToolBytecode;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 静态域提取器
 *
 * @author potatoxf
 */
public class StaticFieldExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticFieldExtractor.class);
    /**
     * 资源名的静态属性名
     */
    private final String staticFieldName;
    /**
     * 域类型，默认字符串类型
     */
    private final Class<?> fieldType;
    /**
     * 存储编码常量
     */
    private final Map<Class<?>, Object> cache = new ConcurrentHashMap<Class<?>, Object>();

    /**
     * @param staticFieldName
     */
    protected StaticFieldExtractor(String staticFieldName) {
        this(staticFieldName, String.class);
    }

    /**
     * @param staticFieldName
     * @param fieldType
     */
    protected StaticFieldExtractor(String staticFieldName, Class<?> fieldType) {
        if (staticFieldName == null) {
            throw new IllegalArgumentException();
        }
        if (fieldType == null) {
            throw new IllegalArgumentException();
        }
        this.staticFieldName = staticFieldName;
        this.fieldType = fieldType;
    }

    /**
     * @param staticFieldName
     * @return
     */
    public static StaticFieldExtractor of(String staticFieldName) {
        return new StaticFieldExtractor(staticFieldName);
    }

    /**
     * @param staticFieldName
     * @param fieldType
     * @return
     */
    public static StaticFieldExtractor of(String staticFieldName, Class<?> fieldType) {
        return new StaticFieldExtractor(staticFieldName, fieldType);
    }

    /**
     * 注册某个类
     *
     * @param clz 类
     */
    private void registerValue(final Class<?> clz) {
        if (clz == null) {
            throw new IllegalArgumentException("The class must be no null");
        }
        if (!cache.containsKey(clz)) {
            synchronized (clz) {
                if (!cache.containsKey(clz)) {
                    Object value =
                            ToolBytecode.getTrackFieldValue(clz, fieldType, staticFieldName, Modifier.STATIC, null);
                    if (value != null) {
                        cache.put(clz, value);
                    } else {
                        if (fieldType.equals(String.class)) {
                            StaticField staticField = clz.getAnnotation(StaticField.class);
                            if (staticField != null) {
                                String[] names = staticField.name();
                                String[] values = staticField.value();
                                if (names == null || names.length == 0) {
                                    if (values != null && values.length == 1) {
                                        cache.put(clz, value);
                                    }
                                } else {
                                    if (values != null && values.length > 0) {
                                        int len = Math.min(names.length, values.length);
                                        Map<String, String> map = new HashMap<String, String>(len, 1);
                                        for (int i = 0; i < len; i++) {
                                            map.put(names[i], values[i]);
                                        }
                                        cache.put(clz, map);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取值
     *
     * @param clz 类
     * @return 返回值，可能为null
     */
    public Object getValue(Class<?> clz) {
        if (!cache.containsKey(clz)) {
            registerValue(clz);
        }
        Object object = cache.get(clz);
        switch (object) {
            case SupplierThrow s:
                try {
                    return ((SupplierThrow<?, ?>) object).get();
                } catch (Throwable e) {
                    return null;
                }
            case Supplier s:
                return ((Supplier<?>) object).get();
            case Map m:
                return ((Map<?, ?>) object).get(staticFieldName);
            case null:
            default:
                return object;
        }
    }

    /**
     * 获取非null值
     *
     * @param clz 类
     * @return 返回值，如果为空抛出异常
     */
    public Object getNonNullValue(Class<?> clz) {
        Object object = cache.get(clz);
        Object result;
        switch (object) {
            case SupplierThrow s:
                try {
                    result = ((SupplierThrow<?, ?>) object).get();
                } catch (Throwable e) {
                    throw new RuntimeException("Error to get value", e);
                }
                break;
            case Supplier s:
                return ((Supplier<?>) object).get();
            case Map m:
                result = ((Map<?, ?>) object).get(staticFieldName);
                break;
            case null:
            default:
                result = object;
                break;
        }
        if (result == null) {
            throw new RuntimeException(
                    "The "
                            + clz
                            + " No declared static field for [public static final "
                            + staticFieldName
                            + "] or annotation [StaticField]");
        }
        return result;
    }
}

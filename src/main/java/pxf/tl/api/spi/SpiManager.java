package pxf.tl.api.spi;


import pxf.tl.collection.map.Parametric;
import pxf.tl.lang.ContextResourceFinder;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spi服务管理器
 *
 * <p>该服务管理在每一套系统只有一个实例类可以使用，适用于系统统一处理方案
 *
 * @author potatoxf
 */
@SuppressWarnings("unchecked")
public final class SpiManager {
    /**
     * 单例实例表
     */
    private static final Map<Class<?>, Object> SINGLETON_CONTAINER =
            new ConcurrentHashMap<Class<?>, Object>();
    /**
     * 服务表
     */
    private static final Map<Class<?>, Spi<?>> SERVICE = findService();

    /**
     * 获取服务实例
     *
     * @param type       类
     * @param parametric 参数
     * @param <T>        类型
     * @return 服务实例，不会为null
     */
    public static <T> T getNullableServiceInstance(Class<T> type, Map<String, ?> parametric) {
        try {
            return getServiceInstance(type, parametric);
        } catch (SpiException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取服务实例
     *
     * @param type 类
     * @param <T>  类型
     * @return 服务实例，不会为null
     */
    public static <T> T getSafeServiceInstance(Class<T> type) {
        return getSafeServiceInstance(type, null);
    }

    /**
     * 获取服务实例
     *
     * @param type 类
     * @param <T>  类型
     * @return 服务实例，不会为null
     */
    public static <T> T getServiceInstance(Class<T> type) throws SpiException {
        return getServiceInstance(type, null);
    }

    /**
     * 获取服务实例
     *
     * @param type       类
     * @param parametric 参数
     * @param <T>        类型
     * @return 服务实例，不会为null
     */
    public static <T> T getSafeServiceInstance(Class<T> type, Map<String, ?> parametric) {
        try {
            return getServiceInstance(type, parametric);
        } catch (SpiException e) {
            throw new RuntimeException("Service instance not obtained", e);
        }
    }

    /**
     * 获取服务实例
     *
     * @param type       类
     * @param parametric 参数
     * @param <T>        类型
     * @return 服务实例，不会为null
     */
    public static <T> T getServiceInstance(Class<T> type, Map<String, ?> parametric)
            throws SpiException {
        Spi<?> spi = SERVICE.get(type);
        if (spi == null) {
            throw new SpiException("No spi of type [" + type + "] was found");
        }
        Parametric param = parametric != null ? new Parametric(parametric) : new Parametric();
        boolean singleton = spi.isSingleton();
        Object result;
        if (singleton) {
            result = SINGLETON_CONTAINER.get(type);
            if (result == null) {
                synchronized (SINGLETON_CONTAINER) {
                    if (result == null) {
                        result = spi.load(param);
                        if (result != null) {
                            SINGLETON_CONTAINER.put(type, result);
                        }
                    }
                }
            }
        } else {
            result = spi.load(param);
        }
        if (result == null) {
            SERVICE.remove(type);
            throw new SpiException("Error load method of spi");
        }
        return (T) result;
    }

    /**
     * 找到所有服务
     *
     * @return {@code Map<Class<?>, Spi<?>>}
     */
    private static Map<Class<?>, Spi<?>> findService() {
        Map<Class<?>, Spi<?>> map = new HashMap<Class<?>, Spi<?>>();
        try {
            ContextResourceFinder service = ContextResourceFinder.of("META-INF/services/tl/");
            List<Properties> spiList = service.findAllProperties("spi.properties");
            Map<Class<?>, Object> valueMap = new HashMap<Class<?>, Object>();
            for (Properties prop : spiList) {
                Set<Map.Entry<Object, Object>> entries = prop.entrySet();
                for (Map.Entry<Object, Object> entry : entries) {
                    Object key = entry.getKey();
                    if (key == null) {
                        continue;
                    }
                    try {
                        Class<?> clz = Class.forName(key.toString());
                        if (Spi.class.isAssignableFrom(clz)) {
                            Constructor<?> constructor = clz.getConstructor();
                            Spi<?> spi = (Spi<?>) constructor.newInstance();
                            Class<?> type = spi.type();
                            if (type != null) {
                                if (map.containsKey(type)) {
                                    Object oldValue = valueMap.get(type);
                                    Object newValue = entry.getValue();
                                    try {
                                        int ov = Integer.parseInt(oldValue.toString());
                                        int nv = Integer.parseInt(newValue.toString());
                                        if (nv >= ov) {
                                            map.put(type, spi);
                                            valueMap.put(type, entry.getValue());
                                        }
                                    } catch (Exception ignored) {
                                        map.put(type, spi);
                                        valueMap.put(type, entry.getValue());
                                    }
                                } else {
                                    map.put(type, spi);
                                    valueMap.put(type, entry.getValue());
                                }
                            }
                        }
                    } catch (ClassNotFoundException
                            | NoSuchMethodException
                            | InvocationTargetException
                            | InstantiationException
                            | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableMap(new HashMap<Class<?>, Spi<?>>(map));
    }
}

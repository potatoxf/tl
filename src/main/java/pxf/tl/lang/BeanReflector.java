package pxf.tl.lang;

import pxf.tl.help.Whether;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Bean属性反射器
 *
 * @author potatoxf
 */
public final class BeanReflector {
    /**
     * Bean属性反射器缓存
     */
    private static final Map<Class<?>, BeanReflector> BEAN_REFLECTOR_CACHE =
            new ConcurrentHashMap<>();
    /**
     * Bean Class
     */
    private final Class<?> clazz;
    /**
     * Bean 属性数据
     */
    private final Map<String, BeanProperty> beanPropertyMap;
    /**
     * Bean 父级反射器
     */
    private final BeanReflector supper;

    private BeanReflector(
            Class<?> clazz, Map<String, BeanProperty> beanPropertyMap, BeanReflector supper) {
        this.clazz = clazz;
        this.beanPropertyMap = beanPropertyMap;
        this.supper = supper;
    }

    /**
     * 构建类Bean反射器
     *
     * @param clz 类
     * @return {@code BeanReflector}
     */
    public static BeanReflector of(Class<?> clz) {
        if (BEAN_REFLECTOR_CACHE.containsKey(clz)) {
            return BEAN_REFLECTOR_CACHE.get(clz);
        }
        BeanReflector.initClassBeanReflector(clz);
        BeanReflector beanReflector = BEAN_REFLECTOR_CACHE.get(clz);
        if (beanReflector != null) {
            return beanReflector;
        } else {
            throw new RuntimeException("The Error to build BeanReflector");
        }
    }

    /**
     * 初始化类Bean反射器
     *
     * @param clz 类
     */
    private static void initClassBeanReflector(Class<?> clz) {
        Deque<Class<?>> deque = new LinkedList<>();
        for (Class<?> c = clz; c != Object.class; c = c.getSuperclass()) {
            deque.push(c);
        }
        BeanReflector supper = null;
        synchronized (BEAN_REFLECTOR_CACHE) {
            if (!BEAN_REFLECTOR_CACHE.containsKey(clz)) {
                while (!Whether.empty(deque)) {
                    Class<?> clazz = deque.poll();
                    BeanReflector beanReflector = BEAN_REFLECTOR_CACHE.get(clazz);
                    if (beanReflector == null) {
                        beanReflector = buildBeanReflector(clazz, supper);
                        BEAN_REFLECTOR_CACHE.put(clazz, beanReflector);
                    }
                    supper = beanReflector;
                }
            }
        }
    }

    /**
     * 构建类Bean反射器
     *
     * @param clazz  类
     * @param supper 父级Bean反射器
     * @return
     */
    private static BeanReflector buildBeanReflector(Class<?> clazz, BeanReflector supper) {
        Map<String, List<Method>> map =
                Arrays.stream(clazz.getDeclaredMethods())
                        .filter(
                                method -> {
                                    int modifiers = method.getModifiers();
                                    if (!Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers)) {
                                        String methName = method.getName();
                                        return ((methName.startsWith("set") || methName.startsWith("get"))
                                                && methName.length() > 3)
                                                || (methName.startsWith("is") && methName.length() > 2);
                                    }
                                    return false;
                                })
                        .collect(
                                Collectors.groupingBy(
                                        method -> {
                                            String name = method.getName();
                                            if (name.startsWith("is")) {
                                                return Character.toLowerCase(name.charAt(2)) + name.substring(3);
                                            } else {
                                                return Character.toLowerCase(name.charAt(3)) + name.substring(4);
                                            }
                                        }));
        Map<String, BeanProperty> result = new HashMap<>(map.size(), 1);
        Set<Map.Entry<String, List<Method>>> entries = map.entrySet();
        for (Map.Entry<String, List<Method>> entry : entries) {
            List<Method> value = entry.getValue();
            if (Whether.empty(value)) {
                continue;
            }
            Method getMethod = null;
            Method setMethod = null;
            Method method = value.get(0);
            String name = method.getName();
            if (name.startsWith("set")) {
                setMethod = method;
                getMethod = findGetMethod(value);
            } else if (name.startsWith("get")) {
                getMethod = method;
                setMethod = findSetMethod(value);
            } else if (name.startsWith("is")) {
                getMethod = method;
                setMethod = findSetMethod(value);
            }
            Field field;
            try {
                field = clazz.getDeclaredField(entry.getKey());
            } catch (NoSuchFieldException e) {
                field = null;
            }
            if (field != null) {
                Class<?> type = field.getType();
                if (setMethod != null) {
                    Class<?>[] parameterTypes = setMethod.getParameterTypes();
                    if (parameterTypes.length != 1 || !type.isAssignableFrom(parameterTypes[0])) {
                        setMethod = null;
                    }
                }
                if (getMethod != null) {
                    Class<?>[] parameterTypes = getMethod.getParameterTypes();
                    Class<?> returnType = getMethod.getReturnType();
                    if (parameterTypes.length != 0 || !returnType.isAssignableFrom(type)) {
                        getMethod = null;
                    }
                }
            } else {
                if (getMethod != null && setMethod != null) {
                    Class<?>[] setMethodParameterTypes = setMethod.getParameterTypes();
                    Class<?>[] getMethodParameterTypes = getMethod.getParameterTypes();
                    Class<?> returnType = getMethod.getReturnType();
                    if (getMethodParameterTypes.length != 0
                            || setMethodParameterTypes.length != 1
                            || setMethodParameterTypes[0] != returnType) {
                        setMethod = null;
                        getMethod = null;
                    }
                }
            }
            if (field == null && getMethod == null && setMethod == null) {
                continue;
            }
            result.put(entry.getKey(), new BeanProperty(field, setMethod, getMethod));
        }
        return new BeanReflector(clazz, Collections.unmodifiableMap(result), supper);
    }

    /**
     * 从Set,Get,Is中找到Get方法，排除第一个
     *
     * @param value 方法列表
     * @return {@code Method}
     */
    private static Method findGetMethod(List<Method> value) {
        Method method;
        String name;
        if (value.size() == 2) {
            return value.get(1);
        } else {
            for (int i = 1; i < 3; i++) {
                method = value.get(i);
                name = method.getName();
                if (name.startsWith("get")) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 从Set,Get,Is中找到Set方法，排除第一个
     *
     * @param value 方法列表
     * @return {@code Method}
     */
    private static Method findSetMethod(List<Method> value) {
        Method method;
        String name;
        if (value.size() == 2) {
            method = value.get(1);
            name = method.getName();
            if (name.startsWith("set")) {
                return method;
            }
        } else {
            for (int i = 1; i < 3; i++) {
                method = value.get(i);
                name = method.getName();
                if (name.startsWith("set")) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 是否存在属性，域，set,get的任一个
     *
     * @param property 属性
     * @return 如果存在返回true，否则返回false
     */
    public boolean isExist(String property) {
        for (BeanReflector br = this; br != null; br = br.supper) {
            if (br.beanPropertyMap.containsKey(property)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否存在属性，只包含域
     *
     * @param property 属性
     * @return 如果存在返回true，否则返回false
     */
    public boolean isExistField(String property) {
        for (BeanReflector br = this; br != null; br = br.supper) {
            if (br.beanPropertyMap.containsKey(property)
                    && br.beanPropertyMap.get(property).field != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取属性类型
     *
     * @param property 属性
     * @return {@code Class<?>}
     */
    public Class<?> getFieldType(String property) {
        for (BeanReflector br = this; br != null; br = br.supper) {
            BeanProperty beanProperty = br.beanPropertyMap.get(property);
            if (beanProperty == null) {
                continue;
            }
            if (beanProperty.field != null) {
                return beanProperty.field.getType();
            }
        }
        return null;
    }

    /**
     * 获取属性类型
     *
     * @param property 属性
     * @return {@code Class<?>}
     */
    public Class<?> getFieldExistClass(String property) {
        for (BeanReflector br = this; br != null; br = br.supper) {
            BeanProperty beanProperty = br.beanPropertyMap.get(property);
            if (beanProperty == null) {
                continue;
            }
            if (beanProperty.field != null) {
                return br.clazz;
            }
        }
        return null;
    }

    /**
     * 获取属性类型
     *
     * @param property 属性
     * @return {@code Class<?>}
     */
    public Class<?> getFieldPossibleType(String property) {
        for (BeanReflector br = this; br != null; br = br.supper) {
            BeanProperty beanProperty = br.beanPropertyMap.get(property);
            if (beanProperty == null) {
                continue;
            }
            if (beanProperty.field != null) {
                return br.clazz;
            }
        }
        for (BeanReflector br = this; br != null; br = br.supper) {
            BeanProperty beanProperty = br.beanPropertyMap.get(property);
            if (beanProperty == null) {
                continue;
            }
            if (beanProperty.setMethod != null) {
                return br.clazz;
            }
            if (beanProperty.getMethod != null) {
                return br.clazz;
            }
        }
        return null;
    }

    /**
     * 获取属性类型
     *
     * @param property 属性
     * @return {@code Class<?>}
     */
    public Class<?> getFieldPossibleExistClass(String property) {
        for (BeanReflector br = this; br != null; br = br.supper) {
            BeanProperty beanProperty = br.beanPropertyMap.get(property);
            if (beanProperty == null) {
                continue;
            }
            if (beanProperty.field != null) {
                return beanProperty.field.getType();
            }
        }
        for (BeanReflector br = this; br != null; br = br.supper) {
            BeanProperty beanProperty = br.beanPropertyMap.get(property);
            if (beanProperty == null) {
                continue;
            }
            if (beanProperty.setMethod != null) {
                return beanProperty.setMethod.getParameterTypes()[0];
            }
            if (beanProperty.getMethod != null) {
                return beanProperty.getMethod.getReturnType();
            }
        }
        return null;
    }

    /**
     * 获取属性值
     *
     * @param object   目标对象
     * @param property 属性
     * @return {@code Object}
     */
    public Object getValue(Object object, String property) {
        checkObject(object);
        for (BeanReflector br = this; br != null; br = br.supper) {
            BeanProperty beanProperty = br.beanPropertyMap.get(property);
            if (beanProperty == null) {
                continue;
            }
            if (beanProperty.getMethod != null) {
                try {
                    return beanProperty.getMethod.invoke(object);
                } catch (Exception ignored) {
                }
            }
            if (beanProperty.field != null) {
                beanProperty.field.setAccessible(true);
                try {
                    return beanProperty.field.get(object);
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    /**
     * 设置属性值
     *
     * @param object   目标对象
     * @param property 属性
     * @param value    值
     * @return 如果成功返回true，否则返回false
     */
    public boolean setValue(Object object, String property, Object value) {
        checkObject(object);
        for (BeanReflector br = this; br != null; br = br.supper) {
            BeanProperty beanProperty = br.beanPropertyMap.get(property);
            if (beanProperty == null) {
                continue;
            }
            if (beanProperty.setMethod != null) {
                try {
                    beanProperty.setMethod.invoke(object, value);
                    return true;
                } catch (Exception ignored) {
                }
            }
            if (beanProperty.field != null) {
                beanProperty.field.setAccessible(true);
                try {
                    beanProperty.field.set(object, value);
                    return true;
                } catch (Exception ignored) {
                }
            }
        }
        return false;
    }

    /**
     * 获取所有属性名称
     *
     * @return {@code Set<String>}
     */
    public Set<String> property() {
        Set<String> result = new LinkedHashSet<>();
        for (BeanReflector br = this; br != null; br = br.supper) {
            result.addAll(br.beanPropertyMap.keySet());
        }
        return Collections.unmodifiableSet(result);
    }

    private void checkObject(Object object) {
        if (!clazz.equals(object.getClass())) {
            throw new IllegalArgumentException();
        }
    }

    private static class BeanProperty {
        private final Field field;
        private final Method setMethod;
        private final Method getMethod;

        private BeanProperty(Field field, Method setMethod, Method getMethod) {
            this.field = field;
            this.setMethod = setMethod;
            this.getMethod = getMethod;
        }
    }
}

package pxf.tlx.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import pxf.tl.function.PredicateThrow;
import pxf.tl.util.ToolBytecode;
import pxf.tl.util.ToolMap;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * @author potatoxf
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class CryptMapperInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] args = invocation.getArgs();
        final MappedStatement mappedStatement = (MappedStatement) args[0];
        //获取接口参数
        Object[] parameterValues = null;
        if (args.length > 1) {
            if (args[1] != null) {
                if (args[1] instanceof Map map) {
                    parameterValues = ToolMap.getValueList(map,
                            IntStream.rangeClosed(1, map.size() / 2).mapToObj(i -> "param" + i).toArray(Object[]::new)).toArray();
                } else {
                    parameterValues = new Object[]{args[1]};
                }
            }
        }
        //获取结果方法
        Method method = null;
        String namespace = mappedStatement.getId();
        Class<?> clazz = ToolBytecode.forNamespaceSilent(namespace);
        if (clazz != null) {
            Method[] methods = ToolBytecode.getMethods(clazz,
                    namespace.substring(namespace.lastIndexOf(".") + 1), false);

            if (methods.length == 1) {
                method = methods[0];
            } else if (methods.length > 1) {
                for (Method m : methods) {
                    if (ToolBytecode.isCompatibilityFrom(m.getParameterTypes(), ToolBytecode.getClasses(parameterValues))) {
                        method = m;
                        break;
                    }
                }
            }
        }

        //获取到
        if (method != null) {
            int parameterCount = method.getParameterCount();
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            if (parameterValues != null && parameterCount == parameterValues.length) {
                if (SqlCommandType.INSERT.equals(sqlCommandType) ||
                        SqlCommandType.UPDATE.equals(sqlCommandType) ||
                        SqlCommandType.SELECT.equals(sqlCommandType)
                ) {

                    Parameter[] methodParameters = method.getParameters();
                    //加密参数
                    for (int i = 0; i < methodParameters.length; i++) {
                        Parameter methodParameter = methodParameters[i];
                        //1.方法参数
                        //2.方法
                        //3.mapper
                        Crypto crypto = methodParameter.getDeclaredAnnotation(Crypto.class);
                        if (crypto == null) {
                            crypto = method.getDeclaredAnnotation(Crypto.class);
                        }
                        if (crypto == null) {
                            crypto = clazz.getDeclaredAnnotation(Crypto.class);
                        }
                        if (crypto != null) {
                            Type genericParameterType = method.getGenericParameterTypes()[i];
                            Class<?> type = null;
                            if (genericParameterType instanceof TypeVariable typeVariable) {
                                type = ToolBytecode.extractGenericClass(clazz, Object.class, 0);
                            } else if (genericParameterType instanceof ParameterizedType parameterizedType) {
                                Type rawType = parameterizedType.getRawType();
                                if (rawType instanceof Class) {
                                    type = (Class<?>) rawType;
                                }
                            }
                            if (type != null) {
                                parameterValues[i] = encode(parameterValues[i], type, crypto);
                            }
                        }
                    }
                }
            }

            //解密查询结果
            if (SqlCommandType.SELECT.equals(sqlCommandType)) {
                Crypto crypto = method.getDeclaredAnnotation(Crypto.class);
                if (crypto == null) {
                    crypto = clazz.getDeclaredAnnotation(Crypto.class);
                }
                if (crypto != null) {
                    Class<?> returnType = method.getReturnType();
                    return decode(invocation.proceed(), returnType, crypto);
                }
            }
        }
        return invocation.proceed();
    }

    private Object encode(Object parameter, Class<?> type, Crypto crypto) {
        return handleObject(parameter, type, crypto, this::encodeString);
    }

    private Object decode(Object returnResult, Class<?> returnType, Crypto crypto) {
        return handleObject(returnResult, returnType, crypto, this::decodeString);
    }

    private Object handleObject(Object object, Class<?> type, Crypto crypto, BiFunction<String, Crypto, String> handler) {
        if (object == null) {
            return null;
        }
        switch (object) {
            case Map map: {
                String[] names = crypto.name();
                if (names.length != 0) {
                    for (String name : names) {
                        Object value = map.get(name);
                        if (value instanceof String string) {
                            map.put(name, handler.apply(string, crypto));
                        }
                    }
                }
                break;
            }
            case List list:
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        Object element = list.get(i);
                        if (element == null) {
                            continue;
                        }
                        Object newElement = handleObject(element, element.getClass(), crypto, handler);
                        if (newElement != element) {
                            list.set(i, newElement);
                        }
                    }
                }
                break;
            case String string:
                return handler.apply(string, crypto);
            default: {
                Set<String> names = Set.of(crypto.name());
                Field[] fields = ToolBytecode.getFields(type, (PredicateThrow<Field, Throwable>) field -> {
                    if (field.getType() == String.class &&
                            names.contains(field.getName())) {
                        return field.getDeclaredAnnotation(Crypto.class) != null;
                    }
                    return false;
                });
                for (Field field : fields) {
                    try {
                        ToolBytecode.setFieldValue(object, field, handler.apply((String) ToolBytecode.getFieldValue(object, field), crypto));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
        return object;
    }

    private String encodeString(String value, Crypto crypto) {
        return value + "---";
    }

    private String decodeString(String value, Crypto crypto) {
        return value.replace("---", "");
    }

    @Override
    public Object plugin(Object target) {
        Object real = realTarget(target);
        return Plugin.wrap(real, this);
    }

    private Object realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            return realTarget(SystemMetaObject.forObject(target).getValue("h.target"));
        }
        return target;
    }
}
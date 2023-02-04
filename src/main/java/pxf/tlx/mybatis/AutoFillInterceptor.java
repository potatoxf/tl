package pxf.tlx.mybatis;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.api.InstanceFactory;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolBytecode;
import pxf.tl.util.ToolLog;
import pxf.tl.util.ToolMap;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * mybatis自动设置插件
 *
 * @author potatoxf
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class AutoFillInterceptor extends InstanceFactory<String, AutoFillValueHandler> implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoFillInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] args = invocation.getArgs();
        final MappedStatement mappedStatement = (MappedStatement) args[0];
        final SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (sqlCommandType == SqlCommandType.INSERT ||
                sqlCommandType == SqlCommandType.UPDATE) {
            //获取接口参数
            Object[] parameterValues = null;
            if (args.length > 1) {
                if (args[1] != null) {
                    if (args[1] instanceof Map map && !map.isEmpty()) {
                        if (map.containsKey("param1")) {
                            parameterValues = ToolMap.getValueList(map,
                                    IntStream.rangeClosed(1, map.size() / 2).mapToObj(i -> "param" + i).toArray(Object[]::new)).toArray();
                        } else if (map.containsKey("arg0")) {
                            parameterValues = ToolMap.getValueList(map,
                                    IntStream.range(0, map.size() / 3).mapToObj(i -> "arg" + i).toArray(Object[]::new)).toArray();
                        } else if (map.containsKey("collection")) {
                            parameterValues = new Object[]{map.get("collection")};
                        } else if (map.containsKey("list")) {
                            parameterValues = new Object[]{map.get("list")};
                        } else if (map.containsKey("array")) {
                            parameterValues = new Object[]{map.get("array")};
                        } else {
                            parameterValues = new Object[]{map.get("arg0")};
                        }
                    } else {
                        parameterValues = new Object[]{args[1]};
                    }
                }
            }
            if (parameterValues != null) {
                for (Object parameterValue : parameterValues) {
                    if (parameterValue == null) {
                        continue;
                    }
                    handleObject(sqlCommandType, parameterValue);
                }
            }
        }


        return invocation.proceed();
    }

    private void handleObject(@Nonnull SqlCommandType sqlCommandType,
                              @Nonnull Object object) {
        switch (object) {
            case CharSequence charSequence -> ToolLog.trace(() -> "Set value no support CharSequence type");
            case Number number -> ToolLog.trace(() -> "Set value no support Number type");
            case Map map -> handleMap(sqlCommandType, (Map<Object, Object>) map);
            case Iterable iterable -> {
                for (Object element : iterable) {
                    handleObject(sqlCommandType, element);
                }
            }
            default -> handleBean(sqlCommandType, object);
        }
    }

    private void handleMap(@Nonnull SqlCommandType sqlCommandType,
                           @Nonnull Map<Object, Object> map) {
        for (String key : this.getKeySet()) {
            handleMapField(sqlCommandType, map, key);
        }
    }

    private void handleMapField(@Nonnull SqlCommandType sqlCommandType,
                                @Nonnull Map<Object, Object> map,
                                @Nonnull String key) {
        AutoFillValueHandler autoFillValueHandler = this.getInstance(key);
        if (autoFillValueHandler != null) {
            Object fieldValue = map.get(key);
            Class<?> fieldType = fieldValue == null ? null : fieldValue.getClass();
            Object value = fieldValue;
            Object newValue;
            try {
                newValue = autoFillValueHandler.handle(map, fieldValue, fieldType, sqlCommandType == SqlCommandType.INSERT);
            } catch (Throwable e) {
                ToolLog.error(LOGGER, () -> "Failure to get auto fill field value with " + autoFillValueHandler + " on " + key + " of Map", e);
                return;
            }

            if (newValue != null && (fieldType == null || fieldType.isInstance(newValue))) {
                value = newValue;
            }
            if (value != fieldValue) {
                map.put(key, value);

                Object finalValue = value;

                ToolLog.debug(LOGGER, () -> "Success to auto fill field value [" + finalValue + "] on " + key + " of Map");
            }

        }
    }

    private void handleBean(@Nonnull SqlCommandType sqlCommandType,
                            @Nonnull Object bean) {
        Field[] fields = ToolBytecode.getFields(bean.getClass(), field -> !Whether.staticModifier(field));
        for (Field field : fields) {
            List<AutoFill> autoFills = ToolBytecode.lookupAnnotationOnElement(field, AutoFill.class, false);
            if (autoFills.isEmpty()) {
                handleBeanFieldWithNoAnnotation(sqlCommandType, bean, field);
            } else {
                AutoFill autoFill = autoFills.get(0);
                if ((sqlCommandType == SqlCommandType.INSERT && autoFill.isInsert()) ||
                        (sqlCommandType == SqlCommandType.UPDATE && autoFill.isUpdate())) {
                    handleBeanFieldWithAnnotation(sqlCommandType, bean, field, autoFill);
                }
            }
        }
    }

    private void handleBeanFieldWithAnnotation(@Nonnull SqlCommandType sqlCommandType,
                                               @Nonnull Object bean,
                                               @Nonnull Field field,
                                               @Nonnull AutoFill autoFill) {
        Class<? extends AutoFillValueHandler>[] autoFillValueHandlerClasses = autoFill.value();
        if (autoFillValueHandlerClasses.length == 0) {
            return;
        }

        final Object fieldValue;
        try {
            fieldValue = ToolBytecode.getFieldValueAsSafe(bean, field);
        } catch (Throwable e) {
            LOGGER.error("Failure to auto fill field value", e);
            return;
        }
        Object value = fieldValue;
        for (Class<? extends AutoFillValueHandler> autoFillValueHandlerClass : autoFillValueHandlerClasses) {

            AutoFillValueHandler autoFillValueHandler = ToolBytecode.createInstanceSilent(autoFillValueHandlerClass);

            Class<?> fieldType = field.getType();

            Object newValue;
            try {
                newValue = autoFillValueHandler.handle(bean, fieldValue, fieldType, sqlCommandType == SqlCommandType.INSERT);
            } catch (Throwable e) {
                LOGGER.error("Failure to get auto fill field value with " + autoFillValueHandler + " on " + field + " of " + bean.getClass(), e);
                continue;
            }
            if (newValue != null && (fieldType == null || field.getType().isInstance(newValue))) {
                value = newValue;
            }

        }

        if (value != null && value != fieldValue) {
            try {
                ToolBytecode.setFieldValueAsSafe(bean, field, value);
            } catch (Throwable e) {
                LOGGER.error("Failure to auto fill field value", e);
                return;
            }

            Object finalValue = value;

            ToolLog.debug(LOGGER, () -> "Success to auto fill field value [" + finalValue + "] on " + field + " of " + bean.getClass());
        }
    }

    private void handleBeanFieldWithNoAnnotation(@Nonnull SqlCommandType sqlCommandType,
                                                 @Nonnull Object bean,
                                                 @Nonnull Field field) {
        AutoFillValueHandler autoFillValueHandler = this.getInstance(field.getName());
        if (autoFillValueHandler != null) {
            Object fieldValue;
            try {
                fieldValue = ToolBytecode.getFieldValueAsSafe(bean, field);
            } catch (Throwable e) {
                ToolLog.error(LOGGER, () -> "Failure to auto fill field value", e);
                return;
            }
            Object value = fieldValue;
            Object newValue;
            try {
                newValue = autoFillValueHandler.handle(bean, fieldValue, field.getType(), sqlCommandType == SqlCommandType.INSERT);
            } catch (Throwable e) {
                ToolLog.error(LOGGER, () -> "Failure to get auto fill field value with " + autoFillValueHandler + " on " + field + " of " + bean.getClass(), e);
                return;
            }

            if (field.getType().isInstance(newValue)) {
                value = newValue;
            }
            if (value != fieldValue) {
                try {
                    ToolBytecode.setFieldValueAsSafe(bean, field, value);
                } catch (Throwable e) {
                    ToolLog.error(LOGGER, () -> "Failure to auto fill field value", e);
                    return;
                }

                Object finalValue = value;

                ToolLog.debug(LOGGER, () -> "Success to auto fill field value [" + finalValue + "] on " + field + " of " + bean.getClass());
            }

        }
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
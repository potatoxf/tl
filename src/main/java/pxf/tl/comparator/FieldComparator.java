package pxf.tl.comparator;


import pxf.tl.help.Assert;
import pxf.tl.util.ToolBytecode;
import pxf.tl.util.ToolString;

import java.lang.reflect.Field;

/**
 * Bean字段排序器<br>
 * 参阅feilong-core中的PropertyComparator
 *
 * @param <T> 被比较的Bean
 * @author potatoxf
 */
public class FieldComparator<T> extends FuncComparator<T> {
    private static final long serialVersionUID = 9157326766723846313L;

    /**
     * 构造
     *
     * @param beanClass Bean类
     * @param fieldName 字段名
     */
    public FieldComparator(Class<T> beanClass, String fieldName) {
        this(getNonNullField(beanClass, fieldName));
    }

    /**
     * 构造
     *
     * @param field 字段
     */
    public FieldComparator(Field field) {
        this(true, field);
    }

    /**
     * 构造
     *
     * @param nullGreater 是否{@code null}在后
     * @param field       字段
     */
    public FieldComparator(boolean nullGreater, Field field) {
        super(
                nullGreater,
                (bean) ->
                        (Comparable<?>)
                                ToolBytecode.getFieldValueAsSafeSilent(bean, Assert.notNull(field, "Field must be not null!")));
    }

    /**
     * 获取字段，附带检查字段不存在的问题。
     *
     * @param beanClass Bean类
     * @param fieldName 字段名
     * @return 非null字段
     */
    private static Field getNonNullField(Class<?> beanClass, String fieldName) {
        final Field[] fields = ToolBytecode.getFields(beanClass, fieldName,false);
        if (fields.length == 0) {
            throw new IllegalArgumentException(
                    ToolString.format("Field [{}] not found in Class [{}]", fieldName, beanClass.getName()));
        }
        return fields[0];
    }
}

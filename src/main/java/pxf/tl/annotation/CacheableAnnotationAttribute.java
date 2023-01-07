package pxf.tl.annotation;


import pxf.tl.help.Assert;
import pxf.tl.util.ToolBytecode;
import pxf.tl.util.ToolObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * {@link AnnotationAttribute}的基本实现
 *
 * @author potatoxf
 */
public class CacheableAnnotationAttribute implements AnnotationAttribute {

    private final Annotation annotation;
    private final Method attribute;
    private boolean valueInvoked;
    private Object value;
    private boolean defaultValueInvoked;
    private Object defaultValue;

    public CacheableAnnotationAttribute(Annotation annotation, Method attribute) {
        Assert.notNull(annotation, "annotation must not null");
        Assert.notNull(attribute, "attribute must not null");
        this.annotation = annotation;
        this.attribute = attribute;
        this.valueInvoked = false;
        this.defaultValueInvoked = false;
    }

    @Override
    public Annotation getAnnotation() {
        return this.annotation;
    }

    @Override
    public Method getAttribute() {
        return this.attribute;
    }

    @Override
    public Object getValue() {
        if (!valueInvoked) {
            valueInvoked = true;
            value = ToolBytecode.invokeSilent(annotation, attribute);
        }
        return value;
    }

    @Override
    public boolean isValueEquivalentToDefaultValue() {
        if (!defaultValueInvoked) {
            defaultValue = attribute.getDefaultValue();
            defaultValueInvoked = true;
        }
        return ToolObject.equals(getValue(), defaultValue);
    }
}

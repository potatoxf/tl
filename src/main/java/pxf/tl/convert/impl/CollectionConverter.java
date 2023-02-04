package pxf.tl.convert.impl;


import pxf.tl.convert.Converter;
import pxf.tl.convert.ConverterRegistry;
import pxf.tl.help.Safe;
import pxf.tl.iter.AnyIter;
import pxf.tl.util.ToolCollection;
import pxf.tl.util.ToolBytecode;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Iterator;

/**
 * 各种集合类转换器
 *
 * @author potatoxf
 */
public class CollectionConverter implements Converter<Collection<?>> {

    /**
     * 集合类型
     */
    private final Type collectionType;
    /**
     * 集合元素类型
     */
    private final Type elementType;

    /**
     * 构造，默认集合类型使用{@link Collection}
     */
    public CollectionConverter() {
        this(Collection.class);
    }

    // ----------------------------------------------------------------------------------------------
    // Constractor start

    /**
     * 构造
     *
     * @param collectionType 集合类型
     */
    public CollectionConverter(Type collectionType) {
        this(collectionType, ToolBytecode.getTypeArgument(collectionType));
    }

    /**
     * 构造
     *
     * @param collectionType 集合类型
     */
    public CollectionConverter(Class<?> collectionType) {
        this(collectionType, ToolBytecode.getTypeArgument(collectionType));
    }

    /**
     * 构造
     *
     * @param collectionType 集合类型
     * @param elementType    集合元素类型
     */
    public CollectionConverter(Type collectionType, Type elementType) {
        this.collectionType = collectionType;
        this.elementType = elementType;
    }
    // ----------------------------------------------------------------------------------------------
    // Constractor end

    @Override
    public Collection<?> convert(Object value, Collection<?> defaultValue)
            throws IllegalArgumentException {
        final Collection<?> result = convertInternal(value);
        return Safe.value(result, defaultValue);
    }

    /**
     * 内部转换
     *
     * @param value 值
     * @return 转换后的集合对象
     */
    protected Collection<?> convertInternal(Object value) {
        final Collection<Object> collection =
                ToolCollection.create(ToolBytecode.getClass(this.collectionType));
        return addAll(collection, value, this.elementType);
    }

    /**
     * 将指定对象全部加入到集合中<br>
     * 提供的对象如果为集合类型，会自动转换为目标元素类型<br>
     * 如果为String，支持类似于[1,2,3,4] 或者 1,2,3,4 这种格式
     *
     * @param <T>         元素类型
     * @param collection  被加入的集合
     * @param value       对象，可能为Iterator、Iterable、Enumeration、Array，或者与集合元素类型一致
     * @param elementType 元素类型，为空时，使用Object类型来接纳所有类型
     * @return 被加入集合
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Collection<T> addAll(Collection<T> collection, Object value, Type elementType) {
        if (null == collection || null == value) {
            return collection;
        }
        if (null == elementType || elementType instanceof TypeVariable) {
            // 元素类型为空时，使用Object类型来接纳所有类型
            elementType = Object.class;
        }

        Iterator iter = AnyIter.ofObject(true, value, elementType);

        final ConverterRegistry convert = ConverterRegistry.getInstance();
        while (iter.hasNext()) {
            collection.add(convert.convert(elementType, iter.next()));
        }

        return collection;
    }
}

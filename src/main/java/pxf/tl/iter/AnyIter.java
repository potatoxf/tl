package pxf.tl.iter;

import pxf.tl.help.Whether;
import pxf.tl.util.ToolBytecode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;

/**
 * 任意对象可迭代
 *
 * @author potatoxf
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class AnyIter<T> extends AbstractCacheIter<T> {


    private AnyIter(@Nullable Iterator iterator,
                    boolean autoContinue,
                    Class<?> type,
                    @Nullable Predicate filter) {
        super(!autoContinue ? null : new LinkedList<>(), autoContinue, o -> (type == null || type.isInstance(o)) && (filter == null || filter.test(o)));
        initIterator(iterator);
    }

    private AnyIter(@Nonnull Iterator iterator,
                    boolean autoContinue,
                    @Nullable Predicate filter) {
        super(!autoContinue ? null : new LinkedList<>(), autoContinue, filter);
        initIterator(iterator);
    }

    public static AnyIter<Boolean> of(boolean... inputs) {
        return of(false, null, inputs);
    }

    public static AnyIter<Boolean> of(@Nullable Predicate<Boolean> predicate, boolean... inputs) {
        return of(false, predicate, inputs);
    }

    public static AnyIter<Boolean> of(boolean autoContinue, @Nullable Predicate<Boolean> predicate, boolean... inputs) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(
                    new ArrayIterator<Boolean>() {
                        @Override
                        int size() {
                            return inputs.length;
                        }

                        @Override
                        Boolean get(int i) {
                            return inputs[i];
                        }
                    },
                    autoContinue, predicate);
        }
    }


    public static AnyIter<Byte> of(byte... inputs) {
        return of(false, null, inputs);
    }

    public static AnyIter<Byte> of(@Nullable Predicate<Byte> predicate, byte... inputs) {
        return of(false, predicate, inputs);
    }

    public static AnyIter<Byte> of(boolean autoContinue, @Nullable Predicate<Byte> predicate, byte... inputs) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(
                    new ArrayIterator<Byte>() {
                        @Override
                        int size() {
                            return inputs.length;
                        }

                        @Override
                        Byte get(int i) {
                            return inputs[i];
                        }
                    },
                    autoContinue, predicate);
        }
    }


    public static AnyIter<Character> of(char... inputs) {
        return of(false, null, inputs);
    }

    public static AnyIter<Character> of(@Nullable Predicate<Character> predicate, char... inputs) {
        return of(false, predicate, inputs);
    }

    public static AnyIter<Character> of(boolean autoContinue, @Nullable Predicate<Character> predicate, char... inputs) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(
                    new ArrayIterator<Character>() {
                        @Override
                        int size() {
                            return inputs.length;
                        }

                        @Override
                        Character get(int i) {
                            return inputs[i];
                        }
                    },
                    autoContinue, predicate);
        }
    }

    public static AnyIter<Short> of(short... inputs) {
        return of(false, null, inputs);
    }

    public static AnyIter<Short> of(@Nullable Predicate<Short> predicate, short... inputs) {
        return of(false, predicate, inputs);
    }

    public static AnyIter<Short> of(boolean autoContinue, @Nullable Predicate<Short> predicate, short... inputs) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(
                    new ArrayIterator<Short>() {
                        @Override
                        int size() {
                            return inputs.length;
                        }

                        @Override
                        Short get(int i) {
                            return inputs[i];
                        }
                    },
                    autoContinue, predicate);
        }
    }

    public static AnyIter<Integer> of(int... inputs) {
        return of(false, null, inputs);
    }

    public static AnyIter<Integer> of(@Nullable Predicate<Integer> predicate, int... inputs) {
        return of(false, predicate, inputs);
    }

    public static AnyIter<Integer> of(boolean autoContinue, @Nullable Predicate<Integer> predicate, int... inputs) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(
                    new ArrayIterator<Integer>() {
                        @Override
                        int size() {
                            return inputs.length;
                        }

                        @Override
                        Integer get(int i) {
                            return inputs[i];
                        }
                    },
                    autoContinue, predicate);
        }
    }

    public static AnyIter<Long> of(long... inputs) {
        return of(false, null, inputs);
    }

    public static AnyIter<Long> of(@Nullable Predicate<Long> predicate, long... inputs) {
        return of(false, predicate, inputs);
    }

    public static AnyIter<Long> of(boolean autoContinue, @Nullable Predicate<Long> predicate, long... inputs) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(
                    new ArrayIterator<Long>() {
                        @Override
                        int size() {
                            return inputs.length;
                        }

                        @Override
                        Long get(int i) {
                            return inputs[i];
                        }
                    },
                    autoContinue, predicate);
        }
    }

    public static AnyIter<Float> of(float... inputs) {
        return of(false, null, inputs);
    }

    public static AnyIter<Float> of(@Nullable Predicate<Float> predicate, float... inputs) {
        return of(false, predicate, inputs);
    }

    public static AnyIter<Float> of(boolean autoContinue, @Nullable Predicate<Float> predicate, float... inputs) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(
                    new ArrayIterator<Float>() {
                        @Override
                        int size() {
                            return inputs.length;
                        }

                        @Override
                        Float get(int i) {
                            return inputs[i];
                        }
                    },
                    autoContinue, predicate);
        }
    }

    public static AnyIter<Double> of(double... inputs) {
        return of(false, null, inputs);
    }

    public static AnyIter<Double> of(@Nullable Predicate<Double> predicate, double... inputs) {
        return of(false, predicate, inputs);
    }

    public static AnyIter<Double> of(boolean autoContinue, @Nullable Predicate<Double> predicate, double... inputs) {

        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(
                    new ArrayIterator<Double>() {
                        @Override
                        int size() {
                            return inputs.length;
                        }

                        @Override
                        Double get(int i) {
                            return inputs[i];
                        }
                    },
                    autoContinue, predicate);
        }
    }

    public static <T> AnyIter<T> ofArray(boolean autoContinue, @Nullable T... inputs) {
        return ofArray(autoContinue, null, inputs);
    }

    public static <T> AnyIter<T> ofArray(@Nullable T... inputs) {
        return ofArray(false, null, inputs);
    }

    public static <T> AnyIter<T> ofArray(@Nullable Predicate<T> predicate, @Nullable T... inputs) {
        return ofArray(false, predicate, inputs);
    }

    public static <T> AnyIter<T> ofArray(boolean autoContinue, @Nullable Predicate<? super T> predicate, @Nullable T... inputs) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(
                    new ArrayIterator<>() {
                        @Override
                        int size() {
                            return inputs.length;
                        }

                        @Override
                        T get(int i) {
                            return inputs[i];
                        }
                    },
                    autoContinue, predicate);
        }
    }

    public static <T> AnyIter<T> ofEnumeration(boolean autoContinue, @Nullable Enumeration<T> inputs) {
        return ofEnumeration(autoContinue, null, inputs);
    }

    public static <T> AnyIter<T> ofEnumeration(@Nullable Enumeration<? extends T> inputs) {
        return ofEnumeration(false, null, inputs);
    }

    public static <T> AnyIter<T> ofEnumeration(@Nullable Predicate<? super T> predicate, @Nullable Enumeration<? extends T> inputs) {
        return ofEnumeration(false, predicate, inputs);
    }

    public static <T> AnyIter<T> ofEnumeration(boolean autoContinue, @Nullable Predicate<? super T> predicate, @Nullable Enumeration<? extends T> inputs) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(
                    new Iterator<>() {
                        @Override
                        public boolean hasNext() {
                            return inputs.hasMoreElements();
                        }

                        @Override
                        public T next() {
                            return inputs.nextElement();
                        }
                    },
                    autoContinue, predicate);
        }
    }

    public static <T> AnyIter<T> ofIterable(boolean autoContinue, @Nullable Iterable<? extends T> inputs) {
        return ofIterable(autoContinue, null, inputs);
    }

    public static <T> AnyIter<T> ofIterable(@Nullable Iterable<? extends T> inputs) {
        return ofIterable(false, null, inputs);
    }

    public static <T> AnyIter<T> ofIterable(@Nullable Predicate<? super T> predicate, @Nullable Iterable<? extends T> inputs) {
        return ofIterable(false, predicate, inputs);
    }

    public static <T> AnyIter<T> ofIterable(boolean autoContinue, @Nullable Predicate<? super T> predicate, @Nullable Iterable<? extends T> inputs) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(inputs.iterator(), autoContinue, predicate);
        }
    }

    public static <T> AnyIter<T> ofIterator(boolean autoContinue, @Nullable Iterator<? extends T> inputs) {
        return ofIterator(autoContinue, null, inputs);
    }

    public static <T> AnyIter<T> ofIterator(@Nullable Iterator<? extends T> inputs) {
        return ofIterator(false, null, inputs);
    }

    public static <T> AnyIter<T> ofIterator(@Nullable Predicate<? super T> predicate, @Nullable Iterator<? extends T> inputs) {
        return ofIterator(false, predicate, inputs);
    }

    public static <T> AnyIter<T> ofIterator(boolean autoContinue, @Nullable Predicate<? super T> predicate, @Nullable Iterator<? extends T> inputs) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(inputs, autoContinue, predicate);
        }
    }

    public static <T> AnyIter<T> ofCollection(boolean autoContinue, @Nullable Collection<? extends T> inputs) {
        return ofCollection(autoContinue, null, inputs);
    }

    public static <T> AnyIter<T> ofCollection(@Nullable Collection<? extends T> inputs) {
        return ofCollection(false, null, inputs);
    }

    public static <T> AnyIter<T> ofCollection(@Nullable Predicate<? super T> predicate, @Nullable Collection<? extends T> inputs) {
        return ofCollection(false, predicate, inputs);
    }

    public static <T> AnyIter<T> ofCollection(boolean autoContinue, @Nullable Predicate<? super T> predicate, @Nullable Collection<? extends T> inputs) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        } else {
            return new AnyIter<>(inputs.iterator(), autoContinue, predicate);
        }
    }

    public static <T> AnyIter<T> ofObject(boolean autoContinue, @Nullable Object inputs, Type type) {
        return ofObject(autoContinue, null, inputs, type);
    }

    public static <T> AnyIter<T> ofObject(@Nullable Object inputs, Type type) {
        return ofObject(false, null, inputs, type);
    }

    public static <T> AnyIter<T> ofObject(@Nullable Predicate<? super T> predicate, @Nullable Object inputs, Type type) {
        return ofObject(false, predicate, inputs, type);
    }

    public static <T> AnyIter<T> ofObject(boolean autoContinue, @Nullable Predicate<? super T> predicate, @Nullable Object inputs, Type type) {
        if (Whether.nvl(inputs)) {
            return ofEmpty();
        }
        Class<?> classType = ToolBytecode.getClass(type);
        switch (inputs) {
            case Iterator i:
                return new AnyIter<>(i, autoContinue, classType, predicate);
            case Collection c:
                return new AnyIter<>(c.iterator(), autoContinue, classType, predicate);
            case Iterable i:
                return new AnyIter<>(i.iterator(), autoContinue, classType, predicate);
            case Enumeration e:
                return new AnyIter<>(new EnumerationIterator(e), autoContinue, classType, predicate);
            default:
                if (inputs.getClass().isArray()) {
                    return new AnyIter<>(new ObjectArrayIterator(inputs), autoContinue, classType, predicate);
                } else if (classType.isInstance(inputs)) {
                    return new AnyIter<>(new ObjectArrayIterator(inputs), autoContinue, classType, predicate);
                } else {
                    return ofEmpty();
                }
        }
    }

    public static <T extends Enum<T>> AnyIter<T> ofEnum(boolean autoContinue, @Nullable Predicate<? super T> predicate, @Nullable Class<T> type) {
        if (type == null) {
            return ofEmpty();
        } else {
            return ofArray(autoContinue, predicate, type.getEnumConstants());
        }
    }

    public static <T> AnyIter<T> ofEmpty() {
        return new AnyIter<>(null, true, null);
    }

    /**
     * 计算新的节点，通过实现此方法，当调用{@link #hasNext()}时将此方法产生的节点缓存，直到调用{@link #next()}取出<br>
     * 当无下一个节点时，须返回{@code null}表示遍历结束
     *
     * @return 节点值
     */
    @Override
    protected T computeNext() {
        return getNextElement();
    }

    private static class EnumerationIterator implements Iterator {
        private final Enumeration enumeration;

        private EnumerationIterator(Enumeration enumeration) {
            this.enumeration = enumeration;
        }

        @Override
        public boolean hasNext() {
            return enumeration.hasMoreElements();
        }

        @Override
        public Object next() {
            return enumeration.nextElement();
        }
    }

    private abstract static class ArrayIterator<T> implements Iterator<T> {

        int i = 0;

        abstract int size();

        abstract T get(int i);

        @Override
        public boolean hasNext() {
            while (i < size()) {
                T t = get(i);
                if (t != null) {
                    return true;
                } else {
                    i++;
                }
            }
            return false;
        }

        @Override
        public T next() {
            return get(i++);
        }
    }

    private static class ObjectArrayIterator extends ArrayIterator<Object> {
        private final Object object;
        private final boolean isArray;
        private final int size;

        public ObjectArrayIterator(Object object) {
            this.object = object;
            this.isArray = object != null && object.getClass().isArray();
            this.size = isArray ? Array.getLength(object) : (object == null ? 0 : 1);

        }

        @Override
        int size() {
            return size;
        }

        @Override
        Object get(int i) {
            if (i < 0 || i > size) {
                throw new NoSuchElementException();
            }
            if (isArray) {
                return Array.get(object, i);
            } else {
                if (object == null) {
                    throw new NoSuchElementException();
                }
                return object;
            }
        }
    }
}

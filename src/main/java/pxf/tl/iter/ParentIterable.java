package pxf.tl.iter;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * 父级可迭代器
 *
 * @author potatoxf
 */
public class ParentIterable<T> implements Iterable<T> {
    private final T start;
    private final T end;
    private final Function<T, T> parentSupplier;
    private final BiPredicate<T, T> hasNextPredicate;

    public ParentIterable(
            T start, T end, Function<T, T> parentSupplier, BiPredicate<T, T> hasNextPredicate) {
        this.start = start;
        this.end = end;
        this.parentSupplier = parentSupplier;
        this.hasNextPredicate =
                hasNextPredicate == null ? (c, e) -> c != null && c != e : hasNextPredicate;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private T current = start;

            @Override
            public boolean hasNext() {
                return hasNextPredicate.test(current, end);
            }

            @Override
            public T next() {
                T result = current;
                current = parentSupplier.apply(current);
                return result;
            }
        };
    }
}

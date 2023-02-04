package pxf.tl.database.sql.formatter.core;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JSLikeList<T> implements Iterable<T> {

    private final List<T> tList;

    public JSLikeList(List<T> tList) {
        this.tList = tList == null ? Collections.emptyList() : new ArrayList<>(tList);
    }

    public List<T> toList() {
        return this.tList;
    }

    public <R> JSLikeList<R> map(Function<T, R> mapper) {
        return new JSLikeList<>(this.tList.stream().map(mapper).collect(Collectors.toList()));
    }

    public String join(CharSequence delimiter) {
        return this.tList.stream()
                .map(Optional::ofNullable)
                .map(x -> x.map(String::valueOf).orElse(""))
                .collect(Collectors.joining(delimiter));
    }

    public JSLikeList<T> with(List<T> other) {
        List<T> list = new ArrayList<>();
        list.addAll(this.toList());
        list.addAll(other);
        return new JSLikeList<>(list);
    }

    public String join() {
        return join(",");
    }

    public T get(int index) {
        if (index < 0) {
            return null;
        }
        if (tList.size() <= index) {
            return null;
        }
        return this.tList.get(index);
    }

    @Override
    public Iterator<T> iterator() {
        return this.tList.iterator();
    }

    public Stream<T> stream() {
        return this.tList.stream();
    }

    public int size() {
        return this.tList.size();
    }
}

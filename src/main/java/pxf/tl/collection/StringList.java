package pxf.tl.collection;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 字符串列表
 *
 * @author potatoxf
 */
public class StringList extends ArrayList<String> {
    /**
     * 分隔符
     */
    private final String delimiter;

    public StringList() {
        this(",");
    }

    public StringList(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * 依次设置字符串列表到指定索引位置
     *
     * @param index 指定索引位置
     * @param input 输入字符串
     */
    public void setStringList(int index, @Nullable String input) {
        AtomicInteger refIndex = new AtomicInteger(index);
        handleStringList(input, s -> super.set(refIndex.getAndIncrement(), s), false);
    }

    /**
     * 添加字符串列表
     *
     * @param input 输入字符串
     */
    public void addStringList(@Nullable String input) {
        handleStringList(input, super::add, false);
    }

    /**
     * 添加字符串列表到指定索引位置
     *
     * @param index 指定索引位置
     * @param input 输入字符串
     */
    public void addStringList(int index, @Nullable String input) {
        handleStringList(input, s -> super.add(index, s), false);
    }

    /**
     * 移除字符串列表
     *
     * @param input 输入字符串
     */
    public void removeStringList(@Nullable String input) {
        handleStringList(input, super::remove, true);
    }

    /**
     * 处理字符串列表
     *
     * @param input    输入字符串
     * @param consumer 字符串处理器
     * @param isExist  是否判断存在
     */
    private void handleStringList(@Nullable String input, Consumer<String> consumer, boolean isExist) {
        if (input != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(input, delimiter);
            while (stringTokenizer.hasMoreTokens()) {
                String token = stringTokenizer.nextToken().trim();
                if (isExist) {
                    while (contains(token)) {
                        consumer.accept(token);
                    }
                } else {
                    while (!contains(token)) {
                        consumer.accept(token);
                    }
                }
            }
        }
    }


    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index   index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public String set(int index, String element) {
        String old = get(index);
        this.setStringList(index, element);
        return old;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param element element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    @Override
    public boolean add(String element) {
        int size = size();
        this.addStringList(element);
        return size != size();
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index   index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public void add(int index, String element) {
        this.addStringList(index, element);
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * {@code i} such that
     * {@code Objects.equals(element, get(i))}
     * (if such an element exists).  Returns {@code true} if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param element element to be removed from this list, if present
     * @return {@code true} if this list contained the specified element
     */
    @Override
    public boolean remove(Object element) {
        if (element instanceof String string) {
            int size = size();
            this.removeStringList(string);
            return size != size();
        }
        return false;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified collection is this list, and this
     * list is nonempty.)
     *
     * @param collection collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(Collection<? extends String> collection) {
        int size = size();
        for (String element : collection) {
            this.addStringList(element);
        }
        return size != size();
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index      index at which to insert the first element from the
     *                   specified collection
     * @param collection collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException      if the specified collection is null
     */
    @Override
    public boolean addAll(int index, Collection<? extends String> collection) {
        int size = size();
        for (String element : collection) {
            this.addStringList(index, element);
        }
        return size != size();
    }

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     *
     * @param collection collection containing elements to be removed from this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException   if the class of an element of this list
     *                              is incompatible with the specified collection
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *                              specified collection does not permit null elements
     *                              (<a href="Collection.html#optional-restrictions">optional</a>),
     *                              or if the specified collection is null
     * @see Collection#contains(Object)
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        int size = size();
        for (Object element : collection) {
            if (element instanceof String string) {
                this.removeStringList(string);
            }
        }
        return size != size();
    }
}

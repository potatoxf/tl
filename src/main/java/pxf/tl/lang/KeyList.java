package pxf.tl.lang;


import pxf.tl.function.FunctionThrow;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 元素内部引用
 *
 * @author potatoxf
 */
public class KeyList<T> {
    private final List<T> elementList = new LinkedList<T>();
    private final Map<String, Map<Object, T>> elementTable = new HashMap<String, Map<Object, T>>();
    private final Map<String, FunctionThrow<T, Object, RuntimeException>> elementKeySupplierTable =
            new HashMap<String, FunctionThrow<T, Object, RuntimeException>>();

    private final AtomicBoolean isInit = new AtomicBoolean(false);

    /**
     * 注册元素
     *
     * @param element
     * @return
     */
    public KeyList<T> registerElement(T element) {
        if (element != null) {
            isInit.set(false);
            synchronized (elementList) {
                elementList.add(element);
            }
        }
        return this;
    }

    /**
     * @param type
     * @param factoryThrow
     * @return
     */
    public KeyList<T> registerElementKeySupplier(
            String type, FunctionThrow<T, Object, RuntimeException> factoryThrow) {
        if (factoryThrow != null) {
            isInit.set(false);
            synchronized (elementKeySupplierTable) {
                elementKeySupplierTable.put(type, factoryThrow);
            }
        }
        return this;
    }

    /**
     * 初始化内部引用键列表
     */
    public void init() {
        if (!isInit.get()) {
            boolean b = isInit.compareAndSet(false, true);
            if (!b) {
                return;
            }
            Map<String, FunctionThrow<T, Object, RuntimeException>> tempElementKeySupplierTable;
            synchronized (elementKeySupplierTable) {
                tempElementKeySupplierTable =
                        new HashMap<String, FunctionThrow<T, Object, RuntimeException>>(elementKeySupplierTable);
            }
            List<T> tempElementList;
            synchronized (elementList) {
                tempElementList = new ArrayList<T>(elementList);
            }
            synchronized (elementTable) {
                elementTable.clear();
                for (Map.Entry<String, FunctionThrow<T, Object, RuntimeException>> entry :
                        tempElementKeySupplierTable.entrySet()) {
                    Map<Object, T> map = new HashMap<Object, T>(tempElementList.size(), 1);
                    FunctionThrow<T, Object, RuntimeException> keySupplier = entry.getValue();
                    for (T t : tempElementList) {
                        map.put(keySupplier.apply(t), t);
                    }
                    elementTable.put(entry.getKey(), map);
                }
            }
        }
    }

    /**
     * @param type
     * @param key
     * @return
     */
    public T getElement(String type, Object key) {
        init();
        synchronized (elementTable) {
            Map<Object, T> map = elementTable.get(type);
            if (map != null) {
                return map.get(type);
            }
        }
        return null;
    }

    /**
     * @return
     */
    public List<T> getElementList() {
        init();
        synchronized (elementList) {
            return new ArrayList<T>(elementList);
        }
    }
}

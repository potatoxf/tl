package pxf.tl.lang;


import pxf.tl.api.Pair;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author potatoxf
 */
public final class ThreadInstanceManager<T> {
    private final ThreadLocal<Pair<Boolean, T>> threadLocal = new ThreadLocal<>();
    private Supplier<T> defaultInstanceSupplier;

    /**
     * @param defaultInstanceSupplier
     */
    public ThreadInstanceManager(Supplier<T> defaultInstanceSupplier) {
        this.defaultInstanceSupplier = Objects.requireNonNull(defaultInstanceSupplier);
    }

    /**
     * @param defaultInstance
     */
    public ThreadInstanceManager(T defaultInstance) {
        Objects.requireNonNull(defaultInstanceSupplier);
        this.defaultInstanceSupplier = () -> defaultInstance;
    }

    /**
     * @return
     */
    public T getInstance() {
        Pair<Boolean, T> loaderPair = threadLocal.get();
        if (loaderPair != null) {
            try {
                return loaderPair.getValue();
            } finally {
                Boolean key = loaderPair.getKey();
                if (key) {
                    threadLocal.remove();
                }
            }
        } else {
            return defaultInstanceSupplier.get();
        }
    }

    /**
     * @param instance
     */
    public void setInstance(T instance) {
        if (instance != null) {
            threadLocal.set(new Pair<>(false, instance));
        }
    }

    /**
     * @param defaultInstanceSupplier
     * @return
     */
    public void setDefaultInstanceSupplier(Supplier<T> defaultInstanceSupplier) {
        if (defaultInstanceSupplier != null) {
            this.defaultInstanceSupplier = defaultInstanceSupplier;
        }
    }

    /**
     * @param instance
     */
    public void setOnceInstance(T instance) {
        if (instance != null) {
            threadLocal.set(new Pair<>(true, instance));
        }
    }
}

package pxf.tl.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.function.SupplierThrow;
import pxf.tl.util.ToolLog;

import java.lang.ref.Reference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * 单例提供器
 *
 * @author potatoxf
 */
public final class InstanceSupplier<T> implements Supplier<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceSupplier.class);
    /**
     * 实例提供器
     */
    private final SupplierThrow<Reference<T>, Throwable> instanceReferenceSupplier;
    /**
     * 默认实例提供器
     */
    private final SupplierThrow<Reference<T>, Throwable> defaultReferenceSupplier;
    /**
     * 实例存储容器
     */
    private final AtomicReference<Reference<T>> instanceContainer = new AtomicReference<>();

    public static <T> InstanceSupplier<T> of(
            SupplierThrow<T, Throwable> instanceSupplier) {
        return InstanceSupplier.of(null, instanceSupplier, null);
    }

    public static <T> InstanceSupplier<T> of(
            SupplierThrow<T, Throwable> instanceSupplier,
            SupplierThrow<T, Throwable> defaultSupplier) {
        return InstanceSupplier.of(null, instanceSupplier, defaultSupplier);
    }

    public static <T> InstanceSupplier<T> of(
            T instance,
            SupplierThrow<T, Throwable> instanceSupplier,
            SupplierThrow<T, Throwable> defaultSupplier) {
        Reference<T> instanceReference = instance == null ? null : ReferenceType.create(ReferenceType.SOFT, instance);
        SupplierThrow<Reference<T>, Throwable> instanceReferenceSupplier = instanceSupplier == null ? null
                : () -> ReferenceType.create(ReferenceType.SOFT, instanceSupplier.getThrow());
        SupplierThrow<Reference<T>, Throwable> defaultReferenceSupplier = defaultSupplier == null ? null
                : () -> ReferenceType.create(ReferenceType.SOFT, defaultSupplier.getThrow());
        return new InstanceSupplier<>(instanceReference, instanceReferenceSupplier, defaultReferenceSupplier);
    }

    public static <T> InstanceSupplier<T> ofReference(
            SupplierThrow<Reference<T>, Throwable> instanceReferenceSupplier) {
        return new InstanceSupplier<>(null, instanceReferenceSupplier, null);
    }

    public static <T> InstanceSupplier<T> ofReference(
            SupplierThrow<Reference<T>, Throwable> instanceReferenceSupplier,
            SupplierThrow<Reference<T>, Throwable> defaultReferenceSupplier) {
        return new InstanceSupplier<>(null, instanceReferenceSupplier, defaultReferenceSupplier);
    }

    public static <T> InstanceSupplier<T> ofReference(
            Reference<T> instanceReference,
            SupplierThrow<Reference<T>, Throwable> instanceReferenceSupplier,
            SupplierThrow<Reference<T>, Throwable> defaultReferenceSupplier) {
        return new InstanceSupplier<>(instanceReference, instanceReferenceSupplier, defaultReferenceSupplier);
    }

    private InstanceSupplier(
            Reference<T> instanceReference,
            SupplierThrow<Reference<T>, Throwable> instanceReferenceSupplier,
            SupplierThrow<Reference<T>, Throwable> defaultReferenceSupplier) {
        this.instanceContainer.set(instanceReference);
        this.instanceReferenceSupplier = instanceReferenceSupplier;
        this.defaultReferenceSupplier = defaultReferenceSupplier;
    }

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public T get() {
        T instance = null;
        Reference<T> instanceReference = instanceContainer.getAcquire();
        if (instanceReference != null) {
            instance = instanceReference.get();
        }
        if (instance == null) {
            instanceContainer.compareAndSet(instanceReference, null);
            try {
                instanceReference = create();
            } catch (Throwable e) {
                ToolLog.warn(LOGGER, e, () -> "Error to create instance Reference");
            }
            if (instanceReference == null) {
                ToolLog.warn(LOGGER, () -> "Error to create instance Reference of null");
            }
            instance = instanceReference.get();
            if (instance == null) {
                ToolLog.warn(LOGGER, () -> "Error to create instance of null");
            }
            instanceContainer.compareAndSet(null, instanceReference);
        }
        return instance;
    }

    private Reference<T> create() {
        Reference<T> instanceReference = null;
        if (instanceContainer.getAcquire() == null) {
            synchronized (instanceContainer) {
                instanceReference = instanceContainer.getAcquire();
                if (instanceReference == null) {
                    if (instanceReferenceSupplier != null) {
                        try {
                            instanceReference = instanceReferenceSupplier.get();
                        } catch (Throwable e) {
                            ToolLog.warn(LOGGER, e, () -> "Error getting value from instance provider");
                        }
                    }
                    if (instanceReference == null && defaultReferenceSupplier != null) {
                        try {
                            instanceReference = defaultReferenceSupplier.get();
                        } catch (Throwable e) {
                            ToolLog.warn(LOGGER, e, () -> "Error getting value from default provider");
                        }
                    }
                    this.instanceContainer.compareAndSet(null, instanceReference);
                }
            }
        }
        return instanceReference;
    }
}

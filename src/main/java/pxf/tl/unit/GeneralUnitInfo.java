package pxf.tl.unit;


import pxf.tl.help.Whether;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 通用单位信息
 *
 * @author potatoxf
 */
public final class GeneralUnitInfo<E extends UnitInfo<E>> implements UnitInfo<E> {

    private final int grade;
    private final int nextIntervalExact;
    private final int nextIntervalHuman;
    private final UnitCatalog<E> catalog;

    public GeneralUnitInfo(Class<E> clz, int grade, int nextInterval) {
        this(clz, grade, nextInterval, nextInterval);
    }

    public GeneralUnitInfo(Class<E> clz, int grade, int nextIntervalExact, int nextIntervalHuman) {
        if (clz.isEnum()) {
            this.catalog = GeneralUnitCatalog.buildEnumUnitInfo(clz);
        } else {
            this.catalog = GeneralUnitCatalog.buildUnitInfo(clz);
        }
        this.grade = grade;
        this.nextIntervalExact = nextIntervalExact;
        this.nextIntervalHuman = nextIntervalHuman;
    }

    public GeneralUnitInfo(UnitCatalog<E> catalog, int grade, int nextInterval) {
        this(catalog, grade, nextInterval, nextInterval);
    }

    public GeneralUnitInfo(
            UnitCatalog<E> catalog, int grade, int nextIntervalExact, int nextIntervalHuman) {
        this.catalog = Objects.requireNonNull(catalog);
        this.grade = grade;
        this.nextIntervalExact = nextIntervalExact;
        this.nextIntervalHuman = nextIntervalHuman;
    }

    /**
     * 获取等级，从小到大依次增加，每相差一就代表两个单位相差一级
     *
     * @return 返回数字等级
     */
    @Override
    public int grade() {
        return grade;
    }

    /**
     * 该单位与下一个大单位的间隔，等级是越来越高
     *
     * @return 与下一个大单位间隔数值
     */
    @Override
    public int nextIntervalExact() {
        return nextIntervalExact;
    }

    /**
     * 该单位与下一个大单位的间隔，等级是越来越高
     *
     * @return 与下一个大单位间隔数值
     */
    @Override
    public int nextIntervalHuman() {
        return nextIntervalHuman;
    }

    /**
     * 单位分类
     *
     * @return {@code UnitCatalog<E>}
     */
    @Override
    public UnitCatalog<E> unitCatalog() {
        return catalog;
    }

    /**
     * 通用单位目录
     *
     * @author potatoxf
     */
    private static final class GeneralUnitCatalog<E extends UnitInfo<E>> implements UnitCatalog<E> {

        /**
         * 缓存分类
         */
        private static final Map<Class<?>, GeneralUnitCatalog<?>> CACHE = new HashMap<>();
        /**
         * 锁
         */
        private static final ReentrantLock LOCK = new ReentrantLock();
        /**
         * 目标类型
         */
        private final Class<E> targetType;
        /**
         * 容器
         */
        private final List<E> container;

        private GeneralUnitCatalog(Class<E> targetType, List<E> container) {
            this.targetType = targetType;
            this.container = container;
        }

        /**
         * 构建单位分类
         *
         * @param clz 单位的 {@code Class}
         * @param <E> 类型 {@code <E extends Enum<E> & UnitEnumInfo<E>>}
         * @return {@code GeneralUnitCatalog<E>}
         */
        @SuppressWarnings("unchecked")
        public static <E extends UnitInfo<E>> GeneralUnitCatalog<E> buildEnumUnitInfo(Class<E> clz) {
            if (!clz.isEnum()) {
                throw new IllegalArgumentException("The class [" + clz + "] is not an enumerated type");
            }
            LOCK.lock();
            try {
                GeneralUnitCatalog<E> generalUnitCatalog = (GeneralUnitCatalog<E>) CACHE.get(clz);
                if (generalUnitCatalog == null) {
                    generalUnitCatalog = new GeneralUnitCatalog<>(clz, Arrays.asList(clz.getEnumConstants()));
                    CACHE.put(clz, generalUnitCatalog);
                }
                return generalUnitCatalog;
            } finally {
                LOCK.unlock();
            }
        }

        /**
         * 构建单位分类
         *
         * @param clz 单位的 {@code Class}
         * @param <E> 类型 {@code <E extends UnitInfo<E>>}
         * @return {@code GeneralUnitCatalog<E>}
         */
        @SuppressWarnings("unchecked")
        public static <E extends UnitInfo<E>> GeneralUnitCatalog<E> buildUnitInfo(Class<E> clz) {
            LOCK.lock();
            try {
                GeneralUnitCatalog<E> generalUnitCatalog = (GeneralUnitCatalog<E>) CACHE.get(clz);
                if (generalUnitCatalog == null) {
                    List<E> list =
                            Arrays.stream(clz.getFields())
                                    .filter(Whether::publicStaticFinalModifier)
                                    .map(
                                            field -> {
                                                try {
                                                    field.setAccessible(true);
                                                    Object obj = field.get(null);
                                                    if (clz.isInstance(obj)) {
                                                        return (E) obj;
                                                    }
                                                } catch (IllegalAccessException e) {
                                                    e.printStackTrace();
                                                }
                                                return null;
                                            })
                                    .filter(Whether::noNvl)
                                    .collect(Collectors.toList());
                    generalUnitCatalog = new GeneralUnitCatalog<>(clz, list);
                    CACHE.put(clz, generalUnitCatalog);
                }
                return generalUnitCatalog;
            } finally {
                LOCK.unlock();
            }
        }

        /**
         * 目标类型
         *
         * @return {@code Class<E>}
         */
        @Override
        public Class<E> targetType() {
            return targetType;
        }

        @Override
        public Iterator<E> iterator() {
            return container.iterator();
        }
    }
}

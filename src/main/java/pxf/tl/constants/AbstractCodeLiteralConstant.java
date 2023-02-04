package pxf.tl.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统编码常量，并检查系统功能的常量是否重复在范围内
 *
 * @author potatoxf
 */
public abstract class AbstractCodeLiteralConstant<T extends AbstractCodeLiteralConstant<T>> extends
        AbstractLiteralConstant<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCodeLiteralConstant.class);
    /**
     * 存储编码常量
     */
    private static final Map<Integer, AbstractCodeLiteralConstant<?>> CODE_CONSTANT_MAP = new ConcurrentHashMap<>();
    /**
     * 存储编码范围
     */
    private static final Map<Class<? extends AbstractCodeLiteralConstant<?>>, int[]> CODE_CONSTANT_RANGE_MAP = new ConcurrentHashMap<>();

    protected boolean success;

    protected AbstractCodeLiteralConstant(int code, String msg) {
        this(code, msg, false);
    }

    protected AbstractCodeLiteralConstant(int code, String msg, boolean success) {
        super(code, msg);
        this.success = success;
        registerCodeConstant(this);
    }

    /**
     * 注册类型范围
     *
     * @param clazz {@code Class<? super CodeConstant<?>>}
     * @param start 起始值
     * @param end   结束值
     */
    protected static void registerRange(Class<? extends AbstractCodeLiteralConstant<?>> clazz, int start,
                                        int end) {
        if (start > end) {
            throw new IllegalArgumentException("<CodeConstant> start > end!");
        }

        if (CODE_CONSTANT_RANGE_MAP.containsKey(clazz)) {
            throw new IllegalArgumentException(
                    String.format("<CodeConstant> Class:%s already exist!", clazz.getSimpleName()));
        }
        CODE_CONSTANT_RANGE_MAP.forEach((k, v) -> {
            if ((start >= v[0] && start <= v[1]) || (end >= v[0] && end <= v[1])) {
                throw new IllegalArgumentException(String
                        .format("<CodeConstant> Class:%s 's id range[%d,%d] has " + "intersection with "
                                        + "class:%s", clazz.getSimpleName(), start, end,
                                k.getSimpleName()));
            }
        });

        CODE_CONSTANT_RANGE_MAP.put(clazz, new int[]{start, end});

        // 提前初始化static变量，进行范围检测
        Field[] fields = clazz.getFields();
        if (fields.length != 0) {
            try {
                fields[0].get(clazz);
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        }
    }

    /**
     * 注册编码常量
     *
     * @param abstractCodeConstant {@code AbstractCodeLiteralConstant<?>}
     */
    protected static void registerCodeConstant(AbstractCodeLiteralConstant<?> abstractCodeConstant) {
        int[] idRange = CODE_CONSTANT_RANGE_MAP.get(abstractCodeConstant.getClass());
        if (idRange == null) {
            throw new IllegalArgumentException(String
                    .format("<CodeConstant> Class:%s has not been registered!",
                            abstractCodeConstant.getClass().getSimpleName()));
        }
        int code = abstractCodeConstant.getCode();
        if (code < idRange[0] || code > idRange[1]) {
            throw new IllegalArgumentException(String
                    .format("<CodeConstant> Id(%d) out of range[%d,%d], " + "class:%s", code, idRange[0],
                            idRange[1], abstractCodeConstant.getClass().getSimpleName()));
        }
        if (CODE_CONSTANT_MAP.containsKey(code)) {
            if (LOG.isErrorEnabled()) {
                LOG.error(String
                        .format("<CodeConstant> Id(%d) out of range[%d,%d], " + "class:%s  code is repeat!",
                                code, idRange[0], idRange[1], abstractCodeConstant.getClass().getSimpleName()));
            }
            System.exit(0);
        }
        CODE_CONSTANT_MAP.put(code, abstractCodeConstant);
    }

    public String msg() {
        return getMessage();
    }

    public boolean isSuccess() {
        return success;
    }

}

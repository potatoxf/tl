package pxf.tl.constants;


import pxf.tl.api.LiteralConstant;
import pxf.tl.api.PoolOfString;

import javax.annotation.Nonnull;

/**
 * 抽象可查找常量
 *
 * @author potatoxf
 */
public abstract class AbstractLiteralConstant<T extends AbstractLiteralConstant<T>>
        implements LiteralConstant<T> {
    /**
     * 唯一身份牌
     */
    private final int code;
    /**
     * 唯一名称
     */
    private final String name;
    /**
     * 别名
     */
    private final String[] alias;
    /**
     * 是否忽略名字大小写
     */
    private final boolean isIgnoreNameCase;
    /**
     * 是否忽略别名大小写
     */
    private final boolean isIgnoreAliasCase;

    protected AbstractLiteralConstant(int code, String name) {
        this(code, name, true, true, PoolOfString.EMPTY);
    }

    protected AbstractLiteralConstant(int code, String name, String... alias) {
        this(code, name, true, true, alias);
    }

    protected AbstractLiteralConstant(
            int code,
            String name,
            boolean isIgnoreNameCase,
            boolean isIgnoreAliasCase,
            String... alias) {
        this.code = code;
        this.isIgnoreNameCase = isIgnoreNameCase;
        this.name = name;
        this.isIgnoreAliasCase = isIgnoreAliasCase;
        this.alias = alias;
    }

    /**
     * 通过身份牌查找
     *
     * @return 返回唯一身份牌
     */
    @Override
    public final int getCode() {
        return code;
    }

    /**
     * 通过唯一名称查找
     *
     * @return 返回名称
     */
    @Nonnull
    @Override
    public final String getName() {
        return name;
    }

    /**
     * 通过别名查找
     *
     * @return 返回别名
     */
    @Override
    public final String[] alias() {
        return alias;
    }

    /**
     * 是否忽略身份牌大小写
     *
     * @return {@code true}忽略，否则 {@code false}
     */
    @Override
    public final boolean isIgnoreNameCase() {
        return isIgnoreNameCase;
    }

    /**
     * 是否忽略别名大小写
     *
     * @return {@code true}忽略，否则 {@code false}
     */
    @Override
    public final boolean isIgnoreAliasCase() {
        return isIgnoreAliasCase;
    }
}

package pxf.tlx.basesystem.action;

import lombok.Getter;
import lombok.Setter;
import pxf.tl.api.InstanceSupplier;
import pxf.tl.api.Tuple;
import pxf.tl.help.Safe;
import pxf.tlx.basesystem.data.mapper.AccountMapper;
import pxf.tlx.basesystem.data.mapper.SystemMapper;
import pxf.tlx.servlet.CurrentRequestHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 设置功能类
 *
 * @author potatoxf
 */
@Getter
@Setter
public final class SettingAction {
    /**
     * 页面大小
     */
    public static final String NAME_PAGE_SIZE = "__page_size";
    /**
     * 键值
     */
    private static final InstanceSupplier<WeakHashMap<Tuple<String>, String>> CACHE = InstanceSupplier.of(WeakHashMap::new);
    /**
     * 键值模板
     */
    private static final String KEY_TEMPLATE = "$_$%s$_$%s$_$";
    /**
     * 账户Mapper
     */
    private final AccountMapper accountMapper;
    /**
     * 系统Mapper
     */
    private final SystemMapper systemMapper;

    /**
     * 默认页面大小
     */
    private int defaultPageSize = 20;

    public SettingAction(@Nonnull AccountMapper accountMapper, @Nonnull SystemMapper systemMapper) {
        this.accountMapper = Objects.requireNonNull(accountMapper);
        this.systemMapper = Objects.requireNonNull(systemMapper);
    }

    public static String getKey(@Nonnull String catalog, @Nonnull String name) {
        return CACHE.get().computeIfAbsent(new Tuple<>(catalog, name), k -> String.format(KEY_TEMPLATE, catalog, name));
    }

    /**
     * 获取账户分页大小
     *
     * @param accountId 账户ID
     * @param catalog   分类
     * @return 分页大小
     */
    public int getDefaultPageSize(@Nullable String accountId, @Nonnull String catalog) {
        Integer result = (Integer) flipPropValue(accountId, catalog, NAME_PAGE_SIZE,
                s -> Safe.toInteger(s, null));
        return Math.max(20, Objects.requireNonNullElseGet(result, () -> defaultPageSize));
    }


    /**
     * 获取属性值，将请求中的参数或个人参数，如果请求中参数有晓则更新个人参数
     * <p>
     * 1. 用{@link #getKey(String, String)}生产键从{@link HttpServletRequest#getParameter(String)}获取
     * 2. 从{@link HttpServletRequest#getParameter(String)}获取
     * 3. 从{@link AccountMapper#getAccountPropValue(String, String, String)}}获取
     * 4. 从{@link SystemMapper#getSettingPropValue(String, String)}}获取
     *
     * @param accountId 账户ID，如果没有则跳过
     * @param catalog   分类
     * @param name      参数名称
     * @param converter 结果转换器，如果没有则返回字符串
     * @return 如果不存在返回null
     */
    @Nullable
    public Object flipPropValue(@Nullable String accountId,
                                @Nonnull String catalog,
                                @Nonnull String name,
                                Function<String, Object> converter) {
        Object result = null;
        String string = CurrentRequestHelper.getParameter(getKey(catalog, name));
        if (string == null) {
            string = CurrentRequestHelper.getParameter(name);
            if (string == null) {
                if (accountId != null) {
                    string = accountMapper.getAccountPropValue(accountId, catalog, name);
                }
                if (string == null) {
                    string = systemMapper.getSettingPropValue(catalog, name);
                }
            } else {
                if (accountId != null) {
                    if (converter != null) {
                        result = converter.apply(string);
                    }
                    if (result != null) {
                        saveAccountPropValue(accountId, catalog, name, string);
                    }
                }
            }
        }
        return result == null ? string : result;
    }

    /**
     * 设置账户属性值
     *
     * @param accountId 账户ID
     * @param catalog   分类
     * @param name      参数名称
     * @param value     值
     */
    public void saveAccountPropValue(@Nonnull String accountId, @Nonnull String catalog, @Nonnull String name, @Nullable String value) {
        if (accountMapper.existsAccountPropValue(accountId, catalog, name)) {
            accountMapper.modifyAccountPropValue(accountId, catalog, name, value);
        } else {
            accountMapper.addAccountPropValue(accountId, catalog, name, value);
        }
    }

}

package pxf.tl.api;

import javax.annotation.Nonnull;
import java.util.Locale;

/**
 * 可查找的常量
 *
 * <p>通过整数和字符来查找某一个常量
 *
 * @author potatoxf
 */
public interface LiteralConstant<T extends LiteralConstant<T>> extends Literal<T> {

    /**
     * 通过别名查找
     *
     * @return 返回别名
     */
    default String[] alias() {
        return PoolOfArray.EMPTY_STRING_ARRAY;
    }

    /**
     * 是否忽略身份牌大小写
     *
     * @return {@code true}忽略，否则 {@code false}
     */
    default boolean isIgnoreNameCase() {
        return true;
    }

    /**
     * 是否忽略别名大小写
     *
     * @return {@code true}忽略，否则 {@code false}
     */
    default boolean isIgnoreAliasCase() {
        return true;
    }

    /**
     * 是否自定义字符串匹配
     *
     * @return 如果自定义匹配则返回 {@code true}，否则 {@code false}
     */
    default boolean isCustomMatchString() {
        return false;
    }

    /**
     * 是否匹配输入字符串
     *
     * @param input 输入字符串
     * @return 如果匹配则返回 {@code true}，否则 {@code false}
     */
    default boolean isMatchString(String input) {
        return false;
    }

    /**
     * 获取英文名
     *
     * @return {@code String}
     */
    @Nonnull
    default String getEnglishMessage() {
        return getMessage(Locale.US);
    }

    /**
     * 获取中文名
     *
     * @return {@code String}
     */
    @Nonnull
    default String getChineseMessage() {
        return getMessage(Locale.CHINESE);
    }

    /**
     * 获取本地化名称
     *
     * @return 名称
     */
    @Nonnull
    default String getSystemLocaleMessage() {
        return getMessage(new Locale(JavaEnvironment.USER_LANGUAGE, JavaEnvironment.USER_COUNTRY));
    }
}

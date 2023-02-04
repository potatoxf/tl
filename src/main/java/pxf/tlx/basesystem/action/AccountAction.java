package pxf.tlx.basesystem.action;

import lombok.Getter;
import pxf.tl.api.PoolOfPattern;
import pxf.tlx.basesystem.data.entity.Account;
import pxf.tlx.basesystem.data.mapper.AccountMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 账号服务
 *
 * @author potatoxf
 */
@Getter
public final class AccountAction {

    private final AccountMapper accountMapper;

    public AccountAction(@Nonnull AccountMapper accountMapper) {
        this.accountMapper = Objects.requireNonNull(accountMapper);
    }

    /**
     * 尝试获取账号
     *
     * @param input ID，用户名，邮箱，电话
     * @return {@code Account}，如果不存在返回null
     */
    @Nullable
    public Account tryGetAccount(@Nullable String input) {
        Account account = null;
        if (input != null) {
            if (PoolOfPattern.EMAIL.matcher(input).matches()) {
                account = accountMapper.getAccountByEmail(input);
            } else if (PoolOfPattern.PHONE.matcher(input).matches()) {
                account = accountMapper.getAccountInfoByPhone(input);
            } else {
                account = accountMapper.getAccount(input);
            }
        }
        return account;
    }
}

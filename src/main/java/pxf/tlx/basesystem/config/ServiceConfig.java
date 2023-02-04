package pxf.tlx.basesystem.config;

import lombok.Getter;
import lombok.Setter;
import pxf.tlx.basesystem.data.mapper.AccessMapper;
import pxf.tlx.basesystem.data.mapper.AccountMapper;
import pxf.tlx.basesystem.data.mapper.SystemMapper;
import pxf.tlx.basesystem.action.AccessAction;
import pxf.tlx.basesystem.action.AccountAction;
import pxf.tlx.basesystem.action.SystemAction;

/**
 * 基本功能配置
 *
 * @author potatoxf
 */
@Getter
@Setter
public abstract class ServiceConfig {

    /**
     * 账号服务
     *
     * @param accountMapper {@code AccountMapper}
     * @return {@code AccountAction}
     */
    public AccountAction accountService(AccountMapper accountMapper) {
        return new AccountAction(accountMapper);
    }

    /**
     * 访问服务
     *
     * @param accessMapper {@code AccessMapper}
     * @return {@code AccessAction}
     */
    public AccessAction accessService(AccessMapper accessMapper) {
        return new AccessAction(accessMapper);
    }

    /**
     * 系统服务
     *
     * @param systemMapper {@code SystemMapper}
     * @return {@code SystemAction}
     */
    public SystemAction systemService(SystemMapper systemMapper) {
        return new SystemAction(systemMapper);
    }
}

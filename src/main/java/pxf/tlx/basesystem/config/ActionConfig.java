package pxf.tlx.basesystem.config;

import pxf.tlx.basesystem.action.SettingAction;
import pxf.tlx.basesystem.data.mapper.AccountMapper;
import pxf.tlx.basesystem.data.mapper.SystemMapper;

/**
 * @author potatoxf
 */
public abstract class ActionConfig {

    public SettingAction settingAction(AccountMapper accountMapper, SystemMapper systemMapper) {
        return new SettingAction(accountMapper, systemMapper);
    }
}

package pxf.tlx.basesystem.action;

import lombok.Getter;
import pxf.tl.algs.AlgHelper;
import pxf.tlx.basesystem.data.entity.AccessMenu;
import pxf.tlx.basesystem.data.mapper.AccessMapper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

/**
 * 访问服务
 *
 * @author potatoxf
 */
@Getter
public final class AccessAction {

    private final AccessMapper accessMapper;

    public AccessAction(@Nonnull AccessMapper accessMapper) {
        this.accessMapper = Objects.requireNonNull(accessMapper);
    }

    /**
     * 获取账户菜单信息
     *
     * @param accountId 账户ID
     * @return {@code List<AccessMenu>}
     */
    public List<AccessMenu> getAccountMenuInfo(String accountId) {
        return AlgHelper.buildTreeNodeData(accessMapper.getMenuInfoMapByAccountId(accountId).values(), -1);
    }
}

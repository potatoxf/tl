package pxf.tlx.db.base.service;

import pxf.tl.algs.AlgHelper;
import pxf.tlx.db.base.entity.AccessMenu;
import pxf.tlx.db.base.mapper.AccessMapper;

import java.util.List;
import java.util.Objects;

/**
 * @author potatoxf
 */
public class AccessService {

    private final AccessMapper accessMapper;

    public AccessService(AccessMapper accessMapper) {
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

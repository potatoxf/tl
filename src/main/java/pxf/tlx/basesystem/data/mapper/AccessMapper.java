package pxf.tlx.basesystem.data.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pxf.tlx.basesystem.data.entity.AccessLink;
import pxf.tlx.basesystem.data.entity.AccessMenu;
import pxf.tlx.basesystem.data.entity.Org;
import pxf.tlx.basesystem.data.entity.OrgAccess;

import java.util.List;
import java.util.Map;

/**
 * @author potatoxf
 */
@Mapper
public interface AccessMapper {

    //------------------------------------------------------------------------------------------------------------------
    //菜单基本操作
    //------------------------------------------------------------------------------------------------------------------

    List<AccessMenu> lookupMenuInfo(@Param("condition") Map<String, Object> condition);

    @MapKey("id")
    Map<Integer, AccessMenu> getMenuInfoMapByOrgId(List<Integer> orgIds);

    @MapKey("id")
    Map<Integer, AccessMenu> getMenuInfoMapByAccountId(String accountId);

    AccessMenu getMenuInfoById(Integer id);

    AccessMenu getMenuById(Integer id);

    int removeMenu(Integer id);

    int addMenu(AccessMenu record);

    int addMenuSelective(AccessMenu record);

    int modifyMenuById(AccessMenu record);

    int modifyMenuByIdSelective(AccessMenu record);

    //------------------------------------------------------------------------------------------------------------------
    //链接基本操作
    //------------------------------------------------------------------------------------------------------------------

    List<AccessLink> lookupLink(@Param("condition") Map<String, Object> condition);

    AccessLink getLinkById(Integer id);

    int removeLink(Integer id);

    int addLink(AccessLink record);

    int addLinkSelective(AccessLink record);

    int modifyLinkById(AccessLink record);

    int modifyLinkByIdSelective(AccessLink record);

    //------------------------------------------------------------------------------------------------------------------
    //组织基本操作
    //------------------------------------------------------------------------------------------------------------------

    @MapKey("id")
    Map<Integer, Org> getOrgMap();

    List<Org> getOrgList();

    List<Org> getOrgChildrenList(Integer pid);

    List<Org> getOrgListByAccountId(String accountId);

    Org getOrgById(Integer id);

    int removeOrgById(Integer id);

    int addOrg(Org record);

    int addOrgSelective(Org record);

    int modifyOrgById(Org record);

    int modifyOrgByIdSelective(Org record);

    //------------------------------------------------------------------------------------------------------------------
    //组织访问权限基本操作
    //------------------------------------------------------------------------------------------------------------------

    int removeOrgAccessByOrgId(@Param("orgId") Integer orgId);

    int removeOrgAccessByMenuId(@Param("menuId") Integer menuId);

    int removeOrgAccess(@Param("orgId") Integer orgId, @Param("menuId") Integer menuId);

    int addOrgAccess(OrgAccess record);
}
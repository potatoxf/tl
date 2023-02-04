package pxf.tlx.basesystem.data.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pxf.tlx.basesystem.data.entity.Account;
import pxf.tlx.basesystem.data.entity.AccountConLog;
import pxf.tlx.basesystem.data.entity.AccountProp;
import pxf.tlx.basesystem.data.entity.AccountPwdHis;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author potatoxf
 */
@Mapper
public interface AccountMapper {

    /**
     * 查找账户数据
     *
     * @param columnList 列列表
     * @param condition  条件
     * @return {@code Account}
     */
    List<Map<String, Object>> lookupAccountData(@Param("columnList") List<String> columnList, @Param("condition") Map<String, Object> condition);

    /**
     * 查找账户
     *
     * @param condition 条件
     * @return {@code Account}
     */
    List<Account> lookupAccount(@Param("condition") Map<String, Object> condition);

    /**
     * 计算邮件数量
     *
     * @param email 邮件
     * @return 邮件数量
     */
    int countEmail(String email);

    /**
     * 计算手机数量
     *
     * @param phone 手机
     * @return 手机数量
     */
    int countPhone(String phone);

    /**
     * 获取账户上级，不存在返回null
     *
     * @param idOrToken 账户ID
     * @param levels    上级级数别名数字
     * @return {@code Account}
     */
    @Nullable
    Account getUpperAccount(@Param("idOrToken") String idOrToken, @Param("levels") int... levels);

    /**
     * 获取账户
     *
     * @param idOrToken 账户ID
     * @return {@code Account}
     */
    @Nullable
    Account getAccount(String idOrToken);

    /**
     * 获取账户
     *
     * @param idOrToken 账户ID
     * @return {@code Account}
     */
    @Nullable
    Account getAccountInfo(String idOrToken);

    /**
     * 获取账户
     *
     * @param email 邮箱
     * @return {@code Account}
     */
    @Nullable
    Account getAccountByEmail(String email);

    /**
     * 获取账户
     *
     * @param email 账户ID
     * @return {@code Account}
     */
    @Nullable
    Account getAccountInfoByEmail(String email);

    /**
     * 获取账户
     *
     * @param phone 手机
     * @return {@code Account}
     */
    @Nullable
    Account getAccountByPhone(String phone);

    /**
     * 获取账户
     *
     * @param phone 手机
     * @return {@code Account}
     */
    @Nullable
    Account getAccountInfoByPhone(String phone);

    /**
     * 添加账户
     *
     * @param account 账户
     */
    void addAccount(Account account);

    /**
     * 添加账户
     *
     * @param account 账户
     */
    int addAccountSelective(Account account);

    /**
     * 添加账户
     *
     * @param accounts 账户
     */
    int addAccountBatched(List<Account> accounts);

    /**
     * 修改账户
     *
     * @param account 账户
     */
    void modifyAccountById(Account account);

    /**
     * 修改账户
     *
     * @param account 账户
     */
    void modifyAccountByIdSelective(Account account);

    /**
     * 修改账户
     *
     * @param accounts 账户
     */
    int modifyAccountBatched(List<Account> accounts);

    /**
     * 删除账户
     *
     * @param id 账户ID
     */
    void removeAccount(String id);

    /**
     * 阿斯顿
     *
     * @param id        账户ID
     * @param condition 条件
     * @return {@code Account}
     */
    List<AccountConLog> listAccountConLog(@Param("id") String id, @Param("condition") Map<String, Object> condition);

    /**
     * 添加账户登录记录
     *
     * @param accountConLog 账户登录日志
     */
    void addAccountConLog(AccountConLog accountConLog);

    /**
     * 获取账户密码历史记录次数
     *
     * @param id            账户ID
     * @param password      密码
     * @param fromTimestamp 从时间搓
     * @return 返回一段时间内的密码出现次数
     */
    int getAccountPasswordHistoryCount(@Param("id") String id, @Param("password") String password, @Param("fromTimestamp") Long fromTimestamp);

    /**
     * 添加账户历史密码
     *
     * @param accountPwdHis 账户密码历史
     */
    void addAccountPasswordHistory(AccountPwdHis accountPwdHis);

    /**
     * 添加账户某个属性
     *
     * @param accountProp 账户属性
     */
    void addAccountProp(AccountProp accountProp);

    /**
     * 修改账户某个属性
     *
     * @param accountProp 账户属性
     */
    void modifyAccountProp(AccountProp accountProp);

    /**
     * 是否存在账户属性
     *
     * @param id      id号码
     * @param catalog 分类
     * @param name    名称
     */
    boolean existsAccountPropValue(@Param("id") String id, @Param("catalog") String catalog, @Param("name") String name);

    /**
     * 获取账户属性
     *
     * @param id      id号码
     * @param catalog 分类
     * @param name    名称
     */
    String getAccountPropValue(@Param("id") String id, @Param("catalog") String catalog, @Param("name") String name);

    /**
     * 添加账户属性
     *
     * @param id      id号码
     * @param catalog 分类
     * @param name    名称
     */
    void addAccountPropValue(@Param("id") String id, @Param("catalog") String catalog, @Param("name") String name, @Param("value") String value);

    /**
     * 修改账户属性
     *
     * @param id      id号码
     * @param catalog 分类
     * @param name    名称
     */
    void modifyAccountPropValue(@Param("id") String id, @Param("catalog") String catalog, @Param("name") String name, @Param("value") String value);

    /**
     * 删除账户属性
     *
     * @param id      id号码
     * @param catalog 分类
     * @param name    名称
     */
    void removeAccountPropValue(@Param("id") String id, @Param("catalog") String catalog, @Param("name") String name);

    /**
     * 删除账户属性
     *
     * @param id id号码
     */
    void clearAccountProp(@Param("id") String id);
}

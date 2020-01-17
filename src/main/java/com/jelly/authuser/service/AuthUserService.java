package com.jelly.authuser.service;

import com.jelly.authuser.entity.AuthUser;
import com.jelly.authuser.entity.AuthUserInfo;
import com.jelly.authuser.entity.bo.Account;

import java.util.List;

/**
 * @author guodongzhang
 */
public interface AuthUserService {

    /**
     * 根据用户id判断用户信息是否存在
     *
     * @param userId 用户id
     * @return 是否存在
     */
    boolean existsUserInfoByUserId(Long userId);

    /**
     * 增加一个登录用户
     *
     * @param username 用户名
     * @param password 密码
     * @param phone    手机号
     * @param mail     邮箱
     * @param groupId  用户组ID
     * @return 是否成功
     */
    boolean createUser(String username, String password, String phone, String mail, Long groupId);

    /**
     * 更新用户信息
     *
     * @param id       用户唯一ID
     * @param username 用户名
     * @param password 密码
     * @param phone    手机号
     * @param mail     邮箱
     * @param groupId  用户组ID
     * @return 受影响行数
     */
    boolean updateUserById(Long id, String username, String password, String salt, String phone, String mail, Long groupId);

    /**
     * 新增一个用户信息
     *
     * @param userId    用户唯一ID
     * @param realName  用户真实姓名
     * @param sex       用户性别
     * @param birth     用户生日
     * @param avatarUrl 用户头像地址
     * @return 是否成功
     */
    boolean createUserInfo(Long userId, String realName, Integer sex, Long birth, String avatarUrl);

    /**
     * 更新用户详细信息
     *
     * @param userId    用户唯一ID
     * @param realName  用户真实姓名
     * @param sex       用户性别
     * @param birth     用户生日
     * @param avatarUrl 用户头像地址
     * @return 受影响行数
     */
    boolean updateUserInfoByUserId(Long userId, String realName, Integer sex, Long birth, String avatarUrl);

    /**
     * //TODO replace 根据UK查询用户
     *
     * @param appId 用户名/邮箱/手机号  中的一个
     * @return 用户信息
     */
    Account getAccountByUniqueKey(String appId);

    /**
     * 根据AppId查询用户Id
     *
     * @param appId 用户名/邮箱/手机号  中的一个
     * @return 用户信息
     */
    Long getUserIdByAppId(String appId);

    /**
     * 根据 userId 查询用户
     *
     * @param userId 用户名唯一ID
     * @return 用户信息
     */
    AuthUser getUserByPrimaryKey(Long userId);

    /**
     * 根据 userId 查询用户详细信息
     *
     * @param userId 用户名唯一ID
     * @return 用户信息
     */
    AuthUserInfo getUserInfoByUserId(Long userId);

    /**
     * 根据手机号查询用户
     *
     * @param phone 用户 手机号
     * @return 用户信息
     */
    AuthUser getUserByPhone(String phone);

    /**
     * 根据用户名称查询用户
     *
     * @param username 用户名称
     * @return 用户信息
     */
    AuthUser getUserByUsername(String username);

    /**
     * 获取全部登录用户列表
     *
     * @param start 分页 起始页
     * @param size  分页 每页数据量
     * @return 用户列表
     */
    List<AuthUser> listAll(Integer start, Integer size);

    /**
     * 根据角色ID查询关联的用户列表
     *
     * @param roleId 角色ID
     * @param start  分页 起始页
     * @param size   分页 每页数据量
     * @return 用户列表
     */
    List<AuthUser> listByRoleId(Integer roleId, Integer start, Integer size);

    /**
     * 根据用户组ID查询关联的用户列表
     *
     * @param groupId 角色ID
     * @param start   分页 起始页
     * @param size    分页 每页数据量
     * @return 用户列表
     */
    List<AuthUser> listByGroupId(Integer groupId, Integer start, Integer size);

    /**
     * 增加用户角色
     *
     * @param uid    用户uid
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean createUserRole(Long uid, Integer roleId);

    /**
     * 删除用户角色
     *
     * @param uid    用户uid
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean deleteUserRole(Long uid, Integer roleId);

    /**
     * 根据UK查询用户角色
     *
     * @param appId UK
     * @return 角色列表;分割的String
     */
    String getRolesByAppId(String appId);

    /**
     * 根据用户ID查询角色列表
     *
     * @param appId 用户uid
     * @return 角色列表
     */
    List<String> listRolesByAppId(String appId);

    /**
     * 派发登陆token
     *
     * @param appIdUserId 用户登录凭证，appId。redis唯一Key
     * @return 登陆token
     */
    String issueLoginToken(Object[] appIdUserId);

    /**
     * 根据username/phone/mail获取 appId，userId
     *
     * @param username 用户名
     * @param phone    手机号
     * @param mail     邮箱
     * @return [appId，userId]
     */
    Object[] getAppIdUserId(String username, String phone, String mail);
}

package com.jelly.authuser.dao;

import com.jelly.authuser.entity.AuthUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author guodongzhang
 */
public interface AuthUserDao {
    /**
     * 增加一个用户
     *
     * @param user 增加的用户信息
     * @return 影响行数
     */
    int insert(AuthUser user);

    /**
     * 根据Id 修改用户信息
     *
     * @param user 需要修改的用户信息
     * @return 受影响行数
     */
    int updateByPrimaryKey(AuthUser user);

    /**
     * 根据ID 查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    AuthUser selectByPrimaryKey(Long id);

    /**
     * 根据用户名称号查找用户
     *
     * @param username 用户名称
     * @return 用户信息
     */
    AuthUser selectByUsername(String username);

    /**
     * 根据用户手机号查找用户
     *
     * @param phone 用户手机号
     * @return 用户信息
     */
    AuthUser selectByPhone(String phone);

    /**
     * 根据角色ID查询与之关联的用户列表
     *
     * @param roleId 角色ID
     * @param start  分页 起始页
     * @param size   分页 每页数据量
     * @return 用户列表
     */
    List<AuthUser> selectByRoleId(@Param("roleId") Integer roleId, @Param("start") Integer start, @Param("size") Integer size);

    /**
     * 根据用户组ID查询与之关联的用户列表
     *
     * @param groupId 用户组ID
     * @param start   分页 起始页
     * @param size    分页 每页数据量
     * @return 用户列表
     */
    List<AuthUser> selectByGroupId(@Param("groupId") Integer groupId, @Param("start") Integer start, @Param("size") Integer size);

    /**
     * 根据appId（用户名/邮箱/手机号  中的一个）查询用户
     *
     * @param username 用户名
     * @param phone    手机号
     * @param mail     邮箱
     * @return 用户ID
     */
    Long selectUserIdByAppId(@Param("username") String username, @Param("phone") String phone, @Param("mail") String mail);

    /**
     * 根据 userId 查询所有的角色
     *
     * @param userId userId
     * @return 角色列表
     */
    List<String> selectUserRolesByUserId(Long userId);

    /**
     * 分页查询所有用户
     *
     * @param start 分页 起始页
     * @param size  分页 每页数据量
     * @return 用户列表
     */
    List<AuthUser> selectAll(@Param("start") Integer start, @Param("size") Integer size);
}

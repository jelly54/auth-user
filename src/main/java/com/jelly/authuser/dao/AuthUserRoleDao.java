package com.jelly.authuser.dao;

import com.jelly.authuser.entity.AuthUserRole;

/**
 * @author guodongzhang
 */
public interface AuthUserRoleDao {

    /**
     * 增加一个用户角色
     *
     * @param userRole 用户角色
     * @return 受影响的行数
     */
    int insert(AuthUserRole userRole);

    /**
     * 根据UK删除用户角色
     *
     * @param userRole 包含uid-roleId
     * @return 受影响行数
     */
    int deleteByUniqueKey(AuthUserRole userRole);
}
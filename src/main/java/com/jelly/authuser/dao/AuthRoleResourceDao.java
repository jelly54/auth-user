package com.jelly.authuser.dao;

import com.jelly.authuser.entity.AuthRoleResource;

/**
 * @author guodongzhang
 */
public interface AuthRoleResourceDao {
    /**
     * 新增角色资源记录
     *
     * @param roleResource 角色资源记录
     * @return 受影响行数
     */
    int insert(AuthRoleResource roleResource);

    /**
     * 根据UK删除角色资源记录
     *
     * @param roleResource 资源
     * @return 受影响行数
     */
    int deleteByUniqueKey(AuthRoleResource roleResource);

}
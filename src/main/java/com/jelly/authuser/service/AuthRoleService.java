package com.jelly.authuser.service;

import com.jelly.authuser.entity.AuthRole;

import java.util.List;

/**
 * @author guodongzhang
 */
public interface AuthRoleService {

    /**
     * 创建角色
     *
     * @param roleName 需要创建的角色名称
     * @return 是否成功
     */
    boolean createRole(String roleName);

    /**
     * 根据角色名称更新角色
     *
     * @param roleName 角色名称
     * @param status   状态
     * @return 是否成功
     */
    boolean updateByRoleName(String roleName, Integer status);

    /**
     * 根据角色名称删除角色信息
     *
     * @param roleName 角色名称
     * @return 是否成功
     */
    boolean deleteByRoleName(String roleName);

    /**
     * 根据名称查询角色信息
     *
     * @param roleName 角色名称
     * @return 角色信息
     */
    AuthRole getRoleByName(String roleName);

    /**
     * 列出所有角色
     *
     * @param start 分页 起始页
     * @param size  分页 每页数据量
     * @return 角色列表
     */
    List<AuthRole> listRoles(int start, int size);

    /**
     * 创建 为角色授权资源
     *
     * @param roleId     角色ID
     * @param resourceId 资源ID
     * @return 是否成功
     */
    boolean createAuthRoleResource(Integer roleId, Integer resourceId);

    /**
     * 删除角色授权的资源
     *
     * @param roleId     角色ID
     * @param resourceId 资源ID
     * @return 是否成功
     */
    boolean deleteAuthRoleResource(Integer roleId, Integer resourceId);

}

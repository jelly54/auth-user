package com.jelly.authuser.dao;

import com.jelly.authuser.entity.AuthResource;
import com.jelly.authuser.shiro.rule.UrlRolesRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author guodongzhang
 */
public interface AuthResourceDao {
    /**
     * 新增资源信息
     *
     * @param resource 资源信息
     * @return 受影响行数
     */
    int insert(AuthResource resource);

    /**
     * 根据主键删除资源信息
     *
     * @param id 资源主键id
     * @return 受影响行数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 根据主键更新资源信息
     *
     * @param resource 需要更新的资源信息含id
     * @return 受影响行数
     */
    int updateByPrimaryKey(AuthResource resource);

    /**
     * 查询url-role api规则
     * resource_type = 2
     * resource_status = 0
     * role_status = 0
     *
     * @return url-needRoles 列表
     */
    List<UrlRolesRule> selectUrlRolesRules();

    /**
     * 根据 资源主键ID查询资源详情
     *
     * @param id api ID
     * @return 资源详情
     */
    AuthResource selectByPrimaryKey(Integer id);

    /**
     * 根据 资源资源名称查询资源详情
     *
     * @param resourceName 资源名称
     * @return 资源详情
     */
    AuthResource selectByResourceName(String resourceName);

    /**
     * 根据parentId获取Api列表
     *
     * @param parentId root节点为-1
     * @param resType  约定 1 资源父级节点； 2 rest-api； 3 module(前端组件)；
     * @param start    分页 起始页
     * @param size     分页 每页数据量
     * @return api资源详情列表
     */
    List<AuthResource> selectByParentId(@Param("parentId") Integer parentId, @Param("resType") Integer resType, @Param("start") Integer start, @Param("size") Integer size);

    /**
     * 根据角色ID查询与之关联的资源列表
     *
     * @param roleId 角色ID
     * @param start  分页 起始页
     * @param size   分页 每页数据量
     * @return 资源列表
     */
    List<AuthResource> selectByRoleId(@Param("roleId") Integer roleId, @Param("start") Integer start, @Param("size") Integer size);
}
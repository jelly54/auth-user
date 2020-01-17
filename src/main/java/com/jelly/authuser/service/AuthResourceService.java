package com.jelly.authuser.service;

import com.jelly.authuser.entity.AuthResource;
import com.jelly.authuser.shiro.rule.UrlRolesRule;

import java.util.List;

/**
 * @author guodongzhang
 */
public interface AuthResourceService {

    /**
     * 创建资源
     *
     * @param resName  资源名称
     * @param resType  资源类型
     * @param parentId 资源父ID
     * @param url      资源地址
     * @param method   访问方法
     * @return 受影响行数
     */
    boolean createResource(String resName, Integer resType, Integer parentId, String url, String method);

    /**
     * 根据资源ID删除资源
     *
     * @param id 资源ID
     * @return 是否成功
     */
    boolean deleteByResourceId(Integer id);

    /**
     * 根据资源ID更新资源信息
     *
     * @param id       资源ID
     * @param resName  资源名称
     * @param resType  资源类型
     * @param parentId 资源父ID
     * @param url      资源地址
     * @param method   访问方法
     * @return 是否成功
     */
    boolean updateByResourceId(Integer id, String resName, Integer resType, Integer parentId, String url, String method);

    /**
     * 根据ID获取资源信息
     *
     * @param id 资源ID
     * @return 资源信息
     */
    AuthResource getByResourceId(Integer id);

    /**
     * 根据名称获取资源信息
     *
     * @param resourceName 资源名称
     * @return 资源信息
     */
    AuthResource getByResourceName(String resourceName);

    /**
     * 根据parentId获取api资源 列表
     *
     * @param parentId 父id
     * @param resType  资源类型
     * @param start    分页 起始页
     * @param size     分页 每页数据量
     * @return 资源列表
     */
    List<AuthResource> listByParentId(Integer parentId, Integer resType, Integer start, Integer size);

    /**
     * 根据parentId获取api资源 列表
     *
     * @param roleId 角色ID
     * @param start  分页 起始页
     * @param size   分页 每页数据量
     * @return
     */
    List<AuthResource> listByRoleId(Integer roleId, Integer start, Integer size);

    /**
     * 加载基于角色/资源的过滤规则
     * 即：用户-角色-资源（URL），对应关系存储与数据库中
     * 在shiro中生成的过滤器链为：url=jwt[角色1、角色2、角色n]
     *
     * @return url-role 规则列表
     */
    List<UrlRolesRule> loadUrlRolesRules();
}

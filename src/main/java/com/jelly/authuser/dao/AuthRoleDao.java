package com.jelly.authuser.dao;

import com.jelly.authuser.entity.AuthRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author guodongzhang
 */
public interface AuthRoleDao {
    /**
     * 增加一个角色 目前仅支持 角色名添加
     *
     * @param record 角色信息
     * @return 受影响行数
     */
    int insert(AuthRole record);

    /**
     * 根据角色名称删除角色信息
     *
     * @param roleName 角色名称
     * @return 受影响行数
     */
    int deleteByRoleName(String roleName);

    /**
     * 根据roleName 修改状态
     *
     * @param roleName 角色名称
     * @param status   状态
     * @return 受影响行数
     */
    int updateByRoleName(@Param("roleName") String roleName, @Param("status") Integer status);

    /**
     * 根据名称获取一个角色
     *
     * @param roleName 角色名称
     * @return 角色信息
     */
    AuthRole selectByName(String roleName);

    /**
     * 查询全部角色
     *
     * @param start 分页 起始页
     * @param size  分页 每页数据量
     * @return 角色列表
     */
    List<AuthRole> selectAll(@Param("start") Integer start, @Param("size") Integer size);
}
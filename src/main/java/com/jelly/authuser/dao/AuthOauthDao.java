package com.jelly.authuser.dao;

import com.jelly.authuser.entity.AuthOauth;

import java.util.List;

/**
 * @author guodongzhang
 */
public interface AuthOauthDao {

    /**
     * 新增授权信息
     *
     * @param oauth 授权信息
     * @return 是否新增成功
     */
    int insert(AuthOauth oauth);

    /**
     * 根据主键信息删除授权信息
     *
     * @param id 主键ID
     * @return 受影响行数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 根据userId更新授权表
     *
     * @param oauth 授权信息
     * @return 是否更新成功
     */
    int updateByUserId(AuthOauth oauth);

    /**
     * 根据用户ID查询授权信息
     *
     * @param userId 用户ID
     * @return 授权信息
     */
    AuthOauth selectByUserId(Integer userId);

    /**
     * 根据userId查看授权信息
     *
     * @param userId 用户ID
     * @return 符合条件的行数
     */
    int countByUserId(Long userId);

    /**
     * 查询所有的授权列表
     *
     * @return 授权信息
     */
    List<AuthOauth> selectAll();

}
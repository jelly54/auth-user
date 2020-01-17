package com.jelly.authuser.dao;

import com.jelly.authuser.entity.AuthUserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author guodongzhang
 */
public interface AuthUserInfoDao {

    /**
     * 增加用户信息
     *
     * @param userInfo 用户信息
     * @return 受影响行数
     */
    int insert(AuthUserInfo userInfo);

    /**
     * 根据userId更新用户信息
     *
     * @param userInfo 用户信息
     * @return 受影响行数
     */
    int updateByUserId(AuthUserInfo userInfo);

    /**
     * 根据userId 查询用户信息
     *
     * @param id 用户id
     * @return 用户信息
     */
    AuthUserInfo selectByUserId(Long id);

    /**
     * 根据userId查看用户信息
     *
     * @param userId 用户ID
     * @return 符合条件的行数
     */
    int countByUserId(Long userId);

    /**
     * 分页查询所有用户信息
     *
     * @param start 分页 起始页
     * @param size  分页 每页数据量
     * @return 用户信息列表
     */
    List<AuthUserInfo> selectAll(@Param("start") Integer start, @Param("size") Integer size);
}
package com.jelly.authuser.service;

import com.jelly.authuser.entity.AuthOauth;

/**
 * @author guodongzhang
 */
public interface AuthOauthService {

    /**
     * 根据用户id判断授权信息是否存在
     *
     * @param userId 用户id
     * @return 是否存在
     */
    boolean existsOauthByUserId(Long userId);

    /**
     * 增加一个授权信息
     *
     * @param userId     第三方授权信息
     * @param oauthType  第三方授权信息
     * @param credential 第三方授权信息
     * @return 是否成功
     */
    boolean createOauthInfo(Long userId, String oauthType, String credential);

    /**
     * 根据userId更新授权信息
     *
     * @param oauth 第三方授权信息
     * @return 是否成功
     */
    boolean updateOauthInfo(AuthOauth oauth);

}

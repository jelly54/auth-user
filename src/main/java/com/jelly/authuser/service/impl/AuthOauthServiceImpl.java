package com.jelly.authuser.service.impl;

import com.jelly.authuser.dao.AuthOauthDao;
import com.jelly.authuser.entity.AuthOauth;
import com.jelly.authuser.service.AuthOauthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author guodongzhang
 */
@Service
public class AuthOauthServiceImpl implements AuthOauthService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthOauthServiceImpl.class);

    @Resource
    AuthOauthDao oauthDao;

    @Override
    public boolean existsOauthByUserId(Long userId) {
        int count = oauthDao.countByUserId(userId);
        LOG.info("Count oauth by userId. {}.", count);

        return count > 0;
    }

    @Override
    public boolean createOauthInfo(Long userId, String oauthType, String credential) {
        AuthOauth oauth = new AuthOauth();
        int insert = 0;
        if (userId != null) {
            oauth.setUserId(userId);
            oauth.setOauthType(oauthType);
            oauth.setCredential(credential);
            insert = oauthDao.insert(oauth);
        }

        return insert > 0;
    }

    @Override
    public boolean updateOauthInfo(AuthOauth oauth) {
        int update = oauthDao.updateByUserId(oauth);

        return update > 0;
    }
}

package com.jelly.authuser.service.impl;

import com.jelly.authuser.dao.AuthResourceDao;
import com.jelly.authuser.entity.AuthResource;
import com.jelly.authuser.service.AuthResourceService;
import com.jelly.authuser.shiro.rule.UrlRolesRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author guodongzhang
 */
@Service
public class AuthResourceServiceImpl implements AuthResourceService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthResourceServiceImpl.class);

    @javax.annotation.Resource
    AuthResourceDao resourceDao;

    @Override
    public boolean createResource(String resName, Integer resType, Integer parentId, String url, String method) {
        AuthResource res = new AuthResource();
        int insert = 0;
        if (!StringUtils.isEmpty(resName)) {
            res.setResName(resName);
            res.setResType(resType);
            res.setParentId(parentId);
            res.setUrl(url);
            res.setMethod(method);
            insert = resourceDao.insert(res);
        }

        LOG.info("Create resource : {}.", insert > 0);
        return insert > 0;
    }

    @Override
    public boolean deleteByResourceId(Integer id) {
        int delete = resourceDao.deleteByPrimaryKey(id);
        LOG.info("Delete resource : {}. resourceId:{}.", delete > 0, id);
        return delete > 0;
    }

    @Override
    public boolean updateByResourceId(Integer id, String resName, Integer resType, Integer parentId, String url, String method) {
        AuthResource resource = new AuthResource(id, resName, resType, parentId, url, method);
        int update = resourceDao.updateByPrimaryKey(resource);
        LOG.info("Update resource : {}.", update > 0);
        return update > 0;
    }

    @Override
    public AuthResource getByResourceId(Integer id) {
        AuthResource resource = resourceDao.selectByPrimaryKey(id);
        LOG.info("Select resource by resourceId. resourceId: {}. resource: {}.", id, resource);
        if (null == resource) {
            return null;
        }

        return resource;
    }

    @Override
    public AuthResource getByResourceName(String resourceName) {
        AuthResource resource = resourceDao.selectByResourceName(resourceName);
        LOG.info("Select resource by resourceName: {}. resource: {}.", resourceName, resource);
        if (null == resource) {
            return null;
        }

        return resource;
    }

    @Override
    public List<AuthResource> listByParentId(Integer parentId, Integer resType, Integer start, Integer size) {
        List<AuthResource> resources = resourceDao.selectByParentId(parentId, resType, start, size);
        LOG.info("List resource by parentId:{}, resType:{}, start:{}, size:{}.", parentId, resType, start, size);

        return resources;
    }

    @Override
    public List<AuthResource> listByRoleId(Integer roleId, Integer start, Integer size) {
        List<AuthResource> resources = resourceDao.selectByRoleId(roleId, start, size);
        LOG.info("List resource by roleId.roleId:{}. start:{}. size:{}. resources:{}.", roleId, start, size, resources);

        return resources;
    }

    @Override
    public List<UrlRolesRule> loadUrlRolesRules() {
        List<UrlRolesRule> rolesRules = resourceDao.selectUrlRolesRules();
        LOG.info("List url-roles rule. {}.", rolesRules);
        return rolesRules;
    }

}

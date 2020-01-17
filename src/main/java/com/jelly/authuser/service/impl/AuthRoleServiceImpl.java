package com.jelly.authuser.service.impl;

import com.jelly.authuser.dao.AuthRoleDao;
import com.jelly.authuser.dao.AuthRoleResourceDao;
import com.jelly.authuser.entity.AuthRole;
import com.jelly.authuser.entity.AuthRoleResource;
import com.jelly.authuser.service.AuthRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author guodongzhang
 */
@Service
public class AuthRoleServiceImpl implements AuthRoleService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthRoleServiceImpl.class);

    @Resource
    AuthRoleDao roleDao;

    @Resource
    AuthRoleResourceDao roleResourceDao;

    @Override
    public boolean createRole(String roleName) {
        int insert = roleDao.insert(new AuthRole(roleName));
        LOG.info("Create role : {}. roleName: {}.", insert > 0, roleName);
        return insert > 0;
    }

    @Override
    public boolean updateByRoleName(String roleName, Integer status) {
        int update = roleDao.updateByRoleName(roleName, status);
        LOG.info("Update role : {}.", update > 0);
        return update > 0;
    }

    @Override
    public boolean deleteByRoleName(String roleName) {
        int delete = roleDao.deleteByRoleName(roleName);
        LOG.info("Delete role : {}.", delete > 0);
        return delete > 0;
    }

    @Override
    public AuthRole getRoleByName(String roleName) {
        AuthRole role = roleDao.selectByName(roleName);
        LOG.info("Select role by roleName:{}. {}", roleName, role.toString());

        return role;
    }

    @Override
    public List<AuthRole> listRoles(int start, int size) {
        List<AuthRole> roles = roleDao.selectAll(start, size);
        LOG.info("List roles start: {}. size:{}.  roles: {}.", start, size, roles);

        return roles;
    }

    @Override
    public boolean createAuthRoleResource(Integer roleId, Integer resourceId) {
        int insert = roleResourceDao.insert(new AuthRoleResource(roleId, resourceId));
        LOG.info("Create auth role-resource : {}. roleId:{}. resourceId:{}.", insert > 0, roleId, resourceId);
        return insert > 0;
    }

    @Override
    public boolean deleteAuthRoleResource(Integer roleId, Integer resourceId) {
        int delete = roleResourceDao.deleteByUniqueKey(new AuthRoleResource(roleId, resourceId));
        LOG.info("Delete auth role-resource : {}. roleId:{}. resourceId:{}.", delete > 0, roleId, resourceId);
        return delete > 0;
    }

}

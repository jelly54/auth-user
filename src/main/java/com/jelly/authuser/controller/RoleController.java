package com.jelly.authuser.controller;

import com.jelly.authuser.entity.AuthResource;
import com.jelly.authuser.entity.AuthRole;
import com.jelly.authuser.entity.AuthUser;
import com.jelly.authuser.entity.vo.UserVO;
import com.jelly.authuser.exception.ErrorEnum;
import com.jelly.authuser.exception.ServerException;
import com.jelly.authuser.service.AuthResourceService;
import com.jelly.authuser.service.AuthRoleService;
import com.jelly.authuser.service.AuthUserService;
import com.jelly.authuser.shiro.filter.ShiroFilterChainManager;
import com.jelly.authuser.util.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

/**
 * @author guodongzhang
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    private static final Logger LOG = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private AuthRoleService roleService;

    @Autowired
    private AuthUserService userService;

    @Autowired
    private AuthResourceService resourceService;

    @Autowired
    private ShiroFilterChainManager shiroFilterChainManager;


    /**
     * 添加角色
     */
    @PostMapping("")
    public CommonResponse createRole(@RequestParam(value = "role_name") String roleName) {
        String rolePrefix = "role_";
        if (StringUtils.isEmpty(roleName) || !roleName.startsWith(rolePrefix)) {
            throw new ServerException(ErrorEnum.INVALID_PARAMS, "roleName must startWith 'role_'");
        }

        LOG.info("Role will be created. roleName:{}.", roleName);
        boolean flag = roleService.createRole(roleName);

        if (!flag) {
            LOG.warn("Role create failed. roleName:{}.", roleName);
            return CommonResponse.error(ErrorEnum.INTERNAL_EXCEPT, "Create role fail!").build();
        }

        AuthRole role = roleService.getRoleByName(roleName);

        LOG.info("Role create successfully. roleName:{}.", roleName);
        return CommonResponse.ok().data(role).build();
    }

    /**
     * 根据roleName 更新角色
     */
    @PatchMapping("")
    public CommonResponse updateRole(@RequestParam(value = "role_name") String roleName,
                                     @RequestParam(value = "status") Integer status) {
        LOG.info("Role will be updated. role:{}. status:{}.", roleName, status);
        boolean flag = roleService.updateByRoleName(roleName, status);

        if (!flag) {
            LOG.warn("Role update failed. roleName:{}.", roleName);
            return CommonResponse.error(ErrorEnum.INTERNAL_EXCEPT, "Update failed!").build();
        }

        AuthRole role = roleService.getRoleByName(roleName);
        LOG.info("Role update successfully. roleName:{}, status:{}.", roleName, status);

        return CommonResponse.ok().data(role).build();
    }

    /**
     * 根据角色ID删除角色
     */
    @DeleteMapping("/{role_name}")
    public CommonResponse deleteRoleByRoleId(@PathVariable(value = "role_name") String roleName) {
        LOG.info("Role will be deleted. roleName:{}.", roleName);
        boolean flag = roleService.deleteByRoleName(roleName);

        if (!flag) {
            LOG.warn("Role delete failed. roleName:{}.", roleName);
            return CommonResponse.error(ErrorEnum.INTERNAL_EXCEPT, "Delete fail!").build();
        }
        LOG.info("Role delete successfully. roleName:{}.", roleName);
        return CommonResponse.ok().data("delete", true).build();
    }

    /**
     * 获取角色LIST
     */
    @GetMapping("/list/{start}/{size}")
    public CommonResponse listRoleList(@PathVariable(value = "start") Integer start,
                                       @PathVariable(value = "size") Integer size) {
        LOG.info("List roles start:{}. size:{}.", start, size);
        List<AuthRole> roles = roleService.listRoles(start, size);

        return CommonResponse.ok().data(roles).build();
    }

//======================================================================================================================

    /**
     * 授权资源给角色
     */
    @PostMapping("/auth/resource")
    public CommonResponse authorityRoleResource(@RequestParam(value = "role_id") Integer roleId,
                                                @RequestParam(value = "resource_id") Integer resourceId) {
        if (roleId == null || resourceId == null) {
            LOG.warn("param can not be null. roleId:{}, resourceId:{}.", roleId, resourceId);
            throw new ServerException(ErrorEnum.INVALID_PARAMS);
        }

        LOG.info("Authority role-resource. roleId:{}. resourceId:{}.", roleId, resourceId);
        boolean flag = roleService.createAuthRoleResource(roleId, resourceId);
        if (!flag) {
            LOG.warn("Role auth resource failed. role_id:{}, resource_id:{}.", roleId, resourceId);
            return CommonResponse.error(ErrorEnum.INTERNAL_EXCEPT, "authority error").build();
        }

        shiroFilterChainManager.reloadFilterChain();

        LOG.info("Authority role-resource successfully. roleId:{}. resourceId:{}.", roleId, resourceId);
        return CommonResponse.ok().data("auth", true).build();
    }

    /**
     * 删除对应的角色的授权资源
     */
    @DeleteMapping("/auth/{role_id}/resource/{resource_id}")
    public CommonResponse deleteAuthRoleResource(@PathVariable(value = "role_id") Integer roleId,
                                                 @PathVariable(value = "resource_id") Integer resourceId) {
        if (roleId == null || resourceId == null) {
            throw new ServerException(ErrorEnum.INVALID_PARAMS);
        }

        LOG.info("Delete role-resource. roleId:{}. resourceId:{}.", roleId, resourceId);
        boolean flag = roleService.deleteAuthRoleResource(roleId, resourceId);
        if (!flag) {
            LOG.warn("Delete role-resource failed. role_id:{}, resource_id:{}.", roleId, resourceId);
            return CommonResponse.error(ErrorEnum.INTERNAL_EXCEPT, "authority error").build();
        }

        shiroFilterChainManager.reloadFilterChain();

        LOG.warn("Delete role-resource successfully. role_id:{}, resource_id:{}.", roleId, resourceId);
        return CommonResponse.ok().data("delete", true).build();
    }

//======================================================================================================================

    /**
     * 获取角色关联的(roleId)对应用户列表
     */
    @GetMapping("/{role_id}/user/{start}/{size}")
    public CommonResponse listUserListByRoleId(@PathVariable(value = "role_id") Integer roleId,
                                               @PathVariable(value = "start") Integer start,
                                               @PathVariable(value = "size") Integer size) {

        LOG.info("Get users by roleId. roleId:{}. start:{}. size:{}.", roleId, start, size);
        List<AuthUser> authUsers = userService.listByRoleId(roleId, start, size);

        List<UserVO> usersVos = new LinkedList<>();
        for (AuthUser user : authUsers) {
            UserVO userVO = new UserVO();
            if (null != user) {
                BeanUtils.copyProperties(user, userVO);
            }
            usersVos.add(userVO);
        }
        return CommonResponse.ok(usersVos).build();
    }

    /**
     * 获取角色(roleId)所被授权的API资源
     */
    @GetMapping("/{role_id}/resource/{start}/{size}")
    public CommonResponse listRestResourceByRoleId(@PathVariable(value = "role_id") Integer roleId,
                                                   @PathVariable(value = "start") Integer start,
                                                   @PathVariable(value = "size") Integer size) {

        LOG.info("Get resources by roleId. roleId:{}. start:{}. size:{}.", roleId, start, size);
        List<AuthResource> authResources = resourceService.listByRoleId(roleId, start, size);

        return CommonResponse.ok(authResources).build();
    }


}

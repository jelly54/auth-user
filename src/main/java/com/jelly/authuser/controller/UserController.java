package com.jelly.authuser.controller;

import com.jelly.authuser.entity.AuthUser;
import com.jelly.authuser.entity.AuthUserInfo;
import com.jelly.authuser.exception.ErrorEnum;
import com.jelly.authuser.exception.ServerException;
import com.jelly.authuser.service.AuthUserService;
import com.jelly.authuser.util.CommonResponse;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author guodongzhang
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    AuthUserService userService;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 创建用户详细信息
     */
    @PostMapping("/info")
    public CommonResponse createUserInfo(@RequestParam(value = "user_id") Long userId,
                                         @RequestParam(value = "real_name", defaultValue = "") String realName,
                                         @RequestParam(value = "sex", defaultValue = "-1") Integer sex,
                                         @RequestParam(value = "birth", defaultValue = "0") Long birth,
                                         @RequestParam(value = "avatar_url", defaultValue = "") String avatarUrl) {

        LOG.info("Create userInfo. userId:{}, realName:{},sex:{}, birth:{},avatarUrl:{}, ", userId,
                realName, sex, birth, avatarUrl);

        AuthUser dbUser = userService.getUserByPrimaryKey(userId);
        if (dbUser == null) {
            LOG.warn("Create userInfo failed. userId has not registered! userId:{}.", userId);
            throw new ServerException(ErrorEnum.INVALID_PARAMS, "the userId has not registered!");
        }

        AuthUserInfo dbUserInfo = userService.getUserInfoByUserId(userId);
        if (dbUserInfo != null) {
            LOG.warn("Create userInfo failed. userId has registered! userId:{}.", userId);
            throw new ServerException(ErrorEnum.INVALID_PARAMS, "the userInfo has registered!");
        }

        boolean create = userService.createUserInfo(userId, realName, sex, birth, avatarUrl);

        if (!create) {
            LOG.info("Create failed！userId:{}, realName:{},sex:{}, birth:{},avatarUrl:{},  ", userId,
                    realName, sex, birth, avatarUrl);
            throw new ServerException(ErrorEnum.INTERNAL_EXCEPT);
        }

        LOG.info("Create userInfo successful.. userId:{}, realName:{},sex:{}, birth:{},avatarUrl:{}, ", userId,
                realName, sex, birth, avatarUrl);
        AuthUserInfo userInfo = userService.getUserInfoByUserId(userId);

        return CommonResponse.ok().data(userInfo).build();
    }

    /**
     * 更新用户详细信息下
     */
    @PatchMapping("/info")
    public CommonResponse updateUserInfo(@RequestParam(value = "user_id") Long userId,
                                         @RequestParam(value = "real_name", defaultValue = "") String realName,
                                         @RequestParam(value = "sex", defaultValue = "") Integer sex,
                                         @RequestParam(value = "birth", defaultValue = "") Long birth,
                                         @RequestParam(value = "avatar_url", defaultValue = "") String avatarUrl,
                                         HttpServletRequest request) {

        LOG.info("Update userInfo. userId:{}, realName:{},sex:{}, birth:{},avatarUrl:{},  ", userId,
                realName, sex, birth, avatarUrl);

        String appId = request.getHeader("app_id");
        Long dbUserId = userService.getUserIdByAppId(appId);
        if (!userId.equals(dbUserId)) {
            LOG.warn("Update userInfo failed. user has no permission! appId:{}. update userId:{}.", appId, userId);
            throw new ServerException(ErrorEnum.NO_PERMISSION);
        }

        AuthUser dbUser = userService.getUserByPrimaryKey(userId);
        AuthUserInfo dbUserInfo = userService.getUserInfoByUserId(userId);
        if (dbUser == null || dbUserInfo == null) {
            LOG.warn("Update userInfo failed. userId has not registered! userId:{}.", userId);
            throw new ServerException(ErrorEnum.INVALID_PARAMS, "the userId has not registered!");
        }

        boolean update = userService.updateUserInfoByUserId(userId, realName, sex, birth, avatarUrl);

        if (!update) {
            LOG.warn("Update failed！userId:{}, realName:{},sex:{}, birth:{},avatarUrl:{},  ", userId,
                    realName, sex, birth, avatarUrl);
            throw new ServerException(ErrorEnum.INTERNAL_EXCEPT);
        }

        LOG.info("Update userInfo successful. userId:{}, realName:{},sex:{}, birth:{},avatarUrl:{},  ", userId,
                realName, sex, birth, avatarUrl);
        AuthUserInfo userInfo = userService.getUserInfoByUserId(userId);

        return CommonResponse.ok().data(userInfo).build();
    }

    /**
     * 获取用户详细信息
     */
    @GetMapping("/info")
    public CommonResponse getUserInfo(HttpServletRequest request) {
        String appId = request.getHeader("app_id");
        LOG.info("Get userInfo. appId:{}.", appId);
        Long userId = userService.getUserIdByAppId(appId);
        if (null == userId) {
            return CommonResponse.error(ErrorEnum.INVALID_PARAMS, "invalid appId").build();
        }
        AuthUserInfo userInfo = userService.getUserInfoByUserId(userId);
        LOG.info("Get userInfo successful. {}", userInfo);

        return CommonResponse.ok().data(userInfo).build();
    }

    /**
     * 获取当前用户的角色列表
     */
    @GetMapping("/role")
    public CommonResponse getUserRoleList(HttpServletRequest request) {

        String appId = request.getHeader("app_id");
        if (StringUtils.isEmpty(appId)) {
            throw new ServerException(ErrorEnum.INVALID_REQUEST);
        }

        LOG.info("Get current user roles. appId:{}.", appId);
        List<String> roles = userService.listRolesByAppId(appId);

        return CommonResponse.ok(roles).build();
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list/{start}/{size}")
    public CommonResponse listUsers(@PathVariable(value = "start") Integer start,
                                    @PathVariable(value = "size") Integer size) {
        LOG.info("List all users. start:{}. size:{}.", start, size);
        List<AuthUser> authUsers = userService.listAll(start, size);

        return CommonResponse.ok(authUsers).build();
    }

//======================================================================================================================

    /**
     * 给用户授权添加角色
     */
    @PostMapping("/auth/role")
    public CommonResponse authorityUserRole(@RequestParam("user_id") Long userId,
                                            @RequestParam("role_id") Integer roleId) {

        LOG.info("Authority user-role. userId:{}. roleId:{}.", userId, roleId);
        boolean flag = userService.createUserRole(userId, roleId);

        if (!flag) {
            LOG.warn("Authority user-role failed. userId:{}, roleId:{}.", userId, roleId);
            return CommonResponse.error(ErrorEnum.INTERNAL_EXCEPT, "Authority error!").build();
        }

        LOG.info("Authority user-role successfully. userId:{}, roleId:{}.", userId, roleId);
        return CommonResponse.ok().data("auth", true).build();
    }

    /**
     * 删除已经授权的用户角色
     */
    @DeleteMapping("/auth/{user_id}/role/{role_id}")
    public CommonResponse deleteUserRole(@PathVariable("user_id") Long userId,
                                         @PathVariable("role_id") Integer roleId) {
        if (userId < 0 || roleId < 0) {
            throw new ServerException(ErrorEnum.INVALID_PARAMS);
        }

        LOG.info("Delete user-role. userId:{}. roleId:{}.", userId, roleId);
        boolean delete = userService.deleteUserRole(userId, roleId);

        if (!delete) {
            LOG.info("Delete user-role failed! userId:{}, roleId:{}.", userId, roleId);
            return CommonResponse.error(ErrorEnum.INTERNAL_EXCEPT, "Delete failed!").build();
        }

        LOG.info("Delete user-role successfully. userId:{}, roleId:{}.", userId, roleId);
        return CommonResponse.ok().data("delete", true).build();

    }
//======================================================================================================================

    /**
     * 退出
     */
    @PostMapping("/logout")
    public CommonResponse logout(@RequestParam(value = "ticket", defaultValue = "") String ticket,
                                 HttpServletRequest request) {

        String appId = request.getHeader("app_id");
        LOG.info("User logout. appId:{}.", appId);

        Long userId = userService.getUserIdByAppId(appId);

        if (StringUtils.isEmpty(appId) || userId == null) {
            return CommonResponse.error(ErrorEnum.INVALID_PARAMS, "please check app_id.").build();
        }
        SecurityUtils.getSubject().logout();

        String jwt = redisTemplate.opsForValue().get("JWT-SESSION-" + userId);
        if (StringUtils.isEmpty(jwt)) {
            LOG.warn("User has not login. appId:{}.", userId);
            return CommonResponse.error(ErrorEnum.INVALID_REQUEST, "User has not login.").build();
        }
        redisTemplate.opsForValue().getOperations().delete("JWT-SESSION-" + userId);

        //uncheck  others logout


        //TODO 记录用户登出IP以及时间?

        LOG.info("User logout success. appId:{}.", appId);
        return CommonResponse.ok().build();
    }
}

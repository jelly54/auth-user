package com.jelly.authuser.controller;


import com.jelly.authuser.entity.AuthUser;
import com.jelly.authuser.entity.vo.UserVO;
import com.jelly.authuser.exception.ErrorEnum;
import com.jelly.authuser.exception.ServerException;
import com.jelly.authuser.service.AuthUserService;
import com.jelly.authuser.util.AesUtil;
import com.jelly.authuser.util.CheckTools;
import com.jelly.authuser.util.CommonResponse;
import com.jelly.authuser.util.IpUtil;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author guodongzhang
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    AuthUserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @PostMapping("/login")
    public CommonResponse login(@RequestParam(value = "username", defaultValue = "") String username,
                                @RequestParam(value = "phone", defaultValue = "") String phone,
                                @RequestParam(value = "mail", defaultValue = "") String mail) {

        Object[] appIdUserId = userService.getAppIdUserId(username, phone, mail);
        if (appIdUserId == null) {
            throw new ServerException(ErrorEnum.INVALID_PARAMS, "check your account or password!");
        }

        String jwt = userService.issueLoginToken(appIdUserId);

        AuthUser authUser = userService.getUserByPrimaryKey(Long.valueOf(appIdUserId[1].toString()));
        if (null == authUser) {
            LOG.warn("get none user!!");
            return CommonResponse.ok().build();
        }

        UserVO userVO = new UserVO();
        userVO.setToken(jwt);
        BeanUtils.copyProperties(authUser, userVO);
        return CommonResponse.ok(userVO).build();
    }


    /**
     * 注册
     */
    @PostMapping("/register")
    public CommonResponse register(@RequestParam(value = "username") String username,
                                   @RequestParam(value = "password") String password,
                                   @RequestParam(value = "phone", defaultValue = "") String phone,
                                   @RequestParam(value = "mail", defaultValue = "") String mail,
                                   @RequestParam(value = "user_key") String userKey,
                                   HttpServletRequest request) {
        LOG.info("Register info. username:{}, userKey:{}.", username, userKey);

        AuthUser dbUser = userService.getUserByUsername(username);
        if (dbUser != null) {
            throw new ServerException(ErrorEnum.INVALID_PARAMS, "the username has registered!");
        }

        String tokenKey = redisTemplate.opsForValue().get("TOKEN_KEY_" +
                IpUtil.getIpFromRequest(WebUtils.toHttp(request)).toUpperCase() + userKey);
        LOG.info("Get tokenKey from redis done. tokenKey:{}.", tokenKey);

        boolean create = userService.createUser(username, AesUtil.aesDecode(password, tokenKey), phone, mail, 0L);

        if (!create) {
            LOG.info("Registered failed！username:{}. password:{}. userKey:{}.", username, password, userKey);
            throw new ServerException(ErrorEnum.INTERNAL_EXCEPT);
        }

        return CommonResponse.ok().data("create", true).build();
    }

    /**
     * 根据登陆账户更新账户
     */
    @PatchMapping("")
    public CommonResponse updateAccount(@RequestParam(value = "phone", defaultValue = "") String phone,
                                        @RequestParam(value = "username", defaultValue = "") String username,
                                        @RequestParam(value = "mail", defaultValue = "") String mail,
                                        @RequestParam(value = "password", defaultValue = "") String password,
                                        @RequestParam(value = "group_id", defaultValue = "") Long groupId,
                                        @RequestParam(value = "user_key") String userKey,
                                        HttpServletRequest request) {

        String appId = request.getHeader("app_id");
        Long userId = userService.getUserIdByAppId(appId);
        if (userId == null) {
            throw new ServerException(ErrorEnum.INVALID_PARAMS);
        }

        String tokenKey = redisTemplate.opsForValue().get("TOKEN_KEY_" +
                IpUtil.getIpFromRequest(WebUtils.toHttp(request)).toUpperCase() + userKey);
        LOG.info("Get tokenKey from redis done. tokenKey:{}.", tokenKey);

        boolean valPhone = phone != null && !CheckTools.checkPhone(phone);
        boolean valMail = mail != null && !CheckTools.checkMail(mail);
        if (valPhone || valMail) {
            LOG.warn("Param invalid. phone:{}. mail:{}.", valPhone ? phone : "", valMail ? mail : "");
            throw new ServerException(ErrorEnum.INVALID_PARAMS);
        }

        boolean updateAccount = userService.updateUserById(userId, username, AesUtil.aesDecode(password, tokenKey),
                null, phone, mail, groupId);

        if (!updateAccount) {
            LOG.info("Update account failed！phone:{}, username:{},  password:{}, groupId:{}, userKey:{}.",
                    phone, username, password, groupId, tokenKey);
            throw new ServerException(ErrorEnum.INTERNAL_EXCEPT);
        }
        return CommonResponse.ok().data("update", true).build();
    }
}

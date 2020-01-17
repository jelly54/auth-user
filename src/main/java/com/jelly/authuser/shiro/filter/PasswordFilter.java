package com.jelly.authuser.shiro.filter;


import com.alibaba.fastjson.JSON;
import com.jelly.authuser.exception.ErrorEnum;
import com.jelly.authuser.service.AuthUserService;
import com.jelly.authuser.shiro.token.PasswordToken;
import com.jelly.authuser.util.CommonResponse;
import com.jelly.authuser.util.CommonUtil;
import com.jelly.authuser.util.IpUtil;
import com.jelly.authuser.util.JsonWebTokenUtil;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 基于 用户名密码 的认证过滤器
 * /account/** 请求将会进入
 *
 * @author guodongzhang
 */
public class PasswordFilter extends AccessControlFilter {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordFilter.class);

    private StringRedisTemplate redisTemplate;

    private AuthUserService userService;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        return null != subject && subject.isAuthenticated();
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {

        // 判断若为获取加密动态秘钥请求
        if (isGetDynamicKey(request)) {
            return doGetDynamicKey(request, response);
        }

        //判断是否为刷新token
        if (isTokenRefresh(request)) {
            return doRefreshToken(request, response);
        }

        // 判断是否是登录请求
        if (isPasswordLogin(request)) {
            return doPasswordLogin(request, response);
        }

        // 判断是否为注册请求,若是通过过滤链进入controller注册
        if (isAccountRegiste(request)) {
            return true;
        }

        // 判断是否为更新用户信息请求,若是验证token后通过过滤链进入controller更新
        if (isAccountPatch(request)) {
            return doAccountPatch(request, response);
        }

        CommonResponse msg = CommonResponse.error(ErrorEnum.INVALID_REQUEST).build();
        CommonUtil.responseWrite(JSON.toJSONString(msg), response);
        return false;
    }

    private boolean isGetDynamicKey(ServletRequest request) {
        String tokenKey = request.getParameter("token");

        return (request instanceof HttpServletRequest)
                && "GET".equals(((HttpServletRequest) request).getMethod().toUpperCase())
                && "get_key".equals(tokenKey);
    }

    private boolean isTokenRefresh(ServletRequest request) {
        String refresh = request.getParameter("token");

        return (request instanceof HttpServletRequest)
                && "GET".equals(((HttpServletRequest) request).getMethod().toUpperCase())
                && "refresh".equals(refresh);
    }

    private boolean isPasswordLogin(ServletRequest request) {

        String password = request.getParameter("password");
        String timestamp = request.getParameter("timestamp");
        String methodName = request.getParameter("method_name");
        return (request instanceof HttpServletRequest)
                && "POST".equals(((HttpServletRequest) request).getMethod().toUpperCase())
                && null != password
                && null != timestamp
                && "login".equals(methodName);
    }

    private boolean isAccountRegiste(ServletRequest request) {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String timestamp = request.getParameter("timestamp");
        String methodName = request.getParameter("method_name");
        return (request instanceof HttpServletRequest)
                && "POST".equals(((HttpServletRequest) request).getMethod().toUpperCase())
                && null != username
                && null != password
                && null != timestamp
                && "register".equals(methodName);
    }

    private boolean isAccountPatch(ServletRequest request) {

        return (request instanceof HttpServletRequest)
                && "PATCH".equals(((HttpServletRequest) request).getMethod().toUpperCase());
    }

    private boolean doGetDynamicKey(ServletRequest request, ServletResponse response) {
        //动态生成秘钥，redis存储秘钥供之后秘钥验证使用，设置有效期5秒用完即丢弃
        String tokenKey = CommonUtil.getRandomString(16);
        String userKey = CommonUtil.getRandomString(6);
        try {
            int expireTime = 5;
            redisTemplate.opsForValue().set("TOKEN_KEY_" +
                            IpUtil.getIpFromRequest(WebUtils.toHttp(request)).toUpperCase() + userKey.toUpperCase(),
                    tokenKey, expireTime, TimeUnit.SECONDS);
            LOG.info("Generate userKey: {}, tokenKey: {}.", userKey, tokenKey);

            // 动态秘钥response返回给前端
            HashMap<Object, Object> tokenData = new HashMap<>(2);
            tokenData.put("token_key", tokenKey);
            tokenData.put("user_key", userKey.toUpperCase());
            CommonUtil.responseWrite(JSON.toJSONString(CommonResponse.ok().data(tokenData).build()), response);

        } catch (Exception e) {
            LOG.warn("Signing a dynamic key failed" + e.getMessage(), e);
            CommonResponse msg = CommonResponse.error(ErrorEnum.INTERNAL_EXCEPT, "issued tokenKey fail").build();
            CommonUtil.responseWrite(JSON.toJSONString(msg), response);
        }
        return false;
    }

    private boolean doRefreshToken(ServletRequest request, ServletResponse response) {
        String appId = WebUtils.toHttp(request).getHeader("app_id");
        String jwt = WebUtils.toHttp(request).getHeader("authorization");
        String refreshJwt = redisTemplate.opsForValue().get("JWT-SESSION-" + appId);

        if (!JsonWebTokenUtil.verifyAppIdIsJwt(appId, jwt)) {
            CommonUtil.responseWrite(JSON.toJSONString(CommonResponse.error(ErrorEnum.INVALID_PARAMS,
                    "errJwt, please login.").build()), response);
            return false;
        }

        if (null != refreshJwt && refreshJwt.equals(jwt)) {
            Long userId = userService.getUserIdByAppId(appId);
            if (userId == null) {
                return false;
            }

            String newJwt = userService.issueLoginToken(new Object[]{appId, userId});
            CommonUtil.responseWrite(JSON.toJSONString(CommonResponse.ok().data("token", newJwt).build()), response);
            return false;
        } else {
            // jwt时间失效过期,jwt refresh time失效
            LOG.info("token expired.");
            CommonResponse msg = CommonResponse.error(ErrorEnum.TOKEN_EXPIRED, "token expired, please login.").build();
            CommonUtil.responseWrite(JSON.toJSONString(msg), response);
            return false;
        }
    }

    private boolean doPasswordLogin(ServletRequest request, ServletResponse response) {
        AuthenticationToken authenticationToken;
        try {
            authenticationToken = createPasswordToken(request);
        } catch (Exception e) {
            // response 告知无效请求
            CommonResponse msg = CommonResponse.error(ErrorEnum.INVALID_REQUEST).build();
            CommonUtil.responseWrite(JSON.toJSONString(msg), response);
            return false;
        }

        Subject subject = getSubject(request, response);
        try {
            subject.login(authenticationToken);
            return true;
        } catch (Exception e) {
            LOG.error(authenticationToken.getPrincipal() + "::auth exception ::" + e.getMessage(), e);
            // 返回response告诉客户端认证失败
            CommonResponse msg = CommonResponse.error(ErrorEnum.INVALID_USER, "login fail").build();
            CommonUtil.responseWrite(JSON.toJSONString(msg), response);
            return false;
        }
    }

    private boolean doAccountPatch(ServletRequest request, ServletResponse response) {
        String appId = WebUtils.toHttp(request).getHeader("app_id");
        String jwt = WebUtils.toHttp(request).getHeader("authorization");

        if (!JsonWebTokenUtil.verifyAppIdIsJwt(appId, jwt)) {
            CommonResponse msg = CommonResponse.error(ErrorEnum.INVALID_PARAMS, "errJwt").build();
            CommonUtil.responseWrite(JSON.toJSONString(msg), response);
            return false;
        }
        return true;
    }

    private AuthenticationToken createPasswordToken(ServletRequest request) throws Exception {

        String username = request.getParameter("username");
        String phone = request.getParameter("phone");
        String mail = request.getParameter("mail");

        String timestamp = request.getParameter("timestamp");
        String password = request.getParameter("password");
        String host = IpUtil.getIpFromRequest(WebUtils.toHttp(request));
        String userKey = request.getParameter("user_key");

        String appId = userService.getAppIdUserId(username, phone, mail)[0].toString();

        //如果redis中token_key 过期将会在AesUtil 报空指针。上层会处理
        String tokenKey = redisTemplate.opsForValue().get("TOKEN_KEY_" + host.toUpperCase() + userKey);
        return new PasswordToken(appId, password, timestamp, host, tokenKey);
    }

    void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    void setUserService(AuthUserService userService) {
        this.userService = userService;
    }

}

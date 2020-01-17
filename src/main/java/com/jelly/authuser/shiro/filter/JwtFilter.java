package com.jelly.authuser.shiro.filter;

import com.alibaba.fastjson.JSON;
import com.jelly.authuser.exception.ErrorEnum;
import com.jelly.authuser.shiro.token.JwtToken;
import com.jelly.authuser.util.CommonResponse;
import com.jelly.authuser.util.CommonUtil;
import com.jelly.authuser.util.IpUtil;
import com.jelly.authuser.util.JsonWebTokenUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 支持restful url 的过滤链  JWT json web token 过滤器，无状态验证
 *
 * @author guodongzhang
 */
public class JwtFilter extends AbstractPathMatchingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtFilter.class);


    private StringRedisTemplate redisTemplate;

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object mappedValue) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);


        // 判断是否为JWT认证请求
        boolean isJwtReq = (null != subject && !subject.isAuthenticated()) && isJwtSubmission(servletRequest);
        if (isJwtReq) {
            AuthenticationToken token = createJwtToken(servletRequest);
            try {
                subject.login(token);
                //每次验证将刷新token有效期 5小时
                redisTemplate.expire("JWT-SESSION-" + token.getPrincipal(), 18000, TimeUnit.SECONDS);
                return this.checkRoles(subject, mappedValue);
            } catch (AuthenticationException e) {
                // 如果是JWT过期
                String jwtExceptionMsg = "expired";
                if (jwtExceptionMsg.equals(e.getMessage())) {
                    CommonResponse msg = CommonResponse.error(ErrorEnum.TOKEN_EXPIRED, "Place refresh token.").build();
                    CommonUtil.responseWrite(JSON.toJSONString(msg), servletResponse);
                    return false;
                } else {
                    // jwt时间失效过期,jwt refresh time失效
                    CommonResponse msg = CommonResponse.error(ErrorEnum.TOKEN_EXCEPT, "Place login.").build();
                    CommonUtil.responseWrite(JSON.toJSONString(msg), servletResponse);
                    return false;
                }

            } catch (Exception e) {
                LOG.error(IpUtil.getIpFromRequest(WebUtils.toHttp(servletRequest)) + "--JWT failed " + e.getMessage(), e);
                CommonResponse msg = CommonResponse.error(ErrorEnum.TOKEN_EXCEPT).build();
                CommonUtil.responseWrite(JSON.toJSONString(msg), servletResponse);
                return false;
            }
        } else {
            // 请求未携带jwt 判断为无效请求
            CommonResponse msg = CommonResponse.error(ErrorEnum.INVALID_REQUEST).build();
            CommonUtil.responseWrite(JSON.toJSONString(msg), servletResponse);
            return false;
        }
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);

        // 未认证的情况
        if (null == subject || !subject.isAuthenticated()) {
            // 告知客户端JWT认证失败需跳转到登录页面
            CommonResponse msg = CommonResponse.error(ErrorEnum.INVALID_USER).build();
            CommonUtil.responseWrite(JSON.toJSONString(msg), servletResponse);
        } else {
            // 告知客户端JWT没有权限访问此资源
            CommonResponse msg = CommonResponse.error(ErrorEnum.NO_PERMISSION).build();
            CommonUtil.responseWrite(JSON.toJSONString(msg), servletResponse);
        }
        return false;
    }

    private boolean isJwtSubmission(ServletRequest request) {
        String jwt = ((HttpServletRequest) request).getHeader("authorization");
        String appId = ((HttpServletRequest) request).getHeader("app_id");
        boolean verifyAppIdIsJwt = false;
        if (!StringUtils.isEmpty(jwt) && !StringUtils.isEmpty(appId)) {
            verifyAppIdIsJwt = JsonWebTokenUtil.verifyAppIdIsJwt(appId, jwt);
        }
        return verifyAppIdIsJwt;
    }

    private AuthenticationToken createJwtToken(ServletRequest request) {
        String appId = ((HttpServletRequest) request).getHeader("app_id");
        String ipHost = request.getRemoteAddr();
        String jwt = ((HttpServletRequest) request).getHeader("authorization");
        String deviceInfo = ((HttpServletRequest) request).getHeader("deviceInfo");

        return new JwtToken(ipHost, deviceInfo, jwt, appId);
    }

    /**
     * 验证当前用户是否属于mappedValue任意一个角色
     */
    private boolean checkRoles(Subject subject, Object mappedValue) {
        String[] rolesArray = (String[]) mappedValue;
        return rolesArray != null && rolesArray.length > 0 &&
                Stream.of(rolesArray).anyMatch(role -> subject.hasRole(role.trim()));
    }


    void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

}

package com.jelly.authuser.shiro.realm;


import com.jelly.authuser.entity.bo.Account;
import com.jelly.authuser.service.AuthUserService;
import com.jelly.authuser.shiro.token.PasswordToken;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 此Realm只支持PasswordToken
 * 这里只需要认证登录，成功之后派发 json web token 授权在那里进行
 *
 * @author guodongzhang
 */
public class PasswordRealm extends AuthorizingRealm {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordRealm.class);

    @Autowired
    private AuthUserService userService;

    @Override
    public Class<?> getAuthenticationTokenClass() {
        return PasswordToken.class;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        if (!(authenticationToken instanceof PasswordToken)) {
            return null;
        }

        if (null == authenticationToken.getPrincipal() || null == authenticationToken.getCredentials()) {
            throw new UnknownAccountException();
        }

        String appId = (String) authenticationToken.getPrincipal();
        Account account = userService.getAccountByUniqueKey(appId);

        if (account != null) {
            return new SimpleAuthenticationInfo(account, account.getPassword(), ByteSource.Util.bytes(account.getSalt()), getName());
        } else {
            return new SimpleAuthenticationInfo(appId, "", getName());
        }
    }

    public void setUserService(AuthUserService userService) {
        this.userService = userService;
    }
}

package com.jelly.authuser.shiro.matcher;


import com.jelly.authuser.entity.bo.JwtAccount;
import com.jelly.authuser.util.JsonWebTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.stereotype.Component;


/**
 * @author guodongzhang
 */
@Component
public class JwtMatcher implements CredentialsMatcher {


    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {

        String jwt = (String) authenticationInfo.getCredentials();
        String tokenJwt = (String) authenticationToken.getCredentials();
        JwtAccount jwtAccount, tokenJwtAccount;
        try {
            tokenJwtAccount = JsonWebTokenUtil.parseJwt(tokenJwt, JsonWebTokenUtil.SECRET_KEY);
            if (!authenticationToken.getPrincipal().toString().equalsIgnoreCase(tokenJwtAccount.getAppId())) {
                throw new AuthenticationException("errJwt");
            }

            jwtAccount = JsonWebTokenUtil.parseJwt(jwt, JsonWebTokenUtil.SECRET_KEY);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException("expired");
        } catch (Exception e) {
            throw new AuthenticationException("errJwt");
        }

        if (null == jwtAccount) {
            throw new AuthenticationException("errJwt");
        }

        return true;
    }
}

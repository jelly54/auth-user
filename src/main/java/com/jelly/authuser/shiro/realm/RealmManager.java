package com.jelly.authuser.shiro.realm;


import com.jelly.authuser.service.AuthUserService;
import com.jelly.authuser.shiro.matcher.JwtMatcher;
import com.jelly.authuser.shiro.token.JwtToken;
import com.jelly.authuser.shiro.token.PasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * realm管理器
 *
 * @author guodongzhang
 */
@Component
public class RealmManager {

    @Value("${shiro.realm.hash-iterations}")
    int hashIterations = 3;

    private JwtMatcher jwtMatcher;
    private AuthUserService userService;

    @Autowired
    public RealmManager(AuthUserService userService, JwtMatcher jwtMatcher) {
        this.userService = userService;
        this.jwtMatcher = jwtMatcher;
    }

    public List<Realm> initGetRealm() {
        List<Realm> realmList = new LinkedList<>();
        // ----- password
        PasswordRealm passwordRealm = new PasswordRealm();
        passwordRealm.setUserService(userService);
        passwordRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        passwordRealm.setAuthenticationTokenClass(PasswordToken.class);
        realmList.add(passwordRealm);

        // ----- jwt
        JwtRealm jwtRealm = new JwtRealm();
        jwtRealm.setCredentialsMatcher(jwtMatcher);
        jwtRealm.setAuthenticationTokenClass(JwtToken.class);
        realmList.add(jwtRealm);
        return Collections.unmodifiableList(realmList);
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(hashIterations);
        return hashedCredentialsMatcher;
    }
}

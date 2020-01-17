package com.jelly.authuser.shiro.filter;


import com.jelly.authuser.service.AuthResourceService;
import com.jelly.authuser.service.AuthUserService;
import com.jelly.authuser.shiro.config.RestPathMatchingFilterChainResolver;
import com.jelly.authuser.shiro.rule.UrlRolesRule;
import com.jelly.authuser.util.SpringContextHolder;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import java.util.*;

/**
 * @author guodongzhang
 */
@Component
public class ShiroFilterChainManager {

    private static final Logger LOG = LoggerFactory.getLogger(ShiroFilterChainManager.class);

    private final StringRedisTemplate redisTemplate;
    private final AuthResourceService resourceService;
    private final AuthUserService userService;

    @Autowired
    public ShiroFilterChainManager(StringRedisTemplate redisTemplate, AuthResourceService resourceService, AuthUserService userService) {
        this.redisTemplate = redisTemplate;
        this.resourceService = resourceService;
        this.userService = userService;
    }

    /**
     * 初始化获取过滤链
     * 1. 密码验证
     * 2. jwt 验证
     */
    public Map<String, Filter> initGetFilters() {
        Map<String, Filter> filters = new LinkedHashMap<>();
        PasswordFilter passwordFilter = new PasswordFilter();
        passwordFilter.setRedisTemplate(redisTemplate);
        passwordFilter.setUserService(userService);
        filters.put("auth", passwordFilter);
        LOG.info("Init auth filter.");

        JwtFilter jwtFilter = new JwtFilter();
        jwtFilter.setRedisTemplate(redisTemplate);
        filters.put("jwt", jwtFilter);
        LOG.info("Init jwt filter.");
        return filters;
    }

    /**
     * 初始化 获取过滤链规则
     */
    public Map<String, String> initGetFilterChain() {
        Map<String, String> filterChain = new LinkedHashMap<>();
        // -------------anon 默认过滤器忽略的URL
        List<String> defaultAnon = Arrays.asList("/css/**", "/js/**");
        defaultAnon.forEach(ignored -> filterChain.put(ignored, "anon"));

        // -------------auth 默认需要认证过滤器的URL 走auth--PasswordFilter
        List<String> defaultAuth = Collections.singletonList("/account/**");
        defaultAuth.forEach(auth -> filterChain.put(auth, "auth"));

        // -------------dynamic 加载动态URL
        if (resourceService != null) {
            List<UrlRolesRule> urlRolesRules = this.resourceService.loadUrlRolesRules();
            if (null != urlRolesRules) {
                urlRolesRules.forEach(rule -> {
                    StringBuilder chain = rule.toFilterChain();
                    if (null != chain) {
                        filterChain.putIfAbsent(rule.getUrl(), chain.toString());
                    }
                });
            }
        }

        StringBuilder filters = new StringBuilder();
        filterChain.forEach((k, v) -> filters.append(k).append("-").append(v).append(";"));
        LOG.info("Init filter chain done. filters:{}.", filters);

        return filterChain;
    }

    /**
     * 动态重新加载过滤链规则
     */
    public void reloadFilterChain() {
        LOG.info("Reload filter chain.");
        ShiroFilterFactoryBean shiroFilterFactoryBean = SpringContextHolder.getBean(ShiroFilterFactoryBean.class);
        AbstractShiroFilter abstractShiroFilter;
        try {
            abstractShiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();
            assert abstractShiroFilter != null;
            RestPathMatchingFilterChainResolver filterChainResolver = (RestPathMatchingFilterChainResolver)
                    abstractShiroFilter.getFilterChainResolver();
            DefaultFilterChainManager filterChainManager = (DefaultFilterChainManager)
                    filterChainResolver.getFilterChainManager();
            filterChainManager.getFilterChains().clear();

            shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
            shiroFilterFactoryBean.setFilterChainDefinitionMap(this.initGetFilterChain());
            shiroFilterFactoryBean.getFilterChainDefinitionMap().forEach(filterChainManager::createChain);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.info("Reload filter chain done.");
    }
}

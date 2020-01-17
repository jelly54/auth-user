package com.jelly.authuser.shiro.config;


import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Iterator;

/**
 * @author guodongzhang
 */
public class RestPathMatchingFilterChainResolver extends PathMatchingFilterChainResolver {

    private static final Logger LOG = LoggerFactory.getLogger(RestPathMatchingFilterChainResolver.class);

    public RestPathMatchingFilterChainResolver() {
        super();
    }

    public RestPathMatchingFilterChainResolver(FilterConfig filterConfig) {
        super(filterConfig);
    }

    /**
     * 重写filterChain匹配
     *
     * @Param [request, response, originalChain]
     * @Return javax.servlet.FilterChain
     */
    @Override
    public FilterChain getChain(ServletRequest request, ServletResponse response, FilterChain originalChain) {
        FilterChainManager filterChainManager = this.getFilterChainManager();
        if (!filterChainManager.hasChains()) {
            return null;
        } else {
            String requestUri = this.getPathWithinApplication(request);
            Iterator var6 = filterChainManager.getChainNames().iterator();

            String pathPattern;
            boolean flag;
            String[] strings;
            do {
                if (!var6.hasNext()) {
                    return null;
                }

                pathPattern = (String) var6.next();

                strings = pathPattern.split("==");
                if (strings.length == 2) {
                    // 分割出url+httpMethod,判断httpMethod和request请求的method是否一致,不一致直接false
                    flag = !WebUtils.toHttp(request).getMethod().toUpperCase().equals(strings[1].toUpperCase());
                } else {
                    flag = false;
                }
                pathPattern = strings[0];
            } while (!this.pathMatches(pathPattern, requestUri) || flag);

            if (LOG.isTraceEnabled()) {
                LOG.debug("Matched path pattern [{}] for requestURI [{}].  Utilizing corresponding filter chain...",
                        pathPattern, requestUri);
            }
            if (strings.length == 2) {
                pathPattern = pathPattern.concat("==").concat(WebUtils.toHttp(request).getMethod().toUpperCase());
            }

            return filterChainManager.proxy(originalChain, pathPattern);
        }
    }

}

package com.jelly.authuser.shiro.rule;

import com.jelly.authuser.util.JsonWebTokenUtil;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Set;

/**
 * @author guodongzhang
 */
public class UrlRolesRule implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;
    /**
     * 访问资源所需要的角色列表，多个列表用逗号间隔
     */
    private String needRoles;

    public String getUrl() {
        return url;
    }

    public String getNeedRoles() {
        return needRoles;
    }

    /**
     * 将url needRoles 转化成shiro可识别的过滤器链：url=jwt[角色1,角色2,角色n]
     */
    public StringBuilder toFilterChain() {

        if (null == this.url || this.url.isEmpty()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> setRole = JsonWebTokenUtil.split(this.getNeedRoles());
        // 约定若role_anon角色拥有此uri资源的权限,则此uri资源直接访问不需要认证和权限
        String roleAnon = "role_anon";
        if (!StringUtils.isEmpty(this.getNeedRoles()) && setRole.contains(roleAnon)) {
            stringBuilder.append("anon");
        }
        //  其他自定义资源uri需通过jwt认证和角色认证
        if (!StringUtils.isEmpty(this.getNeedRoles()) && !setRole.contains(roleAnon)) {
            stringBuilder.append("jwt" + "[").append(this.getNeedRoles()).append("]");
        }

        return stringBuilder.length() > 0 ? stringBuilder : null;
    }

    @Override
    public String toString() {
        return "UrlRolesRule [url=" + url + ", needRoles=" + needRoles + "]";
    }
}

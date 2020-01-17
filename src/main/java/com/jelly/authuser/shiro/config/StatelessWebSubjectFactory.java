package com.jelly.authuser.shiro.config;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * 对于无状态的TOKEN不创建session
 * @author guodongzhang
 */
public class StatelessWebSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(SubjectContext context) {
        AuthenticationToken token = context.getAuthenticationToken();
        context.setSessionCreationEnabled(Boolean.FALSE);
        return super.createSubject(context);
    }

    public StatelessWebSubjectFactory() {
    }

}

package com.github.ququzone.basicauth.web;

import com.github.ququzone.basicauth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * auth interceptor.
 *
 * @author Yang XuePing
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestPath = request.getRequestURI().substring(
                request.getContextPath().length());
        String userId = (String) request.getSession().getAttribute("user");
        if (userId == null || userId.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("user not login for request:" + requestPath);
            }
            response.sendRedirect("/login?next=" + requestPath);
            return false;
        }
        return authService.auditing(userId, requestPath);
    }
}
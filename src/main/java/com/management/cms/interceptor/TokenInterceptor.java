package com.management.cms.interceptor;

import com.management.cms.anotation.RequireToken;
import com.management.cms.exception.TokenInvalidException;
import com.management.cms.utils.JwtUtils;
import com.management.cms.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    // before the actual handler will be executed
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws TokenInvalidException {

        if (handler instanceof HandlerMethod) {
            RequireToken needToken = ((HandlerMethod) handler).getMethodAnnotation(RequireToken.class);
            log.info("Pre check require token in method : {}", needToken);
            // Check login if you have login validation annotations
            if (null != needToken) {
                try {
                    String jwt = Utils.parseJwt(request);
                    if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                        log.info("Token valid");
                    }
                } catch (Exception e) {
                    log.info("Exception while check token", e);
                    throw new TokenInvalidException("Token invalid");
                }
            }
        }
        return true;
    }
}

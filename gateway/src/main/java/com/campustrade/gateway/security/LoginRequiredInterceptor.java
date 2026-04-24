package com.campustrade.gateway.security;

import com.campustrade.gateway.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    private final AuthService authService;

    public LoginRequiredInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = resolveToken(request);
        Long userId = authService.authenticate(token);
        request.setAttribute(AuthConstants.CURRENT_USER_ID_ATTR, userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        request.removeAttribute(AuthConstants.CURRENT_USER_ID_ATTR);
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            String value = authorization.trim();
            if (value.regionMatches(true, 0, "Bearer ", 0, 7)) {
                return value.substring(7).trim();
            }
            if (!value.isEmpty()) {
                return value;
            }
        }

        String token = request.getParameter("token");
        if (token != null && !token.trim().isEmpty()) {
            return token.trim();
        }
        return null;
    }
}

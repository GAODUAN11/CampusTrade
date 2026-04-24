package com.campustrade.gateway.config;

import com.campustrade.gateway.security.CurrentUserIdArgumentResolver;
import com.campustrade.gateway.security.LoginRequiredInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LoginRequiredInterceptor loginRequiredInterceptor;
    private final CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    public WebConfig(LoginRequiredInterceptor loginRequiredInterceptor,
                     CurrentUserIdArgumentResolver currentUserIdArgumentResolver) {
        this.loginRequiredInterceptor = loginRequiredInterceptor;
        this.currentUserIdArgumentResolver = currentUserIdArgumentResolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginRequiredInterceptor)
                .addPathPatterns("/api/v1/me/**")
                .excludePathPatterns("/api/v1/auth/**", "/actuator/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserIdArgumentResolver);
    }
}

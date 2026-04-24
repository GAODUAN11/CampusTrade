package com.campustrade.gateway.security;

import com.campustrade.common.result.ResultCode;
import com.campustrade.gateway.exception.BusinessException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && Long.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Object value = webRequest.getAttribute(AuthConstants.CURRENT_USER_ID_ATTR, RequestAttributes.SCOPE_REQUEST);
        if (value instanceof Long longValue) {
            return longValue;
        }
        if (value instanceof Number numberValue) {
            return numberValue.longValue();
        }
        throw new BusinessException(ResultCode.UNAUTHORIZED, "Login required.");
    }
}

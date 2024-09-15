package pungmul.pungmul.service.member.loginvalidation.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import pungmul.pungmul.config.member.SessionConst;
import pungmul.pungmul.domain.member.auth.SessionUser;

@Slf4j
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(User.class);
        boolean hasMemberFormType = SessionUser.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginAnnotation && hasMemberFormType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new javax.security.sasl.AuthenticationException("로그인 필요");
        }
        return session.getAttribute(SessionConst.SESSION_USER);
    }
}
package com.example.rsupportnotice.config.usercontext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        // 예시: 헤더에서 사용자 ID 추출 (실제 인증 시스템과 연동 필요)
        String user = request.getHeader("X-User-Id");
        UserContextHolder.setCurrentUser(user != null ? user : "user");
        return true;
    }

    //ThreadLocal 사용 후 닫기 후처리
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        UserContextHolder.clear();
    }
}

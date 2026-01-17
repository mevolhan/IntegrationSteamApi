package com.gamer.tracker.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/dashboard", "/gamers/**", "/steam/**", "/leaderboards")
                .excludePathPatterns("/login", "/register", "/", "/home", "/api/**");
    }

    static class AuthInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Object handler) throws Exception {

            HttpSession session = request.getSession(false);

            // Разрешаем доступ к публичным страницам
            String path = request.getRequestURI();
            if (path.equals("/") ||
                    path.equals("/login") ||
                    path.equals("/register") ||
                    path.startsWith("/api/") ||
                    path.startsWith("/css/") ||
                    path.startsWith("/js/") ||
                    path.startsWith("/images/")) {
                return true;
            }

            // Проверяем авторизацию для защищенных страниц
            if (session != null && session.getAttribute("authenticated") != null) {
                return true;
            }

            // Редирект на страницу входа для неавторизованных пользователей
            response.sendRedirect("/login");
            return false;
        }
    }
}
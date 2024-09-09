package com.RapidDataChat.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public HandlerInterceptor jwtInterceptor() {
        return new HandlerInterceptor() {
            private static final String JWT_SECRET = "your_secret_key_here";

            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String authorizationHeader = request.getHeader("Authorization");
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    String token = authorizationHeader.substring(7); // Remove "Bearer "
                    try {
                        JWT jwt = JWTUtil.parseToken(token);
                        jwt.setKey(JWT_SECRET.getBytes());
                        if (jwt.verify()) {
                            return true;
                        }
                    } catch (Exception e) {
                        // Token parsing or validation failed
                    }
                }
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized");
                return false;
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/login", "/auth/register",
                                     "/swagger-ui.html", "/swagger-ui/**",
                                     "/v3/api-docs/**",
                                     "/index.html", "/assets/**");
    }

}

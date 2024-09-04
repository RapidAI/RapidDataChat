package com.RapidDataChat.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 创建 CORS 配置对象
        CorsConfiguration config = new CorsConfiguration();

        // 允许跨域请求的域名
        // config.setAllowedOrigins(List.of("http://localhost:8080"));
        // 修改为允许所有域名
        config.setAllowedOriginPatterns(List.of("*"));

        // 是否允许 cookie
        config.setAllowCredentials(true);

        // 允许的 HTTP 方法
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 允许的 HTTP 请求头
        config.setAllowedHeaders(List.of("*"));

        // 预检请求的缓存时间（秒）
        config.setMaxAge(3600L);

        // 创建 URL 基于 CORS 配置的来源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 对所有路径应用 CORS 配置

        // 将 CORS 配置转换为过滤器
        return new CorsFilter(source);
    }
}

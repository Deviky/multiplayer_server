package com.cybersport.room.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/ws/**")
                .allowedOrigins("http://example.com") // Укажите ваши разрешенные источники
                .allowedMethods("GET", "POST")
                .allowCredentials(true); // Установите false, если учетные данные не используются
    }
}

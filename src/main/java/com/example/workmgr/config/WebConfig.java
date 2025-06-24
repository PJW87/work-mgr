// src/main/java/com/example/workmgr/config/WebConfig.java
package com.example.workmgr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // URL "/uploads/**" 이 들어오면 로컬 디스크 D:/files/폴더에서 찾아 응답
        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations("file:///D:/files/");
    }

}

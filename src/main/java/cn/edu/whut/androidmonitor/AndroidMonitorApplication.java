package cn.edu.whut.androidmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class AndroidMonitorApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AndroidMonitorApplication.class, args);
    }
    
    // -- Mvc configuration ---------------------------------------------------
    @Bean
    WebMvcConfigurer createWebMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("index");
                registry.addViewController("/ws").setViewName("wstest");
            }
            
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // 映射路径 '/static/'到classpath路径 :
                registry.addResourceHandler("/static/**")
                        .addResourceLocations("classpath:/static/");
                registry.addResourceHandler("/dist/**")
                        .addResourceLocations("classpath:/dist/");
            }
        };
    }
    
}
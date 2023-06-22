package cn.edu.whut.androidmonitor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@MapperScan("cn.edu.whut.androidmonitor.mapper")
@PropertySource("classpath:application-db.properties")
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
                registry.addViewController("/sign-up").setViewName("sign-up");
                registry.addViewController("/main").setViewName("main");
                registry.addViewController("/forget-password").setViewName("forget-password");
            }
            
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // 映射路径 '/static/'到classpath路径 :
                registry.addResourceHandler("/static/**")
                        .addResourceLocations("classpath:/static/");
            }
        };
    }
    
}
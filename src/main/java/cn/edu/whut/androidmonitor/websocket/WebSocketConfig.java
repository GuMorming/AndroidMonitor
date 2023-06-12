package cn.edu.whut.androidmonitor.websocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.websocket
 * @createTime : 2023/6/11 11:02
 * @Email : gumorming@163.com
 * @Description :
 */

@Configuration // 标识这是一个Springboot配置类
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private WebSocketHandler handler;
    @Autowired
    private WebSocketInterceptor interceptor;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry // Step 1 : 注册WebSocketHandler,用来处理WebSocket建立以及消息处理的类
                // Step 2 : 注册WebSocket地址,并附带了{INFO}参数,用来注册的时候携带用户信息
                .addHandler(handler, "app-websocket/{INFO}")
                // 关闭跨域校验，方便本地调试
                .setAllowedOrigins("*")
                // Step 3 : 注册WebSocketInterceptor拦截器, 用来在客户端向服务器发起初次连接时,记录客户端拦截信息
                .addInterceptors(interceptor);
    }
    
    /**
     * 缓存调高,解决传输数据过大的问题
     * The decoded text message was too big for the output buffer and
     *
     * @return
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // 在此处设置bufferSize
        container.setMaxTextMessageBufferSize(512000); // 文本消息
        container.setMaxBinaryMessageBufferSize(512000); // 二进制消息
        container.setMaxSessionIdleTimeout(15 * 60000L);
        return container;
    }
    
}
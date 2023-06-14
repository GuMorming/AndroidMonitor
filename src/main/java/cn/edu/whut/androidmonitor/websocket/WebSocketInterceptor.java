package cn.edu.whut.androidmonitor.websocket;


import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.websocket
 * @createTime : 2023/6/11 11:01
 * @Email : gumorming@163.com
 * @Description :
 */

/**
 * 用来拦截客户端第一次连接服务端时的请求,即握手前后
 */
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {
    
    /**
     * 握手前, 为每个session分配一个uid
     *
     * @param serverHttpRequest
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        System.out.println("握手开始");
//        if (serverHttpRequest instanceof ServletServerHttpRequest) {
//            ServletServerHttpRequest request = (ServletServerHttpRequest) serverHttpRequest;
//            HttpSession session = request.getServletRequest().getSession();
//            // 以当前系统时间作为session的uid
//            String uid = new SimpleDateFormat("yyyyMMddHHmmssSSSS").format(System.currentTimeMillis());
//            System.out.println(uid);
//            session.setAttribute(KEY_UID, uid);
//        }
        return true;
    }
    
    /**
     * 握手后
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param exception
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        System.out.println("握手完成");
    }
    
}
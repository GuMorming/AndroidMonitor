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
 * 用来拦截客户端第一次连接服务端时的请求，
 * 即客户端连接/webSocket/{INFO}时，
 * 可以获取到对应INFO的信息
 */
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {
    
    /**
     * 握手前
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        System.out.println("握手开始");
        // 获得请求参数
//        Map<String, String> paramMap = HttpUtil.decodeParamMap(request.getURI().getQuery(), CharsetUtil.CHARSET_UTF_8);
//        String uid = paramMap.get("token");
//        if (StrUtil.isNotBlank(uid)) {
//            // 放入属性域
//            attributes.put("token", uid);
//            System.out.println("用户 token " + uid + " 握手成功！");
//            return true;
//        }
//        System.out.println("用户登录已失效");

//        if (request instanceof ServletServerHttpRequest) {
//            String INFO = request.getURI().getPath().split("INFO=")[1];
//            if (INFO != null && INFO.length() > 0) {
//                JSONObject jsonObject = new JSONObject(INFO);
//                String command = jsonObject.getString("command");
//                if (command != null && MessageKey.ENTER_COMMAND.equals(command)) {
//                    System.out.println("当前session的ID=" + jsonObject.getString("name"));
//                    ServletServerHttpRequest request = (ServletServerHttpRequest) serverHttpRequest;
//                    HttpSession session = request.getServletRequest().getSession();
//                    map.put(MessageKey.KEY_WEBSOCKET_USERNAME, jsonObject.getString("name"));
//                    map.put(MessageKey.KEY_ROOM_ID, jsonObject.getString("roomId"));
//                }
//            }
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
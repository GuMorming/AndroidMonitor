package cn.edu.whut.androidmonitor.websocket;


import cn.edu.whut.androidmonitor.constants.MessageKey;
import cn.edu.whut.androidmonitor.entity.ClientMessage;
import cn.edu.whut.androidmonitor.entity.MonitorMessage;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static cn.edu.whut.androidmonitor.constants.MessageKey.*;
import static cn.edu.whut.androidmonitor.constants.POOL.POOL_NAME_CLIENT;
import static cn.edu.whut.androidmonitor.constants.POOL.POOL_NAME_Monitor;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.websocket
 * @createTime : 2023/6/11 11:00
 * @Email : gumorming@163.com
 * @Description :
 */

/**
 * 对WebSocket事件进行处理
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    // 监控池-监控端-会话Session 及 被控池-被控端-会话Session,
    // 使用双层Map实现对应关系
    private static final Map<String, Map<String, WebSocketSession>> wsSessionMap = new HashMap<>(2);
    
    /**
     * socket 建立成功事件
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("ws建立成功");
        // 将URL中的信息拆分
        String INFO = session.getUri().getPath().split("INFO=")[1];
        System.out.println(INFO);
        
        if (INFO != null && INFO.length() > 0) {
            // 将信息转换为JSON格式并分别获取
            JSONObject jsonObject = new JSONObject(INFO);
            // 命令
            String command = jsonObject.getString(KEY_COMMAND);
            // 连接用户名
            String username = jsonObject.getString(KEY_USERNAME);
            // 连接用户类型
            String poolName = jsonObject.getString(KEY_POOL_NAME);
            // 若收到连接命令
            if (MessageKey.COMMAND_CONNECT.equals(command)) {
                // 获取对应类型的连接池并存入
                Map<String, WebSocketSession> poolSessionMap = wsSessionMap.get(poolName);
                if (poolSessionMap == null) {
                    poolSessionMap = new HashMap<>(3);
                    wsSessionMap.put(poolName, poolSessionMap);
                }
                poolSessionMap.put(username, session);
                
                // 若连接类型为 "client", 通知给所有 "monitor"类型用户
                if (POOL_NAME_CLIENT.equals(poolName)) {
                    Map<String, WebSocketSession> monitorSessionMap = wsSessionMap.get(POOL_NAME_Monitor);
                    if (monitorSessionMap != null) {
                        for (Map.Entry<String, WebSocketSession> entry : monitorSessionMap.entrySet()) {
                            WebSocketSession monitorSession = entry.getValue();
                            monitorSession.sendMessage(new TextMessage("当前被控端" + poolSessionMap.size() + "个"));
                        }
                    }
                    System.out.println("当前被控端个数：" + poolSessionMap.size());
                }
                System.out.println(session.getUri());
            }
        }
    }
    
    /**
     * 接收消息事件
     *
     * @param session
     * @param webSocketMessage
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
        // 获得客户端传来的消息
        // 从HTTPHeader中获取连接池类型
        String poolName = getPoolNameFromSessionHeaders(session.getHandshakeHeaders());
        // 若为android-"client"
        if (POOL_NAME_CLIENT.equals(poolName)) {
            // 将Android-Client发来的消息转为json格式
            JSONObject jsonobject = new JSONObject(webSocketMessage.getPayload().toString());
            ClientMessage message = new ClientMessage(jsonobject.toString());
            String command = message.getCommand();
            String username = getUsernameFromSessionHeaders(session.getHandshakeHeaders());
            if (command != null) {
                switch (command) {
                    // 问候消息
                    case COMMAND_GREETING -> System.out.println("Client-" + username + "发送:" + message.getData());
                    // 截图数据
                    case COMMAND_SCREENSHOT -> {
                        Map<String, WebSocketSession> monitorSessionMap = wsSessionMap.get(POOL_NAME_Monitor);
                        if (monitorSessionMap == null) {
                            System.out.println("暂无监控端在线!");
                            return;
                        } else {
                            for (Map.Entry<String, WebSocketSession> entry : monitorSessionMap.entrySet()) {
                                WebSocketSession monitorSession = entry.getValue();
                                monitorSession.sendMessage(new TextMessage(message.toJson().toString()));
                            }
                        }
                        
                    }
                }
            } else {
                System.out.println("Client发送Command为NULL");
            }
        }
        // 若为web-"monitor"
        else {
            // 将web-monitor发来的消息转为json格式
            JSONObject jsonobject = new JSONObject(webSocketMessage.getPayload().toString());
            MonitorMessage message = new MonitorMessage(jsonobject.toString());
            System.out.println(jsonobject);
            System.out.println(message.getData() + ":来自" + message.getUsername() + "的消息");
            if (message.getUsername() != null && message.getCommand() != null) {
                switch (message.getCommand()) {
                    case COMMAND_GREETING:
                        sendGreetingToClients(message);
                        break;
                    
                    default:
                        break;
                }
            }
        }
        
        session.sendMessage(new TextMessage("server 发送: " + LocalDateTime.now()));
    }
    
    /**
     * Web-Monitor发送给Android-Client问候消息
     *
     * @param message
     */
    public void sendGreetingToClients(MonitorMessage message) throws IOException {
        Map<String, WebSocketSession> clientSessionMap = wsSessionMap.get(POOL_NAME_CLIENT);
        if (clientSessionMap == null) {
            System.out.println("暂无被控端在线!");
        } else {
            for (Map.Entry<String, WebSocketSession> entry : clientSessionMap.entrySet()) {
                WebSocketSession session = entry.getValue();
                session.sendMessage(new TextMessage(message.toJson().toString()));
            }
        }
    }
    
    
    /**
     * socket 断开连接时
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        String poolName = getPoolNameFromSessionHeaders(session.getHandshakeHeaders());
//        Map<String, WebSocketSession> map = wsSessionMap.get(poolName);
//        if (map != null) {
//            map.remove(getUsernameFromSessionHeaders(session.getHandshakeHeaders()));
//        }
//        if (poolName.equals(POOL_NAME_CLIENT)) {
//
//        }
        
        System.out.println("ws断开");
    }
    
    /**
     * 仅Android-Client, 从sessionHeader中获取连接池类型
     *
     * @param headers
     * @return String poolName
     */
    private String getPoolNameFromSessionHeaders(HttpHeaders headers) {
        try {
            return headers.getFirst(KEY_POOL_NAME);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 仅AndroidClient, 从sessionHeader中获取用户名称
     *
     * @param headers
     * @return String username
     */
    private String getUsernameFromSessionHeaders(HttpHeaders headers) {
        try {
            return headers.getFirst(KEY_USERNAME);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 仅Android-Client, 从sessionHeader中获取Command命令
     *
     * @param headers
     * @return String command
     */
    private String getCommandFromSessionHeaders(HttpHeaders headers) {
        try {
            return headers.getFirst(KEY_COMMAND);
        } catch (Exception e) {
            return null;
        }
    }
    
    
}
package cn.edu.whut.androidmonitor.websocket;


import cn.edu.whut.androidmonitor.constants.MessageKey;
import cn.edu.whut.androidmonitor.entity.ClientMessage;
import cn.edu.whut.androidmonitor.entity.MonitorMessage;
import cn.edu.whut.androidmonitor.entity.ScreenShot;
import cn.edu.whut.androidmonitor.service.ScreenshotService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static cn.edu.whut.androidmonitor.constants.MessageKey.*;
import static cn.edu.whut.androidmonitor.constants.POOL.POOL_NAME_CLIENT;
import static cn.edu.whut.androidmonitor.constants.POOL.POOL_NAME_MONITOR;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.websocket
 * @createTime : 2023/6/11 11:00
 * @Email : gumorming@163.com
 * @Description : WebSocket事件处理器
 */

/**
 * 对WebSocket事件进行处理
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    // 监控池-监控端-会话Session 及 被控池-被控端-会话Session, 因此初始化为2
    // 使用双层Map实现对应关系
    private static final Map<String, Map<String, WebSocketSession>> wsSessionMap = new HashMap<>(2);
    // 监控端SessionId-被控端SessionId, 保存选择关系
    private static final Map<String, String> selcetSessionMap = new HashMap<>(2);
    // 被控端Id-对应监控端Id, 保存图像传输关系
    private static final Map<String, List<String>> screenshotSessionMap = new HashMap<>(2);
    
    @Autowired
    ScreenshotService screenshotService;
    
    /**
     * socket 建立成功事件
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("ws建立成功");
        // 将URL中的信息拆分, 连接消息的格式Web和Android客户端为统一格式
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
                poolSessionMap.put(session.getId(), session);
                
                // 若连接类型为 "client", 通知给所有 "monitor"类型用户
                if (POOL_NAME_CLIENT.equals(poolName)) {
                    Map<String, WebSocketSession> monitorSessionMap = wsSessionMap.get(POOL_NAME_MONITOR);
                    if (monitorSessionMap != null) {
                        jsonObject.put(KEY_DATA, session.getId());
                        MonitorMessage clientOnConnectMessage = new MonitorMessage(jsonObject.toString());
                        for (Map.Entry<String, WebSocketSession> entry : monitorSessionMap.entrySet()) {
                            WebSocketSession monitorSession = entry.getValue();
                            monitorSession.sendMessage(new TextMessage(clientOnConnectMessage.toJson().toString()));
                        }
                    }
                    System.out.println("当前被控端个数：" + poolSessionMap.size());
                }
                // 若连接类型为 "monitor", 将当前client池中用户信息推送过去
                else {
                    // 获取Client池
                    Map<String, WebSocketSession> clientSessionMap = wsSessionMap.get(POOL_NAME_CLIENT);
                    if (clientSessionMap != null) {
                        JSONObject pushJson = new JSONObject();
                        pushJson.put(KEY_COMMAND, COMMAND_PUSH);
                        pushJson.put(KEY_POOL_NAME, POOL_NAME_CLIENT);
                        for (Map.Entry<String, WebSocketSession> entry : clientSessionMap.entrySet()) {
                            WebSocketSession clientSession = entry.getValue();
                            String clientName = getUsernameFromSessionHeaders(clientSession.getHandshakeHeaders());
                            pushJson.put(KEY_USERNAME, clientName);
                            pushJson.put(KEY_DATA, clientSession.getId());
                            MonitorMessage pushMessage = new MonitorMessage(pushJson.toString());
                            session.sendMessage(new TextMessage(pushMessage.toJson().toString()));
                        }
                    } else {
                        System.out.println("暂无Android客户端在线");
                    }
                }
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
            // 打印输出
            if (!command.equals(COMMAND_DEVICE_INFO) && !command.equals(COMMAND_SCREENSHOT)) {
                System.out.println(jsonobject);
            }
            String username = getUsernameFromSessionHeaders(session.getHandshakeHeaders());
            switch (command) {
                // 问候消息
                case COMMAND_GREETING -> System.out.println("Client-" + username + ":" + message.getData());
                // 截图数据
                case COMMAND_SCREENSHOT -> getScreenShot(message, session.getId(), username);
                // 用户取消截图
                case COMMAND_SCREENSHOT_CANCEL -> cancelScreenshot(session.getId());
                // 总内存, 可用内存, CPU使用率, 网络状态, 网速, 电量
                case COMMAND_TOTAL_MEMORY, COMMAND_DEVICE_INFO -> {
                    // 获取Monitor池
                    Map<String, WebSocketSession> monitorSessionMap = wsSessionMap.get(POOL_NAME_MONITOR);
                    // 遍历选择关系,发给对应的Monitor
                    for (Map.Entry<String, String> entry : selcetSessionMap.entrySet()) {
                        String clientId = entry.getValue();
                        if (clientId.equals(session.getId())) {
                            WebSocketSession monitorSession = monitorSessionMap.get(entry.getKey());
                            monitorSession.sendMessage(new TextMessage(jsonobject.toString()));
                            break;
                        }
                    }
                }
            }
        }
        // 若为web-"monitor"
        else {
            // 将web-monitor发来的消息转为json格式
            JSONObject jsonobject = new JSONObject(webSocketMessage.getPayload().toString());
            MonitorMessage message = new MonitorMessage(jsonobject.toString());
            System.out.println(jsonobject);
            if (message.getUsername() != null && message.getCommand() != null) {
                switch (message.getCommand()) {
                    case COMMAND_GREETING -> sendGreetingToClients(message);
                    // 选择命令
                    case COMMAND_SELECT -> {
                        String clientSessionId = message.getData();
                        // 保存选择关系
                        selcetSessionMap.put(session.getId(), clientSessionId);
                        // 获取Client池
                        Map<String, WebSocketSession> clientSessionMap = wsSessionMap.get(POOL_NAME_CLIENT);
                        WebSocketSession clientSession = clientSessionMap.get(clientSessionId);
                        // 发送Select命令使Android-Client定时发送内存信息和CPU信息
                        clientSession.sendMessage(new TextMessage(message.toJson().toString()));
                    }
                    case COMMAND_SCREENSHOT -> {
                        // 保存传输关系
                        String clientSessionId = message.getData();
                        List<String> correspondMonitorList = screenshotSessionMap.get(clientSessionId);
                        if (correspondMonitorList == null) {
                            correspondMonitorList = new ArrayList<>();
                        }
                        correspondMonitorList.add(session.getId());
                        screenshotSessionMap.put(clientSessionId, correspondMonitorList);
                        startMonitoring(message);
                    }
                    case COMMAND_SCREENSHOT_STOP -> {
                        List<String> corrMonitorList = screenshotSessionMap.get(message.getData());
                        corrMonitorList.remove(session.getId());
                        screenshotSessionMap.put(message.getData(), corrMonitorList);
                        stopMonitoring(message);
                    }
                    // 返回回放设备列表
                    case COMMAND_GET_PLAYBACK_DEVICE -> {
                        List<String> UIDList = screenshotService.getPlayBackDeviceList();
                        JSONObject UIDJson = new JSONObject();
                        UIDJson.put(KEY_COMMAND, COMMAND_GET_PLAYBACK_DEVICE);
                        for (String UID : UIDList) {
                            UIDJson.put(KEY_DATA, UID);
                            session.sendMessage(new TextMessage(UIDJson.toString()));
                        }
                    }
                    // 根据选择的UID,返回回放列表
                    case COMMAND_GET_PLAYBACK_LIST -> {
                        List<ScreenShot> playBackList = screenshotService.findPlayListByUID(message.getData());
                        JSONObject playListJson = new JSONObject();
                        playListJson.put(KEY_COMMAND, COMMAND_GET_PLAYBACK_LIST);
                        for (ScreenShot screenShot : playBackList) {
                            playListJson.put("sessionId", screenShot.getSessionId());
                            playListJson.put("date", screenShot.getDate());
                            session.sendMessage(new TextMessage(playListJson.toString()));
                        }
                    }
                    // 根据选择的时间对应的SessionId, 返回图片Base64编码
                    case COMMAND_PLAYBACK -> {
                        List<String> base64ImageList = screenshotService.getPlayBackImage(message.getData());
                        JSONObject imageJson = new JSONObject();
                        imageJson.put("total", base64ImageList.size());
                        imageJson.put(KEY_COMMAND, COMMAND_PLAYBACK);
                        for (String base64Image : base64ImageList) {
                            imageJson.put(KEY_DATA, base64Image);
                            session.sendMessage(new TextMessage(imageJson.toString()));
                            // 防止传输过快, 肉眼反应不及
                            Thread.sleep(PLAYBACK_SPEED);
                        }
                    }
                    // 设定图片质量
                    case COMMAND_QUALITY -> {
                        // 获取所选的ClientSessionId
                        String clientSessionId = selcetSessionMap.get(session.getId());
                        // 获取Client池
                        Map<String, WebSocketSession> clientSessionMap = wsSessionMap.get(POOL_NAME_CLIENT);
                        WebSocketSession clientSession = clientSessionMap.get(clientSessionId);
                        clientSession.sendMessage(new TextMessage(message.toJson().toString()));
                    }
                }
            }
        }

//        session.sendMessage(new TextMessage("server: " + LocalDateTime.now()));
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
        System.out.println(":ws断开");
        // 获得客户端传来的消息
        // 从HTTPHeader中获取连接池类型
        String poolName = getPoolNameFromSessionHeaders(session.getHandshakeHeaders());
        System.out.println(poolName);
        // 若为android-"client"
        if (POOL_NAME_CLIENT.equals(poolName)) {
            //向所有 Monitor 推送离线通知
            Map<String, WebSocketSession> monitorSessionMap = wsSessionMap.get(POOL_NAME_MONITOR);
            if (monitorSessionMap != null) {
                JSONObject leaveJson = new JSONObject();
                leaveJson.put(KEY_COMMAND, COMMAND_LEAVE);
                leaveJson.put(KEY_DATA, session.getId());
                ClientMessage leaveMessage = new ClientMessage(leaveJson.toString());
                for (Map.Entry<String, WebSocketSession> entry : monitorSessionMap.entrySet()) {
                    WebSocketSession monitorSession = entry.getValue();
                    monitorSession.sendMessage(new TextMessage(leaveMessage.toJson().toString()));
                    System.out.println("Leave Id:" + session.getId());
                }
            } else {
                System.out.println("暂无监控端在线!");
            }
            //  从Map中移除
            Map<String, WebSocketSession> clientSessionMap = wsSessionMap.get(POOL_NAME_CLIENT);
            clientSessionMap.remove(session.getId());
        }
        // 若为 Monitor
        else {
            String sessionId = session.getId();
            Map<String, WebSocketSession> monitorSessionMap = wsSessionMap.get(POOL_NAME_MONITOR);
            monitorSessionMap.remove(sessionId);
            selcetSessionMap.remove(sessionId);
        }
    }
    
    /**
     * Web-Monitor向Android-Client发送截图命令
     *
     * @param message
     * @throws IOException
     */
    public void startMonitoring(MonitorMessage message) throws IOException {
        Map<String, WebSocketSession> clientSessionMap = wsSessionMap.get(POOL_NAME_CLIENT);
        if (clientSessionMap == null) {
            System.out.println("暂无被控端在线!");
        } else {
            String selectedId = message.getData();
            for (Map.Entry<String, WebSocketSession> entry : clientSessionMap.entrySet()) {
                WebSocketSession session = entry.getValue();
                if (session.getId().equals(selectedId)) {
                    if (session.isOpen()) {
                        // 发送命令
                        session.sendMessage(new TextMessage(message.toJson().toString()));
                    } else {
                        System.out.println("会话已关闭");
                    }
                } else {
                    System.out.println("未找到目标被控端");
                }
            }
        }
    }
    
    /**
     * Web-Monitor向Android-Client发送停止截图命令
     *
     * @param message
     * @throws IOException
     */
    public void stopMonitoring(MonitorMessage message) throws IOException {
        Map<String, WebSocketSession> clientSessionMap = wsSessionMap.get(POOL_NAME_CLIENT);
        if (clientSessionMap == null) {
            System.out.println("暂无被控端在线!");
        } else {
            String screenshotId = message.getData();
            for (Map.Entry<String, WebSocketSession> entry : clientSessionMap.entrySet()) {
                String clientId = entry.getKey();
                if (clientId.equals(screenshotId)) {
                    WebSocketSession session = entry.getValue();
                    session.sendMessage(new TextMessage(message.toJson().toString()));
                    break;
                }
            }
        }
    }
    
    /**
     * Android-client 取消截图
     *
     * @param clientSessionId -- 拒绝截图的Android-client Id
     */
    public void cancelScreenshot(String clientSessionId) throws IOException {
        List<String> correspondMonitorList = screenshotSessionMap.get(clientSessionId);
        if (correspondMonitorList == null || correspondMonitorList.isEmpty()) {
            System.out.println("无可拒绝");
        } else {
            String removeMonitorSessionId = null;
            JSONObject cancelJson = new JSONObject();
            cancelJson.put(KEY_COMMAND, COMMAND_SCREENSHOT_CANCEL);
            Map<String, WebSocketSession> monitorSessionMap = wsSessionMap.get(POOL_NAME_MONITOR);
            for (String corrMonitorSessionId : correspondMonitorList) {
                for (Map.Entry<String, WebSocketSession> entry : monitorSessionMap.entrySet()) {
                    String monitorSessionId = entry.getKey();
                    if (monitorSessionId.equals(corrMonitorSessionId)) {
                        WebSocketSession monitorSession = entry.getValue();
                        monitorSession.sendMessage(new TextMessage(cancelJson.toString()));
                        removeMonitorSessionId = monitorSessionId;
                        break;
                    }
                }
            }
            correspondMonitorList.remove(removeMonitorSessionId);
        }
    }
    
    /**
     * Web-Monitor发送给Android-Client问候消息
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
     * 从Android-client获取截图数据, 转发给对应Web-Monitor
     */
    public void getScreenShot(ClientMessage message, String clientSessionId, String username) throws IOException {
        List<String> corrMonitorList = screenshotSessionMap.get(clientSessionId);
        if (corrMonitorList == null) {
            System.out.println("无可转发!");
        } else {
            Map<String, WebSocketSession> monitorSessionMap = wsSessionMap.get(POOL_NAME_MONITOR);
            for (String corrMonitorId : corrMonitorList) {
                for (Map.Entry<String, WebSocketSession> entry : monitorSessionMap.entrySet()) {
                    String monitorId = entry.getKey();
                    if (corrMonitorId.equals(monitorId)) {
                        // 将收到的Base64编码图片存入实体对象中, 再保存到数据库
                        ScreenShot screenShot = new ScreenShot();
                        screenShot.setUID(username);
                        screenShot.setImgBase64(message.getData());
                        screenShot.setDate(new Timestamp(new Date().getTime()));
                        screenShot.setSessionId(clientSessionId);
                        screenshotService.insertScreenshot(screenShot);
                        // 找到对应的Monitor后转发
                        WebSocketSession monitorSession = entry.getValue();
                        monitorSession.sendMessage(new TextMessage(message.toJson().toString()));
                        break;
                    }
                }
            }
        }
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
     * @return String username
     */
    private String getUsernameFromSessionHeaders(HttpHeaders headers) {
        try {
            return headers.getFirst(KEY_USERNAME);
        } catch (Exception e) {
            return null;
        }
    }
    
    
}
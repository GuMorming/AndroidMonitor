package cn.edu.whut.androidmonitor.controller;

import cn.edu.whut.androidmonitor.entity.Greeting;
import cn.edu.whut.androidmonitor.entity.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.controller
 * @createTime : 2023/6/6 16:09
 * @Email : gumorming@163.com
 * @Description :
 */
@Controller
public class WsController {
    @MessageMapping("/hello") // 标识所有发送到“/hello”这个destination的消息，都会被路由到这个方法进行处理
    @SendTo("/topic/greetings") // 标识这个方法返回的结果，都会被发送到它指定的destination，“/topic/greetings”
    // 传入的参数Message为客户端发送过来的消息，是自动绑定的
    public Greeting greeting(HelloMessage message) throws InterruptedException {
//        System.out.println("React to hello");
//        Thread.sleep(400);
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
    
    @MessageMapping("/hello-msg-mapping")
    @SendToUser("/topic/client")
    public Greeting echoMessageMapping(String message) {
//        System.out.println("React to hello-msg-mapping");
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message) + "!");
    }
    
    
}
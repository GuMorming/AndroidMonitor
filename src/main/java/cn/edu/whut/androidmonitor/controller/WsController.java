package cn.edu.whut.androidmonitor.controller;

import cn.edu.whut.androidmonitor.entity.Greeting;
import cn.edu.whut.androidmonitor.entity.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws InterruptedException {
//        System.out.println("React to hello");
//        Thread.sleep(400);
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
    
    @MessageMapping("/hello-msg-mapping")
    @SendTo("/topic/greetings")
    public Greeting echoMessageMapping(String message) {
//        System.out.println("React to hello-msg-mapping");
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message) + "!");
    }
    
}
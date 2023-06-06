package cn.edu.whut.androidmonitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author : GuMorming
 * @Project : webSocketDemo
 * @Package : com.example.demo
 * @createTime : 2023/6/6 14:49
 * @Email : gumorming@163.com
 * @Description :
 */
@Controller
public class GreetingController {
    @GetMapping("/")
    public String index() {
//        User user = (User) session.getAttribute(KEY_USER);
//        Map<String, Object> model = new HashMap<>();
//        if (user != null) {
//            model.put("user", user);
//        }
        return "index";
    }
    
    @GetMapping("ws")
    public String wsdemo() {
        return "wsdemo";
    }
}
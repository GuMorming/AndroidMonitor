package cn.edu.whut.androidmonitor.controller;

import cn.edu.whut.androidmonitor.entity.User;
import cn.edu.whut.androidmonitor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.controller
 * @createTime : 2023/6/22 21:49
 * @Email : gumorming@163.com
 * @Description :
 */
@Controller
public class LoginController {
    @Autowired
    UserService userService;
    
    @PostMapping("login")
    public String login(User user) {
        if (userService.login(user)) {
            return "redirect:main";
        }
        return "error/user-not-exist";
    }
    
    @PostMapping("/register")
    public String register(User user) {
        if (userService.register(user)) {
            return "main";
        } else {
            return "sign-up";
        }
    }
}
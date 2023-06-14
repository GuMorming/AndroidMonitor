package cn.edu.whut.androidmonitor.controller;

import cn.edu.whut.androidmonitor.entity.HttpStatusEnum;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.controller
 * @createTime : 2023/6/13 10:56
 * @Email : gumorming@163.com
 * @Description :
 */
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    private final static String ERROR_PREFIX = "error_";
    private final static String ERROR_PATH = "/error";
    
    @RequestMapping(value = ERROR_PATH)
    public String errorJson(HttpServletRequest request) {
        Integer statusCode = Integer.valueOf(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString());
        String key = ERROR_PREFIX + statusCode;
        return HttpStatusEnum.valueOf(key).Page();
    }
    
    
}
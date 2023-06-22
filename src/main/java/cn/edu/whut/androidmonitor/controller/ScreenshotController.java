package cn.edu.whut.androidmonitor.controller;

import cn.edu.whut.androidmonitor.service.ScreenshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.controller
 * @createTime : 2023/6/22 11:35
 * @Email : gumorming@163.com
 * @Description :
 */
@Controller
public class ScreenshotController {
    @Autowired
    ScreenshotService screenshotService;
}
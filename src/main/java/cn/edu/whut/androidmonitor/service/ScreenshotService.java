package cn.edu.whut.androidmonitor.service;

import cn.edu.whut.androidmonitor.entity.ScreenShot;
import cn.edu.whut.androidmonitor.mapper.ScreenshotMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.service
 * @createTime : 2023/6/22 11:33
 * @Email : gumorming@163.com
 * @Description :
 */
@Service("screenshotService")
public class ScreenshotService {
    @Autowired
    ScreenshotMapper screenshotMapper;
    
    public void insertScreenshot(ScreenShot screenShot) {
        screenshotMapper.insertScreenshot(screenShot);
    }
    
    public List<ScreenShot> findPlayListByUID(String UID) {
        return screenshotMapper.findScreenshotByUID(UID);
    }
    
    public List<String> getPlayBackDeviceList() {
        return screenshotMapper.findAllUID();
    }
    
    public List<String> getPlayBackImage(String sessionId) {
        return screenshotMapper.findBase64ImageBySessionId(sessionId);
    }
}
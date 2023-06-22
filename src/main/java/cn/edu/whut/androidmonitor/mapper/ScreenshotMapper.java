package cn.edu.whut.androidmonitor.mapper;

import cn.edu.whut.androidmonitor.entity.ScreenShot;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.dao
 * @createTime : 2023/6/22 11:05
 * @Email : gumorming@163.com
 * @Description :
 */
@Repository
public interface ScreenshotMapper {
    /**
     * 保存截图Base64编码
     *
     * @param screenShot
     * @return
     */
    boolean insertScreenshot(ScreenShot screenShot);
    
    /**
     * 根据选择的UID,返回Date和SessionId
     *
     * @param UID
     * @return
     */
    List<ScreenShot> findScreenshotByUID(String UID);
    
    /**
     * 根据选择的SessionId, 返回相应的Base64图片编码
     *
     * @param sessionId
     * @return
     */
    List<String> findBase64ImageBySessionId(String sessionId);
    
    /**
     * 返回表中所有UID
     *
     * @return
     */
    List<String> findAllUID();
}
package cn.edu.whut.androidmonitor.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.entity
 * @createTime : 2023/6/22 10:30
 * @Email : gumorming@163.com
 * @Description :
 */

@Data
public class ScreenShot {
    // 主键,自增获得
    private Long id;
    // Android设备UID
    private String UID;
    // 此次传输的WebSocketSessionId
    private String sessionId;
    // 图片Base64编码
    private String imgBase64;
    // 时间戳
    private Timestamp date;
}
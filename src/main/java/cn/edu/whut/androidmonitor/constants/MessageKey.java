package cn.edu.whut.androidmonitor.constants;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.constants
 * @createTime : 2023/6/11 14:27
 * @Email : gumorming@163.com
 * @Description :
 */

public class MessageKey {
    // 连接命令
    public static final String COMMAND_CONNECT = "connect";
    public static final String COMMAND_SELECT = "select";
    // 推送命令
    public static final String COMMAND_PUSH = "push";
    // 问候命令
    public static final String COMMAND_GREETING = "greeting";
    // 内存相关命令
    public static final String COMMAND_TOTAL_MEMORY = "totalMemory";
    public static final String COMMAND_DEVICE_INFO = "deviceInfo";
    // 屏幕截图命令
    public static final String COMMAND_SCREENSHOT = "screenshot";
    public static final String COMMAND_SCREENSHOT_STOP = "screenshot_stop";
    public static final String COMMAND_SCREENSHOT_CANCEL = "screenshot_cancel";
    public static final String COMMAND_GET_PLAYBACK_DEVICE = "playBackDevices";
    public static final String COMMAND_GET_PLAYBACK_LIST = "playBackList";
    public static final String COMMAND_PLAYBACK = "playBack";
    public static final String COMMAND_QUALITY = "quality";
    public static final int PLAYBACK_SPEED = 40;
    // Android离线推送命令
    public static final String COMMAND_LEAVE = "leave";
    
    // keywords
    public static final String KEY_COMMAND = "command";
    public static final String KEY_POOL_NAME = "poolName";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_DATA = "data";
    public static final String KEY_ID = "id";
    
}
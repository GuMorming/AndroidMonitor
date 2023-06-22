// 基地址
const base = "localhost:8080/";

/* WebSocket ReadyState https://developer.mozilla.org/zh-CN/docs/Web/API/WebSocket/readyState */
const WEBSOCKET_CONNECTING = 0; // 正在链接中
const WEBSOCKET_OPEN = 1; // 已经链接并且可以通讯
const WEBSOCKET_CLOSING = 2; // 连接正在关闭
const WEBSOCKET_CLOSED = 3; // 连接已关闭或者没有链接成功

// 连接池名称
const POOL_NAME = "monitor";

// Android Icon
const ANDROID_ICON = "<img src=\"/img/brands/android.svg\" alt=\"Tabler\" class=\"navbar-brand-image\">";
// 状态绿点
const GREEN_DOT_STATUS_SPAN = "<span class=\"clientStatus status-dot status-dot-animated status-green ms-auto \"></span>";

// 图表渐变颜色列表
const GRADIENT_COLOR_LIST = {
    first: ["#F179C4", "#26AEFB", "#CDAD92", "#ED65BA", "#989FBB", "#E86A6A", "#6718CF"],
    second: ["#E33AA3", "#056FAB", "#FFAA62", "#E33AA3", "#28B1FF", "#FFAA62", "#F47384"]
};

/* 字符串常量 */
const CONNECT_TO_SERVER_STR = "连接服务器";
const DISCONNECTING_STR = "断开中...";
const CONNECTING_STR = "尝试连接中...";
const DISCONNECT_FROM_SERVER_STR = "断开";

const SCREENSHOT_STR = "监控桌面";
const SCREENSHOT_CONNECTING_STR = "申请权限中...";
const SCREENSHOT_STOP_STR = "停止监控";

const ERROR_HINT_STR = "唔.. 你遇到了一个错误";
const ERROR_404_STR = "很抱歉, 你要找的页面不存在";
const ERROR_500_STR = "很抱歉, 我们的服务器出差了";
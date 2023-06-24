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

const CONNECT_SUCCESS_ALERT_H4 = "<h4 class=\"text-nowrap alert-title\">成功连接服务器!</h4>";
const DISCONNECT_ALERT_H4 = "<h4 class=\"text-nowrap alert-title\">与服务器断开!</h4>";

/* Alert */
const CONNECT_SUCCESS = 0;
const DISCONNECT = 1;
// https://tabler-icons.io/i/check
const CHECK_SVG = "<svg xmlns=\"http://www.w3.org/2000/svg\" class=\"icon icon-tabler icon-tabler-check\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" stroke-width=\"1\" stroke=\"currentColor\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\">\n" +
    "   <path stroke=\"none\" d=\"M0 0h24v24H0z\" fill=\"none\"></path>\n" +
    "   <path d=\"M5 12l5 5l10 -10\"></path>\n" +
    "</svg>"
// https://tabler-icons.io/i/alert-circle
const ALERT_CIRCLE_SVG = "<svg xmlns=\"http://www.w3.org/2000/svg\" class=\"icon icon-tabler icon-tabler-alert-circle\" width=\"24\" height=\"24\" viewBox=\"0 0 24 24\" stroke-width=\"1\" stroke=\"currentColor\" fill=\"none\" stroke-linecap=\"round\" stroke-linejoin=\"round\">\n" +
    "   <path stroke=\"none\" d=\"M0 0h24v24H0z\" fill=\"none\"></path>\n" +
    "   <path d=\"M3 12a9 9 0 1 0 18 0a9 9 0 0 0 -18 0\"></path>\n" +
    "   <path d=\"M12 8v4\"></path>\n" +
    "   <path d=\"M12 16h.01\"></path>\n" +
    "</svg>";

const ALERT_TEMPLATE = "<div class=\"list-group-item alert-group-item \">\n" +
    "                                            <div class=\"row align-items-center\">\n" +
    "                                                <div class=\"alert alert-dismissible\" role=\"alert\">\n" +
    "                                                    <div class=\"d-flex\">\n" +
    "                                                        <div class=\"alertIcon\"></div>\n" +
    "                                                        <div class=\"alertContent\"></div>\n" +
    "                                                    </div>\n" +
    "                                                    <a class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"close\"></a>\n" +
    "                                                </div>\n" +
    "                                            </div>\n" +
    "                                        </div>";
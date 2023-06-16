let ws;
let SocketCreated = false;
let isScreenShot = false;
const poolName = "monitor"; // 连接池名称
let username;
let selectId;
/* 元素绑定 */
// 通知
let bell = $(".badge").hide();
// Web客户端连接
let connectBtn = $("#connectBtn");
let connectSpinner = $("#connectSpinner");
connectSpinner.hide()
let connectContent = $("#connectContent");
let plugConnectedSvg = $("#plug-connected-svg");
let plugOffSvg = $("#plug-off-svg");
plugOffSvg.hide();
const connectToServerStr = "连接服务器";
const disconnectingStr = "断开中...";
const connectingStr = "尝试连接中...";
const disconnectFromServerStr = "断开";
// Android客户端上线
let clientNumSpan = $("#clientNum");
let clientNum = 0;
let clientList = $("#clientList");
const clientStatusSpan = "<span class=\"clientStatus status status-dot-animated bg-green ms-auto\"></span>"; // 状态绿点
/* Android客户端数据区 */
// 内存水球图
const memoryChart = echarts.init(document.getElementById("chart-memory-available"));
let memory;
let memoryAvailable;
let memoryProportion = 0;
// Android客户端图像区
let clientDesktop = $("#client-desktop");
let screenShotBtn = $("#screenshotBtn");
let screenShotSpinner = $("#screenshotSpinner");
screenShotSpinner.hide();
let screenShotContent = $("#screenshotContent");
const screenShotStr = "监控桌面";
const screenShotConnectingStr = "申请权限中...";
const screenShotStopStr = "停止监控";
let screenshotId; // 监控桌面中的Android端ID

//------------------------------------------------------------------------------

/**
 * 连接webSocket与断开
 */
function toggleConnectionClicked() {
    if (SocketCreated && (ws.readyState === 0 || ws.readyState === 1)) {
        SocketCreated = false;
        var msg = JSON.stringify({
            'command': 'leave', 'poolName': poolName, 'username': username,
            'info': '离开房间'
        });
        ws.send(msg);
        plugConnectedSvg.hide();
        plugOffSvg.show();
        connectSpinner.show();
        connectContent.text(disconnectingStr);
        ws.close();
    } else {
        console.log("准备连接到服务器 ...");
        username = document.getElementById("username").innerHTML;
        try {
            if ("WebSocket" in window) {
                plugConnectedSvg.hide();
                connectSpinner.show();
                connectContent.html(connectingStr);
                ws = new WebSocket(
                    'ws://localhost:8080/app-websocket/INFO={"command":"connect","username":"' + username + '","poolName":"' + poolName + '"}');
            }
            SocketCreated = true;
        } catch (ex) {
            console.log(ex, "ERROR");
            return;
        }

        ws.onopen = WSonOpen;
        ws.onmessage = WSonMessage;
        ws.onclose = WSonClose;
        ws.onerror = WSonError;
    }
}

function toggleScreenshot() {
    if (isScreenShot) {
        var msg = JSON.stringify({
            'command': 'screenshot_stop',
            'poolName': poolName,
            'username': username,
            'data': screenshotId
        });
        ws.send(msg);
        screenShotBtn.attr("class", "btn btn-success");
        screenShotContent.html(screenShotStr);
        clientDesktop.attr("src", "static/brands/android.svg");
        isScreenShot = false;
    } else {
        screenshotId = selectId;
        if (screenshotId === null) {
            alert(screenshotId + "为空")
            return;
        } else {
            const msg = JSON.stringify({
                'command': 'screenshot'
                , 'poolName': poolName
                , 'username': username
                , 'data': screenshotId
            })
            screenShotSpinner.show();
            screenShotContent.html(screenShotConnectingStr);
            ws.send(msg);
            isScreenShot = true;
        }
    }
}

function onClientItemSelected(selectedItem) {
    if (isScreenShot) {
        alert("isScreenShot:" + isScreenShot);
    } else {
        // 激活监控桌面按钮
        screenShotBtn.removeClass("disabled");
        // 清除所有active类
        $(".client-list-item").removeClass("active");
        // 清除所有绿点
        $(".clientStatus").remove();
        item = $(selectedItem);
        item.parent().remove(".classStatus").remove("active");
        item.addClass("active");
        item.append(clientStatusSpan);
        selectId = item.attr("id");
    }
}


function WSonOpen() {
    /* 修改连接按钮 */
    connectSpinner.hide();
    plugOffSvg.show();
    connectBtn.attr("class", "btn btn-outline-danger")
    connectContent.html(disconnectFromServerStr);
}

function WSonMessage(event) {
    let data = event.data;
    let message = JSON.parse(data);
    let command = message.command;

    switch (command) {
        case "connect" :
            onConnectMessage(message);
            break;
        case "push":
            onConnectMessage(message);
            break;
        case "screenshot":
            onReceiveBitmap(message);
            break;
        case "screenshot_cancel":
            onScreenshotCancel();
            break;
        case "leave":
            onClientLeave(message);
            break;
        default:
            break;
    }

}

function WSonClose() {
    // 连接按钮
    plugOffSvg.hide();
    connectSpinner.hide();
    plugConnectedSvg.show();
    connectBtn.attr("class", "btn btn-primary");
    connectContent.html(connectToServerStr);
    // Android ClientList清空
    clientNum = 0;
    clientNumSpan.html(clientNum);
    $(".client-list-item").remove();
    // 图像区
    isScreenShot = false;
    selectId = null;
    screenshotId = null;
    clientDesktop.attr("src", "static/brands/android.svg");
    screenShotContent.html(screenShotStr);
    screenShotBtn.attr("class", "btn btn-success");
    screenShotBtn.addClass("disabled")
}

function WSonError() {
    console.log("远程连接中断。", "ERROR");
    // 图像区
    clientDesktop.attr("src", "static/brands/android.svg");
    screenShotContent.html(screenShotStr);
    screenShotBtn.attr("class", "btn btn-success");
    screenShotBtn.addClass("disabled")
    isScreenShot = false;
    selectId = null;
    screenshotId = null;
}

function onConnectMessage(message) {
    // 接收客户端数量
    clientNum++;
    clientNumSpan.html(clientNum);
    // 新增android-client节点
    const id = message.data
    const username = message.username;
    const androidIcon = "<img src=\"/static/brands/android.svg\" alt=\"Tabler\" class=\"navbar-brand-image\">";
    const clientNameSpan = "<span>" + username + "</span>";
    let dropItem = "<a id=\"" + id + "\" class=\"dropdown-item client-list-item\" onclick=\"onClientItemSelected(this" + ")\" href=\"#\">" + androidIcon + clientNameSpan + "</a>";

    clientList.append(dropItem);
}

// 收到取消命令,图像区重置
function onScreenshotCancel() {
    isScreenShot = false;

    clientDesktop.attr("src", "static/brands/android.svg");
    screenShotContent.html(screenShotStr);
    screenShotBtn.attr("class", "btn btn-success");
    screenShotSpinner.hide();
}

function onClientLeave(message) {
    const id = message.data;
    // dropitem节点除去
    $("#" + id).remove();
    // 客户端数量
    clientNum--;
    clientNumSpan.html(clientNum);
    // 图像区重置
    if (id === selectId) {
        isScreenShot = false;
        selectId = null;
        screenshotId = null;

        screenShotBtn.addClass("disabled");
        clientDesktop.attr("src", "static/brands/android.svg");
        screenShotContent.html(screenShotStr);
        screenShotBtn.attr("class", "btn btn-success");
    }
}

function onReceiveBitmap(message) {
    url = "data:image/png;base64," + message.data;
    clientDesktop.attr("src", url);
    screenShotSpinner.hide();
    screenShotContent.html(screenShotStopStr);
    screenShotBtn.attr("class", "btn btn-outline-danger");
}
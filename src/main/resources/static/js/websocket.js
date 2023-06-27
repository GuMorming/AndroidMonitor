let ws;
let SocketCreated = false;
let username;
let selectId = null;
let selectUID = null;
/* 元素绑定 */
// 通知
let bellIcon = $("#bell");
let badge = $(".badge");
let alertGroup = $("#alertGroup");

// Web客户端连接
let connectBtn = $("#connectBtn");
let connectSpinner = $("#connectSpinner");
let connectContent = $("#connectContent");
let plugConnectedSvg = $("#plug-connected-svg");
let plugOffSvg = $("#plug-off-svg");

// Android客户端上线
let clientNumSpan = $("#clientNum");
let clientNum = 0;
let clientList = $("#clientList");

/* Android客户端数据区 */
// 内存水球图
let memoryChart;
let memoryChartOption;
let totalMemory;
let availMemory;
let memoryProportion;

// CPU使用率折线图
let cpuUsageChart;
let cpuUsageChartOption;
let cpuChartData;

// 电量-电池图
let batteryChart;
let batteryChartOption;
let batteryData;

// 网络-网络状态
let network = $("#network");
// 网络-网速折线图
let netSpeedChart;
let netSpeedChartOption;
let netSpeedData;

/* 监控区 */
let isScreenShot = false;
let clientDesktop = $("#client-desktop");
let screenShotBtn = $("#screenshotBtn");
let screenShotSpinner = $("#screenshotSpinner");
let screenShotContent = $("#screenshotContent");
let screenshotId; // 监控桌面中的Android端ID
/* 回放区 */
let playBackDeviceList = $("#playBackDeviceList");
let playBackDeviceNumSpan = $("#playBackDeviceNum");
let playBackDeviceNum;
let playBackList = $("#playBackList");
let playBackImageData;
let playBackDesktop = $("#playback-desktop");
let totalPlayBackImageNum;
let countImage;
let progressBar = $("#progressBar");
let refreshDeviceBtn = $("#refreshDeviceBtn");
let replayBtn = $("#replayBtn");
let replayImageNum;

//------------------------------------------------------------------------------
/**
 * 初始化连接区
 */
function initConnectionSector() {
    // 隐藏旋转进度
    connectSpinner.hide();
    // 隐藏断开SVG; 显示连接SVG
    plugOffSvg.hide();
    plugConnectedSvg.show();
    // 链接按钮内容
    connectContent.html(CONNECT_TO_SERVER_STR);
    // 连接按钮样式
    connectBtn.attr("class", "btn btn-primary");
    // Android ClientList清空
    clientNum = 0;
    clientNumSpan.html(clientNum);
    $(".client-list-item").remove();
}

/**
 * 初始化数据监控区
 */
function initDeviceInfoSector() {
    // 内存水球图
    memoryChart = echarts.init(document.getElementById("chart-memory-available"));
    memoryChartOption = {
        series: [{
            type: "liquidFill",
            name: "可用内存",
            data: [0],
            radius: 125,
            animationDuration: 0,
            animationDurationUpdate: 0,
            label: {
                fontSize: 16
            },
            outline: {
                show: false,
            },
        }],
        tooltip: {
            show: true,
        },
    };
    memoryChart.setOption(memoryChartOption, true);
    memoryProportion = 0;

    // CPU使用率折线图
    cpuUsageChart = echarts.init(document.getElementById("chart-cpu-usage"));
    cpuUsageChartOption = {
        series: [{
            name: 'CPU利用率',    // 系列名称，用于tooltip的显示，legend 的图例筛选，在 setOption 更新数据和配置项时用于指定对应的系列。
            type: 'line',
            showSymbol: false,    // 是否显示 symbol, 如果 false 则只有在 tooltip hover 的时候显示。
            data: [],    // 系列中的数据内容数组
            areaStyle: {},
        }],
        tooltip: {    // 提示框组件
            trigger: 'axis',    // 触发类型（axis： 坐标轴触发）
            formatter: function (params) {    // 提示框浮层内容格式器
                params = params[0];
                return params.axisValueLabel + '<br />' + params.marker
                    + ' ' + params.seriesName
                    + '<span style="font-weight: bold;float: right;">' + params.value[1] + '%</span>';
            },
            axisPointer: {    // 坐标轴指示器配置项
                animation: true     // 是否开启动画
            }
        },
        toolbox: {    // 工具栏
            feature: {    // 各工具配置项
                saveAsImage: {
                    name: 'CPUUsage' + new Date().valueOf(),
                    type: 'png',
                },    // 保存为图片
                /*
                // dataView: {    // 数据视图工具，可以展现当前图表所用的数据，编辑后可以动态更新
                //     show: true,    // 是否显示该工具
                //     optionToContent: function (opt) {    // 自定义 dataView 展现函数，用以取代默认的 textarea 使用更丰富的数据编辑。
                //         var data = opt.series[0].data;
                //         var table = '<table border=1 cellspacing=0 cellpadding=5><tbody><tr>'
                //             + '<td align="center">时间</td>'
                //             + '<td align="center">' + opt.series[0].name + '</td>'
                //             + '</tr>';
                //         for (var i = 0, l = data.length; i < l; i++) {
                //             table += '<tr>'
                //                 + '<td align="center">' + data[i].value[0] + '</td>'
                //                 + '<td align="center">' + data[i].value[1] + '%</td>'
                //                 + '</tr>';
                //         }
                //         table += '</tbody></table>';
                //         return table;
                //     }
                // }
                 */
            }
        },
        xAxis: {    // 直角坐标系 grid 中的 x 轴
            type: 'time',    // 坐标轴类型（time: 时间轴，适用于连续的时序数据，与数值轴相比时间轴带有时间的格式化，在刻度计算上也有所不同，例如会根据跨度的范围来决定使用月，星期，日还是小时范围的刻度。）
            splitNumber: 5,
            minInterval: 1,
            splitLine: {    // 坐标轴在 grid 区域中的分隔线
                show: true // 是否显示分隔线。默认数值轴显示，类目轴不显示。
            }
        },
        yAxis: {    // 直角坐标系 grid 中的 y 轴
            type: 'value',    // 坐标轴类型（value: 数值轴，适用于连续数据。）
            boundaryGap: [0, '100%'],    // 坐标轴两边留白策略
            min: 0,        // 坐标轴刻度最小值
            max: 100,    // 坐标轴刻度最大值
            splitLine: {    // 坐标轴在 grid 区域中的分隔线
                show: true // 是否显示分隔线。默认数值轴显示，类目轴不显示。
            },
            axisLabel: {
                formatter: '{value} %'
            },
        },
    };
    cpuUsageChart.setOption(cpuUsageChartOption);
    cpuChartData = [];

    // 电池
    batteryChart = echarts.init(document.getElementById("chart-battery-available")); //初始化chart
    batteryChartOption = {
        grid: [{ //设置边距
            // left: 30,
            bottom: 30,
            top: 10,
            // right: 10
        }],
        yAxis: { //Y轴配置
            type: 'value',
            show: true,
            axisLine: {
                show: false,
                lineStyle: { //隐藏Y轴
                    opacity: 0
                }
            },
            axisTick: { //隐藏刻度
                show: false,
            },
            splitLine: { //隐藏刻度
                show: true,
            },
            axisLabel: { //Y轴文字
                show: true,
                // color: '#fff',
                fontSize: 10,
                formatter: '{value} %'
            },
            boundaryGap: [0, '100%'],    // 坐标轴两边留白策略
            min: 0,        // 坐标轴刻度最小值
            max: 100,    // 坐标轴刻度最大值
        },
        xAxis: { //X轴配置
            type: 'time',
            // data: [getTime(new Date())],
            show: true,
            axisLabel: { //X轴文字样式
                color: '#a9aabc',
                // fontSize: 12,
                // padding: [10, 0, 0, 0]
            },
            axisLine: {
                show: false
            },
            axisTick: {
                show: false,
            }
        },
        series: [{
            name: '',
            type: 'pictorialBar', // 设置类型为象形柱状图
            symbol: 'roundRect', // 图形元素类型，带圆角的矩形
            barWidth: '11%', // 柱图宽度
            barMaxWidth: '20%', // 最大宽度
            symbolMargin: '3', // 图形元素垂直间隔
            animationDelay: (dataIndex, params) => { //每个图形动画持续时间
                return params.index * 60;
            },
            itemStyle: {
                normal: {
                    color: params => { //图形渐变颜色方法，四个数字分别代表，右，下，左，上，offset表示0%到100%
                        return new echarts.graphic.LinearGradient(
                            1, 1, 0, 0, [{ // 0%处的颜色
                                offset: 0,
                                color: GRADIENT_COLOR_LIST.first[(params.dataIndex + Math.floor(Math.random() * 5)) % 7]
                            },
                                { // 100%处的颜色
                                    offset: 1,
                                    color: GRADIENT_COLOR_LIST.second[(params.dataIndex + Math.floor(Math.random() * 5)) % 7]
                                }
                            ])
                    }
                }
            },
            // z: 1,
            symbolRepeat: true, // 图形元素是否重复
            symbolRepeatDirection: 'start',
            symbolSize: [20, 6], // 图形元素的尺寸
            data: [
                [null, null]
            ],
            animationEasing: 'elasticOut' //动画效果
        }],
        tooltip: {    // 提示框组件
            trigger: 'axis',    // 触发类型（axis： 坐标轴触发）
            formatter: function (params) {    // 提示框浮层内容格式器
                params = params[0];
                return params.axisValueLabel + '<br />' + params.marker
                    + ' ' + params.seriesName
                    + '<span style="font-weight: bold;float: right;">' + params.value[1] + '%</span>';
            },
            axisPointer: {    // 坐标轴指示器配置项
                animation: true     // 是否开启动画
            }
        },
    }
    batteryChart.setOption(batteryChartOption);
    batteryData = [];

    // 网络状态
    network.html("");
    // 网速
    netSpeedChart = echarts.init(document.getElementById("chart-net-speed"));
    netSpeedChartOption = {
        series: [{
            name: '流量监控',    // 系列名称，用于tooltip的显示，legend 的图例筛选，在 setOption 更新数据和配置项时用于指定对应的系列。
            type: 'line',
            showSymbol: false,    // 是否显示 symbol, 如果 false 则只有在 tooltip hover 的时候显示。
            data: [],    // 系列中的数据内容数组
        }],
        tooltip: {    // 提示框组件
            trigger: 'axis',    // 触发类型（axis： 坐标轴触发）
            formatter: function (params) {    // 提示框浮层内容格式器
                params = params[0];
                return params.axisValueLabel + '<br />' + params.marker
                    + ' ' + params.seriesName
                    + '<span style="font-weight: bold;float: right;">' + params.value[1] + 'kB/s</span>';
            },
            axisPointer: {    // 坐标轴指示器配置项
                animation: true     // 是否开启动画
            }
        },
        toolbox: {    // 工具栏
            feature: {    // 各工具配置项
                saveAsImage: {
                    name: 'NetSpeed' + new Date().valueOf(),
                    type: 'png',
                },    // 保存为图片
                /*
                // dataView: {    // 数据视图工具，可以展现当前图表所用的数据，编辑后可以动态更新
                //     show: true,    // 是否显示该工具
                //     optionToContent: function (opt) {    // 自定义 dataView 展现函数，用以取代默认的 textarea 使用更丰富的数据编辑。
                //         var data = opt.series[0].data;
                //         var table = '<table border=1 cellspacing=0 cellpadding=5><tbody><tr>'
                //             + '<td align="center">时间</td>'
                //             + '<td align="center">' + opt.series[0].name + '</td>'
                //             + '</tr>';
                //         for (var i = 0, l = data.length; i < l; i++) {
                //             table += '<tr>'
                //                 + '<td align="center">' + data[i].value[0] + '</td>'
                //                 + '<td align="center">' + data[i].value[1] + '%</td>'
                //                 + '</tr>';
                //         }
                //         table += '</tbody></table>';
                //         return table;
                //     }
                // }
                 */
            }
        },
        xAxis: {    // 直角坐标系 grid 中的 x 轴
            type: 'time',    // 坐标轴类型（time: 时间轴，适用于连续的时序数据，与数值轴相比时间轴带有时间的格式化，在刻度计算上也有所不同，例如会根据跨度的范围来决定使用月，星期，日还是小时范围的刻度。）
            splitNumber: 5,
            minInterval: 1,
            splitLine: {    // 坐标轴在 grid 区域中的分隔线
                show: true // 是否显示分隔线。默认数值轴显示，类目轴不显示。
            }
        },
        yAxis: {    // 直角坐标系 grid 中的 y 轴
            type: 'value',    // 坐标轴类型（value: 数值轴，适用于连续数据。）
            name: '网速',
            splitNumber: 5,
            splitLine: {    // 坐标轴在 grid 区域中的分隔线
                show: true // 是否显示分隔线。默认数值轴显示，类目轴不显示。
            },
            axisLabel: {
                formatter: '{value} kB/s'
            },
        },
    };
    netSpeedChart.setOption(netSpeedChartOption);
    netSpeedData = [];
}

/**
 * 初始化桌面监控区
 */
function initDesktopSector() {
    /* 监控区 */
    isScreenShot = false;
    selectId = null;
    screenshotId = null;
    // 图像换为Android logo
    clientDesktop.attr("src", "/img/brands/android.svg");
    // 监控按钮内容为"监控桌面"
    screenShotContent.html(SCREENSHOT_STR);
    // 监控按钮样式
    screenShotBtn.attr("class", "btn btn-success");
    // 监控按钮不可用
    screenShotBtn.addClass("disabled")
    // 隐藏旋转进度
    screenShotSpinner.hide();
    // 保存图像按钮不可用
    $("#saveScreenshotBtn").addClass("disabled");
}

/**
 * 初始化回放区
 */
function initPlayBackSector() {
    playBackDeviceNum = 0;
    playBackImageData = [];
    totalPlayBackImageNum = 0;
    countImage = 0;
    replayImageNum = 0;
    $(".playBackDevice-list-item").remove();
    // 回放历史列表清空
    $(".playBack-list-item").remove();
    // 图像换为Android logo
    playBackDesktop.attr("src", "/img/brands/android.svg");
    // 进度条清零
    progressBar.attr("style", "width: 0%");
    // 刷新按钮失效
    refreshDeviceBtn.addClass("disabled");
    // 重播按钮失效
    replayBtn.addClass("disabled");
}

/**
 * 添加通知
 * @param choice
 */
function appendAlert(choice) {
    let alertTemplate = $(ALERT_TEMPLATE);
    let alertTemp = null;
    switch (choice) {
        case WEBSOCKET_OPEN:
            alertGroup.prepend(alertTemplate);
            alertTemp = appendDetails(choice);
            alertTemp.addClass("alert-success");
            break;
        case WEBSOCKET_CLOSED:
            alertGroup.prepend(alertTemplate);
            alertTemp = appendDetails(choice);
            alertTemp.addClass("alert-danger");
            break;
    }
}

function appendDetails(choice) {
    let alertTemp = $($("[role='alert']")[0]);

    let alertIconDiv = $(".alertIcon");
    alertIconDiv.attr("class", "alertIconDone");
    alertTemp.append(alertIconDiv);

    let alertContentDiv = $(".alertContent");

    alertContentDiv.append("<div class=\"text-muted text-nowrap\">" + getTime(new Date()) + "<\div>");
    alertContentDiv.attr("class", "alertContentDone");
    alertTemp.append(alertContentDiv);

    bellIcon.addClass("icon-tada");
    badge.show();

    switch (choice) {
        case WEBSOCKET_OPEN:
            alertIconDiv.prepend(CHECK_SVG);
            alertContentDiv.prepend(CONNECT_SUCCESS_ALERT_H4);
            break;
        case WEBSOCKET_CLOSED:
            alertIconDiv.prepend(ALERT_CIRCLE_SVG);
            alertContentDiv.prepend(DISCONNECT_ALERT_H4);
            break;
    }


    return alertTemp;
}

/**
 * 通知按钮动画效果去除
 * @param bell
 */
function removeTada(bell) {
    badge.hide()
    let bellIcon = $(bell);
    bellIcon.removeClass("icon-tada");
}

function removeClickedAlert(item) {
    let parents = $(item).parents();
    console.log(parents);

}

/**
 * 连接webSocket与断开
 */
function toggleConnectionClicked() {
    if (SocketCreated && (ws.readyState === WEBSOCKET_CONNECTING || ws.readyState === WEBSOCKET_OPEN)) {
        SocketCreated = false;
        const msg = JSON.stringify({
            'command': 'leave',
            'poolName': POOL_NAME,
            'username': username,
            'info': 'Monitor-' + username + '离开'
        });
        ws.send(msg);
        // 显示断开按钮
        plugOffSvg.show();
        // 显示旋转进度
        connectSpinner.show();
        // 按钮内容为"断开中"
        connectContent.text(DISCONNECTING_STR);
        ws.close();
    } else {
        console.log("准备连接到服务器 ...");
        username = document.getElementById("username").innerHTML;
        try {
            if ("WebSocket" in window) {
                // 隐藏连接按钮
                plugConnectedSvg.hide();
                // 显示连接旋转进度
                connectSpinner.show();
                // 按钮内容为"连接中"
                connectContent.html(CONNECTING_STR);
                ws = new WebSocket(
                    'ws://localhost:8080/app-websocket/INFO={"command":"connect","username":"' + username + '","poolName":"' + POOL_NAME + '"}');
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

/**
 * 屏幕监控
 */
function toggleScreenshot() {
    if (isScreenShot) {
        const msg = JSON.stringify({
            'command': 'screenshot_stop',
            'poolName': POOL_NAME,
            'username': username,
            'data': screenshotId
        });
        ws.send(msg);
        screenShotBtn.attr("class", "btn btn-success");
        screenShotContent.html(SCREENSHOT_STR);
        clientDesktop.attr("src", "/img/brands/android.svg");
        $("#saveScreenshotBtn").addClass("disabled");
        isScreenShot = false;
    } else {
        screenshotId = selectId;
        if (screenshotId === null) {
            alert(screenshotId + "为空")

        } else {
            const msg = JSON.stringify({
                'command': 'screenshot'
                , 'poolName': POOL_NAME
                , 'username': username
                , 'data': screenshotId
            })
            screenShotSpinner.show();
            screenShotContent.html(SCREENSHOT_CONNECTING_STR);
            ws.send(msg);
            isScreenShot = true;
        }
    }
}

function WSonOpen() {
    // 连接成功Alert
    appendAlert(WEBSOCKET_OPEN);
    /* 修改连接按钮 */
    connectSpinner.hide();
    plugOffSvg.show();
    connectBtn.attr("class", "btn btn-outline-danger")
    connectContent.html(DISCONNECT_FROM_SERVER_STR);
    /* 获得回放设备列表 */
    getPlayBackDeviceList();
    // 刷新按钮激活
    refreshDeviceBtn.removeClass("disabled");
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
        case "deviceInfo":
            onReceiveDeviceInfo(message);
            break;
        case "totalMemory":
            totalMemory = message.data;
            break;
        case "screenshot":
            onReceiveBitmap(message);
            break;
        case "screenshot_cancel":
            onScreenshotCancel();
            break;
        case "playBackDevices":
            onReceivePlayBackDeviceList(message);
            break;
        case "playBackList":
            onReceivePlayBackList(message);
            break;
        case "playBack":
            onReceivePlayBackImage(message);
            break;
        case "leave":
            onClientLeave(message);
            break;
        default:
            break;
    }

}

function WSonClose() {
    // 断开连接Alert
    appendAlert(WEBSOCKET_CLOSED);
    //连接区初始化
    initConnectionSector();
    // 数据区初始化
    initDeviceInfoSector();
    // 图像区初始化
    initDesktopSector();
    // 回放区初始化
    initPlayBackSector();
}

function WSonError() {
    console.log("远程连接中断。", "ERROR");
}

/**
 * 选择在线设备
 * @param selectedItem
 */
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
        // 转换为jQuery对象
        let item = $(selectedItem);
        item.parent().remove(".classStatus").remove("active");
        // 为选择设备添加选择状态和绿点
        item.addClass("active");
        item.append(GREEN_DOT_STATUS_SPAN);
        selectId = item.attr("id");
        // 发送给服务器, 获取
        const msg = JSON.stringify({
            'command': 'select',
            'poolName': POOL_NAME,
            'username': username,
            'data': selectId
        });
        ws.send(msg);
        // 发送图片质量设定
        let stepItem = $(".step-item.active");
        console.log(stepItem);
        let quality = stepItem.attr("quality");
        // 发送给服务器, 设定图片质量
        setTimeout(() => {
            const msg = JSON.stringify({
                'command': 'quality',
                'poolName': POOL_NAME,
                'username': username,
                'data': quality
            });
            ws.send(msg);
        }, 500);

    }
}

/**
 * 选择回放设备
 * @param selectedItem
 */
function onPlayBackDeviceSelected(selectedItem) {
    // 清除所有active类
    $(".playBackDevice-list-item").removeClass("active");
    // 清除所有绿点
    $(".clientStatus").remove();
    let item = $(selectedItem);
    item.parent().remove(".classStatus").remove("active");
    item.addClass("active");
    item.append(GREEN_DOT_STATUS_SPAN);
    selectUID = item.attr("id");
    // 清除回放历史列表
    $(".playBack-list-item").remove();
    // 发送给服务器, 获得回放历史列表
    const msg = JSON.stringify({
        'command': 'playBackList',
        'poolName': POOL_NAME,
        'username': username,
        'data': selectUID
    });
    ws.send(msg);
}

/**
 * 选择回放时间
 * @param selectedItem
 */
function onPlayBackItemSelected(selectedItem) {
    countImage = 0;
    progressBar.attr("style", "width: 0%");
    // 清空重播数组
    playBackImageData = [];
    // 清除所有active类
    $(".playBack-list-item").removeClass("active");
    // 清除所有绿点
    $(".clientStatus").remove();
    let item = $(selectedItem);
    item.parent().remove(".classStatus").remove("active");
    item.addClass("active");
    item.append(GREEN_DOT_STATUS_SPAN);
    let selectSessionId = item.attr("id");
    // 发送给服务器,开始准备接收图片编码
    const msg = JSON.stringify({
        'command': 'playBack',
        'poolName': POOL_NAME,
        'username': username,
        'data': selectSessionId
    });
    ws.send(msg);
}

function onConnectMessage(message) {
    // 接收客户端数量
    clientNum++;
    clientNumSpan.html(clientNum);
    // 新增android-client节点
    const id = message.data
    const username = message.username;
    const androidIcon = "<img src=\"/img/brands/android.svg\" alt=\"Tabler\" class=\"navbar-brand-image\">";
    const clientNameSpan = "<span>" + username + "</span>";
    let dropItem = "<a id=\"" + id + "\" class=\"dropdown-item client-list-item\" onclick=\"onClientItemSelected(this" + ")\" >" + androidIcon + clientNameSpan + "</a>";

    clientList.append(dropItem);
}

// 收到取消命令,图像区重置
function onScreenshotCancel() {
    isScreenShot = false;

    clientDesktop.attr("src", "img/brands/android.svg");
    screenShotContent.html(SCREENSHOT_STR);
    screenShotBtn.attr("class", "btn btn-success");
    screenShotSpinner.hide();
    $("#saveScreenshotBtn").addClass("disabled");
}

// Android-Client下线
function onClientLeave(message) {
    const id = message.data;
    // dropitem节点除去
    $("#" + id).remove();
    // 客户端数量
    clientNum--;
    clientNumSpan.html(clientNum);

    if (id === selectId) {
        // 数据区重置
        initDeviceInfoSector();
        // 图像区重置
        initDesktopSector();
    }
}

/**
 * 收到截图Bitmap时,更新img
 * @param message
 */
function onReceiveBitmap(message) {
    let url = "data:image/png;base64," + message.data;
    clientDesktop.attr("src", url);
    screenShotSpinner.hide();
    screenShotContent.html(SCREENSHOT_STOP_STR);
    screenShotBtn.attr("class", "btn btn-outline-danger");
    $("#lockBtn").removeClass("disabled")
    $("#saveScreenshotBtn")
        .attr("href", url)
        .attr("download", "desktopImg" + new Date().valueOf())
        .removeClass("disabled");
}

/**
 * 设定传输图片质量
 * @param item
 */
function setQuality(item) {
    $(".step-item").removeClass("active");
    let stepItem = $(item);
    stepItem.addClass("active");
    let quality = stepItem.attr("quality");
    if (ws.readyState === WEBSOCKET_OPEN && selectId !== null) {
        // 发送给服务器, 设定图片质量
        const msg = JSON.stringify({
            'command': 'quality',
            'poolName': POOL_NAME,
            'username': username,
            'data': quality
        });
        ws.send(msg);
    }
}

/**
 * 收到
 * @availMemory: 可用内存-更新水球图;
 * @cpuUsage: CPU使用率-更新折线图;
 * @netSpeed: 网速-更新折线图;
 * @network: 网络状态
 *
 * @param message
 */
function onReceiveDeviceInfo(message) {
    // 获取实时内存
    availMemory = message.availMemory;
    // 计算可用内存占比
    memoryProportion = (availMemory / totalMemory).toFixed(2);
    // 更新数据选项
    memoryChartOption.series[0].data = [{
        name: availMemory + 'MB',
        value: memoryProportion
    }];
    // 更新chart
    memoryChart.setOption(memoryChartOption);

    // 获取实时CPU使用率
    let cpuUsage = message.cpuUsage;
    let now = new Date();
    // 实时数据压入栈
    cpuChartData.push({
        name: now.toString(),
        value: [getTime(now), cpuUsage]
    });
    // 更新数据选项
    cpuUsageChartOption.series[0].data = cpuChartData;
    // 更新chart
    cpuUsageChart.setOption(cpuUsageChartOption);

    // 获取网络状态
    let networkStatus = message.network;
    network.html(networkStatus);
    // 获取实时网速
    let netSpeed = message.netSpeed;
    // 实时数据压入栈
    netSpeedData.push({
        name: now.toString(),
        value: [getTime(now), parseFloat(netSpeed).toFixed(2)]
    });
    // 更新数据配置
    netSpeedChartOption.series[0].data = netSpeedData;
    // 更新chart
    netSpeedChart.setOption(netSpeedChartOption);

    // 获取电量
    let batteryLevel = message.batteryLevel;
    // 实时数据压栈
    let length = batteryData.push({
        name: now.toString(),
        value: [getTime(now), batteryLevel]
    });
    if (length > 6) {
        batteryData = batteryData.slice(1);
        batteryChartOption.series[0].data = batteryData;
        batteryChart.setOption(batteryChartOption);
        return;
    }
    // 更新数据配置
    batteryChartOption.series[0].data = batteryData;
    // 更新chart
    batteryChart.setOption(batteryChartOption);
}

/**
 * 格式化Date: "yyyy/MM/dd HH:mm:SS"
 */
function getTime(date) {
    const ymd = [date.getFullYear(), date.getMonth() + 1, date.getDate()].join('/');
    const hour = date.getHours() < 10 ? ('0' + date.getHours()) : date.getHours();
    const minute = date.getMinutes() < 10 ? ('0' + date.getMinutes()) : date.getMinutes();
    const second = date.getSeconds() < 10 ? ('0' + date.getSeconds()) : date.getSeconds();
    const hms = [hour, minute, second].join(':');
    return ymd + ' ' + hms;
}

/**
 * 点击回放列表获取回放设备UID
 */
function getPlayBackDeviceList() {
    const msg = JSON.stringify({
        'command': 'playBackDevices',
        'poolName': POOL_NAME,
        'username': username,
    });
    ws.send(msg);
}

/**
 * 获取回放设备UID时
 * @param message
 */
function onReceivePlayBackDeviceList(message) {
    // 增加回放数量
    playBackDeviceNum++;
    playBackDeviceNumSpan.html(playBackDeviceNum);
    // 新增android-client节点
    const UID = message.data
    const playBackNameSpan = "<span>" + UID + "</span>";
    let dropItem = "<a id=\"" + UID + "\" class=\"dropdown-item playBackDevice-list-item\" onclick=\"onPlayBackDeviceSelected(this" + ")\" >" + ANDROID_ICON + playBackNameSpan + "</a>";

    playBackDeviceList.append(dropItem);
}

/**
 * 接收回放列表
 * @param message
 */
function onReceivePlayBackList(message) {
    // 新增android-client节点
    const sessionId = message.sessionId;
    const date = message.date;
    const playBackNameSpan = "<span>" + date + "</span>";
    let dropItem = "<a id=\"" + sessionId + "\" class=\"dropdown-item playBack-list-item\" onclick=\"onPlayBackItemSelected(this" + ")\" >" + playBackNameSpan + "</a>";

    playBackList.append(dropItem);
}

/**
 * 接收回放图像base64编码
 * @param message
 */
function onReceivePlayBackImage(message) {
    replayBtn.addClass("disabled");
    // 获取总数
    totalPlayBackImageNum = message.total;
    // 获取图像编码
    let imageBase64Code = message.data;
    countImage++;
    // 压栈保存, 用以重播
    playBackImageData.push(imageBase64Code);
    let url = "data:image/png;base64," + imageBase64Code;
    playBackDesktop.attr("src", url);
    let percent = countImage / totalPlayBackImageNum * 100;
    progressBar.attr("style", "width:" + percent + "%");
    if (percent === 100) {
        replayBtn.removeClass("disabled");
    }
}

/**
 * 刷新重播设备列表
 */
function refreshPlayBackDevices() {
    let selectDevice = null;
    playBackDeviceNum = 0;
    // 清除所有设备
    $(".playBackDevice-list-item").remove();
    // 获取设备列表
    getPlayBackDeviceList();
    // 对选择设备重新添加选择状态和绿点
    // 异步执行
    setTimeout(() => {
        if (selectUID !== null) {
            selectDevice = document.getElementById(selectUID);
            $(selectDevice).addClass("active");
            $(selectDevice).append(GREEN_DOT_STATUS_SPAN);
        }
    }, 100)

}

/**
 * 重播
 */
function replay() {
    if (replayImageNum === 0) {
        progressBar.attr("style", "width:0%");
        replayBtn.addClass("disabled");
        $("#replayContent").html("重播中...")
    }
    // 结束条件
    if (replayImageNum === playBackImageData.length) {
        replayBtn.removeClass("disabled")
        $("#replayContent").html("开始重播")
        setTimeout(() => {
            replayImageNum = 0;
        }, 50);
        return;
    }
    // 递归同步 1000/40 = 25 帧/s
    setTimeout(() => {
        let percent = (replayImageNum + 1) / totalPlayBackImageNum * 100;
        progressBar.attr("style", "width:" + percent + "%");
        let url = "data:image/png;base64," + playBackImageData[replayImageNum];
        playBackDesktop.attr("src", url);
        replayImageNum++;
        replay();
    }, 40)
}

$(function () {
    username = sessionStorage.getItem("username");
    $(".username").html(username);
    // 连接区初始化
    initConnectionSector();
    // 数据区
    initDeviceInfoSector();
    // 桌面监控区
    initDesktopSector();
    // 初始化回放区
    initPlayBackSector();
    // AlertRemove
    $("#alertGroup").on("mouseenter", ".row", function () {
        $(this).click(function () {
            $(this).parent().remove()
            console.log(this)
        })
    })
})
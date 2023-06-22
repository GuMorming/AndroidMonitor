# Android Monitor

## 目录

- [背景](#背景及技术要求)
- [功能模块](#功能模块)
    - [1.登录](#1-登录)
    - [2.数据采集](#2-数据采集)
    - [3.桌面采集](#3-桌面采集)
    - [4.桌面信息存储及操作回放](#4-桌面信息存储及操作回放)
- [相关仓库](#相关仓库)
- [维护者](#维护者)
- [交流](#欢迎交流)
- [License](#License)

## 背景及技术要求

`AndroidMonitor`: 学校实训项目，旨在实现Android手机界面监控的Web系统。

本项目要求实现的功能是：

基于WebSocket进行网络通信，PC端Web程序通过网络监控Android手机界面实时运行状态，
实时采集Android手机的界面信息和设备运行状态，根据功能将必要信息进行存储。

## 功能模块

### 1. 登录

PC端Web程序作为监控端登录系统。

### 2. 数据采集

被控端-Android手机采集自身信息

1. 剩余内存
2. CPU使用率
3. 电量
4. 网络状态及网速

并定时发送给监控端。

### 3. 桌面采集

监控端对被控端发出桌面监控请求，被控端给予权限后将自身桌面截图并通过WebSocket通信发送给监控端，
监控端收到后展示在前端页面中。

### 4. 桌面信息存储及操作回放

将采集到的桌面截图存储在数据库中，监控端可选择回放。

## 相关仓库

- [Android WebSocket Client](https://github.com/GuMorming/AndroidWebSocketClient) — 本项目的相关Android端App。

## 维护者

[@GuMorming](https://github.com/GuMorming)

## 欢迎交流

非常欢迎！

[提一个 Issue](https://github.com/GuMorming/AndroidMonitor/issues/new) 或者提交一个 Pull Request。

## License

[MIT](LICENSE) © GuMorming
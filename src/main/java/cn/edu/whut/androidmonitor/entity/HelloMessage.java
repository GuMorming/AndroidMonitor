package cn.edu.whut.androidmonitor.entity;

/**
 * @author : GuMorming
 * @Project : webSocketDemo
 * @Package : com.example.demo
 * @createTime : 2023/6/6 14:45
 * @Email : gumorming@163.com
 * @Description :
 */

public class HelloMessage {
    private String name;
    
    public HelloMessage() {
    }
    
    public HelloMessage(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
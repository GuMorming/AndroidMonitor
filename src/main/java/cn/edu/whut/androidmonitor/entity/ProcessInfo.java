package cn.edu.whut.androidmonitor.entity;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.entity
 * @createTime : 2023/6/7 16:48
 * @Email : gumorming@163.com
 * @Description :
 */

public class ProcessInfo {
    private String name;
    
    private long memSize;
    private boolean isSys;
    private String processName;
    
    public ProcessInfo(String name, long memSize, boolean isSys, String processName) {
        this.name = name;
        this.memSize = memSize;
        this.isSys = isSys;
        this.processName = processName;
    }
    
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    
    public long getMemSize() {
        return memSize;
    }
    
    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }
    
    public boolean isSys() {
        return isSys;
    }
    
    public void setSys(boolean sys) {
        isSys = sys;
    }
    
    public String getProcessName() {
        return processName;
    }
    
    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
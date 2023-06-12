package cn.edu.whut.androidmonitor.entity;

import org.json.JSONException;
import org.json.JSONObject;

import static cn.edu.whut.androidmonitor.constants.MessageKey.KEY_COMMAND;
import static cn.edu.whut.androidmonitor.constants.MessageKey.KEY_DATA;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.entity
 * @createTime : 2023/6/12 15:08
 * @Email : gumorming@163.com
 * @Description :
 */

public class ClientMessage {
    String command;
    String data;
    
    public ClientMessage(String json) {
        if (json != null && json.length() > 0) {
            JSONObject jsonObject = new JSONObject(json);
            this.command = jsonObject.optString(KEY_COMMAND);
            this.data = jsonObject.optString(KEY_DATA);
            
        }
    }
    
    public JSONObject toJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_DATA, this.data);
            jsonObject.put(KEY_COMMAND, this.command);
            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
        
    }
    
    
    public String getCommand() {
        return command;
    }
    
    public String getData() {
        return data;
    }
}
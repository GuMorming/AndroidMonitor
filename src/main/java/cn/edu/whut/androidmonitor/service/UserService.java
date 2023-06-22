package cn.edu.whut.androidmonitor.service;

import cn.edu.whut.androidmonitor.entity.User;
import cn.edu.whut.androidmonitor.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.service
 * @createTime : 2023/6/22 21:47
 * @Email : gumorming@163.com
 * @Description :
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public boolean register(User user) {
        return userMapper.insertUser(user);
    }
    
    public boolean login(User user) {
        return (userMapper.isExist(user) != null);
    }
}
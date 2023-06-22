package cn.edu.whut.androidmonitor.mapper;

import cn.edu.whut.androidmonitor.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.mapper
 * @createTime : 2023/6/22 21:37
 * @Email : gumorming@163.com
 * @Description :
 */
@Repository
public interface UserMapper {
    /**
     * 插入用户
     *
     * @param user
     * @return
     */
    public boolean insertUser(User user);
    
    /**
     * 判断用户是否存在
     *
     * @param user
     * @return
     */
    public User isExist(User user);
}
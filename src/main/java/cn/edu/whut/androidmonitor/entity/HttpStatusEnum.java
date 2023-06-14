package cn.edu.whut.androidmonitor.entity;

/**
 * @author : GuMorming
 * @Project : AndroidMonitor
 * @Package : cn.edu.whut.androidmonitor.entity
 * @createTime : 2023/6/13 12:14
 * @Email : gumorming@163.com
 * @Description : 错误页面枚举类
 */

/**
 * 错误页面枚举类
 */
public enum HttpStatusEnum implements StatusReturnPageStr {
    error_404 {
        @Override
        public String Page() {
            return "error/404";
        }
    },
    error_500 {
        @Override
        public String Page() {
            return "error/500";
        }
    },
    error_401 {
        @Override
        public String Page() {
            return "error/401";
        }
    },
    error_403 {
        @Override
        public String Page() {
            return "error/403";
        }
    },
    
}
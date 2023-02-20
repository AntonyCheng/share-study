package top.sharehome.share_study.common.constant;

import io.swagger.models.auth.In;

/**
 * 通用常量类
 *
 * @author AntonyCheng
 */

public interface CommonConstant {
    /**
     * 用户的登陆状态数据Session KEY
     */
    String USER_LOGIN_STATE = "userLoginState";
    /**
     * 默认权限
     */
    Integer DEFAULT_ROLE = 0;
    /**
     * 管理员权限
     */
    Integer ADMIN_ROLE = 1;
    /**
     * 超级管理员权限
     */
    Integer SUPER_ROLE = 2;

}

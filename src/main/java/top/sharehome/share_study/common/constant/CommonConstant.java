package top.sharehome.share_study.common.constant;

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
     * 管理员的登陆状态数据Session KEY
     */
    String ADMIN_LOGIN_STATE = "adminLoginState";

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

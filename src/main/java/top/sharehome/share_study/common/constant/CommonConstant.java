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

    /**
     * 教学资料未审核
     */
    Integer RESOURCE_CENSOR_STATUS_WAIT = 0;

    /**
     * 教学资料审核成功
     */
    Integer RESOURCE_CENSOR_OPTIONS_SUCCESS = 1;

    /**
     * 教学资料审核失败
     */
    Integer RESOURCE_CENSOR_OPTIONS_FAILURE = 2;

    /**
     * 教学资料正在审核
     */
    Integer RESOURCE_CENSOR_STATUS_ONGOING = 1;

    /**
     * 教学资料审核通过
     */
    Integer RESOURCE_CENSOR_STATUS_PASS = 2;

    /**
     * 教学资料审核未通过
     */
    Integer RESOURCE_CENSOR_STATUS_NOT_PASS = 3;

    /**
     * 教学资料审核已发布
     */
    Integer RESOURCE_CENSOR_STATUS_PUBLISHED = 4;

    /**
     * 注册申请审核未发布
     */
    Integer TEACHER_CENSOR_STATUS_WAIT = 0;

    /**
     * 注册申请审核成功
     */
    Integer TEACHER_CENSOR_OPTIONS_SUCCESS = 1;

    /**
     * 注册申请审核失败
     */
    Integer TEACHER_CENSOR_OPTIONS_FAILURE = 2;

    /**
     * 注册申请审核通过
     */
    Integer TEACHER_CENSOR_STATUS_PASS = 1;

    /**
     * 注册申请审核未通过
     */
    Integer TEACHER_CENSOR_STATUS_NOT_PASS = 2;

    /**
     * 注册申请审核已发布
     */
    Integer TEACHER_CENSOR_STATUS_PUBLISHED = 3;

    /**
     * 创建“修改资料”的区块方法名 / rabbitMQ修改资料routingKey
     */
    String UPDATE_RESOURCE = "updateResource";

    /**
     * 创建“增加资料”的区块方法名 / rabbitMQ增加资料routingKey
     */
    String CREATE_RESOURCE = "createResource";

    /**
     * 创建“修改评论”的区块方法名 / rabbitMQ修改评论routingKey
     */
    String UPDATE_COMMENT = "updateComment";

    /**
     * 创建“增加评论”的区块方法名 / rabbitMQ增加评论routingKey
     */
    String CREATE_COMMENT = "createComment";

    /**
     * rabbitMQ发送注册反馈邮件routingKey
     */
    String SEND_REGISTRATION_FEEDBACK_EMAIL = "sendRegistrationFeedbackEmail";
}

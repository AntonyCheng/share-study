package top.sharehome.share_study.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户注册审核表
 *
 * @author AntonyCheng
 */
@ApiModel(description = "用户注册审核表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "share_study.t_teacher_censor")
public class TeacherCensor implements Serializable {

    private static final long serialVersionUID = 8090269732808415574L;
    /**
     * 教师注册审核唯一ID
     */
    @TableId(value = "censor_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "教师注册审核唯一ID")
    private Long id;

    /**
     * 教师账号（具有唯一性，推荐手机号）
     */
    @TableField(value = "teacher_account")
    @ApiModelProperty(value = "教师账号（具有唯一性，推荐手机号）")
    private String account;

    /**
     * 教师账号密码（推荐6-16位）
     */
    @TableField(value = "teacher_password")
    @ApiModelProperty(value = "教师账号密码（推荐6-16位）")
    private String password;

    /**
     * 教师姓名
     */
    @TableField(value = "teacher_name")
    @ApiModelProperty(value = "教师姓名")
    private String name;

    /**
     * 教师头像地址
     */
    @TableField(value = "teacher_avatar")
    @ApiModelProperty(value = "教师头像地址")
    private String avatar;

    /**
     * 教师性别（0表示男性，1表示女性）
     */
    @TableField(value = "teacher_gender")
    @ApiModelProperty(value = "教师性别（0表示男性，1表示女性）")
    private Integer gender;

    /**
     * 所属高校的id
     */
    @TableField(value = "teacher_belong")
    @ApiModelProperty(value = "所属高校的id")
    private Long belong;

    /**
     * 教师邮箱
     */
    @TableField(value = "teacher_email")
    @ApiModelProperty(value = "教师邮箱")
    private String email;

    /**
     * 审核员ID
     */
    @TableField(value = "censor_admin_id")
    @ApiModelProperty(value = "审核员ID")
    private Long censorAdminId;

    /**
     * 审核员姓名
     */
    @TableField(value = "censor_admin_name")
    @ApiModelProperty(value = "审核员姓名")
    private String censorAdminName;

    /**
     * 审核状态（0是未审核，1是审核通过，2是审核未通过）
     */
    @TableField(value = "censor_status", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "审核状态（0是未审核，1是审核通过，2是审核未通过）")
    private Integer status;

    /**
     * 审核反馈
     */
    @TableField(value = "censor_content")
    @ApiModelProperty(value = "审核反馈")
    private String content;

    /**
     * 教师用户注册审核发布时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "教师用户注册审核发布时间")
    private LocalDateTime createTime;

    /**
     * 教师用户注册审核更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "教师用户注册审核更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "逻辑删除（0表示未删除，1表示已删除）")
    private Integer isDeleted;

    public static final String COL_CENSOR_ID = "censor_id";

    public static final String COL_TEACHER_ACCOUNT = "teacher_account";

    public static final String COL_TEACHER_PASSWORD = "teacher_password";

    public static final String COL_TEACHER_NAME = "teacher_name";

    public static final String COL_TEACHER_AVATAR = "teacher_avatar";

    public static final String COL_TEACHER_GENDER = "teacher_gender";

    public static final String COL_TEACHER_BELONG = "teacher_belong";

    public static final String COL_TEACHER_EMAIL = "teacher_email";

    public static final String COL_CENSOR_ADMIN_ID = "censor_admin_id";

    public static final String COL_CENSOR_ADMIN_NAME = "censor_admin_name";

    public static final String COL_CENSOR_STATUS = "censor_status";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";
}
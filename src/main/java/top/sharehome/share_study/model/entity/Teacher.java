package top.sharehome.share_study.model.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.sharehome.share_study.common.converter.ExcelIntegerConverter;
import top.sharehome.share_study.common.converter.ExcelLocalDateTimeConverter;
import top.sharehome.share_study.common.converter.ExcelLongConverter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教师用户表
 *
 * @author AntonyCheng
 */
@ApiModel(description = "教师用户表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "share_study.t_teacher")
public class Teacher implements Serializable {
    private static final long serialVersionUID = -4538857455119856760L;
    /**
     * 教师用户唯一ID
     */
    @TableId(value = "teacher_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "教师用户唯一ID")
    @ExcelProperty(value = "教师用户唯一ID", index = 0, converter = ExcelLongConverter.class)
    private Long id;

    /**
     * 教师账号（具有唯一性，推荐手机号）
     */
    @TableField(value = "teacher_account")
    @ApiModelProperty(value = "教师账号（具有唯一性，推荐手机号）")
    @ExcelProperty(value = "教师账号", index = 1)
    private String account;

    /**
     * 教师账号密码（推荐6-16位）
     */
    @TableField(value = "teacher_password")
    @ApiModelProperty(value = "教师账号密码（推荐6-16位）")
    @ExcelProperty(value = "教师账号密码", index = 2)
    private String password;

    /**
     * 教师姓名
     */
    @TableField(value = "teacher_name")
    @ApiModelProperty(value = "教师姓名")
    @ExcelProperty(value = "教师姓名", index = 3)
    private String name;

    /**
     * 教师头像地址
     */
    @TableField(value = "teacher_avatar")
    @ApiModelProperty(value = "教师头像地址")
    @ExcelProperty(value = "教师头像地址", index = 4)
    private String avatar;

    /**
     * 教师性别（0表示男性，1表示女性）
     */
    @TableField(value = "teacher_gender")
    @ApiModelProperty(value = "教师性别（0表示男性，1表示女性）")
    @ExcelProperty(value = "教师性别", index = 5, converter = ExcelIntegerConverter.class)
    private Integer gender;

    /**
     * 所属高校的id
     */
    @TableField(value = "teacher_belong")
    @ApiModelProperty(value = "所属高校的id")
    @ExcelProperty(value = "所属高校的id", index = 6, converter = ExcelLongConverter.class)
    private Long belong;

    /**
     * 教师邮箱
     */
    @TableField(value = "teacher_email")
    @ApiModelProperty(value = "教师邮箱")
    @ExcelProperty(value = "教师邮箱", index = 7)
    private String email;

    /**
     * 教师创作贡献度
     */
    @TableField(value = "teacher_score")
    @ApiModelProperty(value = "教师创作贡献度")
    @ExcelProperty(value = "教师创作贡献度", index = 8, converter = ExcelIntegerConverter.class)
    private Integer score;

    /**
     * 消息总数
     */
    @TableField(value = "teacher_message_total")
    @ApiModelProperty(value = "消息总数")
    @ExcelProperty(value = "消息总数", index = 9, converter = ExcelIntegerConverter.class)
    private Integer messageTotal;

    /**
     * 消息已读数
     */
    @TableField(value = "teacher_message_read")
    @ApiModelProperty(value = "消息已读数")
    @ExcelProperty(value = "消息已读数", index = 10, converter = ExcelIntegerConverter.class)
    private Integer messageRead;

    /**
     * 教师状态（0表示正常，1表示封禁）
     */
    @TableField(value = "teacher_status", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "教师状态（0表示正常，1表示封禁）")
    @ExcelProperty(value = "教师状态", index = 11, converter = ExcelIntegerConverter.class)
    private Integer status;

    /**
     * 用户角色（0普通用户，1管理员用户，2超级管理员）
     */
    @TableField(value = "teacher_role", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "用户角色（0普通用户，1管理员用户，2超级管理员）")
    @ExcelProperty(value = "用户角色", index = 12, converter = ExcelIntegerConverter.class)
    private Integer role;

    /**
     * 教师录入时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "教师录入时间")
    @ExcelProperty(value = "教师录入时间", index = 13, converter = ExcelLocalDateTimeConverter.class)
    private LocalDateTime createTime;

    /**
     * 教师修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "教师修改时间")
    @ExcelProperty(value = "教师修改时间", index = 14, converter = ExcelLocalDateTimeConverter.class)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "逻辑删除（0表示未删除，1表示已删除）")
    @TableLogic
    @ExcelProperty(value = "逻辑删除", index = 15, converter = ExcelIntegerConverter.class)
    private Integer isDeleted;

    public static final String COL_TEACHER_ID = "teacher_id";

    public static final String COL_TEACHER_ACCOUNT = "teacher_account";

    public static final String COL_TEACHER_PASSWORD = "teacher_password";

    public static final String COL_TEACHER_NAME = "teacher_name";

    public static final String COL_TEACHER_AVATAR = "teacher_avatar";

    public static final String COL_TEACHER_GENDER = "teacher_gender";

    public static final String COL_TEACHER_BELONG = "teacher_belong";

    public static final String COL_TEACHER_SCORE = "teacher_score";

    public static final String COL_TEACHER_EMAIL = "teacher_email";

    public static final String COL_TEACHER_MESSAGE_TOTAL = "teacher_message_total";

    public static final String COL_TEACHER_MESSAGE_READ = "teacher_message_read";

    public static final String COL_TEACHER_STATUS = "teacher_status";

    public static final String COL_TEACHER_ROLE = "teacher_role";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";

}
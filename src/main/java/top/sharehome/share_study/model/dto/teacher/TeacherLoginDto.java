package top.sharehome.share_study.model.dto.teacher;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教师用户Session对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "教师用户Session对象")
public class TeacherLoginDto implements Serializable {

    private static final long serialVersionUID = -8054516471074486206L;
    /**
     * 教师用户唯一ID
     */
    private Long id;

    /**
     * 教师账号（具有唯一性，推荐手机号）
     */
    private String account;

    /**
     * 教师姓名
     */
    private String name;

    /**
     * 教师头像地址
     */
    private String avatar;

    /**
     * 教师性别（0表示男性，1表示女性）
     */
    private Integer gender;

    /**
     * 所属高校
     */
    private String collegeName;

    /**
     * 教师邮箱
     */
    private String email;

    /**
     * 贡献度
     */
    private Integer score;

    /**
     * 消息未读数
     */
    private Integer messageNumber;

    /**
     * 用户角色（0普通用户，1管理员用户，2超级管理员）
     */
    private Integer role;

    /**
     * 用户加入时间
     */
    private LocalDateTime createTime;
}

package top.sharehome.share_study.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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

    private static final long serialVersionUID = -4155135350610263261L;
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
    private String gender;

    /**
     * 所属高校
     */
    private String collegeName;

    /**
     * 教师邮箱（需要隐藏）
     */
    private String email;

    /**
     * 消息未读数
     */
    private Long messageNumber;

    /**
     * 用户角色（0普通用户，1管理员用户，2超级管理员）
     */
    private Integer role;
}

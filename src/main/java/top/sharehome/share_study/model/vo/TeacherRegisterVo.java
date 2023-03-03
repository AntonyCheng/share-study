package top.sharehome.share_study.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教师用户注册Vo
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("教师用户注册Vo")
public class TeacherRegisterVo implements Serializable {

    private static final long serialVersionUID = 7754474638205664372L;
    /**
     * 教师账号（具有唯一性，4-16位）
     */
    @ApiModelProperty(value = "教师账号（具有唯一性，推荐手机号）")
    private String account;

    /**
     * 教师账号密码（4-16位）
     */
    @ApiModelProperty(value = "教师账号密码（4-16位）")
    private String password;

    /**
     * 重复密码验证
     */
    @ApiModelProperty(value = "重复密码验证")
    private String checkPassword;

    /**
     * 教师姓名
     */
    @ApiModelProperty(value = "教师姓名")
    private String name;

    /**
     * 教师头像地址
     */
    @ApiModelProperty(value = "教师头像地址")
    private String avatar;

    /**
     * 教师性别（0表示男性，1表示女性）
     */
    @ApiModelProperty(value = "教师性别（0表示男性，1表示女性）")
    private Integer gender;

    /**
     * 所属高校的院校代码
     */
    @ApiModelProperty(value = "所属高校的院校代码")
    private String code;

    /**
     * 教师邮箱
     */
    @ApiModelProperty(value = "教师邮箱")
    private String email;
}

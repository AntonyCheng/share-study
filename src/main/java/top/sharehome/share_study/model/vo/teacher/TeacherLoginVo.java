package top.sharehome.share_study.model.vo.teacher;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教师用户登录Vo
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("教师用户登录Vo")
public class TeacherLoginVo implements Serializable {

    private static final long serialVersionUID = 7703270039215065042L;
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
}

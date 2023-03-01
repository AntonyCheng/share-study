package top.sharehome.share_study.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教师分页Vo对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("教师分页Vo对象")
public class TeacherPageVo implements Serializable {
    private static final long serialVersionUID = 1822299234345652300L;
    /**
     * 教师账号（具有唯一性，推荐手机号）
     */
    private String account;

    /**
     * 教师姓名
     */
    private String name;

    /**
     * 教师性别（0表示男性，1表示女性）
     */
    private Integer gender;

    /**
     * 所属高校的名称
     */
    private String belongName;

    /**
     * 教师状态
     */
    private Integer status;

    /**
     * 用户角色
     */
    private Integer role;


}

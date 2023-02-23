package top.sharehome.share_study.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理员分页Vo对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("管理员分页Vo对象")
public class AdminPageVo implements Serializable {
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
     * 所属高校的id
     */
    private Long belong;

    /**
     * 教师状态
     */
    private Integer status;

    /**
     * 用户角色
     */
    private Integer role;


    private static final long serialVersionUID = 4516272080340631560L;
}

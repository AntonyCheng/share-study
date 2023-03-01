package top.sharehome.share_study.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教师分页回显对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "教师分页回显对象")
public class TeacherPageDto implements Serializable {
    private static final long serialVersionUID = 1504308710148455138L;
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
     * 所属高校的id
     */
    private Long belong;

    /**
     * 所属高校的名称
     */
    private String belongName;

    /**
     * 教师邮箱
     */
    private String email;

    /**
     * 贡献度
     */
    private Long score;

    /**
     * 教师状态
     */
    private Integer status;

    /**
     * 用户角色
     */
    private Integer role;

    /**
     * 管理员录入时间
     */
    private LocalDateTime createTime;
}

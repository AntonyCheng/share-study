package top.sharehome.share_study.model.dto.teacher_censor;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 注册申请审核分页Dto实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("注册申请审核分页Dto实体")
public class TeacherCensorPageDto implements Serializable {

    private static final long serialVersionUID = -6192881801478725848L;

    /**
     * 教师注册审核唯一ID
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
     * 所属高校名称
     */
    private String belongName;

    /**
     * 教师邮箱
     */
    private String email;

    /**
     * 审核员ID
     */
    private Long censorAdminId;

    /**
     * 审核员姓名
     */
    private String censorAdminName;

    /**
     * 审核状态（0是未审核，1是审核通过，2是审核未通过）
     */
    private Integer status;

    /**
     * 审核反馈
     */
    private String content;

    /**
     * 教师用户注册审核发布时间
     */
    private LocalDateTime createTime;

    /**
     * 教师用户注册审核更新时间
     */
    private LocalDateTime updateTime;
}

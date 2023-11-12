package top.sharehome.share_study.model.dto.user;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 普通用户获取自己信息Dto实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("普通用户获取自己信息Dto实体")
public class UserGetInfoDto implements Serializable {
    private static final long serialVersionUID = -5992209175621448949L;
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
     * 教师邮箱
     */
    private String email;

}

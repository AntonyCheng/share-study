package top.sharehome.share_study.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 超管获取管理员信息Dto实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("超管获取管理员信息Dto实体")
public class AdminGetDto implements Serializable {
    private static final long serialVersionUID = 7034097590300460001L;
    /**
     * 教师用户唯一ID
     */
    private Long id;

    /**
     * 教师邮箱
     */
    private String email;
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
     * 教师状态（0表示正常，1表示封禁）
     */
    private Integer status;

    /**
     * 用户角色（0普通用户，1管理员用户）
     */
    private Integer role;
}

package top.sharehome.share_study.model.vo.teacher_censor;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 注册申请审核分页Vo实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("注册申请审核分页Vo实体")
public class TeacherCensorPageVo implements Serializable {

    private static final long serialVersionUID = -3352144165243403225L;

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
     * 所属高校名称
     */
    private String belongName;

    /**
     * 审核员姓名
     */
    private String censorAdminName;

    /**
     * 审核状态（0是未审核，1是审核通过，2是审核未通过）
     */
    private Integer status;
}

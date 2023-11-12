package top.sharehome.share_study.model.vo.resource_censor;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教学资料审核分页Vo实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("教学资料审核分页Vo实体")
public class ResourceCensorPageVo implements Serializable {

    private static final long serialVersionUID = 4895599745379054419L;

    /**
     * 所属老师名称
     */
    private String belongName;

    /**
     * 教学资料名
     */
    private String name;

    /**
     * 教学资料简介
     */
    private String info;

    /**
     * 第一审核员姓名
     */
    private String censorAdmin1Name;

    /**
     * 第二审核员姓名
     */
    private String censorAdmin2Name;

    /**
     * 第三审核员姓名
     */
    private String censorAdmin3Name;

    /**
     * 资料标签（一个）
     */
    private Long tag;

    /**
     * 审核状态（0是未审核，1是正在审核，2是审核通过，3是审核未通过）
     */
    private Integer status;


}

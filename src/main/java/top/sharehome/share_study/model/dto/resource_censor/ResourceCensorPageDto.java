package top.sharehome.share_study.model.dto.resource_censor;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教学资料审核分页Dto实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("教学资料审核分页Dto实体")
public class ResourceCensorPageDto implements Serializable {

    private static final long serialVersionUID = 7303644704964081825L;
    /**
     * 教学资料审核唯一ID
     */
    private Long id;

    /**
     * 所属老师ID
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
     * 教学资料所在地址
     */
    private String url;

    /**
     * 第一审核员ID
     */
    private Long censorAdmin1Id;

    /**
     * 第一审核员姓名
     */
    private String censorAdmin1Name;

    /**
     * 第一审核员审核结果
     */
    private Integer censorAdmin1Result;

    /**
     * 第二审核员ID
     */
    private Long censorAdmin2Id;

    /**
     * 第二审核员姓名
     */
    private String censorAdmin2Name;

    /**
     * 第二审核员审核结果
     */
    private Integer censorAdmin2Result;

    /**
     * 第三审核员ID
     */
    private Long censorAdmin3Id;

    /**
     * 第三审核员姓名
     */
    private String censorAdmin3Name;

    /**
     * 第三审核员审核结果
     */
    private Integer censorAdmin3Result;

    /**
     * 资料标签
     */
    private List<String> tags;

    /**
     * 审核状态（0是未审核，1是正在审核，2是审核通过，3是审核未通过）
     */
    private Integer status;

    /**
     * 教学资料上传时间
     */
    private LocalDateTime createTime;

    /**
     * 教学资料审核修改时间
     */
    private LocalDateTime updateTime;
}

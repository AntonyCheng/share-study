package top.sharehome.share_study.model.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教学资料审核表
 *
 * @author AntonyCheng
 */
@ApiModel(description = "教学资料审核表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "share_study.t_resource_censor")
public class ResourceCensor implements Serializable {
    private static final long serialVersionUID = -2092984298578682553L;
    /**
     * 教学资料审核唯一ID
     */
    @TableId(value = "censor_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "教学资料审核唯一ID")
    private Long id;

    /**
     * 所属老师ID
     */
    @TableField(value = "resource_belong")
    @ApiModelProperty(value = "所属老师ID")
    private Long belong;

    /**
     * 教学资料名
     */
    @TableField(value = "resource_name")
    @ApiModelProperty(value = "教学资料名")
    private String name;

    /**
     * 教学资料简介
     */
    @TableField(value = "resource_info")
    @ApiModelProperty(value = "教学资料简介")
    private String info;

    /**
     * 教学资料所在地址
     */
    @TableField(value = "resource_url")
    @ApiModelProperty(value = "教学资料所在地址")
    private String url;

    /**
     * 第一审核员ID
     */
    @TableField(value = "censor_admin_1_id")
    @ApiModelProperty(value = "第一审核员ID")
    private Long censorAdmin1Id;

    /**
     * 第一审核员姓名
     */
    @TableField(value = "censor_admin_1_name")
    @ApiModelProperty(value = "第一审核员姓名")
    private String censorAdmin1Name;

    /**
     * 第一审核员审核结果
     */
    @TableField(value = "censor_admin_1_result")
    @ApiModelProperty(value = "第一审核员审核结果（0表示未有审核结果，1表示审核通过，2表示审核未通过）")
    private Integer censorAdmin1Result;

    /**
     * 第二审核员ID
     */
    @TableField(value = "censor_admin_2_id")
    @ApiModelProperty(value = "第二审核员ID")
    private Long censorAdmin2Id;

    /**
     * 第二审核员姓名
     */
    @TableField(value = "censor_admin_2_name")
    @ApiModelProperty(value = "第二审核员姓名")
    private String censorAdmin2Name;

    /**
     * 第二审核员审核结果
     */
    @TableField(value = "censor_admin_2_result")
    @ApiModelProperty(value = "第二审核员审核结果（0表示未有审核结果，1表示审核通过，2表示审核未通过）")
    private Integer censorAdmin2Result;

    /**
     * 第三审核员ID
     */
    @TableField(value = "censor_admin_3_id")
    @ApiModelProperty(value = "第三审核员ID")
    private Long censorAdmin3Id;

    /**
     * 第三审核员姓名
     */
    @TableField(value = "censor_admin_3_name")
    @ApiModelProperty(value = "第三审核员姓名")
    private String censorAdmin3Name;

    /**
     * 第三审核员审核结果
     */
    @TableField(value = "censor_admin_3_result")
    @ApiModelProperty(value = "第三审核员审核结果（0表示未有审核结果，1表示审核通过，2表示审核未通过）")
    private Integer censorAdmin3Result;

    /**
     * 教学资料课程名称标签
     */
    @TableField(value = "resource_tags")
    @ApiModelProperty(value = "教学资料课程名称标签")
    private String tags;

    /**
     * 审核状态（0是未审核，1是正在审核，2是审核通过，3是审核未通过）
     */
    @TableField(value = "censor_status", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "审核状态（0是未审核，1是正在审核，2是审核通过，3是审核未通过）")
    private Integer status;

    /**
     * 教学资料审核发布时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "教学资料审核发布时间")
    private LocalDateTime createTime;

    /**
     * 教学资料审核修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "教学资料审核修改时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "逻辑删除（0表示未删除，1表示已删除）")
    @TableLogic
    private Integer isDeleted;


    public static final String COL_CENSOR_ID = "censor_id";

    public static final String COL_RESOURCE_BELONG = "resource_belong";

    public static final String COL_RESOURCE_NAME = "resource_name";

    public static final String COL_RESOURCE_INFO = "resource_info";

    public static final String COL_RESOURCE_URL = "resource_url";

    public static final String COL_CENSOR_ADMIN_1_ID = "censor_admin_1_id";

    public static final String COL_CENSOR_ADMIN_1_NAME = "censor_admin_1_name";

    public static final String COL_CENSOR_ADMIN_1_RESULT = "censor_admin_1_result";

    public static final String COL_CENSOR_ADMIN_2_ID = "censor_admin_2_id";

    public static final String COL_CENSOR_ADMIN_2_NAME = "censor_admin_2_name";

    public static final String COL_CENSOR_ADMIN_2_RESULT = "censor_admin_2_result";

    public static final String COL_CENSOR_ADMIN_3_ID = "censor_admin_3_id";

    public static final String COL_CENSOR_ADMIN_3_NAME = "censor_admin_3_name";

    public static final String COL_CENSOR_ADMIN_3_RESULT = "censor_admin_3_result";

    public static final String COL_CENSOR_STATUS = "censor_status";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";
}
package top.sharehome.share_study.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 教学资料表
 *
 * @author AntonyCheng
 */
@ApiModel(description = "教学资料表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "share_study.t_resource")
public class Resource implements Serializable {

    private static final long serialVersionUID = 1683058685207781686L;
    /**
     * 教学资料唯一ID
     */
    @TableId(value = "resource_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "教学资料唯一ID")
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
     * 教学资料状态（0表示正常，1表示封禁）
     */
    @TableField(value = "resource_status", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "教学资料状态（0表示正常，1表示封禁）,")
    private Integer status;

    /**
     * 教学资料发布时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "教学资料发布时间")
    private LocalDateTime createTime;

    /**
     * 教学资料修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "教学资料修改时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "逻辑删除（0表示未删除，1表示已删除）")
    @TableLogic
    private Integer isDeleted;

    public static final String COL_RESOURCE_ID = "resource_id";

    public static final String COL_RESOURCE_BELONG = "resource_belong";

    public static final String COL_RESOURCE_NAME = "resource_name";

    public static final String COL_RESOURCE_INFO = "resource_info";

    public static final String COL_RESOURCE_URL = "resource_url";

    public static final String COL_RESOURCE_STATUS = "resource_status";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";
}
package top.sharehome.share_study.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 收藏表
 *
 * @author AntonyCheng
 */
@ApiModel(description = "收藏表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "share_study.t_collect")
public class Collect implements Serializable {
    private static final long serialVersionUID = 779742128441581864L;
    /**
     * 收藏唯一ID
     */
    @TableId(value = "collect_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "收藏唯一ID")
    private Long id;

    /**
     * 收藏者ID
     */
    @TableField(value = "collect_belong")
    @ApiModelProperty(value = "收藏者ID")
    private Long belong;

    /**
     * 被收藏的教学资料ID
     */
    @TableField(value = "collect_resource")
    @ApiModelProperty(value = "被收藏的教学资料ID")
    private Long resource;

    /**
     * 被收藏的教学资料名称
     */
    @TableField(value = "collect_name")
    @ApiModelProperty(value = "被收藏的教学资料名称")
    private String name;

    /**
     * 被收藏的教学资料简介
     */
    @TableField(value = "collect_info")
    @ApiModelProperty(value = "被收藏的教学资料简介")
    private String info;

    /**
     * 收藏状态
     */
    @TableField(value = "collect_status", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "收藏状态（0表示被收藏，1表示取消收藏）")
    private Integer status;

    /**
     * 收藏时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "收藏时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "逻辑删除（0表示未删除，1表示已删除）")
    @TableLogic
    private Integer isDeleted;

    public static final String COL_COLLECT_ID = "collect_id";

    public static final String COL_COLLECT_BELONG = "collect_belong";

    public static final String COL_COLLECT_RESOURCE = "collect_resource";

    public static final String COL_COLLECT_NAME = "collect_name";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";
}
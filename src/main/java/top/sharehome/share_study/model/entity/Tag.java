package top.sharehome.share_study.model.entity;

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
 * 资料标签表
 *
 * @author AntonyCheng
 */
@ApiModel(description = "share_study.t_tag")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "share_study.t_tag")
public class Tag implements Serializable {


    /**
     * 资料标签唯一ID
     */
    @TableId(value = "tag_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "资料标签唯一ID")
    private Long id;

    /**
     * 所属学校ID
     */
    @TableField(value = "tag_belong")
    @ApiModelProperty(value = "所属学校ID")
    private Long belong;

    /**
     * 资料标签名称
     */
    @TableField(value = "tag_name")
    @ApiModelProperty(value = "资料标签名称")
    private String name;

    /**
     * 资料标签发布时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "资料标签发布时间")
    private LocalDateTime createTime;

    /**
     * 资料标签修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "资料标签修改时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "逻辑删除（0表示未删除，1表示已删除）")
    @TableLogic
    private Integer isDeleted;

    private static final long serialVersionUID = -8801011941741951638L;

    public static final String COL_TAG_ID = "tag_id";

    public static final String COL_TAG_BELONG = "tag_belong";

    public static final String COL_TAG_NAME = "tag_name";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";
}
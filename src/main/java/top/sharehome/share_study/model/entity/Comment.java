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
 * 评论交流表
 *
 * @author AntonyCheng
 */
@ApiModel(description = "评论交流表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "share_study.t_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 7445732495167655770L;
    /**
     * 评论交流唯一ID
     */
    @TableId(value = "comment_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "评论交流唯一ID")
    private Long id;

    /**
     * 评论所属资料ID
     */
    @TableField(value = "comment_resource")
    @ApiModelProperty(value = "评论所属资料ID")
    private Long resource;

    /**
     * 评论的教师用户ID
     */
    @TableField(value = "comment_belong")
    @ApiModelProperty(value = "评论的教师用户ID")
    private Long belong;

    /**
     * 接收评论的教师用户ID
     */
    @TableField(value = "comment_send")
    @ApiModelProperty(value = "接收评论的教师用户ID")
    private Long send;

    /**
     * 评论内容
     */
    @TableField(value = "comment_content")
    @ApiModelProperty(value = "评论内容")
    private String content;

    /**
     * 评论是否已读（0表示未读，1表示已读）
     */
    @TableField(value = "comment_read", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "评论是否已读（0表示未读，1表示已读）")
    private Integer read;

    /**
     * 评论状态(0表示正常，1表示已被封禁)
     */
    @TableField(value = "comment_status", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "评论状态(0表示正常，1表示已被封禁)")
    private Integer status;

    /**
     * 评论发布时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "评论发布时间")
    private LocalDateTime createTime;

    /**
     * 评论更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "评论更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "逻辑删除（0表示未删除，1表示已删除）")
    @TableLogic
    private Integer isDeleted;

    public static final String COL_COMMENT_ID = "comment_id";

    public static final String COL_COMMENT_RESOURCE = "comment_resource";

    public static final String COL_COMMENT_BELONG = "comment_belong";

    public static final String COL_COMMENT_SEND = "comment_send";

    public static final String COL_COMMENT_CONTENT = "comment_content";

    public static final String COL_COMMENT_READ = "comment_read";

    public static final String COL_COMMENT_STATUS = "comment_status";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";
}
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
 * 高校表
 *
 * @author AntonyCheng
 */
@ApiModel(description = "高校表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "share_study.t_college")
public class College implements Serializable {
    private static final long serialVersionUID = -2268805076533271998L;
    /**
     * 高校唯一ID
     */
    @TableId(value = "college_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "高校唯一ID")
    private Long id;

    /**
     * 高校名称
     */
    @TableField(value = "college_name")
    @ApiModelProperty(value = "高校名称")
    private String name;

    /**
     * 所属上级
     */
    @TableField(value = "college_belong")
    @ApiModelProperty(value = "所属上级")
    private String belong;

    /**
     * 高校地址
     */
    @TableField(value = "college_address")
    @ApiModelProperty(value = "高校地址")
    private String address;

    /**
     * 院校代码
     */
    @TableField(value = "college_code")
    @ApiModelProperty(value = "院校代码")
    private String code;

    /**
     * 高校录入时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "高校录入时间")
    private LocalDateTime createTime;

    /**
     * 高校更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "高校更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除（0表示未删除，1表示已删除）
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "逻辑删除（0表示未删除，1表示已删除）")
    @TableLogic
    private Integer isDeleted;

    public static final String COL_COLLEGE_ID = "college_id";

    public static final String COL_COLLEGE_NAME = "college_name";

    public static final String COL_COLLEGE_BELONG = "college_belong";

    public static final String COL_COLLEGE_ADDRESS = "college_address";

    public static final String COL_COLLEGE_CODE = "college_code";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";

    public static final String COL_IS_DELETED = "is_deleted";
}
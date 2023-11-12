package top.sharehome.share_study.model.dto.tag;

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

/**
 * 资料标签分页查询接口
 *
 * @author AntonyCheng
 * @since 2023/6/22 11:25:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "资料标签分页查询接口")
public class TagPageDto implements Serializable {

    private static final long serialVersionUID = 5240817124843020000L;

    /**
     * 资料标签唯一ID
     */
    private Long id;

    /**
     * 所属学校名称
     */
    private String belong;

    /**
     * 资料标签名称
     */
    private String name;

    /**
     * 资料标签发布时间
     */
    private LocalDateTime createTime;
}

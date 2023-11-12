package top.sharehome.share_study.model.vo.tag;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 资料标签修改Vo对象
 *
 * @author AntonyCheng
 * @since 2023/6/22 10:39:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "资料标签修改Vo对象")
public class TagUpdateVo implements Serializable {

    private static final long serialVersionUID = -5198088308964423884L;
    /**
     * 资料标签id
     */
    private Long id;
    /**
     * 资料标签名称
     */
    private String name;
}

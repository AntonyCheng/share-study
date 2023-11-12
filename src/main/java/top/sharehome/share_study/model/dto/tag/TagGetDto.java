package top.sharehome.share_study.model.dto.tag;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author AntonyCheng
 * @since 2023/6/22 10:03:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "资料标签回显对象")
public class TagGetDto implements Serializable {

    private static final long serialVersionUID = 8556980862720139674L;

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
}

package top.sharehome.share_study.model.vo.tag;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 资料标签Vo
 *
 * @author AntonyCheng
 * @since 2023/6/21 22:37:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("资料标签Vo")
public class TagAddVo implements Serializable {

    private static final long serialVersionUID = -3952012789618655269L;

    /**
     * 所属学校ID
     */
    private Long belong;

    /**
     * 资料标签名称
     */
    private String name;
}

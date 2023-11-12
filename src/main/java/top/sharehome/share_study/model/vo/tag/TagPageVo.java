package top.sharehome.share_study.model.vo.tag;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 资料标签分页Vo对象
 *
 * @author AntonyCheng
 * @since 2023/6/22 11:32:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("资料标签分页Vo对象")
public class TagPageVo implements Serializable {

    private static final long serialVersionUID = -8119545331877627071L;

    /**
     * 资料标签名称
     */
    private String name;

    /**
     * 所属高校名称
     */
    private Long belong;
}

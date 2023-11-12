package top.sharehome.share_study.model.vo.resource_censor;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理员审核教学资料Dto实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("管理员审核教学资料Dto实体")
public class ResourceCensorUpdateVo implements Serializable {
    private static final long serialVersionUID = -1346612842062264072L;

    /**
     * 资料审核唯一ID
     */
    private Long id;

    /**
     * 审核结果，1表示审核通过，2表示审核不通过
     */
    private Integer result;

    /**
     * 审核结论
     */
    private String reason;
}

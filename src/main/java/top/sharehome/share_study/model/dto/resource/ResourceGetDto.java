package top.sharehome.share_study.model.dto.resource;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理员获取教学资料信息Dto实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("管理员获取教学资料信息Dto实体")
public class ResourceGetDto implements Serializable {
    private static final long serialVersionUID = -349851586228248039L;
    /**
     * 教学资料唯一ID
     */
    private Long id;

    /**
     * 教学资料状态（0表示正常，1表示封禁）
     */
    private Integer status;
}

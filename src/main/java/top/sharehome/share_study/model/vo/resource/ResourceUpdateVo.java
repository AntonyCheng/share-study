package top.sharehome.share_study.model.vo.resource;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理员获取教学资料信息Vo实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("管理员获取教学资料信息Vo实体")
public class ResourceUpdateVo implements Serializable {
    private static final long serialVersionUID = 3197825714172938682L;
    /**
     * 教学资料唯一ID
     */
    private Long id;

    /**
     * 教学资料状态（0表示正常，1表示封禁）
     */
    private Integer status;
}

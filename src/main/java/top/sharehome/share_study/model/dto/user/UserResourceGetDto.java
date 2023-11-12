package top.sharehome.share_study.model.dto.user;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 普通用户获取教学资料信息Dto实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("普通用户获取教学资料信息Dto实体")
public class UserResourceGetDto implements Serializable {

    private static final long serialVersionUID = -5260008520059484796L;

    /**
     * 教学资料唯一ID
     */
    private Long id;

    /**
     * 教学资料名
     */
    private String name;

    /**
     * 教学资料简介
     */
    private String info;

    /**
     * 教学资料所在地址
     */
    private String url;
}

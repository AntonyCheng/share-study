package top.sharehome.share_study.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 普通用户修改教学资料信息Vo实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("普通用户修改教学资料信息Vo实体")
public class UserResourceUpdateVo implements Serializable {

    private static final long serialVersionUID = 6807334865856777320L;

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

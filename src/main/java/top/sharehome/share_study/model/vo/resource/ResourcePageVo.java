package top.sharehome.share_study.model.vo.resource;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教学资料分页Vo对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("教学资料分页Vo对象")
public class ResourcePageVo implements Serializable {

    private static final long serialVersionUID = 5740229842935906781L;

    /**
     * 所属老师名称
     */
    private String belongName;

    /**
     * 教学资料名
     */
    private String name;

    /**
     * 教学资料简介
     */
    private String info;

    /**
     * 资料标签唯一ID
     */
    private Long tag;

    /**
     * 教学资料状态（0表示正常，1表示封禁）
     */
    private Integer status;

}

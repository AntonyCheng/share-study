package top.sharehome.share_study.model.vo.user;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 用户收藏分页Vo对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户收藏分页Vo对象")
public class UserCollectPageVo implements Serializable {

    private static final long serialVersionUID = 6173915539622183589L;
    /**
     * 教学资料名称
     */
    private String resourceName;

    /**
     * 教学资料简介
     */
    private String resourceInfo;
}

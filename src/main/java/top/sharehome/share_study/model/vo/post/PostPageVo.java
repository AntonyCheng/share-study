package top.sharehome.share_study.model.vo.post;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户帖子分页Vo对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户帖子分页Vo对象")
public class PostPageVo implements Serializable {

    private static final long serialVersionUID = -8936515880948037177L;
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
     * 用户高校名称
     */
    private String collegeName;

    /**
     * 用户高校唯一ID
     */
    private Long collegeId;

    /**
     * 教学资料标签唯一ID
     */
    private Long tagId;
}

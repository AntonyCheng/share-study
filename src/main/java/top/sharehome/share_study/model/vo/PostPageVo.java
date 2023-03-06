package top.sharehome.share_study.model.vo;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户帖子分页Vo对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户帖子分页Vo对象")
public class PostPageVo {

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
}

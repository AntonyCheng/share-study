package top.sharehome.share_study.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 发布评论Vo对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("发布评论Vo对象")
public class PostCommentAddVo implements Serializable {

    private static final long serialVersionUID = -3361293653271899606L;

    /**
     * 评论所属资料ID
     */
    private Long resource;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 接收评论的教师用户ID
     */
    private Long send;

    /**
     * 评论中所带的文件OSSUrl
     */
    private String url;
}

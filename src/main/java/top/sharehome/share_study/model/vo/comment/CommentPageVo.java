package top.sharehome.share_study.model.vo.comment;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 交流评论分页Vo对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("交流评论分页Vo对象")
public class CommentPageVo implements Serializable {

    private static final long serialVersionUID = 2518851642473959864L;

    /**
     * 评论所属资料名称
     */
    private String resourceName;

    /**
     * 评论的教师用户名称
     */
    private String belongName;

    /**
     * 接收评论的教师用户名称
     */
    private String sendName;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论是否已读（0表示未读，1表示已读）
     */
    private Integer readStatus;

    /**
     * 评论状态(0表示正常，1表示已被封禁)
     */
    private Integer status;
}

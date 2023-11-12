package top.sharehome.share_study.model.dto.post;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 帖子评论分页Dto对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "帖子评论分页Dto对象")
public class PostCommentPageDto implements Serializable {

    private static final long serialVersionUID = 8449307531518597096L;

    /**
     * 发送者ID
     */
    private Long belong;

    /**
     * 发送者名称
     */
    private String belongName;

    /**
     * 发送者头像OSS链接
     */
    private String belongAvatarUrl;

    /**
     * 发送者高校名称
     */
    private String belongCollege;

    /**
     * 接收者ID
     */
    private Long send;

    /**
     * 接收者名称
     */
    private String sendName;

    /**
     * 接收者头像OSS链接
     */
    private String sendAvatarUrl;

    /**
     * 接收者高校名称
     */
    private String sendCollege;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 评论链接
     */
    private String commentOssUrl;

    /**
     * 评论状态
     */
    private Integer commentStatus;

    /**
     * 评论所在教学资料ID
     */
    private Long resourceId;

}

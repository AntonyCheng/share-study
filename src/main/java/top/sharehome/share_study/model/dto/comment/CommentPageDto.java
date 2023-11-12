package top.sharehome.share_study.model.dto.comment;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 交流评论分页回显对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "交流评论分页回显对象")
public class CommentPageDto implements Serializable {

    private static final long serialVersionUID = 6858956259218551127L;

    /**
     * 评论交流唯一ID
     */
    private Long id;

    /**
     * 评论所属资料ID
     */
    private Long resource;

    /**
     * 评论所属资料名称
     */
    private String resourceName;

    /**
     * 评论的教师用户ID
     */
    private Long belong;

    /**
     * 评论的教师用户名称
     */
    private String belongName;

    /**
     * 接收评论的教师用户ID
     */
    private Long send;

    /**
     * 接收评论的教师用户名称
     */
    private String sendName;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论中所带的文件OSSUrl
     */
    private String url;

    /**
     * 评论是否已读（0表示未读，1表示已读）
     */
    private Integer readStatus;

    /**
     * 评论状态(0表示正常，1表示已被封禁)
     */
    private Integer status;

    /**
     * 评论发布时间
     */
    private LocalDateTime createTime;
}

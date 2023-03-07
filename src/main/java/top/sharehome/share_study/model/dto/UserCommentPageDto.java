package top.sharehome.share_study.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户评论分页Dto对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户评论分页Dto对象")
public class UserCommentPageDto implements Serializable {

    private static final long serialVersionUID = -8663483195169571839L;
    /**
     * 评论所在资料ID
     */
    private Long resourceId;

    /**
     * 评论所属资料名称
     */
    private String resourceName;

    /**
     * 评论的教师用户ID
     */
    private Long belongId;

    /**
     * 评论的教师用户名称
     */
    private String belongName;

    /**
     * 评论交流唯一ID
     */
    private Long id;

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

    /**
     * 评论发布时间
     */
    private LocalDateTime createTime;
}

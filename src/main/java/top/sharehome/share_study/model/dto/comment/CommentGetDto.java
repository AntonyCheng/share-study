package top.sharehome.share_study.model.dto.comment;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理员获取交流评论信息Dto实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("管理员获取交流评论信息Dto实体")
public class CommentGetDto implements Serializable {

    private static final long serialVersionUID = -4605847397561928198L;
    /**
     * 评论交流唯一ID
     */
    private Long id;

    /**
     * 评论状态(0表示正常，1表示已被封禁)
     */
    private Integer status;

}

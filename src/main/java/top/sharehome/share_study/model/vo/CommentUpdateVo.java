package top.sharehome.share_study.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 交流评论修改Vo对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "交流评论修改Vo对象")
public class CommentUpdateVo implements Serializable {

    private static final long serialVersionUID = 2804852992837405644L;
    /**
     * 评论交流唯一ID
     */
    private Long id;

    /**
     * 评论状态(0表示正常，1表示已被封禁)
     */
    private Integer status;
}

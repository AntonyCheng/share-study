package top.sharehome.share_study.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户收藏分页Dto对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户收藏分页Dto对象")
public class UserCollectPageDto implements Serializable {

    private static final long serialVersionUID = 179284972341642845L;

    /**
     * 用户唯一ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户头像OSS链接
     */
    private String userAvatarUrl;

    /**
     * 用户高校名称
     */
    private String collegeName;

    /**
     * 教学资料唯一ID
     */
    private Long resourceId;

    /**
     * 教学资料名称
     */
    private String resourceName;

    /**
     * 教学资料简介
     */
    private String resourceInfo;

    /**
     * 教学资料OSS链接
     */
    private String resourceUrl;

    /**
     * 教学资料状态
     */
    private Integer resourceStatus;

    /**
     * 收藏ID
     */
    private Long collectId;

    /**
     * 收藏时间
     */
    private LocalDateTime createTime;
}

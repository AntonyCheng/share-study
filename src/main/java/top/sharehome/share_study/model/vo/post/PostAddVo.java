package top.sharehome.share_study.model.vo.post;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 添加帖子Vo
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("添加帖子Vo")
public class PostAddVo implements Serializable {

    private static final long serialVersionUID = -5409731863134275220L;

    /**
     * 教学资料所有人唯一ID
     */
    private Long belong;

    /**
     * 教学资料名称
     */
    private String name;

    /**
     * 教学资料简介
     */
    private String info;

    /**
     * 教学资料OSS链接
     */
    private String url;

    /**
     * 教学资料标签列表
     */
    private List<Long> tags;
}

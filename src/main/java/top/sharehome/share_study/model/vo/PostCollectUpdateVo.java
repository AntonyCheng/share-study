package top.sharehome.share_study.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 帖子更新收藏Vo对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("帖子更新收藏Vo对象")
public class PostCollectUpdateVo implements Serializable {

    private static final long serialVersionUID = 1330376642781767348L;

    /**
     * 收藏者ID
     */
    private Long belong;

    /**
     * 被收藏的教学资料ID
     */
    private Long resource;
}

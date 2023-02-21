package top.sharehome.share_study.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 高校修改Vo对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "高校修改Vo对象")
public class CollegeUpdateVo implements Serializable {
    private static final long serialVersionUID = -3904577999103113993L;
    /**
     * 高校唯一ID
     */
    private Long id;

    /**
     * 高校名称
     */
    private String name;

    /**
     * 院校代码
     */
    private String code;
}

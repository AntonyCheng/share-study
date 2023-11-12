package top.sharehome.share_study.model.vo.college;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 高校添加Vo
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("高校添加Vo")
public class CollegeAddVo implements Serializable {
    /**
     * 高校名称
     */
    private String name;

    /**
     * 院校代码
     */
    private String code;

    private static final long serialVersionUID = 401511380398574226L;
}

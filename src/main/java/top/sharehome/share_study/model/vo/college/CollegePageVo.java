package top.sharehome.share_study.model.vo.college;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 高校分页Vo对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("高校分页Vo对象")
public class CollegePageVo implements Serializable {
    private static final long serialVersionUID = -3499793852184056658L;
    /**
     * 高校名称
     */
    private String name;

    /**
     * 院校代码
     */
    private String code;

}

package top.sharehome.share_study.model.vo.teacher_censor;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 注册申请状态更新Vo实体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("注册申请状态更新Vo实体")
public class TeacherCensorUpdateVo implements Serializable {

    private static final long serialVersionUID = -827683492091780172L;
    /**
     * 注册申请ID
     */
    private Long id;

    /**
     * 发布时反馈内容
     */
    private String content;

    /**
     * 注册申请结果
     */
    private Integer result;
}

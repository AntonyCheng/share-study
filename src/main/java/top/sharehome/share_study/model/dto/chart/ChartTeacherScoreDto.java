package top.sharehome.share_study.model.dto.chart;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教师贡献排行数据对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "教师贡献排行数据对象")
public class ChartTeacherScoreDto implements Serializable {

    private static final long serialVersionUID = -6074388157201180541L;

    /**
     * 教师姓名
     */
    private String name;

    /**
     * 教师贡献度
     */
    private Integer score;
}

package top.sharehome.share_study.model.dto.chart;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 高校贡献比数据对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "高校贡献比数据对象")
public class ChartCollegeScoreDto implements Serializable {
    private static final long serialVersionUID = 4907096577424896302L;

    /**
     * 高校名称
     */
    private String name;

    /**
     * 高校贡献度
     */
    private Integer score;
}

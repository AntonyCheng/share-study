package top.sharehome.share_study.model.dto.chart;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 高校坐标可视化对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "高校坐标可视化对象")
public class ChartCollegeLocationDto implements Serializable {
    private static final long serialVersionUID = 3868711196465792346L;

    /**
     * 高校名称
     */
    private String name;

    /**
     * 高校人数
     */
    private Integer number;

    /**
     * 高校坐标
     */
    private String location;
}

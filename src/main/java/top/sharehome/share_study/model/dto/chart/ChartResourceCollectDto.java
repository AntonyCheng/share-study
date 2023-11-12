package top.sharehome.share_study.model.dto.chart;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 资料收藏排行数据对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "资料收藏排行数据对象")
public class ChartResourceCollectDto implements Serializable {
    private static final long serialVersionUID = 6388333831171002409L;

    /**
     * 教学资料名称
     */
    private String name;

    /**
     * 教学资料所属老师名称
     */
    private String belongName;

    /**
     * 教学资料收藏数量
     */
    private Integer score;
}

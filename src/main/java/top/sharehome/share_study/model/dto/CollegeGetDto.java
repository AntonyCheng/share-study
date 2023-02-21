package top.sharehome.share_study.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 高校回显对象
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "高校回显对象")
public class CollegeGetDto implements Serializable {

    private static final long serialVersionUID = -1782554073965997491L;
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

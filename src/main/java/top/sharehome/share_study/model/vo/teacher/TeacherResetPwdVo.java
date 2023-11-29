package top.sharehome.share_study.model.vo.teacher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 重置教师用户密码Vo
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherResetPwdVo implements Serializable {

    private static final long serialVersionUID = 1139536556878216715L;

    /**
     * 教师用户唯一ID
     */
    private Long id;

}

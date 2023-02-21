package top.sharehome.share_study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.share_study.model.dto.CollegeGetDto;
import top.sharehome.share_study.model.dto.CollegePageDto;
import top.sharehome.share_study.model.entity.College;
import top.sharehome.share_study.model.vo.CollegeAddVo;
import top.sharehome.share_study.model.vo.CollegePageVo;
import top.sharehome.share_study.model.vo.CollegeUpdateVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 高校Service
 *
 * @author AntonyCheng
 */
public interface CollegeService extends IService<College> {

    /**
     * 添加高校
     *
     * @param collegeAddVo 高校名称和代码
     */
    void add(CollegeAddVo collegeAddVo);

    /**
     * 删除高校
     *
     * @param id 高校ID
     */
    void delete(Long id);

    /**
     * 获取单个高校信息
     *
     * @param id 高校ID
     * @return 返回对应高校信息
     */
    CollegeGetDto get(Long id);

    /**
     * 修改高校接口
     *
     * @param collegeUpdateVo 高校修改Vo对象
     */
    void updateCollege(CollegeUpdateVo collegeUpdateVo);

    /**
     * 高校数据分页查询
     *
     * @param current       当前页
     * @param pageSize      页面条数
     * @param collegePageVo 高校分页Vo对象
     * @return 返回分页结果
     */
    Page<CollegePageDto> pageCollege(Integer current, Integer pageSize, CollegePageVo collegePageVo);

    /**
     * 批量删除高校
     *
     * @param ids 需要批量删除的高校id
     */
    void deleteBath(List<Long> ids);

    /**
     * 高校信息导出
     *
     * @param response 响应
     */
    void download(HttpServletResponse response);
}

package top.sharehome.share_study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.share_study.model.dto.college.CollegeGetDto;
import top.sharehome.share_study.model.dto.college.CollegePageDto;
import top.sharehome.share_study.model.dto.tag.TagGetDto;
import top.sharehome.share_study.model.entity.College;
import top.sharehome.share_study.model.vo.college.CollegeAddVo;
import top.sharehome.share_study.model.vo.college.CollegePageVo;
import top.sharehome.share_study.model.vo.college.CollegeUpdateVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

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
     * @param request       获取session对象
     * @param collegePageVo 高校分页Vo对象
     * @return 返回分页结果
     */
    Page<CollegePageDto> pageCollege(Integer current, Integer pageSize, HttpServletRequest request
            , CollegePageVo collegePageVo);

    /**
     * 批量删除高校
     *
     * @param ids 需要批量删除的高校id
     */
    void deleteBatch(List<Long> ids);

    /**
     * 高校信息导出
     *
     * @param response 响应
     */
    void download(HttpServletResponse response);

    /**
     * 高校ID和对应名称的List
     *
     * @return 高校名称List
     */
    List<CollegeGetDto> listCollege();

    /**
     * 带有老师的高校ID和对应名称的List
     *
     * @return 高校名称List
     */
    Map<CollegeGetDto,List<TagGetDto>> mapCollegeContainTeacher();

    /**
     * 带有资料标签的高校ID和对应名称的List
     *
     * @return 高校名称List
     */
    Map<CollegeGetDto,List<TagGetDto>> mapCollegeContainTag();

}

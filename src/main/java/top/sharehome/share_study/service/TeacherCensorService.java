package top.sharehome.share_study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.share_study.model.dto.teacher_censor.TeacherCensorPageDto;
import top.sharehome.share_study.model.entity.TeacherCensor;
import top.sharehome.share_study.model.vo.teacher.TeacherRegisterVo;
import top.sharehome.share_study.model.vo.teacher_censor.TeacherCensorPageVo;
import top.sharehome.share_study.model.vo.teacher_censor.TeacherCensorUpdateVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 教师用户注册审核Service
 *
 * @author AntonyCheng
 */
public interface TeacherCensorService extends IService<TeacherCensor> {
    /**
     * 用户注册进入审核状态
     *
     * @param teacherRegisterVo 用户注册Vo
     */
    void addTeacherCensor(TeacherRegisterVo teacherRegisterVo);

    /**
     * 发布审核完成的教师申请
     *
     * @param id      审核完成的教师申请ID
     * @param request 获取Session中的登录状态
     */
    void publishTeacherCensor(Long id, HttpServletRequest request);

    /**
     * 注册申请审核状态更新
     *
     * @param teacherCensorUpdateVo 审核状态更新对象
     * @param request               获取Session登录状态
     * @return 返回更新结果
     */
    Boolean updateTeacherCensor(TeacherCensorUpdateVo teacherCensorUpdateVo, HttpServletRequest request);

    /**
     * 审核资料分页操作
     *
     * @param current             当前页
     * @param pageSize            页面条数
     * @param teacherCensorPageVo 注册申请审核分页Vo对象
     * @return 返回分页结果
     */
    Page<TeacherCensorPageDto> pageTeacherCensor(Integer current, Integer pageSize, TeacherCensorPageVo teacherCensorPageVo);

    /**
     * 注册申请删除记录操作
     *
     * @param id      待删除审核记录ID
     * @param request 获取Session中登录状态
     */
    void deleteTeacherCensor(Long id, HttpServletRequest request);

    /**
     * 注册申请批量删除记录操作
     *
     * @param ids     待批量删除审核记录ID
     * @param request 获取Session中登录状态
     */
    void deleteBatchTeacherCensor(List<Long> ids, HttpServletRequest request);
}

package top.sharehome.share_study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.share_study.model.dto.admin.AdminGetDto;
import top.sharehome.share_study.model.dto.admin.AdminGetSelfDto;
import top.sharehome.share_study.model.dto.admin.AdminPageDto;
import top.sharehome.share_study.model.dto.teacher.TeacherGetDto;
import top.sharehome.share_study.model.dto.teacher.TeacherLoginDto;
import top.sharehome.share_study.model.dto.teacher.TeacherPageDto;
import top.sharehome.share_study.model.dto.user.UserGetInfoDto;
import top.sharehome.share_study.model.entity.Teacher;
import top.sharehome.share_study.model.vo.admin.AdminPageVo;
import top.sharehome.share_study.model.vo.admin.AdminUpdateSelfVo;
import top.sharehome.share_study.model.vo.admin.AdminUpdateVo;
import top.sharehome.share_study.model.vo.teacher.TeacherLoginVo;
import top.sharehome.share_study.model.vo.teacher.TeacherPageVo;
import top.sharehome.share_study.model.vo.teacher.TeacherRegisterVo;
import top.sharehome.share_study.model.vo.teacher.TeacherUpdateVo;
import top.sharehome.share_study.model.vo.user.UserUpdateInfoSelfVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 教师用户Service
 *
 * @author AntonyCheng
 */
public interface TeacherService extends IService<Teacher> {
    /**
     * 注册业务方法
     *
     * @param teacherRegisterVo 教师登录VO实体
     */
    void register(TeacherRegisterVo teacherRegisterVo);

    /**
     * 普通用户登录业务方法
     *
     * @param teacherLoginVo 老师登录传来的账号密码
     * @param request        为了设置登陆状态
     * @return 登陆类型
     */
    TeacherLoginDto login(TeacherLoginVo teacherLoginVo, HttpServletRequest request);

    /**
     * 管理员登录业务方法
     *
     * @param teacherLoginVo 管理员登录传来的账号密码
     * @param request        为了设置登录状态
     * @return 登录类型
     */
    TeacherLoginDto adminLogin(TeacherLoginVo teacherLoginVo, HttpServletRequest request);

    /**
     * 管理员获取自己信息接口
     *
     * @param id      管理员ID
     * @param request 获取登录的Session状态
     * @return 返回管理员自己的可修改信息
     */
    AdminGetSelfDto getSelf(Long id, HttpServletRequest request);

    /**
     * 管理员修改自己信息接口
     *
     * @param adminUpdateSelfVo 管理员更新自己信息Vo实体
     * @param request           获取Session中的登录状态
     */
    void updateSelf(AdminUpdateSelfVo adminUpdateSelfVo, HttpServletRequest request);

    /**
     * 超级管理员获取管理员信息接口
     *
     * @param id      管理员ID
     * @param request 获取登录的Session状态
     * @return 返回管理员可修改信息
     */
    AdminGetDto getAdmin(Long id, HttpServletRequest request);

    /**
     * 超级管理员修改管理员信息接口
     *
     * @param adminUpdateVo 超级管理员更新管理员信息Vo实体
     * @param request       获取Session中的登录状态
     */
    void updateAdmin(AdminUpdateVo adminUpdateVo, HttpServletRequest request);

    /**
     * 管理员分页查询接口（可以做到姓名模糊查询以及学院代码模糊查询）
     *
     * @param current     当前页
     * @param pageSize    页面条数
     * @param adminPageVo 管理员分页Vo对象
     * @return 返回分页结果
     */
    Page<AdminPageDto> pageAdmin(Integer current, Integer pageSize, HttpServletRequest request, AdminPageVo adminPageVo);

    /**
     * 管理员信息导出
     *
     * @param response 响应
     */
    void downloadAdmin(HttpServletResponse response);

    /**
     * 教师信息导出
     *
     * @param response 响应
     */
    void downloadTeacher(HttpServletResponse response);

    /**
     * 删除教师信息
     *
     * @param id 高校ID
     */
    void delete(Long id);

    /**
     * 批量删除教师接口
     *
     * @param ids 教师接口列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * 管理员获取教师信息接口
     *
     * @param id      教师ID
     * @param request 获取登录的Session状态
     * @return 返回教师可修改信息
     */
    TeacherGetDto getTeacher(Long id, HttpServletRequest request);

    /**
     * 管理员修改教师信息接口
     *
     * @param teacherUpdateVo 管理员更新教师信息Vo实体
     * @param request         获取Session中的登录状态
     */
    void updateTeacher(TeacherUpdateVo teacherUpdateVo, HttpServletRequest request);

    /**
     * 教师分页查询接口
     *
     * @param current       当前页
     * @param pageSize      页面条数
     * @param teacherPageVo 教师分页Vo对象
     * @return 返回分页结果
     */
    Page<TeacherPageDto> pageTeacher(Integer current, Integer pageSize, TeacherPageVo teacherPageVo);

    /**
     * 管理员获取登录状态（需要有登录状态才能获取）
     *
     * @param id      前端传来的操作者的id
     * @param request 获取Session中的登录状态
     * @return 返回最新的登录状态
     */
    TeacherLoginDto getAdminLogin(Long id, HttpServletRequest request);

    /**
     * 普通用户获取登录状态（需要有登录状态才能获取）
     *
     * @param id      前端传来的操作者的id
     * @param request 获取Session中的登录状态
     * @return 返回最新的登录状态
     */
    TeacherLoginDto getUserLogin(Long id, HttpServletRequest request);

    /**
     * 普通用户获取自己信息接口（s/a/u）
     *
     * @param id      普通用户ID
     * @param request 获取Session中登录状态
     * @return 返回普通用户自己的可修改信息
     */
    UserGetInfoDto getUserInfo(Long id, HttpServletRequest request);

    /**
     * 普通用户修改自己信息接口（s/a/u）
     *
     * @param userUpdateInfoSelfVo 普通用户更新自己信息Vo实体
     * @param request              获取Session中的登录状态
     */
    void updateUserSelf(UserUpdateInfoSelfVo userUpdateInfoSelfVo, HttpServletRequest request);

    /**
     * 根据教师Id重置教师密码
     *
     * @param id      教师用户ID
     * @param request 获取Session中登录状态
     */
    void resetPwdById(Long id, HttpServletRequest request);
}

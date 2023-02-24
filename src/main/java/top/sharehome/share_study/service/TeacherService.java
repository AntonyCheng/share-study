package top.sharehome.share_study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.sharehome.share_study.model.dto.AdminGetDto;
import top.sharehome.share_study.model.dto.AdminGetSelfDto;
import top.sharehome.share_study.model.dto.AdminPageDto;
import top.sharehome.share_study.model.dto.TeacherLoginDto;
import top.sharehome.share_study.model.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.share_study.model.vo.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    Page<AdminPageDto> pageAdmin(Integer current, Integer pageSize, AdminPageVo adminPageVo);

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
}

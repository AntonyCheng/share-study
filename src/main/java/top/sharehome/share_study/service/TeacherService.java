package top.sharehome.share_study.service;

import top.sharehome.share_study.model.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.share_study.model.vo.TeacherLoginVo;
import top.sharehome.share_study.model.vo.TeacherRegisterVo;

import javax.servlet.http.HttpServletRequest;

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
     * @param request 为了设置登陆状态
     */
    void login(TeacherLoginVo teacherLoginVo, HttpServletRequest request);

    /**
     * 管理员登录业务方法
     *
     * @param teacherLoginVo 管理员登录传来的账号密码
     * @param request 为了设置登录状态
     */
    void adminLogin(TeacherLoginVo teacherLoginVo, HttpServletRequest request);
}

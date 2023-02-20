package top.sharehome.share_study.service.impl;

import java.time.LocalDateTime;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.mapper.TeacherMapper;
import top.sharehome.share_study.model.dto.TeacherLoginDto;
import top.sharehome.share_study.model.entity.College;
import top.sharehome.share_study.model.entity.Teacher;
import top.sharehome.share_study.model.vo.TeacherLoginVo;
import top.sharehome.share_study.model.vo.TeacherRegisterVo;
import top.sharehome.share_study.service.TeacherService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 教师用户ServiceImpl
 *
 * @author AntonyCheng
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    @Resource
    private TeacherMapper teacherMapper;

    /**
     * 注册加盐
     */
    private static final String SALT = "share_study_platform";

    @Override
    public void register(TeacherRegisterVo teacherRegisterVo) {
        // 校验数据库中是否包含该用户
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getAccount, teacherRegisterVo.getAccount());
        Teacher resultFromDatabase = teacherMapper.selectOne(queryWrapper);
        if (resultFromDatabase != null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_ALREADY_EXISTS), "数据库中已经包含该用户：" + teacherRegisterVo.getAccount());
        }

        // 进行数据拷贝和插入
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherRegisterVo, teacher);
        teacher.setPassword(DigestUtil.md5Hex(teacher.getPassword() + SALT));
        int insertResult = teacherMapper.insert(teacher);

        // 判断数据库插入结果
        if (insertResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_ADDITION_FAILED), "注册插入用户失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    public void login(TeacherLoginVo teacherLoginVo, HttpServletRequest request) {
        // 首先获取对象中的基本属性
        String accountBeforeLogin = teacherLoginVo.getAccount();
        String passwordBeforeLogin = teacherLoginVo.getPassword();

        // 根据账户查询该用户存在与否
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getAccount, accountBeforeLogin);
        Teacher teacher = teacherMapper.selectOne(queryWrapper);
        if (teacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST));
        }

        // 查询用户状态是否为可用
        if (teacher.getStatus() == 1) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_BANNED));
        }

        // 对比密码是否一致
        String passwordNeedCompare = DigestUtil.md5Hex(passwordBeforeLogin + SALT);
        if (!passwordNeedCompare.equals(teacher.getPassword())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.WRONG_USER_PASSWORD));
        }

        // 信息脱敏
        TeacherLoginDto teacherLoginDto = new TeacherLoginDto();
        teacherLoginDto.setId(teacher.getId());
        teacherLoginDto.setAccount(teacher.getAccount());
        teacherLoginDto.setAvatar(teacher.getAvatar());
        teacherLoginDto.setGender(teacher.getGender());
        // TODO:改写高校表
        teacherLoginDto.setCollegeName(teacher.getBelong().toString());
        String email = DesensitizedUtil.email(teacher.getEmail());
        teacherLoginDto.setEmail(email);
        teacherLoginDto.setMessageNumber(teacher.getMessageTotal() - teacher.getMessageRead());
        Integer role = teacher.getRole();
        String roleName = null;
        if (role == 0) {
            roleName = "普通用户";
        }
        if (role == 1) {
            roleName = "平台管理员";
        }
        if (role == 2) {
            roleName = "超级管理员";
        }
        teacherLoginDto.setRoleName(roleName);
        request.getSession().setAttribute(CommonConstant.USER_LOGIN_STATE, teacherLoginDto);
        System.out.println(request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE));
    }
}

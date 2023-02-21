package top.sharehome.share_study.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeTransactionException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.mapper.TeacherMapper;
import top.sharehome.share_study.model.dto.TeacherLoginDto;
import top.sharehome.share_study.model.entity.College;
import top.sharehome.share_study.model.entity.Teacher;
import top.sharehome.share_study.model.vo.TeacherLoginVo;
import top.sharehome.share_study.model.vo.TeacherRegisterVo;
import top.sharehome.share_study.service.CollegeService;
import top.sharehome.share_study.service.TeacherService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 教师用户ServiceImpl
 *
 * @author AntonyCheng
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private CollegeService collegeService;

    /**
     * 注册加盐
     */
    private static final String SALT = "share_study_platform";

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void register(TeacherRegisterVo teacherRegisterVo) {
        // 校验数据库中是否包含该用户
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teacher::getAccount, teacherRegisterVo.getAccount());
        Long resultFromDatabase = teacherMapper.selectCount(queryWrapper);
        if (resultFromDatabase != 0) {
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
    @Transactional(rollbackFor = CustomizeTransactionException.class)
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
        String name = DesensitizedUtil.chineseName(teacher.getName());
        teacherLoginDto.setName(name);
        teacherLoginDto.setAvatar(teacher.getAvatar());
        String gender = null;
        if (teacher.getGender() == 0) {
            gender = "男";
        }
        if (teacher.getGender() == 1) {
            gender = "女";
        }
        teacherLoginDto.setGender(gender);
        String collegeName = collegeService.getById(teacher.getBelong()).getName();
        teacherLoginDto.setCollegeName(collegeName);
        teacherLoginDto.setEmail(teacher.getEmail());
        teacherLoginDto.setMessageNumber(teacher.getMessageTotal() - teacher.getMessageRead());
        teacherLoginDto.setRole(teacher.getRole());
        request.getSession().setAttribute(CommonConstant.USER_LOGIN_STATE, teacherLoginDto);
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void adminLogin(TeacherLoginVo teacherLoginVo, HttpServletRequest request) {
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

        // 验证用户的身份
        //if (Objects.equals(teacher.getRole(), CommonConstant.DEFAULT_ROLE)) {
        //    throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED));
        //}

        // 信息脱敏
        TeacherLoginDto teacherLoginDto = new TeacherLoginDto();
        teacherLoginDto.setId(teacher.getId());
        teacherLoginDto.setAccount(teacher.getAccount());
        String name = DesensitizedUtil.chineseName(teacher.getName());
        teacherLoginDto.setName(name);
        teacherLoginDto.setAvatar(teacher.getAvatar());
        String gender = null;
        if (teacher.getGender() == 0) {
            gender = "男";
        }
        if (teacher.getGender() == 1) {
            gender = "女";
        }
        teacherLoginDto.setGender(gender);
        String collegeName = collegeService.getById(teacher.getBelong()).getName();
        teacherLoginDto.setCollegeName(collegeName);
        teacherLoginDto.setEmail(teacher.getEmail());
        teacherLoginDto.setMessageNumber(teacher.getMessageTotal() - teacher.getMessageRead());
        teacherLoginDto.setRole(teacher.getRole());
        request.getSession().setAttribute(CommonConstant.ADMIN_LOGIN_STATE, teacherLoginDto);
    }
}

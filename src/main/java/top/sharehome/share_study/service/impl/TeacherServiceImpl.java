package top.sharehome.share_study.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeFileException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeTransactionException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.mapper.TeacherMapper;
import top.sharehome.share_study.model.dto.AdminGetDto;
import top.sharehome.share_study.model.dto.AdminGetSelfDto;
import top.sharehome.share_study.model.dto.TeacherLoginDto;
import top.sharehome.share_study.model.entity.College;
import top.sharehome.share_study.model.entity.Teacher;
import top.sharehome.share_study.model.vo.AdminUpdateSelfVo;
import top.sharehome.share_study.model.vo.AdminUpdateVo;
import top.sharehome.share_study.model.vo.TeacherLoginVo;
import top.sharehome.share_study.model.vo.TeacherRegisterVo;
import top.sharehome.share_study.service.CollegeService;
import top.sharehome.share_study.service.TeacherService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherLambdaQueryWrapper.eq(Teacher::getAccount, teacherRegisterVo.getAccount());
        Long resultFromTeacher = teacherMapper.selectCount(teacherLambdaQueryWrapper);
        if (resultFromTeacher != 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_ALREADY_EXISTS), "数据库中已经包含该用户：" + teacherRegisterVo.getAccount());
        }

        // 查询用户输入的院校代码存不存在
        LambdaQueryWrapper<College> collegeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collegeLambdaQueryWrapper.eq(College::getCode, teacherRegisterVo.getCode());
        long resultFromCollege = collegeService.count(collegeLambdaQueryWrapper);
        if (resultFromCollege == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS), "没有该院校代码");
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
    public TeacherLoginDto login(TeacherLoginVo teacherLoginVo, HttpServletRequest request) {
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
        teacherLoginDto.setGender(teacher.getGender());
        String collegeName = collegeService.getById(teacher.getBelong()).getName();
        teacherLoginDto.setCollegeName(collegeName);
        teacherLoginDto.setEmail(teacher.getEmail());
        teacherLoginDto.setMessageNumber(teacher.getMessageTotal() - teacher.getMessageRead());
        teacherLoginDto.setRole(teacher.getRole());
        request.getSession().setAttribute(CommonConstant.USER_LOGIN_STATE, teacherLoginDto);
        return teacherLoginDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public TeacherLoginDto adminLogin(TeacherLoginVo teacherLoginVo, HttpServletRequest request) {
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

        // 如果登录的状态处于封禁则无法登录
        if (teacher.getStatus() == 1) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_BANNED));
        }

        // 如果登陆的身份为普通用户，就无法登录
        if (Objects.equals(teacher.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED));
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
        teacherLoginDto.setGender(teacher.getGender());
        String collegeName = collegeService.getById(teacher.getBelong()).getName();
        teacherLoginDto.setCollegeName(collegeName);
        teacherLoginDto.setEmail(teacher.getEmail());
        teacherLoginDto.setMessageNumber(teacher.getMessageTotal() - teacher.getMessageRead());
        teacherLoginDto.setRole(teacher.getRole());
        request.getSession().setAttribute(CommonConstant.ADMIN_LOGIN_STATE, teacherLoginDto);
        return teacherLoginDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public AdminGetSelfDto getSelf(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (!Objects.equals(teacherLoginDto.getId(), id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "任何管理员都无法在个人信息页面获取其他管理员的信息");
        }

        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户后台无数据");
        }

        AdminGetSelfDto adminGetSelfDto = new AdminGetSelfDto();
        adminGetSelfDto.setId(teacher.getId());
        adminGetSelfDto.setAccount(teacher.getAccount());
        adminGetSelfDto.setPassword("");
        adminGetSelfDto.setName(teacher.getName());
        adminGetSelfDto.setAvatar(teacher.getAvatar());
        adminGetSelfDto.setGender(teacher.getGender());
        adminGetSelfDto.setBelong(teacher.getBelong());
        adminGetSelfDto.setEmail(teacher.getEmail());

        return adminGetSelfDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void updateSelf(AdminUpdateSelfVo adminUpdateSelfVo, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (!Objects.equals(teacherLoginDto.getId(), adminUpdateSelfVo.getId())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "任何管理员都无法在个人信息页面修改其他管理员的信息");
        }

        Teacher teacher = teacherMapper.selectById(adminUpdateSelfVo.getId());

        if (teacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户后台无数据");
        }

        if (Objects.equals(adminUpdateSelfVo.getAccount(), teacher.getAccount())
                && Objects.equals(adminUpdateSelfVo.getGender(), teacher.getGender())
                && Objects.equals(adminUpdateSelfVo.getBelong(), teacher.getBelong())
                && adminUpdateSelfVo.getAvatar().equals(teacher.getAvatar())
                && adminUpdateSelfVo.getName().equals(teacher.getName())
                && adminUpdateSelfVo.getEmail().equals(teacher.getEmail())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.THE_UPDATE_DATA_IS_THE_SAME_AS_THE_BACKGROUND_DATA), "更新数据和库中数据相同");
        }

        String passwordNeedCompare = DigestUtil.md5Hex(adminUpdateSelfVo.getPassword() + SALT);
        if (!passwordNeedCompare.equals(teacher.getPassword())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.WRONG_USER_PASSWORD));
        }

        teacher.setAccount(adminUpdateSelfVo.getAccount());
        teacher.setName(adminUpdateSelfVo.getName());
        teacher.setAvatar(adminUpdateSelfVo.getAvatar());
        teacher.setGender(adminUpdateSelfVo.getGender());
        teacher.setBelong(adminUpdateSelfVo.getBelong());
        teacher.setEmail(adminUpdateSelfVo.getEmail());

        int updateResult = teacherMapper.updateById(teacher);

        // 判断数据库插入结果
        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改用户失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public AdminGetDto getAdmin(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (!Objects.equals(teacherLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非超级管理员无法回显其他管理员信息");
        }

        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户后台无数据");
        }

        if (!Objects.equals(teacher.getRole(), CommonConstant.ADMIN_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "修改的对象并非管理员");
        }

        if (Objects.equals(teacher.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "超管不能在此对自己进行数据回显");
        }

        AdminGetDto adminGetDto = new AdminGetDto();
        adminGetDto.setId(teacher.getId());
        adminGetDto.setEmail(teacher.getEmail());
        adminGetDto.setAvatar(teacher.getAvatar());
        adminGetDto.setGender(teacher.getGender());
        adminGetDto.setBelong(teacher.getBelong());
        adminGetDto.setStatus(teacher.getStatus());
        adminGetDto.setRole(teacher.getRole());

        return adminGetDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void updateAdmin(AdminUpdateVo adminUpdateVo, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (!Objects.equals(teacherLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非超级管理员无法更改其他管理员信息");
        }

        Teacher teacher = teacherMapper.selectById(adminUpdateVo.getId());

        if (teacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户后台无数据");
        }

        if (!Objects.equals(teacher.getRole(), CommonConstant.ADMIN_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "修改的对象并非管理员");
        }

        if (Objects.equals(adminUpdateVo.getRole(), teacher.getRole())
                && Objects.equals(adminUpdateVo.getGender(), teacher.getGender())
                && Objects.equals(adminUpdateVo.getBelong(), teacher.getBelong())
                && adminUpdateVo.getAvatar().equals(teacher.getAvatar())
                && adminUpdateVo.getStatus().equals(teacher.getStatus())
                && adminUpdateVo.getEmail().equals(teacher.getEmail())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.THE_UPDATE_DATA_IS_THE_SAME_AS_THE_BACKGROUND_DATA), "更新数据和库中数据相同");
        }

        if (Objects.equals(adminUpdateVo.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED));
        }

        teacher.setRole(adminUpdateVo.getRole());
        teacher.setStatus(adminUpdateVo.getStatus());
        teacher.setAvatar(adminUpdateVo.getAvatar());
        teacher.setGender(adminUpdateVo.getGender());
        teacher.setBelong(adminUpdateVo.getBelong());
        teacher.setEmail(adminUpdateVo.getEmail());

        int updateResult = teacherMapper.updateById(teacher);

        // 判断数据库插入结果
        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改用户失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    public void download(HttpServletResponse response) {
        try {
            // 设置下载信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("管理员信息", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 查询课程分类表所有的数据
            List<Teacher> teacherList = teacherMapper.selectList(null);
            // 将subjectList转变成subjectEeVoList
            List<Teacher> teachers = teacherList.stream().map(subject -> {
                Teacher subjectEeVo = new Teacher();
                BeanUtils.copyProperties(subject, subjectEeVo);
                return subjectEeVo;
            }).collect(Collectors.toList());
            EasyExcelFactory.write(response.getOutputStream(), College.class)
                    .sheet("管理员数据")
                    .doWrite(teachers);
        } catch (UnsupportedEncodingException e) {
            throw new CustomizeFileException(R.failure(RCodeEnum.EXCEL_EXPORT_FAILED), "导出Excel时文件编码异常");
        } catch (IOException e) {
            throw new CustomizeFileException(R.failure(RCodeEnum.EXCEL_EXPORT_FAILED), "文件写入时，响应流发生异常");
        }
    }
}

package top.sharehome.share_study.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeFileException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeTransactionException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.mapper.*;
import top.sharehome.share_study.model.dto.admin.AdminGetDto;
import top.sharehome.share_study.model.dto.admin.AdminGetSelfDto;
import top.sharehome.share_study.model.dto.admin.AdminPageDto;
import top.sharehome.share_study.model.dto.teacher.TeacherGetDto;
import top.sharehome.share_study.model.dto.teacher.TeacherLoginDto;
import top.sharehome.share_study.model.dto.teacher.TeacherPageDto;
import top.sharehome.share_study.model.dto.user.UserGetInfoDto;
import top.sharehome.share_study.model.entity.*;
import top.sharehome.share_study.model.vo.admin.AdminPageVo;
import top.sharehome.share_study.model.vo.admin.AdminUpdateSelfVo;
import top.sharehome.share_study.model.vo.admin.AdminUpdateVo;
import top.sharehome.share_study.model.vo.teacher.TeacherLoginVo;
import top.sharehome.share_study.model.vo.teacher.TeacherPageVo;
import top.sharehome.share_study.model.vo.teacher.TeacherRegisterVo;
import top.sharehome.share_study.model.vo.teacher.TeacherUpdateVo;
import top.sharehome.share_study.model.vo.user.UserUpdateInfoSelfVo;
import top.sharehome.share_study.service.FileOssService;
import top.sharehome.share_study.service.TeacherService;
import top.sharehome.share_study.utils.object.ObjectDataUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    @javax.annotation.Resource
    private TeacherMapper teacherMapper;
    @javax.annotation.Resource
    private CollegeMapper collegeMapper;
    @javax.annotation.Resource
    private ResourceMapper resourceMapper;
    @javax.annotation.Resource
    private CommentMapper commentMapper;
    @javax.annotation.Resource
    private FileOssService fileOssService;
    @javax.annotation.Resource
    private CollectMapper collectMapper;
    @javax.annotation.Resource
    private ResourceCensorMapper resourceCensorMapper;

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

        College resultCollege = collegeMapper.selectById(teacherRegisterVo.getBelong());
        if (resultCollege == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS), "没有该院校");
        }

        // 进行数据拷贝和插入
        Teacher teacher = new Teacher();
        teacher.setBelong(resultCollege.getId());
        BeanUtils.copyProperties(teacherRegisterVo, teacher);
        teacher.setPassword(DigestUtil.md5Hex(teacher.getPassword() + SALT));
        if (StringUtils.isEmpty(teacher.getAvatar())) {
            teacher.setAvatar("");
        }
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
        teacherLoginDto.setName(teacher.getName());
        teacherLoginDto.setAvatar(teacher.getAvatar());
        teacherLoginDto.setGender(teacher.getGender());
        String collegeName = collegeMapper.selectById(teacher.getBelong()).getName();
        teacherLoginDto.setCollegeName(collegeName);
        teacherLoginDto.setEmail(teacher.getEmail());
        teacherLoginDto.setScore(teacher.getScore());
        teacherLoginDto.setMessageNumber(teacher.getMessageTotal() - teacher.getMessageRead());
        teacherLoginDto.setRole(teacher.getRole());
        teacherLoginDto.setCreateTime(teacher.getCreateTime());
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
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户账户不存在");
        }

        // 如果登录的状态处于封禁则无法登录
        if (teacher.getStatus() == 1) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_BANNED), "用户账户被封禁");
        }

        // 如果登陆的身份为普通用户，就无法登录
        if (Objects.equals(teacher.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "登陆的身份为普通用户，无法登录管理后台");
        }

        // 对比密码是否一致
        String passwordNeedCompare = DigestUtil.md5Hex(passwordBeforeLogin + SALT);
        if (!passwordNeedCompare.equals(teacher.getPassword())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PASSWORD_VERIFICATION_FAILED), "密码校验失败");
        }

        // 信息脱敏
        TeacherLoginDto teacherLoginDto = new TeacherLoginDto();
        teacherLoginDto.setId(teacher.getId());
        teacherLoginDto.setAccount(teacher.getAccount());
        teacherLoginDto.setName(teacher.getName());
        teacherLoginDto.setAvatar(teacher.getAvatar());
        teacherLoginDto.setGender(teacher.getGender());
        String collegeName = collegeMapper.selectById(teacher.getBelong()).getName();
        teacherLoginDto.setCollegeName(collegeName);
        teacherLoginDto.setEmail(teacher.getEmail());
        teacherLoginDto.setScore(teacher.getScore());
        teacherLoginDto.setMessageNumber(teacher.getMessageTotal() - teacher.getMessageRead());
        teacherLoginDto.setRole(teacher.getRole());
        teacherLoginDto.setCreateTime(teacher.getCreateTime());

        // 向Session中存入登录状态
        request.getSession().setAttribute(CommonConstant.ADMIN_LOGIN_STATE, teacherLoginDto);
        return teacherLoginDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public AdminGetSelfDto getSelf(Long id, HttpServletRequest request) {
        // 鉴定操作者的权限
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (!Objects.equals(teacherLoginDto.getId(), id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "任何管理员都无法在个人信息页面获取其他管理员的信息");
        }

        // 判断被操作数据是否为空
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户后台无数据");
        }

        // 信息脱敏
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
        // 鉴定操作者的权限
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (!Objects.equals(teacherLoginDto.getId(), adminUpdateSelfVo.getId())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "任何管理员都无法在个人信息页面修改其他管理员的信息");
        }

        // 判断被操作数据是否为空
        Teacher teacher = teacherMapper.selectById(adminUpdateSelfVo.getId());
        if (teacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户后台无数据");
        }

        // 对比密码是否一致
        String passwordNeedCompare = DigestUtil.md5Hex(adminUpdateSelfVo.getPassword() + SALT);
        if (!passwordNeedCompare.equals(teacher.getPassword())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PASSWORD_VERIFICATION_FAILED), "密码校验失败");
        }

        // 判断更新内容是否重复
        if (Objects.equals(adminUpdateSelfVo.getAccount(), teacher.getAccount())
                && Objects.equals(adminUpdateSelfVo.getGender(), teacher.getGender())
                && Objects.equals(adminUpdateSelfVo.getBelong(), teacher.getBelong())
                && adminUpdateSelfVo.getAvatar().equals(teacher.getAvatar())
                && adminUpdateSelfVo.getName().equals(teacher.getName())
                && adminUpdateSelfVo.getEmail().equals(teacher.getEmail())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.THE_UPDATE_DATA_IS_THE_SAME_AS_THE_BACKGROUND_DATA), "更新数据和库中数据相同");
        }

        // 判断账号是否重复
        if (!Objects.equals(adminUpdateSelfVo.getAccount(), teacher.getAccount())) {
            LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teacherLambdaQueryWrapper.eq(Teacher::getAccount, adminUpdateSelfVo.getAccount());
            Teacher exist = teacherMapper.selectOne(teacherLambdaQueryWrapper);
            if (exist != null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_ALREADY_EXISTS));
            }
        }

        // 补全数据
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
        // 鉴定操作者的权限
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (!Objects.equals(teacherLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非超级管理员无法回显其他管理员信息");
        }

        // 判断操作数据是否为空
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户后台无数据");
        }

        // 判断被操作数据是否存在权限问题
        if (!Objects.equals(teacher.getRole(), CommonConstant.ADMIN_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "获取信息的对象并非管理员");
        }
        if (Objects.equals(teacher.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "超管不能在此对自己进行数据回显");
        }

        // 信息脱敏
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
        // 判断操作人是否有权操作
        if (!(Objects.equals(adminUpdateVo.getRole(), CommonConstant.DEFAULT_ROLE)
                || Objects.equals(adminUpdateVo.getRole(), CommonConstant.ADMIN_ROLE)
                || Objects.equals(adminUpdateVo.getRole(), CommonConstant.SUPER_ROLE))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (!Objects.equals(teacherLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非超级管理员无法更改其他管理员信息");
        }

        Teacher teacher = teacherMapper.selectById(adminUpdateVo.getId());

        if (teacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户后台无数据");
        }

        if (!Objects.equals(teacher.getRole(), CommonConstant.ADMIN_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "修改的对象并非管理员");
        }

        if (Objects.equals(adminUpdateVo.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "不满足超级管理员唯一性");
        }

        if (Objects.equals(adminUpdateVo.getRole(), teacher.getRole())
                && Objects.equals(adminUpdateVo.getGender(), teacher.getGender())
                && Objects.equals(adminUpdateVo.getBelong(), teacher.getBelong())
                && adminUpdateVo.getAvatar().equals(teacher.getAvatar())
                && adminUpdateVo.getStatus().equals(teacher.getStatus())
                && adminUpdateVo.getEmail().equals(teacher.getEmail())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.THE_UPDATE_DATA_IS_THE_SAME_AS_THE_BACKGROUND_DATA), "更新数据和库中数据相同");
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
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void downloadAdmin(HttpServletResponse response) {
        try {
            // 设置下载信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("管理员信息", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 查询管理员分类表所有的数据
            LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teacher::getRole, CommonConstant.ADMIN_ROLE)
                    .or()
                    .eq(Teacher::getRole, CommonConstant.SUPER_ROLE)
                    .orderByDesc(Teacher::getRole)
                    .orderByAsc(Teacher::getStatus)
                    .orderByDesc(Teacher::getScore)
                    .orderByAsc(Teacher::getCreateTime);
            List<Teacher> teacherList = teacherMapper.selectList(queryWrapper);
            EasyExcelFactory.write(response.getOutputStream(), Teacher.class)
                    .sheet("管理员数据")
                    .doWrite(teacherList);
        } catch (UnsupportedEncodingException e) {
            throw new CustomizeFileException(R.failure(RCodeEnum.EXCEL_EXPORT_FAILED), "导出Excel时文件编码异常");
        } catch (IOException e) {
            throw new CustomizeFileException(R.failure(RCodeEnum.EXCEL_EXPORT_FAILED), "文件写入时，响应流发生异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void downloadTeacher(HttpServletResponse response) {
        try {
            // 设置下载信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("教师信息", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 查询课程分类表所有的数据
            LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper
                    .eq(Teacher::getRole, CommonConstant.DEFAULT_ROLE)
                    .orderByDesc(Teacher::getRole)
                    .orderByAsc(Teacher::getStatus)
                    .orderByDesc(Teacher::getScore)
                    .orderByAsc(Teacher::getCreateTime);
            List<Teacher> teacherList = teacherMapper.selectList(queryWrapper);
            EasyExcelFactory.write(response.getOutputStream(), Teacher.class)
                    .sheet("教师数据")
                    .doWrite(teacherList);
        } catch (UnsupportedEncodingException e) {
            throw new CustomizeFileException(R.failure(RCodeEnum.EXCEL_EXPORT_FAILED), "导出Excel时文件编码异常");
        } catch (IOException e) {
            throw new CustomizeFileException(R.failure(RCodeEnum.EXCEL_EXPORT_FAILED), "文件写入时，响应流发生异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void delete(Long id) {
        LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherLambdaQueryWrapper.eq(Teacher::getId, id);

        Teacher selectResult = teacherMapper.selectOne(teacherLambdaQueryWrapper);
        if (selectResult == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "教师不存在，不需要进行下一步操作");
        }

        if (!Objects.equals(selectResult.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "教师管理页面不得删除管理员信息");
        }

        LambdaUpdateWrapper<Teacher> teacherLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        teacherLambdaUpdateWrapper.eq(Teacher::getId, id);
        selectResult.setAccount(selectResult.getAccount() + "+" + System.currentTimeMillis());
        teacherMapper.update(selectResult, teacherLambdaUpdateWrapper);

        LambdaQueryWrapper<Resource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceLambdaQueryWrapper.eq(Resource::getBelong, id);
        List<Resource> resourceList = resourceMapper.selectList(resourceLambdaQueryWrapper);
        resourceMapper.delete(resourceLambdaQueryWrapper);

        LambdaQueryWrapper<ResourceCensor> resourceCensorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceCensorLambdaQueryWrapper.eq(ResourceCensor::getBelong, id);
        resourceCensorMapper.delete(resourceCensorLambdaQueryWrapper);

        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getBelong, id);
        commentMapper.delete(commentLambdaQueryWrapper);

        int deleteResult = teacherMapper.delete(teacherLambdaQueryWrapper);

        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "教师数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        resourceList.forEach(resource -> {
            fileOssService.delete(resource.getUrl());
            LambdaQueryWrapper<Collect> collectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            collectLambdaQueryWrapper.eq(Collect::getResource, resource.getId());
            collectMapper.delete(collectLambdaQueryWrapper);
        });
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteBatch(List<Long> ids) {
        List<List<Resource>> resourceLists = ids.stream().map(id -> {
            Teacher selectResult = this.getById(id);
            if (selectResult == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "教师不存在，不需要进行下一步操作");
            }
            if (Objects.equals(selectResult.getRole(), CommonConstant.ADMIN_ROLE)
                    || Objects.equals(selectResult.getRole(), CommonConstant.SUPER_ROLE)) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "教师管理页面不得删除管理员信息");
            }
            LambdaUpdateWrapper<Teacher> teacherLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            teacherLambdaUpdateWrapper.eq(Teacher::getId, id);
            selectResult.setAccount(selectResult.getAccount() + "+" + System.currentTimeMillis());
            teacherMapper.update(selectResult, teacherLambdaUpdateWrapper);

            LambdaQueryWrapper<Resource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resourceLambdaQueryWrapper.eq(Resource::getBelong, id);
            List<Resource> resourceList = resourceMapper.selectList(resourceLambdaQueryWrapper);
            resourceMapper.delete(resourceLambdaQueryWrapper);

            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            commentLambdaQueryWrapper.eq(Comment::getBelong, id);
            commentMapper.delete(commentLambdaQueryWrapper);

            return resourceList;
        }).collect(Collectors.toList());

        int deleteResult = teacherMapper.deleteBatchIds(ids);
        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "教师数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        resourceLists.forEach(resourceList -> {
            resourceList.forEach(resource -> {
                fileOssService.delete(resource.getUrl());
                LambdaQueryWrapper<Collect> collectLambdaQueryWrapper = new LambdaQueryWrapper<>();
                collectLambdaQueryWrapper.eq(Collect::getResource, resource.getId());
                collectMapper.delete(collectLambdaQueryWrapper);
            });
        });
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public TeacherGetDto getTeacher(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "普通用户无权进行操作");
        }

        if (ObjectUtils.isEmpty(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "教师ID为空，无法回显");
        }

        Teacher resultFromDatabase = teacherMapper.selectById(id);
        if (resultFromDatabase == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户后台无数据");
        }
        if (Objects.equals(resultFromDatabase.getRole(), CommonConstant.ADMIN_ROLE)
                || Objects.equals(resultFromDatabase.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员和超级管理员的信息不能在这个页面回显");
        }

        TeacherGetDto teacherGetDto = new TeacherGetDto();
        teacherGetDto.setId(resultFromDatabase.getId());
        teacherGetDto.setEmail(resultFromDatabase.getEmail());
        teacherGetDto.setAvatar(resultFromDatabase.getAvatar());
        teacherGetDto.setGender(resultFromDatabase.getGender());
        teacherGetDto.setBelong(resultFromDatabase.getBelong());
        teacherGetDto.setStatus(resultFromDatabase.getStatus());
        teacherGetDto.setRole(resultFromDatabase.getRole());

        return teacherGetDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void updateTeacher(TeacherUpdateVo teacherUpdateVo, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非管理员无法更改其他教师信息");
        }

        Teacher resultFromDatabase = teacherMapper.selectById(teacherUpdateVo.getId());
        if (resultFromDatabase == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户后台无数据");
        }
        if (!Objects.equals(resultFromDatabase.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "修改的对象是管理员");
        }

        if (Objects.equals(teacherUpdateVo.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "不满足超级管理员唯一性");
        }

        if (Objects.equals(teacherUpdateVo.getRole(), resultFromDatabase.getRole())
                && Objects.equals(teacherUpdateVo.getGender(), resultFromDatabase.getGender())
                && Objects.equals(teacherUpdateVo.getBelong(), resultFromDatabase.getBelong())
                && teacherUpdateVo.getAvatar().equals(resultFromDatabase.getAvatar())
                && teacherUpdateVo.getStatus().equals(resultFromDatabase.getStatus())
                && teacherUpdateVo.getEmail().equals(resultFromDatabase.getEmail())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.THE_UPDATE_DATA_IS_THE_SAME_AS_THE_BACKGROUND_DATA), "更新数据和库中数据相同");
        }

        resultFromDatabase.setRole(teacherUpdateVo.getRole());
        resultFromDatabase.setStatus(teacherUpdateVo.getStatus());
        resultFromDatabase.setAvatar(teacherUpdateVo.getAvatar());
        resultFromDatabase.setGender(teacherUpdateVo.getGender());
        resultFromDatabase.setBelong(teacherUpdateVo.getBelong());
        resultFromDatabase.setEmail(teacherUpdateVo.getEmail());

        int updateResult = teacherMapper.updateById(resultFromDatabase);

        // 判断数据库插入结果
        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改用户失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Page<TeacherPageDto> pageTeacher(Integer current, Integer pageSize, TeacherPageVo teacherPageVo) {
        // 创建原始分页数据以及返回分页数据
        Page<Teacher> page = new Page<>(current, pageSize);
        Page<TeacherPageDto> returnResult = new Page<>(current, pageSize);

        // 过滤分页对象
        LambdaQueryWrapper<Teacher> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .orderByDesc(Teacher::getRole)
                .orderByDesc(Teacher::getScore)
                .orderByDesc(Teacher::getCreateTime);

        // 当不存在模糊查询时的分页操作
        if (ObjectDataUtil.isAllObjectDataEmpty(teacherPageVo)) {
            this.page(page, lambdaQueryWrapper);
            BeanUtils.copyProperties(page, returnResult, "records");
            List<TeacherPageDto> pageDtoList = page.getRecords().stream().map(teacher -> {
                TeacherPageDto teacherPageDto = new TeacherPageDto();
                BeanUtils.copyProperties(teacher, teacherPageDto);
                College college = collegeMapper.selectById(teacher.getBelong());
                if (college == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS), "该管理员所属高校不存在");
                }
                teacherPageDto.setBelongName(college.getName());
                return teacherPageDto;
            }).collect(Collectors.toList());
            returnResult.setRecords(pageDtoList);
            return returnResult;
        }

        // 当存在模糊查询时的分页操作
        lambdaQueryWrapper
                .like(!StringUtils.isEmpty(teacherPageVo.getAccount()), Teacher::getAccount, teacherPageVo.getAccount())
                .like(!StringUtils.isEmpty(teacherPageVo.getName()), Teacher::getName, teacherPageVo.getName())
                .like(!ObjectUtils.isEmpty(teacherPageVo.getGender()), Teacher::getGender, teacherPageVo.getGender())
                .like(!ObjectUtils.isEmpty(teacherPageVo.getStatus()), Teacher::getStatus, teacherPageVo.getStatus())
                .like(!ObjectUtils.isEmpty(teacherPageVo.getRole()), Teacher::getRole, teacherPageVo.getRole());

        String belongName = teacherPageVo.getBelongName();
        List<Long> collegeIds = null;
        if (!StringUtils.isEmpty(belongName)) {
            LambdaQueryWrapper<College> belongNameLambdaQueryWrapper = new LambdaQueryWrapper<>();
            belongNameLambdaQueryWrapper.like(College::getName, belongName);
            List<College> teachers = collegeMapper.selectList(belongNameLambdaQueryWrapper);
            collegeIds = teachers.stream().map(College::getId).collect(Collectors.toList());
        }

        List<Long> finalCollegeIds = collegeIds;
        if (!(!Objects.isNull(finalCollegeIds) && finalCollegeIds.isEmpty())) {
            if (!Objects.isNull(finalCollegeIds) && !finalCollegeIds.isEmpty()) {
                lambdaQueryWrapper
                        .in(Teacher::getBelong, finalCollegeIds);
            }
            this.page(page, lambdaQueryWrapper);
        }

        BeanUtils.copyProperties(page, returnResult, "records");

        List<TeacherPageDto> pageDtoList = page.getRecords().stream().map(teacher -> {
            TeacherPageDto teacherPageDto = new TeacherPageDto();
            BeanUtils.copyProperties(teacher, teacherPageDto);
            College college = collegeMapper.selectById(teacher.getBelong());
            if (college == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS), "该管理员所属高校不存在");
            }
            teacherPageDto.setBelongName(college.getName());
            return teacherPageDto;
        }).collect(Collectors.toList());
        returnResult.setRecords(pageDtoList);
        return returnResult;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public TeacherLoginDto getAdminLogin(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，管理员未登录");
        }

        if (!Objects.equals(teacherLoginDto.getId(), id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "操作者和登录状态中信息不相符");
        }

        Teacher teacher = teacherMapper.selectById(teacherLoginDto.getId());
        TeacherLoginDto adminLoginDto = new TeacherLoginDto();
        adminLoginDto.setId(teacher.getId());
        adminLoginDto.setAccount(teacher.getAccount());
        String name = DesensitizedUtil.chineseName(teacher.getName());
        adminLoginDto.setName(name);
        adminLoginDto.setAvatar(teacher.getAvatar());
        adminLoginDto.setGender(teacher.getGender());
        String collegeName = collegeMapper.selectById(teacher.getBelong()).getName();
        adminLoginDto.setCollegeName(collegeName);
        adminLoginDto.setEmail(teacher.getEmail());
        adminLoginDto.setScore(teacher.getScore());
        adminLoginDto.setMessageNumber(teacher.getMessageTotal() - teacher.getMessageRead());
        adminLoginDto.setRole(teacher.getRole());
        adminLoginDto.setCreateTime(teacher.getCreateTime());

        // 向Session中更新登录状态
        request.getSession().setAttribute(CommonConstant.ADMIN_LOGIN_STATE, adminLoginDto);

        return adminLoginDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public TeacherLoginDto getUserLogin(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，普通用户未登录");
        }

        if (!Objects.equals(teacherLoginDto.getId(), id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "操作者和登录状态中信息不相符");
        }

        Teacher teacher = teacherMapper.selectById(teacherLoginDto.getId());
        TeacherLoginDto userLoginDto = new TeacherLoginDto();
        userLoginDto.setId(teacher.getId());
        userLoginDto.setAccount(teacher.getAccount());
        userLoginDto.setName(teacher.getName());
        userLoginDto.setAvatar(teacher.getAvatar());
        userLoginDto.setGender(teacher.getGender());
        String collegeName = collegeMapper.selectById(teacher.getBelong()).getName();
        userLoginDto.setCollegeName(collegeName);
        userLoginDto.setEmail(teacher.getEmail());
        userLoginDto.setScore(teacher.getScore());
        userLoginDto.setMessageNumber(teacher.getMessageTotal() - teacher.getMessageRead());
        userLoginDto.setRole(teacher.getRole());
        userLoginDto.setCreateTime(teacher.getCreateTime());

        // 向Session中更新登录状态
        request.getSession().setAttribute(CommonConstant.USER_LOGIN_STATE, userLoginDto);

        return userLoginDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public UserGetInfoDto getUserInfo(Long id, HttpServletRequest request) {
        // 判断被操作数据是否为空
        Teacher teacher = teacherMapper.selectById(id);
        if (teacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户后台无数据");
        }

        // 信息脱敏
        UserGetInfoDto userGetInfoDto = new UserGetInfoDto();
        userGetInfoDto.setId(teacher.getId());
        userGetInfoDto.setAccount(teacher.getAccount());
        userGetInfoDto.setName(teacher.getName());
        userGetInfoDto.setAvatar(teacher.getAvatar());
        userGetInfoDto.setGender(teacher.getGender());
        userGetInfoDto.setBelong(teacher.getBelong());
        userGetInfoDto.setEmail(teacher.getEmail());

        return userGetInfoDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void updateUserSelf(UserUpdateInfoSelfVo userUpdateInfoSelfVo, HttpServletRequest request) {
        // 鉴定操作者的权限
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (Objects.isNull(teacherLoginDto)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "用户未登录");
        }
        if (!Objects.equals(teacherLoginDto.getId(), userUpdateInfoSelfVo.getId())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "普通用户都无法在个人信息页面修改其他普通用户的信息");
        }

        // 判断被操作数据是否为空
        Teacher teacher = teacherMapper.selectById(userUpdateInfoSelfVo.getId());
        if (teacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_ACCOUNT_DOES_NOT_EXIST), "用户后台无数据");
        }

        // 对比密码是否一致
        String passwordNeedCompare = DigestUtil.md5Hex(userUpdateInfoSelfVo.getPassword() + SALT);
        if (!passwordNeedCompare.equals(teacher.getPassword())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PASSWORD_VERIFICATION_FAILED), "密码校验失败");
        }

        // 判断更新内容是否重复
        if (Objects.equals(userUpdateInfoSelfVo.getAccount(), teacher.getAccount())
                && Objects.equals(userUpdateInfoSelfVo.getGender(), teacher.getGender())
                && Objects.equals(userUpdateInfoSelfVo.getBelong(), teacher.getBelong())
                && userUpdateInfoSelfVo.getAvatar().equals(teacher.getAvatar())
                && userUpdateInfoSelfVo.getName().equals(teacher.getName())
                && userUpdateInfoSelfVo.getEmail().equals(teacher.getEmail())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.THE_UPDATE_DATA_IS_THE_SAME_AS_THE_BACKGROUND_DATA), "更新数据和库中数据相同");
        }

        // 判断账号是否重复
        if (!Objects.equals(userUpdateInfoSelfVo.getAccount(), teacher.getAccount())) {
            LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teacherLambdaQueryWrapper.eq(Teacher::getAccount, userUpdateInfoSelfVo.getAccount());
            Teacher exist = teacherMapper.selectOne(teacherLambdaQueryWrapper);
            if (exist != null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_ALREADY_EXISTS));
            }
        }

        // 补全数据
        teacher.setAccount(userUpdateInfoSelfVo.getAccount());
        teacher.setName(userUpdateInfoSelfVo.getName());
        teacher.setAvatar(userUpdateInfoSelfVo.getAvatar());
        teacher.setGender(userUpdateInfoSelfVo.getGender());
        teacher.setBelong(userUpdateInfoSelfVo.getBelong());
        teacher.setEmail(userUpdateInfoSelfVo.getEmail());

        int updateResult = teacherMapper.updateById(teacher);

        // 判断数据库插入结果
        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改用户失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Page<AdminPageDto> pageAdmin(Integer current, Integer pageSize, HttpServletRequest request, AdminPageVo adminPageVo) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，管理员未登录");
        }
        // 创建原始分页数据以及返回分页数据
        Page<Teacher> page = new Page<>(current, pageSize);
        Page<AdminPageDto> returnResult = new Page<>(current, pageSize);

        // 过滤分页对象
        LambdaQueryWrapper<Teacher> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper
                .and(condition -> {
                    condition.eq(Teacher::getRole, CommonConstant.ADMIN_ROLE)
                            .or()
                            .eq(Teacher::getRole, CommonConstant.SUPER_ROLE);
                })
                .orderByDesc(Teacher::getRole)
                .orderByDesc(Teacher::getCreateTime);

        // 当不存在模糊查询时的分页操作
        if (ObjectDataUtil.isAllObjectDataEmpty(adminPageVo)) {
            this.page(page, lambdaQueryWrapper);
            BeanUtils.copyProperties(page, returnResult, "records");
            List<AdminPageDto> pageDtoList = page.getRecords().stream().map(teacher -> {
                AdminPageDto adminPageDto = new AdminPageDto();
                BeanUtils.copyProperties(teacher, adminPageDto);
                College college = collegeMapper.selectById(teacher.getBelong());
                if (college == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS), "该管理员所属高校不存在");
                }
                adminPageDto.setBelongName(college.getName());
                return adminPageDto;
            }).collect(Collectors.toList());
            returnResult.setRecords(pageDtoList);
            return returnResult;
        }

        // 当存在模糊查询时的分页操作
        lambdaQueryWrapper
                .like(!StringUtils.isEmpty(adminPageVo.getAccount()), Teacher::getAccount, adminPageVo.getAccount())
                .like(!StringUtils.isEmpty(adminPageVo.getName()), Teacher::getName, adminPageVo.getName())
                .like(!ObjectUtils.isEmpty(adminPageVo.getGender()), Teacher::getGender, adminPageVo.getGender())
                .like(!ObjectUtils.isEmpty(adminPageVo.getStatus()), Teacher::getStatus, adminPageVo.getStatus())
                .like(!ObjectUtils.isEmpty(adminPageVo.getRole()), Teacher::getRole, adminPageVo.getRole());

        String belongName = adminPageVo.getBelongName();
        List<Long> collegeIds = null;
        if (!StringUtils.isEmpty(belongName)) {
            LambdaQueryWrapper<College> belongNameLambdaQueryWrapper = new LambdaQueryWrapper<>();
            belongNameLambdaQueryWrapper.like(College::getName, belongName);
            List<College> teachers = collegeMapper.selectList(belongNameLambdaQueryWrapper);
            collegeIds = teachers.stream().map(College::getId).collect(Collectors.toList());
        }

        List<Long> finalCollegeIds = collegeIds;

        if (!(!Objects.isNull(finalCollegeIds) && finalCollegeIds.isEmpty())) {
            if (!Objects.isNull(finalCollegeIds)) {
                lambdaQueryWrapper
                        .in(Teacher::getBelong, finalCollegeIds);
            }
            this.page(page, lambdaQueryWrapper);
        }

        BeanUtils.copyProperties(page, returnResult, "records");

        List<AdminPageDto> pageDtoList = page.getRecords().stream().map(teacher -> {
            AdminPageDto adminPageDto = new AdminPageDto();
            BeanUtils.copyProperties(teacher, adminPageDto);
            College college = collegeMapper.selectById(teacher.getBelong());
            if (college == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS), "该管理员所属高校不存在");
            }
            adminPageDto.setBelongName(college.getName());
            return adminPageDto;
        }).collect(Collectors.toList());
        returnResult.setRecords(pageDtoList);
        return returnResult;
    }
}

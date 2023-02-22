package top.sharehome.share_study.controller;

import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.model.dto.AdminGetDto;
import top.sharehome.share_study.model.dto.AdminGetSelfDto;
import top.sharehome.share_study.model.dto.CollegePageDto;
import top.sharehome.share_study.model.dto.TeacherLoginDto;
import top.sharehome.share_study.model.vo.AdminUpdateSelfVo;
import top.sharehome.share_study.model.vo.AdminUpdateVo;
import top.sharehome.share_study.model.vo.CollegePageVo;
import top.sharehome.share_study.model.vo.TeacherLoginVo;
import top.sharehome.share_study.service.TeacherService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * 管理员用户相关接口
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/admin")
@Api(tags = "管理员用户相关接口")
@CrossOrigin
public class AdminController {
    @Resource
    private TeacherService teacherService;
    /**
     * 账号的匹配表达式
     */
    private static final String MATCHER_ACCOUNT_REGEX = "^[\u4E00-\u9FA5A-Za-z0-9]{4,16}$";
    /**
     * 姓名的匹配表达式
     */
    private static final String MATCHER_NAME_REGEX = "^[\u4e00-\u9fa5.·]{0,}$";
    /**
     * 邮箱的匹配表达式
     */
    private static final String MATCHER_EMAIL_REGEX = "([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}";
    /**
     * 账号长度最小值
     */
    private static final Integer ACCOUNT_GE_LENGTH = 4;
    /**
     * 账号长度最大值
     */
    private static final Integer ACCOUNT_LE_LENGTH = 16;
    /**
     * 密码长度最小值
     */
    private static final Integer PASSWORD_GE_LENGTH = 4;
    /**
     * 密码长度最大值
     */
    private static final Integer PASSWORD_LE_LENGTH = 16;

    /**
     * 管理员登录接口
     *
     * @param teacherLoginVo 教师登录VO实体
     * @param request        需要存入session用户的登录状态
     * @return 登录信息
     */
    @PostMapping("/login")
    @ApiOperation("管理员登录接口")
    public R<TeacherLoginDto> login(@ApiParam(name = "teacherRegisterVo", value = "教师登录VO实体", required = true) @RequestBody TeacherLoginVo teacherLoginVo,
                                    HttpServletRequest request) {
        // 判空
        if (StringUtils.isAnyEmpty(teacherLoginVo.getAccount(), teacherLoginVo.getPassword())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "必填参数为空");
        }

        // 校验账户长度
        if (teacherLoginVo.getAccount().length() > ACCOUNT_LE_LENGTH || teacherLoginVo.getAccount().length() < ACCOUNT_GE_LENGTH) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_LENGTH_DO_NOT_MATCH), "用户账户的长度不匹配");
        }

        // 校验账户格式
        if (!ReUtil.isMatch(MATCHER_ACCOUNT_REGEX, teacherLoginVo.getAccount())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_CONTAINS_SPECIAL_CHARACTERS), "用户账户中包含特殊字符");
        }

        // 校验密码长度
        if (teacherLoginVo.getPassword().length() > PASSWORD_LE_LENGTH || teacherLoginVo.getPassword().length() < PASSWORD_GE_LENGTH) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PASSWORD_LENGTH_DO_NOT_MATCH), "用户密码的长度不匹配");
        }

        // 先进行数据的清空
        HttpSession session = request.getSession();
        if (session.getAttribute(CommonConstant.ADMIN_LOGIN_STATE) != null) {
            session.removeAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        }

        TeacherLoginDto teacherLoginDto = teacherService.adminLogin(teacherLoginVo, request);

        return R.success(teacherLoginDto, "登录成功");
    }

    /**
     * 管理员退出接口
     *
     * @param request 清空session登录状态
     * @return 返回退出信息
     */
    @PostMapping("/logout")
    @ApiOperation("管理员退出接口")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        return R.success("管理员退出成功");
    }

    /**
     * 管理员获取自己信息接口
     *
     * @param id      管理员ID
     * @param request 获取登录的Session状态
     * @return 返回管理员自己的可修改信息
     */
    @GetMapping("/getSelf/{id}")
    @ApiOperation("管理员获取自己信息接口")
    public R<AdminGetSelfDto> getSelf(@PathVariable("id") Long id, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }
        AdminGetSelfDto adminGetSelfDto = teacherService.getSelf(id, request);

        return R.success(adminGetSelfDto, "回显成功");
    }

    /**
     * 管理员修改自己信息接口
     *
     * @param adminUpdateSelfVo 管理员更新自己信息Vo实体
     * @return 返回更新结果
     */
    @PutMapping("/updateSelf")
    @ApiOperation("管理员修改自己信息接口")
    public R<String> updateSelf(@RequestBody AdminUpdateSelfVo adminUpdateSelfVo, HttpServletRequest request) {
        if (adminUpdateSelfVo == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        if (StringUtils.isAnyEmpty(
                adminUpdateSelfVo.getAccount(),
                adminUpdateSelfVo.getPassword(),
                adminUpdateSelfVo.getName())
                || ObjectUtils.isEmpty(adminUpdateSelfVo.getId())
                || ObjectUtils.isEmpty(adminUpdateSelfVo.getGender())
                || ObjectUtils.isEmpty(adminUpdateSelfVo.getBelong())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        // 校验账户长度
        if (adminUpdateSelfVo.getAccount().length() > ACCOUNT_LE_LENGTH || adminUpdateSelfVo.getAccount().length() < ACCOUNT_GE_LENGTH) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_LENGTH_DO_NOT_MATCH), "用户账户的长度不匹配");
        }

        // 校验账户格式
        if (!ReUtil.isMatch(MATCHER_ACCOUNT_REGEX, adminUpdateSelfVo.getAccount())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_CONTAINS_SPECIAL_CHARACTERS), "用户账户中包含特殊字符");
        }

        // 校验姓名格式
        if (!ReUtil.isMatch(MATCHER_NAME_REGEX, adminUpdateSelfVo.getName())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NAME_FORMAT_VERIFICATION_FAILED), "姓名格式有误");
        }

        // 校验邮箱格式
        if (!ObjectUtils.isEmpty(adminUpdateSelfVo.getEmail()) && !ReUtil.isMatch(MATCHER_EMAIL_REGEX, adminUpdateSelfVo.getEmail())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.EMAIL_FORMAT_VERIFICATION_FAILED), "邮箱格式有误");
        }

        teacherService.updateSelf(adminUpdateSelfVo, request);

        return R.success("修改成功");
    }

    /**
     * 超级管理员获取管理员信息接口
     *
     * @param id      管理员ID
     * @param request 获取登录的Session状态
     * @return 返回管理员可修改信息
     */
    @GetMapping("/get/{id}")
    @ApiOperation("超级管理员获取管理员信息接口")
    public R<AdminGetDto> get(@PathVariable("id") Long id, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }
        AdminGetDto adminGetDto = teacherService.getAdmin(id, request);

        return R.success(adminGetDto, "回显成功");
    }

    /**
     * 超级管理员修改管理员信息接口
     *
     * @param adminUpdateVo 超级管理员更新管理员信息Vo实体
     * @return 返回更新结果
     */
    @PutMapping("/update")
    @ApiOperation("超级管理员修改管理员信息接口")
    public R<String> updateAdmin(@RequestBody AdminUpdateVo adminUpdateVo, HttpServletRequest request) {
        if (adminUpdateVo == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        if (ObjectUtils.isEmpty(adminUpdateVo.getId())
                || ObjectUtils.isEmpty(adminUpdateVo.getStatus())
                || ObjectUtils.isEmpty(adminUpdateVo.getGender())
                || ObjectUtils.isEmpty(adminUpdateVo.getBelong())
                || ObjectUtils.isEmpty(adminUpdateVo.getRole())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        if (!(adminUpdateVo.getGender() == 0 || adminUpdateVo.getGender() == 1)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        if (!(adminUpdateVo.getStatus() == 0 || adminUpdateVo.getStatus() == 1)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        if (!(Objects.equals(adminUpdateVo.getRole(), CommonConstant.DEFAULT_ROLE)
                || Objects.equals(adminUpdateVo.getRole(), CommonConstant.ADMIN_ROLE)
                || Objects.equals(adminUpdateVo.getRole(), CommonConstant.SUPER_ROLE))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        // 校验邮箱格式
        if (!ObjectUtils.isEmpty(adminUpdateVo.getEmail()) && !ReUtil.isMatch(MATCHER_EMAIL_REGEX, adminUpdateVo.getEmail())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.EMAIL_FORMAT_VERIFICATION_FAILED), "邮箱格式有误");
        }

        teacherService.updateAdmin(adminUpdateVo, request);

        return R.success("修改成功");
    }

    // TODO:管理员分页接口

    // TODO:完善Excel导出

    /**
     * 管理员信息导出
     *
     * @param response 响应
     */
    @ApiOperation("管理员信息Excel导出")
    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        teacherService.download(response);
    }
}

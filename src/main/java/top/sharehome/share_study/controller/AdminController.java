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
import top.sharehome.share_study.model.dto.AdminPageDto;
import top.sharehome.share_study.model.dto.TeacherLoginDto;
import top.sharehome.share_study.model.vo.AdminPageVo;
import top.sharehome.share_study.model.vo.AdminUpdateSelfVo;
import top.sharehome.share_study.model.vo.AdminUpdateVo;
import top.sharehome.share_study.model.vo.TeacherLoginVo;
import top.sharehome.share_study.service.TeacherService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
     * 管理员登录接口（s/a）
     *
     * @param teacherLoginVo 教师登录VO实体
     * @param request        保存Session中登录状态
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

        // 执行管理员登录操作
        TeacherLoginDto teacherLoginDto = teacherService.adminLogin(teacherLoginVo, request);

        return R.success(teacherLoginDto, "登录成功");
    }

    /**
     * 管理员退出接口（无需权限）
     *
     * @param request 清空Session中登录状态
     * @return 返回退出信息
     */
    @PostMapping("/logout")
    @ApiOperation("管理员退出接口")
    public R<String> logout(HttpServletRequest request) {
        // 清空Session登录状态
        request.getSession().removeAttribute(CommonConstant.ADMIN_LOGIN_STATE);

        return R.success("管理员退出成功");
    }

    /**
     * 管理员获取自己信息接口（s/a）
     *
     * @param id      管理员ID
     * @param request 获取Session中登录状态
     * @return 返回管理员自己的可修改信息
     */
    @GetMapping("/getSelf/{id}")
    @ApiOperation("管理员获取自己信息接口")
    public R<AdminGetSelfDto> getSelf(@PathVariable("id") Long id, HttpServletRequest request) {
        // 判空
        if (ObjectUtils.isEmpty(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        // 执行获取自身信息的操作
        AdminGetSelfDto adminGetSelfDto = teacherService.getSelf(id, request);

        return R.success(adminGetSelfDto, "回显成功");
    }

    /**
     * 管理员修改自己信息接口（s/a）
     *
     * @param adminUpdateSelfVo 管理员更新自己信息Vo实体
     * @return 返回更新结果
     */
    @PutMapping("/updateSelf")
    @ApiOperation("管理员修改自己信息接口")
    public R<String> updateSelf(@RequestBody AdminUpdateSelfVo adminUpdateSelfVo, HttpServletRequest request) {
        // 判空
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

        // 判断性别数据是否正确
        if (!(adminUpdateSelfVo.getGender() == 0 || adminUpdateSelfVo.getGender() == 1)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
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

        // 执行更新自身信息的操作
        teacherService.updateSelf(adminUpdateSelfVo, request);

        return R.success("修改成功");
    }

    /**
     * 超级管理员获取管理员信息接口（s）
     *
     * @param id      管理员ID
     * @param request 获取登录的Session状态
     * @return 返回管理员可修改信息
     */
    @GetMapping("/get/{id}")
    @ApiOperation("超级管理员获取管理员信息接口")
    public R<AdminGetDto> getAdmin(@PathVariable("id") Long id, HttpServletRequest request) {
        // 判空
        if (ObjectUtils.isEmpty(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        // 执行超级管理员获取管理员信息的操作
        AdminGetDto adminGetDto = teacherService.getAdmin(id, request);

        return R.success(adminGetDto, "回显成功");
    }

    /**
     * 超级管理员修改管理员信息接口（s）
     *
     * @param adminUpdateVo 超级管理员更新管理员信息Vo实体
     * @return 返回更新结果
     */
    @PutMapping("/update")
    @ApiOperation("超级管理员修改管理员信息接口")
    public R<String> updateAdmin(@RequestBody AdminUpdateVo adminUpdateVo, HttpServletRequest request) {
        // 判空
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

        // 判断性别数据是否正确
        if (!(adminUpdateVo.getGender() == 0 || adminUpdateVo.getGender() == 1)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        // 判断状态数据是否正确
        if (!(adminUpdateVo.getStatus() == 0 || adminUpdateVo.getStatus() == 1)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        // 校验邮箱格式
        if (!ObjectUtils.isEmpty(adminUpdateVo.getEmail()) && !ReUtil.isMatch(MATCHER_EMAIL_REGEX, adminUpdateVo.getEmail())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.EMAIL_FORMAT_VERIFICATION_FAILED), "邮箱格式有误");
        }

        // 执行超级管理员更新管理员信息的操作
        teacherService.updateAdmin(adminUpdateVo, request);

        return R.success("修改成功");
    }

    /**
     * 管理员分页查询接口（s）
     *
     * @param current     当前页
     * @param pageSize    页面条数
     * @param adminPageVo 管理员分页Vo对象
     * @return 返回分页结果
     */
    @PostMapping("/page/{current}/{pageSize}")
    @ApiOperation("管理员分页查询接口")
    public R<Page<AdminPageDto>> page(@PathVariable("current") Integer current, @PathVariable("pageSize") Integer pageSize, @ApiParam(name = "adminPageVo", value = "管理员分页Vo对象", required = true) @RequestBody(required = false) AdminPageVo adminPageVo) {
        // 判空
        if (ObjectUtils.isEmpty(current) || ObjectUtils.isEmpty(pageSize)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "分页参数为空");
        }

        // 判断数据格式
        if (current <= 0 || pageSize <= 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "分页参数格式错误");
        }

        // 执行管理员信息分页操作
        Page<AdminPageDto> page = teacherService.pageAdmin(current, pageSize, adminPageVo);

        return R.success(page, "分页查询成功");
    }


    /**
     * 管理员信息导出（s）
     *
     * @param response 响应
     */
    @ApiOperation("管理员信息Excel导出")
    @GetMapping("/download")
    public R<String> download(HttpServletResponse response) {
        // 执行管理员信息导出为Excel的操作
        teacherService.downloadAdmin(response);

        return R.success("导出成功");
    }
}

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
import top.sharehome.share_study.model.dto.TeacherGetDto;
import top.sharehome.share_study.model.dto.TeacherLoginDto;
import top.sharehome.share_study.model.dto.TeacherPageDto;
import top.sharehome.share_study.model.vo.TeacherLoginVo;
import top.sharehome.share_study.model.vo.TeacherPageVo;
import top.sharehome.share_study.model.vo.TeacherRegisterVo;
import top.sharehome.share_study.model.vo.TeacherUpdateVo;
import top.sharehome.share_study.service.TeacherService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

/**
 * 教师用户相关接口
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/teacher")
@Api(tags = "教师用户相关接口")
@CrossOrigin
public class TeacherController {
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
     * 院校代码的匹配表达式
     */
    private static final String MATCHER_CODE_REGEX = "^\\d{5}$";
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
     * 用户注册接口（无需权限）
     *
     * @param teacherRegisterVo 教师注册VO实体
     * @return 注册结果信息
     */
    @PostMapping("/register")
    @ApiOperation("用户注册接口")
    public R<String> register(@ApiParam(name = "teacherRegisterVo", value = "教师注册VO实体", required = true) @RequestBody TeacherRegisterVo teacherRegisterVo) {
        // 判空
        if (StringUtils.isAnyEmpty(teacherRegisterVo.getAccount()
                , teacherRegisterVo.getPassword()
                , teacherRegisterVo.getCheckPassword()
                , teacherRegisterVo.getName()
                , teacherRegisterVo.getCode())
                || ObjectUtils.isEmpty(teacherRegisterVo.getGender())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "必填参数为空");
        }

        // 校验俩次密码
        if (!teacherRegisterVo.getPassword().equals(teacherRegisterVo.getCheckPassword())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PASSWORD_IS_DIFFERENT_FROM_THE_CHECK_PASSWORD), "两次输入的密码不相同");
        }

        // 校验账户长度
        if (teacherRegisterVo.getAccount().length() > ACCOUNT_LE_LENGTH || teacherRegisterVo.getAccount().length() < ACCOUNT_GE_LENGTH) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_LENGTH_DO_NOT_MATCH), "用户账户的长度不匹配");
        }

        // 校验院校代码格式
        if (!ReUtil.isMatch(MATCHER_CODE_REGEX, teacherRegisterVo.getCode())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.CODE_IS_MALFORMED), "院校代码格式有误");
        }

        // 校验账户格式
        if (!ReUtil.isMatch(MATCHER_ACCOUNT_REGEX, teacherRegisterVo.getAccount())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_CONTAINS_SPECIAL_CHARACTERS), "用户账户中包含特殊字符");
        }

        // 校验姓名格式
        if (!ReUtil.isMatch(MATCHER_NAME_REGEX, teacherRegisterVo.getName())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NAME_FORMAT_VERIFICATION_FAILED), "姓名格式有误");
        }

        // 校验邮箱格式
        if (!ReUtil.isMatch(MATCHER_EMAIL_REGEX, teacherRegisterVo.getEmail())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.EMAIL_FORMAT_VERIFICATION_FAILED), "邮箱格式有误");
        }

        // 校验密码长度
        if (teacherRegisterVo.getPassword().length() > PASSWORD_LE_LENGTH || teacherRegisterVo.getPassword().length() < PASSWORD_GE_LENGTH) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PASSWORD_LENGTH_DO_NOT_MATCH), "用户密码的长度不匹配");
        }

        // 校验性别数据格式
        if (!(teacherRegisterVo.getGender() == 0 || teacherRegisterVo.getGender() == 1)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "不满足性别二元性");
        }

        teacherService.register(teacherRegisterVo);

        return R.success("注册成功");
    }

    /**
     * 用户登录接口（无需权限）
     *
     * @param teacherLoginVo 教师登录VO实体
     * @param request        需要存入session用户的登录状态
     * @return 登录信息
     */
    @PostMapping("/login")
    @ApiOperation("用户登录接口")
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
        if (session.getAttribute(CommonConstant.USER_LOGIN_STATE) != null) {
            session.removeAttribute(CommonConstant.USER_LOGIN_STATE);
        }

        TeacherLoginDto teacherLoginDto = teacherService.login(teacherLoginVo, request);

        return R.success(teacherLoginDto, "登录成功");
    }

    /**
     * 管理员获取登录状态（需要有登录状态才能获取）
     *
     * @param id      前端传来的操作者的id
     * @param request 获取Session中的登录状态
     * @return 返回最新的登录状态
     */
    @GetMapping("/getAdminLogin/{id}")
    @ApiOperation("用户获取登录状态")
    public R<TeacherLoginDto> getAdminLogin(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "操作者id为空，操作失败");
        }
        TeacherLoginDto teacherLoginDto = teacherService.getAdminLogin(id, request);
        return R.success(teacherLoginDto, "状态存在，更新状态成功");
    }


    /**
     * 用户退出接口（无需权限）
     *
     * @param request 清空session登录状态
     * @return 返回退出信息
     */
    @PostMapping("/logout")
    @ApiOperation("用户退出接口")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute(CommonConstant.USER_LOGIN_STATE);
        return R.success("退出成功");
    }

    /**
     * 教师信息导出（s）
     *
     * @param response 响应
     */
    @ApiOperation("教师信息Excel导出")
    @GetMapping("/download")
    public R<String> download(HttpServletResponse response) {
        teacherService.downloadTeacher(response);
        return R.success("导出成功");
    }

    /**
     * 教师信息删除接口（s/a）
     *
     * @param id 教师ID
     * @return 删除结果信息
     */
    @ApiOperation("教师信息删除接口")
    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable("id") Long id) {
        if (id == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "教师ID为空");
        }
        teacherService.delete(id);
        return R.success("删除教师成功");
    }

    /**
     * 批量删除教师接口（s/a）
     *
     * @param ids 教师接口列表
     * @return 返回删除结果
     */
    @DeleteMapping("/deleteBatch")
    @ApiOperation("批量删除教师接口")
    public R<String> deleteBatch(@ApiParam(name = "ids", value = "教师ID列表", required = true) @RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "教师ID列表为空");
        }
        teacherService.deleteBatch(ids);
        return R.success("删除教师成功");
    }

    /**
     * 管理员获取教师信息接口（s/a）
     *
     * @param id      教师ID
     * @param request 获取登录的Session状态
     * @return 返回教师可修改信息
     */
    @GetMapping("/get/{id}")
    @ApiOperation("管理员获取教师信息接口")
    public R<TeacherGetDto> get(@PathVariable("id") Long id, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "教师ID为空");
        }
        TeacherGetDto teacherGetDto = teacherService.getTeacher(id, request);

        return R.success(teacherGetDto, "回显成功");
    }

    /**
     * 管理员修改教师信息接口（s/a）
     *
     * @param teacherUpdateVo 管理员更新教师信息Vo实体
     * @return 返回更新结果
     */
    @PutMapping("/update")
    @ApiOperation("管理员修改教师信息接口")
    public R<String> update(@RequestBody TeacherUpdateVo teacherUpdateVo, HttpServletRequest request) {
        if (teacherUpdateVo == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        if (ObjectUtils.isEmpty(teacherUpdateVo.getId())
                || ObjectUtils.isEmpty(teacherUpdateVo.getStatus())
                || ObjectUtils.isEmpty(teacherUpdateVo.getGender())
                || ObjectUtils.isEmpty(teacherUpdateVo.getBelong())
                || ObjectUtils.isEmpty(teacherUpdateVo.getRole())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        if (!(teacherUpdateVo.getGender() == 0 || teacherUpdateVo.getGender() == 1)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        if (!(teacherUpdateVo.getStatus() == 0 || teacherUpdateVo.getStatus() == 1)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        if (!(Objects.equals(teacherUpdateVo.getRole(), CommonConstant.DEFAULT_ROLE)
                || Objects.equals(teacherUpdateVo.getRole(), CommonConstant.ADMIN_ROLE)
                || Objects.equals(teacherUpdateVo.getRole(), CommonConstant.SUPER_ROLE))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        // 校验邮箱格式
        if (!ObjectUtils.isEmpty(teacherUpdateVo.getEmail()) && !ReUtil.isMatch(MATCHER_EMAIL_REGEX, teacherUpdateVo.getEmail())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.EMAIL_FORMAT_VERIFICATION_FAILED), "邮箱格式有误");
        }

        teacherService.updateTeacher(teacherUpdateVo, request);

        return R.success("修改成功");
    }

    /**
     * 教师分页查询接口（s/a）
     *
     * @param current       当前页
     * @param pageSize      页面条数
     * @param teacherPageVo 教师分页Vo对象
     * @return 返回分页结果
     */
    @PostMapping("/page/{current}/{pageSize}")
    @ApiOperation("教师分页查询接口")
    public R<Page<TeacherPageDto>> page(@PathVariable("current") Integer current, @PathVariable("pageSize") Integer pageSize, @ApiParam(name = "teacherPageVo", value = "教师分页Vo对象", required = true) @RequestBody(required = false) TeacherPageVo teacherPageVo) {

        if (ObjectUtils.isEmpty(current) || ObjectUtils.isEmpty(pageSize)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "分页参数为空");
        }

        if (current <= 0 || pageSize <= 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "分页参数格式错误");
        }

        Page<TeacherPageDto> page = teacherService.pageTeacher(current, pageSize, teacherPageVo);

        return R.success(page, "分页查询成功");
    }
}

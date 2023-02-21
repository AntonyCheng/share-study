package top.sharehome.share_study.controller;

import cn.hutool.core.util.ReUtil;
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
import top.sharehome.share_study.model.vo.TeacherLoginVo;
import top.sharehome.share_study.model.vo.TeacherRegisterVo;
import top.sharehome.share_study.service.TeacherService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
     * 用户注册接口
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
                , teacherRegisterVo.getName())
                || ObjectUtils.isEmpty(teacherRegisterVo.getBelong())
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

        // 判断
        teacherService.register(teacherRegisterVo);

        return R.success("注册成功");
    }

    /**
     * 用户登录接口
     *
     * @param teacherLoginVo 教师登录VO实体
     * @param request        需要存入session用户的登录状态
     * @return 登录信息
     */
    @PostMapping("/login")
    @ApiOperation("用户登录接口")
    public R<String> login(@ApiParam(name = "teacherRegisterVo", value = "教师登录VO实体", required = true) @RequestBody TeacherLoginVo teacherLoginVo,
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

        teacherService.login(teacherLoginVo, request);

        return R.success("登陆成功");
    }

    /**
     * 用户退出接口
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

}

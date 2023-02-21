package top.sharehome.share_study.controller;

import cn.hutool.core.util.ReUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.model.vo.TeacherLoginVo;
import top.sharehome.share_study.service.TeacherService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
        if (session.getAttribute(CommonConstant.ADMIN_LOGIN_STATE) != null) {
            session.removeAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        }

        teacherService.adminLogin(teacherLoginVo, request);

        return R.success("登录成功");
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
}

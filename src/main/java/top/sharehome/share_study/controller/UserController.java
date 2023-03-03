package top.sharehome.share_study.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.model.dto.TeacherLoginDto;
import top.sharehome.share_study.model.dto.UserResourcePageDto;
import top.sharehome.share_study.model.vo.UserResourcePageVo;
import top.sharehome.share_study.service.ResourceService;
import top.sharehome.share_study.service.TeacherService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 普通用户相关接口
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/user")
@Api(tags = "普通用户相关接口")
@CrossOrigin
public class UserController {
    @Resource
    private TeacherService teacherService;

    @Resource
    private ResourceService resourceService;

    /**
     * 普通用户获取登录状态（需要有登录状态才能获取）
     *
     * @param id      前端传来的操作者的id
     * @param request 获取Session中的登录状态
     * @return 返回最新的登录状态
     */
    @GetMapping("/getUserLogin/{id}")
    @ApiOperation("普通用户获取登录状态")
    public R<TeacherLoginDto> getUserLogin(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "操作者id为空，操作失败");
        }
        TeacherLoginDto teacherLoginDto = teacherService.getUserLogin(id, request);
        return R.success(teacherLoginDto, "状态存在，更新状态成功");
    }

    /**
     * 普通用户的教学资料分页
     *
     * @param id       普通用户的ID
     * @param current  当前页
     * @param pageSize 页面条数
     * @param request  获取操作者的登录状态
     * @return 返回分页结果
     */
    @GetMapping("/resource/page/{current}/{pageSize}/{id}")
    @ApiOperation("普通用户的教学资料分页")
    public R<Page<UserResourcePageDto>> getResourcePage(@PathVariable("id") Long id, @PathVariable("current") Integer current, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request, @RequestBody(required = false) UserResourcePageVo userResourcePageVo) {
        if (id == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "操作者id为空，操作失败");
        }

        // 判空
        if (ObjectUtils.isEmpty(current) || ObjectUtils.isEmpty(pageSize)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "分页参数为空");
        }

        // 判断数据格式
        if (current <= 0 || pageSize <= 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "分页参数格式错误");
        }

        Page<UserResourcePageDto> userResourceDtoPage = resourceService.getUserResourcePage(id, current, pageSize, request, userResourcePageVo);

        return R.success(userResourceDtoPage, "用户教学资料查询成功");
    }

}

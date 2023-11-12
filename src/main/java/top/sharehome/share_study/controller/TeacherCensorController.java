package top.sharehome.share_study.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.model.dto.teacher_censor.TeacherCensorPageDto;
import top.sharehome.share_study.model.vo.teacher_censor.TeacherCensorPageVo;
import top.sharehome.share_study.model.vo.teacher_censor.TeacherCensorUpdateVo;
import top.sharehome.share_study.service.TeacherCensorService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户注册审核相关接口
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/teacher_censor")
@Api(tags = "用户注册审核相关接口")
@CrossOrigin
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TeacherCensorController {
    @Resource
    private TeacherCensorService teacherCensorService;

    /**
     * 发布审核完成的教师申请
     *
     * @param id      审核完成的教师申请ID
     * @param request 获取Session中的登录状态
     * @return 返回发布结果
     */
    @PostMapping("/publish/{id}")
    @ApiOperation("发布审核完成的教师申请")
    public R<String> censorPublish(@PathVariable("id") Long id, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        teacherCensorService.publishTeacherCensor(id, request);

        return R.success("注册申请通过，反馈邮件已经发送！");
    }

    /**
     * 注册申请审核状态更新
     *
     * @param teacherCensorUpdateVo 审核状态更新对象
     * @param request               获取Session登录状态
     * @return 返回更新结果
     */
    @PutMapping("/update")
    @ApiOperation("注册申请审核状态更新")
    public R<String> censorUpdate(@RequestBody TeacherCensorUpdateVo teacherCensorUpdateVo, HttpServletRequest request) {
        if (teacherCensorUpdateVo == null
                || ObjectUtils.isEmpty(teacherCensorUpdateVo.getId())
                || ObjectUtils.isEmpty(teacherCensorUpdateVo.getResult())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        Boolean returnResult = teacherCensorService.updateTeacherCensor(teacherCensorUpdateVo, request);

        return returnResult ? R.success("审核通过") : R.success("审核不通过");
    }

    /**
     * 注册申请分页操作
     *
     * @param current             当前页
     * @param pageSize            页面条数
     * @param teacherCensorPageVo 注册申请审核分页Vo对象
     * @return 返回分页结果
     */
    @GetMapping("/page/{current}/{pageSize}")
    @ApiOperation("注册申请分页操作")
    public R<Page<TeacherCensorPageDto>> censorPage(@PathVariable("current") Integer current, @PathVariable("pageSize") Integer pageSize, @ApiParam(name = "teacherCensorPageVo", value = "注册申请审核分页Vo对象", required = true)  TeacherCensorPageVo teacherCensorPageVo) {
        if (ObjectUtils.isEmpty(current) || ObjectUtils.isEmpty(pageSize)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "分页参数为空");
        }

        if (current <= 0 || pageSize <= 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "分页参数格式错误");
        }

        Page<TeacherCensorPageDto> page = teacherCensorService.pageTeacherCensor(current, pageSize, teacherCensorPageVo);

        return R.success(page, "分页查询成功");
    }

    /**
     * 注册申请删除记录操作
     *
     * @param id      待删除审核记录ID
     * @param request 获取Session中登录状态
     * @return 返回删除结果
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("注册申请删除记录操作")
    public R<String> censorDelete(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "教学资料审核ID为空");
        }
        teacherCensorService.deleteTeacherCensor(id, request);
        return R.success("删除注册申请成功");
    }

    /**
     * 注册申请批量删除记录操作
     *
     * @param ids     待批量删除审核记录ID
     * @param request 获取Session中登录状态
     * @return 返回删除结果
     */
    @DeleteMapping("/deleteBatch")
    @ApiOperation("批量删除注册申请记录接口")
    public R<String> deleteBatch(@ApiParam(name = "ids", value = "教学资料ID列表", required = true) @RequestBody List<Long> ids, HttpServletRequest request) {
        if (ids == null || ids.isEmpty()) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "教学资料ID列表为空");
        }
        teacherCensorService.deleteBatchTeacherCensor(ids, request);
        return R.success("删除教学资料成功");
    }
}

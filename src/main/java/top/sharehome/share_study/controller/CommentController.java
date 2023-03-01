package top.sharehome.share_study.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.model.dto.CommentGetDto;
import top.sharehome.share_study.model.dto.CommentPageDto;
import top.sharehome.share_study.model.vo.CommentPageVo;
import top.sharehome.share_study.model.vo.CommentUpdateVo;
import top.sharehome.share_study.service.CommentService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 评论交流相关接口
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/comment")
@Api(tags = "评论交流相关接口")
@CrossOrigin
public class CommentController {
    @Resource
    private CommentService commentService;

    /**
     * 交流评论数据导出（s）
     *
     * @param response 响应
     */
    @ApiOperation("交流评论数据Excel导出")
    @GetMapping("/download")
    public R<String> download(HttpServletResponse response) {
        commentService.download(response);
        return R.success("导出成功");
    }

    /**
     * 交流评论数据删除接口（s/a）
     *
     * @param id      交流评论ID
     * @param request 获取Session中的登录状态
     * @return 删除结果信息
     */
    @ApiOperation("交流评论数据删除接口")
    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable("id") Long id, HttpServletRequest request) {
        // 判空
        if (id == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "交流评论ID为空");
        }

        commentService.delete(id, request);

        return R.success("删除教学资料成功");
    }

    /**
     * 批量删除交流评论接口（s/a）
     *
     * @param ids     交流评论ID列表
     * @param request 获取Session中的登录状态
     * @return 返回删除结果
     */
    @DeleteMapping("/deleteBatch")
    @ApiOperation("批量删除交流评论接口")
    public R<String> deleteBatch(@ApiParam(name = "ids", value = "交流评论ID列表", required = true) @RequestBody List<Long> ids, HttpServletRequest request) {
        // 判空
        if (ids == null || ids.isEmpty()) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "教学资料ID列表为空");
        }

        commentService.deleteBatch(ids, request);

        return R.success("删除交流评论成功");
    }

    /**
     * 管理员获取交流评论信息接口（s/a）
     *
     * @param id      教师ID
     * @param request 获取登录的Session状态
     * @return 返回交流评论可修改信息
     */
    @GetMapping("/get/{id}")
    @ApiOperation("管理员获取交流评论信息接口")
    public R<CommentGetDto> get(@PathVariable("id") Long id, HttpServletRequest request) {
        // 判空
        if (ObjectUtils.isEmpty(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "交流评论ID为空");
        }

        CommentGetDto commentGetDto = commentService.get(id, request);

        return R.success(commentGetDto, "回显成功");
    }

    /**
     * 管理员修改交流评论信息接口（s/a）
     *
     * @param commentUpdateVo 管理员更新交流评论信息Vo实体
     * @return 返回更新结果
     */
    @PutMapping("/update")
    @ApiOperation("管理员修改交流评论信息接口")
    public R<String> update(@RequestBody CommentUpdateVo commentUpdateVo, HttpServletRequest request) {
        // 判空
        if (commentUpdateVo == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }
        if (ObjectUtils.isEmpty(commentUpdateVo.getId())
                || ObjectUtils.isEmpty(commentUpdateVo.getStatus())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        // 判断该评论是否被封禁
        if (!(commentUpdateVo.getStatus() == 0 || commentUpdateVo.getStatus() == 1)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        commentService.updateComment(commentUpdateVo, request);

        return R.success("修改成功");
    }

    /**
     * 交流评论分页查询接口（s/a）
     *
     * @param current       当前页
     * @param pageSize      页面条数
     * @param commentPageVo 交流评论分页Vo对象
     * @return 返回分页结果
     */
    @PostMapping("/page/{current}/{pageSize}")
    @ApiOperation("交流评论分页查询接口")
    public R<Page<CommentPageDto>> page(@PathVariable("current") Integer current, @PathVariable("pageSize") Integer pageSize, @ApiParam(name = "commentPageVo", value = "评论交流分页Vo对象", required = true) @RequestBody(required = false) CommentPageVo commentPageVo) {
        // 判空
        if (ObjectUtils.isEmpty(current) || ObjectUtils.isEmpty(pageSize)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "分页参数为空");
        }

        // 判断参数格式是否有误
        if (current <= 0 || pageSize <= 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "分页参数格式错误");
        }

        Page<CommentPageDto> page = commentService.pageComment(current, pageSize, commentPageVo);

        return R.success(page, "分页查询成功");
    }
}

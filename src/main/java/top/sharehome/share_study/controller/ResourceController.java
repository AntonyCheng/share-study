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
import top.sharehome.share_study.model.dto.ResourceGetDto;
import top.sharehome.share_study.model.dto.ResourcePageDto;
import top.sharehome.share_study.model.vo.ResourcePageVo;
import top.sharehome.share_study.model.vo.ResourceUpdateVo;
import top.sharehome.share_study.service.ResourceService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 教学资料相关接口
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/resource")
@Api(tags = "教学资料相关接口")
@CrossOrigin
public class ResourceController {
    @Resource
    private ResourceService resourceService;

    /**
     * 教学资料信息导出
     *
     * @param response 响应
     */
    @ApiOperation("教学资料信息Excel导出")
    @GetMapping("/download")
    public R<String> download(HttpServletResponse response) {
        resourceService.download(response);
        return R.success("导出成功");
    }

    /**
     * 教学资料信息删除接口
     *
     * @param id      教学资料ID
     * @param request 获取Session中的登录状态
     * @return 删除结果信息
     */
    @ApiOperation("教学资料信息删除接口")
    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "教学资料ID为空");
        }
        resourceService.delete(id, request);
        return R.success("删除教学资料成功");
    }

    /**
     * 批量删除教学资料接口
     *
     * @param ids 教学资料接口列表
     * @return 返回删除结果
     */
    @DeleteMapping("/deleteBatch")
    @ApiOperation("批量删除教学资料接口")
    public R<String> deleteBatch(@ApiParam(name = "ids", value = "教学资料ID列表", required = true) @RequestBody List<Long> ids, HttpServletRequest request) {
        if (ids == null || ids.isEmpty()) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "教学资料ID列表为空");
        }
        resourceService.deleteBatch(ids, request);
        return R.success("删除教学资料成功");
    }

    /**
     * 管理员获取教学资料信息接口
     *
     * @param id      教师ID
     * @param request 获取登录的Session状态
     * @return 返回教学资料可修改信息
     */
    @GetMapping("/get/{id}")
    @ApiOperation("管理员获取教学资料信息接口")
    public R<ResourceGetDto> get(@PathVariable("id") Long id, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "教学资料ID为空");
        }
        ResourceGetDto resourceGetDto = resourceService.get(id, request);

        return R.success(resourceGetDto, "回显成功");
    }

    /**
     * 管理员修改教学资料信息接口
     *
     * @param resourceUpdateVo 管理员更新教学资料信息Vo实体
     * @return 返回更新结果
     */
    @PutMapping("/update")
    @ApiOperation("管理员修改教学资料信息接口")
    public R<String> update(@RequestBody ResourceUpdateVo resourceUpdateVo, HttpServletRequest request) {
        if (resourceUpdateVo == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        if (ObjectUtils.isEmpty(resourceUpdateVo.getId())
                || ObjectUtils.isEmpty(resourceUpdateVo.getStatus())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        if (!(resourceUpdateVo.getStatus() == 0 || resourceUpdateVo.getStatus() == 1)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        resourceService.updateResource(resourceUpdateVo, request);

        return R.success("修改成功");
    }

    /**
     * 教学资料分页查询接口
     *
     * @param current        当前页
     * @param pageSize       页面条数
     * @param resourcePageVo 教学资料分页Vo对象
     * @return 返回分页结果
     */
    @PostMapping("/page/{current}/{pageSize}")
    @ApiOperation("教学资料分页查询接口")
    public R<Page<ResourcePageDto>> page(@PathVariable("current") Integer current, @PathVariable("pageSize") Integer pageSize, @ApiParam(name = "resourcePageVo", value = "教学资料分页Vo对象", required = true) @RequestBody(required = false) ResourcePageVo resourcePageVo) {

        if (ObjectUtils.isEmpty(current) || ObjectUtils.isEmpty(pageSize)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "分页参数为空");
        }

        if (current <= 0 || pageSize <= 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "分页参数格式错误");
        }

        Page<ResourcePageDto> page = resourceService.pageResource(current, pageSize, resourcePageVo);

        return R.success(page, "分页查询成功");
    }
}

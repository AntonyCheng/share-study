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
import top.sharehome.share_study.model.dto.resource_censor.ResourceCensorPageDto;
import top.sharehome.share_study.model.vo.resource_censor.ResourceCensorPageVo;
import top.sharehome.share_study.model.vo.resource_censor.ResourceCensorUpdateVo;
import top.sharehome.share_study.service.ResourceCensorService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 教学资料审核相关接口
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/resource_censor")
@Api(tags = "教学资料审核相关接口")
@CrossOrigin
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResourceCensorController {

    @Resource
    private ResourceCensorService resourceCensorService;

    /**
     * 教学资料审核状态更新
     *
     * @param resourceCensorUpdateVo 审核状态更新对象
     * @param request                获取Session登录状态
     * @return 返回更新结果
     */
    @PutMapping("/update")
    @ApiOperation("教学资料审核状态更新")
    public R<String> censorUpdate(@RequestBody ResourceCensorUpdateVo resourceCensorUpdateVo, HttpServletRequest request) {
        if (resourceCensorUpdateVo == null
                || ObjectUtils.isEmpty(resourceCensorUpdateVo.getId())
                || ObjectUtils.isEmpty(resourceCensorUpdateVo.getResult())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        Boolean returnResult = resourceCensorService.updateResourceCensor(resourceCensorUpdateVo, request);

        return returnResult ? R.success("审核通过") : R.success("审核不通过");
    }

    /**
     * 发布审核完成的资料
     *
     * @param id      审核完毕的资料ID
     * @param request 获取Session中的登录状态
     * @return 返回发布结果
     */
    @PostMapping("/publish/{id}")
    @ApiOperation("发布审核完成的资料")
    public R<String> censorPublish(@PathVariable("id") Long id, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        resourceCensorService.publishResourceCensor(id, request);

        return R.success("发布资料成功");
    }

    /**
     * 审核资料分页操作
     *
     * @param current              当前页
     * @param pageSize             页面条数
     * @param resourceCensorPageVo 教学资料审核分页Vo对象
     * @return 返回分页结果
     */
    @GetMapping("/page/{current}/{pageSize}")
    @ApiOperation("审核资料分页操作")
    public R<Page<ResourceCensorPageDto>> censorPage(@PathVariable("current") Integer current, @PathVariable("pageSize") Integer pageSize, @ApiParam(name = "resourceCensorPageVo", value = "教学资料审核分页Vo对象", required = true) ResourceCensorPageVo resourceCensorPageVo) {
        if (ObjectUtils.isEmpty(current) || ObjectUtils.isEmpty(pageSize)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "分页参数为空");
        }

        if (current <= 0 || pageSize <= 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "分页参数格式错误");
        }

        Page<ResourceCensorPageDto> page = resourceCensorService.pageResourceCensor(current, pageSize, resourceCensorPageVo);

        return R.success(page, "分页查询成功");
    }

    /**
     * 审核资料删除记录操作
     *
     * @param id      待删除审核记录ID
     * @param request 获取Session中登录状态
     * @return 返回删除结果
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("审核资料删除记录操作")
    public R<String> censorDelete(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "教学资料审核ID为空");
        }
        resourceCensorService.deleteResourceCensor(id, request);
        return R.success("删除教学资料成功");
    }

    /**
     * 批量删除教学资料审核条目接口
     *
     * @param ids     教学资料审核条目ID列表
     * @param request 获取Session的登录状态
     * @return 返回删除结果
     */
    @DeleteMapping("/deleteBatch")
    @ApiOperation("批量删除教学资料审核条目接口")
    public R<String> deleteBatch(@ApiParam(name = "ids", value = "教学资料ID列表", required = true) @RequestBody List<Long> ids, HttpServletRequest request) {
        if (ids == null || ids.isEmpty()) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "教学资料ID列表为空");
        }
        resourceCensorService.deleteBatchResourceCensor(ids, request);
        return R.success("删除教学资料成功");
    }
}

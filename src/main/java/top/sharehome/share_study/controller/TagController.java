package top.sharehome.share_study.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.model.dto.college.CollegeGetDto;
import top.sharehome.share_study.model.dto.tag.TagGetDto;
import top.sharehome.share_study.model.dto.tag.TagPageDto;
import top.sharehome.share_study.model.vo.tag.TagAddVo;
import top.sharehome.share_study.model.vo.tag.TagPageVo;
import top.sharehome.share_study.model.vo.tag.TagUpdateVo;
import top.sharehome.share_study.service.TagService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 资料标签相关接口
 *
 * @author AntonyCheng
 * @since 2023/6/21 22:27:56
 */
@RestController
@RequestMapping("/tag")
@Api(tags = "资料标签相关接口")
@CrossOrigin
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TagController {
    @Resource
    private TagService tagService;

    /**
     * 添加资料标签接口（s/u）
     *
     * @param tagAddVo 资料标签Vo
     * @return 返回添加结果
     */
    @PostMapping("/add")
    @ApiOperation("添加资料标签接口")
    public R<String> add(@ApiParam(name = "tagAddVo", value = "资料标签添加Vo", required = true) @RequestBody TagAddVo tagAddVo) {
        if (ObjectUtils.isEmpty(tagAddVo)
                || StringUtils.isEmpty(tagAddVo.getName())
                || ObjectUtils.isEmpty(tagAddVo.getBelong())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        tagService.add(tagAddVo);

        return R.success("添加资料标签接口成功");
    }

    /**
     * 删除资料标签接口（s）
     *
     * @param id 资料标签ID
     * @return 返回删除结果
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除资料标签接口")
    public R<String> delete(@ApiParam(name = "id", value = "资料标签ID", required = true) @PathVariable("id") Long id) {
        if (id == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "高校ID为空");
        }

        tagService.delete(id);

        return R.success("删除资料标签成功");
    }

    /**
     * 批量删除资料标签接口（s）
     *
     * @param ids 资料标签ID列表
     * @return 返回删除结果
     */
    @DeleteMapping("/deleteBatch")
    @ApiOperation("批量删除资料标签接口")
    public R<String> deleteBatch(@ApiParam(name = "ids", value = "资料标签ID列表", required = true) @RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "资料标签ID列表为空");
        }

        tagService.deleteBatch(ids);

        return R.success("删除院校成功");
    }

    /**
     * 获取单个资料标签接口，用于修改信息时的回显（s）
     *
     * @param id 资料标签ID
     * @return 返回回显结果
     */
    @GetMapping("/get/{id}")
    @ApiOperation("获取单个资料标签接口")
    public R<TagGetDto> get(@ApiParam(name = "id", value = "资料标签ID", required = true) @PathVariable("id") Long id) {
        // 判空
        if (id == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "资料标签ID为空");
        }

        // 执行获取高校信息的操作
        TagGetDto tagGetDto = tagService.get(id);

        return R.success(tagGetDto);
    }

    /**
     * 修改资料标签接口（s）
     *
     * @param tagUpdateVo 资料标签修改Vo对象
     * @return 返回修改完成信息
     */
    @PutMapping("/update")
    @ApiOperation("修改资料标签接口")
    public R<String> update(@ApiParam(name = "tagUpdateVo", value = "资料标签修改Vo对象", required = true) @RequestBody TagUpdateVo tagUpdateVo) {
        // 判空
        if (StringUtils.isAnyEmpty(tagUpdateVo.getName())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "资料标签待修改数据为空");
        }

        // 执行更新资料标签的操作
        tagService.updateTag(tagUpdateVo);

        return R.success("修改资料标签成功");
    }

    /**
     * 资料标签分页查询接口（可以做到模糊查询）（s）
     *
     * @param current   当前页
     * @param pageSize  页面条数
     * @param request   获取session对象
     * @param tagPageVo 资料标签分页Vo对象
     * @return 返回分页结果
     */
    @GetMapping("/page/{current}/{pageSize}")
    @ApiOperation("资料标签分页查询接口")
    public R<Page<TagPageDto>> pageTag(@PathVariable("current") Integer current, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request, @ApiParam(name = "tagPageVo", value = "资料标签分页Vo对象", required = true) TagPageVo tagPageVo) {
        if (ObjectUtils.isEmpty(current) || ObjectUtils.isEmpty(pageSize)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "分页参数为空");
        }

        if (current <= 0 || pageSize <= 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "分页参数格式错误");
        }

        Page<TagPageDto> page = tagService.pageTag(current, pageSize, request, tagPageVo);

        return R.success(page, "分页查询成功");
    }

    /**
     * 资料标签ID和对应名称的List（s/a/u）
     *
     * @return 资料标签List
     */
    @ApiOperation("资料标签ID和对应名称的List")
    @GetMapping(value = "/list")
    public R<List<TagGetDto>> list(HttpServletRequest request) {
        // 执行获取高校信息列表的操作
        List<TagGetDto> tagGetDtoList = tagService.listTag(request);

        return R.success(tagGetDtoList, "回显资料标签名称成功");
    }
}

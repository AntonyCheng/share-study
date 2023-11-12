package top.sharehome.share_study.controller;

import cn.hutool.core.util.ReUtil;
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
import top.sharehome.share_study.model.dto.college.CollegePageDto;
import top.sharehome.share_study.model.dto.tag.TagGetDto;
import top.sharehome.share_study.model.vo.college.CollegeAddVo;
import top.sharehome.share_study.model.vo.college.CollegePageVo;
import top.sharehome.share_study.model.vo.college.CollegeUpdateVo;
import top.sharehome.share_study.service.CollegeService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 高校相关接口
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/college")
@Api(tags = "高校相关接口")
@CrossOrigin
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CollegeController {
    @Resource
    private CollegeService collegeService;

    /**
     * 高校名称的匹配表达式
     */
    private static final String MATCHER_NAME_REGEX = "^[\u4e00-\u9fa5]{0,}$";

    /**
     * 院校代码的匹配表达式
     */
    private static final String MATCHER_CODE_REGEX = "^\\d{5}$";

    /**
     * 添加高校接口（s）
     *
     * @param collegeAddVo 高校名称和代码
     * @return 返回添加结果
     */
    @PostMapping("/add")
    @ApiOperation("添加高校接口")
    public R<String> add(@ApiParam(name = "collegeAddVo", value = "高校添加Vo", required = true) @RequestBody CollegeAddVo collegeAddVo) {
        // 判空
        if (collegeAddVo == null || StringUtils.isAnyEmpty(
                collegeAddVo.getName(),
                collegeAddVo.getCode()
        )) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        // 校验学校名称以及学院代码的数据格式
        if (!ReUtil.isMatch(MATCHER_CODE_REGEX, collegeAddVo.getCode()) ||
                !ReUtil.isMatch(MATCHER_NAME_REGEX, collegeAddVo.getName())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        // 执行添加高校的操作
        collegeService.add(collegeAddVo);

        return R.success("添加院校成功");
    }

    /**
     * 删除高校接口（s）
     *
     * @param id 高校ID
     * @return 返回删除结果
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除高校接口")
    public R<String> delete(@ApiParam(name = "id", value = "高校ID", required = true) @PathVariable("id") Long id) {
        // 判空
        if (id == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "高校ID为空");
        }

        // 执行删除高校的操作
        collegeService.delete(id);

        return R.success("删除院校成功");
    }

    /**
     * 批量删除高校接口（s）
     *
     * @param ids 高校接口列表
     * @return 返回删除结果
     */
    @DeleteMapping("/deleteBatch")
    @ApiOperation("批量删除高校接口")
    public R<String> deleteBatch(@ApiParam(name = "ids", value = "高校ID列表", required = true) @RequestBody List<Long> ids) {
        // 判空
        if (ids == null || ids.isEmpty()) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "高校ID列表为空");
        }

        // 执行批量删除高校的操作
        collegeService.deleteBatch(ids);

        return R.success("删除院校成功");
    }

    /**
     * 获取单个高校信息接口，用于修改信息时的回显（s）
     *
     * @param id 高校ID
     * @return 返回回显结果
     */
    @GetMapping("/get/{id}")
    @ApiOperation("获取单个高校信息接口")
    public R<CollegeGetDto> get(@ApiParam(name = "id", value = "高校ID", required = true) @PathVariable("id") Long id) {
        // 判空
        if (id == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "高校ID为空");
        }

        // 执行获取高校信息的操作
        CollegeGetDto collegeGetDto = collegeService.get(id);

        return R.success(collegeGetDto);
    }

    /**
     * 修改高校接口（s）
     *
     * @param collegeUpdateVo 高校修改Vo对象
     * @return 返回修改完成信息
     */
    @PutMapping("/update")
    @ApiOperation("修改高校接口")
    public R<String> update(@ApiParam(name = "collegeUpdateVo", value = "高校修改Vo对象", required = true) @RequestBody CollegeUpdateVo collegeUpdateVo) {
        // 判空
        if (StringUtils.isAnyEmpty(collegeUpdateVo.getName(), collegeUpdateVo.getCode()) ||
                ObjectUtils.isEmpty(collegeUpdateVo.getId())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "高校待修改数据为空");
        }

        // 校验高校名称和院校代码
        if (!ReUtil.isMatch(MATCHER_CODE_REGEX, collegeUpdateVo.getCode()) ||
                !ReUtil.isMatch(MATCHER_NAME_REGEX, collegeUpdateVo.getName())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }

        // 执行更新高校信息的操作
        collegeService.updateCollege(collegeUpdateVo);

        return R.success("修改院校成功");
    }

    /**
     * 高校分页查询接口（可以做到模糊查询）（s）
     *
     * @param current       当前页
     * @param pageSize      页面条数
     * @param collegePageVo 高校分页Vo对象
     * @return 返回分页结果
     */
    @GetMapping("/page/{current}/{pageSize}")
    @ApiOperation("高校分页查询接口")
    public R<Page<CollegePageDto>> pageCollege(@PathVariable("current") Integer current, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request, @ApiParam(name = "collegePageVo", value = "高校分页Vo对象", required = true) CollegePageVo collegePageVo) {
        // 判空
        if (ObjectUtils.isEmpty(current) || ObjectUtils.isEmpty(pageSize)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "分页参数为空");
        }

        // 判断参数数据格式
        if (current <= 0 || pageSize <= 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "分页参数格式错误");
        }

        // 执行高校信息分页的操作
        Page<CollegePageDto> page = collegeService.pageCollege(current, pageSize,request, collegePageVo);

        return R.success(page, "分页查询成功");
    }

    /**
     * 高校ID和对应名称的List（s/a/u）
     *
     * @return 高校名称List
     */
    @ApiOperation("高校ID和对应名称的List")
    @GetMapping(value = "/list")
    public R<List<CollegeGetDto>> list() {
        // 执行获取高校信息列表的操作
        List<CollegeGetDto> collegeGetDtoList = collegeService.listCollege();

        return R.success(collegeGetDtoList, "回显高校名称成功");
    }

    /**
     * 带有老师的高校ID和对应名称的List（s/a/u）
     *
     * @return 带有老师的高校名称List
     */
    @ApiOperation("带有老师的高校ID和对应名称的List")
    @GetMapping(value = "/mapContainTeacher")
    public R<Map<CollegeGetDto,List<TagGetDto>>> mapContainTeacher() {
        // 执行获取高校信息列表的操作
        Map<CollegeGetDto,List<TagGetDto>> collegeGetDtoMap = collegeService.mapCollegeContainTeacher();

        return R.success(collegeGetDtoMap, "回显高校名称成功");
    }

    /**
     * 带有资料标签的高校ID和对应名称的List（s/a）
     */
    @ApiOperation("带有资料标签的高校ID和对应名称的List")
    @GetMapping(value = "/mapContainTag")
    public R<Map<CollegeGetDto,List<TagGetDto>>> mapContainTag(){
        // 执行获取高校信息列表的操作
        Map<CollegeGetDto,List<TagGetDto>> collegeGetDtoList = collegeService.mapCollegeContainTag();

        return R.success(collegeGetDtoList, "回显高校名称成功");
    }

    /**
     * 高校信息导出（s）
     *
     * @param response 响应
     */
    @ApiOperation("高校信息Excel导出")
    @GetMapping("/download")
    public R<String> download(HttpServletResponse response) {
        // 执行高校信息导出为Excel的操作
        collegeService.download(response);

        return R.success("导出成功");
    }
}

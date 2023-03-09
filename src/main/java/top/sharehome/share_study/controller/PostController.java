package top.sharehome.share_study.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.model.dto.PostCommentPageDto;
import top.sharehome.share_study.model.dto.PostInfoDto;
import top.sharehome.share_study.model.dto.PostPageDto;
import top.sharehome.share_study.model.vo.PostAddVo;
import top.sharehome.share_study.model.vo.PostCollectUpdateVo;
import top.sharehome.share_study.model.vo.PostCommentAddVo;
import top.sharehome.share_study.model.vo.PostPageVo;
import top.sharehome.share_study.service.CollectService;
import top.sharehome.share_study.service.CommentService;
import top.sharehome.share_study.service.ResourceService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 用户帖子相关接口
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/post")
@Api(tags = "用户帖子相关接口")
@CrossOrigin
public class PostController {
    @Resource
    private ResourceService resourceService;

    @Resource
    private CommentService commentService;

    @Resource
    private CollectService collectService;

    /**
     * 用户帖子分页
     *
     * @param current    当前页
     * @param pageSize   页面条数
     * @param request    获取操作者的登录状态
     * @param postPageVo 帖子分页模糊查询参数
     * @return 返回分页结果
     */
    @GetMapping("/page/{current}/{pageSize}")
    @ApiOperation("用户帖子分页")
    public R<Page<PostPageDto>> pagePost(@PathVariable("current") Integer current, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request, @ApiParam(name = "resourcePageVo", value = "教学资料分页Vo对象", required = true) @RequestBody(required = false) PostPageVo postPageVo) {
        // 判空
        if (ObjectUtils.isEmpty(current) || ObjectUtils.isEmpty(pageSize)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "分页参数为空");
        }

        // 判断数据格式
        if (current <= 0 || pageSize <= 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "分页参数格式错误");
        }

        Page<PostPageDto> postDtoPage = resourceService.pagePost(current, pageSize, request, postPageVo);

        return R.success(postDtoPage, "用户帖子分页成功");
    }

    /**
     * 发布帖子接口
     *
     * @param postAddVo 帖子添加Vo
     * @param request   获取Session中的登录状态
     * @return 返回添加结果
     */
    @PostMapping("/add")
    @ApiOperation("发布帖子接口")
    public R<String> add(@ApiParam(name = "postAddVo", value = "帖子添加Vo", required = true) @RequestBody PostAddVo postAddVo, HttpServletRequest request) {
        // 判空
        if (postAddVo == null || StringUtils.isAnyEmpty(
                postAddVo.getName(),
                postAddVo.getInfo(),
                postAddVo.getUrl()
        )) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        // 执行添加教学资料的操作
        resourceService.add(postAddVo, request);

        return R.success("添加教学资料成功");
    }

    /**
     * 帖子详情接口
     *
     * @param id      教学资料ID
     * @param request 获取Session中的登录状态
     * @return 帖子详情Dto对象
     */
    @GetMapping("/info/{id}")
    @ApiOperation("帖子详情接口")
    public R<PostInfoDto> info(@PathVariable("id") Long id, HttpServletRequest request) {
        if (Objects.isNull(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }

        PostInfoDto postInfoDto = resourceService.info(id, request);

        return R.success(postInfoDto, "帖子详情显示成功");
    }

    /**
     * 资料详情评论分页
     *
     * @param id       教学资料ID
     * @param current  当前页
     * @param pageSize 页面条数
     * @param request  获取操作者的登录状态
     * @return 返回分页结果
     */
    @GetMapping("/page/{id}/{current}/{pageSize}")
    @ApiOperation("资料详情评论分页")
    public R<Page<PostCommentPageDto>> pagePostComment(@PathVariable("id") Long id, @PathVariable("current") Integer current, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request) {
        // 判空
        if (ObjectUtils.isEmpty(current) || ObjectUtils.isEmpty(pageSize) || ObjectUtils.isEmpty(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "分页参数为空");
        }

        // 判断数据格式
        if (current <= 0 || pageSize <= 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "分页参数格式错误");
        }

        Page<PostCommentPageDto> commentDtoPage = commentService.pageResourceComment(id, current, pageSize, request);

        return R.success(commentDtoPage, "资料详情评论分页成功");
    }

    /**
     * 修改收藏状态
     *
     * @param postCollectUpdateVo 修改收藏Vo对象
     * @param request             获取Session登录状态
     * @return 返回收藏结果
     */
    @PutMapping("/update/collect")
    @ApiOperation("修改收藏状态")
    public R<String> updateCollect(@RequestBody PostCollectUpdateVo postCollectUpdateVo, HttpServletRequest request) {
        if (postCollectUpdateVo == null
                || ObjectUtils.isEmpty(postCollectUpdateVo.getBelong())
                || ObjectUtils.isEmpty(postCollectUpdateVo.getResource())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "必填参数为空");
        }
        Boolean result = collectService.updateCollect(postCollectUpdateVo, request);
        return Boolean.TRUE.equals(result) ? R.success("收藏成功") : R.success("取消收藏成功");
    }

    /**
     * 新增评论
     *
     * @param postCommentAddDto 添加评论Dto对象
     * @param request           获取Session中登录状态
     * @return 返回新增结果
     */
    @PostMapping("/comment/add")
    @ApiOperation("新增评论")
    public R<String> addComment(@RequestBody PostCommentAddVo postCommentAddDto, HttpServletRequest request) {
        if (StringUtils.isAnyEmpty(postCommentAddDto.getContent())
                || ObjectUtils.isEmpty(postCommentAddDto.getSend())
                || ObjectUtils.isEmpty(postCommentAddDto.getResource())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "必填参数为空");
        }

        commentService.addComment(postCommentAddDto, request);

        return R.success("评论成功");
    }

    @DeleteMapping("/comment/delete/{id}")
    @ApiOperation("删除评论")
    public R<String> deleteComment(@PathVariable("id") Long id, HttpServletRequest request) {
        if (Objects.isNull(id)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS));
        }

        commentService.deleteComment(id, request);

        return R.success("评论删除成功");
    }
}

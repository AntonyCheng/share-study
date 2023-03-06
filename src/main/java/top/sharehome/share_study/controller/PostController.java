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
import top.sharehome.share_study.model.dto.PostPageDto;
import top.sharehome.share_study.model.vo.PostPageVo;
import top.sharehome.share_study.service.ResourceService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    public R<Page<PostPageDto>> getPostPage(@PathVariable("current") Integer current, @PathVariable("pageSize") Integer pageSize, HttpServletRequest request, @ApiParam(name = "resourcePageVo", value = "教学资料分页Vo对象", required = true) @RequestBody(required = false) PostPageVo postPageVo) {
        // 判空
        if (ObjectUtils.isEmpty(current) || ObjectUtils.isEmpty(pageSize)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY), "分页参数为空");
        }

        // 判断数据格式
        if (current <= 0 || pageSize <= 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH), "分页参数格式错误");
        }

        Page<PostPageDto> postDtoPage = resourceService.getPostPage(current, pageSize, request, postPageVo);

        return R.success(postDtoPage, "用户帖子分页成功");
    }
}

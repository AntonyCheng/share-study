package top.sharehome.share_study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.share_study.model.dto.post.PostInfoDto;
import top.sharehome.share_study.model.dto.post.PostPageDto;
import top.sharehome.share_study.model.dto.resource.ResourceGetDto;
import top.sharehome.share_study.model.dto.resource.ResourcePageDto;
import top.sharehome.share_study.model.dto.user.UserResourceGetDto;
import top.sharehome.share_study.model.entity.Resource;
import top.sharehome.share_study.model.vo.post.PostAddVo;
import top.sharehome.share_study.model.vo.post.PostPageVo;
import top.sharehome.share_study.model.vo.resource.ResourcePageVo;
import top.sharehome.share_study.model.vo.resource.ResourceUpdateVo;
import top.sharehome.share_study.model.vo.user.UserResourcePageVo;
import top.sharehome.share_study.model.vo.user.UserResourceUpdateVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 教学资料Service
 *
 * @author AntonyCheng
 */
public interface ResourceService extends IService<Resource> {

    /**
     * 教学资料信息导出
     *
     * @param response 响应
     */
    void download(HttpServletResponse response);

    /**
     * 教学资料信息删除接口
     *
     * @param id      教学资料ID
     * @param request 获取Session中的登录状态
     */
    void delete(Long id, HttpServletRequest request);

    /**
     * 批量删除教学资料接口
     *
     * @param ids     教学资料接口列表
     * @param request 获取Session中的登录状态
     */
    void deleteBatch(List<Long> ids, HttpServletRequest request);

    /**
     * 管理员获取教学资料信息接口
     *
     * @param id      教师ID
     * @param request 获取登录的Session状态
     * @return 返回教学资料可修改信息
     */
    ResourceGetDto getResource(Long id, HttpServletRequest request);

    /**
     * 管理员修改教学资料信息接口
     *
     * @param resourceUpdateVo 管理员更新教学资料信息Vo实体
     * @param request          获取登录的Session状态
     */
    void updateResource(ResourceUpdateVo resourceUpdateVo, HttpServletRequest request);

    /**
     * 教学资料分页查询接口
     *
     * @param current        当前页
     * @param pageSize       页面条数
     * @param resourcePageVo 教学资料分页Vo对象
     * @return 返回分页结果
     */
    Page<ResourcePageDto> pageResource(Integer current, Integer pageSize, ResourcePageVo resourcePageVo);

    /**
     * 普通用户的教学资料分页
     *
     * @param id                 普通用户的ID
     * @param current            当前页数
     * @param pageSize           页面条数
     * @param request            获取操作者的登录状态
     * @param userResourcePageVo 模糊查询条件类
     * @return 返回分页结果
     */
    Page<PostPageDto> getUserResourcePage(Long id, Integer current, Integer pageSize, HttpServletRequest request, UserResourcePageVo userResourcePageVo);

    /**
     * 普通用户删除教学资料
     *
     * @param id      教学资料的ID
     * @param request 获取操作者的登录状态
     */
    void deleteUserResource(Long id, HttpServletRequest request);

    /**
     * 普通用户获取教学资料信息接口（s/a/u）
     *
     * @param id      教学资料ID
     * @param request 获取登录的Session状态
     * @return 返回教学资料可修改信息
     */
    UserResourceGetDto getUserResource(Long id, HttpServletRequest request);

    /**
     * 普通用户修改教学资料信息接口（s/a/u）
     *
     * @param userResourceUpdateVo 普通用户更新教学资料信息Vo实体
     * @param request              获取Session中的登录状态
     */
    void updateUserResource(UserResourceUpdateVo userResourceUpdateVo, HttpServletRequest request);

    /**
     * 用户帖子分页
     *
     * @param current    当前页
     * @param pageSize   页面条数
     * @param request    获取操作者的登录状态
     * @param postPageVo 帖子分页模糊查询参数
     * @return 返回分页结果
     */
    Page<PostPageDto> pagePost(Integer current, Integer pageSize, HttpServletRequest request, PostPageVo postPageVo);

    /**
     * 帖子详情接口
     *
     * @param id      教学资料ID
     * @param request 获取Session中的登录状态
     * @return 帖子详情Dto对象
     */
    PostInfoDto info(Long id, HttpServletRequest request);
}

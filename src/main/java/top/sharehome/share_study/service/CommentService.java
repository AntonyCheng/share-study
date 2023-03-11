package top.sharehome.share_study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.share_study.model.dto.CommentGetDto;
import top.sharehome.share_study.model.dto.CommentPageDto;
import top.sharehome.share_study.model.dto.PostCommentPageDto;
import top.sharehome.share_study.model.dto.UserCommentPageDto;
import top.sharehome.share_study.model.entity.Comment;
import top.sharehome.share_study.model.vo.CommentPageVo;
import top.sharehome.share_study.model.vo.CommentUpdateVo;
import top.sharehome.share_study.model.vo.PostCommentAddVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 评论交流Service
 *
 * @author AntonyCheng
 */
public interface CommentService extends IService<Comment> {

    /**
     * 交流评论数据导出
     *
     * @param response 响应
     */
    void download(HttpServletResponse response);

    /**
     * 交流评论数据删除接口
     *
     * @param id      交流评论ID
     * @param request 获取Session中的登录状态
     */
    void delete(Long id, HttpServletRequest request);

    /**
     * 批量删除交流评论接口
     *
     * @param ids     交流评论ID列表
     * @param request 获取Session中的登录状态
     */
    void deleteBatch(List<Long> ids, HttpServletRequest request);

    /**
     * 管理员获取交流评论信息接口
     *
     * @param id      教师ID
     * @param request 获取登录的Session状态
     * @return 返回交流评论可修改信息
     */
    CommentGetDto get(Long id, HttpServletRequest request);

    /**
     * 管理员修改交流评论信息接口
     *
     * @param commentUpdateVo 管理员更新交流评论信息Vo实体
     * @param request         获取登录的Session状态
     */
    void updateComment(CommentUpdateVo commentUpdateVo, HttpServletRequest request);

    /**
     * 交流评论分页查询接口
     *
     * @param current       当前页
     * @param pageSize      页面条数
     * @param commentPageVo 交流评论分页Vo对象
     * @return 返回分页结果
     */
    Page<CommentPageDto> pageComment(Integer current, Integer pageSize, CommentPageVo commentPageVo);

    /**
     * 交流评论分页查询接口（s/a）
     *
     * @param request  获取Session中的登录状态
     * @param current  当前页
     * @param pageSize 页面条数
     * @return 返回分页结果
     */
    Page<UserCommentPageDto> getUserCommentPage(HttpServletRequest request, Integer current, Integer pageSize);

    /**
     * 普通用户删除单条评论
     *
     * @param id      评论交流的ID
     * @param request 获取操作者的登录状态
     */
    void deleteUserComment(Long id, HttpServletRequest request);

    /**
     * 普通用户清空收到的评论
     *
     * @param request 获取操作者的登录状态
     */
    void deleteUserCommentBatch(HttpServletRequest request);

    /**
     * 资料详情评论分页
     *
     * @param id       教学资料ID
     * @param current  当前页
     * @param pageSize 页面条数
     * @param request  获取操作者的登录状态
     * @return 返回分页结果
     */
    Page<PostCommentPageDto> pageResourceComment(Long id, Integer current, Integer pageSize, HttpServletRequest request);

    /**
     * 新增评论
     *
     * @param postCommentAddDto 添加评论Dto对象
     * @param request           获取Session中登录状态
     */
    void addComment(PostCommentAddVo postCommentAddDto, HttpServletRequest request);

    /**
     * 用户自己删除自己的评论
     *
     * @param id      被删的评论ID
     * @param request 获取Session中登录状态
     */
    void deleteComment(Long id, HttpServletRequest request);

    /**
     * 用户修改消息是否已读
     *
     * @param id      评论ID
     * @param request 获取用户状态
     * @return 返回修改结果
     */
    void updateCommentRead(Long id, HttpServletRequest request);
}

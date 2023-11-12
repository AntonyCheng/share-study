package top.sharehome.share_study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.share_study.model.dto.user.UserCollectPageDto;
import top.sharehome.share_study.model.entity.Collect;
import top.sharehome.share_study.model.vo.post.PostCollectUpdateVo;
import top.sharehome.share_study.model.vo.user.UserCollectPageVo;

import javax.servlet.http.HttpServletRequest;

/**
 * 收藏Service
 *
 * @author AntonyCheng
 */
public interface CollectService extends IService<Collect> {
    /**
     * 普通用户的收藏分页
     *
     * @param id                普通用户的ID
     * @param current           当前页
     * @param pageSize          页面条数
     * @param request           获取操作者的登录状态
     * @param userCollectPageVo 分页模糊查询
     * @return 返回分页结果
     */
    Page<UserCollectPageDto> getUserResourcePage(Long id, Integer current, Integer pageSize, HttpServletRequest request, UserCollectPageVo userCollectPageVo);

    /**
     * 普通用户删除收藏
     *
     * @param id      收藏的ID
     * @param request 获取操作者的登录状态
     */
    void deleteUserCollect(Long id, HttpServletRequest request);

    /**
     * 修改收藏状态
     *
     * @param postCollectUpdateVo 修改收藏Vo对象
     * @param request             获取Session登录状态
     * @return 返回收藏结果
     */
    Boolean updateCollect(PostCollectUpdateVo postCollectUpdateVo, HttpServletRequest request);
}

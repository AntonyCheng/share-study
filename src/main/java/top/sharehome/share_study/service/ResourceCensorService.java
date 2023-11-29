package top.sharehome.share_study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.share_study.model.dto.resource_censor.ResourceCensorPageDto;
import top.sharehome.share_study.model.entity.ResourceCensor;
import top.sharehome.share_study.model.vo.post.PostAddVo;
import top.sharehome.share_study.model.vo.resource_censor.ResourceCensorPageVo;
import top.sharehome.share_study.model.vo.resource_censor.ResourceCensorUpdateVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 教学资料审核Service
 *
 * @author AntonyCheng
 */
public interface ResourceCensorService extends IService<ResourceCensor> {

    /**
     * 帖子进入审核状态
     *
     * @param postAddVo 帖子添加Vo
     * @param request   获取Session中的登录状态
     */
    void addResourceCensor(PostAddVo postAddVo, HttpServletRequest request);

    /**
     * 教学资料审核状态更新
     *
     * @param resourceCensorUpdateVo 审核状态更新对象
     * @param request                获取Session登录状态
     * @return 返回更新结果
     */
    Boolean updateResourceCensor(ResourceCensorUpdateVo resourceCensorUpdateVo, HttpServletRequest request);

    /**
     * 发布审核完成的资料
     *
     * @param id      审核完毕的资料ID
     * @param request 获取Session中的登录状态
     */
    void publishResourceCensor(Long id, HttpServletRequest request);

    /**
     * 审核资料分页操作
     *
     * @param current              当前页
     * @param pageSize             页面条数
     * @param resourceCensorPageVo 教学资料审核分页Vo对象
     * @return 返回分页结果
     */
    Page<ResourceCensorPageDto> pageResourceCensor(Integer current, Integer pageSize, ResourceCensorPageVo resourceCensorPageVo);

    /**
     * 审核资料删除记录操作
     *
     * @param id      待删除审核记录ID
     * @param request 获取Session中登录状态
     */
    void deleteResourceCensor(Long id, HttpServletRequest request);

    /**
     * 批量删除教学资料审核条目接口
     *
     * @param request 获取Session的登录状态
     * @param ids     教学资料审核条目ID列表
     */
    void deleteBatchResourceCensor(List<Long> ids, HttpServletRequest request);
}

package top.sharehome.share_study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.share_study.model.dto.ResourceGetDto;
import top.sharehome.share_study.model.dto.ResourcePageDto;
import top.sharehome.share_study.model.dto.TeacherGetDto;
import top.sharehome.share_study.model.entity.Resource;
import top.sharehome.share_study.model.vo.ResourcePageVo;
import top.sharehome.share_study.model.vo.ResourceUpdateVo;

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
    ResourceGetDto get(Long id, HttpServletRequest request);

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
}
